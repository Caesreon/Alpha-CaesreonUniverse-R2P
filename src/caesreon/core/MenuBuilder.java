package caesreon.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Diese Klasse soll alle Tools und Methoden beinhalten, um ein unspezifisches Menü zu ermöglichen, ohne konkretes Muster
 * oder im Rahmen der Massenverarbeitung, wo nicht bekannt ist, wie viele ItemStacks oder seiten dort angezeigt werden sollen.
 * Allerdings müssen hier dann auch die Positionen angegeben werden.
 */
public class MenuBuilder extends Menu {

    /**
     * Erstellt ein Standard Menü ohne Inhalt
     *
     * @param Owner Owner/oeffner des Menues
     * @param Slots Anzahl der Plaetze (muss mindestens 9 oder ein Vielfaches von 9 sein!)
     * @param Titel Name fuer das Menue
     */
    public Menu erstelleBasisMenue(InventoryHolder Owner, String Titel, int Slots) {
        this.titel = Titel;
        this.slots = Slots;
        this.inventar = Bukkit.createInventory(Owner, Slots, Titel);
        return this;
    }

    public ItemStack erstelleGUIItem(String MaterialString, String Displayname, List<String> Lore, String Regex) {
        ItemStack GUIItem;
        try {
            GUIItem = new ItemStack(Objects.requireNonNull(Material.matchMaterial(MaterialString)), 1);
            ItemMeta Meta = GUIItem.getItemMeta();
            Objects.requireNonNull(Meta).setDisplayName(ColorCodeParser.Parse(Displayname, Regex));
            Meta.setLore(ColorCodeParser.ParseList(Lore, Regex));
            GUIItem.setItemMeta(Meta);
            return GUIItem;
        } catch (Exception e) {
            return null;
        }
    }

    public ItemStack erstelleGUIItem(String MaterialString, String Displayname, String Regex) {
        ItemStack GUIItem;
        try {
            GUIItem = new ItemStack(Objects.requireNonNull(Material.matchMaterial(MaterialString)), 1);
            ItemMeta Meta = GUIItem.getItemMeta();
            Objects.requireNonNull(Meta).setDisplayName(ColorCodeParser.Parse(Displayname, Regex));
            GUIItem.setItemMeta(Meta);
            return GUIItem;
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
            return null;
        }
    }

    public ItemStack erstelleGUIItem(Material material, String Displayname, String Regex) {
        ItemStack GUIItem;
        try {
            GUIItem = new ItemStack(Objects.requireNonNull(material), 1);
            ItemMeta Meta = GUIItem.getItemMeta();
            Objects.requireNonNull(Meta).setDisplayName(ColorCodeParser.Parse(Displayname, Regex));
            GUIItem.setItemMeta(Meta);
            return GUIItem;
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
            return null;
        }
    }

    public Menu itemHinzufuegen(Menu m, ItemStack Item) {
        m.inventar.addItem(Item);
        return m;
    }

    public Menu itemHinzufuegen(Menu m, ItemStack Item, int slot) {
        m.inventar.setItem(slot, Item);
        return m;
    }

    public void oeffneInventar(final HumanEntity ent, Inventory inv) {
        ent.openInventory(inv);
    }

    public void oeffneMenu(final HumanEntity ent, Menu menu) {
        ent.openInventory(menu.inventar);
    }

    /**
     * Diese Klasse soll bereits fertige Layouts beinhalten, was bedeutet, dass ich hier nur noch die jeweiligen ItemStacks
     * übergeben muss. Dies erspart minimal Quellcode und standardisierte Menüs quer durch das Plugin
     */
    public static class MenuLayoutContainer extends MenuBuilder {
        /**
         * ;-----------------;<br>
         * |x|_|_|_|x|_|_|_|x|<br>
         *
         * @param Owner Der Besitzer des Menüs
         * @param Titel Name des Menüs
         * @param Item1 Das erste Item
         * @param Item2 Das zweite Item
         * @param Item3 Das dritte Item
         * @param ItemHintergrund Alle anderen Items als "Hintergrundfarbe"
         * @return Das vollständige Inventar Menu
         */
        public Menu MenuR1I3(InventoryHolder Owner, String Titel, ItemStack Item1, ItemStack Item2, ItemStack Item3, ItemStack ItemHintergrund) {
            this.titel = Titel;
            this.slots = 9;
            this.inventar = Bukkit.createInventory(Owner, slots, Titel);
            if (ItemHintergrund != null)
                for (int i = 1; i < slots; i++) {
                    inventar.setItem(i, ItemHintergrund);
                }
            inventar.setItem(0, Item1);
            inventar.setItem(4, Item2);
            inventar.setItem(8, Item3);
            return this;
        }

        public Menu MenuR1I4(InventoryHolder Owner, String Titel, ItemStack Item1, ItemStack Item2, ItemStack Item3, ItemStack Item4, ItemStack ItemHintergrund) {
            this.titel = Titel;
            this.slots = 9;
            this.inventar = Bukkit.createInventory(Owner, slots, Titel);
            if (ItemHintergrund != null)
                for (int i = 1; i < slots; i++) {
                    inventar.setItem(i, ItemHintergrund);
                }
            inventar.setItem(1, Item1);
            inventar.setItem(3, Item2);
            inventar.setItem(5, Item3);
            inventar.setItem(7, Item4);
            return this;
        }

        /**
         * Erstellt ein Menü anhand der Liste guiItems. Dabei ist es egal, ob guiItems 3 Itemstacks enthält, oder 300 ItemStacks
         * Die Idee hierhinter ist, diese Methode so universal wie möglich zu halten und zeitgleich ein standardisiertes Menü
         * für eine unbekannt Große Anzahl an GUI Items zu ermöglichen
         * @param titel Name des Menüs
         * @param guiItems Die zu verwendenden Items
         * @return
         */
        public List<Menu> MenusR5I36_9_MassenInventar(String titel, List<ItemStack> guiItems)
        {
            List<Menu> gespeicherteMenus = new ArrayList<>();
            int loop_nummer = 1;
            int menu_nummer = 1;
            for (int i = 0; i < guiItems.size(); i++)
            {
                Menu m;
                Log.SpigotLogger.Debug("Loop: " + i);
                if (loop_nummer == 1)
                {
                    m = erstelleBasisMenue(null, titel,36);
                    gespeicherteMenus.add(m);
                }
                //TODO: Testcommand für Inventar mit fake inventory
                if ((loop_nummer % 27) == 0 || !(loop_nummer <= menu_nummer * 36 - 9))
                {
                    menu_nummer += 1;
                    Log.SpigotLogger.Debug("Menue teiler von 27 oder max menu size: " + menu_nummer);
                    m = erstelleBasisMenue(null, titel,36);
                    gespeicherteMenus.add(m);
                }

                if (loop_nummer <= menu_nummer * 36 - 9)
                {
                    Log.SpigotLogger.Debug("Menue: " + menu_nummer);
                    m = gespeicherteMenus.get(menu_nummer-1);
                    gespeicherteMenus.remove(menu_nummer -1);
                    //GS-Item
                    itemHinzufuegen(m, guiItems.get(loop_nummer -1));
                    gespeicherteMenus.add(m);
                }

                if (guiItems.size() == loop_nummer || loop_nummer == menu_nummer * 36)
                {
                    m = gespeicherteMenus.get(menu_nummer-1);
                    ItemStack item_vor = erstelleGUIItem(Material.ENDER_EYE, "&fZurück", "&");
                    ItemStack item_back = erstelleGUIItem(Material.END_CRYSTAL, "&fZurück", "&");
                    //Nicht menue_nummer*36-1 da ein Menue ja nur 36 Items beinhaltet
                    m.inventar.setItem(36 - 9, item_back);
                    m.inventar.setItem(36 - 1, item_vor);
                    gespeicherteMenus.add(m);
                }
                loop_nummer += 1;
            }
            return gespeicherteMenus;
        }
    }

}
