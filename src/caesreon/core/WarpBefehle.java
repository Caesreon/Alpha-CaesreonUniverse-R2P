package caesreon.core;

import caesreon.universe.spigot.jupiter.ServerBefehle;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Logik fuer zentrale Warp Befehle im Caesreon System. Diese wird von Serverbefehle aufgerufen, aber auch von anderen Klassen wie beispielweise
 * GUI's
 *
 * @see ServerBefehle
 */
public final class WarpBefehle {

    public static void Survival(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.survival.toString()));
    }

    public static void Creative(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.creative.toString()));
    }

    public static void Dungeon(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.dungeon.toString()));
    }

    public static void Kathedrale(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.kathedrale.toString()));
    }

    public static void Lobby(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.lobby.toString()));
    }

    public static void Galleone(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.galleone.toString()));
    }

    public static void Basilica(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.basilica.toString()));
    }

    public static void SolterraSpawn(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.solterraspawn.toString()));
    }

    public static void JapanGarten(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.japanhaus.toString()));
    }

    public static void Tutorial(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.tutorial.toString()));
    }

    public static void farmwelt(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "warp " + CoreKomponente.getDefaultConfig().getString(COMMANDS.BASIS.farmwelt.toString()));
    }
}