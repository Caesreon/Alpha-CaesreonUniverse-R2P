package caesreon.core.hilfsklassen;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class WeltUT {
    public static class CheckWeltenName {
        public static Boolean playerInteractEvent(PlayerInteractEvent e, String weltenName) {
            if (e.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(weltenName))
                return true;
            return false;
        }

        public static boolean player(Player p, String Weltenname) {
            if (p.getWorld().getName().equalsIgnoreCase(Weltenname)) {
                return true;
            }
            return false;
        }

        public static Boolean playerInteractEventListe(PlayerInteractEvent e, List<String> weltenListe) {
            for (String weltenName : weltenListe) {
                if (e.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(weltenName))
                    return true;
            }
            return false;
        }

        public static Boolean playerListe(Player p, List<String> weltenListe) {
            for (String weltenName : weltenListe) {
                if (p.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(weltenName))
                    return true;
            }
            return false;
        }

    }

}
