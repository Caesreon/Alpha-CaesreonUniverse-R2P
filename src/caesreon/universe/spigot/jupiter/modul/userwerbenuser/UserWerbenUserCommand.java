package caesreon.universe.spigot.jupiter.modul.userwerbenuser;

import caesreon.core.COMMANDS;
import caesreon.core.Log;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.PermissionUT;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UserWerbenUserCommand implements CommandExecutor {
    UserWerbenUserModul modul;

    public UserWerbenUserCommand(UserWerbenUserModul modul) {
        this.modul = modul;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command arg1, @NotNull String arg2, String[] arg3) {
        String Token;
        Player p = (Player) sender;
        if (sender instanceof Player) {
            if (arg3.length == 0) {
                ChatSpigot.NachrichtSenden(p, modul.Prefix, ChatColor.AQUA + "User-Werben-User Service");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/uwu code erstellen - um eigenen Token zu erhalten");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/uwu code einlösen [Token] - um Token von einem existierenden Spieler einzulösen");
            }

            if (arg3.length > 0) {

                if (arg3[0].equalsIgnoreCase(COMMANDS.BASIS.UserWerbenUser.CODE.code.toString())) {
                    if (arg3[1].equalsIgnoreCase(COMMANDS.BASIS.UserWerbenUser.CODE.erstellen.toString())) {
                        modul.getUwUHandler().TokenErhalten(p);
                    }

                    if (arg3[1].equalsIgnoreCase(COMMANDS.BASIS.UserWerbenUser.CODE.einlösen.toString()) && !PermissionUT.istZweitAccount(p)) {
                        Token = arg3[2];
                        modul.getUwUHandler().TokenEinloesen(p, Token);
                    }

                    if (arg3[1].equalsIgnoreCase("import-players")) {
                        if (p.hasPermission("group.admin"))
                            Log.SpigotLogger.Debug("UWU: Befehl Importiere Account");
                        modul.getUwUHandler().importiereAltAccounts();
                    }
                }
            }
        }
        return true;
    }

}
