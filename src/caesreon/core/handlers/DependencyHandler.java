package caesreon.core.handlers;

import caesreon.core.CoreKomponente;
import caesreon.core.Log;
import caesreon.core.interfaces.ILog;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class DependencyHandler implements Listener {
    private final CoreKomponente modul;

    public DependencyHandler(CoreKomponente modul) {
        this.modul = modul;
        Check_Abhaengigkeiten();
    }

    public void Check_Abhaengigkeiten() {
        Log.JavaLogger.Info("Ueberpruefe Abhaengigkeiten!");
        LuckPerms();
        //TNE();
        setupPlaceholderApi();
        checkWorldGuard();
        checkWorldEdit();
    }

    private void LuckPerms() {
        if (!CheckLuckperms()) {
            Log.JavaLogger.Error("Deaktiviert weil LuckPerms Abhaengigkeit nicht gefunden wurde!");
            modul.getMain().getServer().getPluginManager().disablePlugin(modul.getMain());
        }
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        // WorldGuard may not be loaded
        if (!(plugin instanceof WorldEditPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldEditPlugin) plugin;
    }

    private boolean CheckLuckperms() {
        if (modul.getMain().getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            return false;
        }
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();
            modul.getMain().setPermissions(api);
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    private boolean setupPlaceholderApi() {
        if (modul.getMain().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return true;
        }
        Log.JavaLogger.Warning("Konnte PlaceholderAPI nicht finden.");
        modul.getMain().getServer().getPluginManager().disablePlugin(modul.getMain());
        return false;
    }

    private boolean checkWorldGuard() {
        return modul.getMain().getServer().getPluginManager().getPlugin("WorldGuard") != null;
    }

    private boolean checkWorldEdit() {
        return modul.getMain().getServer().getPluginManager().getPlugin("WorldEdit") != null;
    }

    public boolean CheckWeltName(Player P, String Welt) {
        Log.JavaLogger.Debug((P.getLocation().getWorld().getName()));
        Log.JavaLogger.Debug("CheckWeltName");
        if (P.getLocation().getWorld().getName().equalsIgnoreCase(Welt)) {
            Log.JavaLogger.Debug("CheckWeltName(): true");
            return true;
        }
        //modul.Debug("CheckServerName(): false");
        return false;
    }

}
