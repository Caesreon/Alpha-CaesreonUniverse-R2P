package caesreon.universe.spigot.merkur.modul.berufe;

import caesreon.core.system.BasisBerechtigungsGruppen;
import caesreon.core.hilfsklassen.ChatSpigot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BerufeCommands implements CommandExecutor {
    BerufeModul modul;

    public BerufeCommands(BerufeModul modul) {
        this.modul = modul;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command Command, @NotNull String arg2, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length <= 3 && args[0].equalsIgnoreCase("test")) {
                modul.getBerufeHandler().test2(Integer.parseInt(args[1]), Integer.parseInt(args[2]), sender);
            }
            sender.sendMessage("Dies ist ein Spielerbefehl und kann nicht in der Console genutzt werden");
            return true;
        }

        Player p = (Player) sender;

        //beruf
        // Hilfe-Befehl zum Anzeigen der moeglichen Parameter
        if (args.length == 0) {
            if (sender.hasPermission("caesreon.commands.spieler.beruf")) {
                ChatSpigot.NachrichtSenden(p, BerufeModul.Prefix, ChatSpigot.chatTrennerModulBefehle(ChatColor.DARK_GREEN, ChatColor.AQUA + "[Caesreon-Berufe]"));
                ChatSpigot.NachrichtSenden(p, BerufeModul.Prefix, "/beruf");
                ChatSpigot.NachrichtSenden(p, BerufeModul.Prefix, "/beruf liste - Zeigt alle aktiven Berufe an");
                ChatSpigot.NachrichtSenden(p, BerufeModul.Prefix, "/beruf stats - Zeigt Errrungenschaften an");
            }
            return true;
        }


        //beruf info

        //beruf top

        //beruf liste
        if (args.length == 1 && args[0].equalsIgnoreCase("liste")) {
            if (p.hasPermission(BasisBerechtigungsGruppen.Buerger)) {
                modul.getBerufeHandler().berufListe(p);
            }
        }

        //beruf stats
        if (args.length <= 2 && args[0].equalsIgnoreCase("stats")) {
            if (p.hasPermission(BasisBerechtigungsGruppen.Buerger)) {
                modul.getBerufeHandler().berufStatistik(p);
            }
        }
        //*******************Team Commands********************

        //beruf aktivieren / laden <beruf>

        //beruf deaktivieren / entladen <beruf>
        if (args.length <= 2 && args[0].equalsIgnoreCase("hinzufuegen")) {
            if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
                modul.getBerufeHandler().berufZuDatenbankHinzufuegen(args[1]);
            }
        }

        if (args.length <= 2 && args[0].equalsIgnoreCase("entfernen")) {
            if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
                modul.getBerufeHandler().berufAusDatenbankLoeschen(args[1]);
            }
        }

        //beruf umbenennen <alterName> <neuerName>
        if (args.length <= 3 && args[0].equalsIgnoreCase("umbenennen")) {
            if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
                modul.getBerufeHandler().berufUmbenennen(args[1], args[2]);
            }
        }
        //beruf setzen <beruf> <level> <spielername>
        if (args.length <= 4 && args[0].equalsIgnoreCase("setzen")) {
            if (p.hasPermission(BasisBerechtigungsGruppen.Moderator)) {

            }
        }

        if (args.length <= 2 && args[0].equalsIgnoreCase("test")) {
            if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
                modul.getBerufeHandler().test(Integer.parseInt(args[1]), p);
            }
        }
        if (args.length <= 2 && args[0].equalsIgnoreCase("test2")) {
            if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
                modul.getBerufeHandler().testBerufeLaden(p);
            }
        }

        return true;
    }

}
