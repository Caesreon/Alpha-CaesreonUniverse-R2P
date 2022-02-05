package caesreon.universe.spigot.merkur.modul.shops;

import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.InventarUT;
import caesreon.core.hilfsklassen.SpielerUT;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Klasse welche die tatsaechliche Gueter Transaktion durchfuehrt
 *
 * @author Coriolanus_S
 */
public class TransaktionsHelfer {
    //TODO: Texte in Configfile auslagern

    private final SchilderHandelsModul modul;

    protected enum transaktionstyp { KAUFEN, VERKAUFEN}
    public TransaktionsHelfer(SchilderHandelsModul modul) {
        this.modul = modul;
    }

    private boolean ueberweisen;
    private boolean abbuchen;
    private boolean ueberweisungErfolgreich;

    public boolean getUeberweisungErfolgreich() {
        return ueberweisungErfolgreich;
    }

    public void setUeberweisungErfolgreich(boolean ueberweisungErfolgreich) {
        this.ueberweisungErfolgreich = ueberweisungErfolgreich;
    }

    public boolean getUeberweisen() {
        return ueberweisen;
    }

    public void setUeberweisen(boolean ueberweisen) {
        this.ueberweisen = ueberweisen;
    }

    public boolean getAbbuchen() {
        return abbuchen;
    }

    public void setAbbuchen(boolean ueberweisungErfolgreich) {
        this.ueberweisungErfolgreich = ueberweisungErfolgreich;
    }

    /**
     * Diese Methode ermöglicht den Verkauf und prüft ob alle Vorraussetzungen für eine Transaktion gegeben sind.
     *
     * @param s           Schild mit welchen interagiert wurde
     * @param c           Kiste welche das jeweilige Item enthalten soll
     * @param Item        das zu handelnde Item
     * @param Anzahl      die zu handelnde Menge
     * @param Preis       Preis des zu handelnden Items
     * @param Kontostand  aktueller Kontostand des zu belastenden Konto
     * @param Sender      Spieler welcher mit Schild interagiert
     * @param Empfaenger Spieler/Besitzer welcher in der Datenbank hinterlegt ist
     * @param Flag       0=KAUFEN, 1=VERKAUFEN 2=ADMINSHOP-KAUF 3=ADMINSHOP-VERKAUF
     */
    public TransactionsDaten beginneTransaktion(Sign s, Chest c, ItemStack Item, int Anzahl, double Preis, double Kontostand, Spieler Sender, Spieler Empfaenger, int Flag) {
        ArrayList<ItemStack> items = new ArrayList<>();
        switch (Flag) {
            //=========================KAUFEN LOGIK=========================
            case 0:
                try {
                    if (InventarUT.pruefeInventarPlatz(Sender.getBukkitSpieler(), modul.prefix) && InventarUT.pruefeWareInChest(c, Item, Anzahl) && (Kontostand - Preis) >= 0) {
                        s.setLine(0, modul.colorCodePrefix + "2" + modul.kaufen);
                        s.update();
                        Item.setAmount(Anzahl);
                        items.add(Item);
                        setUeberweisen(true);
                        return new TransactionsDaten(transaktionstyp.KAUFEN, c, Item, Anzahl, Sender, Empfaenger, items);
                    }

                    //Wenn Item nicht enthalten
                    //Schild umfaerben, abbruch der Aktion, wenn spieleronline nachricht an verkauefer
                    if (!InventarUT.pruefeWareInChest(c, Item, Anzahl)) {
                        s.setLine(0, modul.colorCodePrefix + "4" + modul.kaufen);
                        s.update();
                        if (Empfaenger != null) {
                            if (Empfaenger.istOnline()) {
                                String GekauftesItem = Item.getType().name().toLowerCase().substring(0, 1).toUpperCase() + Item.getType().name().toLowerCase().substring(1);
                                ChatSpigot.NachrichtSenden(Empfaenger.getBukkitSpieler(), modul.prefix, "Dein " + GekauftesItem + " Shop ist ausverkauft!");
                            }
                        }
                        return null;
                    }

                    if ((Kontostand - Preis) <= 0) {
                        ChatSpigot.NachrichtSenden(Sender.getBukkitSpieler(), modul.prefix, "Du besitzt nicht ausreichend Geld!");
                    }
                } catch (Exception e) {
                    System.out.println("Transaktion " + e);
                    ChatSpigot.NachrichtSendenFehler(Sender.getBukkitSpieler());
                }
                break;
            //=========================VERKAUFEN LOGIK=========================
            case 1:
                try {
                    if (!InventarUT.pruefePlatzInChest(c, Item, Anzahl)) {
                        ChatSpigot.NachrichtSenden(Sender.getBukkitSpieler(), modul.prefix, "Der Händler hat keinen Platz mehr in der Kiste! Handel abgebrochen.");
                    }
                    if ((Kontostand - Preis) <= 0) {
                        ChatSpigot.NachrichtSenden(Sender.getBukkitSpieler(), modul.prefix, "Der Händler besitzt nicht ausreichend Geld!");
                    }
                    if (InventarUT.pruefeInventarWareVorhanden(Sender.getBukkitSpieler(), Item, Anzahl, modul.prefix) && InventarUT.pruefePlatzInChest(c, Item, Anzahl) && (Kontostand - Preis) >= 0) {
                        Item.setAmount(Anzahl);
                        InventarUT.entferneItemAusInv(Sender.getBukkitSpieler().getInventory(), Item, Anzahl);
                        c.getInventory().addItem(Item);
                        abbuchen = true;
                        return new TransactionsDaten(transaktionstyp.VERKAUFEN, c, Item, Anzahl, Sender, Empfaenger, items);
                    }
                } catch (Exception e) {
                    Log.SpigotLogger.Error(e.toString());
                    ChatSpigot.NachrichtSendenFehler(Sender.getBukkitSpieler());
                }
                break;
            //=========================ADMIN SHOP KAUFEN LOGIK=========================
            case 2:
                try {
                    if (InventarUT.pruefeInventarPlatz(Sender.getBukkitSpieler(), modul.prefix) && (Kontostand - Preis) >= 0) {

                        Item.setAmount(Anzahl);

                        items.add(Item);
                        ueberweisen = true;
                        return new TransactionsDaten(transaktionstyp.KAUFEN, c, Item, Anzahl, Sender, Empfaenger, items);
                    }

                    if ((Kontostand - Preis) <= 0) {
                        ChatSpigot.NachrichtSenden(Sender.getBukkitSpieler(), modul.prefix, "Du besitzt nicht ausreichend Geld!");
                    }
                } catch (Exception e) {
                    Log.SpigotLogger.Error("Transaktion " + e);
                    ChatSpigot.NachrichtSendenFehler(Sender.getBukkitSpieler());
                }
                break;
            //=========================ADMIN SHOP VERKAUFEN LOGIK=========================
            case 3:
                try {
                    if (InventarUT.pruefeInventarWareVorhanden(Sender.getBukkitSpieler(), Item, Anzahl, modul.prefix) && InventarUT.pruefePlatzInChest(c, Item, Anzahl) && (Kontostand - Preis) >= 0) {
                        Item.setAmount(Anzahl);
                        InventarUT.entferneItemAusInv(Sender.getBukkitSpieler().getInventory(), Item, Anzahl);
                        //c.getInventory().addItem(Item);
                        abbuchen = true;
                        return new TransactionsDaten(transaktionstyp.VERKAUFEN, c, Item, Anzahl, Sender, Empfaenger, items);
                    }

                    if (!InventarUT.pruefePlatzInChest(c, Item, Anzahl)) {
                        ChatSpigot.NachrichtSenden(Sender.getBukkitSpieler(), modul.prefix, "Der Adminshop hat keinen Platz mehr in der Kiste! Handel abgebrochen.");
                    }
                    if ((Kontostand - Preis) <= 0) {
                        ChatSpigot.NachrichtSenden(Sender.getBukkitSpieler(), modul.prefix, "Die Nationalbank besitzt nicht ausreichend Geld!");
                    }
                } catch (Exception e) {
                    Log.SpigotLogger.Error(e.toString());
                    ChatSpigot.NachrichtSendenFehler(Sender.getBukkitSpieler());
                }
                break;
        }
        return  null;
    }

    /**
     * Diese Methode soll anhand des Booleans ueberweisungErfolgreich und den in den TransaktionsDaten übergebenen Parametern nach erfolgreicher
     * Zahlung im ShopHandler den eigentlichen Item-Transfer abwickeln. Dabei wird der Überweisungstyp anhand des übergebenen Enums festgelegt.
     * @param transaktionsDaten Alle für den Item-Transfer relevanten Daten (Item, Sender, Empfänger, Anzahl)
     */
    public void itemTransaktionSpielerShop(TransactionsDaten transaktionsDaten) {
        assert ueberweisungErfolgreich;
        if (getUeberweisungErfolgreich()) {
            if (transaktionsDaten.transaktionstyp.equals(transaktionstyp.KAUFEN)) {
                InventarUT.entferneItemAusKiste(transaktionsDaten.kistenShop, transaktionsDaten.item, transaktionsDaten.anzahl);
                SpielerUT.Items_AnSpielerGeben(transaktionsDaten.kaufenderSpieler.getBukkitSpieler(), transaktionsDaten.items);
                String GekauftesItem = transaktionsDaten.item.getType().name().toLowerCase().substring(0, 1).toUpperCase() + transaktionsDaten.item.getType().name().toLowerCase().substring(1);
                ChatSpigot.NachrichtSenden(transaktionsDaten.kaufenderSpieler.getBukkitSpieler(), modul.prefix, "Du hast " + transaktionsDaten.anzahl + " " + GekauftesItem + " gekauft.");
            }
            if (transaktionsDaten.transaktionstyp.equals(transaktionstyp.VERKAUFEN)) {
                String item = transaktionsDaten.item.getType().name().toLowerCase().substring(0, 1).toUpperCase() + transaktionsDaten.item.getType().name().toLowerCase().substring(1);
                ChatSpigot.NachrichtSenden(transaktionsDaten.kaufenderSpieler.getBukkitSpieler(), modul.prefix, "Du hast erfolgreich " + transaktionsDaten.anzahl + " " + transaktionsDaten.item + " verkauft.");
                if (transaktionsDaten.verkaufenderSpieler != null) {
                    ChatSpigot.NachrichtSenden(transaktionsDaten.verkaufenderSpieler.getBukkitSpieler(), modul.prefix, "Dir wurde erfolgreich " + transaktionsDaten.anzahl + " " + item + " verkauft von " + ChatColor.GREEN + transaktionsDaten.kaufenderSpieler.getSpielername() + ChatColor.WHITE + ".");
                }
            }
            ueberweisungErfolgreich = false;
        }
    }

    public void itemTransaktionAdminShop(TransactionsDaten transaktionsDaten) {
        assert ueberweisungErfolgreich;
        if (getUeberweisungErfolgreich()) {
            if (transaktionsDaten.transaktionstyp.equals(transaktionstyp.KAUFEN)) {
                SpielerUT.Items_AnSpielerGeben(transaktionsDaten.kaufenderSpieler.getBukkitSpieler(), transaktionsDaten.items);
                String GekauftesItem = transaktionsDaten.item.getType().name().toLowerCase().substring(0, 1).toUpperCase() + transaktionsDaten.item.getType().name().toLowerCase().substring(1);
                ChatSpigot.NachrichtSenden(transaktionsDaten.kaufenderSpieler.getBukkitSpieler(), modul.prefix, "Du hast " + transaktionsDaten.anzahl + " " + GekauftesItem + " gekauft.");
            }
            if (transaktionsDaten.transaktionstyp.equals(transaktionstyp.VERKAUFEN)) {
                String item = transaktionsDaten.item.getType().name().toLowerCase().substring(0, 1).toUpperCase() + transaktionsDaten.item.getType().name().toLowerCase().substring(1);
                ChatSpigot.NachrichtSenden(transaktionsDaten.kaufenderSpieler.getBukkitSpieler(), modul.prefix, "Du hast erfolgreich " + transaktionsDaten.anzahl + " " + item + " verkauft.");
            }
            ueberweisungErfolgreich = false;
        }
    }

}
