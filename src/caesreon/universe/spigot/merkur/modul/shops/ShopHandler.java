package caesreon.universe.spigot.merkur.modul.shops;

import caesreon.core.system.BasisBerechtigungsGruppen;
import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.handlers.MySqlHandler;
import caesreon.core.handlers.MySqlHandler.ConnectionHandler;
import caesreon.core.hilfsklassen.BlockUT;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.InventarUT;
import caesreon.core.hilfsklassen.SerializeUT;
import caesreon.core.minecraft.Koordinate;
import caesreon.universe.spigot.merkur.modul.wirtschaft.WirtschaftsModul;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Klasse zur zentralen Umsetzung vom "Schild Klick Event" zur "SQLFunktion" der Shop Funktionen: erstelleShop(), kaufen(), verkaufen()
 *
 * @author Coriolanus_S
 */
public class ShopHandler {
    /**
     * Enthaelt die Koordinate der Kiste und den dazu passenden Shop Datensatz
     */
    protected static Map<String, Shop> shops = new HashMap<>();
    private final SchilderHandelsModul modul;

    public ShopHandler(SchilderHandelsModul modul) {
        this.modul = modul;
        PruefeDatenbank();
        ladeAlleShops();
    }

    /**
     * Erstellt einen Shop in der Datenbank und hinterlegt diesen als Shop Objekt in der Map shops.
     * Ist der Shop bereits vorhanden, wird die Methode updateShop() ausgeführt.
     *
     * @param p Der Spieler, welcher einen Shop erstellen will
     * @param s Das Shop-Schild
     * @param c Die Kiste des Shops
     */
    protected void erstelleShop(Player p, Sign s, Chest c) {
        Log.SpigotLogger.Debug("jupiter.shops.erstelleShop()");
        try {
            Location loc_s = s.getBlock().getLocation();
            Location loc_c = c.getBlock().getLocation();

            if (!checkIfShopExists(loc_s)) {

                //Statement und Connection müssen hier definiert werden, da in checkIfShopExists eine mögliche Verbindung vpm Connection schliesst
                PreparedStatement preparedUpdateStatement;
                Connection Con = ConnectionHandler.System();

                String Eigentuemer = p.getUniqueId().toString();
                String spielername = p.getDisplayName();
                Log.SpigotLogger.Debug("Shop ColorCodeRegex: " + modul.colorCodePrefix);
                if (s.getLine(0).equalsIgnoreCase(modul.colorCodePrefix + "2" + modul.adminShopKaufen)
                        || s.getLine(0).equalsIgnoreCase(modul.colorCodePrefix + "1" + modul.adminShopVerkaufen)) {
                    if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
                        Eigentuemer = modul.getWirtschaftsKomponente().getWirtschaftsModul().staatskasse_uuid;
                        spielername = WirtschaftsModul.Nationalbank.getSpielername();
                    } else {
                        ChatSpigot.NachrichtSenden(p, modul.prefix, "Du darfst keinen Adminshop erstellen");
                        return;
                    }
                }
                //String[] IdA = s.getLine(2).split(" ");
                ItemStack Ware;

                Koordinate schild = new Koordinate(loc_s);
                Koordinate kiste = new Koordinate(loc_c);

                int Anzahl = Integer.parseInt(s.getLine(2));
                //int Anzahl = Integer.parseInt(IdA[1]);

                Log.SpigotLogger.Debug("Shop Anzahl Item: " + Anzahl);
                double Preis = Double.parseDouble(PreisValidator(s.getLine(3)));
                Log.SpigotLogger.Debug("Shop Preis: " + Preis);
                //TODO: Ueber Items iterieren, aktuell wird beim kaufen und shop erstellen nur auf die erste position geachtet

                Ware = InventarUT.erhalteItemStackVonInventar_EINZEL(c.getInventory());

                if (Ware != null) {
                    String ID = Ware.getType().name();

                    SerializeUT sUT = new SerializeUT(Ware);
                    String MetaDaten = sUT.toYaml();
                    Log.SpigotLogger.Debug("Erstelle Shop Zusammenfassung:");
                    Log.SpigotLogger.Debug("Meta: " + MetaDaten);
                    Log.SpigotLogger.Debug("Eigentuemer: " + Eigentuemer + " " + Objects.requireNonNull(p.getPlayer()).getDisplayName());
                    Log.SpigotLogger.Debug("Loc Schild: " + schild.getX() + " " + schild.getY() + " " + schild.getZ());
                    Log.SpigotLogger.Debug("Loc Kiste: " + kiste.getX() + " " + kiste.getY() + " " + kiste.getZ());
                    Log.SpigotLogger.Debug("Ware/Anzahl: " + ID + " " + Anzahl);
                    String data = ShopSQL.erstelleShop;
                    preparedUpdateStatement = Con.prepareStatement(data);
                    preparedUpdateStatement.setString(1, Eigentuemer);
                    preparedUpdateStatement.setString(2, spielername);
                    preparedUpdateStatement.setString(3, Objects.requireNonNull(p.getLocation().getWorld()).getName());
                    preparedUpdateStatement.setInt(4, schild.getX());
                    preparedUpdateStatement.setInt(5, schild.getY());
                    preparedUpdateStatement.setInt(6, schild.getZ());
                    preparedUpdateStatement.setInt(7, kiste.getX());
                    preparedUpdateStatement.setInt(8, kiste.getY());
                    preparedUpdateStatement.setInt(9, kiste.getZ());
                    preparedUpdateStatement.setString(10, ID);
                    preparedUpdateStatement.setString(11, MetaDaten);
                    preparedUpdateStatement.setInt(12, Anzahl);
                    preparedUpdateStatement.setInt(13, (int) Preis);

                    MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                    updateMap(new Shop(new String[]{
                            Eigentuemer,
                            String.valueOf(schild.getX()),
                            String.valueOf(schild.getY()),
                            String.valueOf(schild.getZ()),
                            String.valueOf(kiste.getX()),
                            String.valueOf(kiste.getY()),
                            String.valueOf(kiste.getZ()),
                            ID,
                            String.valueOf(Anzahl),
                            String.valueOf((int) Preis),
                            MetaDaten
                    }));
                    ChatSpigot.NachrichtSenden(p, modul.prefix, "Dein " + modul.firmenBezeichner + " wurde erfolgreich erstellt.");
                }
            } else {
                updateShop(p, s, c, loc_s, loc_c);
            }

        } catch (Exception ex) {
            Log.SpigotLogger.Debug(ex.toString());
        }
    }

    /**
     * Wenn beim erstellen eines Shops an genau dieser Position bereits ein Shop vorhanden ist, so wird diese
     * Methode ausgeführt und Änderungen wie bspw. des Inhalts, der gehandelten Ware oder des Werts erfasst.
     * Diese Änderungen werden dann in der Datenbank hinterlegt sowie im gecashten Shop Objekt in der Hashmap shops
     *
     * @param p     Der Spieler, welcher seinen Shop ändern möchte
     * @param s     Das Shop-Schild
     * @param c     Die Shop-Kiste
     * @param loc   Die Location des Schilds
     * @param loc_c Die Location der Kiste
     */
    protected void updateShop(Player p, Sign s, Chest c, Location loc, Location loc_c) {
        Log.SpigotLogger.Debug("jupiter.ShopHandler.updateShop()");
        Shop daten = new Shop(Objects.requireNonNull(getShop(loc)));
        Log.SpigotLogger.Debug(daten.getUuid().toString());
        Log.SpigotLogger.Debug(p.getUniqueId().toString());

        if (daten.getUuid().equals(p.getUniqueId())) {
            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            try {
                double chest_x = loc_c.getX();
                double chest_y = loc_c.getY();
                double chest_z = loc_c.getZ();
                int Anzahl = Integer.parseInt(s.getLine(2));
                double Preis = Anzahl * Double.parseDouble(PreisValidator(s.getLine(3)));
                ItemStack Ware = InventarUT.erhalteItemStackVonInventar_EINZEL(c.getInventory());
                assert Ware != null;
                String ID = Ware.getType().name();
                SerializeUT sUT = new SerializeUT(Ware);
                String MetaDaten = sUT.toYaml();
                String data = ShopSQL.updateShop;
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setInt(1, (int) chest_x);
                preparedUpdateStatement.setInt(2, (int) chest_y);
                preparedUpdateStatement.setInt(3, (int) chest_z);
                preparedUpdateStatement.setString(4, ID);
                preparedUpdateStatement.setString(5, MetaDaten);
                preparedUpdateStatement.setInt(6, Anzahl);
                preparedUpdateStatement.setInt(7, (int) Preis);
                preparedUpdateStatement.setInt(8, daten.getKoordinateSchild().getX());
                preparedUpdateStatement.setInt(9, daten.getKoordinateSchild().getY());
                preparedUpdateStatement.setInt(10, daten.getKoordinateSchild().getZ());
                MySqlHandler.schliesseResultUndPreparedStatement(null, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);

                s.setLine(0, modul.colorCodePrefix + "2" + s.getLine(0));
                daten.setPreis((int) Preis);
                daten.setMeta_daten(MetaDaten);
                daten.setItem_id(ID);
                daten.setAnzahl(Anzahl);
                s.update();
                updateMap(daten);
                ChatSpigot.NachrichtSenden(p, modul.prefix, "Dein " + modul.firmenBezeichner + " wurde erfolgreich aktualisiert.");
            } catch (Exception e) {
                Log.SpigotLogger.Warning(e.toString());
                ChatSpigot.NachrichtSendenFehler(p);
            }
        }

        if (!daten.getUuid().equals(p.getUniqueId())) {
            ChatSpigot.NachrichtSenden(p, modul.prefix, "Du bist nicht der Inhaber von diesem" + modul.firmenBezeichner + "!");
        }
    }

    /**
     * Shopbesitzer verkauft an Spieler, vorher findet Validierung statt ob Shop vorhanden ist in Hashmap und erst wenn dies nicht der Fall ist
     * wird dieser von der Datenbank geladen.
     *
     * @param Schild Das Shop-Schild
     * @param Sender Der kaufende Spieler
     * @implNote Validierung ist wichtig, um "too many db connections" zu vermeiden
     */
    protected void kaufen(Sign Schild, Player Sender) {
        Log.SpigotLogger.Debug("shop.kaufen()");
        try {
            Shop daten = pruefeObShopInHashmap(Schild);

            Spieler kaufender = new Spieler(Sender);
            Spieler verkaufender = new Spieler(daten.getUuid());

            int Anzahl = Integer.parseInt(Schild.getLine(2));
            double Preis = Double.parseDouble(PreisValidator(Schild.getLine(3)));
            double KtoStandSchuldiger = modul.getWirtschaftsKomponente().getServerWirtschaft().getCachedKontostand(Sender.getUniqueId());
            Log.SpigotLogger.Debug(KtoStandSchuldiger);
            Log.SpigotLogger.Debug(verkaufender.getSpielername() + ":" + verkaufender.getUuid());
            TransaktionsHelfer t = new TransaktionsHelfer(modul);
            ItemStack oIS = new SerializeUT(daten.getMeta_daten()).toItemStack();

            Chest c = BlockUT.erhalteKisteDurchKoordinaten(Sender.getWorld(),
                    (double) daten.getKoordinateKiste().getX(),
                    (double) daten.getKoordinateKiste().getY(),
                    (double) daten.getKoordinateKiste().getZ());
            if (Sender.getUniqueId().equals(verkaufender.getUuid())) {
                ChatSpigot.NachrichtSenden(Sender, modul.prefix, "Du kannst nicht in deinem eigenen " + modul.firmenBezeichner + " einkaufen.");
                return;
            }
            TransactionsDaten transactionsDaten = t.beginneTransaktion(Schild, c, oIS, Anzahl, Preis, KtoStandSchuldiger, kaufender, verkaufender, 0);
            if (t.getUeberweisen()) {
                Log.SpigotLogger.Debug("Beginne Transaktion");
                try {
                    modul.getWirtschaftsKomponente().getServerWirtschaft().GeldUeberweisen(kaufender, verkaufender, Preis);
                    t.setUeberweisungErfolgreich(true);

                } catch (Exception e) {
                    ChatSpigot.NachrichtSendenFehler(Sender);
                    Log.SpigotLogger.Error(e.toString());
                }
                t.itemTransaktionSpielerShop(transactionsDaten);
            }
        } catch (Exception e) {
            Log.SpigotLogger.Debug("Fehler: kaufen " + e);
        }
    }

    /**
     * AdminShop verkauft an Spieler bzw. Kunde kauft vom Adminshop
     *
     * @param Schild Das Shopschild
     * @param Sender Der kaufende Spieler
     */
    protected void adminShopKaufen(Sign Schild, Player Sender) {
        Log.SpigotLogger.Debug("shop.Adminshopkaufen()");
        try {
            Shop daten = pruefeObShopInHashmap(Schild);

            Spieler kaufender = new Spieler(Sender);

            int Anzahl = Integer.parseInt(Schild.getLine(2));
            double Preis = Double.parseDouble(PreisValidator(Schild.getLine(3)));
            double KtoStandSchuldiger = modul.getWirtschaftsKomponente().getServerWirtschaft().getCachedKontostand(kaufender.getUuid());

            TransaktionsHelfer t = new TransaktionsHelfer(modul);
            ItemStack oIS = new SerializeUT(daten.getMeta_daten()).toItemStack();

            Chest c1 = BlockUT.erhalteKisteDurchKoordinaten(Sender.getWorld(),
                    (double) daten.getKoordinateKiste().getX(),
                    (double) daten.getKoordinateKiste().getY(),
                    (double) daten.getKoordinateKiste().getZ());

            TransactionsDaten transactionsDaten = t.beginneTransaktion(Schild, c1, oIS, Anzahl, Preis, KtoStandSchuldiger, kaufender, null, 2);

            if (t.getUeberweisen()) {
                Log.SpigotLogger.Debug("Beginne Transaktion");
                try {
                    modul.getWirtschaftsKomponente().getServerWirtschaft().ServerGeldAbbuchen(kaufender, Preis);
                    t.setUeberweisungErfolgreich(true);
                } catch (Exception e) {
                    ChatSpigot.NachrichtSendenFehler(Sender);
                    Log.SpigotLogger.Error(e.toString());
                }
                t.itemTransaktionAdminShop(transactionsDaten);
            }
        } catch (Exception e) {
            Log.SpigotLogger.Debug("Fehler: kaufen " + e);
        }
    }

    /**
     * Shopanbieter kauft von Spielerkunden
     *
     * @param Schild Das Shop-Schild
     * @param Sender Der verkaufende Spieler
     */
    protected void verkaufen(Sign Schild, Player Sender) {
        Log.SpigotLogger.Debug("shop.verkaufen()");
        try {
            Shop daten = pruefeObShopInHashmap(Schild);

            int Anzahl = Integer.parseInt(Schild.getLine(2));
            Spieler zahlender = new Spieler(daten.getUuid());
            Spieler sender = new Spieler(Sender);
            double Preis = Double.parseDouble(PreisValidator(Schild.getLine(3)));
            double KtoStandSchuldiger = modul.getWirtschaftsKomponente().getServerWirtschaft().getCachedKontostand(zahlender.getUuid());

            ItemStack oIS = new SerializeUT(daten.getMeta_daten()).toItemStack();

            TransaktionsHelfer t = new TransaktionsHelfer(modul);

            Chest c = BlockUT.erhalteKisteDurchKoordinaten(Sender.getWorld(),
                    (double) daten.getKoordinateKiste().getX(),
                    (double) daten.getKoordinateKiste().getY(),
                    (double) daten.getKoordinateKiste().getZ());

            if (sender.equals(zahlender)) {
                ChatSpigot.NachrichtSenden(Sender, modul.prefix, "Du kannst nicht an dein eigenes " + modul.firmenBezeichner + " verkaufen.");
                return;
            }

            TransactionsDaten transactionsDaten = t.beginneTransaktion(Schild, c, oIS, Anzahl, Preis, KtoStandSchuldiger, sender, zahlender, 1);

            if (t.getAbbuchen()) {
                try {
                    modul.getWirtschaftsKomponente().getServerWirtschaft().GeldAbbuchen(sender, zahlender, Preis, "Warenkauf");
                    t.setUeberweisungErfolgreich(true);
                } catch (Exception e) {
                    ChatSpigot.NachrichtSendenFehler(Sender);
                    Log.SpigotLogger.Error(e.toString());
                }
                t.itemTransaktionSpielerShop(transactionsDaten);
            }

        } catch (Exception e) {
            Log.SpigotLogger.Debug("Fehler: kaufen" + e);
        }
    }

    /**
     * Adminshop kaueft vom Spieler bzw. Spieler verkauft an Adminshop
     *
     * @param Schild Das Shop-Schild
     * @param Sender Der verkaufende Spieler
     */
    protected void adminShopVerkaufen(Sign Schild, Player Sender) {
        Log.SpigotLogger.Debug("adminShopVerkaufen()");
        try {
            Shop daten = pruefeObShopInHashmap(Schild);

            int Anzahl = Integer.parseInt(Schild.getLine(2));

            Spieler empfaenger = new Spieler(Sender);
            Spieler zahlender = new Spieler(daten.getUuid());


            double KtoStandSchuldiger = modul.getWirtschaftsKomponente().getServerWirtschaft().getCachedKontostand(zahlender.getUuid());
            double Preis = Double.parseDouble(PreisValidator(Schild.getLine(3)));

            ItemStack oIS = new SerializeUT(daten.getMeta_daten()).toItemStack();

            TransaktionsHelfer t = new TransaktionsHelfer(modul);

            Chest c = BlockUT.erhalteKisteDurchKoordinaten(Sender.getWorld(),
                    (double) daten.getKoordinateKiste().getX(),
                    (double) daten.getKoordinateKiste().getY(),
                    (double) daten.getKoordinateKiste().getZ());

            TransactionsDaten transactionsDaten = t.beginneTransaktion(Schild, c, oIS, Anzahl, Preis, KtoStandSchuldiger, empfaenger, zahlender, 3);

            if (t.getAbbuchen()) {
                try {
                    modul.getWirtschaftsKomponente().getServerWirtschaft().ServerGeldUeberweisenHandel(empfaenger, Preis, "Verkauf an Adminshop");
                    t.setUeberweisungErfolgreich(true);
                } catch (Exception e) {
                    ChatSpigot.NachrichtSendenFehler(Sender);
                    Log.SpigotLogger.Error(e.toString());
                }
                t.itemTransaktionAdminShop(transactionsDaten);
            }

        } catch (Exception e) {
            Log.SpigotLogger.Debug("Fehler: kaufen" + e);
        }
    }

    /**
     * Löscht ein Geschäft vollständig aus der Datenbank und aus der Hashmap shops
     *
     * @param player Der Spieler
     * @param s      Das Shop-Schild des Shops, welcher gelöscht werden soll
     */
    protected void entferneShop(Player player, Sign s) {
        Shop daten = new Shop(Objects.requireNonNull(getShop(s.getLocation())));
        if (checkIfShopExists(s.getLocation())) {
            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            try {
                String data = ShopSQL.deleteDataBlockBreakSign;
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, String.valueOf(daten.getKoordinateSchild().getX()));
                preparedUpdateStatement.setString(2, String.valueOf(daten.getKoordinateSchild().getY()));
                preparedUpdateStatement.setString(3, String.valueOf(daten.getKoordinateSchild().getZ()));
                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                shops.remove(daten.getKoordinateSchild().toString());
                ChatSpigot.NachrichtSenden(player, modul.prefix, "Du hast dein " + modul.firmenBezeichner + " erfolgreich gelöscht!");
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }

    /**
     * Prueft anhand der Koordinate des Schildes ob ein passender Shop Datensatz vorhanden ist. Ist dies der Fall, wird dieser aus der Hashmap geladen
     * Wenn nicht, wird dieser aus der Datenbank heruntergeladen
     *
     * @param schild Das Shop-Schild
     * @return Shop
     */
    private Shop pruefeObShopInHashmap(Sign schild) {
        Log.SpigotLogger.Debug("pruefeObShopInHashmap)");
        Location loc = schild.getBlock().getLocation();
        Koordinate schildKoordinate = new Koordinate(loc);
        Log.SpigotLogger.Debug(shops.keySet().size());
        Log.SpigotLogger.Debug(shops.values().size());
        Log.SpigotLogger.Debug(schildKoordinate.toString());
        try {
            Shop shop = shops.get(schildKoordinate.toString());
            Log.SpigotLogger.Debug("Key vorhanden");
            if (shop == null)
                Log.SpigotLogger.Debug("Shop war NPE");
            assert shop != null;
            Log.SpigotLogger.Debug(shop.toString());
            return shop;
        } catch (Exception e) {
            Log.SpigotLogger.Debug("Key nicht vorhanden");
            Log.SpigotLogger.Debug(e.toString());
            return new Shop(Objects.requireNonNull(getShop(loc)));
        }
    }

    /**
     * Prueft anhand der Koordinate des Schildes ob ein passender Shop Datensatz vorhanden ist. Ist dies der Fall, wird dieser aus der Hashmap geladen
     * Wenn nicht, wird dieser aus der Datenbank heruntergeladen
     *
     * @param loc Location des Shop-Schild
     * @return Boolean
     */
    private Boolean pruefeObShopInHashmap(Location loc) {
        Log.SpigotLogger.Debug("pruefeObShopInHashmap)");
        Koordinate schildKoordinate = new Koordinate(loc);
        Log.SpigotLogger.Debug(shops.keySet().size());
        Log.SpigotLogger.Debug(shops.values().size());
        Log.SpigotLogger.Debug(schildKoordinate.toString());
        try {
            Shop shop = shops.get(schildKoordinate.toString());
            Log.SpigotLogger.Debug("Key vorhanden");
            if (shop == null)
                Log.SpigotLogger.Debug("Shop war NPE");
            assert shop != null;
            Log.SpigotLogger.Debug(shop.toString());
            return true;
        } catch (Exception e) {
            Log.SpigotLogger.Debug("Key nicht vorhanden");
            Log.SpigotLogger.Debug(e.toString());
            return false;
        }
    }

    /**
     * Prüft über die Hashmap und wenn dort nicht vorhanden in der Datenbank, ob ein Shop an der angegebenen Location bereits vorhanden ist
     *
     * @param loc Die Location des Shops-Schildes
     * @return true wenn bereits ein Shop an dieser Location/Koordinate in der Hashmap shops oder in der Datenbank vermerkt ist
     */
    private Boolean checkIfShopExists(Location loc) {
        Log.SpigotLogger.Debug("universe.shop.ShopHandler()checkIfShopExist();");
        if (pruefeObShopInHashmap(loc)) {
            return true;
        } else {
            if (getShop(loc) != null) {
                Log.SpigotLogger.Debug("checkIfShopExist() return true");
                return true;
            }
            Log.SpigotLogger.Debug("checkIfShopExist() return false");
            return false;
        }
    }

    /**
     * Läd alle Shops aus der Datenbank und speichert diese in der Hashmap Shops
     */
    private void ladeAlleShops() {
        Log.SpigotLogger.Debug("core.economy.jupiter.shops.ShopHandler().getShop()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();
        ResultSet result;
        if (Con != null) {
            try {
                String sql = ShopSQL.getAllData;
                Log.SpigotLogger.Debug(sql);
                preparedUpdateStatement = Con.prepareStatement(sql);

                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                while (true) {
                    assert result != null;
                    if (!result.next()) break;
                    String[] data = {
                            result.getString("uuid"),//0
                            String.valueOf(result.getInt("schild_x")), //1
                            String.valueOf(result.getInt("schild_y")),  //2
                            String.valueOf(result.getInt("schild_z")),  //3
                            String.valueOf(result.getInt("kiste_x")), //4
                            String.valueOf(result.getInt("kiste_y")),  //5
                            String.valueOf(result.getInt("kiste_z")),  //6
                            String.valueOf(result.getString("item_id")), //7
                            String.valueOf(result.getInt("anzahl")),  //8
                            String.valueOf(result.getInt("preis")), //9
                            String.valueOf(result.getString("meta_daten"))}; //10
                    Shop shop = new Shop(data);
                    Log.SpigotLogger.Verbose("Shop gefunden: " + shop.getKoordinateSchild().toString());
                    if (!shops.containsValue(shop)) {
                        shops.put(shop.getKoordinateSchild().toString(), shop);
                    }
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);
            } catch (Exception e) {
                Log.SpigotLogger.Debug("Fehler" + e);
            }
        }
    }

    /**
     * Läd einen Shop anhand der Positionsdaten aus der Datenbank und gibt diesen als String[] zurück
     *
     * @param loc Die Location des Shops-Schildes
     * @return String Array mit ShopDaten
     */
    private String[] getShop(Location loc) {
        String[] temp = null;
        Log.SpigotLogger.Debug("core.economy.jupiter.shops.ShopHandler().getShop()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();
        ResultSet result;
        if (Con != null) {
            try {
                String sql = ShopSQL.getData;
                Log.SpigotLogger.Debug(sql);
                preparedUpdateStatement = Con.prepareStatement(sql);
                preparedUpdateStatement.setInt(1, (int) loc.getX());
                preparedUpdateStatement.setInt(2, (int) loc.getY());
                preparedUpdateStatement.setInt(3, (int) loc.getZ());

                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                assert result != null;
                if (result.next()) {
                    String[] data = {
                            result.getString("uuid"),//0
                            String.valueOf(result.getInt("schild_x")), //1
                            String.valueOf(result.getInt("schild_y")),  //2
                            String.valueOf(result.getInt("schild_z")),  //3
                            String.valueOf(result.getInt("kiste_x")), //4
                            String.valueOf(result.getInt("kiste_y")),  //5
                            String.valueOf(result.getInt("kiste_z")),  //6
                            String.valueOf(result.getString("item_id")), //7
                            String.valueOf(result.getInt("anzahl")),  //8
                            String.valueOf(result.getInt("preis")), //9
                            String.valueOf(result.getString("meta_daten"))}; //10
                    temp = data;
                    Shop shop = new Shop(data);
                    shops.put(shop.getKoordinateSchild().toString(), shop);
                }

                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);
                return temp;
            } catch (Exception e) {
                Log.SpigotLogger.Debug("Fehler" + e);
            }
        }
        return null;
    }

    /**
     * Erlaubt dem Spieler eine gewisse Variang in der Währungsbezeichnung auf seinem Shop Schild
     *
     * @param s Eingabe String, welcher über das Shopschild festgelegt wird
     * @return Für die weitere verarbeitung angepasster String
     */
    String PreisValidator(String s) {
        Log.SpigotLogger.Debug("PreisValidator()");

        if (s.contains(" $"))
            s = s.replace(" $", "");
        else if (s.contains("$"))
            s = s.replace("$", "");
        else if (s.contains(" €"))
            s = s.replace(" €", "");
        else if (s.contains("€"))
            s = s.replace("€", "");
        else if (s.contains("Caesh"))
            s = s.replace(" Caesh", "");
        else if (s.contains(" C"))
            s = s.replace(" C", "");
        else if (s.contains("C"))
            s = s.replace("C", "");
        Log.SpigotLogger.Debug(s);
        return s;
    }

    /**
     * Prüft beim Starten des Servers, ob die benötigten Tabellen in der Datenbank vorhanden sind
     */
    private void PruefeDatenbank() {
        Connection Con = ConnectionHandler.System();
        MySqlHandler.pruefeObDatenbankExistiert(Con, ShopSQL.erstelleTabelle);
    }

    /**
     * Wenn ein Shop in der Hashmap shops enthalten ist, wird dieser Wert durch einen neuen Wert anhand des Keys ersetzt.
     * Ist der Shop noch nicht in der Hashmap, so wird dieser hinzugefügt.
     *
     * @param shop Der Shop, welcher hinzugefügt oder geupdated werden soll
     */
    private void updateMap(Shop shop) {
        if (!shops.containsValue(shop)) {
            shops.put(shop.getKoordinateSchild().toString(), shop);
        } else {
            shops.remove(shop.getKoordinateSchild().toString());
            shops.put(shop.getKoordinateSchild().toString(), shop);
        }

    }
}
