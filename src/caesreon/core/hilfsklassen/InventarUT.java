package caesreon.core.hilfsklassen;

import caesreon.core.Log;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventarUT {
    /**
     * Gedacht fuer Anwendung: Finde den EINZIGEN Itemstack in einem Inventar. Genutzt fuer Shop Erstellung
     *
     * @param inv
     * @return
     */
    public static ItemStack erhalteItemStackVonInventar_EINZEL(Inventory inv) {
        for (ItemStack i : inv.getContents()) {
            if (i != null) {
                return i;
            }

        }
        return null;
    }

    /**
     *
     * @param kiste
     * @param item
     * @param anzahl
     */
    public static void entferneItemAusKiste(Chest kiste, ItemStack item, int anzahl) {
        Inventory inv = kiste.getInventory();
        Material m = item.getType();

        Log.SpigotLogger.Debug(m.name());
        Log.SpigotLogger.Debug(anzahl);
        int itemStackZaehler = 0;
        for (ItemStack itemStack : inv.getContents()) {
            if (itemStack != null && itemStack.getType() == m) {
                if (anzahl <= 0)
                    return;

                if (anzahl < itemStack.getAmount()) {
                    Log.SpigotLogger.Debug(itemStack.getAmount());
                    Log.SpigotLogger.Debug(anzahl);
                    int nAnzahl = itemStack.getAmount() - anzahl;
                    Log.SpigotLogger.Debug(nAnzahl);
                    itemStack.setAmount(nAnzahl);
                    return;
                }
                if (itemStackZaehler < 1 && anzahl == itemStack.getAmount()) {
                    inv.removeItem(itemStack);
                    return;
                }
                itemStackZaehler += 1;
            }

        }

        if (itemStackZaehler == 1)
            for (ItemStack i : inv.getContents()) {
                if (i != null && i.getType() == m) {
                    if (anzahl == i.getAmount())
                        Log.SpigotLogger.Debug("Letztes Item in Stack.");
                    inv.remove(i);
                    return;
                }
            }

        inv.removeItem(new ItemStack(m, anzahl));
        kiste.update(true);
    }

    public static void entferneItemAusInv(Inventory inv, ItemStack item, int Anzahl) {
        Material m = item.getType();

        //System.out.println(m.name());
        //System.out.println(Anzahl);

        for (ItemStack i : inv.getContents()) {
            if (i != null && i.getType() == m) {
                if (Anzahl <= 0)

                    return;

                if (Anzahl < i.getAmount()) {
//	                 	System.out.println(i.getAmount());
                    //System.out.println(Anzahl);
                    int nAnzahl = i.getAmount() - Anzahl;
                    System.out.println(nAnzahl);
                    i.setAmount(nAnzahl);
                    return;
                }

                if (Anzahl == i.getAmount())
                    //TODO: Sehr fragil.
                    inv.remove(i);
                return;

            }
        }
    }

    public static Boolean pruefeInventarPlatz(Player p, String Prefix) {
        //System.out.println("pruefeInventarPlatz()");
        PlayerInventory i = p.getInventory();
        boolean InventarVoll = (i.firstEmpty() == -1);
        if (InventarVoll) {
            ChatSpigot.NachrichtSenden(p, Prefix, "Dein Inventar ist voll! Handel wurde abgebrochen.");
            return false;
        }
        return true;
    }

    public static Boolean pruefeInventarWareVorhanden(Player p, String Item, int Anzahl, String Prefix) {
        //System.out.println("pruefeInventarWareVorhanden()");
        Inventory inv = p.getInventory();
        Material m = Material.matchMaterial(Item);

        for (ItemStack i : inv.getContents()) {
            if (i != null && i.getType() == m) {
                //System.out.println(i.getAmount());
                if (i.getAmount() >= Anzahl) {
                    return true;
                }
                if (Anzahl <= 0)
                    return false;
            }

        }
        ChatSpigot.NachrichtSenden(p, Prefix, "Du hast alles Material dieser Art verkauft!");
        return false;
    }

    public static Boolean pruefeInventarWareVorhanden(Player p, ItemStack Item, int Anzahl, String Prefix) {
//		System.out.println("pruefeInventarWareVorhanden()");
        Inventory inv = p.getInventory();
        Material m = Item.getType();

        for (ItemStack i : inv.getContents()) {
            if (i != null && i.getType() == m) {
//    			System.out.println(i.getAmount());
                if (i.getAmount() >= Anzahl) {
                    return true;
                }
                if (Anzahl <= 0)
                    return false;
            }

        }
        ChatSpigot.NachrichtSenden(p, Prefix, "Du hast alles Material dieser Art verkauft!");
        return false;
    }

    public static Boolean pruefePlatzInChest(Chest c, String Item, int Anzahl) {
        //System.out.println("pruefPlatzInChest()");

        Inventory inv = c.getInventory();


        ItemStack itemToAdd = new ItemStack(Material.matchMaterial(Item), Anzahl);
        int foundcount = itemToAdd.getAmount();
        //System.out.println(String.valueOf(foundcount));

        for (ItemStack stack : inv.getContents()) {
            if (stack == null) {
                return true;
            }
            if (stack.getType() == itemToAdd.getType()) {
                foundcount -= itemToAdd.getMaxStackSize() - stack.getAmount();
                if (foundcount <= 0)
                    return true;
            }
        }
        return false;
    }

    public static Boolean pruefePlatzInChest(Chest c, ItemStack Item, int Anzahl) {
        System.out.println("pruefPlatzInChest()");

        Inventory inv = c.getInventory();

        Item.setAmount(Anzahl);
        int foundcount = Item.getAmount();
        System.out.println(foundcount);

        for (ItemStack stack : inv.getContents()) {
            if (stack == null) {
                return true;
            }
            if (stack.getType() == Item.getType()) {
                foundcount -= Item.getMaxStackSize() - stack.getAmount();
                if (foundcount <= 0)
                    return true;
            }
        }
        return false;
    }

    public static Boolean pruefeWareInChest(Chest c, String Item, int Anzahl) {

        //System.out.println("pruefeWareInChest()");
        try {
            Inventory inv = c.getInventory();
            if (inv == null) {
                return false;
            }
            Material m = Material.matchMaterial(Item);

            for (ItemStack i : inv.getContents()) {
                if (i != null && i.getType() == m) {
                    //System.out.println(i.getAmount());
                    if (i.getAmount() >= Anzahl) {
                        System.out.println("Ware war da");
                        return true;
                    }
                    if (Anzahl <= 0)
                        return false;
                }

            }
        } catch (Exception e) {
            return false;
        }
        System.out.println("Ware war nicht da");
        return false;
    }

    /**
     *
     * @param kiste
     * @param itemStack
     * @param anzahl
     * @return
     */
    public static Boolean pruefeWareInChest(Chest kiste, ItemStack itemStack, int anzahl) {

        //System.out.println("pruefeWareInChest()");
        try {
            Inventory inv = kiste.getInventory();
            if (inv == null) {
                return false;
            }
            Material m = itemStack.getType();

            for (ItemStack i : inv.getContents()) {
                if (i != null && i.getType() == m) {
                    //System.out.println(i.getAmount());
                    if (i.getAmount() >= anzahl) {
                        return true;
                    }
                    if (anzahl <= 0)
                        return false;
                }

            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public final static int konvertiereRawSlotzuInventorySlot(final InventoryClickEvent e) {
        switch (e.getRawSlot()) {
            case 36:
                return 0;
            case 37:
                return 1;
            case 38:
                return 2;
            case 39:
                return 3;
            case 40:
                return 4;
            case 41:
                return 5;
            case 42:
                return 6;
            case 43:
                return 7;
        }
        return (Integer) null;
    }
}
