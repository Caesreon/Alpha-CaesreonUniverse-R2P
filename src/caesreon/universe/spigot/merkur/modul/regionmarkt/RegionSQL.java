package caesreon.universe.spigot.merkur.modul.regionmarkt;

import caesreon.core.system.DatanbankTabellenMySQL.SYSTEM;

public class RegionSQL {
    protected static final String Select_All_Regions = "SELECT * FROM `" + SYSTEM.Immobilien + "`";
    protected static final String Delete_Region = "DELETE FROM `" + SYSTEM.Immobilien + "` WHERE `plotname` = ?";
    protected static final String Update_RegionOwners = "UPDATE `" + SYSTEM.Immobilien + "` "
            + "SET `owners` = ?"
            + " WHERE `plotname` = ?";

    protected static final String Update_RegionMembers = "UPDATE `" + SYSTEM.Immobilien + "` "
            + "SET `members` = ?"
            + "WHERE `plotname` = ?";
    protected static final String Update_infoSchild = "UPDATE `" + SYSTEM.Immobilien + "` "
            + "SET "
            + "`vektor_schild` = ?"
            + " WHERE `plotname` = ?";
    protected static final String Update_Plot_Preis = "UPDATE `" + SYSTEM.Immobilien + "` "
            + "SET "
            + "`preis` = ?"
            + " WHERE `plotname` = ?";
    protected static final String Update_Plotname = "UPDATE `" + SYSTEM.Immobilien + "` "
            + "SET "
            + "`plotname` = ?,"
            + " WHERE `plotname` = ?";
    protected static final String Update_RegionOwnersUndMembers = "UPDATE `" + SYSTEM.Immobilien + "` "
            + "SET "
            + "`members` = ?,"
            + "`owners` = ?"
            + " WHERE `plotname` = ?";
    protected static final String Update_Region = "UPDATE `" + SYSTEM.Immobilien + "` "
            + "SET "
            + "`vektor_min` = ?, "
            + "`vektor_max` = ?, "
            + "`owners` = ?,"
            + "`members` = ?, "
            + "`preis` = ?, "
            + "`mietbar` = ?,"
            + "`verbleibende_mietdauer` = ?,"
            + "`verkaufbar` = ?,"
            + "`vektor_schild` = ?"
            + " WHERE `plotname` = ?";

    protected static final String Select_Region = "SELECT * FROM `" + SYSTEM.Immobilien + "` "
            + "WHERE `plotname` = ?";
    protected static final String Select_Region_Contains_Owner = "SELECT * FROM `" + SYSTEM.Immobilien + "` "
            + "WHERE owners LIKE ?";
    protected static final String Select_Region_Contains_Members = "SELECT * FROM `" + SYSTEM.Immobilien + "` "
            + "WHERE members LIKE ?";
    protected static final String Create_Region = "INSERT INTO " + SYSTEM.Immobilien + "("
            + "`plotname`, "
            + "`welt`, "
            + "`vektor_min`, "
            + "`vektor_max`, "
            + "`owners`, "
            + "`members`, "
            + "`preis`, "
            + "`mietbar`, "
            + "`verbleibende_mietdauer`,"
            + "`zeitstempel`, "
            + "`verkaufbar`,"
            + "`vektor_schild`)"
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    protected static final String Update_Enteignung = "UPDATE `" + SYSTEM.Immobilien + "` " + "SET "
            + "`owners` = ?, "
            + "`members` = ?, "
            + "`mietbar` = ?, "
            + "`verbleibende_mietdauer` = ?"
            + " WHERE `plotname` = ?";
    protected static final String Update_Miete = "UPDATE `" + SYSTEM.Immobilien + "` "
            + "SET `owners` = ?, `mietbar` = ?, `verbleibende_mietdauer` = ?"
            + " WHERE `plotname` = ?";
    protected static final String Update_Eigentuemer = "UPDATE `" + SYSTEM.Immobilien + "` "
            + "SET `eigentuemer_uuid` = ?, `eigentuemer_spielername` = ?" + "WHERE `plotname` = ?";
    protected static final String Update_Verkaufbar = "UPDATE `" + SYSTEM.Immobilien + "` "
            + "SET `verkaufbar` = ?" + "WHERE `plotname` = ?";
    /**
     * varchar(50) plotname,
     * welt varchar(50), varchar(50) vektor_min,
     * varchar(50) vektor_Max, varchar(200) owners, varchar(200) members, int preismietbar varchar(10),
     * verbleibende_mietdauer varchar(20), verkaufbar varchar10)
     */
    protected static final String CreateTable = "CREATE TABLE IF NOT EXISTS `" + SYSTEM.Immobilien
            + "` ( "
            + "id int(10) AUTO_INCREMENT, "
            + "plotname varchar(50) UNIQUE, "
            + "welt varchar(50) NOT NULL, "
            + "vektor_min varchar(50) NOT NULL, "
            + "vektor_max varchar(50) NOT NULL, "
            + "vektor_schild varchar(50), "
            + "owners varchar(200), "
            + "members varchar(200), "
            + "mietbar varchar(10), "
            + "verbleibende_mietdauer int, "
            + "verkaufbar varchar(10) NOT NULL,"
            + "zeitstempel bigint NOT NULL, "
            + "preis int NOT NULL,"
            + "PRIMARY KEY(id));";

    protected static String Select_Region_Contains_Plottyp_And_Owner(String owner, String plotname) {
        return "SELECT * FROM `" + SYSTEM.Immobilien.toString() + "` " + "WHERE owners LIKE '%" + owner
                + "%' AND plotname LIKE '%" + plotname + "%';";
    }
}
