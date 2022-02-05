package caesreon.main;

import caesreon.core.CoreKomponente;
import caesreon.core.Log;
import caesreon.core.system.IDENTIFIER;
import caesreon.universe.spigot.jupiter.JupiterKomponente;
import caesreon.universe.spigot.merkur.MerkurKomponente;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import entchenklein.bossPlugin.items.itemLists.ItemEnum;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import teozfrank.ultimatevotes.util.UltimateVotesAPI;


public class SpigotMain extends JavaPlugin {

    private static SpigotMain spigotMainInstance;
    private CoreKomponente coreAnkerPunkt;
    private JupiterKomponente jupiterAnkerPunkt;
    private MerkurKomponente merkurAnkerPunkt;
    private LuckPerms LP;
    private UltimateVotesAPI uV;
    private ItemEnum ente;

    public static SpigotMain getSpigotMainInstance() {
        return spigotMainInstance;
    }

    @Override
    public void onEnable() {
        spigotMainInstance = this;
        Log.SpigotLogger.Info("------------------------- " + ChatColor.RED + IDENTIFIER.PluginName.CaesreonUniverse + "-Agent" + ChatColor.WHITE + " -------------------------");
        coreAnkerPunkt = new CoreKomponente(this);
        jupiterAnkerPunkt = new JupiterKomponente(this);
        merkurAnkerPunkt = new MerkurKomponente(this);
        Log.SpigotLogger.Info("System wurde erfolgreich initialisiert");
        Log.SpigotLogger.Info("-------------------------------------------------------");
    }

    @Override
    public void onDisable() {
        spigotMainInstance = null;
        coreAnkerPunkt.getServerShutdown().datenSpeicherung();
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        Log.SpigotLogger.Info(IDENTIFIER.PluginName.CaesreonUniverse + "-Agent wird beendet!");
    }

    public JupiterKomponente getJupiterKomponente() {
        return jupiterAnkerPunkt;
    }

    public CoreKomponente getCore() {
        return coreAnkerPunkt;
    }

    public MerkurKomponente getMerkurKomponente() {
        return merkurAnkerPunkt;
    }

    public LuckPerms getPermissions() {
        return LP;
    }

    public void setPermissions(LuckPerms Input) {
        LP = Input;
    }

    public ItemEnum getTheOneAndOnlyEnte() {
        return ente;
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        // WorldGuard may not be loaded
        if (!(plugin instanceof WorldEditPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldEditPlugin) plugin;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (!(plugin instanceof WorldGuardPlugin)) {
            Log.SpigotLogger.Debug("Could not load WorldGuard");
            return null; // Maybe you want throw an exception instead
        } else {
            Log.SpigotLogger.Debug("Loaded WorldGuard");
        }

        return (WorldGuardPlugin) plugin;
    }

    public UltimateVotesAPI getUltimateVotes() {
        return uV;
    }

}
