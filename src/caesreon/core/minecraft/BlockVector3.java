package caesreon.core.minecraft;

import org.bukkit.Location;

public class BlockVector3 {
    private int x;
    private int y;
    private int z;

    public BlockVector3(String input) {

        if (input.equals("0"))
            input = "0:0:0";
        erhalteKoordinatenVonString(input);
    }

    public void erhalteKoordinatenVonString(String input) {
        String[] temp = new String[0];
        if (input.contains("(")) {
            input = input.replace("(", "");
            input = input.replace(")", "");
            input = input.replace(",", "");
            temp = input.split(" ");
        } else {
            temp = input.split(":");
        }

        setX(Integer.parseInt(temp[0]));
        setY(Integer.parseInt(temp[1]));
        setZ(Integer.parseInt(temp[2]));

    }

    public Location getLocation() {
        return null;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
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

}
