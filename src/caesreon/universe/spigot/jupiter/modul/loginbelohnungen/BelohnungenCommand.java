package caesreon.universe.spigot.jupiter.modul.loginbelohnungen;

import caesreon.core.annotations.Befehl;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.PermissionUT;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


/**
 * Commands fuer taegliche Belohnungen
 */
public class BelohnungenCommand implements CommandExecutor
{
    private final BelohnungenModul modul;

    public BelohnungenCommand(BelohnungenModul modul) {
        this.modul = modul;
    }

    @Befehl
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command Command, @NotNull String arg2, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[" + modul.Prefix + "-Fehler] Dies ist ein Spielerbefehl und kann nur von Spielern ausgeführt werden");
            return true;
        }
        Player p = (Player) sender;

        // Hilfe-Befehl zum Anzeigen der moeglichen Parameter
        if (args.length == 0) {
            if (sender.hasPermission("caesreon.commands.spieler.loginbelohnung")) {
                ChatSpigot.NachrichtSenden(p, modul.Prefix, ChatColor.AQUA + "Caesreon Login Belohnungen");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/belohnung einlösen (Beansprucht Belohnung)");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/belohnung heute (Zeigt Tagesbelohnung)");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/belohnung monat (Zeigt alle Monatsbelohnungen)");
            }
            return true;
        }


        // Test-Befehl fuer einzelne Tagesbelohnungen
        if (args.length == 2 && args[0].equalsIgnoreCase("test")) {
            if (!p.hasPermission("caesreon.commands.admin")) {
                p.sendMessage("[" + modul.Prefix + "-Fehler] Dazu brauchst du administrative Rechte");
                return true;
            }

            try {
                int day = Integer.parseInt(args[1]);
                modul.getBelohnungsHandler().claimTest(p, day);
            } catch (NumberFormatException e) {
                p.sendMessage("[" + modul.Prefix + "-Fehler] Der zweite Parameter muss der Tag des Monats sein");
            }
            return true;
        }


        // Einlösen-Befehl zum Erhalten der Belohnung
        if (args.length == 1 && args[0].equalsIgnoreCase("einlösen")) {
            if (!p.hasPermission("caesreon.commands.spieler.loginbelohnung") && !PermissionUT.istZweitAccount(p)) {
                p.sendMessage("[" + modul.Prefix + "-Fehler] Du darfst leider keine Belohnung bekommen");
                return true;
            }

            modul.getBelohnungsHandler().claimTagesBelohnung(p);
            return true;
        }


        // Heute-Befehl zur Anzeige der heutigen Belohnung
        if (args.length == 1 && args[0].equalsIgnoreCase("heute")) {
            if (!p.hasPermission("caesreon.commands.spieler.loginbelohnung")) {
                p.sendMessage("[" + modul.Prefix + "-Fehler] Du darfst dir die Belohnung nicht ansehen");
                return true;
            }

            modul.getBelohnungsHandler().zeigeTagesBelohnung(p);
            return true;
        }


        // Monat-Befehl zum Anzeigen der Belohnungen des Monats
        if (args.length == 1 && args[0].equalsIgnoreCase("monat")) {
            if (!p.hasPermission("caesreon.commands.spieler.loginbelohnung")) {
                p.sendMessage("[" + modul.Prefix + "-Fehler] Du darfst dir die Belohnung nicht ansehen");
                return true;
            }

            modul.getBelohnungsHandler().zeigeMonatsBelohnungen(p);
            return true;
        }
        return false;
    }
}
