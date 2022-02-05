package caesreon.universe.spigot.jupiter.modul.loginbelohnungen;

import caesreon.core.ColorCodeParser;
import caesreon.core.interfaces.IMenuManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BelohnungsMenuManager implements Listener, IMenuManager {
    private final BelohnungenModul modul;
    private final Inventory Tag = null;
    private final Inventory Monat = null;

    public BelohnungsMenuManager(BelohnungenModul modul) {
        this.modul = modul;
    }


    private Boolean istTagInv(final InventoryClickEvent e) {

        try {
            if (e.getView().getTitle().equals(ColorCodeParser.ParseString(Objects.requireNonNull(modul.getBelohnungsConfig().getString("text.tagesbelohnung")), modul.Regex)))
                return true;
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    private Boolean istMonatInv(final InventoryClickEvent e) {
        try {
            if (e.getView().getTitle().contains(ColorCodeParser.ParseString(Objects.requireNonNull(modul.getBelohnungsConfig().getString("text.monatsbelohnung")), modul.Regex)))
                return true;
        } catch (Exception ex) {
            return false;
        }

        return false;
    }

    @Override @EventHandler
    public void onInventarClick(InventoryClickEvent e) {
        ItemStack is = e.getCurrentItem();
        if (is == null || is.getType().isAir()) return;

        if (istTagInv(e) || istMonatInv(e)) {
            e.setCancelled(true);
        }
    }

    @Override @EventHandler
    public void onInventarDrag(final InventoryDragEvent e) {
        if (Tag != null && e.getInventory() == Tag) {
            e.setCancelled(true);
        }
        if (Monat != null && e.getInventory().equals(Monat)) {
            e.setCancelled(true);
        }
    }
}
