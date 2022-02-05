package caesreon.core.listener;

import caesreon.core.CoreKomponente;
import caesreon.core.Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnect implements Listener {
    private final CoreKomponente modul;

    public PlayerDisconnect(CoreKomponente modul) {
        this.modul = modul;
    }

    @EventHandler
    public void onDisconnect(final PlayerQuitEvent event) {
        Log.SpigotLogger.Debug("PlayerDisconnect.onDisconnect()");
        Runnable saveWirtschaft = () -> {
            Log.SpigotLogger.Debug("onDisconnect().Runnable saveWirtschaft()");
            Player p = event.getPlayer();
            modul.getMain().getMerkurKomponente().getWirtschaftsModul().getEconomySQLHandler().spielerDisconnect(p);

        };

        Runnable saveBerufe = () -> {
            Log.SpigotLogger.Debug("onDisconnect().Runnable saveBerufe()");
            Player p = event.getPlayer();
            modul.getMain().getMerkurKomponente().getBerufeModul().getBerufeHandler().spielerDisconnect(p);
            modul.getSkynetModul().getSkynetHandler().spielerDisconnect(p);
        };

        Runnable saveSkyNet = () -> {
            Log.SpigotLogger.Debug("onDisconnect().Runnable r()");
            Player p = event.getPlayer();
            modul.getSkynetModul().getSkynetHandler().spielerDisconnect(p);
        };

        Bukkit.getScheduler().runTaskLaterAsynchronously(modul.getMain(), saveWirtschaft, 10L);
        Bukkit.getScheduler().runTaskLaterAsynchronously(modul.getMain(), saveBerufe, 20L);
        Bukkit.getScheduler().runTaskLaterAsynchronously(modul.getMain(), saveSkyNet, 15L);
    }


}
