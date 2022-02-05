package caesreon.universe.spigot.merkur.modul.regionmarkt;

import caesreon.core.hilfsklassen.ChatSpigot;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class RegionSchild implements Listener {

    private final RegionMarktModul modul;

    public RegionSchild(RegionMarktModul m) {
        this.modul = m;
    }

    @EventHandler
    public void SchildInteraktionClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            //Wenn Linksklick wird hier die Methode wieder verlassen
            return;
        }

        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        assert b != null;
        if ((b.getType().name().contains("_SIGN"))) {
            Sign s = (Sign) b.getState();
            if (s.getLine(0).equals(modul.Farbiger_Identifier)) {
                if (s.getLine(1).equals("§2" + modul.Kaufen)) {
                    modul.getRegionMarketHandler().kaufen(s.getLine(3), p, s);
                    return;
                }
                if (s.getLine(1).equals("§4" + modul.Verkauft)) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses Grundstück gehört bereits " + s.getLine(2));
                }
            }
        }
    }

    @EventHandler
    public void erstelleRegionSchild(SignChangeEvent e) {
        if (e.getPlayer().hasPermission(modul.Permissions_Spieler)) {
            for (int i = 0; i < 4; i++) {
                String line = e.getLine(i);
                if (line != null && !line.equals("")) {
                    e.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
                }
            }
            if (Objects.equals(e.getLine(0), modul.Identifier)) {
                e.setLine(0, modul.Farbiger_Identifier);
                if (Objects.requireNonNull(e.getLine(2)).isEmpty() && Objects.requireNonNull(e.getLine(1)).isEmpty()) {
                    modul.getRegionMarketHandler().setzeRegionSchild(e, e.getPlayer());
                }

                if (Objects.requireNonNull(e.getLine(2)).isEmpty() && !Objects.requireNonNull(e.getLine(1)).isEmpty()) {
                    modul.getRegionMarketHandler().setzeRegionSchild(e, e.getPlayer(), e.getLine(1));
                }

                if (Objects.equals(e.getLine(2), modul.Verkaufen)) {
                    modul.getRegionMarketHandler().setzeRegionSchildVerkaufen(e, e.getPlayer());
                }

            }
        }
    }
}