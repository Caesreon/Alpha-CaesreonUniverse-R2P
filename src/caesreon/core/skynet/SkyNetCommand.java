package caesreon.core.skynet;

import caesreon.core.listener.BungeeListener;
import caesreon.core.Spieler;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.MenueUT;
import caesreon.core.system.BasisBerechtigungsGruppen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkyNetCommand implements CommandExecutor {
    SkyNetModul modul;
    public SkyNetCommand(SkyNetModul modul)
    {
        this.modul = modul;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command arg1, @NotNull String arg2, String[] args) {
        Player p = (Player) sender;
        if (sender.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
            if (args.length == 0) {
                ChatSpigot.NachrichtSenden(p, modul.prefix, "Caesreon Intelligence Service");
            }

            if (args.length >= 2 && args[0].equalsIgnoreCase("info")) {
                Spieler spieler = new Spieler(args[1]);
                modul.getSkynetHandler().info(p, spieler);
            }

            if (args.length >= 4 && args[0].equalsIgnoreCase("ip")) {
                BungeeListener bungeeListener = new BungeeListener();
                if (args[1].equalsIgnoreCase("-v")) {
                    modul.getSkynetHandler().vergleicheIPSpieler(p, args[2], args[3]);
                }
                if (args[1].equalsIgnoreCase("-e")) {
                    Spieler spieler = new Spieler(args[2]);
                    bungeeListener.erhalteIP(p, spieler);
                }
            }

            if (args.length >= 3 && (args[0].equalsIgnoreCase("inv") || args[0].equalsIgnoreCase("-i"))) {
                if (args[1].equalsIgnoreCase("main") || args[1].equalsIgnoreCase("-m")) {
                    Spieler spieler = new Spieler(args[2]);
                    MenueUT.oeffneInventar(p, spieler.getBukkitSpieler().getInventory());
                }
                if (args[1].equalsIgnoreCase("enderchest") || args[1].equalsIgnoreCase("-ec")) {
                    Spieler spieler = new Spieler(args[2]);
                    MenueUT.oeffneInventar(p, spieler.getBukkitSpieler().getEnderChest());
                }
            }
            return false;
        }
        return true;
    }
}
