package caesreon.universe.spigot.jupiter;

import caesreon.core.listener.BungeeListener;
import caesreon.core.CoreKomponente;
import caesreon.core.annotations.Befehl;
import caesreon.core.COMMANDS;
import caesreon.core.WarpBefehle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ServerBefehle implements CommandExecutor {
    private final FileConfiguration F;
    private final BungeeListener bungeeListener = new BungeeListener();

    public ServerBefehle(FileConfiguration conf) {
        this.F = conf;
    }

    @Befehl
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            //Server
            if (CoreKomponente.istBungeecordModus())
            {
                if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.survival.toString()))
                    bungeeListener.serverConnect(player, COMMANDS.BASIS.survival.toString());
                if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.lobby.toString()))
                    bungeeListener.serverConnect(player, COMMANDS.BASIS.lobby.toString());
                if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.creative.toString()))
                    bungeeListener.serverConnect(player, COMMANDS.BASIS.creative.toString());
                if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.kathedrale.toString()))
                    bungeeListener.serverConnect(player, COMMANDS.BASIS.kathedrale.toString());
            }

            //Server
            if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.survival.toString()))
               WarpBefehle.Survival(player);
            if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.lobby.toString()))
                WarpBefehle.Lobby(player);
            if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.creative.toString()))
                WarpBefehle.Creative(player);

            //Warps / Welten
            if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.hub.toString()))
                Bukkit.dispatchCommand(sender, "warp " + this.F.getString(COMMANDS.BASIS.hub.toString()));
            if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.farmwelt.toString()))
                WarpBefehle.farmwelt(sender);
            if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.citybuild.toString()))
                Bukkit.dispatchCommand(sender, "warp " + this.F.getString(COMMANDS.BASIS.citybuild.toString()));
            if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.tutorial.toString()))
                WarpBefehle.Tutorial(sender);
            if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.kathedrale.toString()))
                WarpBefehle.Kathedrale(sender);
            if (command.getName().equalsIgnoreCase(COMMANDS.BASIS.dungeon.toString()))
                WarpBefehle.Dungeon(sender);

            //Sonstiges
            if (command.getName().equalsIgnoreCase(COMMANDS.ERWEITERT.haustiere.toString()))
                Bukkit.dispatchCommand(sender, Objects.requireNonNull(this.F.getString(COMMANDS.ERWEITERT.haustiere.toString())));
            if (command.getName().equalsIgnoreCase(COMMANDS.ERWEITERT.gadgets.toString()))
                Bukkit.dispatchCommand(sender, Objects.requireNonNull(this.F.getString(COMMANDS.ERWEITERT.gadgets.toString())));
            if (command.getName().equalsIgnoreCase(COMMANDS.ERWEITERT.kopfbedeckungen.toString()))
                Bukkit.dispatchCommand(sender, Objects.requireNonNull(this.F.getString(COMMANDS.ERWEITERT.kopfbedeckungen.toString())));
            if (command.getName().equalsIgnoreCase(COMMANDS.ERWEITERT.outfits.toString()))
                Bukkit.dispatchCommand(sender, Objects.requireNonNull(this.F.getString(COMMANDS.ERWEITERT.outfits.toString())));
            if (command.getName().equalsIgnoreCase(COMMANDS.ERWEITERT.partikel.toString()))
                Bukkit.dispatchCommand(sender, Objects.requireNonNull(this.F.getString(COMMANDS.ERWEITERT.partikel.toString())));
            if (command.getName().equalsIgnoreCase(COMMANDS.ERWEITERT.cosmetics.toString()))
                Bukkit.dispatchCommand(sender, Objects.requireNonNull(this.F.getString(COMMANDS.ERWEITERT.cosmetics.toString())));
            if (command.getName().equalsIgnoreCase("quack")) {
                ItemStack brot = new ItemStack(Material.BREAD);
                brot.setAmount(1);
                player.getInventory().addItem(brot);
            }
        }
        return true;
    }
}
