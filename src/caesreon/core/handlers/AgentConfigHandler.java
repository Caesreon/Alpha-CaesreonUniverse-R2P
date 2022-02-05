package caesreon.core.handlers;

import caesreon.core.CoreKomponente;
import caesreon.core.Log;
import caesreon.core.RuntimeModus;
import caesreon.core.system.IDENTIFIER;
import caesreon.core.hilfsklassen.FileUT;
import caesreon.main.SpigotMain;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * TODO: Reload Command fuer bereits geladene Konfigurationen
 */
//Wichtig:
//Debug Logger kann hier nicht aufgerufen weil logischerweise hier erst gelesen wird ob der gewünscht ist. Ansonsten Nullpointer
public class AgentConfigHandler {
    public static final String rootOrdnerAbsolut = "plugins" + FileSeperator(IDENTIFIER.PluginName.Caesreon.toString());
    private final SpigotMain main;
    private List<KonfigurationsDatenSatz> Konfigurationen;

    public AgentConfigHandler(SpigotMain main) {
        this.main = main;
        SpigotMain.getSpigotMainInstance().saveDefaultConfig();
        CheckOrdner();
    }

    public static File leseKonfigurationsDatei(KonfigurationsDatenSatz set) {
        //NPE Validierung des Dateipfades
        set = pruefeObPfadNullFuerDefaultKonfig(set);
        assert set != null;
        Log.SpigotLogger.Debug(set.getDateiPfadAbsolut());
        set.setFileYML(new File(set.getDateiPfadAbsolut()));

        if (!set.getFileYML().exists()) {
            Log.SpigotLogger.Debug("pruefeObResourceDateiVorhanden().if(!set.getFileYML().exists())");
            Log.SpigotLogger.Warning("Konfigurationsdatei " + set.getDateiName() + " konnte nicht gefunden werden. Neue Datei wird erstellt...");

            if (set.getOrdner() != null) {
                SpigotMain.getSpigotMainInstance().saveResource(set.getOrdner() + FileSeperator(set.getDateiName()), false);
                Log.SpigotLogger.Debug("pruefeObResourceDateiVorhanden().if(!Ordner==NPE)");
                return set.getFileYML();
            } else if (set.getOrdner() == null) {
                SpigotMain.getSpigotMainInstance().saveResource(set.getDateiName(), false);
                Log.SpigotLogger.Debug("pruefeObResourceDateiVorhanden().if(Ordner== NULL)");
                return set.getFileYML();
            }
        }
        Log.SpigotLogger.Debug("pruefeObResourceDateiVorhanden().if(set.getFileYML().exists()) TRUE");
        return set.getFileYML();
    }

    /**
     * Prueft ob Ordner NULL ist und wandelt relativen Pfad zu absoluten Pfad um
     *
     * @param set - KonfigurationsDatenSatz in welchem der Pfad enthalten ist
     * @return - KonfigurationsDatenSatz welcher modifiziert werden sollte
     * @see FileUT
     */
    public static KonfigurationsDatenSatz pruefeObPfadNullFuerDefaultKonfig(KonfigurationsDatenSatz set) {
        if (set.getOrdner() != null) {
            Log.SpigotLogger.Debug("pruefeObPfadNullFuerDefaultKonfig().set.getOrdner != NULL");
            if (set.getOrdner().contains(rootOrdnerAbsolut)) {
                //Dieser Fall kann ueber die FileUT eintreten da dort bspw. der absolute Pfad ueber den relativen Ordner mitgegeben wird
                set.setOrdnerAbsolut(set.getOrdner());
                return set;
            }
            set.setOrdnerAbsolut(rootOrdnerAbsolut + FileSeperator(set.getOrdner()));
            return set;
        } else if (set.getOrdner() == null) {
            Log.SpigotLogger.Debug("pruefeObPfadNullFuerDefaultKonfig().set.getOrdner == NULL");
            Log.SpigotLogger.Debug("Neuer Pfad = " + rootOrdnerAbsolut);
            set.setOrdnerAbsolut(rootOrdnerAbsolut);
            return set;
        }
        return null;
    }

    /**
     * @param set
     * @return
     * @implNote Bottleneck, der kleinste Fehler an dieser Stelle bedeutet ein fatales Systemversagen aller Komponenten muss set.getFileYML() == null sein da
     * !set.getFileYML().exists() unweigerlich zu einer NPE fuehrt!
     */
    public static YamlConfiguration getYamlConfigurationDurchKonfigurationsDatenSatz(KonfigurationsDatenSatz set) {
        if (set.getFileYML() == null)
            set.setFileYML(leseKonfigurationsDatei(set));

        YamlConfiguration temp = YamlConfiguration.loadConfiguration(set.getFileYML());
        if (temp != null)
            Log.SpigotLogger.Info("Konfigurationsdatei: " + ChatColor.GREEN + set.getDateiName() + ChatColor.WHITE + " erfolgreich geladen");
        else if (temp == null)
            Log.SpigotLogger.Warning("Konfigurationsdatei: " + ChatColor.RED + set.getDateiName() + ChatColor.WHITE + " nicht erfolgreich geladen");
        return temp;
    }

    public static String FileSeperator(String FileName) {
        return File.separator + FileName;
    }

    //TODO: wird uberarbeitet, modul muss ueber den reload informiert werden
    public void reloadKonfigurationen() {

    }

    /**
     * Diese Methode soll bei erfolgreichem laden einer Konfiguration den Datensatz volatil
     * in einer Liste speichern
     * TODO: wird uberarbeitet, modul muss ueber den reload informiert werden
     *
     * @param set
     */
    private void fuegeKonfigurationsDatenSatzListeHinzu(KonfigurationsDatenSatz set) {
        if (Konfigurationen == null)
            Konfigurationen = new ArrayList<>();
        Konfigurationen.add(set);
    }

    /**
     * TODO: in jeweilige Module auslagern
     * Prueft ob notwendige Ordner vorhanden sind.
     */
    private void CheckOrdner() {
        checkPluginOrdnerVorhanden();
        CheckOrdnerVorhanden(RootOrdner.Modulkonfigurationen.toString());
        CheckOrdnerVorhanden(RootOrdner.Menü.toString());
        CheckOrdnerVorhanden(RootOrdner.Bücher.toString());
        CheckOrdnerVorhanden(RootOrdner.Modulkonfigurationen + FileSeperator("Jupiter"));
        CheckOrdnerVorhanden(RootOrdner.Modulkonfigurationen + FileSeperator("Merkur") + FileSeperator("Berufe"));
        CheckOrdnerVorhanden(RootOrdner.Modulkonfigurationen + FileSeperator("Merkur") + FileSeperator("Berufe") + FileSeperator("BerufKonfigurationen"));
        CheckOrdnerVorhanden(RootOrdner.Modulkonfigurationen + FileSeperator("Merkur") + FileSeperator("Skills"));
    }

    /**
     * Prüft ob Root verzeichnis vorhanden ist und erstellt dieses neu wenn nicht vorhanden.
     */
    private void checkPluginOrdnerVorhanden() {
        File Ordner = new File("plugins" + FileSeperator(IDENTIFIER.PluginName.CaesreonUniverse.toString()));
        if (!Ordner.exists()) {
            Log.JavaLogger.Warning("Ordner " + Ordner + " konnte nicht gefunden werden. Ordner wird erstellt...");
            Ordner.mkdir();
        }
    }

    /**
     * Prüft ob Plugin Root verzeichnis vorhanden ist und erstellt dieses neu wenn nicht vorhanden.
     */
    public static void checkPluginRootOrdner(String rootOrdnerName) {
        File Ordner = new File("plugins" + FileSeperator(rootOrdnerName));
        if (!Ordner.exists()) {
            if (!CoreKomponente.istBungeecordModus()){
                Log.SpigotLogger.Warning("Ordner " + Ordner + " konnte nicht gefunden werden. Ordner wird erstellt...");
            } else {
                Log.BungeeLogger.Warning("Ordner " + Ordner + " konnte nicht gefunden werden. Ordner wird erstellt...");
            }
            Ordner.mkdir();
        }
    }

    /**
     * Prüft ob Ordner in Pluginordner vorhanden ist und erstellt diesen neu wenn nicht vorhanden
     *
     * @param Name
     */
    private void CheckOrdnerVorhanden(String Name) {
        File Ordner = new File(rootOrdnerAbsolut + FileSeperator(Name));
        if (!Ordner.exists()) {
            Log.SpigotLogger.Warning("Ordner " + Ordner + " konnte nicht gefunden werden. Ordner wird erstellt...");
            Ordner.mkdir();
        }
    }

    /**
     * Prüft ob Konfigurationsdatei vorhanden ist bei der Initialisierung der Anwendung
     *
     * @return File
     */
    private File pruefeUndLeseDefaultKonfiguration() {
        File FileYML = new File("plugins" + FileSeperator(IDENTIFIER.PluginName.CaesreonUniverse.toString()) + FileSeperator("config.yml"));
        if (!FileYML.exists()) {
            Log.SpigotLogger.Warning("Konfigurationsdatei " + "config.yml" + " konnte nicht gefunden werden. Neue Datei wird erstellt...");
            saveDefaultConfig();
        }
        return FileYML;
    }

    private void saveDefaultConfig() {
        main.saveDefaultConfig();
    }

    public enum RootOrdner {Modulkonfigurationen, Bücher, Menü, BerufKonfigurationen}

}