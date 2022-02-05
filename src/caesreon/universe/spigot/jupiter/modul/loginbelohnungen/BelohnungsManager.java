package caesreon.universe.spigot.jupiter.modul.loginbelohnungen;

import caesreon.core.*;
import caesreon.core.hilfsklassen.BossPluginUT;
import caesreon.core.hilfsklassen.SpielerUT;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Manager fuer Belohnungen
 */
public class BelohnungsManager {
    BelohnungenModul modul;
    private final List<String[]> Belohnungen = new ArrayList<>();// Belohnungsdaten (Typ, X, Y) fuer jeden Tag

    public BelohnungsManager(BelohnungenModul modul) {
        this.modul = modul;
        initialisiereBelohnungen();
    }


    /**
     * METHODE WIRD NICHT MEHR GEBRAUCHT
     */
    @Deprecated
    protected void starteSpielerBelohnen(Player p, int tag) {
        Log.SpigotLogger.Debug("OLD starteSpielerBelohnen()");
        tag -= 1;    // Konvertierung Tag zu Array-Index
        String[] Data = Belohnungen.get(tag);
        switch (Data[0]) {
            case "M":
                gebeSpielerMaterialBelohnung(p, Data);
                break;
            case "B":
                gebeSpielerBefehlBelohnung(p, Data);
                break;
            case "C":
                gebeSpielerCustomBelohnung(p, Data);
                break;
        }
    }

    /**
     * Belohnung fuer den Tag auswaehlen
     *
     * @param tag Tag, fuer den der Spieler die Belohnung bekommt
     * @return Belohnungs-Item
     */
    private ItemStack lookUpBelohnung(int tag) {
        Log.SpigotLogger.Debug("starteSpielerBelohnen()");
        tag -= 1;    // Konvertierung Tag zu Array-Index
        String[] Data = Belohnungen.get(tag);
        switch (Data[0]) {
            case "M":
                return materialBelohnung(Data);
            case "B":
                return befehlBelohnung(Data);
            case "C":
                return customBelohnung(Data);
            default:
                Log.SpigotLogger.Debug("Unbekannter Belohnungstyp: " + Data[0]);
        }
        return null;
    }


    //TODO: Texte in Configfile auslagern


    /**
     * Belohnungen des Tages in Inventarmenue anzeigen
     *
     * @param p   Spieler, dem das Belohnungs-menue gezeigt werden soll
     * @param tag Tag, fuer den die Belohnung angezeigt werden soll
     */
    protected void zeigeTagesBelohnungGUI(Player p, int tag) {
        MenuBuilder m = new MenuBuilder();
        String regex = modul.Regex;
        Menu tagesMenu = m.erstelleBasisMenue(null, ColorCodeParser.ParseString(Objects.requireNonNull(modul.getBelohnungsConfig().getString("text.tagesbelohnung")), regex),9);
        m.itemHinzufuegen(tagesMenu, lookUpBelohnung(tag), 4);    // Item des Tags in die Mitte des Inventars setzen
        m.oeffneMenu(p, tagesMenu);
    }

    /**
     * Belohnungen des Monats in Inventarmenue anzeigen
     *
     * @param p Spieler, dem das Belohnungs-menue gezeigt werden soll
     */
    protected void zeigeMonatsBelohnungGUI(Player p) {
        MenuBuilder m = new MenuBuilder();
        String regex = modul.Regex;
        Menu monatsMenu = m.erstelleBasisMenue(null, ColorCodeParser.ParseString(Objects.requireNonNull(modul.getBelohnungsConfig().getString("text.monatsbelohnung")), regex) ,36);
        for (int i = 1; i <= 31; i++)
            m.itemHinzufuegen(monatsMenu, lookUpBelohnung(i), i - 1);
        m.oeffneMenu(p, monatsMenu);
    }


    /**
     * Gebe dem Spieler die Belohnung, die ein Material ist
     *
     * @param p    Spieler, der das Item bekommt
     * @param Data Item-Daten
     */
    private void gebeSpielerMaterialBelohnung(Player p, String[] Data)    //TODO Comment: Hier waere es sinnvoller ein String und ein Integer zu uebergeben, da die Methode dann universeller waere
    {
        Log.SpigotLogger.Debug("gebeSpielerMaterialBelohnun()");
        try {
            ItemStack is = new ItemStack(Objects.requireNonNull(Material.matchMaterial(Data[1])), Integer.parseInt(Data[2]));
            SpielerUT.Item_AnSpielerGeben(p, is);
        } catch (Exception exception) {
            p.sendMessage("[Belohnungs-Fehler] Entschuldigung. Hier ist ein Fehler aufgetreten. Bitte frage beim Admin nach der Belohnung");
        }
    }


    /**
     * Gebe dem Spieler die Belohnung, die ein Befehl ist
     *
     * @param p    Spieler, der Ziel des Befehls ist
     * @param Data Befehlsdaten
     */
    private void gebeSpielerBefehlBelohnung(Player p, String[] Data)    //TODO Comment: Auch hier waere die uebergabe von Strings und Double universeller
    {
        // Geld-Befehl
        if (Data[1].equalsIgnoreCase(COMMANDS.ECONOMY.geld.toString())) {
            Spieler empf = new Spieler(p);
            modul.getMain().getMerkurKomponente().getServerWirtschaft().ServerGeldUeberweisen(empf, Double.parseDouble(Data[2]), "Login-Belohnung");
        }

        // Fly-Befehl
        if (Data[1].equalsIgnoreCase(COMMANDS.FLY.tempfly.toString())) {
            modul.getMain().getJupiterKomponente().getFlyModul().getFlyHandler().setzeTempfly(p, Integer.parseInt(Data[2]));
        }
    }

    /**
     * Gebe dem Spieler die Belohnung, die ein Custom-Item ist
     *
     * @param p    Spieler, der das Item bekommt
     * @param Data Custom-Item
     */
    private void gebeSpielerCustomBelohnung(Player p, String[] Data) {
        Log.SpigotLogger.Debug("gebeSpielerCustomBelohnung()");
        try {
            ItemStack is = BossPluginUT.getCustomItemStack(modul.getMain(), Data[1]);
            if (is == null) {
                is = new ItemStack(Material.DIAMOND_BLOCK, 5);
            }
            SpielerUT.Item_AnSpielerGeben(p, is);
        } catch (Exception exception) {
            p.sendMessage("[Belohnungs-Fehler] Entschuldigung. Hier ist ein Fehler aufgetreten. Bitte frage beim Admin nach der Belohnung");
        }
    }


    /**
     * Erstelle das Item zum Anzeige der Material-Belohnung
     *
     * @param Data Item-Daten
     * @return Belohnungs-Item oder null bei Fehler
     */
    private ItemStack materialBelohnung(String[] Data) {
        Log.SpigotLogger.Debug("MaterialBelohnung()");
        try {
            return new ItemStack(Objects.requireNonNull(Material.matchMaterial(Data[1])), Integer.parseInt(Data[2]));
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Erstelle das Item zum Anzeige der Befehl-Belohnung
     *
     * @param Data Befehl-Daten
     * @return Repraesentations-Item oder null bei Fehler
     */
    private ItemStack befehlBelohnung(String[] Data) {
        // Geld-Befehl
        if (Data[1].equalsIgnoreCase(COMMANDS.ECONOMY.geld.toString())) {
            ItemStack is = new ItemStack(Material.GOLD_NUGGET, 1);
            ItemMeta im = is.getItemMeta();
            assert im != null;
            im.setDisplayName(ChatColor.WHITE + Data[2] + " Caesh");
            is.setItemMeta(im);
            return is;
        }

        // Fly-Befehl
        if (Data[1].equalsIgnoreCase(COMMANDS.FLY.tempfly.toString())) {
            ItemStack is2 = new ItemStack(Material.ELYTRA, 1);
            ItemMeta im2 = is2.getItemMeta();
            assert im2 != null;
            im2.setDisplayName(ChatColor.WHITE + Data[2] + " Temp-Fly");
            is2.setItemMeta(im2);
            return is2;
        }

        return null;
    }

    /**
     * Erstelle das Item zum Anzeige der Custom-Belohnung
     *
     * @param Data Custom-Daten
     * @return Item oder null bei Fehler
     */
    private ItemStack customBelohnung(String[] Data) {
        Log.SpigotLogger.Debug("gebeSpielerCustomBelohnung()");
        try {
            ItemStack is = BossPluginUT.getCustomItemStack(modul.getMain(), Data[1]);
            if (is == null) {
                is = new ItemStack(Material.DIAMOND_BLOCK, 5);
            }
            return is;
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Initialisieren (Auslesen aus Config) der Belohnungen
     */
    private void initialisiereBelohnungen() {
        YamlConfiguration config = modul.getBelohnungsConfig();

        for (int tag = 1; tag <= 31; tag++) {
            String configValue = config.getString("belohnungen.tag" + tag);
            assert configValue != null;
            if (!configValue.isEmpty()) {
                String[] data = configValue.split(" ");
                if (data.length != 3)
                    Log.SpigotLogger.Debug("Diese Belohnung hat nicht das korrekte Format: " + configValue);
                else {
                    this.Belohnungen.add(data);
                }
            } else {
                Log.SpigotLogger.Debug("Diese Belohnung hat nicht das korrekte Format: " + configValue);
            }
        }
    }
}