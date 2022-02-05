package caesreon.main;

import caesreon.core.Log;
import caesreon.core.system.IDENTIFIER;
import caesreon.universe.bungee.BungeeConfigHandler;
import caesreon.universe.bungee.befehle.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Hinweis für mich:
 * Ich kann BungeeCord nur zum globalen Abfragen spezifischer, in Bungee vorgegebenen Variablen benutzen. Bspw. IP, Serverwechsel, Nachrichten.
 * Ich kann NICHT auf die Bungee-Config zugreifen und ich kann auch keine Werte von dort beziehen. Eventuell ist es deswegen notwendig, eine
 * eigene Proxy-Applikation zu schreiben
 */
public class BungeeMain extends Plugin implements Listener  {

    private static ProxyServer _proxy = null;
    private BungeeConfigHandler configHandler;
    private static BungeeMain _instance;

    public void onEnable() {
        BungeeMain._instance = this;
        Log.BungeeLogger.Info("------------------------- " + ChatColor.RED + IDENTIFIER.PluginName.Caesreon + "-Proxy" + ChatColor.WHITE + " -------------------------");
        _proxy = getProxy();
        configHandler = new BungeeConfigHandler(this);
        _proxy.getPluginManager().registerCommand(this, new Lobby());
        _proxy.getPluginManager().registerCommand(this, new Survival());
        _proxy.getPluginManager().registerCommand(this, new Creative());
        _proxy.getPluginManager().registerCommand(this, new Event());
        _proxy.getPluginManager().registerCommand(this, new Kathedrale());
        _proxy.registerChannel(String.valueOf(IDENTIFIER.PluginChannel.cae_sys));
        Log.BungeeLogger.Info("System wurde erfolgreich initialisiert");
        Log.BungeeLogger.Info("-------------------------------------------------------");
    }

    public  void onDisable() {
        _proxy.getPluginManager().unregisterListeners(this);
        Log.BungeeLogger.Info(IDENTIFIER.PluginName.Caesreon + "-Proxy deaktiviert");
    }

    public static BungeeMain getInstance() { return _instance;
    }

    public BungeeConfigHandler getConfigHandler() {
        return configHandler;
    }

    public static ProxyServer get_proxy() {
        return _proxy;
    }
}
