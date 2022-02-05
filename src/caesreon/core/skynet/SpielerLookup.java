package caesreon.core.skynet;

import caesreon.core.Log;
import caesreon.core.handlers.MySqlHandler;
import caesreon.core.handlers.MySqlHandler.ConnectionHandler;
import caesreon.core.hilfsklassen.ZeitUtils;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.OptionalLong;
import java.util.UUID;

public class SpielerLookup {
    public static class ErhalteConnection {
        public static UUID getUUID(Connection Con, String Spielername) {
            PreparedStatement preparedUpdateStatement;

            if (Con == ConnectionHandler.EconomyBridge()) {
                Con = ConnectionHandler.System();
                ResultSet result;
                try {
                    //Log.Debug(result.getString("getUUID"));
                    String data = SkyNetSQL.getPlayerDataFromName;
                    Log.SpigotLogger.Debug(data);
                    preparedUpdateStatement = Con.prepareStatement(data);
                    preparedUpdateStatement.setString(1, Spielername);
                    result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                    UUID temp = null;
                    assert result != null;
                    if (result.next()) {
                        Log.SpigotLogger.Debug(result.getString("uuid"));
                        temp = UUID.fromString(result.getString("uuid"));
                    }
                    MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                    ConnectionHandler.EconomyBridge();
                    return temp;

                } catch (Exception e) {
                    Log.SpigotLogger.Debug(e.toString());
                }
            }

            //Connection Con = ConnectionHandler.System();
            ResultSet result;
            try {
                //Log.Debug(result.getString("getUUID"));
                String data = SkyNetSQL.getPlayerDataFromName;
                Log.SpigotLogger.Debug(data);
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, Spielername);
                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                UUID temp = null;
                if (result.next()) {
                    Log.SpigotLogger.Debug(result.getString("uuid"));
                    temp = UUID.fromString(result.getString("uuid"));
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                //ConnectionHandler.closeConnection(Con);
                return temp;

            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return null;

        }

        /**
         * Methode um Spielernamen anhand von UUID nachzuschlagen, welcher in der MySQL Datenbank hinterlegt ist <br>
         * Dabei ist es egal, ob dieser Spieler real existiert oder ob er ein vSpieler ist, welcher nur in diesem Plugin definiert ist. <br>
         * Bspw. Nationalbank
         *
         * @param uuid
         * @return - Spielername
         */
        public static String getSpielername(Connection Con, UUID uuid) {
            if (Con == ConnectionHandler.EconomyBridge()) {
                Con = ConnectionHandler.System();
            }
            PreparedStatement preparedUpdateStatement;
            //Connection Con = ConnectionHandler.System();
            ResultSet result;
            try {
                String data = SkyNetSQL.getPlayerDataFromUUID;
                Log.SpigotLogger.Debug(data);
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, uuid.toString());
                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                String temp = null;
                assert result != null;
                if (result.next()) {
                    Log.SpigotLogger.Debug(result.getString("spielername"));
                    temp = result.getString("spielername");
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.EconomyBridge();
                return temp;
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return null;
        }
    }

    public static class SchliesseConnection {
        public static UUID getUUID(String Spielername) {
            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            ResultSet result;
            try {
                //Log.Debug(result.getString("getUUID"));
                String data = SkyNetSQL.getPlayerDataFromName;
                Log.SpigotLogger.Debug(data);
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, Spielername);
                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                UUID temp = null;
                assert result != null;
                if (result.next()) {
                    Log.SpigotLogger.Debug(result.getString("uuid"));
                    temp = UUID.fromString(result.getString("uuid"));
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);
                return temp;

            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return null;
        }

        /**
         * Methode um Spielernamen anhand von UUID nachzuschlagen, welcher in der MySQL Datenbank hinterlegt ist <br>
         * Dabei ist es egal, ob dieser Spieler real existiert oder ob er ein vSpieler ist, welcher nur in diesem Plugin definiert ist. <br>
         * Bspw. Nationalbank
         *
         * @param uuid
         * @return - Spielername
         */
        public static String getSpielername(UUID uuid) {
            Log.SpigotLogger.Debug("getSpieleDaten(UUID uuid)");
            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            ResultSet result;
            try {
                String data = SkyNetSQL.getPlayerDataFromUUID;
                Log.SpigotLogger.Debug(data);
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, uuid.toString());
                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                String temp = null;
                if (result.next()) {
                    Log.SpigotLogger.Debug(result.getString("spielername"));
                    temp = result.getString("spielername");
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);
                return temp;
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return null;
        }

        /**
         * Methode um Spielerdaten anhand von UUID nachzuschlagen, welcher in der MySQL Datenbank hinterlegt ist <br>
         * @param uuid Die UUID des Spielers, welcher abgefragt wird
         * @return
         */
        public static String[] getSpielerDatenSatzAnhandUUID(UUID uuid) {
            Log.SpigotLogger.Debug("getSpieleDaten(UUID uuid)");
            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            ResultSet result;
            try {
                String data = SkyNetSQL.getPlayerDataFromUUID;
                Log.SpigotLogger.Debug(data);
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, uuid.toString());
                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                String[] temp = null;
                assert result != null;
                if (result.next()) {
                    OptionalLong zuletzt_gesehen;
                    try {
                        zuletzt_gesehen = OptionalLong.of(result.getLong("zuletzt_gesehen"));
                    } catch (Exception ex) {
                        zuletzt_gesehen = OptionalLong.of(0);
                    }

                    temp = new String[]{
                            result.getString("spielername"),
                            String.valueOf(result.getLong("zuerst_gesehen")),
                            String.valueOf(zuletzt_gesehen.getAsLong())
                    };
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);
                return temp;
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return null;
        }

        /**
         * Methode um Spielerdaten anhand von Spielernamen nachzuschlagen, welcher in der MySQL Datenbank hinterlegt ist <br>
         * @param spielername Der Spieler, welcher abgefragt wird
         * @return
         */
        public static String[] getSpielerDatenSatzAnhandSpielername(String spielername) {
            Log.SpigotLogger.Debug("getSpieleDaten(UUID uuid)");
            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            ResultSet result;
            try {
                String data = SkyNetSQL.getPlayerDataFromName;
                Log.SpigotLogger.Debug(data);
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, spielername);
                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                String[] temp = null;
                if (result.next()) {
                    temp = new String[]{
                            result.getString("uuid"),
                            String.valueOf(result.getLong("zuerst_gesehen")),
                            String.valueOf(result.getLong("zuletzt_gesehen"))
                    };
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);
                return temp;
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return null;
        }

        public static Boolean istInDatenbank(Player p) {

            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            ResultSet result;
            try {
                String data = SkyNetSQL.getPlayerDataFromUUID;
                Log.SpigotLogger.Debug(data);
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, p.getUniqueId().toString());
                result = MySqlHandler.executeQueryAndGetResult(preparedUpdateStatement);
                assert result != null;
                if (!result.next()) {
                    spielerHinzufuegen(p);
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedUpdateStatement);
                ConnectionHandler.closeConnection(Con);

            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return false;
        }

        public static void spielerHinzufuegen(Player p) {
            PreparedStatement preparedUpdateStatement = null;
            Connection Con = ConnectionHandler.System();
            try {
                String data = SkyNetSQL.setDataFirstJoin;
                Log.SpigotLogger.Debug(p.getUniqueId() + ":" + p.getDisplayName());
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, p.getUniqueId().toString());
                preparedUpdateStatement.setString(2, p.getDisplayName());
                preparedUpdateStatement.setLong(3, ZeitUtils.erhalteAktuellenZeitstempel());
                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }

        public static void spielerHinzufuegen(String Name, String UUID) {
            PreparedStatement preparedUpdateStatement = null;
            Connection Con = ConnectionHandler.System();
            try {
                String data = SkyNetSQL.setDataFirstJoin;
                preparedUpdateStatement = Con.prepareStatement(data);
                preparedUpdateStatement.setString(1, UUID);
                preparedUpdateStatement.setString(2, Name);
                preparedUpdateStatement.setLong(2, ZeitUtils.erhalteAktuellenZeitstempel());
                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }
}
