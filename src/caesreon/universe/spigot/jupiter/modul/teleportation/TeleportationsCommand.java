package caesreon.universe.spigot.jupiter.modul.teleportation;

import caesreon.core.annotations.Befehl;
import caesreon.core.system.BasisBerechtigungsGruppen;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.WeltUT;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeleportationsCommand implements CommandExecutor {
    private final TeleportationsModul modul;

    public TeleportationsCommand(TeleportationsModul modul) {
        this.modul = modul;
    }

    @Befehl
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command Command, @NotNull String arg2, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dies ist ein Spielerbefehl und kann nicht in der Console genutzt werden");
            return true;
        }

        Player p = (Player) sender;

        // Hilfe-Befehl
        if (args.length == 0) {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, ChatColor.AQUA + "Caesreon Teleportations Service");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "/ctp z - Für zufälligen Teleport");
        }

        // Zufallsteleport
        if (args.length == 1 && (args[0].equalsIgnoreCase("z") ||args[0].equalsIgnoreCase("zufall"))) {
            if (p.hasPermission(BasisBerechtigungsGruppen.Basis_Spieler) && WeltUT.CheckWeltenName.playerListe(p, modul.erlaubteWelten)) {
                modul.getTeleportationsHandler().zufallsTeleport(p);
            } else {
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst diesen Befehl hier leider nicht ausführen");
            }
        }
        return true;
    }
}
