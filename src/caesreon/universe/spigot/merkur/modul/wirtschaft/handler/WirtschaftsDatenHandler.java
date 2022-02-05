package caesreon.universe.spigot.merkur.modul.wirtschaft.handler;

import caesreon.universe.spigot.merkur.modul.wirtschaft.WirtschaftsModul;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Deprecated
public class WirtschaftsDatenHandler {
    private WirtschaftsModul modul;
    //Map um Spieler und Balancewert zu tracken
    private Map<Player, Double> backupMoney = new HashMap<>();
    private Map<Player, Double> balanceMap = new HashMap<>();
    private Map<Player, Integer> runningTasks = new HashMap<>();
    private Set<Player> playersInSync = new HashSet<>();

    public WirtschaftsDatenHandler(WirtschaftsModul modul) {
        this.modul = modul;
    }

    /**
     * Aktualisiere Map Kontostaende
     */
    public void updateBalanceMap() {
        //modul.getMain().getCoreAnkerPunkt().Debug("updateBalanceMap()");
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (!onlinePlayers.isEmpty())
            for (Player p : onlinePlayers) {
                if (this.playersInSync.contains(p))
                    this.balanceMap.put(p, modul.getServerWirtschaft().getCachedKontostand(p.getUniqueId()));
            }
    }

}