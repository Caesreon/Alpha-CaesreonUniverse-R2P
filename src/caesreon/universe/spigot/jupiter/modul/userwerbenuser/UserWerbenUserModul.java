package caesreon.universe.spigot.jupiter.modul.userwerbenuser;

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

public class UserWerbenUserModul {
    private final JupiterKomponente modul;
    public String Prefix;
    private final UserWerbenUserHandler handler;
    private final UserWerbenUserSQL sql;
    private final YamlConfiguration config;


    public UserWerbenUserModul(JupiterKomponente modul) {
        this.modul = modul;
        Log.SpigotLogger.Init("Modul: Initialisiere Modul User-Werben-User..");

        // Konfiguration laden
        KonfigurationsDatenSatz userWerbenUserConfigSet = new KonfigurationsDatenSatz(null,
                AgentConfigHandler.RootOrdner.Modulkonfigurationen +
                        AgentConfigHandler.FileSeperator("Jupiter"), "UserWerbenUserSettings");
        config = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(userWerbenUserConfigSet);

        this.Prefix = config.getString("prefix");
        // Handler erstellen
        sql = new UserWerbenUserSQL(this);
        handler = new UserWerbenUserHandler(this);
        // Listener aktivieren
        aktiviereListenerAnhandConfig(config.getBoolean("aktiviert"));
        Log.SpigotLogger.InitErfolgreich("Modul: User-Werben-User erfolgreich initialisiert");
    }

    private void aktiviereListenerAnhandConfig(Boolean aktiviert) {
        if (aktiviert) {
            try {
                //Commands aktivieren
                PluginCommand command = getMain().getCommand("uwu");
                assert command != null;
                command.setTabCompleter((sender, command1, label, args) -> {
                    if (args.length == 1)
                    {
                        List<String> completions = new ArrayList<>();
                        completions.add("code erstellen");
                        completions.add("code einlösen");
                        return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
                    } else {
                        return Collections.emptyList();
                    }
                });
                command.setExecutor(new UserWerbenUserCommand(this));

            } catch (Exception e) {
            }

            //m.LoadModule();
            Log.SpigotLogger.InitErfolgreich("Modul User Werben User aktiviert");
            return;
        }
        Log.SpigotLogger.Info("Modul User-Werben-User deaktiviert");
    }

    public SpigotMain getMain() {
        return modul.getMain();
    }

    public UserWerbenUserHandler getUwUHandler() {
        return handler;
    }

    public YamlConfiguration getUwUConfig() {
        return config;
    }

    public UserWerbenUserSQL getUWU() {
        return sql;
    }
}
