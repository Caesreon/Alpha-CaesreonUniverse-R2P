package caesreon.universe.spigot.jupiter.modul.loginbelohnungen;

import caesreon.core.Log;
import caesreon.core.handlers.MySqlHandler;
import caesreon.core.handlers.MySqlHandler.ConnectionHandler;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.ZeitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Handler fuer die taeglichen Belohnungen
 */
public class BelohnungsHandler {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    BelohnungenModul modul;
    private ClaimData claimData;
    /**
     * Konstruktor fuer BelohnungsHandler prueft/erstellt Datenbank Verbindung
     */
    public BelohnungsHandler(BelohnungenModul modul) {
        this.modul = modul;
        initialisiereDatenbank();
    }

    /**
     * Erinnert Spieler an offene Belohnungen
     *
     * @param p Spieler, der sich eingeloggt hat
     * @see caesreon.core.listener.PlayerConnect();
     */
    public void belohnungsReminderOnLogin(Player p) {
        String[] daten = ladeDaten(p);
        if (daten != null)
            claimData = new ClaimData(daten);
        else {
            claimData = null;
        }


        if (besitztAccount(claimData, p) && !claimEingeloest(claimData, true)) {

            if (!claimEingeloest(claimData, true))
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast deine Login Belohnung noch nicht eingelöst. /belohnung fuer mehr Informationen");
        }
    }

    /**
     * Zeigt alle Monatsbelohnungen als GUI
     *
     * @param p Spieler, der Belohnungen sehen will
     */
    protected void zeigeMonatsBelohnungen(Player p) {
        //TODO Comment: Sehr schlechte Implementierung! Wenn Command ausgeloest wird, muss Liste neu geladen werden und alle Belohnungen werden initialisiert und angezeigt.
        // Dann wird die Klasse mit allen erzeugten Objekten sofort wieder geloescht
        // Besser: module.GetManager() nutzen
        BelohnungsManager b = new BelohnungsManager(modul);
        b.zeigeMonatsBelohnungGUI(p);
    }

    /**
     * Zeigt Belohnung des jeweiligen Tages in GUI
     *
     * @param p Spieler, der Belohnung sehen will
     */
    protected void zeigeTagesBelohnung(Player p) {
        claimData = new ClaimData(Objects.requireNonNull(ladeDaten(p)));
        int tag = 0;

        if (!claimEingeloest(claimData, true))
            tag = claimData.claims_monat + 1;
        else if (claimEingeloest(claimData, true))
            tag = claimData.claims_monat;
        BelohnungsManager b = new BelohnungsManager(modul);
        b.zeigeTagesBelohnungGUI(p, tag);
    }

    /**
     * Einloesung der Loginbelohnung
     *
     * @param p Spieler, der seine Belohnung einloest
     */
    protected void claimTagesBelohnung(Player p) {
        //TODO Comment: Auch hier wieder mehrfaches Laden der Daten -> besitztAccount? - ladeDaten - verarbeite(daten)
        claimData = new ClaimData(Objects.requireNonNull(ladeDaten(p)));

        if (besitztAccount(claimData, p) && !claimEingeloest(claimData, false)) {
            PreparedStatement preparedUpdateStatement;
            Connection Con = MySqlHandler.ConnectionHandler.System();
            try {
                String uuid = p.getUniqueId().toString();

                int claims = claimData.claims_monat + 1;    // aktuelle Claims + heute
                int claimsalle = claimData.claims_alle + 1;

                //TODO Comment: Besser auslagern in eigene Methode genau wie ladeDaten -> speicherDaten(QueryData)
                String updateDataSQL = BelohnungenSQL.setData;
                preparedUpdateStatement = Con.prepareStatement(updateDataSQL);
                preparedUpdateStatement.setInt(1, claims);
                preparedUpdateStatement.setInt(2, claimsalle);
                preparedUpdateStatement.setLong(3, setzeValidationFlag());
                preparedUpdateStatement.setString(4, uuid);
                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);

                // Belohnung aushaendigen
                BelohnungsManager b = new BelohnungsManager(modul);
                b.starteSpielerBelohnen(p, claims);
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast deine tägliche Belohnung erfolgreich beansprucht.");
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }

    /**
     * Einloesung der Loginbelohnung
     *
     * @param p   Spieler, der Test-Belohnung bekommt
     * @param day Tag der Belohnung
     */
    protected void claimTest(Player p, int day) {
        claimData = new ClaimData(Objects.requireNonNull(ladeDaten(p)));
        if (besitztAccount(claimData, p)) {
            try {
                int claims = claimData.claims_monat + 1;    // aktuelle Claims + heute
                int claimsalle = claimData.claims_alle + 1;

                Log.SpigotLogger.Debug(p.getUniqueId() + ":" + p.getName() + ":" + claims + ":" + claimsalle);
                modul.getBelohnungsManager().starteSpielerBelohnen(p, day);

                ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast deine tägliche Belohnung erfolgreich beansprucht.");
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }

    /**
     * Prueft, ob heute Anfang des Monats ist
     *
     * @return true, wenn heute erster Tag des Monats
     */
    private Boolean istAnfangDesMonats() {
        Log.SpigotLogger.Debug("jupiter.modul.BelohnungsHandler.istAnfangDesMonats");

        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.DAY_OF_MONTH) == 1);
    }

    /**
     * Validiert, ob Server Zeitstempel groeßer ist als der zum Spieler gespeicherte Zeitstempel
     *
     * @param data       Spieler Query Datensatz
     * @param serverComm Kommt Befehl vom Server?
     * @return true, wenn Spieler-Zeitstempel aelter als aktueller Zeitstempel
     */
    private Boolean validationFlag(ClaimData data, Boolean serverComm) {
        Log.SpigotLogger.Debug("jupiter.modul.BelohnungsHandler.validationFlag()");

        long validationTimestamp = data.validations_zeit;
        Log.SpigotLogger.Debug(String.valueOf(validationTimestamp));

        if (ZeitUtils.erhalteAktuellenZeitstempel() >= validationTimestamp) {
            Log.SpigotLogger.Debug("jupiter.modul.BelohnungsHandler.validationFlag(): valide");
            return true;
        }
        Log.SpigotLogger.Debug("jupiter.modul.BelohnungsHandler.validationFlag(): abgelehnt");

        if (!serverComm)
            ChatSpigot.NachrichtSenden(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(data.uuid))), modul.Prefix, "Tägliche Belohnung wurde bereits eingelöst!");

        return false;
    }

    /**
     * Setzt den neuen Zeitstempel
     *
     * @return Zeitstempel fuer 0 Uhr naechster Tag
     */
    private long setzeValidationFlag() {
        // Zum aktuellen Datum einen Tag hinzufuegen und Zeitstempel zurueckgeben
        Date aktuellesDatum = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(aktuellesDatum);
        Log.SpigotLogger.Debug(dateFormat.format(aktuellesDatum));
        c.add(Calendar.DATE, 1);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        //Neues errechnetes Validierungs Datum
        Date validierungsDatum = c.getTime();
        Log.SpigotLogger.Debug(dateFormat.format(validierungsDatum));
        return ZeitUtils.erhalteZeitstempelVonDatum(validierungsDatum);
    }

    /**
     * Prueft, ob Belohnungs-Claim fuer heute schon eingeloest wurde
     *
     * @param data            Datensatz des Spieler, dessen Claim geprueft werden soll
     * @param istServerSender Kommt Befehl vom Server?
     * @return true, wenn Belohnung noch nicht eingeloest wurde
     */
    private boolean claimEingeloest(ClaimData data, Boolean istServerSender) {
        return !validationFlag(data, istServerSender);
    }

    /**
     * Prueft, ob Spieler einen Belohnungs-Account hat und legt diesen ggf. an
     *
     * @param p Spieler, der vielleicht keinen Account hat
     * @return true, wenn Spieler einen Account hat
     */
    private boolean besitztAccount(ClaimData data, Player p) {
        Log.SpigotLogger.Debug("jupiter.modul.BelohnungsHandler.besitztAccount()");

        //TODO Comment: Das ist zu aufwaendig, um nur zu testen, ob Spieler einen Account hat
        // Hier gibt es 2 moegliche Ansaetze:
        // #1 besitztAccount liefert auch gleich den Account zurueck (in den meisten Faellen wird eh geprueft, ob Spieler einen Account hat und dann wird er geladen)
        // #2 simplere Abfrage an der Datenbank: SELECT uuid FROM <Table> WHERE uuid = xyz
        // Da kein JOIN verwendet wird, ist Ansatz 1 wohl sinnvoller.

        if (data != null) {
            Log.SpigotLogger.Debug("jupiter.modul.BelohnungsHandler.besitztAccount(): true");
            return true;
        } else {
            Log.SpigotLogger.Debug("jupiter.modul.BelohnungsHandler.besitztAccount(): false");
            erstelleAccount(p);
        }
        return true;
    }

    /**
     * Erstellt einen Belohnungs-Account in der Datenbank
     *
     * @param p Spieler mit neuem Account
     */
    private void erstelleAccount(Player p) {
        Log.SpigotLogger.Debug("jupiter.modul.BelohnungsHandler.erstelleAccount()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = MySqlHandler.ConnectionHandler.System();
        try {
            String uuid = p.getUniqueId().toString();
            String spielername = p.getName();
            String sql = BelohnungenSQL.erstelleAccount;

            preparedUpdateStatement = Con.prepareStatement(sql);
            preparedUpdateStatement.setString(1, uuid);
            preparedUpdateStatement.setString(2, spielername);
            preparedUpdateStatement.setInt(3, 0);
            preparedUpdateStatement.setInt(4, 0);
            preparedUpdateStatement.setLong(5, 0);
            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);

            //TODO Comment: Hier waere es sinnvoll, wenn gleich QueryData erstellt und zurueckgegeben wird. Die Daten dafuer sind bekannt (default).
            // Dadurch reduziert sich die Anzahl der Abfragen am Server. Wenn der Account nicht relevant ist, kann der Rueckgabewert ignoriert werden

            //Umgesetzt und Daten innerhalb der Klasse reinitialisiert und bekanntgegeben. Heißt egal wie, die nachfolgende Methode arbeitet mit dem richtigen Datensatz ohne von
            //der Datenbank neu abfragen zu muessen
            String[] DataSet = {uuid, spielername, "0", "0", "0"};
            claimData = new ClaimData(DataSet);
            //QueryData = DataSet;
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Laedt Belohnungs-Daten aus Datenbank
     *
     * @param p Spieler, fuer den die Daten geladen werden sollen
     * @return uuid, claims_monat, claims_alle, validations_zeit
     */
    private String[] ladeDaten(Player p) {
        try {
            PreparedStatement preparedStatement;
            ResultSet result;
            Connection Con = MySqlHandler.ConnectionHandler.System();
            String[] data = null;
            String sql = BelohnungenSQL.getData;
            Log.SpigotLogger.Debug(sql);
            preparedStatement = Con.prepareStatement(sql);
            preparedStatement.setString(1, p.getUniqueId().toString());

            result = MySqlHandler.executeQueryAndGetResult(preparedStatement);
            assert result != null;
            if (result.next()) {
                data = new String[]{
                        String.valueOf(result.getString("uuid")),
                        String.valueOf(result.getInt("claims_monat")),
                        String.valueOf(result.getInt("claims_alle")),
                        String.valueOf(result.getLong("validations_zeit"))};
                //TODO Comment: Hier besser gleich das ClaimData Objekt erstellen
            }

            MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
            ConnectionHandler.closeConnection(Con);
            return data;
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
        return null;
    }

    /**
     * Prueft die Tabellen der Datenbank und setzt am Monatsanfang alle monatlichen Claims auf 0
     */
    private void initialisiereDatenbank() {
        Log.SpigotLogger.Debug("jupiter.modul.BelohnungsHandler.initialisiereDatenbank()");

        pruefeTabelle(ConnectionHandler.System());
        if (istAnfangDesMonats()) {
            PreparedStatement preparedUpdateStatement;
            try {
                if (Objects.equals(modul.getBelohnungsConfig().getString("reset"), "true")) {
                    String updateMonthClaimsSQL = BelohnungenSQL.setClaimsMonat;
                    modul.getBelohnungsConfig().set("reset", "false");
                    preparedUpdateStatement = ConnectionHandler.System().prepareStatement(updateMonthClaimsSQL);
                    preparedUpdateStatement.setInt(1, 0);    // Setze alle monatlichen Claims auf 0
                    MySqlHandler.executePreparedStatementAndCloseConnection(ConnectionHandler.System(), preparedUpdateStatement);
                }
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }

    /**
     * Prueft die Tabellen fuer das Belohnungs-System
     *
     * @param con Verbindung zur Datenbank
     */
    private void pruefeTabelle(Connection con) {
        Log.SpigotLogger.Debug("jupiter.modul.BelohnungsHandler.pruefeTabelle()");
        MySqlHandler.pruefeObTabelleExistiert(con, BelohnungenSQL.erstelleTabelle);
        ConnectionHandler.closeConnection(con);
    }

    //TODO Comment:
    // Besser eine Subklasse (Inner class) erstellen. Dann sind auch Methoden wie getAnzahlLoginClaims() unnoetig
    // Noch besser waere ein DAO (Java Data Access Object), das die Daten und DB Zugriffe (laden, speichern, pruefen + Strings) buendelt
    private class ClaimData {
        public String uuid;
        public int claims_monat;
        public int claims_alle;
        public long validations_zeit;

        ClaimData(String[] data) {
            for (String s : data) {
                Log.SpigotLogger.Debug(s);
            }
            uuid = data[0];
            claims_monat = Integer.parseInt(data[1]);
            claims_alle = Integer.parseInt(data[2]);
            validations_zeit = Long.parseLong(data[3]);
        }
    }

}
