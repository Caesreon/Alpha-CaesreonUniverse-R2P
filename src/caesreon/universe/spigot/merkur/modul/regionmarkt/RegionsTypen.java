package caesreon.universe.spigot.merkur.modul.regionmarkt;

public class RegionsTypen {
    public final static String freebuild = "freebuild";
    public final static String legion = "legion_gs";
    public final static String villen = "villen_gs";
    public final static String buerger_kl = "k_stadt_gs";
    public final static String buerger_gr = "g_stadt_gs";
    public final static String shop = "shop_gs";
    public final static String starter = "starter_gs";

    public static String getPlotTypFromString(String data) {
        if (data.contains(freebuild))
            return freebuild;
        if (data.contains(legion))
            return legion;
        if (data.contains(villen))
            return villen;
        if (data.contains(buerger_kl))
            return buerger_kl;
        if (data.contains(buerger_gr))
            return buerger_gr;
        if (data.contains(shop))
            return shop;
        if (data.contains(starter))
            return starter;
        return null;
    }
}
