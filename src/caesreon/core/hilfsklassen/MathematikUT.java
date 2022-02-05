package caesreon.core.hilfsklassen;

import com.sk89q.worldedit.math.BlockVector2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MathematikUT {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static class RegionQuaderKanten {
        public int laengeKanteA;
        public int laengeKanteB;

        public RegionQuaderKanten(int a, int b) {
            laengeKanteA = a;
            laengeKanteB = b;
        }

        public static RegionQuaderKanten berechneKantenlaengenVonRegion(List<BlockVector2> gs_eckpunkte) {
            BlockVector2 eckpunkt1 = gs_eckpunkte.get(0);
            BlockVector2 eckpunkt2 = gs_eckpunkte.get(1);
            BlockVector2 eckpunkt3 = gs_eckpunkte.get(2);

            int eckpunktLinksOben_x = eckpunkt1.getX();
            int eckpunktRechtsOben_x = eckpunkt2.getX();
            int eckpunktLinksOben_z = eckpunkt1.getZ();
            int eckpunktLinksUnten_z = eckpunkt3.getZ();

            if (eckpunktLinksOben_x < 0)
                eckpunktLinksOben_x = -eckpunktLinksOben_x;
            if (eckpunktRechtsOben_x < 0)
                eckpunktRechtsOben_x = -eckpunktRechtsOben_x;
            if (eckpunktLinksOben_z < 0)
                eckpunktLinksOben_z = -eckpunktLinksOben_z;
            if (eckpunktLinksUnten_z < 0)
                eckpunktLinksUnten_z = -eckpunktLinksUnten_z;

            int a = eckpunktLinksOben_x - eckpunktRechtsOben_x + 1;
            int b = eckpunktLinksUnten_z - eckpunktLinksOben_z + 1;

            System.out.println(a + " " + b);
            return new RegionQuaderKanten(a, b);

        }
    }

    public static class Konvertierungen {
        public static long StundenInTicks(long Wert) {
            Wert = Wert * 60 * 60 * 20;
            return Wert;
        }

        public static long MinutenInTicks(long Wert) {
            Wert = Wert * 60 * 20;
            return Wert;
        }

        public static long SekundenInTicks(long Wert) {
            Wert = Wert * 20;
            return Wert;
        }

        public static int toInt(String s) {
            return Integer.parseInt(s);
        }
    }

}

