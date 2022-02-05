package caesreon.core.handlers;

import caesreon.core.system.IDENTIFIER;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Klasse welche zentralisiert die Konfigurationsdatei, den Dateipfad sowie den Dateinamen buendelt.
 * Genutzt wird diese Klasse durch den ConfigHandler und waehrend der Konfigurationsinitialisierungen
 * in den jeweiligen Modul Klassen.
 *
 * @author Coriolanus_S
 * @implNote Dateiendung (.yml) ist im Quellcode hinterlegt und darf nicht im Konstruktur fuer @param dateiName angebeben
 * werden. Der Getter gibt dann dateiName + dateiTyp, also bspw. config + .yml = config.yml
 */
public class KonfigurationsDatenSatz {
    private final String dateiTyp = ".yml";
    private File fileYML;
    private String ordner;
    private String ordnerAbsolut = "plugins" + AgentConfigHandler.FileSeperator(IDENTIFIER.PluginName.Caesreon.toString());
    private String dateiName;
    private int flag;
    private YamlConfiguration configuration;
    private Enum benoetigteOrdner;

    public KonfigurationsDatenSatz(File fileYML, String ordner, String dateiName, Enum benoetigteOrdner) {
        if (dateiName.contains(".yml"))
            dateiName = dateiName.replace(".yml", "");
        this.setFileYML(fileYML);
        this.setOrdner(ordner);
        this.setDateiName(dateiName + dateiTyp);
        this.benoetigteOrdner = benoetigteOrdner;
    }

    /**
     * @param fileYML
     * @param ordner
     * @param dateiName
     */
    public KonfigurationsDatenSatz(File fileYML, String ordner, String dateiName) {
        if (dateiName.contains(".yml"))
            dateiName = dateiName.replace(".yml", "");
        this.setFileYML(fileYML);
        this.setOrdner(ordner);
        this.setDateiName(dateiName + dateiTyp);
    }

    public File getFileYML() {
        return fileYML;
    }

    public void setFileYML(File fileYML) {
        this.fileYML = fileYML;
    }

    public String getOrdner() {
        return ordner;
    }

    public void setOrdner(String ordner) {
        this.ordner = ordner;
    }

    public String getOrdnerAbsolut() {
        return ordnerAbsolut;
    }

    public void setOrdnerAbsolut(String ordner) {
        this.ordnerAbsolut = ordner;
    }

    public String getDateiName() {
        return dateiName;
    }

    public void setDateiName(String dateiName) {
        this.dateiName = dateiName;
    }

    /**
     * Ordner + Pfad
     *
     * @return "Fully Qualified Name / Path" der jeweiligen Datei
     */
    public String getDateiPfadAbsolut() {
        return ordnerAbsolut + AgentConfigHandler.FileSeperator(dateiName);
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public YamlConfiguration getYamlConfiguration() {
        return configuration;
    }

    public void setYamlConfiguration(YamlConfiguration config) {
        this.configuration = config;
    }
}