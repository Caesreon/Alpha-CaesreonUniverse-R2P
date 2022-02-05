package caesreon.universe.spigot.merkur.modul.wirtschaft;

import caesreon.core.annotations.umlaut;
import caesreon.core.system.BasisBerechtigungsGruppen;
import caesreon.core.COMMANDS;
import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.hilfsklassen.ChatSpigot;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WirtschaftsCommands implements CommandExecutor {
    private final WirtschaftsModul modul;

    public WirtschaftsCommands(WirtschaftsModul modul) {
        this.modul = modul;
        Log.SpigotLogger.Debug("WirtschaftsCommands()");
    }

    @Override
    @umlaut
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command arg1, @NotNull String arg2, String[] arg3) {
        if (!(sender instanceof Player)) {
            if (arg3.length > 0) {
                Log.SpigotLogger.Debug("ConsoleSender");
                AdminCommands(arg3);
            }
        }

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (arg3.length == 0) {
                ChatSpigot.NachrichtSenden(p, modul.bankPrefix, ChatColor.AQUA + "Caesreon Wirtschafts Service");
                ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "/geld senden [Spielername] [Betrag]");
                ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "/geld top");
                ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "Kontostand: " + ChatColor.DARK_GREEN + String.format("%.0f", modul.getServerWirtschaft().getCachedKontostand(p.getUniqueId())) + ChatColor.WHITE + " Caesh");
                if (p.hasPermission(BasisBerechtigungsGruppen.Administrator))
                {
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "------------------------------");
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "/geld setzen [Spielername] [Betrag]");
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "/geld geben [Spielername] [Betrag]");
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "/geld abziehen [Spielername] [Betrag]'");
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "/geld einsehen [Spielername]");
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "/geld speichern");
                }
                return true;
            }

            try {
                //Spieler Comms
                if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.senden.toString())) {
                    try {
                        Spieler e_spieler = new Spieler(arg3[1]);
                        Spieler s_spieler = new Spieler(p);
                        modul.getServerWirtschaft().GeldUeberweisen(s_spieler, e_spieler, Double.parseDouble(arg3[2]));

                    } catch (Exception e) {
                        ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "Spieler " + arg3[1] + " unbekannt. Transaktion abgebrochen!");
                    }
                    return true;
                }

                if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.top.toString())) {
                    List<String> Vermoegende = modul.getServerWirtschaft().GeldTop10();
                    String[] Top10 = Vermoegende.toArray(new String[0]);
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, "Top 10 Vermögende"); //TODO: Umlaut
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, Top10[0]);
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, Top10[1]);
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, Top10[2]);
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, Top10[3]);
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, Top10[4]);
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, Top10[5]);
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, Top10[6]);
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, Top10[7]);
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, Top10[8]);
                    ChatSpigot.NachrichtSenden(p, modul.bankPrefix, Top10[9]);
                    return true;
                }

                AdminCommands((Player) sender, arg3);

            } catch (Exception e) {
                p.sendMessage(ChatColor.GOLD + modul.bankPrefix + ChatColor.RED + "Upps.. Irgendwas ist schief gelaufen. Befehl richtig eingegeben?");
                return true;
            }
        }

        return true;
    }

    private void AdminCommands(Player p, String[] arg3) {
        Spieler empfaenger = null;
        if (arg3.length >= 2)
        {
            empfaenger = new Spieler(arg3[1]);
        }

        if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
            //Admin Comms
            if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.ADMIN.setzen.toString())) {
                modul.getServerWirtschaft().GeldSetzen(empfaenger, Double.parseDouble(arg3[2]));
                ChatSpigot.NachrichtSenden(p, modul.bankPrefix, ChatColor.WHITE + "Du hast den Kontostand von Spieler " + ChatColor.GREEN + arg3[1] + ChatColor.WHITE + " auf " + Double.valueOf(arg3[2]) + " gesetzt.");
            }
            if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.ADMIN.speichern.toString())) {
                modul.getEconomySQLHandler().serverShutdownSpeicher();
                ChatSpigot.NachrichtSenden(p, modul.bankPrefix, ChatColor.WHITE + "Du hast alle volatilen Kontostände in der Datenbank gespeichert");
            }

        }

        if (p.hasPermission(BasisBerechtigungsGruppen.Moderator)) {
            //Moderierende Comms
            if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.ADMIN.geben.toString()) && arg3[3] == null) {
                modul.getServerWirtschaft().ServerGeldUeberweisen(empfaenger, Double.parseDouble(arg3[2]));
                ChatSpigot.NachrichtSenden(p, modul.bankPrefix, ChatColor.WHITE + "Du hast dem Spieler " + ChatColor.GREEN + arg3[1] + " " + ChatColor.WHITE + Double.valueOf(arg3[2]) + " Caesh gegeben.");
            }
            /*
            if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.ADMIN.geben.toString()) && arg3[3] != null) {
                StringBuilder sB = new StringBuilder();
                for (String s : arg3) {
                    if (!s.equalsIgnoreCase(arg3[0]) && !s.equalsIgnoreCase(arg3[1]) && !s.equalsIgnoreCase(arg3[2]))
                        sB.append(s + " ");
                }
                modul.getServerWirtschaft().ServerGeldUeberweisen(empfaenger, Double.parseDouble(arg3[2]), sB.toString());
                ChatUT.NachrichtSenden(p, modul.bankPrefix, ChatColor.WHITE + "Du hast dem Spieler " + ChatColor.GREEN + arg3[1] + " " + ChatColor.WHITE + Double.valueOf(arg3[2]) + " Caesh gegeben.");
            } */
            if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.ADMIN.abziehen.toString())) {
                modul.getServerWirtschaft().ServerGeldAbbuchen(empfaenger, Double.parseDouble(arg3[2]));
                ChatSpigot.NachrichtSenden(p, modul.bankPrefix, ChatColor.WHITE + "Du hast dem Spieler " + ChatColor.GREEN + arg3[1] + " " + ChatColor.WHITE + Double.valueOf(arg3[2]) + " Caesh abgezogen.");
            }
        }

        //Geld einsehen
        if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
            if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.ADMIN.einsehen.toString())) {
                ChatSpigot.NachrichtSenden(p, modul.bankPrefix, ChatColor.WHITE + "Spieler besitzt " + ChatColor.GREEN + String.format("%.2f", modul.getServerWirtschaft().getCachedKontostand(empfaenger.getUuid()))+ ChatColor.WHITE + " Caesh.");
            }

        }
    }

    //Console
    private void AdminCommands(String[] arg3) {
        Spieler empfaenger = new Spieler(arg3[1]);
        //Admin Comms
        if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.ADMIN.setzen.toString())) {
            modul.getServerWirtschaft().GeldSetzen(empfaenger, Double.parseDouble(arg3[2]));
        }

        if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.ADMIN.geben.toString()) && arg3[3] == null) {
            modul.getServerWirtschaft().ServerGeldUeberweisen(empfaenger, Double.parseDouble(arg3[2]));
        }

        if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.ADMIN.geben.toString()) && arg3[3] != null) {
            StringBuilder sB = new StringBuilder();
            for (String s : arg3) {
                if (!s.equalsIgnoreCase(arg3[0]) && !s.equalsIgnoreCase(arg3[1]) && !s.equalsIgnoreCase(arg3[2]))
                    sB.append(s).append(" ");
            }
            modul.getServerWirtschaft().ServerGeldUeberweisen(empfaenger, Double.parseDouble(arg3[2]), sB.toString());
        }

        if (arg3[0].equalsIgnoreCase(COMMANDS.ECONOMY.ADMIN.abziehen.toString())) {
            modul.getServerWirtschaft().ServerGeldAbbuchen(empfaenger, Double.parseDouble(arg3[2]));
        }
    }
}
