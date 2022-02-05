package caesreon.universe.spigot.jupiter;

import caesreon.core.hilfsklassen.SpielerUT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Elderstab implements Listener, CommandExecutor {

    @EventHandler
    public void Interaktion(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("caesreon.items.admin.elderstab")) {
            try {
                ItemStack Item = p.getInventory().getItemInMainHand();
                if (Objects.requireNonNull(Item.getItemMeta().getLore()).contains("Apparet id etiam caeco!")) {
                    Location location = p.getTargetBlock((Set<Material>) null, 100).getLocation();
                    location.getWorld().strikeLightning(location);
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (sender instanceof Player) {
            SpielerUT.Item_AnSpielerGeben((Player) sender, ElderstabItem());
        }
        return false;
    }

    public ItemStack ElderstabItem() {
        ItemStack ElderstabWaffe = new ItemStack(Material.STICK);
        ItemMeta ElderstabMeta = ElderstabWaffe.getItemMeta();
        List<String> Lore = new ArrayList<>();
        Lore.add("§6Zauberstab des Coriolanus_S");
        Lore.add("Apparet id etiam caeco!");
        ElderstabMeta.setDisplayName("Elderstab");
        ElderstabMeta.setUnbreakable(true);
        ElderstabMeta.setLore(Lore);
        ElderstabWaffe.setItemMeta(ElderstabMeta);
        return ElderstabWaffe;
    }
}
