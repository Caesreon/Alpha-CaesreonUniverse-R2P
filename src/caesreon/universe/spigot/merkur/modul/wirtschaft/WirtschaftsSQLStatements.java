package caesreon.universe.spigot.merkur.modul.wirtschaft;

import caesreon.core.Log;

public class WirtschaftsSQLStatements {
    public static final String erstelleTabelleZahlungsLog = "CREATE TABLE IF NOT EXISTS Transaktions_Log ("
            + "id int(10) AUTO_INCREMENT,"
            + "sender_uuid varchar(50) NOT NULL, "
            + "empfaenger_uuid varchar(50) NOT NULL, "
            + "buchung varchar(20) NOT NULL, betrag double(30,2) NOT NULL, "
            + "verwendungszweck varchar(100), "
            + "zeitstempel int NOT NULL, "
            + "kto_sender double(30,2) NOT NULL, "
            + "kto_empfaenger double(30,2) NOT NULL, "
            + "PRIMARY KEY(id));";
    public static final String logZahlung = "INSERT INTO Transaktions_Log("
            + "`sender_uuid`,"
            + "`empfaenger_uuid`, "
            + "`buchung`, "
            + "`betrag`, "
            + "`verwendungszweck`,"
            + "`zeitstempel`,"
            + "`kto_sender`, "
            + "`kto_empfaenger`) VALUES(?, ?, ?, ?, ?, ? ,?, ?)";
    private WirtschaftsModul modul;

    public WirtschaftsSQLStatements(WirtschaftsModul modul) {
        this.modul = modul;
    }

    public String erstelleDatenbankSQLCommand() {
        String Create_Database = "CREATE DATABASE IF NOT EXISTS " + WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.database") + ";";
        Log.SpigotLogger.Debug(Create_Database);
        return Create_Database;
    }

    public String erstelleTabelleSQLCommand() {
        String Create_Table = "CREATE TABLE IF NOT EXISTS `" + WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.table_accounts") + "` (id int(10) AUTO_INCREMENT, uuid varchar(50) NOT NULL UNIQUE, spielername varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
                + " kontostand double(30,2) NOT NULL, PRIMARY KEY(id));";
        return Create_Table;
    }

    /**
     * UPDATE [Tabelle] "SET `spielername` = ?" + ", `kontostand` = ?"
     */
    public String speichereKontostand() {
        return "UPDATE " + WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.table_accounts") + " SET "
                + "`spielername` = ?, "
                + "`kontostand` = ? "
                + "WHERE `uuid` = ?";
    }

    public String getPlayerDataSQLCommand() {
        String Select_Table = "SELECT * FROM `" + WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.table_accounts") + "` WHERE `uuid` = ? LIMIT 1";
        return Select_Table;
    }

    public String erstelleAccountSQLCommand() {
        String InsertInto_Table = "INSERT INTO `" + WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.table_accounts") + "`(`uuid`, `spielername`, `kontostand`) " + "VALUES(?, ?, ?)";
        return InsertInto_Table;
    }

    public String besitztAccountSQLCommand() {
        String Select_Table = "SELECT `uuid` FROM `" + WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.table_accounts") + "` WHERE `uuid` = ? LIMIT 1";
        return Select_Table;
    }

    public String GeldSubtrahierenSQLCommand() {
        String Update_Table = "UPDATE `" + WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.table_accounts") + "` " + "SET `kontostand` = (kontostand - ?)" + " WHERE `uuid` = ?";
        return Update_Table;
    }

    public String GeldAddieren() {
        String Update_Table = "UPDATE `" + WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.table_accounts") + "` " + "SET `kontostand` = (kontostand + ?)" + " WHERE `uuid` = ?";
        return Update_Table;
    }

    public String Top10SQLCommand() {
        String Select_Table = "SELECT * FROM `" + WirtschaftsModul.WirtschaftsKonfiguration.getString("mysql.table_accounts") + "` WHERE spielername NOT LIKE 'town%' AND spielername NOT LIKE '%Nationalbank%' ORDER BY kontostand DESC LIMIT 10";
        return Select_Table;
    }
}
