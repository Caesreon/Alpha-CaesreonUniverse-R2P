package caesreon.core.hilfsklassen;

import caesreon.core.Menu;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Hybride, also halb statische Klasse, um Inventare von User Interfaces in das jeweilige Menu umzuwandeln, um diese zu öffnen
 * oder zu schliessen
 */
public final class MenueUT extends Menu {

    public static void oeffneInventar(final @NotNull HumanEntity ent, Inventory inv) {
        ent.openInventory(inv);
    }

    /**
     * Nicht statische Methode um InventoryClickEvent in Menu umzuwandeln
     * @param inventoryClickEvent
     * @return
     */
    public Menu inventoryClickEventToMenu(@NotNull InventoryClickEvent inventoryClickEvent) {
        inventar = inventoryClickEvent.getInventory();
        this.slots = inventoryClickEvent.getInventory().getSize();
        this.titel = inventoryClickEvent.getView().getTitle();
        return this;
    }

    /**
     * Nicht statische Methode um InventoryClickEvent in Menu umzuwandeln
     * @param inventoryDragEvent
     * @return
     */
    public Menu inventoryDragEventToMenu(@NotNull InventoryDragEvent inventoryDragEvent) {
        inventar = inventoryDragEvent.getInventory();
        this.slots = inventoryDragEvent.getInventory().getSize();
        this.titel = inventoryDragEvent.getView().getTitle();
        return this;
    }

    public void closeInventory(final HumanEntity ent, Inventory inv) {
        ent.openInventory(inv);
    }
}
