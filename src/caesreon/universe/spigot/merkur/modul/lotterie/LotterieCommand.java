package caesreon.universe.spigot.merkur.modul.lotterie;

import caesreon.core.COMMANDS;
import caesreon.core.hilfsklassen.PermissionUT;
import caesreon.universe.spigot.merkur.MerkurKomponente;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LotterieCommand implements CommandExecutor {

    public LotterieCommand(MerkurKomponente modul) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2, String[] arg3) {
        if (arg3.length == 0) {
            //Erklaerung
        }

        Player p = (Player) arg0;

        if (arg3.length > 0) {
            if (arg3[0].equalsIgnoreCase(COMMANDS.BASIS.LOTTERIE.TAGESLOTTERIE.tag.toString())) {
                if (arg3[1].equalsIgnoreCase(COMMANDS.BASIS.LOTTERIE.TAGESLOTTERIE.tag.toString())) {
                    //Tageslotterie
                    if (arg3[2].equalsIgnoreCase(COMMANDS.BASIS.LOTTERIE.TAGESLOTTERIE.kaufen.toString()) && !PermissionUT.istZweitAccount(p)) {

                    }
                    if (arg3[2].equalsIgnoreCase(COMMANDS.BASIS.LOTTERIE.TAGESLOTTERIE.ziehung.toString())) {

                    }
                }
            }

            if (arg3[0].equalsIgnoreCase(COMMANDS.BASIS.LOTTERIE.WOCHENLOTTERIE.woche.toString())) {
                //Wochenlotterie
                if (arg3[2].equalsIgnoreCase(COMMANDS.BASIS.LOTTERIE.TAGESLOTTERIE.kaufen.toString())) {

                }

                if (arg3[2].equalsIgnoreCase(COMMANDS.BASIS.LOTTERIE.TAGESLOTTERIE.ziehung.toString())) {

                }
            }
        }
        return true;
    }
}
