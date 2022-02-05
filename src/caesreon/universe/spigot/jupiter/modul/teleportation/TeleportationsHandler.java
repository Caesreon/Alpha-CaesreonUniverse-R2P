package caesreon.universe.spigot.jupiter.modul.teleportation;

import caesreon.core.Log;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.universe.spigot.merkur.modul.wirtschaft.WirtschaftsModul;
import org.bukkit.entity.Player;

import java.util.Random;

public class TeleportationsHandler {
    private final TeleportationsModul modul;

    public TeleportationsHandler(TeleportationsModul modul) {
        this.modul = modul;
    }

    /**
     * Teleportiert den Spieler an einen zufaelligen Ort anhand eines in der Konfiguration hinterlegten Maximalwert.
     * Dieser Maximalwert wird nicht ueberschritten und so ergibt sich eine Koordinate zwischen dem positiven und negativen Maximalwert
     *
     * @param p - Der Spieler welcher teleportiert werden soll
     */
    public void zufallsTeleport(Player p) {
        Random zufallsGenerator = new Random();
        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Teleport zu einem zufälligem Ort..");
        int x = zufallsGenerator.nextInt(modul.erlaubterZufallsTeleportRadius * 2) - modul.erlaubterZufallsTeleportRadius ;
        int z = zufallsGenerator.nextInt(modul.erlaubterZufallsTeleportRadius * 2) - modul.erlaubterZufallsTeleportRadius ;
        Log.SpigotLogger.Debug("TEST: RTP x=" + x + " y=" + z);
        org.bukkit.Location zufallsOrt = p.getWorld().getHighestBlockAt(x, z).getLocation();
        zufallsOrt.setY(zufallsOrt.getY() + 1);
        p.teleport(zufallsOrt);
        modul.getJupiterKomponente().getMain().getMerkurKomponente().getServerWirtschaft().ServerGeldAbbuchen(WirtschaftsModul.Nationalbank.zuSpieler(), modul.getConfig().getInt("zufallsteleport.kosten"), "Zufallsteleport");
    }
}
