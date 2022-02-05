package caesreon.core.hilfsklassen;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

public class BlockUT {
    public static Chest erhalteKisteDurchKoordinaten(World Welt, Double x, Double y, Double z) {
        Location chestLoc = new Location(Welt, x, y, z);
        Block b = chestLoc.getBlock();
        return (Chest) b.getState();
    }

    public static Sign erhalteSchildDurchKoordinaten(World Welt, int x, int y, int z) {
        Location signLoc = new Location(Welt, x, y, z);
        Block b = signLoc.getBlock();
        return (Sign) b.getState();
    }

    public static Block erhalteBlockDurchKoordinaten(World Welt, int x, int y, int z) {
        Location signLoc = new Location(Welt, x, y, z);
        return signLoc.getBlock();
    }

}
