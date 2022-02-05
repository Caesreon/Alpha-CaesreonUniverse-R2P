package caesreon.universe.spigot.jupiter.modul.loginbelohnungen;

import caesreon.core.system.DatanbankTabellenMySQL.SYSTEM;

public class BelohnungenSQL {
    public static final String erstelleTabelle = "CREATE TABLE IF NOT EXISTS `" + SYSTEM.LoginBelohnungen + "` (id int(10) AUTO_INCREMENT, uuid varchar(50) NOT NULL, spielername varchar(50) NOT NULL, claims_monat int NOT NULL, claims_alle int NOT NULL, validations_zeit bigint NOT NULL, PRIMARY KEY(id));";

    public static final String setData = "UPDATE `" + SYSTEM.LoginBelohnungen + "` " + "SET `claims_monat` = ?, `claims_alle` = ?, `validations_zeit` = ?" + " WHERE `uuid` = ?";

    public static final String setClaimsMonat = "UPDATE `" + SYSTEM.LoginBelohnungen + "` " + "SET `claims_monat` = ?";

    public static final String getData = "SELECT * FROM `" + SYSTEM.LoginBelohnungen + "` WHERE `uuid` = ?";

    public static final String erstelleAccount = "INSERT INTO `" + SYSTEM.LoginBelohnungen + "`(`uuid`, `spielername`, `claims_monat`, `claims_alle`, `validations_zeit`) " + "VALUES(?, ?, ?, ?, ?)";

}
