package caesreon.core.hilfsklassen;

import caesreon.main.SpigotMain;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;

public class WorldEditUT {
    /**
     * Wird genutzt um aus einer WorldEdit Selektion heraus eine Region zu erstellen
     *
     * @param p - der Spieler
     * @return - die Region anhand der WorldEdit Selektion
     */
    public static Region erhalteRegionVonSelection(Player p) {
        BukkitPlayer spieler = BukkitAdapter.adapt(p);
        Region region = null;
        try {
            region = WorldEdit.getInstance().getSessionManager().get(spieler).getSelection(spieler.getWorld());
        } catch (IncompleteRegionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return region;
    }

    /**
     * Konvertiert eine Location zu einem BlockVector
     *
     * @param location
     * @return
     */
    public static BlockVector3 konvertiereLocationZuBlockVector(Location location) {

        return BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }

    /***
     * Diese Methode sucht und findet wenn die Region vorhanden ist, in welcher der Spieler steht
     * @param m
     * @param Loc
     * @return die korrete Region anhand der Location sofern vorhanden
     * @throws NullPointerException
     */
    public static ProtectedRegion erhalteRegionVonLocation(SpigotMain m, Location Loc) throws NullPointerException {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(Loc);
        for (ProtectedRegion region : set) {
            return region;
        }
        return null;
    }

    public static ProtectedRegion erhalteRegionDurchName(SpigotMain m, String Plotname, World Welt) throws NullPointerException {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(Welt);
        return regions.getRegion(Plotname);
    }

    public static int berechneBlocksInCuboid_m3(ProtectedRegion r) {
        return r.volume();
    }

    public static int berechneBlocksInCuboid_m2(ProtectedRegion r) {
        return r.volume() / 256;
    }

    public Location erhalteWorldguardLocationVonBukkitLocation(org.bukkit.Location BukkitLoc) {
        return new Location(null, BukkitLoc.getX(), BukkitLoc.getY(), BukkitLoc.getZ());
    }
}