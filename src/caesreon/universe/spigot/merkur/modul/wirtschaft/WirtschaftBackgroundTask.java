package caesreon.universe.spigot.merkur.modul.wirtschaft;

import caesreon.core.Log;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class WirtschaftBackgroundTask {
    private WirtschaftsModul modul;

    public WirtschaftBackgroundTask(WirtschaftsModul modul) {
        this.modul = modul;
        runTask();
    }

    private void runTask() {
        if (WirtschaftsModul.WirtschaftsKonfiguration.getBoolean("EconomyBridge.saveDataTask.enabled")) {
            Log.SpigotLogger.Info("core.BackgroundTaskEconomy: Datenspeicherung wurde erlaubt.");
        } else {
            Log.SpigotLogger.Info("Datenspeicherung wurde als Task deaktiviert.");
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin) modul.getMain(), new Runnable() {
            public void run() {
                //modul.getEconomyDataHandler().updateBalanceMap();
            }
        }, 20L, 20L);
    }
}