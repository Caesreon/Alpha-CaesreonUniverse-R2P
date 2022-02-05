package caesreon.universe.spigot.merkur.modul.berufe;

import caesreon.core.system.DatanbankTabellenMySQL.SYSTEM;

public class BerufeSQL {
    protected static final String CreateTable = "CREATE TABLE IF NOT EXISTS `" + SYSTEM.Berufe + "` ( "
            + "id int(10) AUTO_INCREMENT, "
            + "uuid varchar(50) UNIQUE NOT NULL, "
            + "spielername varchar(50) NOT NULL,"
            + "PRIMARY KEY(id));";

    protected static final String get_SpielerDaten = "SELECT * FROM `" + SYSTEM.Berufe.toString() + "` "
            + "WHERE `uuid` = ?";

    protected static final String loesche_SpielerDaten = "DELETE FROM `" + SYSTEM.Berufe.toString() + "` "
            + "WHERE `uuid` = ?";


    /**
     * @param BerufName
     * @return
     */
    protected static String berufZuDatenbankHinzufuegen(String BerufName) {
        return "ALTER TABLE " + SYSTEM.Berufe.toString() + " ADD " + BerufName + " varchar(50) DEFAULT '0=0.0' NOT NULL;";
    }

    /**
     * @param BerufName
     * @return
     */
    protected static String berufAusDatenbankLoeschen(String BerufName) {
        return "ALTER TABLE " + SYSTEM.Berufe.toString() + " DROP " + BerufName + ";";
    }

    /**
     * @param teilQuerySpielerStatistik
     * @return
     */
    protected static String setSpielerDaten(String teilQuerySpielerStatistik) {
        return "UPDATE `" + SYSTEM.Berufe.toString() + "` " + "SET " + teilQuerySpielerStatistik + " WHERE `uuid` = ?";
    }

    /**
     * @param alterBerufName
     * @param neuerBerufName
     * @return
     */
    protected static String updateBerufName(String alterBerufName, String neuerBerufName) {
        return "ALTER TABLE " + SYSTEM.Berufe.toString() + " CHANGE " + alterBerufName + " " + neuerBerufName + "varchar NOT NULL";
    }

    /**
     * @param Beruf
     * @return
     */
    protected String setSpielerBerufsLevelAdministrativ(String Beruf) {
        return "UPDATE `" + SYSTEM.Berufe.toString() + "` " + "SET `" + Beruf + "´ = ?" + " WHERE `uuid` = ?";
    }

    /**
     * Da ich nicht von vorne herein weiss wieviele Berufe existieren, müssen diese quasi nach inseriert werden.
     *
     * @param dynamischeTeilQueryBerufe
     * @param dynamischeTeilQueryWerte
     * @return
     */
    protected String erstelleAccount(String dynamischeTeilQueryBerufe, String dynamischeTeilQueryWerte) {
        return "INSERT INTO " + SYSTEM.Berufe.toString() + "(`uuid`, `spielername` " + dynamischeTeilQueryBerufe + ") " + "VALUES(?, ?" + dynamischeTeilQueryWerte + ")";

    }
}
