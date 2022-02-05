package caesreon.core.minecraft;

import org.bukkit.Location;

public class Koordinate {
    private int x;
    private int y;
    private int z;

    /**
     * Stellt eine Minecraft Koordinate dar
     *
     * @param x -
     * @param y - Höhe
     * @param z -
     */
    public Koordinate(int x, int y, int z) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    public Koordinate(Location location) {
        this.setX(location.getBlockX());
        this.setY(location.getBlockY());
        this.setZ(location.getBlockZ());
    }

    /**
     * Dieser Konstruktor wird genutzt wenn die Hoehe unrelevant ist. Diese wird dann ggf. errechnet anhand des höchstmoeglichen naechsten
     * Punkts
     *
     * @param x
     * @param z
     */
    public Koordinate(int x, int z) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String toString() {
        return "x=" + String.valueOf(x) + " y=" + String.valueOf(y) + " z=" + String.valueOf(z);
    }


}
