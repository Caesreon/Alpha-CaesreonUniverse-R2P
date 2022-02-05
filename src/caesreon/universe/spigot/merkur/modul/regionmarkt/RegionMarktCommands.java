package caesreon.universe.spigot.merkur.modul.regionmarkt;

import caesreon.core.annotations.Befehl;
import caesreon.core.system.BasisBerechtigungsGruppen;
import caesreon.core.Spieler;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.PermissionUT;
import caesreon.core.hilfsklassen.WeltUT;
import caesreon.universe.spigot.merkur.modul.regionmarkt.RegionMarktHandler.AnzahlGS;
import caesreon.universe.spigot.merkur.modul.regionmarkt.RegionMarktHandler.Limits;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RegionMarktCommands implements CommandExecutor {
    private final RegionMarktModul modul;

    public RegionMarktCommands(RegionMarktModul modul) {
        this.modul = modul;
    }

    @Befehl
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command Command, @NotNull String arg2, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dies ist ein Spieler-Befehl und kann nicht in der Console genutzt werden");
            return true;
        }


        Player p = (Player) sender;

        // Hilfe-Befehl
        if (args.length == 0) {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, ChatColor.AQUA + "Caesreon Immobilien Service");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs kaufen");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs auflösen");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs verkaufen");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs hinzufügen <member>/<owner> <spielername>");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs entfernen <member>/<owner> <spielername>");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs info");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs limits");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs claim");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs claims");
            //ChatUT.NachrichtSenden(p, modul.Prefix,"/gs gui");
            if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "==================================================");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "Die Team-Befehle sind:");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs enteignen [Regionsname]");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs verkaufbar <true>/<false> [Plotname]");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs setze owner/member [Regionsname] [NeuerBesitzer]");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs entferne owner/member [Regionsname] [NeuerBesitzer]");
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "/gs erstellen [Regionsname]");
            }
            return true;
        }

        if (WeltUT.CheckWeltenName.playerListe(p, modul.erlaubteWelten)) {

            // Info-Befehl
            if (args.length <= 2 && args[0].equalsIgnoreCase("info")) {
                if (p.hasPermission(modul.Permissions_Spieler)) {
                    if (args.length == 2)
                        modul.getRegionMarketHandler().gsInfo(p, args[1]);
                    if (args.length == 1)
                        modul.getRegionMarketHandler().gsInfo(p);
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst niemanden enteignen");
                    return true;
                }
            }

            // Erstellung-Befehl
            if (args.length == 2 && args[0].equalsIgnoreCase("erstellen")) {
                if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
                    modul.getRegionMarketHandler().RegionErstellen(args[1], p);
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst keine neue Region erstellen");
                }
            }

            // Loeschen-Befehl
            if (args.length == 2 && args[0].equalsIgnoreCase("löschen")) {
                if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
                    modul.getRegionMarketHandler().regionLoeschen(args[1], p);
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Region " + args[1] + " wurde erfolgreich gelöscht.");
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst keine Region löschen");
                }
            }

            // Claim-Befehl
            if (args.length == 1 && args[0].equalsIgnoreCase("claim")) {
                if ((p.hasPermission(modul.Permissions_Spieler)) && !PermissionUT.istZweitAccount(p)) {
                    modul.getRegionMarketHandler().claimChunk(p);
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst kein Grundstück erwerben");
                }
            }

            // Claims-Befehl
            if (args.length == 1 && args[0].equalsIgnoreCase("claims")) {
                if (p.hasPermission(modul.Permissions_Spieler)) {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast " + AnzahlGS.freebuild(p, modul) + " von "
                            + modul.getRegionMarktConfig().getInt(modul.configRootFreebuild + ".limit") + " Claims verwendet");
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst kein Grundstück erwerben");
                }
            }

            // test-chunk
            if (args.length == 1 && args[0].equalsIgnoreCase("chunk")) {
                if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
                    modul.getRegionMarketHandler().testeChunk(p);
                }
            }

            // Kaufen-Befehl
            if (args[0].equalsIgnoreCase("kaufen")) {
                if (p.hasPermission(modul.Permissions_Spieler)) {
                    if (args.length == 2)
                        modul.getRegionMarketHandler().kaufen(args[1], p, null);
                    if (args.length == 1)
                        modul.getRegionMarketHandler().kaufen(p);
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst kein Grundstück erwerben");
                }
            }


            // Verkaufen-Befehl
            if (args[0].equalsIgnoreCase("verkaufen")) {
                if (p.hasPermission(modul.Permissions_Spieler)) {
                    if (args.length == 2)
                        modul.getRegionMarketHandler().verkaufen(args[1], p);
                    if (args.length == 1)
                        modul.getRegionMarketHandler().verkaufen(p);

                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst kein GS verkaufen");
                }
            }

            // Aufloesen-Befehl
            if (args[0].equalsIgnoreCase("auflösen")) {
                if (p.hasPermission(modul.Permissions_Spieler)) {
                    if (args.length == 2)
                        modul.getRegionMarketHandler().regionAufloesen(args[1], p);
                    if (args.length == 1)
                        modul.getRegionMarketHandler().regionAufloesen(p);

                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst kein GS auflösen");
                }
            }

            // Hinzufuegen-Befehl
            if (args.length >= 3 && (args[0].equalsIgnoreCase("hinzufügen") || args[0].equalsIgnoreCase("+"))) {
                if (p.hasPermission(modul.Permissions_Spieler)) {
                    if (args[1].equalsIgnoreCase("owner") || args[1].equalsIgnoreCase("-o")) {

                        if (args[2].equalsIgnoreCase("alle") || args[2].equalsIgnoreCase("-a") ) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().ownerHinzufuegenAlleGS(p, spieler);
                        }

                        if (args.length == 4) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().ownerHinzufügenEinzelGSCached(args[2], p, spieler);
                        }

                        if (args.length == 3) {
                            Spieler spieler = new Spieler(args[2]);
                            modul.getRegionMarketHandler().ownerHinzufügenEinzelGSCached(null, p, spieler);
                        }


                    }
                    if (args[1].equalsIgnoreCase("member") || args[1].equalsIgnoreCase("-m")) {
                        if (args[2].equalsIgnoreCase("alle") || args[2].equalsIgnoreCase("-a") ) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().memberHinzufuegenAlleGS(p, spieler);
                        }
                        if (args.length == 4) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().memberHinzufügenEinzelGSCached(args[2], p, spieler);
                        }

                        if (args.length == 3) {
                            Spieler spieler = new Spieler(args[2]);
                            modul.getRegionMarketHandler().memberHinzufügenEinzelGSCached(null, p, spieler);
                        }
                    }
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst niemanden zu dem Grundstück hinzufügen");
                }
            }

            // Entfernen-Befehl
            if (args.length >= 3 && (args[0].equalsIgnoreCase("entfernen")  || args[0].equalsIgnoreCase("-"))) {
                if (p.hasPermission(modul.Permissions_Spieler)) {
                    if (args[1].equalsIgnoreCase("owner") || args[1].equalsIgnoreCase("-o")) {
                        if (args[2].equalsIgnoreCase("alle") || args[2].equalsIgnoreCase("-a") ) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().ownerEntfernenAlleGS(p, spieler);
                        }

                        if (args.length == 4) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().ownerEntfernenEinzelGSCached(args[2], p, spieler);
                        }
                        if (args.length == 3) {
                            Spieler spieler = new Spieler(args[2]);
                            modul.getRegionMarketHandler().ownerEntfernenEinzelGSCached(null, p, spieler);
                        }
                    }
                    if (args[1].equalsIgnoreCase("member") || args[1].equalsIgnoreCase("-m")) {
                        if (args[2].equalsIgnoreCase("alle") || args[2].equalsIgnoreCase("-a") ) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().memberEntfernenAlleGS(p, spieler);
                        }
                        if (args.length == 4) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().memberEntfernenEinzelGSCached(args[2], p, spieler);
                        }
                        if (args.length == 3) {
                            Spieler spieler = new Spieler(args[2]);
                            modul.getRegionMarketHandler().memberEntfernenEinzelGSCached(null, p, spieler);
                        }
                    }
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst niemanden vom Grundstück entfernen");
                }
            }

            // Hinzufuegen-Befehl TEAM
            if (args.length >= 2 && args[0].equalsIgnoreCase("setze")) {
                if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
                    if (args[1].equalsIgnoreCase("owner")) {
                        if (args.length == 4) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().ownerHinzufügenEinzelGS(args[2], p, spieler);
                        }

                        if (args.length == 3) {
                            Spieler spieler = new Spieler(args[2]);
                            modul.getRegionMarketHandler().ownerHinzufügenEinzelGS(null, p, spieler);
                        }
                    }
                    if (args[1].equalsIgnoreCase("member")) {
                        if (args.length == 4) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().memberHinzufügenEinzelGS(args[2], p, spieler);
                        }

                        if (args.length == 3) {
                            Spieler spieler = new Spieler(args[2]);
                            modul.getRegionMarketHandler().memberHinzufügenEinzelGS(null, p, spieler);
                        }

                    }
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst niemanden zu dem Grundstück hinzufügen");
                }
            }

            // Entfernen-Befehl TEAM
            if (args.length >= 2 && args[0].equalsIgnoreCase("entferne")) {
                if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
                    if (args[1].equalsIgnoreCase("owner")) {
                        if (args.length == 4) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().ownerEntfernenEinzelGS(args[2], p, spieler);
                        }
                        if (args.length == 3) {
                            Spieler spieler = new Spieler(args[2]);
                            modul.getRegionMarketHandler().ownerEntfernenEinzelGS(null, p, spieler);
                        }
                    }
                    if (args[1].equalsIgnoreCase("member")) {
                        if (args.length == 4) {
                            Spieler spieler = new Spieler(args[3]);
                            modul.getRegionMarketHandler().memberEntfernenEinzelGS(args[2], p, spieler);
                        }
                        if (args.length == 3) {
                            Spieler spieler = new Spieler(args[2]);
                            modul.getRegionMarketHandler().memberEntfernenEinzelGS(null, p, spieler);
                        }
                    }
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst niemanden vom Grundstück entfernen");
                }
            }

            // GUI-Befehl
            if (args.length == 1 && args[0].equalsIgnoreCase("gui")) {
                if (p.hasPermission(modul.Permissions_Spieler)) {
                    modul.getMenuManager().erstelleGUIMenus(p);
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst das Menü nicht öffnen");
                }
            }

            // Limits-Befehl  Hinweis: Diese Verzweigung ist so gewählt, dass zukuenftig vll jede permgruppe eigene Limits hat! Ist aber nicht der Fall aktuell
            if (args.length == 1 && args[0].equalsIgnoreCase("limits")) {
                if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
                    Limits.administrator(p, modul);
                } else if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
                    Limits.team(p, modul);
                } else if (p.hasPermission(modul.Permissions_Ehrenlegion)) {
                    Limits.ehrenlegion(p, modul);
                } else if (p.hasPermission(modul.Permissions_Spieler)) {
                    Limits.buerger(p, modul);
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst das Menü nicht öffnen");
                }
            }

            // Enteignen-Befehl
            if (args.length >= 2 && args[0].equalsIgnoreCase("enteignen")) {
                if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
                    modul.getRegionMarketHandler().UpdateEnteignung(args[1], p);
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Die Region " + args[1] + " wurde verstaatlicht");
                } else {
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du darfst niemanden enteignen");
                    return true;
                }
            }

            //Modifzierbefehl fuer die Verkaufbarkeit TODO: Eventuell auf Angabe des Plots verzichten und anhand der Position
            if (args.length <= 3 && args[0].equalsIgnoreCase("verkaufbar")) {
                if (p.hasPermission(BasisBerechtigungsGruppen.Moderator)) {
                    if (args[1].equalsIgnoreCase("true")) {
                        modul.getRegionMarketHandler().setzeVerkaufsFlag(args[2], true);
                    }
                    if (args[1].equalsIgnoreCase("false")) {
                        modul.getRegionMarketHandler().setzeVerkaufsFlag(args[2], false);
                    }
                }
            }
            //Modifzierbefehl um Gs-Namen zu aendern
            if (args.length <= 4 && args[0].equalsIgnoreCase("mod")) {
                if (args[1].equalsIgnoreCase("name")) {
                    if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
                        modul.getRegionMarketHandler().aendereName(args[2], args[3], p);
                    }
                }
                if (args[1].equalsIgnoreCase("preis")) {
                    if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
                        modul.getRegionMarketHandler().aenderePreis(args[2], Integer.parseInt(args[3]), p);
                    }
                }
            }
        }

        return true;
    }
}
