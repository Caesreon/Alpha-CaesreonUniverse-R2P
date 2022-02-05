package caesreon.universe.spigot.jupiter.modul.pvp;

import caesreon.core.Log;
import org.bukkit.entity.Player;

import java.io.IOException;

public class PvPHandler {
    PvPModul modul;

    public PvPHandler(PvPModul modul) {
        this.modul = modul;
    }

    protected void aktivierePvP(Player p) {
        modul.pvpSpieler.add(p.getUniqueId().toString());
        speichereConfig();
    }

    protected void deaktivierePvP(Player p) {
        modul.pvpSpieler.remove(p.getUniqueId().toString());
        speichereConfig();
    }

    private void speichereConfig() {
        modul.getPvpConfig().set("pvp_spieler", modul.pvpSpieler);
        try {
            modul.getPvpConfig().save(modul.pvpConfigSet.getFileYML());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.SpigotLogger.Debug(e.toString());
            ;
        }
    }
}
