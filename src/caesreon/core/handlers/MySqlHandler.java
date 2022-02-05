package caesreon.core.handlers;

import caesreon.core.CoreKomponente;
import caesreon.core.Log;
import caesreon.universe.spigot.merkur.modul.wirtschaft.WirtschaftsModul;

import java.sql.*;
import java.util.Properties;

public class MySqlHandler {
    private static DatenbankVerbindungsSatz wirtschaftDatenbankVerbindungsSatz;
    private static DatenbankVerbindungsSatz systemDatenbankVerbindungsSatz;

    public MySqlHandler() {
        Log.JavaLogger.Debug("onInit(): MySqlHandler()");
        systemDatenbankVerbindungsSatz = initialisiereSystemDatenbankVerbindungsSatz();
        wirtschaftDatenbankVerbindungsSatz = initialisiereWirtschaftDatenbankVerbindungsSatz();
    }

    public static Properties setProperties(String Username, String Passwort, String autoReconnect, String verifyServerCertificate) {
        Properties properties = new Properties();
        properties.setProperty("username", Username);
        properties.setProperty("password", Passwort);
        properties.setProperty("autoReconnect", autoReconnect);
        properties.setProperty("verifyServerCertificate", verifyServerCertificate);
        return properties;
    }

    /**
     * Fuehrt Prepared Statement aus, schliesst das Statement und schliesst final die Connection
     *
     * @param Con - die Verbindung welche genutzt werden soll
     * @param S   - Das Prepared Statement welches ausgefuehrt werden soll
     * @implNote Con.close() ist hier elementar da ansonsten "too many connections" exception getriggert wird
     */
    public static void executePreparedStatementAndCloseConnection(Connection Con, PreparedStatement S) {
        try {
            Log.JavaLogger.Debug("executePreparedStatementAndCloseConnection(): Führe Statement aus");
            S.executeUpdate();
        } catch (SQLException e) {
            Log.JavaLogger.Warning("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (S != null) {
                    Log.JavaLogger.Debug("executePreparedStatementAndCloseConnection(): Schliesse Query");
                    S.close();
                }
                if (Con != null)
                {
                    Con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param S
     * @return
     * @implNote Connections dürfen nicht hier geclosed werden sondern ausschliesslich von der aufrufenden Instanz!
     */
    public static ResultSet executeQueryAndGetResult(PreparedStatement S) {
        Log.JavaLogger.Verbose("caesreon.core.handlers.executeQueryAndGetResult()");
        ResultSet result;
        try {
            Log.JavaLogger.Verbose("caesreon.core.handlers.executeQueryAndGetResult(): ");
            result = S.executeQuery();
            Log.JavaLogger.Verbose("caesreon.core.handlers.executeQueryAndGetResult(): Erfolgreich");
            return result;
        } catch (SQLException e) {
            Log.JavaLogger.Warning("Error: " + e.getMessage());
            e.printStackTrace();
        }
        Log.JavaLogger.Debug("caesreon.core.handlers.executeQueryAndGetResult(): Nicht Erfolgreich");
        return null;
    }

    /**
     * Schliesst das Prepared Statement, nicht die Connection
     *
     * @param rs
     * @param s
     */
    public static void schliesseResultUndPreparedStatement(ResultSet rs, PreparedStatement s) {
        try {
            if (rs != null)
                rs.close();
            if (s != null)
                Log.JavaLogger.Verbose("ClosepreparedStatement.close()");
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pruefeObDatenbankExistiert(Connection Con, String SQL) {
        if (Con == null) {
            Log.JavaLogger.Debug("Con war null");
            return;
        }
        PreparedStatement query = null;
        try {
            query = Con.prepareStatement(SQL);
            query.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.JavaLogger.Error("core.economy.handlers.MySqlHandler.pruefeObDatenbankExistiert(): Fehler beim erstellen der Datenbank! Error: " + e.getMessage());
        } finally {
            try {
                if (query != null) {
                    Log.JavaLogger.Verbose("core.economy.handlers.MySqlHandler.pruefeObDatenbankExistiert():  Schliesse Query");
                    query.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO: Refaktorisieren in createIfNotExists() und pruefeObTabelleExistiert als neue Methode, evtl Boolean mit DatabaseMetaData
     *
     * @param Con
     * @param SQL
     * @implNote Setzt i, SQL Statement ein CREATE IF NOT EXISTS Statement vorrausetzt
     */
    public static void pruefeObTabelleExistiert(Connection Con, String SQL) {
        if (Con == null) {
            return;
        }
        PreparedStatement query = null;
        try {
            query = Con.prepareStatement(SQL);
            query.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            Log.JavaLogger.Error("core.economy.handlers.MySqlHandler.pruefeObTableEistiert(): Fehler beim erstellen der Tabelle! Error: " + e.getMessage());
        } finally {
            try {
                if (query != null) {
                    Log.JavaLogger.Debug("core.economy.handlers.MySqlHandler.pruefeObTableEistiert(): Schliesse Query");
                    query.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void UpdateTables(Connection Con, String Catalog, String schemaPattern, String tableNamePattern, String ColumnNamePattern, String SQL) {
        Log.JavaLogger.Debug("updateTables()");
        if (Con == null) {
            return;
        }
        DatabaseMetaData md;
        ResultSet rs1 = null;
        PreparedStatement query1 = null;
        try {
            md = Con.getMetaData();
            rs1 = md.getColumns(Catalog, schemaPattern, tableNamePattern, ColumnNamePattern);

            if (!rs1.next()) {
                Log.JavaLogger.Debug("core.handlers.MSQLHandler.updateTables()!rs1.next():" + SQL);
                query1 = Con.prepareStatement(SQL);
                query1.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (query1 != null)
                    query1.close();
                if (rs1 != null)
                    rs1.close();
                Con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private DatenbankVerbindungsSatz initialisiereWirtschaftDatenbankVerbindungsSatz() {
        try {
            Log.JavaLogger.Debug("onInit(): MySqlHandler().new wirtschaftDatenbankVerbindungsSatz");
            return new DatenbankVerbindungsSatz(
                    WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.server-address"),
                    WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.username"),
                    WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.passwort"),
                    WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.database"),
                    WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.port"));

        } catch (Exception e) {
            if (WirtschaftsModul.WirtschaftsKonfiguration == null)
                Log.JavaLogger.Debug("onInit(): MySqlHandler(): MerkurKomponente.wirtschaftsKonfiguration == NPE");
            Log.JavaLogger.Debug(e.toString());
        }
        return null;
    }

    private DatenbankVerbindungsSatz initialisiereSystemDatenbankVerbindungsSatz() {
        try {
            Log.JavaLogger.Debug("onInit(): MySqlHandler().new systemDatenbankVerbindungsSatz");
            return new DatenbankVerbindungsSatz(CoreKomponente.getDefaultConfig().getString("mysql.server-address"),
                    CoreKomponente.getDefaultConfig().getString("mysql.username"),
                    CoreKomponente.getDefaultConfig().getString("mysql.passwort"),
                    CoreKomponente.getDefaultConfig().getString("mysql.database"),
                    CoreKomponente.getDefaultConfig().getString("mysql.port"));
        } catch (Exception e) {
            if (CoreKomponente.getDefaultConfig() == null)
                Log.JavaLogger.Debug("onInit(): MySqlHandler(): CoreKomponente.standardKonfiguration == NPE");
            Log.JavaLogger.Debug(e.toString());
        }
        return null;
    }

    /**
     * Diese Klasse enthaelt eine Sammlung aller gaengigen JDBC Driver URL Connection Strings.
     *
     * @author Coriolanus_S
     */
    public static class JDBC_Driver_URLS {
        public static final String JDBC_DRIVER_5 = "com.mysql.jdbc.Driver";
        public static final String JDBC_DRIVER_8 = "com.mysql.cj.jdbc.Driver";  //Funktioniert nicht, ClassNotFoundException
        public static final String MySQLDB_URL = "jdbc:mysql://";
        public static final String MariaDB_URL = "jdbc:mariadb://";
    }

    /**
     * Diese Klasse vereint alle wichtigen Daten, welche fuer eine erfolgreiche Datenbankverbindung benoetigt werden.
     * Ein wesentlicher Vorteil dieser Architektur ist es, dass verschiedene Datenbankentypen und Datenbankadressen gleichzeitig waehrend der Runtime
     * adressiert werden koennen.
     *
     * @author Coriolanus_S
     */
    static class DatenbankVerbindungsSatz {
        private final String host;
        private final String user;
        private final String pass;
        private final String db;
        private final String port;
        public String connectionString;
        private Connection verbindung;

        /**
         * Diese Klasse vereint alle wichtigen Daten, welche fuer eine erfolgreiche Datenbankverbindung benoetigt werden.
         *
         * @param Host - IP Adresse, unter welchem der jeweilige Datenbank-Dienst betrieben wird
         * @param User - Datenbank-Nutzer welcher sich in die Datenbank einloggt
         * @param Pass - Passwort fuer Datenbank-Nutzer
         * @param DB   - Datenbank-Name, zu welcher eine Verbindung aufgebaut werden soll
         * @param Port - Port unter welchem die Datenbank betrieben wird
         */
        public DatenbankVerbindungsSatz(String Host, String User, String Pass, String DB, String Port) {
            this.host = Host;
            this.user = User;
            this.pass = Pass;
            this.db = DB;
            this.port = Port;
            connectionString = JDBC_Driver_URLS.MySQLDB_URL + host + ":" + port + "/" + db;
            Log.JavaLogger.Debug(connectionString + " + Creds: " + "User: " + user + " Passwort" + pass);
            verbindung = ConnectionHandler.Connector(this);
        }

        public Connection getConnection() {
            return verbindung;
        }

        public void setConnection(Connection verbindung) {
            this.verbindung = verbindung;
        }
    }

    /**
     * Connection Handler mit statischem Zugriff auf Datenbank Connection der Wirtschaft und des
     * restlichen System.
     *
     * @author Coriolanus_S
     * @implNote Da das Plugin mit der Zeit gewachsen ist, haben wir hier noch das duale Datenbank-System
     * operativ im Betrieb. Zukuenftig wird allerdings die Wirtschafts Datenbank in die "System"-Datenbank
     * fusioniert.
     */
    public static class ConnectionHandler {

        public static Connection EconomyBridge() {
            Log.JavaLogger.Verbose("core.handlers.MySqlHandler.ConnectionHandler.EconomyBridge()");
            if (wirtschaftDatenbankVerbindungsSatz == null) {
                Log.JavaLogger.Debug("MySqlHandler().EconomyBridge().wirtschaftDatenbankVerbindungsSatz war NULL, versuche VerbindungsSatz neu zu initialisieren");
                wirtschaftDatenbankVerbindungsSatz = new MySqlHandler().initialisiereWirtschaftDatenbankVerbindungsSatz();
            }
            return pruefeConnection(wirtschaftDatenbankVerbindungsSatz);
        }

        public static Connection System() {
            Log.JavaLogger.Verbose("core.handlers.MySqlHandler.ConnectionHandler.System()");
            if (systemDatenbankVerbindungsSatz == null) {
                Log.JavaLogger.Debug("MySqlHandler().System().systemDatenbankVerbindungsSatz war NPE, versuche VerbindungsSatz neu zu initialisieren");
                systemDatenbankVerbindungsSatz = new MySqlHandler().initialisiereSystemDatenbankVerbindungsSatz();
            }
            return pruefeConnection(systemDatenbankVerbindungsSatz);
        }

        /**
         * Stellt die eigentliche Verbindung zur Datenbank her und uebergibt sie der verarbeitenden Instanz
         *
         * @param dBVerbindungsSatz
         * @return
         */
        public static Connection Connector(DatenbankVerbindungsSatz dBVerbindungsSatz) {
            try {
                Log.JavaLogger.Debug("core.handlers.MySqlHandler.ConnectionHandler.Connector() 2: Open Connection");
                Connection Con = DriverManager.getConnection(dBVerbindungsSatz.connectionString, dBVerbindungsSatz.user, dBVerbindungsSatz.pass);
                return Con;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Oeffnet eine Connection ueber die Methode reConnect() welche wiederum prueft, ob bereits eine Verbindung etabliert wurde
         *
         * @param dbVerbindungsSatz - die Datenbankverbindungs Daten inklusive der Connection
         */
        public static void openConnection(DatenbankVerbindungsSatz dbVerbindungsSatz) {
            Log.JavaLogger.Debug("core.handlers.MySqlHandler.ConnectionHandler.openConnection()");
            reConnect(dbVerbindungsSatz);
        }

        /**
         * Prueft ob eine Datenbankverbindung etabliert wurde und wenn nicht, wird diese etabliert
         *
         * @param dbVerbindungsSatz - die Datenbankverbindungs Daten inklusive der Connection
         * @return
         */
        public static Connection pruefeConnection(DatenbankVerbindungsSatz dbVerbindungsSatz) {
            Log.JavaLogger.Verbose("core.handlers.MySqlHandler.ConnectionHandler.pruefeConnection()");
            try {
                if (dbVerbindungsSatz.getConnection() == null) {
                    Log.JavaLogger.Warning("Datenbank Verbindung fehlgeschlagen. Reconnecting...");
                    return reConnect(dbVerbindungsSatz);
                }
                if (!dbVerbindungsSatz.getConnection().isValid(3)) {
                    Log.JavaLogger.Debug("Verbindung untätig oder unterbrochen. Reconnecting...");
                    return reConnect(dbVerbindungsSatz);
                }
                if (dbVerbindungsSatz.getConnection().isClosed()) {
                    Log.JavaLogger.Warning("Verbindung wurde beendet. Reconnecting...");
                    return reConnect(dbVerbindungsSatz);
                }

                if (dbVerbindungsSatz.getConnection().isValid(3)) {
                    Log.JavaLogger.Debug("Verbindung ist Valide..");
                    return dbVerbindungsSatz.getConnection();
                }

            } catch (Exception e) {
                Log.JavaLogger.Error("Verbindung zur Datenbank " + dbVerbindungsSatz.db + " nicht moeglich! Error: " + e.getMessage());
            }
            return null;
        }

        /**
         * Schliesst eine Verindung
         *
         * @param Con
         */
        public static void closeConnection(Connection Con) {
            try {
                Log.JavaLogger.Debug("Schließe Datenbankverbindung");
                Log.JavaLogger.Debug("core.handlers.MySqlHandler.ConnectionHandler.CloseConnection");
                if (Con == null) {
                    return;
                }
                Con.close();
                Con = null;
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }

        /**
         * Etabliert oder reetabliert Datenbankverbindung
         *
         * @param dbVerbindungsSatz
         * @return
         */
        public static Connection reConnect(DatenbankVerbindungsSatz dbVerbindungsSatz) {
            Log.JavaLogger.Verbose("core.handlers.MySqlHandler.reConnect()");
            try {
                long start;
                long ende;
                start = System.currentTimeMillis();
                Log.JavaLogger.Debug("Versuche Verbindung zur Datenbank zu etablieren!");
                //Con = Connector(Con, setProperties(User, Pass, "true", "false"));
                dbVerbindungsSatz.setConnection(Connector(dbVerbindungsSatz));
                ende = System.currentTimeMillis();
                Log.JavaLogger.Debug("Connection zu MySQL Server wurde in " + (ende - start) + " ms etabliert!");
                return dbVerbindungsSatz.getConnection();
            } catch (Exception e) {
                Log.JavaLogger.Error("Fehler beim reconnecten! Error: " + e.getMessage());
                if (!(dbVerbindungsSatz.getConnection() == null)) {
                    Log.JavaLogger.Debug("core.handlers.MySqlHandler.reConnect(): Connection ist null!");
                    return null;
                }
                //CheckSystemDatenbanken();
                return null;
            }
        }
    }
}
