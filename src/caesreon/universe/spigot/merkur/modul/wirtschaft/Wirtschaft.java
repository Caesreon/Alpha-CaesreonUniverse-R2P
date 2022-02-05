package caesreon.universe.spigot.merkur.modul.wirtschaft;

import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.MathematikUT;
import caesreon.universe.spigot.merkur.modul.wirtschaft.handler.WirtschaftsHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.*;

public class Wirtschaft {
    public static Map<UUID, Double> lokaleSpielerKontoDaten = new HashMap<>();
    /**
     * Diese Klasse ist der Kernzugriffspunkt der Servereigenen Economy
     */

    private final WirtschaftsModul modul;

    public Wirtschaft(WirtschaftsModul modul) {
        this.modul = modul;
        Log.SpigotLogger.Debug("Wirtschaft()");
    }

    public static void setzeCachedSpielerKontostand(UUID uuid, Double Kontostand) {
        lokaleSpielerKontoDaten.put(uuid, Kontostand);
    }

    public static void entferneCaechedSpielerKontostand(UUID uuid) {
        lokaleSpielerKontoDaten.remove(uuid);
    }

    /**
     * Laed Kontostand anhand der als Key hinterlegten UUID des Spielers
     *
     * @param uuid des Spielers
     * @return Den lokalen (volatilen) Kontostand des Spielers
     */
    public double getCachedKontostand(UUID uuid) {
        Log.SpigotLogger.Verbose("Economy.getCachedEconomy()");
        if (lokaleSpielerKontoDaten.get(uuid) == null) {
            Log.SpigotLogger.Debug("Economy.getCachedEconomy() war NPE; lade Kto von Datenbank");
            setzeCachedSpielerKontostand(uuid, Kontostand(uuid));
            Log.SpigotLogger.Debug(String.valueOf(lokaleSpielerKontoDaten.get(uuid)));
            return lokaleSpielerKontoDaten.get(uuid);
        }
        Log.SpigotLogger.Verbose(String.valueOf(lokaleSpielerKontoDaten.get(uuid)));
        return lokaleSpielerKontoDaten.get(uuid);
    }

    /**
     * Gibt den aktuellen Kontostand des Spielers wieder
     *
     * @param uuid UUID des Spielers oder vSpielers, wessen Kontostand abgefragt wird
     * @return Den aktuellen Kontostand des Spielers
     */
    public double Kontostand(UUID uuid) {
        try {
            WirtschaftsKonto spielerKonto = modul.getEconomySQLHandler().prüfeObSpielerKontoExistiert(uuid);
            return spielerKonto.getKontostand();
        } catch (Exception e) {
            Log.SpigotLogger.Debug((e.toString()));
            return 0;
        }
    }

    /**
     * Gibt den aktuellen Kontostand des Spielers wieder
     *
     * @param uuid Die UUID des Spielers
     * @return Den in der Datenbank hinterlegen Kontostand des Spielers, insofern der Spieler keinen lokalen Kontostand im RAM geladen hat
     */
    public double Kontostand(String uuid) {
        try {
            if (Objects.requireNonNull(Bukkit.getPlayer(uuid)).isOnline() && !(WirtschaftsHandler.spielerMitFehlerhaftenKonten.contains(UUID.fromString(uuid)))) {
                return lokaleSpielerKontoDaten.get(Bukkit.getPlayer(uuid).getUniqueId());
            }
            WirtschaftsKonto spielerKonto = modul.getEconomySQLHandler().prüfeObSpielerKontoExistiert(UUID.fromString(uuid));
            return spielerKonto.getKontostand();
        } catch (Exception e) {
            return 0;
        }
    }

    //

    /**
     * Spieler zu Spieler | Ich sende einem Spieler
     *
     * @param sender     verliert Geld
     * @param empfaenger erhaelt Geld
     * @param Betrag
     * @implNote GESCHLOSSENER HANDEL/WIRTSCHAFTSKREISLAUF
     */
    public void GeldUeberweisen(Spieler sender, Spieler empfaenger, double Betrag) {
        try {
            if (sender.equals(empfaenger)) {
                ChatSpigot.NachrichtSenden(sender.getBukkitSpieler(), modul.bankPrefix, "Du kannst dir Geld nicht selber überweisen!");
                return;
            }

            Betrag = MathematikUT.round(Betrag, 2);

            if (kontoGedeckt(sender, Betrag) && Betrag > 0) {

                if (!empfaenger.getIstVSpieler() && empfaenger.istOnline()) {
                    modul.getEconomySQLHandler().GeldSubstrahierenCached(sender, Betrag);
                    modul.getEconomySQLHandler().GeldAddierenCached(empfaenger, Betrag);
                    try {

                        ChatSpigot.NachrichtSenden(empfaenger.getBukkitSpieler(), modul.bankPrefix, ChatColor.WHITE + "Du hast " + Betrag + " " + modul.waehrungsName + " von "
                                + ChatColor.GREEN + sender.getSpielername() + ChatColor.WHITE + " erhalten!");
                    } catch (Exception e)
                    {
                        Log.SpigotLogger.Debug(e.toString());
                    }
                }

                else if (!empfaenger.getIstVSpieler() && !empfaenger.istOnline()) {
                    modul.getEconomySQLHandler().GeldSubstrahierenCached(sender, Betrag);
                    modul.getEconomySQLHandler().GeldAddieren(empfaenger, Betrag);
                }

                ChatSpigot.NachrichtSenden(sender.getBukkitSpieler(), modul.bankPrefix, ChatColor.WHITE + "Du hast " + (Betrag) + " " + modul.waehrungsName + " an "
                        + ChatColor.GREEN + empfaenger.getSpielername() + ChatColor.WHITE + " gesendet!");

                modul.getEconomySQLHandler().LogTransaction(sender.getUuid(), empfaenger.getUuid(), "Zahlung", Betrag, "");
            }
            if (!kontoGedeckt(sender, Betrag)) {
                ChatSpigot.NachrichtSenden(sender.getBukkitSpieler(), modul.bankPrefix, "Du besitzt nicht ausreichend Geld!");
            }
            if (Betrag <= 0) {
                ChatSpigot.NachrichtSenden(sender.getBukkitSpieler(), modul.bankPrefix, "Geldwert muss mehr als 0 sein!");
            }

        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    public Boolean kontoGedeckt(Spieler kontoinhaber, Double Betrag) {
        double Kontostand = modul.getServerWirtschaft().getCachedKontostand(kontoinhaber.getUuid());
        double KontoValidierung = Kontostand - Betrag;
        Log.SpigotLogger.Debug(Kontostand + ":" + Betrag + ":" + KontoValidierung);
        return KontoValidierung >= 0;
    }

    /**
     * Spieler zu Spieler, Ich als initiator kriege Geld, der Empfaenger verliert Geld
     * Anwendung: Durch Shop
     *
     * @param sender     erhaelt Geld
     * @param empfaenger verliert Geld
     * @param Betrag Der jeweilige Geldbetrag
     * @implNote GESCHLOSSENER HANDEL/WIRTSCHAFTSKREISLAUF
     */
    public void GeldAbbuchen(Spieler sender, Spieler empfaenger, double Betrag) {
        try {
            if (sender.equals(empfaenger)) {
                ChatSpigot.NachrichtSenden(sender.getBukkitSpieler(), modul.bankPrefix, "Du kannst dir Geld nicht selber überweisen");
            }
            modul.getEconomySQLHandler().GeldSubstrahierenCached(empfaenger, Betrag);
            modul.getEconomySQLHandler().GeldAddierenCached(sender, Betrag);
            modul.getEconomySQLHandler().LogTransaction(sender.getUuid(), empfaenger.getUuid(), "Zahlung", Betrag, "");
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
            //Sender.sendMessage("Betrag konnte nicht abgebucht werden!");
        }

    }

    /**
     * Spieler zu Spieler, Ich als initiator kriege Geld, der Empfaenger verliert Geld MIT Verwendungszweck
     * Anwendung: Durch Shop
     *
     * @param sender           erhaelt Geld
     * @param empfaenger       verliert Geld
     * @param Betrag
     * @param Verwendungszweck - Grund der Transaktion
     * @implNote GESCHLOSSENER HANDEL/WIRTSCHAFTSKREISLAUF
     */
    public void GeldAbbuchen(Spieler sender, Spieler empfaenger, double Betrag, String Verwendungszweck) {
        try {
            if (sender.equals(empfaenger)) {
                ChatSpigot.NachrichtSenden(sender.getBukkitSpieler(), modul.bankPrefix, "Du kannst dir Geld nicht selber überweisen");
            }
            modul.getEconomySQLHandler().GeldSubstrahierenCached(empfaenger, Betrag);
            modul.getEconomySQLHandler().GeldAddierenCached(sender, Betrag);
            modul.getEconomySQLHandler().LogTransaction(sender.getUuid(), empfaenger.getUuid(), "Zahlung", Betrag, Verwendungszweck);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
            //Sender.sendMessage("Betrag konnte nicht abgebucht werden!");
        }

    }

    /**
     * Server ueberweist Spieler Betrag
     *
     * @param empfaenger
     * @param Betrag
     * @implNote OFFENER HANDEL/WIRTSCHAFTSKREISLAUF
     */
    public void ServerGeldUeberweisen(Spieler empfaenger, double Betrag) {
        try {
            modul.getEconomySQLHandler().GeldAddierenCached(empfaenger, Betrag);
            modul.getEconomySQLHandler().LogTransaction(WirtschaftsModul.Nationalbank.getUuid(), empfaenger.getUuid(), "Gutschrift", Betrag, "");
            ChatSpigot.NachrichtSenden(empfaenger.getBukkitSpieler(), modul.bankPrefix, "Du hast vom Server " + Betrag + " " + modul.waehrungsName + " erhalten!");

        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }

    }

    /**
     * Server-Geld ueberweisen fuer Vote-Plugin
     *
     * @param empfaenger
     * @param Betrag
     * @param Verwendungszweck
     * @implNote OFFENER HANDEL/WIRTSCHAFTSKREISLAUF
     * @see Belohnungsmodul, Vote-Plugin, Wirtschaft
     */
    public void ServerGeldUeberweisen(Spieler empfaenger, double Betrag, String Verwendungszweck) {
        try {
            modul.getEconomySQLHandler().GeldAddieren(empfaenger, Betrag);
            modul.getEconomySQLHandler().LogTransaction(WirtschaftsModul.Nationalbank.getUuid(), empfaenger.getUuid(), "Gutschrift", Betrag, Verwendungszweck);
            ChatSpigot.NachrichtSenden(empfaenger.getBukkitSpieler(), modul.bankPrefix, "Du hast von " + WirtschaftsModul.Nationalbank.getSpielername() + " " + Betrag + " " + modul.waehrungsName + " erhalten!");

        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Server-Geld ueberweisen fuer Vote-Plugin
     *
     * @param empfaenger
     * @param Betrag
     * @param Verwendungszweck
     * @implNote OFFENER HANDEL/WIRTSCHAFTSKREISLAUF
     * @see Belohnungsmodul, Vote-Plugin, Wirtschaft
     */
    public void ServerGeldUeberweisenLeise(Spieler sender, Spieler empfaenger, double Betrag, String Verwendungszweck) {
        try {
            modul.getEconomySQLHandler().GeldAddieren(empfaenger, Betrag);
            modul.getEconomySQLHandler().LogTransaction(WirtschaftsModul.Nationalbank.getUuid(), empfaenger.getUuid(), "Gutschrift", Betrag, Verwendungszweck);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Server-Geld ueberweisen innerhalb geschlossener Wirtschaft MIT Benachrichtigung des empfandenden Spielers
     *
     * @param empfaenger       - UUID des Spielers als String
     * @param Betrag
     * @param Verwendungszweck
     * @implNote GESCHLOSSENER HANDEL/WIRTSCHAFTSKREISLAUF
     */
    public void ServerGeldUeberweisenHandel(Spieler empfaenger, double Betrag, String Verwendungszweck) {
        try {
            modul.getEconomySQLHandler().GeldAddieren(empfaenger, Betrag);
            modul.getEconomySQLHandler().GeldSubstrahieren(WirtschaftsModul.Nationalbank.zuSpieler(), Betrag);
            modul.getEconomySQLHandler().LogTransaction(WirtschaftsModul.Nationalbank.getUuid(), empfaenger.getUuid(), "Server-Handel", Betrag, Verwendungszweck);
            ChatSpigot.NachrichtSenden(empfaenger.getBukkitSpieler(), modul.bankPrefix, "Du hast von " + WirtschaftsModul.Nationalbank.getSpielername() + " " + Betrag + " " + modul.waehrungsName + " erhalten!");

        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Server-Geld ueberweisen innerhalb geschlossener Wirtschaft OHNE Benachrichtigung des empfangenden Spielers
     *
     * @param empfaenger UUID des Spielers als String
     * @param Betrag
     * @param Verwendungszweck
     * @implNote GESCHLOSSENER HANDEL/WIRTSCHAFTSKREISLAUF
     */
    public void ServerGeldUeberweisenHandelLeiseCached(Spieler empfaenger, double Betrag, String Verwendungszweck) {
        try {
            modul.getEconomySQLHandler().GeldAddierenCached(empfaenger, Betrag);
            modul.getEconomySQLHandler().GeldSubstrahierenCached(WirtschaftsModul.Nationalbank.zuSpieler(), Betrag);
            //modul.getEconomySQLHandler().LogTransaction(modul.staatskasse_uuid, Empfaenger, "Server-Handel", Betrag, Verwendungszweck);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }

    }

    /**
     * Server-Geld ueberweisen innerhalb geschlossener Wirtschaft OHNE Benachrichtigung des empfandenden Spielers
     *
     * @param empfaenger UUID des Spielers als String
     * @param Betrag
     * @param Verwendungszweck
     * @implNote GESCHLOSSENER HANDEL/WIRTSCHAFTSKREISLAUF
     */
    public void ServerGeldUeberweisenHandelLeise(Spieler empfaenger, double Betrag, String Verwendungszweck) {
        try {
            modul.getEconomySQLHandler().GeldAddieren(empfaenger, Betrag);
            modul.getEconomySQLHandler().GeldSubstrahieren(WirtschaftsModul.Nationalbank.zuSpieler(), Betrag);
            //modul.getEconomySQLHandler().LogTransaction(modul.staatskasse_uuid, Empfaenger, "Server-Handel", Betrag, Verwendungszweck);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }

    }

    /**
     * Diese Methode zieht dem Spieler Serverseitig Geld ab und fuegt der Serverkasse den Betrag hinzu
     *
     * @param empfaenger
     * @param Betrag
     * @implNote GESCHLOSSENER HANDEL/WIRTSCHAFTSKREISLAUF
     */
    public void ServerGeldAbbuchen(Spieler empfaenger, double Betrag) {
        try {
            modul.getEconomySQLHandler().GeldSubstrahierenCached(empfaenger, Betrag);
            modul.getEconomySQLHandler().GeldAddierenCached(WirtschaftsModul.Nationalbank.zuSpieler(), Betrag);
            modul.getEconomySQLHandler().LogTransaction(WirtschaftsModul.Nationalbank.getUuid(), empfaenger.getUuid(), "Belastung", Betrag, "");
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }

    }

    /**
     * Diese Methode zieht dem Spieler Serverseitig Geld ab und loggt diese MIT Verwendungszweck
     *
     * @param empfaenger
     * @param Betrag
     * @implNote GESCHLOSSENER HANDEL/WIRTSCHAFTSKREISLAUF
     */
    public void ServerGeldAbbuchen(Spieler empfaenger, double Betrag, String Verwendungszweck) {
        try {
            modul.getEconomySQLHandler().GeldSubstrahieren(empfaenger, Betrag);
            modul.getEconomySQLHandler().GeldAddieren(WirtschaftsModul.Nationalbank.zuSpieler(), Betrag);
            modul.getEconomySQLHandler().LogTransaction(WirtschaftsModul.Nationalbank.getUuid(), empfaenger.getUuid(), "Belastung", Betrag, Verwendungszweck);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }

    }

    /**
     * Setzt Kontostand in der Datenbank auf definierten Wert
     *
     * @param spieler
     * @param betrag
     */
    public void GeldSetzen(Spieler spieler, double betrag) {
        try {
            modul.getEconomySQLHandler().datenSpeichern(spieler.getUuid(), betrag);
            setzeCachedSpielerKontostand(spieler.getUuid(), betrag);
            modul.getEconomySQLHandler().LogTransaction(WirtschaftsModul.Nationalbank.getUuid(), spieler.getUuid(), "Sollbuchung", betrag, "Neuer Kontostand");
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    public List<String> GeldTop10() {
        try {
            return modul.getEconomySQLHandler().getTop10();
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
            return null;
        }
    }

}
