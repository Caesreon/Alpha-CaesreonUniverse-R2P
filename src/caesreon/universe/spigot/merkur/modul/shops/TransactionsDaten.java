package caesreon.universe.spigot.merkur.modul.shops;

import caesreon.core.Spieler;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Klasse um Item-Transfer zwischen zwei Handelnden Spielern festzuhalten. Diese speichert alle für den Transfer relevante Daten
 */
public class TransactionsDaten {
    protected final TransaktionsHelfer.transaktionstyp transaktionstyp;
    Chest kistenShop;
    ItemStack item;
    int anzahl;
    Spieler kaufenderSpieler;
    Spieler verkaufenderSpieler;
    ArrayList<ItemStack> items;

    TransactionsDaten(TransaktionsHelfer.transaktionstyp transaktionstyp, Chest kistenShop, ItemStack item, int anzahl, Spieler kaufenderSpieler, Spieler verkaufenderSpieler, ArrayList<ItemStack> items)
    {
        this.kistenShop = kistenShop;
        this.item = item;
        this.anzahl = anzahl;
        this.kaufenderSpieler = kaufenderSpieler;
        this.verkaufenderSpieler = verkaufenderSpieler;
        this.items = items;
        this.transaktionstyp = transaktionstyp;
    }
}
