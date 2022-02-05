package caesreon.universe.spigot.jupiter.modul.mail;

import caesreon.core.Log;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.skynet.SpielerLookup;
import caesreon.universe.spigot.jupiter.JupiterKomponente;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MailCommand implements CommandExecutor {
    private JupiterKomponente modul;

    public MailCommand(JupiterKomponente modul) {
        this.modul = modul;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            StringBuilder sb;
            String allArgs;

            if (p.hasPermission("caesreon.commands.spieler.basis")) {
                if (args == null) {
                    return true;
                }

                if (args.length == 0) {
                    ChatSpigot.NachrichtSenden(p, "[Mail] ", ChatColor.AQUA + "Caesreon Mailing Service");
                    ChatSpigot.NachrichtSenden(p, "[Mail] ", "/mail senden [Spielername] [Nachricht] um Mail zu senden");
                    ChatSpigot.NachrichtSenden(p, "[Mail] ", "/mail empfangen um aktuelle Mails zu laden");
                    ChatSpigot.NachrichtSenden(p, "[Mail] ", "/mail öffnen [Nr] um Mail zu öffnen.");
                }
                if (args.length > 0) {
                    switch (args[0]) {
                        case "senden":

                            Log.SpigotLogger.Debug(args[1]);
                            String uuid = SpielerLookup.SchliesseConnection.getUUID(args[1]).toString();
                            Log.SpigotLogger.Debug(uuid);
                            sb = new StringBuilder();
                            for (int i = 0; i < args.length; i++) {
                                if (i >= 2) {
                                    sb.append(args[i]).append(" ");
                                }
                            }

                            allArgs = sb.toString().trim();
                            try {
                                modul.getMailHandler().sendMail(p.getUniqueId().toString(), SpielerLookup.SchliesseConnection.getUUID(args[1]).toString(), allArgs);
                                ChatSpigot.NachrichtSenden(p, "[Mail]", "Nachricht wurde an " + ChatColor.GOLD + args[1] + ChatColor.WHITE + " gesendet!");
                            } catch (Exception e) {
                                ChatSpigot.NachrichtSenden(p, "[Mail]", ChatColor.RED + "Spieler konnte nicht gefunden werden");
                            }
                            break;
                        case "send":
                            sb = new StringBuilder();
                            for (int i = 0; i < args.length; i++) {
                                if (i >= 2) {
                                    sb.append(args[i]).append(" ");
                                }
                            }

                            allArgs = sb.toString().trim();
                            try {
                                modul.getMailHandler().sendMail(p.getUniqueId().toString(), SpielerLookup.SchliesseConnection.getUUID(args[1]).toString(), allArgs);
                                ChatSpigot.NachrichtSenden(p, "[Mail]", "Nachricht wurde an " + ChatColor.GOLD + args[1] + ChatColor.WHITE + " gesendet!");
                            } catch (Exception e) {
                                ChatSpigot.NachrichtSenden(p, "[Mail]", ChatColor.RED + "Spieler konnte nicht gefunden werden");
                            }
                            break;
                        case "empfangen":
                            try {
                                modul.getMailHandler().MailEmpfangen(p);
                                if (modul.getMailHandler().KeineMailBenachrichtigung() != null) {
                                    p.sendMessage(modul.getMailHandler().KeineMailBenachrichtigung());
                                }
                            } catch (Exception e) {
                                ChatSpigot.NachrichtSenden(p, "[Mail]", ChatColor.RED + "Upps irgendwas ist schief gelaufen");
                            }
                            break;
                        case "öffnen":
                            try {
                                modul.getMailHandler().MailOeffnen(p, modul.getMailHandler().getMails(p), args[1]);
                            } catch (Exception e) {
                                ChatSpigot.NachrichtSenden(p, "[Mail]", ChatColor.RED + "Upps irgendwas ist schief gelaufen");
                            }
                            break;
                    }
                }
            }
        }
        return true;
    }

}
