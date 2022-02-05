package caesreon.universe.spigot.jupiter.modul.userwerbenuser;

import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.handlers.MySqlHandler;
import caesreon.core.handlers.MySqlHandler.ConnectionHandler;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.StringUT;
import caesreon.main.SpigotMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class UserWerbenUserHandler {
    private SpigotMain main;
    private String Spielername;
    private String uuid;
    private String Token;
    private String[] QueryDataVermittler;
    private String[] QueryDataReferee;
    private final UserWerbenUserModul modul;

    //TODO: Inner Classes einbauen
    public UserWerbenUserHandler(UserWerbenUserModul modul) {
        this.modul = modul;
        initialisiereDatenbank();
    }

    public void TokenEinloesen(Player p, String Token) {
        //TOKEN muss in Vermittler Tabelle stehen, einlpesender darf der Vermittler selbst nicht sein,
        if (!besitztAccount(p, "Referee") && !besitztAccount(p, "Vermittler")) {
            if (TokenGueltig(Token)) {
                Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler().TokenEinloesen(): GueltigerToken");
                PreparedStatement preparedUpdateStatement;
                QueryDataVermittler = ladeVermittlerDatenByToken(Token);
                Connection Con = ConnectionHandler.System();
                if (Con != null) {
                    try {
                        String sql = modul.getUWU().setVermittlungen();
                        int Vermittlungen = Integer.parseInt(QueryDataVermittler[3]);
                        Vermittlungen += 1;
                        Log.SpigotLogger.Debug(sql);
                        preparedUpdateStatement = Con.prepareStatement(sql);
                        preparedUpdateStatement.setInt(1, Vermittlungen);
                        preparedUpdateStatement.setString(2, Token);

                        MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                    } catch (Exception e) {
                        Log.SpigotLogger.Debug(e.toString());
                    }
                }
                Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler().TokenEinloesen(): Belohnung & Cleanup");
                erstelleRefereeAcount(p, Token);
                QueryDataVermittler = ladeVermittlerDatenByToken(Token);
                Belohnung(p, QueryDataVermittler[0]);
                Cleanup();
                return;
            }
        }
        Ablehnung(p);
    }

    public void TokenErhalten(Player p) {
        if (!besitztAccount(p, "Vermittler")) {
            Token = StringUT.ZufallsToken(10);
            PreparedStatement preparedUpdateStatement = null;
            Connection Con = ConnectionHandler.System();
            try {
                uuid = p.getUniqueId().toString();
                Spielername = p.getName();
                String data = modul.getUWU().erstelleAccount_VERMITTLER();
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, uuid);
                preparedUpdateStatement.setString(2, Spielername);
                preparedUpdateStatement.setString(3, Token);
                preparedUpdateStatement.setInt(4, 0);

                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            sendeNachrichtToken(p, Token);
            Cleanup();
        }

        sendeNachrichtToken(p, QueryDataVermittler[2]);
        Cleanup();
    }

    public Boolean besitztAccount(Player p, String Account) {
        Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.besitztAccount()");
        QueryDataReferee = null;
        QueryDataVermittler = null;
        switch (Account) {
            case "Vermittler":
                QueryDataVermittler = ladeVermittlerDaten(p);
                if (QueryDataVermittler != null) {
                    Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.besitztAccount(): Vermittler true");
                    return true;
                }
                break;
            case "Referee":

                QueryDataReferee = ladeRefereeDaten(p);
                if (QueryDataReferee != null) {
                    Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.besitztAccount() Referee true");
                    return true;
                }

        }
        Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.besitztAccount(): false");
        return false;
    }

    public String[] ladeVermittlerDaten(Player p) {
        try {
            PreparedStatement preparedStatement;
            ResultSet result = null;
            Connection Con = ConnectionHandler.System();
            String[] data = null;
            String sql = modul.getUWU().getAccountVermittler();
            Log.SpigotLogger.Debug(sql);
            preparedStatement = Con.prepareStatement(sql);
            preparedStatement.setString(1, p.getUniqueId().toString());

            result = MySqlHandler.executeQueryAndGetResult(preparedStatement);
            assert result != null;
            if (result.next()) {
                data = new String[]{String.valueOf(result.getString("referrer_uuid")), String.valueOf(result.getString("spielername")), String.valueOf(result.getString("TOKEN")), String.valueOf(result.getInt("Vermittlungen"))};
            }
            MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
            ConnectionHandler.closeConnection(Con);
            return data;
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
        return null;
    }

    public String[] ladeVermittlerDatenByToken(String Token) {
        String[] data = new String[0];
        try {
            PreparedStatement preparedStatement;
            ResultSet result;
            Connection Con = ConnectionHandler.System();
            String sql = modul.getUWU().getVermittlerDatadurchTOKEN();
            Log.SpigotLogger.Debug(sql);
            preparedStatement = Con.prepareStatement(sql);
            preparedStatement.setString(1, Token);

            result = MySqlHandler.executeQueryAndGetResult(preparedStatement);
            assert result != null;
            if (result.next()) {
                data = new String[]{String.valueOf(result.getString("referrer_uuid")), String.valueOf(result.getString("spielername")), String.valueOf(result.getString("TOKEN")), String.valueOf(result.getInt("Vermittlungen"))};
            }

            MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
            ConnectionHandler.closeConnection(Con);
            return data;
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
        return null;
    }


    public String[] ladeRefereeDaten(Player p) {
        try {
            PreparedStatement preparedStatement;
            ResultSet result;
            Connection Con = ConnectionHandler.System();
            String[] data = null;
            String sql = modul.getUWU().getAccontReferee();
            Log.SpigotLogger.Debug(sql);
            preparedStatement = Con.prepareStatement(sql);
            preparedStatement.setString(1, p.getUniqueId().toString());

            result = MySqlHandler.executeQueryAndGetResult(preparedStatement);
            assert result != null;
            if (result.next()) {
                data = new String[]{String.valueOf(result.getString("referee_uuid")), String.valueOf(result.getString("spielername")), String.valueOf(result.getString("USED_TOKEN"))};
            }
            MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
            ConnectionHandler.closeConnection(Con);
            return data;
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
        return null;
    }

    public Boolean TokenGueltig(String Token) {
        try {
            PreparedStatement preparedStatement;
            ResultSet result;
            Connection Con = ConnectionHandler.System();
            String sql = modul.getUWU().getVermittlerDatadurchTOKEN();
            Log.SpigotLogger.Debug(sql);
            preparedStatement = Con.prepareStatement(sql);
            preparedStatement.setString(1, Token);

            result = MySqlHandler.executeQueryAndGetResult(preparedStatement);
            assert result != null;
            if (result.next()) {
                return true;
            }

            MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
            ConnectionHandler.closeConnection(Con);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
        return false;
    }

    public void importiereAltAccounts() {
        Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.impotiereAlteAccounts()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();
        OfflinePlayer[] AlleSpieler = main.getServer().getOfflinePlayers();
        for (OfflinePlayer Spieler : AlleSpieler) {
            //Player p = Spieler.getPlayer();
            if (!besitztAccount(Spieler.getPlayer(), "Vermittler")) {
                Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.impotiereAlteAccounts() Initiere Import");
                Token = StringUT.ZufallsToken(10);
                try {
                    uuid = Spieler.getUniqueId().toString();
                    Spielername = Spieler.getName();
                    Log.SpigotLogger.Debug(uuid);
                    Log.SpigotLogger.Debug(Spielername);
                    Log.SpigotLogger.Debug(Token);
                    String data = modul.getUWU().erstelleAccount_VERMITTLER();
                    preparedUpdateStatement = Con.prepareStatement(data);
                    preparedUpdateStatement.setString(1, uuid);
                    preparedUpdateStatement.setString(2, Spielername);
                    preparedUpdateStatement.setString(3, Token);
                    preparedUpdateStatement.setInt(4, 0);

                    MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                    Cleanup();
                } catch (Exception e) {
                    Log.SpigotLogger.Debug(e.toString());
                }
            }
        }
    }

    /**
     * TODO: DSGVO-Relevant
     */
    public void spielerDatenLoeschen() {

    }

    private void initialisiereDatenbank() {
        Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.initialisiereDatenbank()");
        Connection Con = ConnectionHandler.System();
        pruefeDatenbank(Con);
        pruefeTabelle(Con);
    }

    private void pruefeDatenbank(Connection Con) {
        Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.pruefeDatenbank()");

        MySqlHandler.pruefeObDatenbankExistiert(Con, modul.getUWU().erstelleDatenbank());
    }

    private void pruefeTabelle(Connection Con) {
        Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.pruefeTabelle()");
        MySqlHandler.pruefeObTabelleExistiert(Con, modul.getUWU().erstelleTabelleVermittler());
        MySqlHandler.pruefeObTabelleExistiert(Con, modul.getUWU().erstelleTabelleReferee());
        ConnectionHandler.closeConnection(Con);
    }

    private void erstelleRefereeAcount(Player p, String Token) {
        Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.erstelleAccountReferee()");
        PreparedStatement preparedUpdateStatement = null;
        Connection Con = ConnectionHandler.System();
        //Player p = Spieler.getPlayer();
        Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler.erstelleRefereeAcc");
        try {
            uuid = p.getUniqueId().toString();
            Spielername = p.getName();
            Log.SpigotLogger.Debug(uuid);
            Log.SpigotLogger.Debug(Spielername);
            Log.SpigotLogger.Debug(Token);
            String data = modul.getUWU().erstelleAccount_REFEREE();
            preparedUpdateStatement = Con.prepareStatement(data);
            preparedUpdateStatement.setString(1, uuid);
            preparedUpdateStatement.setString(2, Spielername);
            preparedUpdateStatement.setString(3, Token);

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    private void sendeNachrichtToken(Player p, String Token) {
        TextComponent msg = new TextComponent(ChatColor.DARK_AQUA + Token + ChatColor.WHITE + " (Klick mich zum kopieren)");
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Token));

        ChatSpigot.NachrichtSenden(p, modul.Prefix, " Hier ist dein Einladungstoken:");
        p.spigot().sendMessage(msg);
    }

    private void Belohnung(Player p, String Vermittler) {
        Log.SpigotLogger.Debug("jupiter.modul.UserWerbenUserHandler().Belohnung()");
        Player V = (Player) Bukkit.getOfflinePlayer(UUID.fromString(Vermittler));

        Spieler vermittler = new Spieler(Vermittler);
        Spieler geworbener = new Spieler(p);

        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast erfolgreich deinen Einladungscode eingelöst!");
        modul.getMain().getMerkurKomponente().getServerWirtschaft().ServerGeldUeberweisen(vermittler, modul.getUwUConfig().getDouble("belohnung"), "UwU-Belohnung");
        modul.getMain().getMerkurKomponente().getServerWirtschaft().ServerGeldUeberweisen(geworbener, modul.getUwUConfig().getDouble("belohnung"), "UwU-Belohnung");
    }

    private void Cleanup() {
        QueryDataVermittler = null;
        QueryDataReferee = null;
    }

    private void kopierbestaetigung(Player p) {
        ChatSpigot.NachrichtSenden(p, modul.Prefix, " Link kopiert");
    }

    private void Ablehnung(Player p) {
        ChatSpigot.NachrichtSenden(p, modul.Prefix, " Du kannst diesen Code nicht benutzen. Account existiert schon");
    }
}
