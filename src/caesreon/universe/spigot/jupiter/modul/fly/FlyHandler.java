package caesreon.universe.spigot.jupiter.modul.fly;

import caesreon.core.annotations.Handler;
import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.PermissionUT;
import net.luckperms.api.LuckPerms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

@Handler
public class FlyHandler {
    public static ArrayList<Player> Fliegen = new ArrayList<>();
    private static LuckPerms perms;
    private final FlyModul modul;

    public FlyHandler(FlyModul modul, LuckPerms lp) {
        this.modul = modul;
        perms = lp;
    }

    /**
     * Ingame Command fuer temp-fly
     *
     * @param p Der Spieler welcher Fliegen soll
     */
    public void setzeTempfly(Player p, String[] args) {
        if (args.length == 0) {
            if (Fliegen.contains(p)) {
                aktiviereFly(p, modul.getFlyConfig().getString("text.fly_temp_enabled"));
            } else {
                if (PermissionUT.isPlayerInGroup(p, modul.getFlyConfig().getString("fly_luckperms_gruppe"))) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, ChatColor.RED + modul.getFlyConfig().getString("text.fly_bereits_gekauft"));
                } else {
                    Spieler spieler = new Spieler(p);
                    if (modul.getJupiterKomponente().getMain().getMerkurKomponente().getServerWirtschaft().getCachedKontostand(spieler.getUuid()) >= modul.getFlyConfig().getInt("fly_kosten")) {
                        modul.getJupiterKomponente().getMain().getMerkurKomponente().getServerWirtschaft().ServerGeldAbbuchen(spieler, modul.getFlyConfig().getInt("fly_kosten"));
                        PermissionUT.setzeSpielerTemporaerGruppe(perms, p, modul.getFlyConfig().getString("fly_luckperms_gruppe"), modul.getFlyConfig().getLong("fly_dauer"));
                        aktiviereFly(p, modul.getFlyConfig().getString("text.fly_enabled"));
                    } else {
                        ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getFlyConfig().getString("text.fly_nicht_genug_geld"));
                    }
                }
            }
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getFlyConfig().getString("text.fly_keine_berechtigung"));
        }

    }

    /**
     * Diese Methode wird nur programmintern genutzt, um Spieler beispielweise mit Temp-fly zu Belohnen
     * @param p Der Spieler welcher Fliegen soll
     * @param Dauer Die Dauer des temporären Fliegens in Stunden
     */
    public void setzeTempfly(Player p, int Dauer) {
        if (Fliegen.contains(p)) {
            aktiviereFly(p, modul.getFlyConfig().getString("text.fly_temp_enabled"));
        } else {
            if (PermissionUT.isPlayerInGroup(p, modul.getFlyConfig().getString("fly_luckperms_gruppe"))) {
                ChatSpigot.NachrichtSenden(p, modul.Prefix, ChatColor.RED + modul.getFlyConfig().getString("text.fly_bereits_gekauft"));
            } else {
                try {
                    PermissionUT.setzeSpielerTemporaerGruppe(perms, p, modul.getFlyConfig().getString("fly_luckperms_gruppe"), Dauer);
                    aktiviereFly(p, modul.getFlyConfig().getString("text.fly_enabled"));
                } catch (Exception e) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getFlyConfig().getString("text.fly_nicht_genug_geld"));
                }
            }
        }
    }

    public void setzeFly(Player p, String[] args) {
        if (args.length == 0) {
            if (Fliegen.contains(p)) {
                deaktiviereFly(p, modul.getFlyConfig().getString("text.fly_disabled"));
            } else {
                aktiviereFly(p, modul.getFlyConfig().getString("text.fly_enabled"));
            }
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, ChatColor.RED + modul.getFlyConfig().getString("text.fly_keine_berechtigung"));
        }


    }

    public void setzeFlypeed(Player p, String args0) {
        float Speed = Float.parseFloat(args0);
        Log.SpigotLogger.Debug(String.valueOf(Speed));
        try {
            flySpeed(p, Speed);
        } catch (Exception e) {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, ChatColor.RED + modul.getFlyConfig().getString("text.fly_keine_berechtigung"));
        }
    }

    private void aktiviereFly(Player p, String Nachricht) {
        //Dieser Befehl wirft eine EXCEPTION player.setFlying(true) die beiden unteren Befehle funktionieren!!
        ChatSpigot.NachrichtSenden(p, modul.Prefix, Nachricht);
        Objects.requireNonNull(p.getPlayer()).setAllowFlight(true);
        p.getPlayer().getAllowFlight();
        Fliegen.add(p);
    }

    private void deaktiviereFly(Player p, String Nachricht) {
        ChatSpigot.NachrichtSenden(p, modul.Prefix, Nachricht);
        Objects.requireNonNull(p.getPlayer()).setAllowFlight(false);
        p.setFlying(false);
        Fliegen.remove(p);
    }

    private void flySpeed(Player p, float wert) {
        try {
            p.setFlySpeed(wert);
        } catch (Exception e) {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Irgendwas ist schief gelaufen. Du musst einen Wert zwischen 0.0 und 0.9 eingeben");
        }
    }


}
