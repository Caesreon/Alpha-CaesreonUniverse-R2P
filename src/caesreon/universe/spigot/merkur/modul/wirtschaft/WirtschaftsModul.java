package caesreon.universe.spigot.merkur.modul.wirtschaft;

import caesreon.core.Log;
import caesreon.core.handlers.AgentConfigHandler;
import caesreon.core.handlers.KonfigurationsDatenSatz;
import caesreon.core.vSpieler;
import caesreon.main.SpigotMain;
import caesreon.universe.spigot.merkur.MerkurKomponente;
import caesreon.universe.spigot.merkur.modul.wirtschaft.handler.WirtschaftsDatenHandler;
import caesreon.universe.spigot.merkur.modul.wirtschaft.handler.WirtschaftsHandler;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.StringUtil;

import java.util.*;

//todo: wirtschaftsmodul an das neue System anpassen
public class WirtschaftsModul {
    public static vSpieler Nationalbank;
    //Anmerkung: YamlConfiguration hier hier static, da dies im MySQLHandler f�r den EconomyConnector genutzt wird
    public static YamlConfiguration WirtschaftsKonfiguration;
    public final String prefix;
    public final String staatskasse_uuid;
    public String bankPrefix;
    String waehrungsName;
    private WirtschaftsSQLStatements wirtschaftsSQLStatements;
    private WirtschaftsDatenHandler wirtschaftDatenHandler;
    private WirtschaftBackgroundTask backgroundTaskWirtschaft;
    private WirtschaftsHandler wirtschaftMySQLHandler;
    private Wirtschaft serverWirtschaft;
    private final MerkurKomponente mak;

    public WirtschaftsModul(MerkurKomponente mak) {
        Log.SpigotLogger.Init("Modul: Initialisiere Wirtschaft..");
        this.mak = mak;
        // Konfigurationen laden
        KonfigurationsDatenSatz wirtschaftConfigSet = new KonfigurationsDatenSatz(null, AgentConfigHandler.RootOrdner.Modulkonfigurationen
                + AgentConfigHandler.FileSeperator("Merkur"), "WirtschaftsSettings");
        WirtschaftsKonfiguration = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(wirtschaftConfigSet);

        //Handler
        wirtschaftsSQLStatements = new WirtschaftsSQLStatements(this);
        wirtschaftDatenHandler = new WirtschaftsDatenHandler(this);
        wirtschaftMySQLHandler = new WirtschaftsHandler(this);
        backgroundTaskWirtschaft = new WirtschaftBackgroundTask(this);
        serverWirtschaft = new Wirtschaft(this);

        //Zuweisungen
        prefix = WirtschaftsKonfiguration.getString("allgemein.prefix.bank");
        bankPrefix = WirtschaftsKonfiguration.getString("allgemein.prefix.bank");
        staatskasse_uuid = WirtschaftsKonfiguration.getString("serverbank.vUUID");
        Nationalbank = new vSpieler(WirtschaftsKonfiguration.getString("serverbank.name"), UUID.fromString(Objects.requireNonNull(WirtschaftsKonfiguration.getString("serverbank.vUUID"))));
        waehrungsName = WirtschaftsKonfiguration.getString("waehrung.name");

        //Befehle
        Log.SpigotLogger.Init("Registriere /geld {...}");
        // Commands aktivieren
        PluginCommand command = mak.getMain().getCommand("geld");
        assert command != null;
        command.setAliases(List.of("g"));
        command.setTabCompleter((sender, command1, label, args) -> {
            if (args.length == 1)
            {
                List<String> completions = new ArrayList<>();
                completions.add("senden");
                completions.add("top");
                return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
            } else {
                return Collections.emptyList();
            }
        });
        command.setExecutor(new WirtschaftsCommands(this));
        Log.SpigotLogger.InitErfolgreich("Modul: Wirtschaft erfolgreich initialisiert");
    }


    public SpigotMain getMain() {
        return mak.getMain();
    }

    public Wirtschaft getServerWirtschaft() {
        return serverWirtschaft;
    }

    public WirtschaftsHandler getEconomySQLHandler() {
        return wirtschaftMySQLHandler;
    }

    public WirtschaftsSQLStatements getEconomyBridge() {
        return wirtschaftsSQLStatements;
    }
}
