package caesreon.universe.spigot.merkur.modul.shops;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

public class ErstelleShopFlags {
    private boolean Flag_Schild = false;
    private boolean Flag_Kiste = false;
    private Sign Schild_;
    private Chest Kiste_;

    public Sign getSchild() {
        return Schild_;
    }

    public void setSchild(Sign s) {
        Schild_ = s;
    }


    public Boolean creationFlag_Schild() {
        return Flag_Schild;

    }

    public Boolean creationFlag_Kiste() {
        return Flag_Kiste;

    }

    public void setFlag_Schild(boolean b) {
        Flag_Schild = b;

    }

    public void setFlag_Kiste(boolean b) {
        Flag_Kiste = b;

    }

    public Chest getKiste() {
        return Kiste_;
    }

    public void setKiste(Chest c) {
        Kiste_ = c;
    }

}
