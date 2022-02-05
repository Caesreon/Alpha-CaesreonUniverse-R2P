package caesreon.universe.spigot.merkur.modul.regionmarkt;

import caesreon.core.ColorCodeParser;
import caesreon.core.Menu;
import caesreon.core.MenuBuilder;
import caesreon.core.hilfsklassen.MenueUT;
import caesreon.core.interfaces.IMenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MenuManager implements Listener, IMenuManager {
    RegionMarktModul modul;
    Menu hauptmenu;
    List<Menu> meineGsOwnerMenus;
    List<Menu> meineGsMemberMenus;
    int inventarSeite = 1;

    public  MenuManager(RegionMarktModul modul) {
        this.modul = modul;
    }

    @Override
    public void onInventarClick(InventoryClickEvent e) {
        ItemStack is = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        MenueUT mUT = new MenueUT();
        if (is == null || is.getType().isAir()) {
            return;
        }

        else if (e.getInventory().equals(hauptmenu.inventar)) {
            e.setCancelled(true);
            hauptUI(e, p, is);
        } else if (meineGsOwnerMenus.contains(mUT.inventoryClickEventToMenu(e))) {
            e.setCancelled(true);
            meineGsOwnerUI(e, p, is);
        } else if (meineGsMemberMenus.contains(mUT.inventoryClickEventToMenu(e))) {
            e.setCancelled(true);
            meineGsMemberUI(e, p, is);
        }
    }

    @Override
    public void onInventarDrag(InventoryDragEvent e) {
        Player p = (Player) e.getWhoClicked();
        MenueUT mUT = new MenueUT();
        if (e.getInventory().equals(hauptmenu.inventar)) {
            e.setCancelled(true);
        } else if (meineGsOwnerMenus.contains(mUT.inventoryDragEventToMenu(e))) {
            e.setCancelled(true);
        } else if (meineGsMemberMenus.contains(mUT.inventoryDragEventToMenu(e))) {
            e.setCancelled(true);
        }
    }

    public void hauptUI(InventoryClickEvent e, Player p, ItemStack itemStack) {
        int slot = e.getRawSlot();
        inventarSeite = 1;
        switch (slot)
        {
            case 0:
                MenueUT.oeffneInventar(p, meineGsOwnerMenus.get(0).inventar);
                break;
            case 4:
                MenueUT.oeffneInventar(p, meineGsMemberMenus.get(0).inventar);
                break;
            case 8:
                break;
        }
    }

    public void meineGsOwnerUI(InventoryClickEvent e, Player p, ItemStack itemStack) {
        int slot = e.getRawSlot();
        switch (slot) {
            case 36-1:
                inventarSeite -= 1;
                MenueUT.oeffneInventar(p, meineGsOwnerMenus.get(inventarSeite -1).inventar);
                break;
            case 36-9:
                inventarSeite += 1;
                MenueUT.oeffneInventar(p, meineGsOwnerMenus.get(inventarSeite -1).inventar);
                break;
        }
    }

    public void meineGsMemberUI(InventoryClickEvent e, Player p,  ItemStack itemStack) {
        int slot = e.getRawSlot();
        switch (slot) {
            case 36-1:
                inventarSeite -= 1;
                MenueUT.oeffneInventar(p, meineGsMemberMenus.get(inventarSeite -1).inventar);
                break;
            case 36-9:
                inventarSeite += 1;
                MenueUT.oeffneInventar(p, meineGsMemberMenus.get(inventarSeite -1).inventar);
                break;
        }
    }

    /**
     * Erstellt alle notwendigen GUI Menues
     */
    protected void erstelleGUIMenus(Player p) {
        MenuBuilder mB = new MenuBuilder();
        MenuBuilder.MenuLayoutContainer mlc = new MenuBuilder.MenuLayoutContainer();

        hauptmenu = mlc.MenuR1I3(
                null,
                ColorCodeParser.ParseString(Objects.requireNonNull(
                        modul.getRegionMarktConfig().getString("gui.hauptmenu.titel")), modul.Regex),
                mB.erstelleGUIItem(modul.getRegionMarktConfig().getString("gui.hauptmenu.item_meine_gs_owner"),
                        modul.getRegionMarktConfig().getString("gui.hauptmenu.item_meine_gs_owner.text"), modul.Regex),
                mB.erstelleGUIItem(modul.getRegionMarktConfig().getString("gui.hauptmenu.item_meine_gs_member"),
                        modul.getRegionMarktConfig().getString("gui.hauptmenu.item_meine_gs_member.text"), modul.Regex),
                mB.erstelleGUIItem(modul.getRegionMarktConfig().getString("gui.hauptmenu.item_gs_info"),
                        modul.getRegionMarktConfig().getString("gui.hauptmenu.item_gs_info.text"), modul.Regex),
                mB.erstelleGUIItem(modul.getRegionMarktConfig().getString("gui.hauptmenu.item_hintergrund"),
                        null, modul.Regex));

        //List<RegionsDatenSatz> grundstueckeOwner = RegionMarktHandler.LadeAlleGS.spielerIstOwner(p);
        //List<RegionsDatenSatz> grundstueckeMember = RegionMarktHandler.LadeAlleGS.spielerIstMember(p);
        List<RegionsDatenSatz> grundstueckeOwner = RegionMarktHandler.LadeAlleGS.testDatensatz();
        List<RegionsDatenSatz> grundstueckeMember = RegionMarktHandler.LadeAlleGS.testDatensatz();

        List<ItemStack> guiMeta = new ArrayList<>();

        if (grundstueckeOwner != null) {
            for (RegionsDatenSatz grundstueck : grundstueckeOwner) {
                List<String> meta = Arrays.asList(
                        "&fWelt: " + grundstueck.Welt,
                        "&f" + grundstueck.getKoordinate().toString(),
                        "&fSteht zum Verkauf: &6" + grundstueck.Verkaufbar,
                        "&fWert: &2" + grundstueck.Preis
                );

                ItemStack guiItem = mB.erstelleGUIItem(
                        modul.getRegionMarktConfig().getString("gui.meinegsowner.item"),
                        "&f" + grundstueck.plotname,
                        meta,
                        modul.Regex);
                guiMeta.add(guiItem);
            }

            meineGsOwnerMenus = mlc.MenusR5I36_9_MassenInventar(
                    ColorCodeParser.ParseString(Objects.requireNonNull(modul.getRegionMarktConfig().getString("gui.meinegsowner.titel")), modul.Regex),
                    guiMeta
            );
        }

        if (grundstueckeMember != null) {
            for (RegionsDatenSatz grundstueck : grundstueckeMember) {
                List<String> meta = Arrays.asList(
                        "&fWelt: " + grundstueck.Welt,
                        "&f" + grundstueck.getKoordinate().toString()
                );

                ItemStack guiItem = mB.erstelleGUIItem(
                        modul.getRegionMarktConfig().getString("gui.meinegsmember.item"),
                        "&f" + grundstueck.plotname,
                        meta,
                        modul.Regex);
                guiMeta.add(guiItem);
            }

            meineGsMemberMenus = mlc.MenusR5I36_9_MassenInventar(
                    ColorCodeParser.ParseString(Objects.requireNonNull(modul.getRegionMarktConfig().getString("gui.meinegsmember.titel")), modul.Regex),
                    guiMeta
            );
        }
        mB.oeffneMenu(p, hauptmenu);
        //Menu InfoLimits = mlc.customMenu(null, module.getRegionMarktConfig().getString("gui.meinegsmember.titel"), 9);
        /*
         * TODO: Logik wie in Landlord mit nummerierten Bloecken, welche eine Zahl auf schwarzem Hintergrund darstellen
         */

    }

}
