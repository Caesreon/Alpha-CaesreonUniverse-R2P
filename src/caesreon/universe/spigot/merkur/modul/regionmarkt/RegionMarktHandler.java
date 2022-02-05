package caesreon.universe.spigot.merkur.modul.regionmarkt;

import caesreon.core.system.BasisBerechtigungsGruppen;
import caesreon.core.Log;
import caesreon.core.Spieler;
import caesreon.core.handlers.MySqlHandler;
import caesreon.core.handlers.MySqlHandler.ConnectionHandler;
import caesreon.core.hilfsklassen.BlockUT;
import caesreon.core.hilfsklassen.ChatSpigot;
import caesreon.core.hilfsklassen.MathematikUT.RegionQuaderKanten;
import caesreon.core.hilfsklassen.WorldEditUT;
import caesreon.core.minecraft.ChunkAdapter;
import caesreon.core.skynet.SpielerLookup;
import caesreon.universe.spigot.merkur.modul.wirtschaft.WirtschaftsModul;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RegionMarktHandler {
    /*
     * Weitere TODO:
     * Hashmap.clear wenn Server herunterfuehrt
     * Aktuelles System: Speicherungsart in DB -> Displayname (evtl muss dies zu UUID umgewandelt werden=
     * Wenn zu UUID umgewandelt wird, dann muss bei kaufSequenz() der SpielerLookup.getUUID() enfernt werden
     */

    private static final Map<String, RegionsDatenSatz> cachedGrundstuecke = new HashMap<>();
    private static final Set<RegionsDatenSatz> modifizierteGrundstuecke = new HashSet<>();
    //Variablen welche zwischen Methoden ausgetauscht werden
    private static int fehlenderBetrag;
    private final RegionMarktModul modul;

    /**
     * Konstruktor speichert Zugriff zur Main
     */
    public RegionMarktHandler(RegionMarktModul module) {
        this.modul = module;
        pruefeDatenbank();
        ladeAlleGrundstuecke();
    }

    /**
     * Diese Methode soll beim Herunterfahren alle Grundstücke in der Datenbank speichern, welche im Laufe der Runtime modifiziert wurden
     */
    public void onServerShutdown() {
        for (RegionsDatenSatz regionsDatenSatz : modifizierteGrundstuecke) {
            updateGrundstueck(regionsDatenSatz);
        }
    }

    /**
     * Erstellt eine Region aus den World Edit Selektion eines Nutzers
     *
     * @param Typ Regions-Typ (Starter_GS, Buerger_GS, Ehrenlegion_GS, Freebuild_GS, Villen_GS, Shop_GS)
     * @param p   Spieler, der die Region erstellt
     */
    protected void RegionErstellen(String Typ, Player p) {
        try {
            Region regionWorldEdit = WorldEditUT.erhalteRegionVonSelection(p);
            if (regionWorldEdit == null) {
                p.sendMessage("Bitte vorher eine vollstuendige Region auswählen");
                return;
            }

            //TODO Exception Handling + case gs_typ auslagern in enum

            /*Switch fuer Typesicherheit und predefinierung des Speichernamens fuer die jeweiligen GS. Eventuell werde ich das hier trotzdem in eine Autonome
             * in eine autonome Loesung umarbeiten wo man zum Ende hin nur noch ueber die Konfig den
             * Namen des GS-Typen definiert und den damit resultierenden Datenbank/Speicher Prefix
             *
             * Comment: Kleinschreibung deswegen, weil Worldguard nicht in case-sensitive unterscheidet.
             * Zur vereinfachung der Speicherlogik ist es daher nowendig, GS-Namen kleinzuschreiben
             */
            switch (Typ) {
                case RegionsTypen.starter:
                    speichereGrundstueck(RegionsTypen.starter + "_", regionWorldEdit.getMinimumPoint(), regionWorldEdit.getMaximumPoint(), p);
                    break;
                case RegionsTypen.buerger_kl:
                    speichereGrundstueck(RegionsTypen.buerger_kl + "_", regionWorldEdit.getMinimumPoint(), regionWorldEdit.getMaximumPoint(), p);
                    break;
                case RegionsTypen.buerger_gr:
                    speichereGrundstueck(RegionsTypen.buerger_gr + "_", regionWorldEdit.getMinimumPoint(), regionWorldEdit.getMaximumPoint(), p);
                    break;
                case RegionsTypen.legion:
                    speichereGrundstueck(RegionsTypen.legion + "_", regionWorldEdit.getMinimumPoint(), regionWorldEdit.getMaximumPoint(), p);
                    break;
                case RegionsTypen.freebuild:
                    speichereGrundstueck(RegionsTypen.freebuild + "_", regionWorldEdit.getMinimumPoint(), regionWorldEdit.getMaximumPoint(), p);
                    break;
                case RegionsTypen.villen:
                    speichereGrundstueck(RegionsTypen.villen + "_", regionWorldEdit.getMinimumPoint(), regionWorldEdit.getMaximumPoint(), p);
                    break;
                case RegionsTypen.shop:
                    speichereGrundstueck(RegionsTypen.shop + "_", regionWorldEdit.getMinimumPoint(), regionWorldEdit.getMaximumPoint(), p);
                    break;
                default:
                    ChatSpigot.NachrichtSenden(p, null, "Du musst einen Regionstypen oder hast diesen falsch angeben!");
                    break;
            }
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Gibt einen konvertieren, und beinahe uniquen String wieder welcher den GS-Namen beschreiben wird
     * anhand des selektierten Bereiches
     *
     * @param region - Worldguard-Region
     * @return String im Format {GS_Typ}_{x_y_z_x1_y2_z2}_{zuehler}
     */
    private String erhalteSelectionString(Region region) {
        String data = region.getBoundingBox().toString();
        data = data.replace(" ", "");
        data = data.replace("(", "");
        data = data.replace(")", "");
        data = data.replace("-", "_");
        data = data.replace(",", "_");
        return data;
    }

    /**
     * Loescht Region aus Datenbank
     *
     * @param plotname Name der Region
     */
    protected void regionLoeschen(String plotname, Player p) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.regionLoeschen()");

        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();
        try {
            preparedUpdateStatement = Con.prepareStatement(RegionSQL.Delete_Region);
            preparedUpdateStatement.setString(1, plotname);

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
            erhalteRegionContainer(p).removeRegion(plotname);
            cachedGrundstuecke.remove(plotname);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Löst ein Claim auf Wunsch des Spielers auf und löscht dieses auch aus der Datenbank
     *
     * @param plotname Name der Region
     */
    protected void regionAufloesen(String plotname, Player p) {
       try {
           RegionsDatenSatz regionenDatenSatz = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(plotname)));
           if (!checkObGSServerGehoert(regionenDatenSatz) && spielerBesitztGrundstueck(regionenDatenSatz, p)) {
               if (Objects.equals(RegionsTypen.getPlotTypFromString(plotname), RegionsTypen.freebuild)) {
                   PreparedStatement preparedUpdateStatement;
                   Connection Con = ConnectionHandler.System();
                   try {
                       preparedUpdateStatement = Con.prepareStatement(RegionSQL.Delete_Region);
                       preparedUpdateStatement.setString(1, plotname);

                       MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                       erhalteRegionContainer(p).removeRegion(plotname);

                       Spieler enteigneter = new Spieler(erhalteErstenOwnerUUID(regionenDatenSatz.Owners));
                       modul.getMain().getMerkurKomponente().getServerWirtschaft().ServerGeldUeberweisenHandel(enteigneter, regionenDatenSatz.Preis, "Enteignung");
                       entferneGSvomCache(plotname);

                   } catch (Exception e) {
                       Log.SpigotLogger.Debug(e.toString());
                   }
               }
           }
       } catch (Exception ex) {
           Log.SpigotLogger.Debug(ex.toString());
       }
    }

    /**
     * Löst ein Claim auf Wunsch des Spielers auf und löscht dieses auch aus der Datenbank
     *
     * @param p Name des Spielers
     */
    protected void regionAufloesen(Player p) {
        try {
            RegionsDatenSatz regionenDatenSatz = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(erhaltePlotnameDurchPosition(p))));
            if (!checkObGSServerGehoert(regionenDatenSatz) && spielerBesitztGrundstueck(regionenDatenSatz, p)) {
                if (regionenDatenSatz.plottyp.equals(RegionsTypen.freebuild)) {
                    PreparedStatement preparedUpdateStatement;
                    Connection Con = ConnectionHandler.System();
                    try {
                        preparedUpdateStatement = Con.prepareStatement(RegionSQL.Delete_Region);
                        preparedUpdateStatement.setString(1, regionenDatenSatz.plotname);

                        MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                        erhalteRegionContainer(p).removeRegion(regionenDatenSatz.plotname);

                        Spieler enteigneter = new Spieler(erhalteErstenOwnerUUID(regionenDatenSatz.Owners));
                        modul.getMain().getMerkurKomponente().getServerWirtschaft().ServerGeldUeberweisenHandel(enteigneter, regionenDatenSatz.Preis, "Enteignung");
                        entferneGSvomCache(regionenDatenSatz.plotname);
                        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast dein Grundstück erfolgreich aufgelöst.");

                    } catch (Exception e) {
                        Log.SpigotLogger.Debug(e.toString());
                    }
                }
            }
        } catch (Exception exception) {
            Log.SpigotLogger.Debug(exception.toString());
        }
    }

    protected void gsInfo(Player p, String plotname) {
        try {
            RegionsDatenSatz data = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(plotname)));
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Plotname: " + data.plotname);
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Besitzer: " + erhalteErstenOwner(data.Owners));
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Steht zum Verkauf?: " + data.Verkaufbar);
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Preis: " + data.Preis);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    protected void gsInfo(Player p) {
        try {
            RegionsDatenSatz data = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(erhaltePlotnameDurchPosition(p))));
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Plotname: " + data.plotname);
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Besitzer: " + erhalteErstenOwner(data.Owners));
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Steht zum Verkauf?: " + data.Verkaufbar);
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Preis: " + data.Preis);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /***
     * Kaufsequenz anhand des Plotnamens und der Welt des Spielers
     * @param Plotname - Das Grundstück
     * @param p - Der Spieler
     */
    protected void kaufen(String Plotname, Player p, Sign s) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.kaufen(String Plotname, Player p)");
        RegionsDatenSatz RegionenDatenSatz = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(Plotname)));
        kaufSequenz(RegionenDatenSatz, p, s);
    }

    /**
     * Kauf einer Region anhand der Position des Spielers
     *
     * @param p Spieler, der die Region kauft
     */
    protected void kaufen(Player p) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.kaufen(Player p)");
        String Plotname = erhaltePlotnameDurchPosition(p);
        RegionsDatenSatz RegionenDatenSatz = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(Plotname)));
        kaufSequenz(RegionenDatenSatz, p, null);
    }

    private void kaufSequenz(RegionsDatenSatz data, Player p, Sign s) {
        if (!spielerBesitztGrundstueck(data, p) && limitErreicht(p, data) && stehtZumVerkauf(data) && besitztGenuegendGeld((double) data.Preis, p)) {
            Spieler kaufender = new Spieler(p);
            Spieler verkaufender = new Spieler(erhalteErstenOwnerUUID(data.Owners));

            if (checkObGSServerGehoert(data)) {
                modul.getMain().getMerkurKomponente().getServerWirtschaft().ServerGeldAbbuchen(kaufender, data.Preis, "GS-Kauf");
            } else {
                modul.getMain().getMerkurKomponente().getServerWirtschaft().GeldUeberweisen(kaufender, verkaufender, data.Preis);
            }
            DefaultDomain owners = new DefaultDomain();
            DefaultDomain anzeigeNameOwners = new DefaultDomain();
            anzeigeNameOwners.addPlayer(p.getDisplayName());
            owners.addPlayer(p.getUniqueId());
            data.Owners = owners.toPlayersString();
            DefaultDomain members = new DefaultDomain();
            updateBesitzerUndMitglieder(data.plotname, owners.toPlayersString(), members.toPlayersString());
            Objects.requireNonNull(erhalteRegionContainer(p).getRegion(data.plotname)).setOwners(anzeigeNameOwners);
            Objects.requireNonNull(erhalteRegionContainer(p).getRegion(data.plotname)).setMembers(members);
            setzeVerkaufsFlag(data.plotname, false);
            if (s != null) //Abfrage fuer Kauf anhand Command + Position ist da hier s null ist
            {
                s.setLine(1, "§4" + modul.Verkauft);
                s.setLine(2, erhalteErstenOwner(data.Owners));
                s.setLine(3, data.plotname);
                s.update();
            }
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast erfolgreich das Grundstück " + data.plotname + " erworben!");
        } else if (!besitztGenuegendGeld((double) data.Preis, p)) {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du kannst dir dieses Grundstück nicht leisten.");
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Das Grundstück kostet: " + data.Preis + " und dir fehlen noch " + fehlenderBetrag + " Caesh");
        } else if (spielerBesitztGrundstueck(data, p)) {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du kannst dein eigenes Grundstück nicht kaufen");
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses Grundstück steht nicht zum Verkauf.");
        }
    }

    /**
     * Verkaufsequenz GS von Spieler an den Server.
     *
     * @param data - Der Regions Datensatz
     * @param p    - Der Spieler, welcher sein Grundstück an den Server verkäuft
     * @see #setzeVerkaufsFlag(String, Boolean)
     * @see #verkaufen(Player)
     */
    private void verkaufSequenzAnServer(RegionsDatenSatz data, Player p) {
        if (spielerBesitztGrundstueck(data, p)) {
            Block b = BlockUT.erhalteBlockDurchKoordinaten(p.getWorld(), data.infoSchild.getX(), data.infoSchild.getY(), data.infoSchild.getZ());
            UpdateEnteignung(data.plotname, p);
            RegionsDatenSatz aktualisierterRegionsDatenSatz = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(data.plotname)));
            setzeRegionSchild(b, p, aktualisierterRegionsDatenSatz);

            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast das Grundstück " + data.plotname + " erfolgreich verkauft!");
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du kannst dieses Grundstück nicht verkaufen");
        }
    }

    /**
     * Methode zum aktualisieren der "kaufbar" Flag in der Datenbank.
     * Wird entweder per Schild oder Befehl auf "true" oder "false" gesetzt bzw. nach einem erfolgreichen Kauf "false"
     *
     * @param plotname Name des Grundstücks
     * @param Flag     true=verkaufbar false=nicht verkaufbar
     */
    protected void setzeVerkaufsFlag(String plotname, Boolean Flag) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.setVerkaufsflag()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();
        try {
            preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_Verkaufbar);
            preparedUpdateStatement.setString(1, String.valueOf(Flag));
            preparedUpdateStatement.setString(2, plotname);

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Methode zum manuellen veraendern des Plotpreises seitens eines Nutzers.
     * Wird genutzt beim setzen eines Verkaufsschildes durch den Grundstueckseigentuemer
     *
     * @param plotname Name des Grundstücks
     * @param Preis    Preis des Grundstücks
     * @see #setzeRegionSchildVerkaufen(SignChangeEvent, Player)
     */
    protected void setPlotPreis(String plotname, int Preis) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.setVerkaufsFlag()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();
        try {
            preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_Plot_Preis);
            preparedUpdateStatement.setInt(1, Preis);
            preparedUpdateStatement.setString(2, plotname);

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Verkaufsequenz anhand des Plotnamens und der Welt des Spielers
     * Diese Funktion ist nur fuer den sofortigen Verkauf des GS an den Server gedacht. fuer den Verkauf von Spieler zu Spieler ist
     * die Methode setVerkaufsFlag() gedacht. Unterschied ist, dass der Spieler bei setVerkaufsFlag() sein
     * GS behaelt und dieses erst verliert, wenn ein Spieler eien dementsprechenden Betrag bezahlt.
     *
     * @param Plotname Name des Grundstücks
     * @param p        Spieler, der das Grundstück verkauft
     * @see #setzeVerkaufsFlag(String, Boolean)
     */
    protected void verkaufen(String Plotname, Player p) {
        RegionsDatenSatz regionsDaten = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(Plotname)));
        if (regionsDaten.Preis >= 0) {
            verkaufSequenzAnServer(regionsDaten, p);
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast die Region " + regionsDaten.plotname + " an den Staat verkauft");
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du kannst die Region " + regionsDaten.plotname + " nicht mit einem negativen Wert verkaufen");
        }
    }

    /**
     * Verkaufsequenz anhand der Position des Spielers
     * Diese Funktion ist nur fuer den sofortigen Verkauf des GS an den Server gedacht. fuer den Verkauf von Spieler zu Spieler ist
     * die Methode verkaufAnSpieler() gedacht. Unterschied ist, dass der Spieler bei verkaufAnSpieler() sein
     * GS behaelt und dieses erst verliert, wenn ein Spieler eien dementsprechenden Betrag bezahlt.
     *
     * @param p Spieler, der das Grundstück verkauft
     * @see #setzeVerkaufsFlag(String, Boolean)
     * @see #verkaufSequenzAnServer(RegionsDatenSatz, Player)
     */
    protected void verkaufen(Player p) {
        String Plotname = erhaltePlotnameDurchPosition(p);
        RegionsDatenSatz regionsDaten = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(Plotname)));
        if (regionsDaten.Preis >= 0) {
            verkaufSequenzAnServer(regionsDaten, p);
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du kannst die Region " + regionsDaten.plotname + " nicht mit einem negativen Wert verkaufen");
        }
    }

    /**
     * Command: /gs setowner [plotname] [Owner]
     * Command: /gs hinzufuegen owner [plotname] [Owner]
     *
     * @param plotname Grundstücksname
     * @param p        Spieler welcher den Command ausfuehrt
     * @param spieler  Spieler welchee hinzugefuegt werden soll
     */
    protected void ownerHinzufügenEinzelGS(String plotname, Player p, Spieler spieler) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.UpdateOwner()");
        if (plotname == null) plotname = erhaltePlotnameDurchPosition(p);
        RegionsDatenSatz RegionenDatenSatz = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(plotname)));
        if (spielerBesitztGrundstueck(RegionenDatenSatz, p) || hatTeamBerechtigung(p)) {

            DefaultDomain owners = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).getOwners();
            DefaultDomain anzeigeNameOwners = owners;

            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            try {
                if (Boolean.TRUE.equals(spielerValide(p, spieler))) {
                    owners.addPlayer(spieler.getSpielername());
                    anzeigeNameOwners.addPlayer(spieler.getUuid());

                    Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).setOwners(anzeigeNameOwners);

                    preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_RegionOwners);
                    preparedUpdateStatement.setString(1, owners.toPlayersString());
                    preparedUpdateStatement.setString(2, plotname);

                    MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                    updateCachedrundstuecke(RegionenDatenSatz);
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast " + spieler.getSpielername() + " als Besitzer des Grundstücks hinzugefügt");
                }
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
                ChatSpigot.NachrichtSendenFehler(p);
            }
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses Grundstück gehört dir nicht");
        }
    }

    /**
     * Command: /gs entfernen owner [Plotname] [Owner]
     *
     * @param plotname Grundstücksname
     * @param p        Spieler welcher den Command ausfuehrt
     * @param spieler  Spieler welcher hinzugefuegt werden soll
     */
    protected void ownerEntfernenEinzelGS(String plotname, Player p, Spieler spieler) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.UpdateOwner()");
        if (plotname == null) plotname = erhaltePlotnameDurchPosition(p);
        RegionsDatenSatz RegionenDatenSatz = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(plotname)));
        if (spielerBesitztGrundstueck(RegionenDatenSatz, p) || hatTeamBerechtigung(p)) {
            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            try {
                if (Boolean.TRUE.equals(spielerValide(p, spieler))) {
                    DefaultDomain owners = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).getOwners();
                    owners.removePlayer(spieler.getUuid());
                    Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).setOwners(owners);

                    preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_RegionOwners);
                    preparedUpdateStatement.setString(1, owners.toPlayersString());
                    preparedUpdateStatement.setString(2, plotname);
                    MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                    updateCachedrundstuecke(RegionenDatenSatz);
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Grundstück aktualisiert!");
                }
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
                ChatSpigot.NachrichtSendenFehler(p);
            }
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses Grundstück gehört dir nicht");
        }
    }

    private void updateModifizierteGrundstuecke(RegionsDatenSatz regionsDatenSatz) {
        modifizierteGrundstuecke.remove(regionsDatenSatz);
        modifizierteGrundstuecke.add(regionsDatenSatz);
    }

    private void updateCachedrundstuecke(RegionsDatenSatz regionsDatenSatz) {
        if (cachedGrundstuecke.containsKey(regionsDatenSatz.plotname))
            cachedGrundstuecke.remove((regionsDatenSatz.plotname));
        cachedGrundstuecke.put(regionsDatenSatz.plotname, regionsDatenSatz);
    }

    /**
     * Fuegt einen Member einen Grundstück hinzu
     *
     * @param plotname Grundstücksname
     * @param p        Spieler welcher den Command ausfuehrt
     * @param spieler  Weitere Spieler welche hinzugefuegt werden sollen
     */
    protected void memberHinzufügenEinzelGS(String plotname, Player p, Spieler spieler) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.UpdateMember()");
        if (plotname == null) plotname = erhaltePlotnameDurchPosition(p);

        RegionsDatenSatz regionenDatenSatz = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(plotname)));
        if (spielerBesitztGrundstueck(regionenDatenSatz, p) || hatTeamBerechtigung(p)) {

            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            try {
                if (Boolean.TRUE.equals(spielerValide(p, spieler))) {
                    DefaultDomain members = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).getMembers();
                    DefaultDomain anzeigeNameMembers = members;
                    anzeigeNameMembers.addPlayer(spieler.getSpielername());

                    Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).setMembers(anzeigeNameMembers);

                    preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_RegionMembers);
                    preparedUpdateStatement.setString(1, members.toPlayersString());
                    preparedUpdateStatement.setString(2, plotname);

                    MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast " + spieler.getSpielername() + " als Mitglied des Grundstücks hinzugefügt");
                }
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
                ChatSpigot.NachrichtSendenFehler(p);
            }
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses Grundstück gehört dir nicht");
        }
    }

    /**
     * Entfernt ein Mitglied von einem Grundstück
     *
     * @param plotname Grundstücksname
     * @param p        Spieler welcher den Command ausführt
     * @param spieler  Weitere Spieler welche hinzugefügt werden sollen
     */
    protected void memberEntfernenEinzelGS(String plotname, Player p, Spieler spieler) {
        if (plotname == null) plotname = erhaltePlotnameDurchPosition(p);
        RegionsDatenSatz RegionenDatenSatz = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(plotname)));
        if (spielerBesitztGrundstueck(RegionenDatenSatz, p) || hatTeamBerechtigung(p)) {
            Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.UpdateMember()");
            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            try {
                if (Boolean.TRUE.equals(spielerValide(p, spieler))) {
                    DefaultDomain members = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).getMembers();
                    members.removePlayer(spieler.getUuid());
                    Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).setMembers(members);

                    preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_RegionMembers);
                    preparedUpdateStatement.setString(1, members.toPlayersString());
                    preparedUpdateStatement.setString(2, plotname);

                    MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Grundstück aktualisiert!");
                }
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
                ChatSpigot.NachrichtSendenFehler(p);
            }
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses Grundstück gehört dir nicht");
        }

    }

    protected void ownerHinzufügenEinzelGSCached(String plotname, Player p, Spieler spieler) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.UpdateOwner()");
        if (plotname == null) plotname = erhaltePlotnameDurchPosition(p);
        RegionsDatenSatz regionenDatenSatz;
        try {
            regionenDatenSatz = Objects.requireNonNull(ladeDatenCached(plotname));
            if (spielerBesitztGrundstueck(regionenDatenSatz, p) || hatTeamBerechtigung(p)) {
                try {
                    if (Boolean.TRUE.equals(spielerValide(p, spieler))) {
                        DefaultDomain owners = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).getOwners();
                        owners.addPlayer(spieler.getUuid());
                        //anzeigeNameMembers.addPlayer(spieler.getSpielername());

                        Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).setOwners(owners);
                        regionenDatenSatz.Owners = owners.toPlayersString();
                        updateModifizierteGrundstuecke(regionenDatenSatz);
                        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast " + spieler.getSpielername() + " als Besitzer des Grundstücks " + regionenDatenSatz.plotname + " hinzugefügt");
                    }
                } catch (Exception e) {
                    Log.SpigotLogger.Debug(e.toString());
                    ChatSpigot.NachrichtSendenFehler(p);
                }
            } else {
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses Grundstück gehört dir nicht");
            }
        } catch (Exception exception)
        {
            Log.SpigotLogger.Debug(exception.toString());
        }
    }

    /**
     * Command: /gs entfernen owner [Plotname] [Owner]
     *
     * @param plotname Grundstücksname
     * @param p        Spieler welcher den Command ausfuehrt
     * @param spieler  Spieler welcher hinzugefuegt werden soll
     */
    protected void ownerEntfernenEinzelGSCached(String plotname, Player p, Spieler spieler) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.UpdateOwner()");
        if (plotname == null) plotname = erhaltePlotnameDurchPosition(p);
        RegionsDatenSatz regionenDatenSatz;
        try {
            regionenDatenSatz = Objects.requireNonNull(ladeDatenCached(plotname));
            if (spielerBesitztGrundstueck(regionenDatenSatz, p) || hatTeamBerechtigung(p)) {
                try {
                    if (Boolean.TRUE.equals(spielerValide(p, spieler))) {
                        DefaultDomain owners = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).getOwners();
                        owners.removePlayer(spieler.getUuid());
                        Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).setOwners(owners);
                        regionenDatenSatz.Owners = owners.toPlayersString();
                        updateModifizierteGrundstuecke(regionenDatenSatz);
                        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Grundstück aktualisiert!");
                    }
                } catch (Exception e) {
                    Log.SpigotLogger.Debug(e.toString());
                    ChatSpigot.NachrichtSendenFehler(p);
                }
            } else {
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses Grundstück gehört dir nicht");
            }
        }
        catch (Exception exception)
        {
            Log.SpigotLogger.Debug(exception.toString());
        }
    }

    /**
     * Fuegt einen Member einen Grundstück hinzu
     *
     * @param plotname Grundstücksname
     * @param spieler  Spieler welcher den Command ausfuehrt
     */
    protected void memberHinzufügenEinzelGSCached(String plotname, Player p, Spieler spieler) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.UpdateMember()");
        if (plotname == null) plotname = erhaltePlotnameDurchPosition(p);

        RegionsDatenSatz regionenDatenSatz;
        try {
            regionenDatenSatz = Objects.requireNonNull(ladeDatenCached(plotname));
            if (spielerBesitztGrundstueck(regionenDatenSatz, p) || hatTeamBerechtigung(p)) {
                try {
                    if (Boolean.TRUE.equals(spielerValide(p, spieler))) {
                        DefaultDomain members = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).getMembers();
                        members.addPlayer(spieler.getUuid());

                        Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).setMembers(members);
                        regionenDatenSatz.Members = members.toPlayersString();
                        updateModifizierteGrundstuecke(regionenDatenSatz);
                        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast " + spieler.getSpielername() + " als Mitglied des Grundstücks hinzugefügt");
                    }
                } catch (Exception e) {
                    Log.SpigotLogger.Debug(e.toString());
                    ChatSpigot.NachrichtSendenFehler(p);
                }
            } else {
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses Grundstück gehört dir nicht");
            }
        } catch (Exception exception)
        {
            Log.SpigotLogger.Debug(exception.toString());
        }

    }

    /**
     * Entfernt ein Mitglied von einem Grundstück und speichert dies Lokal in der Hashmap.
     * Diese Methode soll per Command durch die Spieler aufgerufen werden
     *
     * @param plotname Grundstücksname
     * @param p        Spieler welcher den Command ausführt
     * @param spieler  Weitere Spieler welche hinzugefügt werden sollen
     * @implNote UUID SpielerLookup sollte in aufrufender Methode bereits geschehen
     */
    protected void memberEntfernenEinzelGSCached(String plotname, Player p, Spieler spieler) {
        if (plotname == null) plotname = erhaltePlotnameDurchPosition(p);
        RegionsDatenSatz regionenDatenSatz;
        try {
            regionenDatenSatz = Objects.requireNonNull(ladeDatenCached(plotname));
            if (spielerBesitztGrundstueck(regionenDatenSatz, p) || hatTeamBerechtigung(p)) {
                Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.UpdateMember()");
                try {
                    if (Boolean.TRUE.equals(spielerValide(p, spieler))) {
                        DefaultDomain members = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).getMembers();
                        members.removePlayer(spieler.getUuid());
                        Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).setMembers(members);
                        regionenDatenSatz.Members = members.toPlayersString();
                        updateModifizierteGrundstuecke(regionenDatenSatz);
                        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Grundstück aktualisiert!");
                    }
                } catch (Exception e) {
                    Log.SpigotLogger.Debug(e.toString());
                    ChatSpigot.NachrichtSendenFehler(p);
                }
            } else {
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses Grundstück gehört dir nicht");
            }
        } catch (Exception exception)
        {
            Log.SpigotLogger.Debug(exception.toString());
        }


    }

    /**
     * Diese Methode enteignet ein Grundstück von einem Spieler und überschreibt dieses dem vSpieler Nationalbank
     *
     * @param plotname Der Grundstückname des zu enteignenen Grundstücks
     * @param p Der Spieler, welcher enteignet wird
     */
    protected void UpdateEnteignung(String plotname, Player p) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.UpdateEnteignung()");

        RegionsDatenSatz regionenDatenSatz = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(plotname)));
        if (!checkObGSServerGehoert(regionenDatenSatz)) {
            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            try {
                DefaultDomain owners = new DefaultDomain();
                DefaultDomain members = new DefaultDomain();
                DefaultDomain anzeigeName = new DefaultDomain();
                //TODO: Mietbar
                String mietbar = "false";
                int verbleibendeMietdauer = 0;
                String verkaufbar = "true";

                String Plottyp = RegionsTypen.getPlotTypFromString(plotname);
                Plottyp = Objects.requireNonNull(Plottyp).toLowerCase();

                if (!plotname.contains(RegionsTypen.freebuild))
                    regionenDatenSatz.Preis = berechnePreis(Plottyp, erhalteRegionContainer(p).getRegion(plotname));

                anzeigeName.addPlayer(WirtschaftsModul.Nationalbank.getSpielername());
                owners.addPlayer(WirtschaftsModul.Nationalbank.getUuid());

                Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).setOwners(anzeigeName);
                Objects.requireNonNull(erhalteRegionContainer(p).getRegion(plotname)).setMembers(members);
                preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_Region);
                preparedUpdateStatement.setString(1, regionenDatenSatz.MinVektor3.toString());
                preparedUpdateStatement.setString(2, regionenDatenSatz.MaxVektor3.toString());
                preparedUpdateStatement.setString(3, owners.toPlayersString());
                preparedUpdateStatement.setString(4, members.toPlayersString());
                preparedUpdateStatement.setInt(5, regionenDatenSatz.Preis);
                preparedUpdateStatement.setString(6, mietbar);
                preparedUpdateStatement.setInt(7, verbleibendeMietdauer);
                preparedUpdateStatement.setString(8, verkaufbar);
                preparedUpdateStatement.setString(9, regionenDatenSatz.infoSchild.toString());
                preparedUpdateStatement.setString(10, plotname);

                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);

                Spieler enteigneter = new Spieler(erhalteErstenOwnerUUID(regionenDatenSatz.Owners));
                modul.getMain().getMerkurKomponente().getServerWirtschaft().ServerGeldUeberweisenHandel(enteigneter, regionenDatenSatz.Preis, "Enteignung");
                updateCachedrundstuecke(regionenDatenSatz);
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
                ChatSpigot.NachrichtSendenFehler(p);
            }
        }
    }

    /**
     * Diese Methode speichert ein modifiziertes Grundstück in der Datenbank
     *
     * @param regionenDatenSatz Der modifizierte Datensatz, welcher in der Datenbank gespeichert werden soll
     */
    protected void updateGrundstueck(RegionsDatenSatz regionenDatenSatz) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.UpdateEnteignung()");

        if (!checkObGSServerGehoert(regionenDatenSatz)) {
            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            try {
                preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_Region);
                preparedUpdateStatement.setString(1, regionenDatenSatz.MinVektor3.toString());
                preparedUpdateStatement.setString(2, regionenDatenSatz.MaxVektor3.toString());
                preparedUpdateStatement.setString(3, regionenDatenSatz.Owners);
                preparedUpdateStatement.setString(4, regionenDatenSatz.Members);
                preparedUpdateStatement.setInt(5, regionenDatenSatz.Preis);
                preparedUpdateStatement.setString(6, regionenDatenSatz.Mietbar);
                preparedUpdateStatement.setInt(7, regionenDatenSatz.Mietdauer);
                preparedUpdateStatement.setString(8, regionenDatenSatz.Verkaufbar);
                preparedUpdateStatement.setString(9, regionenDatenSatz.infoSchild.toString());
                preparedUpdateStatement.setString(10, regionenDatenSatz.plotname);

                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                modifizierteGrundstuecke.remove(regionenDatenSatz);
            } catch (Exception e) {
                Log.SpigotLogger.Error(e.toString());
            }
        }
    }

    /**
     * Diese Methode wird ausschließlich beim Kaufen eines Grundstücks genutzt und speichert diese direkt in der Datenbank
     *
     * @param plotname Der Name des Grundstücks
     * @param owners Die Besitzer des Grundstücks
     * @param members Die Mitglieder des Grundstücks
     */
    private void updateBesitzerUndMitglieder(String plotname, String owners, String members) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.updateOwnersAndMembers()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();
        try {
            preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_RegionOwnersUndMembers);
            preparedUpdateStatement.setString(1, members);
            preparedUpdateStatement.setString(2, owners);
            preparedUpdateStatement.setString(3, plotname);

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }


    //TODO: Teleporter zu Freiem GS
    @SuppressWarnings("unused")
    protected void teleportZuFreiemGS() {

    }

    /**
     * TODO: DSGVO-Relevant
     */
    @SuppressWarnings("unused")
    protected void spielerDatenLoeschen() {

    }

    /**
     * Speichert Grundstücke in Hashmap waehrend der Runtime
     *
     * @param plotname  - Name des jeweiligen Grundstücks
     * @param datensatz
     */
    private void setCachedGrundstuecke(String plotname, RegionsDatenSatz datensatz) {
        cachedGrundstuecke.put(plotname, datensatz);
    }

    private void entferneGSvomCache(String Plotname) {
        cachedGrundstuecke.remove(Plotname);
    }

    /**
     * Fügt einen Spieler zu allen Grundstücken eines bestimmten Spielers als Owner hinzu
     * @param p Der Spieler, welcher jemanden als Owner auf allen seinen Grundstücken eintragen möchte
     * @param spieler Der Spieler, welcher zu den Grundstücken hinzugefügt werden soll
     */
    public void ownerHinzufuegenAlleGS(Player p, Spieler spieler) {
        for (RegionsDatenSatz grundstueck : Objects.requireNonNull(LadeAlleGS.spielerIstOwner(p))) {
            ownerHinzufügenEinzelGSCached(Objects.requireNonNull(grundstueck.plotname), p, spieler);
        }
        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast " + spieler.getSpielername()
                + " als Miteigentümer zu allen deinen Grundstücken hinzugefügt.");
    }
    /**
     * Fügt einen Spieler zu allen Grundstücken eines bestimmten Spielers als Member hinzu
     * @param p Der Spieler, welcher jemanden als Member auf allen seinen Grundstücken eintragen möchte
     * @param spieler Der Spieler, welcher zu den Grundstücken hinzugefügt werden soll
     */
    public void memberHinzufuegenAlleGS(Player p, Spieler spieler) {
        for (RegionsDatenSatz grundstueck : Objects.requireNonNull(LadeAlleGS.spielerIstOwner(p))) {
            memberHinzufügenEinzelGSCached(Objects.requireNonNull(grundstueck.plotname), p, spieler);
        }
        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast " + spieler.getSpielername()
                + " als Freund zu allen deinen Grundstücken hinzugefügt.");
    }

    /**
     * Entfernt einen Spieler als Owner aus allen Grundstücken eines bestimmten Spielers
     * @param p Der Spieler, welcher jemanden als Owner aus allen seinen Grundstücken entfernen möchte
     * @param spieler Der Spieler, welcher von den Grundstücken als Owner entfernt werden soll
     */
    public void ownerEntfernenAlleGS(Player p, Spieler spieler) {
        for (RegionsDatenSatz grundstueck : Objects.requireNonNull(LadeAlleGS.spielerIstOwner(p))) {
            ownerEntfernenEinzelGSCached(Objects.requireNonNull(grundstueck.plotname), p, spieler);
        }
        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast " + spieler.getSpielername()
                + " als Miteigentümer von allen deinen Grundstücken entfernt.");
    }

    /**
     * Entfernt einen Spieler als Owner aus allen Grundstücken eines bestimmten Spielers
     * @param p Der Spieler, welcher jemanden als Member aus allen seinen Grundstücken entfernen möchte
     * @param spieler Der Spieler, welcher von den Grundstücken als Member entfernt werden soll
     */
    public void memberEntfernenAlleGS(Player p, Spieler spieler) {
        for (RegionsDatenSatz grundstueck : Objects.requireNonNull(LadeAlleGS.spielerIstOwner(p))) {
            memberEntfernenEinzelGSCached(Objects.requireNonNull(grundstueck.plotname), p, spieler);
        }
        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du hast " + spieler.getSpielername()
                + "als Freund von allen deinen Grundstücken entfernt.");
    }

    //Koennte evtl im CIS Modul gebraucht werden
    public void zeigeAlleGSWoSpielerIstOwner(Player p) {
        for (RegionsDatenSatz grundstueck : Objects.requireNonNull(LadeAlleGS.spielerIstOwner(p))) {
        }
    }

    //Koennte evtl im CIS Modul gebraucht werden
    public void zeigeAlleGSWoSpielerIstMember(Player p) {
    }

    /**
     * Laedt Grundstücks-Daten aus Datenbank
     *
     * @param plotname Name des Grundstücks
     * @return eigentuemer_uuid, eigentuemer_spielername, welt, vektor_min, vektor_Max, owners, members, preis, mietbar, verbleibende_mietdauer
     */
    private String[] ladeDaten(String plotname) {
        try {
            PreparedStatement preparedStatement;
            ResultSet result;
            Connection Con = ConnectionHandler.System();
            String[] data = new String[0];
            String sql = RegionSQL.Select_Region;
            Log.SpigotLogger.Debug(sql);
            preparedStatement = Con.prepareStatement(sql);
            preparedStatement.setString(1, plotname);

            result = MySqlHandler.executeQueryAndGetResult(preparedStatement);
            if (Objects.requireNonNull(result).next()) {

                data = new String[]{
                        String.valueOf(result.getString("plotname")), String.valueOf(result.getString("welt")),
                        String.valueOf(result.getString("vektor_min")), String.valueOf(result.getString("vektor_max")),
                        String.valueOf(result.getString("owners")), String.valueOf(result.getString("members")),
                        String.valueOf(result.getString("mietbar")), String.valueOf(result.getInt("verbleibende_mietdauer")),
                        String.valueOf(result.getString("verkaufbar")), String.valueOf(result.getInt("preis")),
                        String.valueOf(result.getLong("zeitstempel")),
                        String.valueOf(result.getString("vektor_schild"))
                };
            }

            MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
            ConnectionHandler.closeConnection(Con);
            return data;
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
        return null;
    }

    /**
     * Laedt Grundstücks-Daten aus Datenbank
     *
     * @param plotname Name des Grundstücks
     * @return eigentuemer_uuid, eigentuemer_spielername, welt, vektor_min, vektor_Max, owners, members, preis, mietbar, verbleibende_mietdauer
     */
    private RegionsDatenSatz ladeDatenCached(String plotname) {
        return cachedGrundstuecke.get(plotname);
    }

    private String erhaltePlotnameDurchPosition(Player p) {
        Location Loc = BukkitAdapter.adapt(p.getLocation());
        ProtectedRegion region = WorldEditUT.erhalteRegionVonLocation(modul.getMain(), Loc);
        return Objects.requireNonNull(region).getId();
    }

    private ProtectedRegion erhalteRegionDurchSchildPosition(SignChangeEvent e) {
        Location Loc = BukkitAdapter.adapt(e.getBlock().getLocation());
        return WorldEditUT.erhalteRegionVonLocation(modul.getMain(), Loc);
    }

    private ProtectedRegion erhalteRegionDurchSchildPosition(Sign e) {
        Location Loc = BukkitAdapter.adapt(e.getBlock().getLocation());
        return WorldEditUT.erhalteRegionVonLocation(modul.getMain(), Loc);
    }

    /**
     * Lädt alle Grundstücke aus der Datenbank und speichert diese in einer Hashmap<plotname, preis>
     */
    private void ladeAlleGrundstuecke() {
        try {
            PreparedStatement preparedStatement;
            ResultSet result;
            Connection Con = ConnectionHandler.System();
            String sql = RegionSQL.Select_All_Regions;
            Log.SpigotLogger.Debug(sql);
            preparedStatement = Con.prepareStatement(sql);

            result = MySqlHandler.executeQueryAndGetResult(preparedStatement);

            while (Objects.requireNonNull(result).next()) {
                try {
                    String[] temp = {String.valueOf(result.getString("plotname")),
                            String.valueOf(result.getString("welt")),
                            String.valueOf(result.getString("vektor_min")),
                            String.valueOf(result.getString("vektor_max")),
                            String.valueOf(result.getString("owners")),
                            String.valueOf(result.getString("members")),
                            String.valueOf(result.getString("mietbar")),
                            String.valueOf(result.getInt("verbleibende_mietdauer")),
                            String.valueOf(result.getString("verkaufbar")),
                            String.valueOf(result.getInt("preis")),
                            String.valueOf(result.getLong("zeitstempel")),
                            String.valueOf(result.getString("vektor_schild"))};

                    RegionsDatenSatz data = new RegionsDatenSatz(temp);
                    setCachedGrundstuecke(result.getString("plotname"), data);
                    Log.SpigotLogger.Verbose(result.getString("plotname") + " " + result.getInt("preis"));
                } catch (Exception e) {
                    Log.SpigotLogger.Warning(e.toString());
                }
            }
            MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Speichert das Grundstück, wenn der Eintrag nicht in der Datenbank vorhanden ist.
     *
     * @param plotname Name fuer das Grundstück
     * @param min      Vektor zur Beschreibung der Minimal-Ecke
     * @param max      Vektor zur Beschreibung der Maximal-Ecke
     * @param p        Spieler, der das Grundstück erstellt
     */
    private void speichereGrundstueck(String plotname, BlockVector3 min, BlockVector3 max, Player p) {
        Log.SpigotLogger.Debug("merkur.modul.RegionMarktHandler.speichereGrundstueck()");
        //ladeAlleGrundstuecke();
        int GS_Nummer = 1;
        while (grundstueckExistiert(plotname + GS_Nummer)) {
            GS_Nummer += 1;
        }

        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();

        Calendar c = Calendar.getInstance();
        Date aktuellesDatum = new Date();
        c.setTime(aktuellesDatum);
        long zeitstempel = aktuellesDatum.getTime();

        DefaultDomain owners = new DefaultDomain();
        DefaultDomain members = new DefaultDomain();
        DefaultDomain anzeigeName = new DefaultDomain();

        if (!plotname.contains(RegionsTypen.freebuild)) {
            owners.addPlayer(SpielerLookup.ErhalteConnection.getUUID(Con, WirtschaftsModul.Nationalbank.getSpielername()));
            anzeigeName.addPlayer(WirtschaftsModul.Nationalbank.getSpielername());
        } else {
            owners.addPlayer(p.getUniqueId());
            anzeigeName.addPlayer(p.getUniqueId());
        }

        ProtectedCuboidRegion regionWG = new ProtectedCuboidRegion(plotname + GS_Nummer, min, max);

        String Plottyp = RegionsTypen.getPlotTypFromString(plotname);
        Plottyp = Objects.requireNonNull(Plottyp).toLowerCase();
        int Preis = berechnePreis(Plottyp, regionWG);
        String Owners = owners.toPlayersString();
        String Members = members.toPlayersString();
        String Mietbar = "false";
        String Welt = p.getWorld().getName();
        String infoSchild = "0";
        String Verkaufbar = "true";

        if (Plottyp.equals(RegionsTypen.freebuild + "_")) Verkaufbar = "false";
        //TODO: Mietzeit Funktion entwickeln
        int Verbleibende_Mietzeit = berechneMietdauer(null);

        Log.SpigotLogger.Debug(regionWG.toString());

        erhalteRegionContainer(p).addRegion(regionWG);
        Objects.requireNonNull(erhalteRegionContainer(p).getRegion(regionWG.getId())).setOwners(anzeigeName);
        Objects.requireNonNull(erhalteRegionContainer(p).getRegion(regionWG.getId())).setMembers(members);
        try {
            preparedUpdateStatement = Con.prepareStatement(RegionSQL.Create_Region);
            preparedUpdateStatement.setString(1, plotname + GS_Nummer);
            preparedUpdateStatement.setString(2, Welt);
            preparedUpdateStatement.setString(3, min.toString());
            preparedUpdateStatement.setString(4, max.toString());
            preparedUpdateStatement.setString(5, Owners);
            preparedUpdateStatement.setString(6, Members);
            preparedUpdateStatement.setInt(7, Preis);
            preparedUpdateStatement.setString(8, Mietbar);
            preparedUpdateStatement.setInt(9, Verbleibende_Mietzeit);
            preparedUpdateStatement.setLong(10, zeitstempel);
            preparedUpdateStatement.setString(11, Verkaufbar);
            preparedUpdateStatement.setString(12, infoSchild);
            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);

            String[] temp = {plotname, Welt, min.toString(), max.toString(), Owners, Members, Mietbar, String.valueOf(Verbleibende_Mietzeit), Verkaufbar, String.valueOf(Preis), String.valueOf(zeitstempel), infoSchild};
            RegionsDatenSatz regionsDatenSatz = new RegionsDatenSatz(temp);

            setCachedGrundstuecke(plotname + GS_Nummer, regionsDatenSatz);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
            ChatSpigot.NachrichtSendenFehler(p);
        }
        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Grundstück " + plotname + GS_Nummer + " erstellt!");
    }

    /**
     * Speichert das FREEBUILD Grundstück, wenn der Eintrag nicht in der Datenbank vorhanden ist.
     *  @param min      Vektor zur Beschreibung der Minimal-Ecke
     * @param max      Vektor zur Beschreibung der Maximal-Ecke
     * @param p        Spieler, der das Grundstück erstellt
     */
    private void speichereGrundstueckFreebuild(BlockVector3 min, BlockVector3 max, Player p, int preis) {
        Log.SpigotLogger.Debug("merkur.modul.RegionMarktHandler.speichereGrundstueck()");
        //ladeAlleGrundstuecke();
        int GS_Nummer = 1;
        while (grundstueckExistiert("freebuild_" + GS_Nummer)) {
            GS_Nummer += 1;
        }

        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();

        Calendar c = Calendar.getInstance();
        Date aktuellesDatum = new Date();
        c.setTime(aktuellesDatum);
        long zeitstempel = aktuellesDatum.getTime();

        DefaultDomain owners = new DefaultDomain();
        DefaultDomain members = new DefaultDomain();
        DefaultDomain anzeigeName = new DefaultDomain();

        owners.addPlayer(p.getUniqueId());
        anzeigeName.addPlayer(p.getUniqueId());

        ProtectedCuboidRegion regionWG = new ProtectedCuboidRegion("freebuild_" + GS_Nummer, min, max);

        String Plottyp = RegionsTypen.getPlotTypFromString("freebuild_");
        Plottyp = Objects.requireNonNull(Plottyp).toLowerCase();
        String Owners = owners.toPlayersString();
        String Members = members.toPlayersString();
        String Mietbar = "false";
        String Welt = p.getWorld().getName();
        String Verkaufbar = "false";
        String infoSchild = "0";

        if (Plottyp.equals(RegionsTypen.freebuild + "_")) Verkaufbar = "false";
        //TODO: Mietzeit Funktion entwickeln
        int Verbleibende_Mietzeit = berechneMietdauer(null);

        Log.SpigotLogger.Debug(regionWG.toString());

        erhalteRegionContainer(p).addRegion(regionWG);
        Objects.requireNonNull(erhalteRegionContainer(p).getRegion(regionWG.getId())).setOwners(anzeigeName);
        Objects.requireNonNull(erhalteRegionContainer(p).getRegion(regionWG.getId())).setMembers(members);

        try {
            preparedUpdateStatement = Con.prepareStatement(RegionSQL.Create_Region);
            preparedUpdateStatement.setString(1, "freebuild_" + GS_Nummer);
            preparedUpdateStatement.setString(2, Welt);
            preparedUpdateStatement.setString(3, min.toString());
            preparedUpdateStatement.setString(4, max.toString());
            preparedUpdateStatement.setString(5, Owners);
            preparedUpdateStatement.setString(6, Members);
            preparedUpdateStatement.setInt(7, preis);
            preparedUpdateStatement.setString(8, Mietbar);
            preparedUpdateStatement.setInt(9, Verbleibende_Mietzeit);
            preparedUpdateStatement.setLong(10, zeitstempel);
            preparedUpdateStatement.setString(11, Verkaufbar);
            preparedUpdateStatement.setString(12, infoSchild);
            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);

            String[] temp = {"freebuild_", Welt, min.toString(), max.toString(), Owners, Members, Mietbar, String.valueOf(Verbleibende_Mietzeit), Verkaufbar, String.valueOf(preis), String.valueOf(zeitstempel), infoSchild};
            RegionsDatenSatz regionsDatenSatz = new RegionsDatenSatz(temp);

            setCachedGrundstuecke("freebuild_" + GS_Nummer, regionsDatenSatz);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
            ChatSpigot.NachrichtSendenFehler(p);
        }
        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Grundstück " + "freebuild_" + GS_Nummer + " erstellt!");
    }

    protected void updateRegionSchildDB(BlockVector3 location, String plotname) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.setzeRegionSchild()");
        PreparedStatement preparedUpdateStatement;
        Connection Con = ConnectionHandler.System();
        String data = location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
        try {
            preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_infoSchild);
            preparedUpdateStatement.setString(1, data);
            preparedUpdateStatement.setString(2, plotname);

            MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    /**
     * Setzt Regio-Info  Schild innerhalb des GS Auslöser durch Quellcodeseitig HANDLER
     *
     * @param block
     * @param p
     * @param data
     */
    protected void setzeRegionSchild(Block block, Player p, RegionsDatenSatz data) {
        Log.SpigotLogger.Debug(ChatColor.DARK_GREEN + block.getType().toString());

        if (!(block.getState() instanceof Sign))
            block = p.getWorld().getBlockAt(data.infoSchild.getX(), data.infoSchild.getY(), data.infoSchild.getZ());
        block.setType(Material.DARK_OAK_SIGN);

        Sign e = (Sign) block.getState();

        if (checkObGSServerGehoert(data) || stehtZumVerkauf(data)) {
            e.setLine(0, modul.Farbiger_Identifier);
            e.setLine(1, "§2" + modul.Kaufen);
            e.setLine(2, erhalteErstenOwner(data.Owners));
            e.setLine(3, data.plotname);
            e.update();
            updateRegionSchildDB(WorldEditUT.konvertiereLocationZuBlockVector(BukkitAdapter.adapt(e.getBlock().getLocation())), data.plotname);
            return;
        }

        Log.SpigotLogger.Debug("§4" + modul.Verkauft);
        Log.SpigotLogger.Debug(erhalteErstenOwner(data.Owners));
        Log.SpigotLogger.Debug(data.plotname);

        e.setLine(0, modul.Farbiger_Identifier);
        e.setLine(1, "§4" + modul.Verkauft);
        e.setLine(2, erhalteErstenOwner(data.Owners));
        e.setLine(3, data.plotname);
        e.update();
        updateRegionSchildDB(WorldEditUT.konvertiereLocationZuBlockVector(BukkitAdapter.adapt(e.getBlock().getLocation())), data.plotname);
    }

    /**
     * Setzt Regio-Info  Schild innerhalb des GS, Auslöser durch SCHILD
     *
     * @param e Das Schild Veränderungs-Event
     * @param p Der Spieler, welcher das Schuld modifiziert hat
     */
    protected void setzeRegionSchild(SignChangeEvent e, Player p) {
        try {
            ProtectedRegion region = erhalteRegionDurchSchildPosition(e);
            RegionsDatenSatz data = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(region.getId())));

            Log.SpigotLogger.Debug("SignChange Event f");
            Log.SpigotLogger.Debug(data.plotname);

            if (checkObGSServerGehoert(data) || stehtZumVerkauf(data)) {
                Log.SpigotLogger.Debug("Ich war eein Schild und bin kaufbar");
                e.setLine(1, "§2" + modul.Kaufen);
                e.setLine(2, erhalteErstenOwner(data.Owners));
                e.setLine(3, data.plotname);
                updateRegionSchildDB(WorldEditUT.konvertiereLocationZuBlockVector(BukkitAdapter.adapt(e.getBlock().getLocation())), data.plotname);
                return;
            }
            Log.SpigotLogger.Debug("Ich war eein Schild und bin nicht kaufbar");

            Log.SpigotLogger.Debug("§4" + modul.Verkauft);
            Log.SpigotLogger.Debug(erhalteErstenOwner(data.Owners));
            Log.SpigotLogger.Debug(data.plotname);

            e.setLine(1, "§4" + modul.Verkauft);
            e.setLine(2, erhalteErstenOwner(data.Owners));
            e.setLine(3, data.plotname);
            updateRegionSchildDB(WorldEditUT.konvertiereLocationZuBlockVector(BukkitAdapter.adapt(e.getBlock().getLocation())), data.plotname);
        } catch (Exception ex) {
            Log.SpigotLogger.Debug(ChatColor.RED + ex.toString());
        }
    }

    /**
     * Setzt Region-Info Schild ausserhalb des GS anhand des GS-Namens welcher aufs Schild draufgeschrieben wurde
     *
     * @param e Das Schild Veränderungs-Event
     * @param p Der Spieler, welcher das Schuld modifiziert hat
     * @param plotname
     */
    protected void setzeRegionSchild(SignChangeEvent e, Player p, String plotname) {
        RegionsDatenSatz data = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(plotname)));
        try {
            if (checkObGSServerGehoert(data) || stehtZumVerkauf(data)) {
                e.setLine(1, "§2" + modul.Kaufen);
                e.setLine(2, erhalteErstenOwner(data.Owners));
                e.setLine(3, data.plotname);
                return;
            }
            Log.SpigotLogger.Debug("§4" + modul.Verkauft);
            Log.SpigotLogger.Debug(erhalteErstenOwner(data.Owners));
            Log.SpigotLogger.Debug(data.plotname);

            e.setLine(1, "§4" + modul.Verkauft);
            e.setLine(2, erhalteErstenOwner(data.Owners));
            e.setLine(3, data.plotname);
            updateRegionSchildDB(WorldEditUT.konvertiereLocationZuBlockVector(BukkitAdapter.adapt(e.getBlock().getLocation())), data.plotname);
        } catch (Exception ex) {
            Log.SpigotLogger.Debug(ex.toString());
        }

    }

    /**
     * @param e Das Schild Veränderungs-Event
     * @param p Der Spieler, welcher das Schuld modifiziert hat
     */
    protected void setzeRegionKaufen(Sign e, Player p) {
        ProtectedRegion region = erhalteRegionDurchSchildPosition(e);
        RegionsDatenSatz data = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(region.getId())));
        if (spielerBesitztGrundstueck(data, p) || hatTeamBerechtigung(p)) {
            setzeVerkaufsFlag(data.plotname, true);
            setPlotPreis(data.plotname, Integer.parseInt(e.getLine(3)));
            e.setLine(0, ChatColor.GOLD + "[GS]");
            e.setLine(1, "§2" + modul.Kaufen);
            e.setLine(2, erhalteErstenOwner(data.Owners));
            e.setLine(3, ChatColor.GREEN + e.getLine(3));
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du kannst dieses GS nicht verkaufen da es nicht gehört");
        }
    }

    /**
     * Diese Methode wird ausgeführt, wenn ein Spieler ein Schild setzt und darin sein Grundstück für den Verkauf
     * ausschreibt.
     *
     * @param e Das Schild Veränderungs-Event
     * @param p Der Spieler, welcher das Schuld modifiziert hat
     */
    protected void setzeRegionSchildVerkaufen(SignChangeEvent e, Player p) {
        ProtectedRegion region = erhalteRegionDurchSchildPosition(e);
        RegionsDatenSatz data = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(region.getId())));
        if (spielerBesitztGrundstueck(data, p) || hatTeamBerechtigung(p)) {
            setzeVerkaufsFlag(data.plotname, true);
            setPlotPreis(data.plotname, Integer.parseInt(Objects.requireNonNull(e.getLine(3))));
            e.setLine(0, ChatColor.GOLD + "[GS]");
            e.setLine(1, "§2" + modul.Kaufen);
            e.setLine(2, erhalteErstenOwner(data.Owners));
            e.setLine(3, ChatColor.GREEN + e.getLine(3));
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du kannst dieses GS nicht verkaufen da es nicht gehört");
        }
    }

    /**
     * @param alterGsName Aktueller Name des Grundstücks
     * @param neuerGsName Name, in welches das Grundstück umbenannt werden soll
     * @param p Der Spieler, welcher den Befehl ausführt
     */
    protected void aendereName(String alterGsName, String neuerGsName, Player p) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.aendereName()");
        if (cachedGrundstuecke.containsKey(alterGsName)) {
            RegionsDatenSatz data = new RegionsDatenSatz(Objects.requireNonNull(ladeDaten(alterGsName)));

            PreparedStatement preparedUpdateStatement;
            Connection Con = ConnectionHandler.System();
            try {
                DefaultDomain owners = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(alterGsName)).getOwners();
                DefaultDomain members = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(alterGsName)).getMembers();
                BlockVector3 min = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(alterGsName)).getMinimumPoint();
                BlockVector3 max = Objects.requireNonNull(erhalteRegionContainer(p).getRegion(alterGsName)).getMaximumPoint();

                preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_Plotname);
                preparedUpdateStatement.setString(1, neuerGsName);
                preparedUpdateStatement.setString(2, alterGsName);
                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);

                ProtectedCuboidRegion region = new ProtectedCuboidRegion(neuerGsName, min, max);
                region.setOwners(owners);
                region.setMembers(members);
                erhalteRegionContainer(p).removeRegion(alterGsName);
                erhalteRegionContainer(p).addRegion(region);
                cachedGrundstuecke.remove(alterGsName);
                cachedGrundstuecke.put(neuerGsName, data);
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "GS-Name erfolgreich modifiziert");

            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses GS konnte nicht gefunden werden. Evtl. ein Tippfehler?");
        }
    }

    protected void aenderePreis(String gsName, int Preis, Player p) {
        Log.SpigotLogger.Debug("jupiter.modul.RegionMarktHandler.aendereName()");
        if (cachedGrundstuecke.containsKey(gsName)) {
            PreparedStatement preparedUpdateStatement = null;
            Connection Con = ConnectionHandler.System();
            try {
                preparedUpdateStatement = Con.prepareStatement(RegionSQL.Update_Plot_Preis);
                preparedUpdateStatement.setInt(1, Preis);
                preparedUpdateStatement.setString(2, gsName);

                MySqlHandler.executePreparedStatementAndCloseConnection(Con, preparedUpdateStatement);
                ChatSpigot.NachrichtSenden(p, modul.Prefix, "Preis erfolgreich modifiziert");
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
        } else {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieses GS konnte nicht gefunden werden. Evtl. ein Tippfehler?");
        }
    }

    protected void claimChunk(Player p) {
        int preis = berechneFreebuildPreisProgressiv(modul.getRegionMarktConfig().getInt(modul.configRootFreebuild + ".preis"), AnzahlGS.freebuild(p, modul));

        if (besitztGenuegendGeld((double) preis, p)) {
            if (limitErreicht(p, RegionsTypen.freebuild)) {
                ChunkAdapter chunk = new ChunkAdapter(p);
                chunk.getEckpunkteChunk().getA().setY(chunk.getEckpunkteChunk().getA().getY() + 1);
                chunk.getEckpunkteChunk().getB().setY(chunk.getEckpunkteChunk().getB().getY() + 1);
                chunk.getEckpunkteChunk().getC().setY(chunk.getEckpunkteChunk().getC().getY() + 1);
                chunk.getEckpunkteChunk().getD().setY(chunk.getEckpunkteChunk().getD().getY() + 1);

                BlockVector3 min = WorldEditUT.konvertiereLocationZuBlockVector(BukkitAdapter.adapt(chunk.getEckpunkteChunk().getB()));
                BlockVector3 max = WorldEditUT.konvertiereLocationZuBlockVector(BukkitAdapter.adapt(chunk.getEckpunkteChunk().getC()));

                min = min.withY(-64); //-63 vll global auslagern
                max = max.withY(319); //319

                ProtectedCuboidRegion region = new ProtectedCuboidRegion("temp", min, max);
                ApplicableRegionSet set = erhalteRegionContainer(p).getApplicableRegions(region);
                if (set.getRegions().size() != 0) {
                    Log.SpigotLogger.Debug(String.valueOf(set.getRegions().size()));
                    Log.SpigotLogger.Debug(String.valueOf(set.getRegions().toString()));
                    ChatSpigot.NachrichtSenden(p, modul.Prefix, "Dieser Chunk gehört bereits jemanden.");
                    return;
                }
                if (set.getRegions().size() == 0) {
                    chunk.getEckpunkteChunk().getA().getBlock().setType(Material.TORCH);
                    chunk.getEckpunkteChunk().getB().getBlock().setType(Material.TORCH);
                    chunk.getEckpunkteChunk().getC().getBlock().setType(Material.TORCH);
                    chunk.getEckpunkteChunk().getD().getBlock().setType(Material.TORCH);

                    speichereGrundstueckFreebuild(min, max, p, preis);
                    Spieler spieler = new Spieler(p);
                    modul.getMerkurKomponente().getServerWirtschaft().ServerGeldAbbuchen(spieler, preis, "Kauf Grundstück");
                    return;
                }
            }
        }

        ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du besitzt nicht ausreichend Geld!");
    }

    /**
     * Berechnet Plotpreis in Relation zum Basispreis und der Anzahl der vorhandenen Grundstücke und der erlaubten Gratisclsims
     *
     * @param basispreis
     * @param anzahl
     * @return
     */
    private int berechneFreebuildPreisProgressiv(int basispreis, int anzahl) {
        Log.SpigotLogger.Debug("berechneFreebuildPreisProgressiv(int basispreis, int anzahl)");
        Log.SpigotLogger.Debug("anzahl=" + anzahl);
        if (anzahl <= modul.getRegionMarktConfig().getInt(modul.configRootFreebuild + ".gratis_claims")) {
            Log.SpigotLogger.Debug("anzahl_adj=" + anzahl);
            return 0;
        }
        if (anzahl > modul.getRegionMarktConfig().getInt(modul.configRootFreebuild + ".gratis_claims"))
            anzahl = anzahl - modul.getRegionMarktConfig().getInt(modul.configRootFreebuild + ".gratis_claims");
        Log.SpigotLogger.Debug("anzahl_adj=" + anzahl);
        Log.SpigotLogger.Debug(String.valueOf(modul.getRegionMarktConfig().getInt(modul.configRootFreebuild + ".gratis_claims")));
        double berechneterPreis = (0.1 * (anzahl * anzahl)) + (60 * anzahl) + basispreis;
        Log.SpigotLogger.Debug("wert=" + (int) berechneterPreis);
        return (int) berechneterPreis;
    }

    protected void testeChunk(Player p) {
        ChunkAdapter chunk = new ChunkAdapter(p);
        chunk.getEckpunkteChunk().getA().setY(chunk.getEckpunkteChunk().getA().getY() + 1);
        chunk.getEckpunkteChunk().getB().setY(chunk.getEckpunkteChunk().getB().getY() + 1);
        chunk.getEckpunkteChunk().getC().setY(chunk.getEckpunkteChunk().getC().getY() + 1);
        chunk.getEckpunkteChunk().getD().setY(chunk.getEckpunkteChunk().getD().getY() + 1);

        chunk.getEckpunkteChunk().getA().getBlock().setType(Material.TORCH);
        chunk.getEckpunkteChunk().getB().getBlock().setType(Material.TORCH);
        chunk.getEckpunkteChunk().getC().getBlock().setType(Material.TORCH);
        chunk.getEckpunkteChunk().getD().getBlock().setType(Material.TORCH);

        int anzahl = AnzahlGS.freebuild(p, modul);
        Log.SpigotLogger.Debug("anzahl=" + anzahl);
        if (anzahl <= modul.getRegionMarktConfig().getInt("gs_typ.freebuild.gratis_claims")) anzahl = 0;
        Log.SpigotLogger.Debug("anzahl_adj=" + anzahl);
        Log.SpigotLogger.Debug(String.valueOf(modul.getRegionMarktConfig().getInt("gs_typ.freebuild.gratis_claims")));
        int test = (int) ((0.1 * (anzahl * anzahl)) + (60 * anzahl) + 500);
        Log.SpigotLogger.Debug("wert=" + test);
    }

    //TODO: Formel in Config auslagern und Parser dafuer schreiben

    /**
     * Methode welche DefaultDomain.toPlayerString() zu verwertbaren Spielernamen/UUID umwandelt
     *
     * @param Input DefaultDomain-Owner String
     * @return Spielernamen des ersten Owners
     */
    private String erhalteErstenOwner(String Input) {
        String[] data;
        String temp;
        if (Input.contains(" ")) {
            data = Input.split(" ");
            temp = data[0].substring(data[0].lastIndexOf(":")).replace(":", "").replace(",", "");
            return temp;
        }
        temp = Input.substring(Input.lastIndexOf(":")).replace(":", "").replace(",", "");
        temp = SpielerLookup.SchliesseConnection.getSpielername(UUID.fromString(temp));
        return temp;
    }

    /**
     * Methode welche DefaultDomain.toPlayerString() zu verwertbaren Spielernamen/UUID umwandelt
     *
     * @param Input
     * @return - UUID des ersten Owners
     */
    private UUID erhalteErstenOwnerUUID(String Input) {
        String[] data;
        String temp;
        if (Input.contains(" ")) {
            data = Input.split(" ");
            temp = data[0].substring(data[0].lastIndexOf(":")).replace(":", "").replace(",", "");
            return UUID.fromString(temp);
        }
        temp = Input.substring(Input.lastIndexOf(":")).replace(":", "").replace(",", "");
        return UUID.fromString(temp);
    }

    /* Bestimmt GS Nummer
     * @param Plotname
     */
    private int plotNummer(String Plotname) {
        if (Plotname.contains("shop") || Plotname.contains("Shop")) {
            return Integer.parseInt(Plotname.substring(4));
        }
        return Integer.parseInt(Plotname.substring(5));
    }

    /**
     * Gibt alle Regionen von der Welt wieder, in welcher sich der Spieler befindet
     *
     * @param p - der Spieler
     * @return Region in welcher der Spieler sich befindet
     */
    private RegionManager erhalteRegionContainer(Player p) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(p.getWorld()));
    }

    /**
     * Methode zur Berechnung des Grundstückpreises
     *
     * @param Regionstyp (starter_gs, buerger_gs, ehrenlegion_gs, freebuild_gs, villen_gs, shop_gs)
     * @return Kalkulierter Grundstückspreis
     */
    private int berechnePreis(String Regionstyp, ProtectedRegion protectedRegion) {
        modul.getRegionMarktConfig().getBoolean("gs_typ." + Regionstyp + ".auto_preis");
        return (int) berechneSummePreisFlaeche(Regionstyp, protectedRegion);
    }

    private double berechneSummePreisFlaeche(String Regionstyp, ProtectedRegion protectedRegion) {
        double tempPreis = 0;
        double berechneterPreis = 0;
        if (Objects.requireNonNull(modul.getRegionMarktConfig().getString("gs_typ." + Regionstyp + ".metrik")).equals("m1")) {
            tempPreis = modul.getRegionMarktConfig().getInt("gs_typ." + Regionstyp + ".preis");
            RegionQuaderKanten berechneteKanten = RegionQuaderKanten.berechneKantenlaengenVonRegion(protectedRegion.getPoints());
            berechneterPreis = Math.floor(berechneterPreis * berechneteKanten.laengeKanteA);
            return berechneterPreis;
        }
        if (Objects.requireNonNull(modul.getRegionMarktConfig().getString("gs_typ." + Regionstyp + ".metrik")).equals("m2")) {
            tempPreis = modul.getRegionMarktConfig().getInt("gs_typ." + Regionstyp + ".preis");
            RegionQuaderKanten berechneteKanten = RegionQuaderKanten.berechneKantenlaengenVonRegion(protectedRegion.getPoints());
            berechneterPreis = Math.floor(tempPreis * berechneteKanten.laengeKanteA * berechneteKanten.laengeKanteB);
            return berechneterPreis;
        }
        if (Objects.requireNonNull(modul.getRegionMarktConfig().getString("gs_typ." + Regionstyp + ".metrik")).equals("m3")) {
            tempPreis = modul.getRegionMarktConfig().getInt("gs_typ." + Regionstyp + ".preis");
            double faktor = Double.parseDouble(Objects.requireNonNull(modul.getRegionMarktConfig().getString("gs_typ." + Regionstyp + ".m3_faktor")));
            berechneterPreis = tempPreis * protectedRegion.volume() * faktor;
            Log.SpigotLogger.Debug("bpeis=" + tempPreis + "volume=" + protectedRegion.volume() + " faktor=" + faktor + " berechnet=" + berechneterPreis);
            return berechneterPreis;
        } else {
            return 0;
        }
    }

    //TODO: Mietdauer Logik entwerfen
    private int berechneMietdauer(YamlConfiguration Config) {
        return 0;
    }

    private Boolean checkObGSServerGehoert(RegionsDatenSatz data) {
        return data.Owners.contains(WirtschaftsModul.Nationalbank.getSpielername()) || data.Owners.contains(WirtschaftsModul.Nationalbank.getUuid().toString());
    }

    /**
     * Prueft ob Spielername oder UUID im OwnerString der RegionenDatenSatz Klasse enthalten ist. Aus diesem Grund brauch ich in der RegioData auch keine DefaultDomain
     * da ich hier relativ einfach pruefen kann ob der String die UUID beinhaltet
     *
     * @param data Owner-String innerhalb der RegionenDatenSatz
     * @param p    Der Spieler
     * @return Wahr wenn Spieler in String enthalten
     */
    private Boolean spielerBesitztGrundstueck(RegionsDatenSatz data, Player p) {
        Log.SpigotLogger.Debug(data.Owners);
        return data.Owners.contains(p.getUniqueId().toString()) || data.Owners.contains(p.getDisplayName());
    }

    /**
     * Prueft, ob Grundstück in Hashmap vorhanden ist
     *
     * @param plotname - Name des Grundstücks, welches in der Hashmap abgefragt wird
     * @return true wenn Grundstück existiert
     */
    private Boolean grundstueckExistiert(String plotname) {
        return cachedGrundstuecke.containsKey(plotname);
    }

    /**
     * Prueft ob Grundstück in Hashmap vorhanden ist
     *
     * @param plotname - Prüft anhand des Namens, ob ein bestimmtes Grundstück aus der Datenbank geladen wurde
     * @return true wenn Grundstück existiert
     */
    private Boolean grundstueckExistiertDB(String plotname) {
        return cachedGrundstuecke.containsKey(plotname);
    }

    private Boolean hatTeamBerechtigung(Player p) {
        return p.hasPermission("caesreon.commands.team");
    }

    private Boolean besitztGenuegendGeld(Double preis, Player p) {
        double kontostandSchuldiger = modul.getMain().getMerkurKomponente().getServerWirtschaft().getCachedKontostand(p.getUniqueId());
        Log.SpigotLogger.Debug(String.valueOf(preis));
        Log.SpigotLogger.Debug(String.valueOf(kontostandSchuldiger));
        Log.SpigotLogger.Debug(String.valueOf(kontostandSchuldiger - preis));
        if ((kontostandSchuldiger - preis) >= 0) {
            return true;
        }
        fehlenderBetrag = (int) (preis - kontostandSchuldiger);
        return false;
    }

    private Boolean stehtZumVerkauf(RegionsDatenSatz data) {
        return data.Verkaufbar.equals("true") || data.Verkaufbar.equals("Ja");
    }

    /**
     * @param p Der Spieler, für welchen die Limits geprüft werden
     * @param data
     * @return true, wenn Limit überschritten wurde, false wenn x<Limit
     */
    private Boolean limitErreicht(Player p, RegionsDatenSatz data) {
        List<RegionsDatenSatz> Grundstuecke = LadeAlleGS.spielerIstOwnerAnhandGsTyp(p, data.plottyp);
        //Immer Wahr weil ein Administrator dies Bypassed
        if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
            return true;
        } else if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
            return Objects.requireNonNull(Grundstuecke).size() <= modul.getRegionMarktConfig().getInt("gs_typ." + data.plottyp + ".limit");
        } else if (p.hasPermission(modul.Permissions_Ehrenlegion)) {
            return Objects.requireNonNull(Grundstuecke).size() <= modul.getRegionMarktConfig().getInt("gs_typ." + data.plottyp + ".limit");
        } else if (p.hasPermission(modul.Permissions_Spieler)) {
            return Objects.requireNonNull(Grundstuecke).size() <= modul.getRegionMarktConfig().getInt("gs_typ." + data.plottyp + ".limit");
        }
        return false;
    }

    /**
     * Prüft, ob der Spieler die in einer Konfigurationsdatei hinterlegten Limits erreicht hat oder nicht
     *
     * @param p Der Spieler, für welchen die Limits geprüft werden
     * @param plottyp
     * @return true, wenn Limit überschritten wurde, false wenn x<Limit
     */
    private Boolean limitErreicht(Player p, String plottyp) {
        List<RegionsDatenSatz> Grundstuecke = LadeAlleGS.spielerIstOwnerAnhandGsTyp(p, plottyp);
        //Immer Wahr weil ein Administrator dies Bypassed
        if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
            return true;
        } else if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
            assert Grundstuecke != null;
            return Grundstuecke.size() <= modul.getRegionMarktConfig().getInt("gs_typ." + plottyp + ".limit");
        } else if (p.hasPermission(modul.Permissions_Ehrenlegion)) {
            assert Grundstuecke != null;
            return Grundstuecke.size() <= modul.getRegionMarktConfig().getInt("gs_typ." + plottyp + ".limit");
        } else if (p.hasPermission(modul.Permissions_Spieler)) {
            assert Grundstuecke != null;
            return Grundstuecke.size() <= modul.getRegionMarktConfig().getInt("gs_typ." + plottyp + ".limit");
        }
        return false;
    }

    private Boolean spielerValide(Player p, Spieler spieler) {
        if (spieler.getUuid() == null) {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Du musst einen Spieler angeben!");
            return false;
        }
        if (spieler.getUuid() != null) {
            return true;
        }
        return null;
    }

    private void pruefeDatenbank() {
        MySqlHandler.pruefeObDatenbankExistiert(ConnectionHandler.System(), RegionSQL.CreateTable);
    }

    /**
     * Klasse, welche die Anzahl der Grundstuecke ermittelt, welche ein Spieler besitzt
     *
     * @author Coriolanus_S
     */
    static class AnzahlGS {
        //
        protected static int freebuild(Player p, RegionMarktModul m) {
            List<RegionsDatenSatz> grundstuecke = LadeAlleGS.spielerIstOwnerAnhandGsTyp(p, RegionsTypen.freebuild);
            return emittelAnzahl(p, grundstuecke, m);
        }

        protected static int kl_buerger(Player p, RegionMarktModul m) {
            List<RegionsDatenSatz> grundstuecke = LadeAlleGS.spielerIstOwnerAnhandGsTyp(p, RegionsTypen.buerger_kl);
            return emittelAnzahl(p, grundstuecke, m);
        }

        protected static int gr_buerger(Player p, RegionMarktModul m) {
            List<RegionsDatenSatz> grundstuecke = LadeAlleGS.spielerIstOwnerAnhandGsTyp(p, RegionsTypen.buerger_gr);
            return emittelAnzahl(p, grundstuecke, m);
        }

        protected static int legion(Player p, RegionMarktModul m) {
            List<RegionsDatenSatz> grundstuecke = LadeAlleGS.spielerIstOwnerAnhandGsTyp(p, RegionsTypen.legion);
            return emittelAnzahl(p, grundstuecke, m);
        }

        protected static int villen(Player p, RegionMarktModul m) {
            List<RegionsDatenSatz> grundstuecke = LadeAlleGS.spielerIstOwnerAnhandGsTyp(p, RegionsTypen.villen);
            return emittelAnzahl(p, grundstuecke, m);
        }

        protected static int shop(Player p, RegionMarktModul m) {
            List<RegionsDatenSatz> grundstuecke = LadeAlleGS.spielerIstOwnerAnhandGsTyp(p, RegionsTypen.shop);
            return emittelAnzahl(p, grundstuecke, m);
        }

        protected static int starter(Player p, RegionMarktModul m) {
            List<RegionsDatenSatz> grundstuecke = LadeAlleGS.spielerIstOwnerAnhandGsTyp(p, RegionsTypen.starter);
            return emittelAnzahl(p, grundstuecke, m);
        }

        private static int emittelAnzahl(Player p, List<RegionsDatenSatz> grundstuecke, RegionMarktModul m) {
            if (grundstuecke == null) return 0;

            if (p.hasPermission(BasisBerechtigungsGruppen.Administrator)) {
                return grundstuecke.size();
            } else if (p.hasPermission(BasisBerechtigungsGruppen.Team)) {
                return grundstuecke.size();
            }

            //Ehrenlegion
            else if (p.hasPermission(m.Permissions_Ehrenlegion)) {
                return grundstuecke.size();
            }

            //Spieler
            else if (p.hasPermission(m.Permissions_Spieler)) {
                return grundstuecke.size();
            }
            return 0;
        }
    }

    static class LadeAlleGS {
        /**
         * Laed alle Grundstücke wo Spieler Owner ist
         *
         * @param p Der Spieler
         * @return Array mit GS-Namen
         */
        protected static List<RegionsDatenSatz> spielerIstOwnerAnhandGsTyp(Player p, String gstyp) {
            try {
                PreparedStatement preparedStatement;
                ResultSet result;
                Connection Con = ConnectionHandler.System();
                List<RegionsDatenSatz> data = new ArrayList<>();
                String sql = RegionSQL.Select_Region_Contains_Plottyp_And_Owner(p.getUniqueId().toString(), gstyp);
                Log.SpigotLogger.Debug(sql);
                preparedStatement = Con.prepareStatement(sql);
                result = MySqlHandler.executeQueryAndGetResult(preparedStatement);
                while (Objects.requireNonNull(result).next()) {
                    String[] temp = {String.valueOf(result.getString("plotname")), String.valueOf(result.getString("welt")),

                            String.valueOf(result.getString("vektor_min")), String.valueOf(result.getString("vektor_max")),

                            String.valueOf(result.getString("owners")), String.valueOf(result.getString("members")),

                            String.valueOf(result.getString("mietbar")), String.valueOf(result.getInt("verbleibende_mietdauer")), String.valueOf(result.getString("verkaufbar")), String.valueOf(result.getInt("preis")), String.valueOf(result.getLong("zeitstempel")), String.valueOf(result.getString("vektor_schild"))};
                    RegionsDatenSatz region = new RegionsDatenSatz(temp);
                    data.add(region);
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
                ConnectionHandler.closeConnection(Con);
                return data;
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return null;
        }

        /**
         * Laed alle Grundstücke wo Spieler Owner ist
         *
         * @param p Der Spieler
         * @return Array mit GS-Namen
         */
        protected static List<RegionsDatenSatz> spielerIstOwner(Player p) {
            try {
                PreparedStatement preparedStatement;
                ResultSet result;
                Connection Con = ConnectionHandler.System();
                List<RegionsDatenSatz> data = new ArrayList<>();
                String sql = RegionSQL.Select_Region_Contains_Owner;
                Log.SpigotLogger.Debug(sql);
                preparedStatement = Con.prepareStatement(sql);
                preparedStatement.setString(1, "%" + p.getUniqueId() + "%");
                result = MySqlHandler.executeQueryAndGetResult(preparedStatement);
                while (Objects.requireNonNull(result).next()) {
                    String[] temp = {String.valueOf(result.getString("plotname")), String.valueOf(result.getString("welt")),

                            String.valueOf(result.getString("vektor_min")), String.valueOf(result.getString("vektor_max")),

                            String.valueOf(result.getString("owners")), String.valueOf(result.getString("members")),

                            String.valueOf(result.getString("mietbar")), String.valueOf(result.getInt("verbleibende_mietdauer")), String.valueOf(result.getString("verkaufbar")), String.valueOf(result.getInt("preis")), String.valueOf(result.getLong("zeitstempel")), String.valueOf(result.getString("vektor_schild"))};
                    RegionsDatenSatz region = new RegionsDatenSatz(temp);
                    data.add(region);
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
                ConnectionHandler.closeConnection(Con);
                return data;
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return null;
        }

        /**
         * Methode um während der Entwicklung der GUI ein Testdatensatz zu generieren
         * @return
         */
        protected static List<RegionsDatenSatz> testDatensatz() {
            try {
                List<RegionsDatenSatz> data = new ArrayList<>();
                for (int i=0; i < 40; i++) {
                    String[] temp = {"legion_gs_" + i, "metav3rse",
                            "0", "0",
                            "thanos", "avengers",
                            "false", "0", "true", "200", "010101010", "0"};
                    RegionsDatenSatz region = new RegionsDatenSatz(temp);
                    data.add(region);
                }
                return data;
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return null;
        }

        /**
         * Laed alle Grundstücke in welcher der Spieler "Member" ist
         *
         * @param p Der Spieler
         * @return Array mit GS-Namen
         */
        protected static List<RegionsDatenSatz> spielerIstMember(Player p) {
            try {
                PreparedStatement preparedStatement;
                ResultSet result;
                Connection Con = ConnectionHandler.System();
                List<RegionsDatenSatz> data = new ArrayList<>();
                String sql = RegionSQL.Select_Region_Contains_Members;
                Log.SpigotLogger.Debug(sql);
                preparedStatement = Con.prepareStatement(sql);
                preparedStatement.setString(1, "%" + p.getUniqueId() + "%");
                result = MySqlHandler.executeQueryAndGetResult(preparedStatement);
                while (Objects.requireNonNull(result).next()) {
                    String[] temp = {String.valueOf(result.getString("plotname")), String.valueOf(result.getString("welt")),

                            String.valueOf(result.getString("vektor_min")), String.valueOf(result.getString("vektor_max")),

                            String.valueOf(result.getString("owners")), String.valueOf(result.getString("members")),

                            String.valueOf(result.getString("mietbar")), String.valueOf(result.getInt("verbleibende_mietdauer")), String.valueOf(result.getString("verkaufbar")), String.valueOf(result.getInt("preis")), String.valueOf(result.getLong("zeitstempel")), String.valueOf(result.getString("vektor_schild"))};

                    RegionsDatenSatz region = new RegionsDatenSatz(temp);
                    data.add(region);
                }
                MySqlHandler.schliesseResultUndPreparedStatement(result, preparedStatement);
                return data;
            } catch (Exception e) {
                Log.SpigotLogger.Debug(e.toString());
            }
            return null;
        }
    }

    protected static class Limits {
        protected static void administrator(Player p, RegionMarktModul modul) {
            basisLimitNachricht(p, modul);
        }

        protected static void team(Player p, RegionMarktModul modul) {
            basisLimitNachricht(p, modul);
        }

        protected static void ehrenlegion(Player p, RegionMarktModul modul) {
            basisLimitNachricht(p, modul);
        }

        protected static void buerger(Player p, RegionMarktModul modul) {
            basisLimitNachricht(p, modul);
        }

        /**
         * Spielerbenachrichtigung für die Joblimits
         *
         * @param p Der Spieler, welcher die Chat Nachricht erhalten soll
         * @param modul
         */
        private static void basisLimitNachricht(Player p, RegionMarktModul modul) {
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Starter-GS: " + ChatColor.DARK_GREEN + AnzahlGS.starter(p, modul) + "/" + modul.getRegionMarktConfig().getInt("gs_typ.starter_gs.limit"));
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Kleines Bürger-GS: " + ChatColor.DARK_GREEN + AnzahlGS.kl_buerger(p, modul) + "/" + modul.getRegionMarktConfig().getInt("gs_typ.kl_stadt_gs.limit"));
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Großes Bürger-GS: " + ChatColor.DARK_GREEN + AnzahlGS.gr_buerger(p, modul) + "/" + modul.getRegionMarktConfig().getInt("gs_typ.gr_stadt_gs.limit"));
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Villen-GS: " + ChatColor.DARK_GREEN + AnzahlGS.villen(p, modul) + "/" + modul.getRegionMarktConfig().getInt("gs_typ.villen_gs.limit"));
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Shop-GS: " + ChatColor.DARK_GREEN + AnzahlGS.shop(p, modul) + "/" + modul.getRegionMarktConfig().getInt("gs_typ.shop_gs.limit"));
            ChatSpigot.NachrichtSenden(p, modul.Prefix, "Freebuild-GS: " + ChatColor.DARK_GREEN + AnzahlGS.freebuild(p, modul) + "/" + modul.getRegionMarktConfig().getInt("gs_typ.freebuild.limit"));
        }
    }
}
