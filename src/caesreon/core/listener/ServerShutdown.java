package caesreon.core.listener;

import caesreon.core.CoreKomponente;
import org.bukkit.event.Listener;

public class ServerShutdown implements Listener {
    private CoreKomponente modul;

    public ServerShutdown(CoreKomponente modul) {
        this.modul = modul;
    }

    public void datenSpeicherung() {
        modul.getMain().getMerkurKomponente().getWirtschaftsModul().getEconomySQLHandler().serverShutdownSpeicher();
        modul.getMain().getMerkurKomponente().getRegionMerktModul().getRegionMarketHandler().onServerShutdown();
        /*
        Runnable speichereWirtschaftsDaten = () -> {
            try {
                modul.getMain().getMerkurKomponente().getWirtschaftsModul().getEconomySQLHandler().serverShutdownSpeicher();
            } catch (Exception e) {
                Log.Debug(e.toString());
            }
        };
        Runnable speichereGrundstuecksDaten = () -> {
            try {
                modul.getMain().getMerkurKomponente().getRegionMerktModul().getRegionMarketHandler().onServerShutdown();

            } catch (Exception e) {
                Log.Debug(e.toString());
            }
            ;
        };

        Bukkit.getScheduler().runTaskLaterAsynchronously(modul.getMain(), speichereWirtschaftsDaten, 10L);
        Bukkit.getScheduler().runTaskLaterAsynchronously(modul.getMain(), speichereGrundstuecksDaten, 20L);

         */
    }

}
