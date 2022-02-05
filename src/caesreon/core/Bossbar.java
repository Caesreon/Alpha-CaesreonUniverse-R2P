package caesreon.core;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class Bossbar {
    private BarColor bC = BarColor.BLUE; //Default falls dieser nicht gesetzt wird
    private BarStyle bS = BarStyle.SOLID; //Default falls dieser nicht gesetzt wird

    public BossBar erstelleBossBar(String Input, String Regex) {
        BossBar bossBar = Bukkit.createBossBar(ColorCodeParser.ParseString(Input, Regex), bC, bS);
        return bossBar;
    }
    //TODO: Vll noch nen BarStyleParser oder nen BarColorParser schreiben

    public void setBarStyleSolid() {
        bS = BarStyle.SOLID;
    }

    public void setBarSytyleSegmented_10() {
        bS = BarStyle.SEGMENTED_10;
    }

    public void setBarSytyleSegmented_12() {
        bS = BarStyle.SEGMENTED_12;
    }

    public void setBarSytyleSegmented_20() {
        bS = BarStyle.SEGMENTED_20;
    }

    public void setBarSytyleSegmented_6() {
        bS = BarStyle.SEGMENTED_6;
    }

    public void setBarColor_Pink() {
        bC = BarColor.PINK;
    }

    public void setBarColor_Green() {
        bC = BarColor.GREEN;
    }

    public void setBarColor_Red() {
        bC = BarColor.RED;
    }

    public void setBarColor_Yellow() {
        bC = BarColor.YELLOW;
    }

    public void setBarColor_Purple() {
        bC = BarColor.PURPLE;
    }

    public void setBarColor_White() {
        bC = BarColor.WHITE;
    }

    public void setBarColor_Blue() {
        bC = BarColor.BLUE;
    }
}
