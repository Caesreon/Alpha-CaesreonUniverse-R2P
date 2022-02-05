package caesreon.universe.spigot.merkur.modul.wirtschaft.handler;

//import java.math.BigDecimal;
//import java.sql.Connection;
//pruefen welche Conn funktioniert, vermutlich die JDBC

import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.handlers.MySqlHandler;
import caesreon.core.handlers.MySqlHandler.ConnectionHandler;
import caesreon.core.skynet.SpielerLookup;
import caesreon.universe.spigot.merkur.modul.wirtschaft.Wirtschaft;
import caesreon.universe.spigot.merkur.modul.wirtschaft.WirtschaftsKonto;
import caesreon.universe.spigot.merkur.modul.wirtschaft.WirtschaftsModul;
import caesreon.universe.spigot.merkur.modul.wirtschaft.WirtschaftsSQLStatements;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


/*
 * Falls JDBC Import falsch ist, das ganze auf Java.sql imports umstellen
 */

public class WirtschaftsHandler {
    public static List<UUID> spielerMitFehlerhaftenKonten = new ArrayList<>();
    public static Set<UUID> spielerModifizierteKonten = new HashSet<>();
    private final WirtschaftsModul modul;

    public WirtschaftsHandler(WirtschaftsModul modul) {
        this.modul = modul;
        initialisiereDatenbank();
        serverStartUp();
    }

    public void serverStartUp() {

    }

    public void serverShutdownSpeicher() {
        Log.SpigotLogger.Info("Server-Wirtschaft: Speicher Online Spieler Daten...");
        try {
            List<Player> spielerOnline = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (Player p : spielerOnline) {
                if (p.isOnline())
                    datenSpeichern(p.getUniqueId(), Wirtschaft.lokaleSpielerKontoDaten.get(p.getUniqueId()));
            }
            Log.SpigotLogger.Info("merkur.wirtschaft.handler.EconomyDataHandler: Daten wurden erfolgreich bei " + spielerOnline.size() + " Online Spieler(n) gespeichert.");
        } catch (Exception ex) {
            Log.SpigotLogger.Debug(ex.toString());
        }
        for (UUID spieler : spielerModifizierteKonten) {
            datenSpeichern(spieler, modul.getServerWirtschaft().getCachedKontostand(spieler));
        }
        Log.SpigotLogger.Info("merkur.wirtschaft.handler.EconomyDataHandler: Daten wurden erfolgreich bei " + spielerModifizierteKonten.size() + " Offline Spieler(n) gespeichert.");
    }

    /**
     * Wird genutzt von onJoin Funktionen und basiert auf ONLINE Spielern und ist nicht kompatibel mit vSpielern welche nur innerhalb
     * dieses Plugins "existieren"
     *
     * @param p - der Spieler welcher den Server betritt
     */
    public void spielerBeitritt(Player p) {
        Log.SpigotLogger.Debug("spielerBeitritt(Player p)");
        WirtschaftsKonto konto = prüfeObSpielerKontoExistiert(p.getUniqueId());
        double kontostand = konto.getKontostand();
        Log.SpigotLogger.Debug("spielerBeitritt(Player p).balance= " + kontostand);
        Wirtschaft.lokaleSpielerKontoDaten.put(konto.getUuid(), konto.getKontostand());
    }

    /**
     * Pr�ft ob Spielerkonto ein korrumpierter Datensatz ist und speichert diesen nicht
     *
     * @param p - der Spieler welcher den Server verlässt
     */
    public void spielerDisconnect(Player p) {
        Log.SpigotLogger.Debug("Merkur.Wirtschaft.WirtschaftHandler.spielerDisconnect()");
        if (!spielerMitFehlerhaftenKonten.contains(p.getUniqueId()))
            Log.SpigotLogger.Debug(Wirtschaft.lokaleSpielerKontoDaten.get(p.getUniqueId()).toString());
        spielerModifizierteKonten.remove(p.getUniqueId());
        datenSpeichern(p.getUniqueId(), Wirtschaft.lokaleSpielerKontoDaten.get(p.getUniqueId()));
        datenBereinigung(p);
    }

    private void datenBereinigung(Player p) {
        Runnable datenBereinigung = () -> Wirtschaft.lokaleSpielerKontoDaten.remove(p.getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(modul.getMain(), datenBereinigung, 20L);
    }

    /**
     * Speichert Spielerdaten
     * @param uuid Die UUID des jeweiligen Spielers
     * @param kontoStand Der Kontostand des jeweiligen Spielers
     */
    public void datenSpeichern(UUID uuid, Double kontoStand) {
        Log.SpigotLogger.Verbose("core.economy.handler.EconomyMSQLHandler.datenSpeichern()");
        String spielername = SpielerLookup.SchliesseConnection.getSpielername(uuid);
        Connection Con = ConnectionHandler.EconomyBridge();
        PreparedStatement preparedUpdateStatement;
        if (Con == null) {
            Log.SpigotLogger.Debug("core.economy.handler.EconomyMSQLHandler.setData() Conn war null");
        }
        if (Con != null) {
            try {
                String data = modul.getEconomyBridge().speichereKontostand();
                Log.SpigotLogger.Debug(data);
                Log.SpigotLogger.Debug(spielername);
                Log.SpigotLogger.Debug(kontoStand.toString());
                Log.SpigotLogger.Debug(uuid.toString());
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, spielername);
                preparedUpdateStatement.setDouble(2, kontoStand);
                preparedUpdateStatement.setString(3, uuid.toString());
                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }

    public void erstelleAccount(Player player) {
        Log.SpigotLogger.Debug("erstelleAccount() fuer ONLINE Spieler");
        Connection Con = ConnectionHandler.EconomyBridge();
        PreparedStatement preparedStatement;
        if (Con != null) {
            try {
                String sql = modul.getEconomyBridge().erstelleAccountSQLCommand();
                preparedStatement = Con.prepareStatement(sql);
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, player.getName());
                preparedStatement.setDouble(3, 0.0);
                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedStatement);
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }

    /**
     * @param uuid UUID des jeweiligen Spielers
     * @param Name DisplayName des jeweiligen Spielers
     * @param Kontostand Neuer Kontostand des jeweiligen Spielers
     */
    private void erstelleAccount(UUID uuid, String Name, Double Kontostand) {
        Log.SpigotLogger.Debug("erstelleAccount().");
        Connection Con = ConnectionHandler.EconomyBridge();
        PreparedStatement preparedStatement;
        if (Con != null) {
            try {
                String sql = modul.getEconomyBridge().erstelleAccountSQLCommand();
                preparedStatement = Con.prepareStatement(sql);
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, Name);
                preparedStatement.setDouble(3, Kontostand);
                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedStatement);
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
                Log.SpigotLogger.Warning("Erstellung Wirtschaftsaccount fehlgeschlagen");
            }
        }
    }

    public OptionalDouble ladeKontostandAusDatenbank(UUID uuid) {
        Log.SpigotLogger.Debug("merkur.wirtschaft.handler.EconomyMSQLHandler.ladeKontostandAusDatenbank()");
        Connection Con = ConnectionHandler.EconomyBridge();
        PreparedStatement preparedUpdateStatement;
        ResultSet result;

        if (Con != null) {
            try {
                String sql = modul.getEconomyBridge().getPlayerDataSQLCommand();
                Log.SpigotLogger.Debug(sql);
                preparedUpdateStatement = Con.prepareStatement(sql);
                preparedUpdateStatement.setString(1, uuid.toString());
                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                assert result != null;
                if (result.next()) {
                    return OptionalDouble.of(result.getDouble("kontostand"));
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);
                return OptionalDouble.empty();
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
        if (Con == null) {
            Log.SpigotLogger.Debug("Connection war Null");
        }
        return OptionalDouble.empty();
    }

    /**
     * Diese Methode prüft, ob ein Spieler Wirtschaftskonto vorhanden ist und gibt dieses der aufrufenden Methode zurück.
     * Ist kein Konto vorhanden, so wird ein neues angelegt.
     * Gelingt dies nicht, wird dies in der Console geloggt und der Spieler über den Fehler informiert
     *
     * @param uuid Die UUID des jeweiligen Spielers
     * @return WirtschaftsKonto, beinhaltend Kontostand und Eigentümer
     */
    public WirtschaftsKonto prüfeObSpielerKontoExistiert(UUID uuid) {
        OptionalDouble kontostand_temp;
        double kontostand_final;
        WirtschaftsKonto konto = null;
        try {
            /*Hier soll getestet werden ob Spieler bereits ein Konto mit einem dazugehörigen Kontostand hat. Wenn ja,
             * soll dieser als Wirtschaftskonto zurückgegeben werden
             */
            Log.SpigotLogger.Debug("pruefeObSpielerKontoExistiert(UUID uuid)");
            kontostand_temp = ladeKontostandAusDatenbank(uuid);
            if (!kontostand_temp.isEmpty())
            {
                kontostand_final = kontostand_temp.getAsDouble();
                Log.SpigotLogger.Debug("prüfeObSpielerKontoExistiert(UUID uuid).balance= " + kontostand_final);
            } else {
                //Wenn Konto nicht vorhanden war, erstelle neues Konto und gebe dieses zurück
                kontostand_final = 0.0;
                erstelleAccount(uuid, SpielerLookup.SchliesseConnection.getSpielername(uuid), kontostand_final);
                Log.SpigotLogger.Debug("pruefeObSpielerKontoExistiert(UUID uuid).erstelle neuen Account für=" + uuid);
            }
            return new WirtschaftsKonto(uuid, kontostand_final, true);

        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
            //Spielerkonto war nicht vorhanden, erstelle in Try einen neuen
            Log.SpigotLogger.Warning("Fehler beim Laden eines Wirtschaftskontos");
            Log.SpigotLogger.Warning(e.toString());
            kontostand_final = 0;
            konto = new WirtschaftsKonto(uuid, kontostand_final, false);
            spielerMitFehlerhaftenKonten.add(uuid);
            return konto;
        }
    }

    /**
     * TODO: DSGVO-Relevant
     */
    public void spielerDatenLoeschen() {

    }

    private void initialisiereDatenbank() {
        Log.SpigotLogger.Debug("core.economy.handler.EconomyMSQLHandler.initialisiereDatenbank(): Initialisiere Datenbank in EconomyMySQLHandler");
        Connection conn = ConnectionHandler.EconomyBridge();
        //pruefeDatenbank(conn);
        pruefeTabelle(conn);
        //Verbindung wird an anderer Stelle geschlossen
    }

    private void pruefeTabelle(Connection Con) {
        MySqlHandler.pruefeObTabelleExistiert(Con, modul.getEconomyBridge().erstelleTabelleSQLCommand());
        MySqlHandler.pruefeObTabelleExistiert(Con, WirtschaftsSQLStatements.erstelleTabelleZahlungsLog);
        ConnectionHandler.closeConnection(Con);
    }

    /**
     * Mittel Layer - Geld addieren Logik, Vorbereitung fuer Bottom Layer SQL Verarbeitung
     *
     * @param spieler Der Spieler welcher Geld erhalten soll
     * @param Geld Der zu addierende Betrag
     */
    public void GeldAddieren(Spieler spieler, Double Geld) {
        Log.SpigotLogger.Debug("core.economy.handler.EconomyMSQLHandler.GeldAddieren()");
        Connection Con = ConnectionHandler.EconomyBridge();
        PreparedStatement preparedStatement;
        if (Con == null) {
            Log.SpigotLogger.Debug("GeldAddieren() Conn war null");
        }
        if (Con != null) {
            try {
                String sql = modul.getEconomyBridge().GeldAddieren();
                Log.SpigotLogger.Debug(sql);
                preparedStatement = Con.prepareStatement(sql);
                preparedStatement.setDouble(1, Geld);
                preparedStatement.setString(2, spieler.getUuid().toString());
                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedStatement);

                double alterKontostand = modul.getServerWirtschaft().getCachedKontostand(spieler.getUuid());
                Log.SpigotLogger.Verbose("alt_kto=" + alterKontostand + " betrag=" + Geld);
                setzeCachedKontoDaten(spieler.getUuid(), alterKontostand + Geld);
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }

    private void updateModifizierteKonten(UUID uuid)
    {
        spielerModifizierteKonten.remove(uuid);
        spielerModifizierteKonten.add(uuid);
    }

    /**
     * Mittel Layer - Geld addieren Logik fuer Hashmap
     *
     * @param spieler Der Spieler welcher Geld erhalten soll
     * @param Geld Der zu addierende Betrag
     */
    public void GeldAddierenCached(Spieler spieler, Double Geld) {
        Log.SpigotLogger.Debug("core.economy.handler.EconomyMSQLHandler.GeldAddieren()");
        double alterKontostand = modul.getServerWirtschaft().getCachedKontostand(spieler.getUuid());
        Log.SpigotLogger.Debug(alterKontostand + " + " + Geld);
        Log.SpigotLogger.Debug(alterKontostand + Geld);
        setzeCachedKontoDaten(spieler.getUuid(), alterKontostand + Geld);
        updateModifizierteKonten(spieler.getUuid());

    }

    /**
     * Mittel Layer - Geld Subtrahieren Logik, vorbereitung fuer Bottom Layer SQL Verarbeitung
     *
     * @param spieler Der Spieler welcher Geld verlieren soll
     * @param Geld Der zu subtrahierende Betrag
     */
    public void GeldSubstrahieren(Spieler spieler, Double Geld) {
        Log.SpigotLogger.Debug("core.economy.handler.EconomyMSQLHandler.GeldAbbuchen()");
        Connection Con = ConnectionHandler.EconomyBridge();
        PreparedStatement preparedStatement;
        if (Con == null) {
            Log.SpigotLogger.Debug("GeldAbbuchen() Conn war null");
        }
        if (Con != null) {
            try {
                String sql = modul.getEconomyBridge().GeldSubtrahierenSQLCommand();
                Log.SpigotLogger.Debug(sql);
                preparedStatement = Con.prepareStatement(sql);
                preparedStatement.setDouble(1, Geld);
                preparedStatement.setString(2, spieler.getUuid().toString());
                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedStatement);

                double alterKontostand = modul.getServerWirtschaft().getCachedKontostand(spieler.getUuid());
                setzeCachedKontoDaten(spieler.getUuid(), alterKontostand - Geld);
            } catch (Exception e) {
                //p.sendMessage("Handelspartner hat nicht genuegend Geld!");
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }

    /**
     * Geld Subtrahieren Logik f�r Hashmap
     *
     * @param spieler
     * @param Geld Der zu subtrahierende Betrag
     */
    public void GeldSubstrahierenCached(Spieler spieler, Double Geld) {
        Log.SpigotLogger.Debug("core.economy.handler.EconomyMSQLHandler.GeldAbbuchen()");
        double alterKontostand = modul.getServerWirtschaft().getCachedKontostand(spieler.getUuid());
        setzeCachedKontoDaten(spieler.getUuid(), alterKontostand - Geld);
        updateModifizierteKonten(spieler.getUuid());
    }


    private void setzeCachedKontoDaten(UUID uuid, Double Geld) {
        if (!spielerMitFehlerhaftenKonten.contains(uuid)) {
            Wirtschaft.entferneCaechedSpielerKontostand(uuid);
            Wirtschaft.setzeCachedSpielerKontostand(uuid, Geld);
        }
    }

    public List<String> getTop10() {
        Log.SpigotLogger.Debug("core.economy.handler.EconomyMSQLHandler.getTop10()");
        Connection Con = ConnectionHandler.EconomyBridge();
        PreparedStatement preparedUpdateStatement ;
        ResultSet result;

        if (Con != null) {
            try {
                String sql = modul.getEconomyBridge().Top10SQLCommand();
                Log.SpigotLogger.Debug(sql);
                preparedUpdateStatement = Con.prepareStatement(sql);
                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                List<String> Ergebnis = new ArrayList<>();
                while (true) {
                    assert result != null;
                    if (!result.next()) break;
                    double d = (Double) result.getObject("kontostand");
                    String data = ChatColor.GREEN + result.getString("spielername") + ": " + ChatColor.WHITE + d;
                    Ergebnis.add(data);
                    Log.SpigotLogger.Debug(data);
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);
                return Ergebnis;
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
        if (Con == null) {
            Log.SpigotLogger.Debug("Connection war Null");
        }
        return null;
    }

    /**
     * Loggt den jeweiligen Zahlungsvorgang
     *
     * @param sender_uuid Spieler oder vSpieler, von welchem eine initiierende Transaktion ausgeht
     * @param empfaenger_uuid Spieler oder vSpieler, welcher eine Transaktion erhält
     * @param Buchung Art der Buchung, möglich ist hier beispielweise Gutschrift, Belastung oder Zahlung
     * @param Betrag Der Ingame Geld Betrag, welcher in der Transaktion definiert wurde
     * @param Verwendungszweck Ein Hinweis, warum eine Transaktion durchgeführt wurde, beispielweise Vote-Belohnung, Login-Belohnung oder Überweisung
     */
    public void LogTransaction(final UUID sender_uuid, final UUID empfaenger_uuid, final String Buchung, final Double Betrag, final String Verwendungszweck) {
        Log.SpigotLogger.Debug("core.economy.handler.EconomyMSQLHandler.LogTransaction()");

        BukkitScheduler scheduler = modul.getMain().getServer().getScheduler();
        scheduler.runTaskLaterAsynchronously(modul.getMain(), () -> {
            Log.SpigotLogger.Debug("core.economy.handler.EconomyMSQLHandler.LogTransaction(): runnable");
            Connection Con = ConnectionHandler.EconomyBridge();
            PreparedStatement preparedStatement;
            if (Con == null) {
                Log.SpigotLogger.Debug("LogTransaction() Conn war null");
            }
            if (Con != null) {
                try {
                    Calendar c = Calendar.getInstance();
                    Date aktuellesDatum = new Date();
                    c.setTime(aktuellesDatum);
                    int zeitstempel = (int) (aktuellesDatum.getTime() / 1000);

                    String sql = WirtschaftsSQLStatements.logZahlung;
                    double kto_sender = modul.getServerWirtschaft().getCachedKontostand(sender_uuid);
                    Log.SpigotLogger.Debug(empfaenger_uuid.toString());
                    double kto_empfaenger = modul.getServerWirtschaft().Kontostand(empfaenger_uuid);
                    Log.SpigotLogger.Debug(kto_empfaenger);

                    Log.SpigotLogger.Debug(sql);
                    preparedStatement = Con.prepareStatement(sql);
                    preparedStatement.setString(1, sender_uuid.toString());
                    preparedStatement.setString(2, empfaenger_uuid.toString());
                    preparedStatement.setString(3, Buchung);
                    preparedStatement.setDouble(4, Betrag);
                    preparedStatement.setString(5, Verwendungszweck);
                    preparedStatement.setInt(6, zeitstempel);
                    preparedStatement.setDouble(7, kto_sender);
                    preparedStatement.setDouble(8, kto_empfaenger);
                    MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedStatement);
                } catch (Exception e) {
                    Log.SpigotLogger.Debug(e.toString());
                }
            }
        }, 60L);
    }
}