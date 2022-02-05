package caesreon.universe.spigot.jupiter.modul.gilden;

import caesreon.universe.spigot.jupiter.JupiterKomponente;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GildenCommand implements CommandExecutor {
    private JupiterKomponente modul;

    public GildenCommand(JupiterKomponente modul) {
        this.modul = modul;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }
}
