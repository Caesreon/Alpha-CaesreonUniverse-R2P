package caesreon.universe.spigot.jupiter.modul.userwerbenuser;

public class UserWerbenUserSQL {
    private static String Create_Table;
    private static String InsertInto_Table;
    private static String Select_Table;
    private final UserWerbenUserModul modul;
    //TODO: Evtl ueberarbeiten
    public UserWerbenUserSQL(UserWerbenUserModul modul) {
        this.modul = modul;
    }

    protected String erstelleDatenbank() {
        String create_Database = "CREATE DATABASE IF NOT EXISTS " + modul.getUwUConfig().getString("mysql.database") + ";";
        return create_Database;
    }

    protected  String erstelleTabelleVermittler() {
        Create_Table = "CREATE TABLE IF NOT EXISTS `" + modul.getUwUConfig().getString("mysql.table_referrer") + "` (id int(10) AUTO_INCREMENT, referrer_uuid varchar(50) NOT NULL UNIQUE, spielername varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL, TOKEN VARCHAR(50), Vermittlungen int(10), PRIMARY KEY(id));";
        return Create_Table;
    }

    protected String erstelleTabelleReferee() {
        Create_Table = "CREATE TABLE IF NOT EXISTS `" + modul.getUwUConfig().getString("mysql.table_referee") + "` (id int(10) AUTO_INCREMENT, referee_uuid varchar(50) NOT NULL UNIQUE, spielername varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL, USED_TOKEN VARCHAR(50), PRIMARY KEY(id));";
        return Create_Table;
    }

    //Soll Vermittlungen um eins erhoehen
    protected String setVermittlungen() {
        return "UPDATE `" + modul.getUwUConfig().getString("mysql.table_referrer") + "` " + "SET `Vermittlungen` = ?" + " WHERE `TOKEN` = ?";
    }

    //Prueft ob es Token gibt, wenn nicht, abbruch der Operation
    protected String getVermittlerDatadurchTOKEN() {
        Select_Table = "SELECT * FROM `" + modul.getUwUConfig().getString("mysql.table_referrer") + "` WHERE `TOKEN` = ? LIMIT 1";
        return Select_Table;
    }

    //Wenn keine Accounts Vorhanden sind, lege neue an
    protected String erstelleAccount_VERMITTLER() {
        InsertInto_Table = "INSERT INTO `" + modul.getUwUConfig().getString("mysql.table_referrer") + "`(`referrer_uuid`, `spielername`, `TOKEN`, `Vermittlungen`) " + "VALUES(?, ?, ?, ?)";
        return InsertInto_Table;
    }

    protected String erstelleAccount_REFEREE() {
        InsertInto_Table = "INSERT INTO `" + modul.getUwUConfig().getString("mysql.table_referee") + "`(`referee_uuid`, `spielername`, `USED_TOKEN`) " + "VALUES(?, ?, ?)";
        return InsertInto_Table;
    }

    //UID Gebunden, Prueft ob Account vorhanden ist
    protected String getAccontReferee() {
        Select_Table = "SELECT * FROM `" + modul.getUwUConfig().getString("mysql.table_referee") + "` WHERE `referee_uuid` = ? LIMIT 1";
        return Select_Table;
    }

    protected String getAccountVermittler() {
        Select_Table = "SELECT * FROM `" + modul.getUwUConfig().getString("mysql.table_referrer") + "` WHERE `referrer_uuid` = ? LIMIT 1";
        return Select_Table;
    }

}
