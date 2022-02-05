package caesreon.universe.spigot.jupiter;

import caesreon.core.annotations.Befehl;
import caesreon.core.hilfsklassen.ChatSpigot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadConfigCommand implements CommandExecutor {
    private final JupiterKomponente modul;

    public ReloadConfigCommand(JupiterKomponente modul) {
        this.modul = modul;
    }

    @Befehl
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, String[] args) {
        if (sender instanceof Player) {
            ReloadConfig(args);
            ChatSpigot.NachrichtSenden((Player) sender, "[System]", "Konfigurationsdateien neu geladen");
        }
        if (!(sender instanceof Player)) {
            ReloadConfig(args);
        }
        return true;
    }

    private void ReloadConfig(String[] args) {
        if (args.length == 0) ;
        if (args.length > 0)
            if (args[0].equalsIgnoreCase("sys"))
                if (args[1].equalsIgnoreCase("reload"))
                    modul.getMain().getCore().getConfigHandler().reloadKonfigurationen();
    }
}
