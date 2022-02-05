package caesreon.universe.bungee;

import caesreon.core.RuntimeModus;
import caesreon.core.system.IDENTIFIER;
import caesreon.main.BungeeMain;

import static caesreon.core.handlers.AgentConfigHandler.FileSeperator;
import static caesreon.core.handlers.AgentConfigHandler.checkPluginRootOrdner;

public class BungeeConfigHandler {
    private BungeeMain main;
    private final String rootOrdnerAbsolut = "plugins" + FileSeperator(IDENTIFIER.PluginName.Caesreon.toString());

    public BungeeConfigHandler(BungeeMain main) {
        this.main = main;
        checkPluginRootOrdner(IDENTIFIER.PluginName.Caesreon.toString());
    }
}
