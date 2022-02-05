package caesreon.universe.spigot.jupiter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteHider implements Listener {
    private FileConfiguration F;


    public TabCompleteHider(FileConfiguration Input) {
        F = Input;
    }

    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandSendEvent e) {
        List<String> blockedCommands = new ArrayList<>();
        Player Spieler = e.getPlayer();

        //for i in liste (von configfile=
        //Wenn spieler nicht permission hat dann
        //else return

        for (Object i : F.getList("permission-node")) {
            System.out.println((String) i);
            if (!Spieler.hasPermission((String) i)) {
                System.out.println("Blocked " + (String) i);
                blockedCommands.add(i.toString());
            }
        }

        e.getCommands().removeAll(blockedCommands);
    }

}
