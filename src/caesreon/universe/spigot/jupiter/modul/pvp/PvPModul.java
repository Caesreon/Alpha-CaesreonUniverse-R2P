package caesreon.universe.spigot.jupiter.modul.pvp;

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

public class PvPModul {
    protected List<String> erlaubteWelten;
    protected List<String> pvpSpieler;
    protected String Prefix;
    protected KonfigurationsDatenSatz pvpConfigSet;
    JupiterKomponente modul;
    PvPHandler pvpHandler;
    private final YamlConfiguration pvpConfig;
    private String regex;

    public PvPModul(JupiterKomponente modul) {
        Log.SpigotLogger.Init("Modul: Initialisiere Modul Fly");
        pvpConfigSet = new KonfigurationsDatenSatz(null, AgentConfigHandler.RootOrdner.Modulkonfigurationen
                + AgentConfigHandler.FileSeperator("Jupiter"), "PVP");

        pvpConfig = AgentConfigHandler.getYamlConfigurationDurchKonfigurationsDatenSatz(pvpConfigSet);

        erlaubteWelten = getPvpConfig().getStringList("erlaubte_welten");
        pvpSpieler = getPvpConfig().getStringList("pvp_spieler");
        if (pvpSpieler == null) {
            Log.SpigotLogger.Debug("PvP Spieler Liste war NULL");
            pvpSpieler = new ArrayList<String>();
        }
        Prefix = getPvpConfig().getString("allgemein.prefix");
        regex = getPvpConfig().getString("allgemein.regex");
        boolean aktiviert = getPvpConfig().getBoolean("allgemein.aktiviert");

        if (aktiviert) {
            //Handler
            pvpHandler = new PvPHandler(this);
            // Commands aktivieren
            PluginCommand command = modul.getMain().getCommand("pvp");
            assert command != null;
            command.setTabCompleter((sender, command1, label, args) -> {
                if (args.length == 1)
                {
                    List<String> completions = new ArrayList<>();
                    completions.add("an");
                    completions.add("aus");
                    return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
                } else {
                    return Collections.emptyList();
                }
            });
            command.setExecutor(new PvPCommand(this));
            //Listener
            modul.getMain().getServer().getPluginManager().registerEvents(new PvPListener(this), modul.getMain());
        }

    }

    public PvPHandler getPvpHandler() {
        return pvpHandler;
    }

    public YamlConfiguration getPvpConfig() {
        return pvpConfig;
    }

}
