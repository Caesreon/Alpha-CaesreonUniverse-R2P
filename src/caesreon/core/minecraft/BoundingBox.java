package caesreon.core.minecraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Diese Klasse enthält alle Eckpunkte einer bestimmten Region, bspw. eines Chunks und speichert diese in Locations
 *
 * @author Coriolanus_S
 */
public class BoundingBox {
    private Location a;
    private Location b;
    private Location c;
    private Location d;

    /**
     * Diese Klasse ermittelt die Locations (Eckpunkte) anhand der uebermittelten Koordinaten und speichert diese in den Locations
     *
     * @param p - Der Spieler von welchem die Koordinaten abgefragt werden
     * @param a - Punkt Linksoben
     * @param b - Punkt Linksunten
     * @param c - Punkt Rechtsoben
     * @param d - Punkt Rechtsunten
     * @implNote Worldguard zaehlt anders wie Mojang, deswegen muss fuer eine Protected Region Punkt b und C geladen werden!
     */
    public BoundingBox(Player p, Koordinate a, Koordinate b, Koordinate c, Koordinate d) {
        this.a = p.getWorld().getHighestBlockAt(a.getX(), a.getZ()).getLocation();
        this.b = p.getWorld().getHighestBlockAt(b.getX(), b.getZ()).getLocation();
        this.c = p.getWorld().getHighestBlockAt(c.getX(), c.getZ()).getLocation();
        this.d = p.getWorld().getHighestBlockAt(d.getX(), d.getZ()).getLocation();

    }

    /**
     * @param a - Punkt Linksoben
     * @param b - Punkt Linksunten
     * @param c - Punkt Rechtsoben
     * @param d - Punkt Rechtsunten
     * @implNote Worldguard zaehlt anders wie Mojang, deswegen muss fuer eine Protected Region Punkt b und C geladen werden!
     */
    public BoundingBox(Location a, Location b, Location c, Location d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.setD(d);
    }

    public Location getA() {
        return a;
    }

    public void setA(Location a) {
        this.a = a;
    }

    public Location getB() {
        return b;
    }

    public void setB(Location b) {
        this.b = b;
    }

    public Location getC() {
        return c;
    }

    public void setC(Location c) {
        this.c = c;
    }

    public Location getD() {
        return d;
    }

    public void setD(Location d) {
        this.d = d;
    }


}
