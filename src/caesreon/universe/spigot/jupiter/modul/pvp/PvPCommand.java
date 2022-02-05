package caesreon.universe.spigot.jupiter.modul.pvp;

import caesreon.core.Log;
import caesreon.core.hilfsklassen.ChatSpigot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PvPCommand implements CommandExecutor {
    PvPModul modul;

    public PvPCommand(PvPModul modul) {
        this.modul = modul;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command arg1, @NotNull String arg2, String[] arg3) {
        if (!(sender instanceof Player)) {
            if (arg3.length > 0) {
                Log.SpigotLogger.Debug("ConsoleSender");
            }
        }

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (arg3.length == 0) {
                ChatSpigot.NachrichtSenden(p, modul.Prefix, ChatColor.AQUA + "Caesreon PvP Service");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/pvp an - Macht dich verwundbar");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/pvp aus - Macht dich unverwundbar");
            }

            if (arg3.length == 1 && arg3[0].equalsIgnoreCase("an")) {
                modul.getPvpHandler().aktivierePvP(p);
                ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getPvpConfig().getString("text.pvp_aktiviert"));
            }

            if (arg3.length == 1 && arg3[0].equalsIgnoreCase("aus")) {
                modul.getPvpHandler().deaktivierePvP(p);
                ChatSpigot.NachrichtSenden(p, modul.Prefix, modul.getPvpConfig().getString("text.pvp_deaktiviert"));
            }

        }
        return true;
    }
}
