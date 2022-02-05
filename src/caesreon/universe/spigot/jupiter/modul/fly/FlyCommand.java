package caesreon.universe.spigot.jupiter.modul.fly;

import caesreon.core.annotations.Befehl;
import caesreon.core.COMMANDS;
import caesreon.core.hilfsklassen.WeltUT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlyCommand implements CommandExecutor {
    private final FlyModul modul;

    public FlyCommand(FlyModul modul) {
        this.modul = modul;
    }

    @Befehl
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, String[] args) {
        if (sender instanceof Player) {
            //Deklarationen
            Player player = (Player) sender;

            if (args.length == 0) {
                //temp-fly
                if (command.getName().equalsIgnoreCase(COMMANDS.FLY.tempfly.toString())) {
                    if (!WeltUT.CheckWeltenName.playerListe(player, modul.verboteneWelten)) {
                        modul.getFlyHandler().setzeTempfly(player, args);
                    }
                }

                ///fly
                if (command.getName().equalsIgnoreCase(COMMANDS.FLY.fly.toString())) {

                    if (player.hasPermission("caesreon.commands.fly")) {
                        modul.getFlyHandler().setzeFly(player, args);
                    }
                }
            }

            //flyspeed
            if (args.length > 0) {
                if (command.getName().equalsIgnoreCase(COMMANDS.FLY.flyspeed.toString())) {
                    if (player.hasPermission("caesreon.commands.flyspeed")) {
                        modul.getFlyHandler().setzeFlypeed(player, args[0]);
                    }
                }
            }
        }
        return true;
    }
}
