package caesreon.universe.spigot.jupiter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class BefehlsVerzoegerung implements Listener {
    @EventHandler
    public void onPlayerCommmand(PlayerCommandPreprocessEvent e) {
        String[] args = e.getMessage().split(" ");
        if (args.length > 1 && args[0].equals("/back")) {
            Player player = e.getPlayer();
        }
    }

}
