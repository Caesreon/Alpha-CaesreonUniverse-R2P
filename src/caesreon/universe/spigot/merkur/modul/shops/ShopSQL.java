package caesreon.universe.spigot.merkur.modul.shops;

import caesreon.core.system.DatanbankTabellenMySQL.SYSTEM;

/**
 * @author Coriolanus_S
 */
public class ShopSQL {
    //(id, uuid, spielername, welt, schild_x, schild_y, schild_z, kiste_x, kiste_y, kiste_z, item_id, meta_daten, int Anzahl, Preis, PRIMARY KEY(id));";
    protected static final String erstelleTabelle = "CREATE TABLE IF NOT EXISTS `" + SYSTEM.SpielerShops + "` (id int(10) AUTO_INCREMENT, "
            + "uuid varchar(50) NOT NULL, spielername varchar(50) NOT NULL,"
            + "welt varchar(10) NOT NULL, "
            + "schild_x int NOT NULL,"
            + "schild_y int NOT NULL, "
            + "schild_z int NOT NULL, "
            + "kiste_x int NOT NULL, "
            + "kiste_y int NOT NULL, "
            + "kiste_z int NOT NULL, item_id varchar(50) NOT NULL, "
            + "meta_daten json, "
            + "Anzahl int NOT NULL, Preis int NOT NULL,"
            + " PRIMARY KEY(id));";
    /**
     * id, uuid, spielername, welt, schild_x, schild_y, schild_z, kiste_x, kiste_y, kiste_z, item_id, meta_daten, anzahl, preis
     */
    protected static final String erstelleShop = "INSERT INTO `" + SYSTEM.SpielerShops + "`(`uuid`, `spielername`, `welt`, `schild_x`, `schild_y`, `schild_z`, `kiste_x`, `kiste_y`, `kiste_z`, `item_id`, `meta_daten`, `anzahl`, `preis`) " + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    protected static final String updateShop = "UPDATE `" + SYSTEM.SpielerShops + "` " + "SET `kiste_x` = ?, `kiste_y` = ?, `kiste_z` = ?, `item_id` = ?, `meta_daten` = ?, `anzahl` = ?, `preis` = ?" + " WHERE `schild_x` = ? AND `schild_y` = ? AND `schild_z` = ?";
    /**
     * Achtung: Uniqer Identifier ist hier die Position des Shop Schilds
     * TODO: Erkennung der Welt einbauen
     *
     *  id, uuid, spielername, schild_x, schild_y, schild_z, kiste_x, kiste_y, kiste_z, item_id, anzahl, preis
     */
    protected static final String getData = "SELECT * FROM `" + SYSTEM.SpielerShops + "` WHERE `schild_x` = ? AND `schild_y` = ? AND `schild_z` = ?";
    protected static final String getAllData = "SELECT DISTINCT * FROM `" + SYSTEM.SpielerShops + "`;";
    protected static final String deleteDataBlockBreakChest = "DELETE FROM `" + SYSTEM.SpielerShops + "` WHERE `kiste_x` = ? AND `kiste_y` = ? AND `kiste_z` = ?";

    protected static final String deleteDataBlockBreakSign = "DELETE FROM `" + SYSTEM.SpielerShops + "` WHERE `schild_x` = ? AND `schild_y` = ? AND `schild_z` = ?";
}
