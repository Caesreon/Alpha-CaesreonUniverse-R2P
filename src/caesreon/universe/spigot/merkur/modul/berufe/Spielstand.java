package caesreon.universe.spigot.merkur.modul.berufe;

import caesreon.core.Log;
import caesreon.core.hilfsklassen.ChatSpigot;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Spielstand {
    protected static String trenner = "=";
    private Beruf beruf;
    private double levelPunkte;
    private int level;
    private double punkteGesamtOberesLevel;
    private double punkteGesamtUnteresLevel;
    private double punkteBisOberesLevel;
    private double punkteFortschrittAktuellesLevel;

    public Spielstand(Beruf beruf, String levelString) {
        this.setBeruf(beruf);
        setzeLevelDatenVonDataString(levelString);
    }

    public double getLevelPunkte() {
        return levelPunkte;
    }

    public void setLevelPunkte(int levelPunkte) {
        this.levelPunkte = levelPunkte;
    }

    public void addiereLevelPunkte(double levelPunkte, Player p) {
        this.levelPunkte += levelPunkte;
        if (levelPunkte >= punkteBisOberesLevel) {
            Log.SpigotLogger.Debug("Spielerdaten: Level up!");
            this.level += 1;
            ChatSpigot.NachrichtSenden(p, BerufeModul.Prefix, "Glückwunsch! Du bist auf Level " + String.valueOf(this.level) + " aufgestiegen.");
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 0);
            ermittelLevelMetaDaten(level);
            return;
        }
        ermittelLevelMetaDaten(level);
    }

    public String toString() {
        return beruf.name + " lvl=" + level + " p_ges=" + levelPunkte + " p_o_lvl=" +
                punkteGesamtOberesLevel + " p_u_lvl=" + punkteGesamtUnteresLevel + " p_bis_o_lvl=" +
                punkteBisOberesLevel + " p_fortschritt=" + punkteFortschrittAktuellesLevel;
    }

    public Beruf getBeruf() {
        return beruf;
    }

    public void setBeruf(Beruf beruf) {
        this.beruf = beruf;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getPunkteBisOberesLevel() {
        return punkteBisOberesLevel;
    }

    public void setPunkteBisOberesLevel(double d) {
        this.punkteBisOberesLevel = d;
    }

    public double getPunkteFortschrittAktuellesLevel() {
        return punkteFortschrittAktuellesLevel;
    }

    public double getPunkteGesamtNextLevel() {
        return punkteGesamtOberesLevel;
    }

    public String getDatenString() {
        Log.SpigotLogger.Verbose(String.valueOf(level) + trenner + String.valueOf(levelPunkte));
        return String.valueOf(level) + trenner + String.valueOf(levelPunkte);
    }

    /**
     * Trennt Inputstring anhand des vordefinierten Zeichens und parsed die beiden Werte in ihre jeweilign Deklarationen
     *
     * @param daten Input String: Level=LevelPunkte
     */
    private void setzeLevelDatenVonDataString(String daten) {
        int level = Integer.valueOf(daten.substring(0, daten.lastIndexOf(trenner)));
        double levelPunkte = Double.valueOf(daten.substring(daten.lastIndexOf(trenner) + 1, daten.length()));
        this.level = level;
        this.levelPunkte = levelPunkte;
        ermittelLevelMetaDaten(level);
    }

    /**
     * Berechnet anhand eines unbestimmten Levels alle dazugehörigen Metadaten anhand eines bereits ermittelten Levels.
     * Dies ist notwending um beispielweise das nächst höhere oder tiefere Level zu bestimmen, beispielweise bei jedem Block
     * der abgebaut wird .
     *
     * @param level
     */
    private void ermittelLevelMetaDaten(int level) {
        this.punkteGesamtOberesLevel = BerufeMathematik.berechneGesamtLevelPunkteProgression(level + 1);
        this.setPunkteBisOberesLevel(punkteGesamtOberesLevel - this.levelPunkte);
        this.punkteGesamtUnteresLevel = BerufeMathematik.berechneGesamtLevelPunkteProgression(getLevel());
        this.punkteFortschrittAktuellesLevel = this.levelPunkte - punkteGesamtUnteresLevel;
        Log.SpigotLogger.Debug(toString());
    }

}
