package caesreon.universe.spigot.jupiter.modul.mail;

import caesreon.core.system.DatanbankTabellenMySQL.SYSTEM;
import caesreon.core.Log;
import caesreon.universe.spigot.jupiter.JupiterKomponente;

public class Mail {
    private final JupiterKomponente modul;

    public Mail(JupiterKomponente modul) {
        this.modul = modul;
    }

    protected String erstelleDatenbank() {
        String create_Database = "CREATE DATABASE IF NOT EXISTS " + modul.getMain().getCore().getDefaultConfig().getString("mysql.database") + ";";
        Log.SpigotLogger.Debug(create_Database);
        return create_Database;
    }

    protected String erstelleTabelle() {
        return "CREATE TABLE IF NOT EXISTS `" + SYSTEM.Mail + "` (id int(10) AUTO_INCREMENT, sender_uuid varchar(50) NOT NULL, empfaenger_uuid varchar(50) NOT NULL, nachricht varchar(100) NOT NULL, gelesen varchar(5) NOT NULL, PRIMARY KEY(id));";
    }

    protected String alterTable() {
        return "ALTER TABLE '" + SYSTEM.Mail + "` " + "` ADD gelesen varchar(5) NOT NULL DEFAULT 'true';";
    }

    protected String setStatus() {
        return "UPDATE `" + SYSTEM.Mail + "` " + "SET `gelesen` = ?" + " WHERE `id` = ?";
    }

    protected String getPlayerData() {
        return "SELECT * FROM `" + SYSTEM.Mail + "` WHERE `empfaenger_uuid` = ? AND gelesen='false'";
    }

    protected String erstelleNachricht() {
        return "INSERT INTO `" + SYSTEM.Mail + "`(`sender_uuid`, `empfaenger_uuid`, `nachricht`, `gelesen`) " + "VALUES(?, ?, ?, ?)";
    }
}
