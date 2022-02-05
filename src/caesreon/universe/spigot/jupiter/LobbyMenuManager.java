package caesreon.universe.spigot.jupiter;

import caesreon.core.WarpBefehle;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.InventarUT;
import caesreon.core.interfaces.IMenuManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class LobbyMenuManager implements Listener, IMenuManager {
    private final JupiterKomponente modul;
    private final Inventory Lobby = null;

    public LobbyMenuManager(JupiterKomponente modul) {
        this.modul = modul;
    }

    private Boolean istLobbyInv(final InventoryClickEvent e) {
        try {
            if (e.getInventory() == modul.getLobbyMenu().getInventarLobby())
                return true;
        } catch (Exception ex) {
            return false;
        }

        return false;
    }

    private Boolean istInfoGUIInv(final InventoryClickEvent e) {
        try {
            if (e.getInventory() == modul.getLobbyMenu().getInventarInfoGUI())
                return true;
        } catch (Exception ex) {
            return false;
        }

        return false;
    }

    private Boolean istProjekteGUIInv(final InventoryClickEvent e) {
        try {
            if (e.getInventory() == modul.getLobbyMenu().getInventarProjektGUI())
                return true;
        } catch (Exception ex) {
            return false;
        }

        return false;
    }

    @Override
    public void onInventarClick(InventoryClickEvent e) {
        ItemStack is = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        if (is == null || is.getType().isAir()) {
        }

        else if (istProjekteGUIInv(e)) {
            e.setCancelled(true);
            ProjektInvClick(e, p, is);
        } else if (istInfoGUIInv(e)) {
            e.setCancelled(true);
            InfoGUIInvClick(e, p, is);
        } else if (istLobbyInv(e)) {
            e.setCancelled(true);
            LobbyLogikInvClick(e, p, is);
        }
    }

    private void InfoGUIInvClick(final InventoryClickEvent e, Player p, ItemStack item) {
        int Slot = e.getRawSlot();
        if (Slot == modul.getMenuConfig().getInt("gui.informationen.subitems.regeln.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.informationen.subitems.regeln.material"))))) {
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.regeln.url"));
        }
        if (Slot == modul.getMenuConfig().getInt("gui.informationen.subitems.wiki.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.informationen.subitems.wiki.material"))))) {
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.wiki.url"));
        }
        if (Slot == modul.getMenuConfig().getInt("gui.informationen.subitems.discord.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.informationen.subitems.discord.material"))))) {
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.discord.url"));
        }
        if (Slot == modul.getMenuConfig().getInt("gui.informationen.subitems.livemap.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.informationen.subitems.livemap.material"))))) {
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.livemap.url"));
        }
        if (Slot == modul.getMenuConfig().getInt("gui.informationen.subitems.voteseiten.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.informationen.subitems.voteseiten.material"))))) {
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.voteseiten.url.seite1"));
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.voteseiten.url.seite2"));
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.voteseiten.url.seite3"));
        }
        if (Slot == modul.getMenuConfig().getInt("gui.informationen.subitems.socialmedia.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.informationen.subitems.socialmedia.material"))))) {
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.socialmedia.url.facebook"));
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.socialmedia.url.twitter"));
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.socialmedia.url.youtube"));
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.socialmedia.url.instagram"));
        }
        if (Slot == modul.getMenuConfig().getInt("gui.informationen.subitem.changelog.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.informationen.subitems.changelog.material"))))) {
            ChatSpigot.NachrichtSenden(p, modul.getMain().getCore().getDefaultConfig().getString("ingameprefix"), modul.getMenuConfig().getString("gui.informationen.subitems.changelog.url"));
        }
    }

    private void ProjektInvClick(final InventoryClickEvent e, Player p, ItemStack item) {
        int Slot = e.getRawSlot();
        if (Slot == modul.getMenuConfig().getInt("gui.projekte.subitems.kathedrale.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.projekte.subitems.kathedrale.material"))))) {
            WarpBefehle.Kathedrale(p);
        }
        if (Slot == modul.getMenuConfig().getInt("gui.projekte.subitems.basilica.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.projekte.subitems.basilica.material"))))) {
            WarpBefehle.Basilica(p);
        }
        if (Slot == modul.getMenuConfig().getInt("gui.projekte.subitems.japan.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.projekte.subitems.japan.material"))))) {
            WarpBefehle.JapanGarten(p);
        }
        if (Slot == modul.getMenuConfig().getInt("gui.projekte.subitems.galleone.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.projekte.subitems.galleone.material"))))) {
            WarpBefehle.Galleone(p);
        }
        if (Slot == modul.getMenuConfig().getInt("gui.projekte.subitems.solterra_spawn.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("gui.projekte.subitems.solterra_spawn.material"))))) {
            WarpBefehle.SolterraSpawn(p);
        }
    }

    private void LobbyLogikInvClick(final InventoryClickEvent e, Player p, ItemStack item) {
        int Slot = e.getRawSlot();
        if (Slot > 8)
            Slot = InventarUT.konvertiereRawSlotzuInventorySlot(e);
        if (Slot == modul.getMenuConfig().getInt("hotbar.informationen.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("hotbar.informationen.material"))))) {
            modul.getLobbyMenu().Information_GUI(p);
        }

        if (Slot == modul.getMenuConfig().getInt("hotbar.survival.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("hotbar.survival.material"))))) {
            WarpBefehle.Survival(p);
        }
        if (Slot == modul.getMenuConfig().getInt("hotbar.creative.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("hotbar.creative.material"))))) {
            WarpBefehle.Creative(p);
        }

        if (Slot == modul.getMenuConfig().getInt("hotbar.projekte.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("hotbar.projekte.material"))))) {
            modul.getLobbyMenu().Projekte_GUI(p);
        }

        if (Slot == modul.getMenuConfig().getInt("hotbar.tutorial.position")
                && (item.getType() == Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("hotbar.tutorial.material"))))) {
            //CoreBefehle.Tutorial(p);
        }
    }

    @EventHandler
    @Override
    public void onInventarDrag(final InventoryDragEvent e) {
        if (Lobby != null) {
            e.getInventory();
        }
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent e) {
        if (Objects.requireNonNull(e.getPlayer().getLocation().getWorld()).getName().equalsIgnoreCase("hub") || e.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase("citymap")) {
            Player p = e.getPlayer();
            if (p.getInventory().getItemInMainHand().getType().equals(Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("hotbar.informationen.material"))))) {
                modul.getLobbyMenu().Information_GUI(p);
            }
            if (p.getInventory().getItemInMainHand().getType().equals(Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("hotbar.survival.material"))))) {
                WarpBefehle.Survival(p);
            }

            if (p.getInventory().getItemInMainHand().getType().equals(Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("hotbar.creative.material"))))) {
                WarpBefehle.Creative(p);
            }

            if (p.getInventory().getItemInMainHand().getType().equals(Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("hotbar.projekte.material"))))) {
                modul.getLobbyMenu().Projekte_GUI(p);
            }

            if (p.getInventory().getItemInMainHand().getType().equals(Material.matchMaterial(Objects.requireNonNull(modul.getMenuConfig().getString("hotbar.tutorial.material"))))) {
                //CoreBefehle.Tutorial(p);
            }
        }
    }

}
