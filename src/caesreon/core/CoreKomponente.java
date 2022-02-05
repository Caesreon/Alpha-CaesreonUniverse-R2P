package caesreon.core;

import caesreon.core.listener.*;
import caesreon.core.handlers.*;
import caesreon.core.skynet.SkyNetModul;
import caesreon.core.system.IDENTIFIER;
import caesreon.main.SpigotMain;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class CoreKomponente {
    private static FileConfiguration standardKonfiguration;
    private static DependencyHandler dependencyHandler;
    private static AgentConfigHandler configHandler;
    private static MySqlHandler mySqlHandler;
    private static ScoreboardDataHandler sDH;
    private final SpigotMain main;
    private static Enum<RuntimeModus> _runtimeModus = RuntimeModus.SPIGOT;
    private ServerShutdown serverShutdown;
    private SkyNetModul skynetModul;

    public CoreKomponente(SpigotMain m) {
        main = m;

        Log.SpigotLogger.Init("Initialisiere Core Komponente..");
        try {
            KonfigurationsDatenSatz defaultConfigSet = new KonfigurationsDatenSatz(null, null, "config");
            standardKonfiguration = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(defaultConfigSet);

            dependencyHandler = new DependencyHandler(this);
            configHandler = new AgentConfigHandler(m);
            mySqlHandler = new MySqlHandler();

            //if (getDefaultConfig().getBoolean("debug"))
            //	Log.setLevel(Level.ALL);
            skynetModul = new SkyNetModul(this);
            serverShutdown = new ServerShutdown(this);

            sDH = new ScoreboardDataHandler(this);

            Log.SpigotLogger.Init("Registriere Core Events und Listener");
            main.getServer().getPluginManager().registerEvents(new FirstJoin(this), main);
            main.getServer().getPluginManager().registerEvents(new PlayerConnect(this), main);
            main.getServer().getPluginManager().registerEvents(new PlayerDisconnect(this), main);

            if (Objects.equals(standardKonfiguration.getString("bungeecord"), "true")) {
                _runtimeModus = RuntimeModus.BUNGEECORD;
            }

            if (istBungeecordModus()) {
                Log.SpigotLogger.Dev("Multi-Server Betrieb=true");
                main.getServer().getMessenger().registerOutgoingPluginChannel(main, IDENTIFIER.Namespace.caesreon + ":" + IDENTIFIER.PluginChannel.cae_sys);
                main.getServer().getMessenger().registerIncomingPluginChannel(main, IDENTIFIER.Namespace.caesreon + ":" + IDENTIFIER.PluginChannel.cae_sys, new BungeeListener());
            }

            //Diesen Befehl nur entkommentieren wenn die Server-Economy Datenbank importiert werden muss
            //main.getInstance().getCommand("import-database").setExecutor(new EconomyBridge(main));

            Log.SpigotLogger.Debug("Main.Main(): Debugging aktiviert");
            Log.SpigotLogger.InitErfolgreich("Core erfolgreich initialisiert");
        } catch (Exception e) {
            Log.SpigotLogger.Error("Komponente konnte nicht initialisiert werden");
            Log.SpigotLogger.Error(e.toString());
        }

    }

    public AgentConfigHandler getConfigHandler() {
        return configHandler;
    }

    public DependencyHandler getDependencyHandler() {
        return dependencyHandler;
    }

    public MySqlHandler getMySqlHandler() {
        return mySqlHandler;
    }

    public ScoreboardDataHandler getScoreBoardDataHandler() {
        return sDH;
    }

    public SkyNetModul getSkynetModul() {
        return skynetModul;
    }

    public SpigotMain getMain() {
        return main;
    }

    public static FileConfiguration getDefaultConfig() {
        return standardKonfiguration;
    }

    public ServerShutdown getServerShutdown() {
        return serverShutdown;
    }

    public static Boolean istBungeecordModus() {
        return Objects.equals(_runtimeModus, RuntimeModus.BUNGEECORD);
    }
}
