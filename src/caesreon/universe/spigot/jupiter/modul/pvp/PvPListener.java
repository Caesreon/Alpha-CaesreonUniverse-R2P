package caesreon.universe.spigot.jupiter.modul.pvp;

import caesreon.core.Log;
import caesreon.core.hilfsklassen.ChatSpigot;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvPListener implements Listener {
    private PvPModul modul;

    public PvPListener(PvPModul modul) {
        this.modul = modul;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void entitaetenSchaden(EntityDamageByEntityEvent e) {
        Entity verteidiger = e.getEntity();
        Entity angreifer = e.getDamager();
        if (angreifer instanceof Player && verteidiger instanceof Player) {
            Player spieler_verteidiger = (Player) verteidiger;
            Player spieler_angreifer = (Player) angreifer;

            Log.SpigotLogger.Debug("Angreifer war Spieler");

            Log.SpigotLogger.Debug(spieler_angreifer.getDisplayName());
            Log.SpigotLogger.Debug(spieler_verteidiger.getDisplayName());
            if (modul.pvpSpieler.contains(spieler_verteidiger.getUniqueId().toString()) && modul.pvpSpieler.contains(spieler_angreifer.getUniqueId().toString())) {
                Log.SpigotLogger.Debug("Angreifer war Spieler");
                return;
            }
            ChatSpigot.NachrichtSenden(spieler_angreifer, modul.Prefix, modul.getPvpConfig().getString("text.pvp_abgelehnt"));
            e.setCancelled(true);
        } else if (angreifer instanceof Arrow) {
            Arrow pfeil = (Arrow) angreifer;
            if (pfeil.getShooter() instanceof Player && verteidiger instanceof Player) {
                Player schuetze = (Player) pfeil.getShooter();
                if (modul.pvpSpieler.contains(schuetze.getUniqueId().toString()) && modul.pvpSpieler.contains(verteidiger.getUniqueId().toString())) {
                    Log.SpigotLogger.Debug("Angreifer war Spieler");
                    return;
                }
                ChatSpigot.NachrichtSenden(schuetze, modul.Prefix, modul.getPvpConfig().getString("text.pvp_abgelehnt"));
                e.setCancelled(true);
            }
        }
    }

}
