package caesreon.universe.spigot.jupiter.modul.mail;

import caesreon.core.Log;
import caesreon.core.handlers.MySqlHandler;
import caesreon.core.handlers.MySqlHandler.ConnectionHandler;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.SpielerUT;
import caesreon.universe.spigot.jupiter.JupiterKomponente;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MailHandler {
    private final JupiterKomponente modul;
    private String keineMail;

    public MailHandler(JupiterKomponente modul) {
        this.modul = modul;
        initialisiereDatenbank();
    }

    protected void sendMail(String Sender, String Empfaenger, String Nachricht) {
        Log.SpigotLogger.Debug("core.economy.jupiter.modul.mail.MailHandler().sendMails()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();
        try {
            String data = modul.getMail().erstelleNachricht();
            Log.SpigotLogger.Debug(data);
            Log.SpigotLogger.Debug(Sender + ":" + Empfaenger + ":" + Nachricht);
            preparedUpdateStatement = Con.prepareStatement(data);
            preparedUpdateStatement.setString(1, Sender);
            preparedUpdateStatement.setString(2, Empfaenger);
            preparedUpdateStatement.setString(3, Nachricht);
            preparedUpdateStatement.setString(4, "false");

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    protected List<String[]> getMails(Player p) {
        Log.SpigotLogger.Debug("core.economy.jupiter.modul.mail.MailHandler().getMails()");
        List<String[]> L = new ArrayList<>();
        PreparedStatement preparedUpdateStatement = null;
        Connection Con = ConnectionHandler.System();
        ResultSet result;
        if (Con != null) {
            try {
                String sql = modul.getMail().getPlayerData();
                Log.SpigotLogger.Debug(sql);
                preparedUpdateStatement = Con.prepareStatement(sql);
                preparedUpdateStatement.setString(1, p.getUniqueId().toString());

                //preparedUpdateStatement.setString(2, "false");
                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                while (true) {
                    assert result != null;
                    if (!result.next()) break;
                    String[] data = {String.valueOf(result.getInt("id")), result.getString("sender_uuid"), result.getString("nachricht")};
                    L.add(data);
                }

                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);
                return L;
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
        return null;
    }


    private void initialisiereDatenbank() {
        Log.SpigotLogger.Debug("core.economy.jupiter.modul.mail.MailHandler().initialisiereDatenbank()");
        Connection Con = ConnectionHandler.System();
        pruefeDatenbank(Con);
        pruefeTabelle(Con);
    }

    private void pruefeDatenbank(Connection Con) {
        if (modul.getMail() == null)
            Log.SpigotLogger.Debug("mail war npe");

        MySqlHandler.pruefeObDatenbankExistiert(Con, modul.getMail().erstelleDatenbank());
    }

    private void pruefeTabelle(Connection Con) {

        MySqlHandler.pruefeObTabelleExistiert(Con, modul.getMail().erstelleTabelle());
        ConnectionHandler.closeConnection(Con);
    }

    protected void gelesenStatus(int id, String gelesenStatus) {
        Log.SpigotLogger.Debug("core.economy.jupiter.modul.mail.MailHandler().gelesenStatus()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();
        if (Con != null) {
            try {
                String sql = modul.getMail().setStatus();
                Log.SpigotLogger.Debug(sql);
                preparedUpdateStatement = Con.prepareStatement(sql);
                preparedUpdateStatement.setString(1, gelesenStatus);
                preparedUpdateStatement.setInt(2, id);

                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }

    public void MailEmpfangen(Player p) {
        List<String[]> data = modul.getMailHandler().getMails(p);
        int dataSize = data.size();
        if (dataSize == 1) {
            for (String[] s : data) {
                Log.SpigotLogger.Debug(s[1] + " " + s[2]);
                p.sendMessage(DuHastEineNachrichtVon(s));
                ChatSpigot.NachrichtSenden(p, "[Mail]",  "Diese kannst du öffnen mit /mail öffnen 1");
            }
        }
        if (dataSize > 1) {
            ChatSpigot.NachrichtSenden(p, "[Mail]", "Du hast " + ChatColor.RED + dataSize + " neue Nachrichten.");
            ChatSpigot.NachrichtSenden(p, "[Mail]", "Diese kannst du öffnen mit /mail öffnen [Nr]");
        }

        if (dataSize == 0) {
            KeineMails();
        }

    }

    private void KeineMails() {
        keineMail = ChatColor.GOLD + "[Mail] " + ChatColor.WHITE + "Du hast keine neuen Nachrichten in deinem Posteingang.";
    }

    String KeineMailBenachrichtigung() {
        return keineMail;
    }

    protected void MailOeffnen(Player p, List<String[]> L, String Nr) {
        try {
            int IndexNr = Integer.parseInt(Nr);
            IndexNr = IndexNr - 1;
            String[] data = L.get(IndexNr);
            Log.SpigotLogger.Debug(data[1] + " " + data[2]);
            ChatSpigot.NachrichtSenden(p, "[Mail]", data[2]);
            gelesenStatus(Integer.parseInt(data[0]), "true");

        } catch (Exception e) {
            p.sendMessage(ChatColor.GOLD + "[Mail] " + ChatColor.RED + "Upps, irgendwas ist schief gelaufen! Hast du als Nr. eine Zahl verwendet?");
        }
    }

    private String DuHastEineNachrichtVon(String[] s) {
        return ChatColor.GOLD + "[Mail] " + ChatColor.WHITE + "Du hast eine Nachricht von " + ChatColor.GOLD + SpielerUT.getOfflinePlayerName(UUID.fromString(s[1])) + ChatColor.WHITE + " erhalten.";
    }
}
