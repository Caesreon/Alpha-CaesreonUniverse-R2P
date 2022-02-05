package caesreon.universe.spigot.merkur.modul.berufe;

public class BerufeMathematik {
    /**
     * Berechnet das Integral des jeweiligen Levels
     *
     * @param x Level
     * @return
     */
    static int berechneGesamtLevelPunkteProgression(int x) {
        return (int) (Math.pow(x, 2) * (15 * Math.pow(x, 2) + 16 * x + 60)) / 120;
    }

    static int berechneLevelProgression(int level) {
        return (int) (0.5 * Math.pow(level, 3) + 0.4 * Math.pow(level, 2) + level);
    }

    static double berechneEinkommensProgression(double basis_belohnung, double spielerLevel) {
        //return (int) Math.round(0.0095 * basis_belohnung * (spielerLevel + 99));
        return 0.0095 * basis_belohnung * (spielerLevel + 99);
    }

    /**
     * Berechnet Levelpunkte die der Spieler erhalten soll anhand eines Basis-Werts, welches einem Item
     *
     * @param basisErfahrung
     * @param level
     * @return
     */
    static double berechneLevelpunkteProgression(double basisErfahrung, int level) {
        //return (int) Math.round(0.01 * basisErfahrung * (level + 99));
        return Math.round(0.01 * basisErfahrung * (level + 99) * 100.0) / 100.0;
    }

}
