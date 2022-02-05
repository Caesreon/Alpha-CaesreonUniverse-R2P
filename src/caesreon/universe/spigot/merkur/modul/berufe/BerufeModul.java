package caesreon.universe.spigot.merkur.modul.berufe;

import caesreon.core.Log;
import caesreon.core.handlers.AgentConfigHandler;
import caesreon.core.handlers.KonfigurationsDatenSatz;
import caesreon.main.SpigotMain;
import caesreon.universe.spigot.merkur.MerkurKomponente;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BerufeModul {
    protected static String Prefix;
    private final YamlConfiguration allgemeineBerufeConfig;
    private final YamlConfiguration skillsConfig;

    //Hier soll es hingehen, damit die Ordner auch final vom ConfigHandler weg sind. Ist aber noch nicht in use!!
    //Wird aber teilweise in den erbenden Klassen genutzt
    private final MerkurKomponente ankermodul;
    private final BerufeHandler berufeHandler;
    private final BerufeSQL berufeSQL;
    public String Regex;
    protected List<String> geblockteItems;
    protected String trenner = "===============";
    protected String berufeKonfigurationenPfad = AgentConfigHandler.rootOrdnerAbsolut
            + AgentConfigHandler.FileSeperator(AgentConfigHandler.RootOrdner.Modulkonfigurationen.toString())
            + AgentConfigHandler.FileSeperator("Merkur")
            + AgentConfigHandler.FileSeperator(konfigurationsOrdner.Berufe.toString())
            + AgentConfigHandler.FileSeperator(konfigurationsOrdner.BerufKonfigurationen.toString());

    //Berufe Schilder
    String Identifier = "[Berufe]";
    String Info = "Info";

    //Permission:
    String permissionNode = "caesreon.commands.spieler.beruf.verdienst";

    public BerufeModul(MerkurKomponente ankermodul) {
        this.ankermodul = ankermodul;
        Log.SpigotLogger.Init("Modul: Initialisiere Modul Berufe...");

        // Konfiguration laden
        KonfigurationsDatenSatz berufeAllgemeinConfigSet = new KonfigurationsDatenSatz(
                null,
                AgentConfigHandler.RootOrdner.Modulkonfigurationen +
                        AgentConfigHandler.FileSeperator("Merkur") +
                        AgentConfigHandler.FileSeperator(konfigurationsOrdner.Berufe.toString()),
                konfigurationsDateien.BerufeAllgemeinSettings.toString());
        KonfigurationsDatenSatz skillsAllgemeinConfigSet = new KonfigurationsDatenSatz(
                null,
                AgentConfigHandler.RootOrdner.Modulkonfigurationen +
                        AgentConfigHandler.FileSeperator("Merkur") +
                        AgentConfigHandler.FileSeperator(konfigurationsOrdner.Skills.toString()),
                konfigurationsDateien.SkillsAllgemeinSettings.toString());

        allgemeineBerufeConfig = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(berufeAllgemeinConfigSet);
        skillsConfig = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(skillsAllgemeinConfigSet);

        this.Regex = allgemeineBerufeConfig.getString("allgemein.regex");
        Prefix = allgemeineBerufeConfig.getString("allgemein.prefix");
        this.geblockteItems = allgemeineBerufeConfig.getStringList("geblockte_items");

        // Handler erstellen
        berufeHandler = new BerufeHandler(this);
        berufeSQL = new BerufeSQL();

        // Commands aktivieren
        PluginCommand command = ankermodul.getMain().getCommand("beruf");
        assert command != null;
        command.setAliases(List.of("b"));
        command.setTabCompleter((sender, command1, label, args) -> {
            if (args.length == 1)
            {
            List<String> completions = new ArrayList<>();
            completions.add("liste");
            completions.add("stats");
                return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
            } else {
                return Collections.emptyList();
            }
        });
        command.setExecutor(new BerufeCommands(this));

        // Listener aktivieren
        ankermodul.getMain().getServer().getPluginManager().registerEvents(new AktionsListener(this), ankermodul.getMain());

        Log.SpigotLogger.InitErfolgreich("Modul: Berufe erfolgreich initialisiert");
    }

    public SpigotMain getMain() {
        return ankermodul.getMain();
    }

    public MerkurKomponente getKomponente() {
        return ankermodul;
    }

    public BerufeHandler getBerufeHandler() {
        return berufeHandler;
    }

    public YamlConfiguration getBerufeKonfig() {
        return allgemeineBerufeConfig;
    }

    public BerufeSQL getBerufeSQL() {
        return berufeSQL;
    }

    enum Handlungen {bauen, abbauen, zaehmen, toeten, craften}

    protected enum konfigurationsOrdner {Berufe, Skills, BerufKonfigurationen}

    protected enum konfigurationsDateien {BerufeAllgemeinSettings, SkillsAllgemeinSettings}
}
