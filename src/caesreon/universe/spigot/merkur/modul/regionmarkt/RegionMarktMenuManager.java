package caesreon.universe.spigot.merkur.modul.regionmarkt;

import caesreon.core.annotations.Handler;
import caesreon.core.ColorCodeParser;
import caesreon.core.hilfsklassen.ChatSpigot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RegionMarktMenuManager implements Listener {
    private RegionMarktModul module;
    private Inventory Owner = null;
    private Inventory Member = null;
    private Inventory Teleport = null;
    private Inventory FlagEinstellungen = null;
    private Inventory GS_Uebersicht = null;
    private Inventory FreundeVerwalten = null;

    public RegionMarktMenuManager(RegionMarktModul m) {
        module = m;
    }

    private void OwnerInvClick(final InventoryClickEvent e, Player p, ItemStack item) {

    }

    private void MemberInvClick(final InventoryClickEvent e, Player p, ItemStack item) {

    }

    private void TeleportInvClick(final InventoryClickEvent e, Player p, ItemStack item) {

    }

    private void FlagsVerwaltenInvClick(final InventoryClickEvent e, Player p, ItemStack item) {

    }

    private void GSUebersichtInvClick(final InventoryClickEvent e, Player p, ItemStack item) {
        int Slot = e.getRawSlot();
        if (Slot == module.getRegionMarktConfig().getInt("gui.informationen.subitems.regeln.position")
                && (item.getType() == Material.matchMaterial(module.getRegionMarktConfig().getString("gui.informationen.subitems.regeln.material")))) {
            ChatSpigot.NachrichtSenden(p, module.Prefix, module.getRegionMarktConfig().getString("gui.informationen.subitems.regeln.url"));
        }
    }

    private void FreundeVerwaltenInvClick(final InventoryClickEvent e, Player p, ItemStack item) {

    }

    /**
     * Boolean, welcher Menue "Meine GS-Owner" anhand des Menue Namens prueft
     *
     * @param e InventoryClickEvent
     * @return
     */
    private Boolean istOwnerInv(final InventoryClickEvent e) {

        try {
            if (e.getView().getTitle().equals(ColorCodeParser.ParseString(module.getRegionMarktConfig().getString("meinegsowner.titel"), module.Regex)))
                return true;
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * Boolean, welcher Menue "Meine GS-Mitgliedschaften" anhand des Menue Namens prueft
     *
     * @param e InventoryClickEvent
     * @return
     */
    private Boolean istMemberInv(final InventoryClickEvent e) {

        try {
            if (e.getView().getTitle().equals(ColorCodeParser.ParseString(module.getRegionMarktConfig().getString("meinegsmember.titel"), module.Regex)))
                return true;
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * Boolean, welcher Menue "Freie GS" anhand des Menue Namens prueft
     *
     * @param e InventoryClickEvent
     * @return
     */
    private Boolean istTeleportInv(final InventoryClickEvent e) {

        try {
            if (e.getView().getTitle().equals(ColorCodeParser.ParseString(module.getRegionMarktConfig().getString("freiegs.titel"), module.Regex)))
                return true;
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * Boolean, welcher Menue "Mein GS Verwalten" anhand des Menue Namens prueft
     *
     * @param e InventoryClickEvent
     * @return
     */
    private Boolean istFlagsVerwaltenInv(final InventoryClickEvent e) {

        try {
            if (e.getView().getTitle().equals(ColorCodeParser.ParseString(module.getRegionMarktConfig().getString("gs_verwalten.titel"), module.Regex)))
                return true;
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * Boolean, welcher Menue "Mein GS-Uebersicht" anhand des Menue Namens prueft
     *
     * @param e InventoryClickEvent
     * @return
     */
    private Boolean istGS_UebersichtInv(final InventoryClickEvent e) {

        try {
            if (e.getView().getTitle().equals(ColorCodeParser.ParseString(module.getRegionMarktConfig().getString("gs_uebersicht.titel"), module.Regex)))
                return true;
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * Boolean, welcher Menue "Freunde verwalten" anhand des Menue Namens prueft
     *
     * @param e InventoryClickEvent
     * @return
     */
    private Boolean istFreundeVerwaltenInv(final InventoryClickEvent e) {

        try {
            if (e.getView().getTitle().equals(ColorCodeParser.ParseString(module.getRegionMarktConfig().getString("gs_freunde_verwalten.titel"), module.Regex)))
                return true;
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * Methode, welche die eigentliche GUI Logik beinhaltet und auf das InventoryClickEvent() reagiert
     *
     * @param e
     */
    @EventHandler
    @Handler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack is = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        if (is == null || is.getType().isAir()) return;

        if (istOwnerInv(e)) {
            e.setCancelled(true);
            OwnerInvClick(e, p, is);
        } else if (istMemberInv(e)) {
            e.setCancelled(true);
            MemberInvClick(e, p, is);
        } else if (istFreundeVerwaltenInv(e)) {
            e.setCancelled(true);
            FreundeVerwaltenInvClick(e, p, is);
        } else if (istGS_UebersichtInv(e)) {
            e.setCancelled(true);
            GSUebersichtInvClick(e, p, is);
        } else if (istTeleportInv(e)) {
            e.setCancelled(true);
            TeleportInvClick(e, p, is);
        } else if (istFlagsVerwaltenInv(e)) {
            e.setCancelled(true);
            FlagsVerwaltenInvClick(e, p, is);
        }
        return;
    }

    /**
     * Methode welche Drag Events im Inventar verhindert. Ist wichtig, damit Items nicht entnommen werden koennen.
     *
     * @param e
     */
    @EventHandler
    @Handler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (Owner != null && e.getInventory() == Owner) {
            e.setCancelled(true);
        }
        if (Member != null && e.getInventory().equals(Member)) {
            e.setCancelled(true);
        }
        if (Teleport != null && e.getInventory() == Teleport) {
            e.setCancelled(true);
        }
        if (FlagEinstellungen != null && e.getInventory().equals(FlagEinstellungen)) {
            e.setCancelled(true);
        }
        if (GS_Uebersicht != null && e.getInventory() == GS_Uebersicht) {
            e.setCancelled(true);
        }
        if (FreundeVerwalten != null && e.getInventory().equals(FreundeVerwalten)) {
            e.setCancelled(true);
        }
    }
}
