package caesreon.universe.spigot.merkur.modul.berufe;

import caesreon.core.system.DatanbankTabellenMySQL.SYSTEM;
import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.handlers.AgentConfigHandler;
import caesreon.core.handlers.KonfigurationsDatenSatz;
import caesreon.core.handlers.MySqlHandler;
import caesreon.core.handlers.MySqlHandler.ConnectionHandler;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.FileUT;
import caesreon.core.skynet.SpielerLookup;
import caesreon.universe.spigot.merkur.modul.berufe.BerufeBelohnungsSatz.ItemUndEntitaetenBelohnungen;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class BerufeHandler {

    private final Map<String, Beruf> berufKonfigurationenInDatenBank = new HashMap<>();
    protected BerufeBelohnungsSatz berufeBelohnungsSatz = new BerufeBelohnungsSatz();
    BerufeModul modul;
    Map<Player, SpielerStatistiken> berufeCachedSpielstaende = new HashMap<>();
    /*
     * TODO:
     * berechneLevel() -> vll sogar Liste mit Leveln
     * erhoeheXP(exp addition)
     * berecheBelohnung()
     * ...............................
     * SchildInteract()
     * Top10 pro Beruf()
     */

    public BerufeHandler(BerufeModul modul) {
        this.modul = modul;

        Connection con = ConnectionHandler.System();
        MySqlHandler.pruefeObTabelleExistiert(con, BerufeSQL.CreateTable);
        ConnectionHandler.closeConnection(con);
        initialisiereBerufe();
    }

    /**
     * Initialisiert alle Berufe anhand der Configfiles, wenn der jeweilige Name des Berufes in der Datenbank vorhanden ist
     */
    private void initialisiereBerufe() {
        Log.SpigotLogger.Info("Suche nach Berufkonfigurationen..");
        Log.SpigotLogger.Debug("caesreon.universe.spigot.merkur.modul.berufe.initialisiereBerufe()");
        List<KonfigurationsDatenSatz> berufKonfigurationen =
                FileUT.getAlleKonfigurationsDateienVonOrdner(modul.berufeKonfigurationenPfad);

        if (berufKonfigurationen.isEmpty()) {
            Log.SpigotLogger.Info("Keine Berufkonfigurationen vorhanden");
            return;
        }

        for (KonfigurationsDatenSatz KDS : berufKonfigurationen) {
            YamlConfiguration config = KDS.getYamlConfiguration();
            ConfigurationSection handlungenSektion = config.getConfigurationSection("handlungen");

            Beruf beruf = new Beruf(config);

            if (berufIstInDatenbank(beruf)) {
                berufKonfigurationenInDatenBank.put(beruf.name, beruf);
                assert handlungenSektion != null;
                updateBerufeBelohnungsSatz(berufeBelohnungsSatz, config, handlungenSektion, beruf);
            } else {
                Log.SpigotLogger.Info("Beruf " + beruf.name + " nicht in Datenbank");

            }
        }
        Log.SpigotLogger.Info("Initialisierung der Berufe erfolgreich");
    }

    public void testBerufeLaden(Player p) {
        List<KonfigurationsDatenSatz> berufKonfigurationen;
        int beruf1 = 0;
        int beruf2 = 0;
        for (int i = 0; i <= 10000; i++) {
            berufKonfigurationen =
                    FileUT.getAlleKonfigurationsDateienVonOrdner(modul.berufeKonfigurationenPfad);

            if (berufKonfigurationen.get(0).getDateiName().equals("Minenarbeiter.yml")) {
                beruf1 += 1;
            } else if (berufKonfigurationen.get(0).getDateiName().equals("Holzfäller.yml")) {
                beruf2 += 1;
            }

            berufKonfigurationen = null;

        }

        ChatSpigot.NachrichtSenden(p, "btest", "Minenarbeiter als 1: " + beruf1 + "Holzfäller als 1: " + beruf2);

    }

    /**
     * Initialisiet einen spezifischen Beruf anhand einer Configfils, wenn der jeweilige Name des Berufes in der Datenbank vorhanden ist
     *
     * @implNote berufName muss hier als String identisch mit dem namen der Configfile sein
     */
    private void initialisiereBeruf(String berufName) {
        Log.SpigotLogger.Debug("caesreon.universe.spigot.merkur.modul.berufe.initialisiereBerufe()");

        KonfigurationsDatenSatz berufDatenSatz = new KonfigurationsDatenSatz(null, modul.berufeKonfigurationenPfad, berufName);
        berufDatenSatz.setYamlConfiguration(AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(berufDatenSatz));

        if (berufDatenSatz.getYamlConfiguration() == null) {
            Log.SpigotLogger.Warning("Keine Berufkonfigurationen vorhanden");
            return;
        }

        ConfigurationSection handlungenSektion = berufDatenSatz.getYamlConfiguration().getConfigurationSection("handlungen");

        Beruf beruf = new Beruf(berufDatenSatz.getYamlConfiguration());
        berufKonfigurationenInDatenBank.put(beruf.name, beruf);
        assert handlungenSektion != null;
        berufeBelohnungsSatz = updateBerufeBelohnungsSatz(berufeBelohnungsSatz, berufDatenSatz.getYamlConfiguration(), handlungenSektion, beruf);
        Log.SpigotLogger.Info("Initialisierung des Beruf " + beruf.name + " erfolgreich");
    }

    /**
     * Methode welche einen Beruf zu einem vorhandenen BerufeBelohnungsSatz hinzufuegt. Wird genutzt bei Initialisierung des Plugins
     * sowie beim laden eines Berufs per Befehl.
     *
     * @param berufeBelohnungsSatz
     * @param config
     * @param handlungenSektion
     * @param beruf
     * @return Berufe welche ein bestimmtes Item Belohnen
     * @see BerufeBelohnungsSatz
     */
    private BerufeBelohnungsSatz updateBerufeBelohnungsSatz(BerufeBelohnungsSatz berufeBelohnungsSatz, YamlConfiguration config, ConfigurationSection handlungenSektion, Beruf beruf) {
        Log.SpigotLogger.Debug("caesreon.universe.spigot.merkur.modul.berufe.updateBerufeBelohnungsSatz()");
        Set<String> handlungenKeys = handlungenSektion.getKeys(false);

        String item, pfad;
        int erfahrung, einkommen;

        for (String handlungsKey : handlungenKeys) {
            Log.SpigotLogger.Debug("caesreon.universe.spigot.merkur.modul.berufe.updateBerufeBelohnungsSatz():");
            Log.SpigotLogger.Debug("Handlungskey: " + handlungsKey);

            ConfigurationSection handlung = config.getConfigurationSection("handlungen." + handlungsKey);
            if (handlung == null)
                Log.SpigotLogger.Debug("section handlung war NPE");
            assert handlung != null;
            Set<String> handlungKeys = handlung.getKeys(false);
            if (handlungKeys == null)
                Log.SpigotLogger.Debug("section handlungKeys war NPE");

            for (String object : handlungKeys) {
                pfad = "handlungen." + handlungsKey + "." + object;
                item = object;
                einkommen = config.getInt(pfad + ".einkommen");
                erfahrung = config.getInt(pfad + ".erfahrung");
                Log.SpigotLogger.Config(object + ": " + "Einkommen: " + einkommen + " Erfahrung: " + erfahrung);
                EntitaetBelohnungsSatz entitaetBelohnungsSatz = new EntitaetBelohnungsSatz(item, einkommen, erfahrung, beruf);
                //Uebergibt Handlungskey, bspw. an berufeBelohnungsSatz, sowie den EntitaetBelohnungsSatz mit allen noetigen Daten
                berufeBelohnungsSatz.updateBerufeSetFuerHandlungsBelohnung(handlungsKey, entitaetBelohnungsSatz);
                Log.SpigotLogger.Config("Berufkonfiguration: " + beruf.name + " wurde erfolgreich geladen");
            }
        }
        return berufeBelohnungsSatz;
    }

    //System Handling

    protected void berufAusDatenbankLoeschen(String BerufName) {
        Log.SpigotLogger.Debug("merkur.modul.Berufe.berufAusDatenbankLoeschen()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = MySqlHandler.ConnectionHandler.System();
        try {
            String sql = BerufeSQL.berufAusDatenbankLoeschen(BerufName);
            preparedUpdateStatement = Con.prepareStatement(sql);
            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
            Log.SpigotLogger.Info("Berufkonfiguration: " + BerufName + " wurde erfolgreich geladen");
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Diesse Methode wird durch BerufeCommands aufgerufen und fuegt einen in der DB noch nicht vorhandenen Beruf hinzu,
     * sofern eine Konfiguration vorhanden ist welche einen gleichnamigen Beruf enthält
     *
     * @param berufName
     */
    protected void berufZuDatenbankHinzufuegen(String berufName) {
        Log.SpigotLogger.Debug("merkur.modul.Berufe.berufZuDatenbankHinzufuegen()");
        if (berufKonfigurationenInDatenBank.containsKey(berufName)) {
            Log.SpigotLogger.Debug("Beruf: " + berufName + " bereits in Datenbank vorhanden");
            return;
        }
        PreparedStatement preparedUpdateStatement = null;
        Connection Con = MySqlHandler.ConnectionHandler.System();
        try {
            String sql = BerufeSQL.berufZuDatenbankHinzufuegen(berufName);
            preparedUpdateStatement = Con.prepareStatement(sql);

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
            Log.SpigotLogger.Info("Berufkonfiguration: " + berufName + " wurde erfolgreich der Datenbank hinzugefuegt");
            initialisiereBeruf(berufName);
            spielerDatenNeuEinlesen();
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    protected void berufListe(Player spieler) {
        ChatSpigot.NachrichtSenden(spieler, BerufeModul.Prefix, ChatSpigot.chatTrennerModulBefehle(ChatColor.DARK_GREEN, ChatColor.WHITE + "[Berufe-Liste]"));
        for (Beruf b : berufKonfigurationenInDatenBank.values()) {
            ChatSpigot.NachrichtSenden(spieler, BerufeModul.Prefix, b.chatfarbe + b.name);
        }
    }

    protected void berufInfo(String berufName) {

    }

    protected void test(int testLevel, Player p) {

        int punkteGesamtOberesLevel = BerufeMathematik.berechneGesamtLevelPunkteProgression(testLevel + 1);
        int punkteGesamtUnteresLevel = BerufeMathematik.berechneGesamtLevelPunkteProgression(testLevel);
        int differenz = punkteGesamtOberesLevel - punkteGesamtUnteresLevel;
        ChatSpigot.NachrichtSenden(p, "test nl=", String.valueOf(punkteGesamtOberesLevel));
        ChatSpigot.NachrichtSenden(p, "test l=", String.valueOf(punkteGesamtUnteresLevel));
        ChatSpigot.NachrichtSenden(p, "test diff=", String.valueOf(differenz));
    }

    protected void test2(int l, int p, CommandSender sender) {
        int punkteGesamtOberesLevel = BerufeMathematik.berechneGesamtLevelPunkteProgression(l + 1);
        int punkteGesamtUnteresLevel = BerufeMathematik.berechneGesamtLevelPunkteProgression(l);
        int punkteBisOLevel = punkteGesamtOberesLevel - p;
        int punkteSeitULevel = p - punkteGesamtUnteresLevel;
        int differenz = punkteGesamtOberesLevel - punkteGesamtUnteresLevel;
        sender.sendMessage("test nl=" + punkteGesamtOberesLevel);
        sender.sendMessage("test l=" + punkteGesamtUnteresLevel);
        sender.sendMessage("test diff=" + differenz);
        sender.sendMessage("test p -> ol=" + punkteBisOLevel);
        sender.sendMessage("test p <- ul=" + punkteSeitULevel);
    }

    protected void berufStatistik(Player spieler) {
        SpielerStatistiken spielerStatistik = berufeCachedSpielstaende.get(spieler);
        ChatSpigot.NachrichtSenden(spieler, BerufeModul.Prefix, ChatSpigot.chatTrennerModulBefehle(ChatColor.DARK_GREEN, ChatColor.GOLD + "[Berufe-Statistiken]"));

        for (Spielstand spielstand : spielerStatistik.erhalteSpielstaende()) {
            ChatSpigot.NachrichtSenden(spieler, BerufeModul.Prefix, spielstand.getBeruf().chatfarbe + spielstand.getBeruf().name +
                    " Stufe " +
                    spielstand.getLevel() + "/" + spielstand.getBeruf().maximalLevel +
                    " Punktzahl: : " + String.format("%.0f", spielstand.getPunkteFortschrittAktuellesLevel()) +
                    "/" + String.format("%.0f", spielstand.getPunkteGesamtNextLevel()));
        }
    }

    /**
     * Methode um Beruf in der Datenbank umzubenennen, falls dieser falsch eingespeichert wurde
     *
     * @param alterName Alte Bezeichnung des Berufs
     * @param neuerName Neue Bezeichnung des berufs
     */
    protected void berufUmbenennen(String alterName, String neuerName) {
        Log.SpigotLogger.Debug("merkur.modul.Berufe.erstelleAccount()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = MySqlHandler.ConnectionHandler.System();
        try {
            String sql = BerufeSQL.updateBerufName(alterName, neuerName);
            preparedUpdateStatement = Con.prepareStatement(sql);

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
            Log.SpigotLogger.Info("Beruf " + alterName + " wurde in " + neuerName + " umbenannt.");
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Methode um Beruf in der Datenbank umzubenennen, falls dieser falsch eingespeichert wurde
     *
     * @param beruf
     * @param level
     * @param spielername
     */
    protected void setzeSpielerBerufsLevel(String beruf, int level, String spielername) {
        Log.SpigotLogger.Debug("merkur.modul.Berufe.erstelleAccount()");
        PreparedStatement preparedStatement;
        Connection Con = MySqlHandler.ConnectionHandler.System();
        try {
            String sql = modul.getBerufeSQL().setSpielerBerufsLevelAdministrativ(beruf);
            preparedStatement = Con.prepareStatement(sql);
            preparedStatement.setInt(1, level);
            preparedStatement.setString(2, Objects.requireNonNull(SpielerLookup.ErhalteConnection.getUUID(Con, spielername)).toString());

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedStatement);
            //Log.Config("Beruf " + alterName + " wurde in " + neuerName + " umbenannt.");
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    //TODO:
    public void spielerDatenSpeichern(SpielerDaten spielerDaten) {
        Log.SpigotLogger.Debug("merkur.modul.Berufe.spielerDatenSpeicher()");
        PreparedStatement preparedStatement;
        Connection Con = MySqlHandler.ConnectionHandler.System();
        try {
            StringBuilder temp = null;

            int zaehler = 0;

            //List<String> werteListe = new ArrayList<>();

            for (Spielstand spielstand : spielerDaten.spielerStatistiken.erhalteSpielstaende()) {
                if (zaehler == 0) {
                    Log.SpigotLogger.Debug(spielstand.getBeruf().name);
                    temp = new StringBuilder("`" + spielstand.getBeruf().name + "` ='" + spielstand.getDatenString() + "',");
                    zaehler += 1;
                } else {
                    Log.SpigotLogger.Debug(spielstand.getBeruf().name);
                    temp.append("`").append(spielstand.getBeruf().name).append("` ='").append(spielstand.getDatenString()).append("',");
                }
            }

            assert temp != null;
            StringBuilder teilQuery = new StringBuilder(temp.toString());
            teilQuery.deleteCharAt(teilQuery.length() - 1);
            String sql = BerufeSQL.setSpielerDaten(teilQuery.toString());

            Log.SpigotLogger.Debug(teilQuery.toString());
            Log.SpigotLogger.Debug(sql);
            preparedStatement = Con.prepareStatement(sql);
            preparedStatement.setString(1, spielerDaten.spieler.getUniqueId().toString());

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedStatement);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Methode, welche per Team Befehl aufgerufen wird und welche in Zukunft in einem zusammengeführten DSGVO-Loesch Befehl aufgerufen wird
     * TODO: DSGVO-Relevant
     */
    public void spielerDatenLoeschen(Player p) {
        Log.SpigotLogger.Debug("merkur.modul.Berufe.erstelleAccount()");
        PreparedStatement preparedStatement;
        Connection Con = MySqlHandler.ConnectionHandler.System();
        try {
            String sql = BerufeSQL.loesche_SpielerDaten;
            preparedStatement = Con.prepareStatement(sql);
            preparedStatement.setString(1, p.getUniqueId().toString());

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedStatement);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    private void spielerDatenNeuEinlesen() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            SpielerDaten spielerDatenAlt = new SpielerDaten(p, berufeCachedSpielstaende.get(p));
            spielerDatenSpeichern(spielerDatenAlt);
            SpielerStatistiken spielerStatistiken = ladeSpielerDaten(p);
            SpielerDaten spielerDaten = new SpielerDaten(p, spielerStatistiken);
            berufeCachedSpielstaende.put(spielerDaten.spieler, spielerDaten.spielerStatistiken);
        }
    }

    //Spieler Events

    public void spielerBeitritt(Player p) {
        SpielerStatistiken spielerStatistiken = ladeSpielerDaten(p);
        SpielerDaten spielerDaten = new SpielerDaten(p, spielerStatistiken);
        if (spielerIstInDatenbank(spielerStatistiken)) {
            berufeCachedSpielstaende.put(spielerDaten.spieler, spielerDaten.spielerStatistiken);
        } else {
            erstelleAccount(spielerDaten.spieler);
        }
    }

    /**
     * Methode welche vom Core - Spieler Eventlistener wahrgenommen wird
     *
     * @param p Der Spieler welcher sich ausloggt
     */
    public void spielerDisconnect(Player p) {
        Log.SpigotLogger.Debug("spielerDisconnect()");
        SpielerDaten spielerDaten = new SpielerDaten(p, berufeCachedSpielstaende.get(p));
        spielerDatenSpeichern(spielerDaten);
        berufeCachedSpielstaende.remove(p);
    }

    private SpielerStatistiken ladeSpielerDaten(Player p) {
        Log.SpigotLogger.Debug("ladeSpielerDaten(Player p)");
        try {
            PreparedStatement preparedStatement;
            ResultSet result = null;
            Connection Con = MySqlHandler.ConnectionHandler.System();
            String sql = BerufeSQL.get_SpielerDaten;
            SpielerStatistiken spielerStatistiken = new SpielerStatistiken();
            Log.SpigotLogger.Debug(sql);
            preparedStatement = Con.prepareStatement(sql);
            preparedStatement.setString(1, p.getUniqueId().toString());

            result = MySqlHandler.executeQueryAndGetResult(preparedStatement);
            if (result.next()) {
                ResultSetMetaData md = result.getMetaData();
                int coloumn_index = 4; //Da 1= id, 2=uuid 3=spielername 4=beruf1

                for (Beruf beruf : berufKonfigurationenInDatenBank.values()) {
                    if (md.getColumnName(coloumn_index).equals(beruf.name)) {
                        //Log.Dev(md.getColumnName(coloumn_index) + ":" + beruf.name);
                        Spielstand spielstand = new Spielstand(beruf, result.getString(coloumn_index));
                        Log.SpigotLogger.Debug(spielstand.getBeruf().name);
                        spielerStatistiken.spielstandZuSpielstaendeHinzufuegen(spielstand);
                        coloumn_index += 1;
                    } else {
                        ChatSpigot.NachrichtSenden(p, "System:", "Beim Laden deiner Berufedaten ist etwas schief gegangen, bitte informiere einen Administrator");
                    }
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
                ConnectionHandler.closeConnection(Con);
                return spielerStatistiken;
            }
            MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
            ConnectionHandler.closeConnection(Con);
            return null;
        } catch (Exception e) {
            Log.SpigotLogger.Debug(ChatColor.YELLOW + "BerufeHandler.ladeSpielerDaten(Player p): Fehler beim laden des Datensatzes");
            Log.SpigotLogger.Debug(e.toString());
        }
        return null;
    }

    /**
     * Erstellt einen Berufe-Account in der Datenbank
     *
     * @param p Spieler mit neuem Account
     * @implNote Hier kleine Besonderheit da ich hier ueber eine Teilquery arbeiten muss da ich anfangs nicht weiss, wieviele Berufe aktuell in der Datenbank sind oder sein sollen.
     * Dies muss quasi erst ueber BerufSatz: Berufe in Datenbank herausgefunden worden. Dies ist deswegen so geregelt, damit nachtraeglich Berufe hinzugefuegt oder
     * entfernt werden koennen, unabhaengig von der erstmaligen Konfiguration dieses Plugins/Moduls
     */
    protected void erstelleAccount(Player p) {
        Log.SpigotLogger.Debug("merkur.modul.Berufe.erstelleAccount()");
        PreparedStatement preparedUpdateStatement = null;
        Connection Con = MySqlHandler.ConnectionHandler.System();
        try {
            String uuid = p.getUniqueId().toString();
            SpielerStatistiken spielerStatistiken = new SpielerStatistiken();

            String teilQueryBerufe = null;
            String teilQueryWerte = null;

            List<String> werteListe = new ArrayList<>();

            int zaehler = 0;
            for (Beruf beruf : berufKonfigurationenInDatenBank.values()) {
                Log.SpigotLogger.Debug("merkur.modul.Berufe.erstelleAccount(): for beruf : berufeSatz");
                Spielstand spielstand = new Spielstand(beruf, "0" + Spielstand.trenner + "0");
                spielerStatistiken.spielstandZuSpielstaendeHinzufuegen(spielstand);
                if (zaehler == 0) {
                    Log.SpigotLogger.Debug("merkur.modul.Berufe.erstelleAccount()for: if erster durchlauf");
                    teilQueryBerufe = ",`" + beruf.name + "`";
                    teilQueryWerte = ", ?";
                    werteListe.add(spielstand.getDatenString());
                    zaehler += 1;
                } else {
                    Log.SpigotLogger.Debug("merkur.modul.Berufe.erstelleAccount(): else restliche berufe");
                    teilQueryBerufe = teilQueryBerufe + ",`" + beruf.name + "`";
                    teilQueryWerte = teilQueryWerte + ", ?";
                    werteListe.add(spielstand.getDatenString());
                }
                String sql = modul.getBerufeSQL().erstelleAccount(teilQueryBerufe, teilQueryWerte);
                Log.SpigotLogger.Debug(sql);

                preparedUpdateStatement = Con.prepareStatement(sql);
                preparedUpdateStatement.setString(1, uuid);
                preparedUpdateStatement.setString(2, p.getDisplayName());

                zaehler = 3;
                for (String wert : werteListe) {
                    preparedUpdateStatement.setString(zaehler, wert);
                    zaehler += 1;
                }
            }

            String sql = modul.getBerufeSQL().erstelleAccount("", "");
            Log.SpigotLogger.Debug(sql);

            preparedUpdateStatement = Con.prepareStatement(sql);
            preparedUpdateStatement.setString(1, uuid);
            preparedUpdateStatement.setString(2, p.getDisplayName());

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);

            berufeCachedSpielstaende.put(p, spielerStatistiken);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Diese Methode entlohnt alle Berufe, welche das jeweilige Item oder die Entitaet inklusive der Handlung in ihrer Konfiguration vermerkt haben.
     * Die vorherigen Methoden haben dafuer einen BerufeSatz ermittelt, ueber welchen hier Schlussendlich iteriert wird
     *
     * @param handlung bspw. "bauen" "abbauen" "toeten" "zaehmen"
     * @param entitaet Block oder LivingEntity Objekt
     * @param berufe   die entlohnenden Berufe
     * @param itemNameSpielerHand nur relevant für abbauende Blöcke, da hier auf Entes Custom Items geprüft wird
     */
    protected void entlohneBerufe(Player spieler, String handlung, String entitaet, BerufeSatz berufe, String itemNameSpielerHand) {
        if (berufe == null) {
            Log.SpigotLogger.Debug("Berufe: " + entitaet + " mit Handlung " + handlung + " nicht in Berufekonfigurationen gefunden");
            return;
        }

        entitaet = entitaet.toLowerCase();

        for (Beruf beruf : berufe.berufeSatz) {
            Log.SpigotLogger.Debug("Berufe: " + entitaet + " mit Handlung " + handlung + " in " + beruf.name + " gefunden");
            double einkommen = beruf.getKonfiguration().getDouble("handlungen." + handlung + "." + entitaet + ".einkommen");
            double erfahrung = beruf.getKonfiguration().getDouble("handlungen." + handlung + "." + entitaet + ".erfahrung");

            SpielerStatistiken SpielerStatistik = berufeCachedSpielstaende.get(spieler);
            Spielstand spielstandPassenderBeruf = SpielerStatistik.erhalteSpielsstandAnhandBeruf(beruf);

            double berechnete_geld_belohnung = BerufeMathematik.berechneEinkommensProgression(einkommen, spielstandPassenderBeruf.getLevel());
            double berechnete_levelpunkte_belohnung = BerufeMathematik.berechneLevelpunkteProgression(erfahrung, spielstandPassenderBeruf.getLevel());

            Log.SpigotLogger.Debug("Berufe: config_einkommen=" + einkommen
                    + " config_erfahrung=" + erfahrung + " berechnet_geld=" + berechnete_geld_belohnung
                    + " berechnete_erfahrung=" + berechnete_levelpunkte_belohnung);

            //Izem in Hand prueft nur auf Entes Custom Items
            itemNameSpielerHand = ChatColor.stripColor(itemNameSpielerHand);
            if (itemNameSpielerHand != null) {
                if (modul.geblockteItems.contains(itemNameSpielerHand)) {
                    berechnete_geld_belohnung = berechnete_geld_belohnung * 0.07;
                    berechnete_levelpunkte_belohnung = berechnete_levelpunkte_belohnung * 0.07;
                    Log.SpigotLogger.Debug("Adjustierte Belohnung!");
                    Log.SpigotLogger.Debug("Berufe: config_einkommen=" + einkommen
                            + " config_erfahrung=" + erfahrung + " berechnet_geld=" + berechnete_geld_belohnung
                            + " berechnete_erfahrung=" + berechnete_levelpunkte_belohnung);
                }
            }

            spielstandPassenderBeruf.addiereLevelPunkte(berechnete_levelpunkte_belohnung, spieler);

            Log.SpigotLogger.Debug("Spielerdaten:");
            Log.SpigotLogger.Debug(spielstandPassenderBeruf.toString());

            Spieler empfaenger = new Spieler(spieler);

            //Testweise geschlossener Wirtschaftskreislauf
            modul.getKomponente().getServerWirtschaft().ServerGeldUeberweisenHandelLeiseCached(empfaenger, berechnete_geld_belohnung, "Berufsbelohnung");
        }
    }

    /**
     * Prueft ob Beruf in Datenbank ist, da nur in der Datenbank geladene Berufe in die Wirtschaft hereingeladen werden sollen.
     * Ist der Beruf nicht vorhanden, so muss dieser per Befehl in die Datenbank aufgenommen werden. Dies sollte aber nur mit fertig und richtig
     * konfigurierten Berufen geschehen.
     *
     * @param beruf Der jeweilige Beruf welcher anhand einer Konfigurationsdate geladen wurde
     * @return true wenn die Datenbank ein gleichnamige Coloumn besitzt
     */
    private Boolean berufIstInDatenbank(Beruf beruf) {
        try {
            DatabaseMetaData md = ConnectionHandler.System().getMetaData();
            ResultSet rs = md.getColumns(null, null, SYSTEM.Berufe.toString(), beruf.name);
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            //ChatUT.NachrichtSenden(p, modul.Prefix, ChatColor.RED + "Beruf konnte nicht in der Datenbank hinzugefügt werden!");
            Log.SpigotLogger.Debug(e.toString());
        }
        return false;
    }

    /**
     * Prueft ob Spieler ein Berufe Konto in der Datenbank hinterlegt hat anhand der in spielerStatistiken übergebenen Daten
     *
     * @param spielerStatistiken Enthält eine Liste mit allen Spielständen des jeweiligen Spilers
     * @return true wenn die geladenen SpielerStatistiken nicht NULL sind
     */
    private Boolean spielerIstInDatenbank(SpielerStatistiken spielerStatistiken) {
        if (spielerStatistiken != null) {
            Log.SpigotLogger.Debug("Beruf: Spieler in DB gefunden");
            Log.SpigotLogger.Debug("Gefundene Spielstände: " + spielerStatistiken.spielstaende.values().size());
            return true;
        }
        Log.SpigotLogger.Debug("Beruf: Spieler nicht in DB gefunden");
        return false;
    }

    static class SpielerDaten {
        public Player spieler;
        public SpielerStatistiken spielerStatistiken;

        public SpielerDaten(Player spieler, SpielerStatistiken spielerStatistiken) {
            this.spieler = spieler;
            this.spielerStatistiken = spielerStatistiken;
        }
    }

    /**
     * Diese Klasse ermittelt anhand des uebergebenen Blocks und der Handlung welche ueber die Methoden definiert wird, welche Berufe genau
     * den jeweiligen Block oder die jeweilige Entitaet entlohen und gibt diese als BerufeSatz zurueck.
     *
     * @author Coriolanus_S
     */
    protected static class ErmittelEntlohnendeBerufe {
        protected static BerufeSatz bauen(Block b) {
            return ItemUndEntitaetenBelohnungen.bauen.get(b.getBlockData().getMaterial().toString().toLowerCase());
        }

        protected static BerufeSatz bauen(Block b, int alter) {
            return ItemUndEntitaetenBelohnungen.bauen.get(b.getBlockData().getMaterial().toString().toLowerCase() + "_" + alter);
        }

        protected static BerufeSatz abbauen(Block b) {
            return ItemUndEntitaetenBelohnungen.abbauen.get(b.getBlockData().getMaterial().toString().toLowerCase());
        }

        protected static BerufeSatz toeten(Entity entity) {
            return ItemUndEntitaetenBelohnungen.toeten.get(entity.getName().toLowerCase());
        }

        protected static BerufeSatz zuechten(Entity entity) {
            return ItemUndEntitaetenBelohnungen.zuechten.get(entity.getName().toLowerCase());
        }

        protected static BerufeSatz zaehmen(Entity entity) {
            return ItemUndEntitaetenBelohnungen.zaehmen.get(entity.getName().toLowerCase());
        }

        protected static BerufeSatz abbauen(Block b, int alter) {
            return ItemUndEntitaetenBelohnungen.abbauen.get(b.getBlockData().getMaterial().toString().toLowerCase() + "_" + alter);
        }
    }
}

