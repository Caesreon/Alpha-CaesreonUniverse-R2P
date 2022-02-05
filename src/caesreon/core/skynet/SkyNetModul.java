package caesreon.core.skynet;

import caesreon.core.CoreKomponente;
import caesreon.core.Log;
import caesreon.main.SpigotMain;
import org.bukkit.command.PluginCommand;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkyNetModul {
    private CoreKomponente cap;
    private SpielerLookup spielerLookup;
    private SkyNetHandler skynetHandler;
    protected String prefix;

    public SkyNetModul(CoreKomponente cap) {
        this.cap = cap;

        Log.SpigotLogger.Init("Initialisiere Spieler-Register");

        prefix = "[Skynet]";
        skynetHandler = new SkyNetHandler(this);

        Log.SpigotLogger.InitErfolgreich("Spieler-Register initialisiert");
        // Commands aktivieren
        PluginCommand command = cap.getMain().getCommand("skynet");
        command.setTabCompleter((sender, command1, label, args) -> {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();
                completions.add("info");
                completions.add("-i -m");
                completions.add("-i -ec");
                return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
            } else {
                return Collections.emptyList();
            }
        });
        command.setExecutor(new SkyNetCommand(this));
    }

    public SkyNetHandler getSkynetHandler() {
        return skynetHandler;
    }

    public SpigotMain getMain()
    {
        return cap.getMain();
    }
}
