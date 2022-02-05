package caesreon.core.skynet;

import caesreon.core.system.DatanbankTabellenMySQL.SYSTEM;

public class SkyNetSQL {
    public static String erstelleTabelle =
            "CREATE TABLE IF NOT EXISTS  `" + SYSTEM.SpielerRegister.toString() + "` (id int(10) AUTO_INCREMENT, "
                    + "uuid varchar(50) NOT NULL UNIQUE,"
                    + " spielername varchar(50), "
                    + "zuerst_gesehen bigint NOT NULL, "
                    + "zuletzt_gesehen bigint,"
                    + " PRIMARY KEY(id));";

    public static String setDataFirstJoin =
            "INSERT INTO `" + SYSTEM.SpielerRegister.toString() + "` (`uuid`, `spielername`, `zuerst_gesehen`) "
                    + "VALUES(?, ?, ?);";

    public static String speichereSpielerRegisterDaten =
            "UPDATE `" + SYSTEM.SpielerRegister.toString() + "` SET `zuletzt_gesehen` = ? WHERE `uuid` = ? LIMIT 1;";

    public static String getPlayerDataFromUUID = "SELECT * FROM `" + SYSTEM.SpielerRegister.toString() + "`WHERE `uuid` = ? LIMIT 1;";

    public static String getPlayerDataFromName = "SELECT * FROM `" + SYSTEM.SpielerRegister.toString() + "` WHERE `spielername` = ? LIMIT 1;";
}
