package caesreon.universe.spigot.jupiter.modul.fly;

import caesreon.core.COMMANDS;
import caesreon.core.Log;
import caesreon.core.handlers.AgentConfigHandler;
import caesreon.core.handlers.KonfigurationsDatenSatz;
import caesreon.universe.spigot.jupiter.JupiterKomponente;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class FlyModul {
    public List<String> verboteneWelten;
    String Prefix;
    String Regex;
    private final JupiterKomponente modul;
    private final YamlConfiguration flyConfig;
    private final FlyHandler flyHandler;

    public FlyModul(JupiterKomponente modul) {
        this.modul = modul;
        Log.SpigotLogger.Init("Modul: Initialisiere Modul Fly");
        KonfigurationsDatenSatz flyConfigSet = new KonfigurationsDatenSatz(null, AgentConfigHandler.RootOrdner.Modulkonfigurationen
                + AgentConfigHandler.FileSeperator("Jupiter"), "Fly");
        flyConfig = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(flyConfigSet);

        this.Regex = flyConfig.getString("allgemein.regex");
        this.Prefix = flyConfig.getString("allgemein.prefix");
        verboteneWelten = flyConfig.getStringList("verbotene_welten");

        flyHandler = new FlyHandler(this, modul.getMain().getPermissions());

        submodul_Fly(flyConfig.getBoolean("allgemein.aktiviert"));
        Log.SpigotLogger.InitErfolgreich("Modul: Fly aktiviert");
    }

    private void submodul_Fly(Boolean aktiviert) {
        if (aktiviert) {
            for (COMMANDS.FLY i : COMMANDS.FLY.values()) {
                try {
                    Log.SpigotLogger.Info("Registriere /fly" + i.toString());
                    // Commands aktivieren
                    PluginCommand command = modul.getMain().getCommand("fly");
                    assert command != null;
                    command.setTabCompleter((sender, command1, label, args) -> {
                        List<String> completions = new ArrayList<>();
                        return completions;
                    });
                    command.setExecutor(new FlyCommand(this));
                } catch (Exception e) {
                    Log.SpigotLogger.Debug(e.toString());
                }
            }
        }
    }

    public JupiterKomponente getJupiterKomponente() {
        return modul;
    }

    public FlyHandler getFlyHandler() {
        return flyHandler;
    }

    public YamlConfiguration getFlyConfig() {
        return flyConfig;
    }

}
