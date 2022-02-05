package caesreon.core.hilfsklassen;

import caesreon.core.Log;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class SpielerUT {
    //TODO: uebergangsweise
    private static String Prefix = "[Caesreon]";
    public StringUT Spielername = null;
    public Date LastUpdate = null;
    public Date CreateTime = null;
    public UUID uuid = null;

    public SpielerUT() {
    }

    public SpielerUT(UUID uuid, Date createTime, Date lastUpdate, StringUT SpielerName) {
        this.uuid = uuid;
        this.CreateTime = createTime;
        this.LastUpdate = lastUpdate;
        this.Spielername = SpielerName;
    }

    public static Player vPlayer(String Spielername, String UUID) {
        Player p = null;
        p.setDisplayName(Spielername);
        return p;
    }

    public static void Items_AnSpielerGeben(Player p, ArrayList<ItemStack> items) {
        PlayerInventory i = p.getInventory();
        boolean IstVoll = false;
        for (ItemStack is : items) {
            boolean InventarVoll = (i.firstEmpty() == -1);
            if (InventarVoll) {
                p.getWorld().dropItem(p.getLocation(), is);
                IstVoll = true;
                continue;
            }
            i.addItem(new ItemStack[]{is});
        }
        if (IstVoll) {
            ChatSpigot.NachrichtSenden(p, Prefix, "Dein Inventar ist voll.");
        }
    }

    public static void Item_AnSpielerGeben(Player p, ItemStack item) {
        PlayerInventory i = p.getInventory();
        boolean IstVoll = false;
        boolean InventarVoll = (i.firstEmpty() == -1);
        if (InventarVoll) {
            p.getWorld().dropItem(p.getLocation(), item);
            IstVoll = true;
        }
        p.getInventory().addItem(item);
        if (IstVoll) {
            ChatSpigot.NachrichtSenden(p, Prefix, "Dein Inventar ist voll.");
        }
    }

    public static void Item_AnSpielerGeben_KeinDrop(Player p, ItemStack item) {
        PlayerInventory i = p.getInventory();
        boolean IstVoll = false;
        boolean InventarVoll = (i.firstEmpty() == -1);
        if (InventarVoll) {
            IstVoll = true;
        }
        p.getInventory().addItem(item);
        if (IstVoll) {
            ChatSpigot.NachrichtSenden(p, Prefix, "Dein Inventar ist voll.");
        }
    }

    public static UUID fromTrimmed(String trimmedUUID) throws IllegalArgumentException {
        if (trimmedUUID == null) throw new IllegalArgumentException();
        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        /* Backwards adding to avoid index adjustments */
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }

        return UUID.fromString(builder.toString());
    }

    public static List<Player> getPlayers(World w) {
        return w.getPlayers();
    }

    public static boolean isOnline(UUID id) {
        return (getPlayer(id) != null);
    }

    /**
     * Prüft ob ein Spieler online ist und gibt den Spieler wieder
     *
     * @param id Die UUID des Spielers
     * @return
     */
    public static Player getPlayer(UUID id) {
        Player spieler = null;
        try {
            Log.SpigotLogger.Debug(Objects.requireNonNull(Bukkit.getPlayer(id)).getUniqueId().toString());
            spieler = Bukkit.getPlayer(id);
        } catch (Exception exception) {
            try {
                Log.SpigotLogger.Debug("Spieler ist Offline");
                spieler = Bukkit.getOfflinePlayer(id).getPlayer();
            } catch (Exception exception2) {
                Log.SpigotLogger.Info(exception2.toString());
            }
        }
        return spieler;
    }

    public static String getOfflinePlayerName(UUID id) {
        return Bukkit.getOfflinePlayer(id).getName();
    }


    public static UUID formatiereInputUUID(String uuid) throws IllegalArgumentException {
        if (uuid == null) throw new IllegalArgumentException();
        uuid = uuid.trim();
        return uuid.length() == 32 ? fromTrimmed(uuid.replaceAll("-", "")) : UUID.fromString(uuid);
    }

    public static String trimUUID(String uuid) {
        return uuid.replaceAll("-", "");
    }
}
