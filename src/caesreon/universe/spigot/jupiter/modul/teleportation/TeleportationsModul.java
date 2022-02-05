package caesreon.universe.spigot.jupiter.modul.teleportation;

import caesreon.core.Log;
import caesreon.core.handlers.AgentConfigHandler;
import caesreon.core.handlers.KonfigurationsDatenSatz;
import caesreon.universe.spigot.jupiter.JupiterKomponente;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeleportationsModul {
    protected final int erlaubterZufallsTeleportRadius;
    public List<String> erlaubteWelten;
    String Prefix;
    String Regex;
    private final JupiterKomponente modul;
    private final YamlConfiguration config;
    private final TeleportationsHandler handler;

    public TeleportationsModul(JupiterKomponente modul) {
        this.modul = modul;

        Log.SpigotLogger.Init("Modul: Initialisiere Modul Teleportation..");

        // Konfiguration laden
        KonfigurationsDatenSatz regionMarktConfigSet = new KonfigurationsDatenSatz(null,
                AgentConfigHandler.RootOrdner.Modulkonfigurationen +
                        AgentConfigHandler.FileSeperator("Jupiter"), "TeleportSettings");
        config = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(regionMarktConfigSet);

        this.erlaubterZufallsTeleportRadius = getConfig().getInt("zufallsteleport.erlaubter_radius");
        this.Regex = getConfig().getString("allgemein.regex");
        this.Prefix = getConfig().getString("allgemein.prefix");
        erlaubteWelten = getConfig().getStringList("erlaubte_welten");

        // Handler erstellen
        handler = new TeleportationsHandler(this);

        // Commands aktivieren
        PluginCommand command = modul.getMain().getCommand("ctp");
        assert command != null;
        Log.SpigotLogger.Info("Registriere /ctp");
        command.setTabCompleter((sender, command1, label, args) -> {
            if (args.length == 1)
            {
                List<String> completions = new ArrayList<>();
                completions.add("z");
                completions.add("zufall");
                return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
            } else {
                return Collections.emptyList();
            }

        });
        command.setExecutor(new TeleportationsCommand(this));
        Log.SpigotLogger.InitErfolgreich("Modul: Teleportation erfolgreich initialisiert");
    }

    public TeleportationsHandler getTeleportationsHandler() {
        return handler;
    }

    public JupiterKomponente getJupiterKomponente() {
        return modul;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
