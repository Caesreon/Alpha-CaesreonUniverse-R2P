package caesreon.universe.spigot.jupiter.modul.loginbelohnungen;

import caesreon.core.Log;
import caesreon.core.handlers.AgentConfigHandler;
import caesreon.core.handlers.KonfigurationsDatenSatz;
import caesreon.universe.spigot.jupiter.JupiterKomponente;
import caesreon.main.SpigotMain;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Zentrale Anlaufstelle fuer die Belohnungen
 */
public class BelohnungenModul {
    public String Prefix;
    public String Regex;
    private final  JupiterKomponente modul;
    private final BelohnungsManager manager;
    private final BelohnungsHandler handler;
    private final YamlConfiguration config;

    public BelohnungenModul(JupiterKomponente modul) {
        Log.SpigotLogger.Init("Modul: Initialisiere Modul Login-Belohnungen...");
        this.modul = modul;
        KonfigurationsDatenSatz loginBelohnungenConfigSet = new KonfigurationsDatenSatz(null, AgentConfigHandler.RootOrdner.Modulkonfigurationen
                + AgentConfigHandler.FileSeperator("Jupiter"), "LoginBelohnungen");
        config = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(loginBelohnungenConfigSet);

        this.Prefix = config.getString("allgemein.prefix");
        this.Regex = config.getString("allgemein.regex");


        manager = new BelohnungsManager(this);            // Manager wird nur 1x erstellt und Belohnungen werden einmalig gelesen
        //TODO Comment:
        // Der Konstruktor von "Belohnungen" koennte auch die GUI (Tags/Monatsbelohnung) direkt erstellen und bei getGUI nur den Tag pruefen
        // -> halte ich aber fuer unnoetig. Das wuerde Datenbankzugriffe sparen, aber andererseits den Server RAM kosten. Da es wohl eher
        // selten auftritt, dass sich Leute die Belohnungen anschauen, ist der DB Zugriff und GUI Aufbau wahrscheinlich kostenguenstiger,
        // als alles im Speicher zu halten)

        handler = new BelohnungsHandler(this);

        //Listener
        aktiviereListenerAnhandConfig(config.getBoolean("allgemein.aktiviert"));
        Log.SpigotLogger.InitErfolgreich("Modul: Login-Belohnungen erfolgreich initialisiert");
    }

    public SpigotMain getMain() {
        return modul.getMain();
    }

    public YamlConfiguration getBelohnungsConfig() {
        return config;
    }

    public BelohnungsHandler getBelohnungsHandler() {
        return handler;
    }

    public BelohnungsManager getBelohnungsManager() {
        return manager;
    }

    private void aktiviereListenerAnhandConfig(Boolean aktiviert) {
        if (aktiviert) {
            Log.SpigotLogger.Info("Registriere /belohnung");
            // Commands aktivieren
            PluginCommand command = modul.getMain().getCommand("belohnung");
            assert command != null;
            command.setTabCompleter((sender, command1, label, args) -> {
                if(args.length == 1)
                {
                    List<String> completions = new ArrayList<>();
                    completions.add("einlösen");
                    completions.add("heute");
                    completions.add("monat");
                    return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
                } else {
                    return Collections.emptyList();
                }
            });
            command.setExecutor(new BelohnungenCommand(this));
            modul.getMain().getServer().getPluginManager().registerEvents(new BelohnungsMenuManager(this), modul.getMain());
        }
    }


}
