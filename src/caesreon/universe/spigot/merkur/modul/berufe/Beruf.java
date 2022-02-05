package caesreon.universe.spigot.merkur.modul.berufe;

import caesreon.core.ColorCodeParser;
import caesreon.core.Log;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;

/**
 * Diese Klasse definiert den Beruf ansich wie er in der Konfiguration hinterlegt ist und wird bei der Initialisierung der Berufe
 * aufgerufen
 *
 * @author Coriolanus_S
 * @see BerufeBelohnungsSatz
 * @see BerufeHandler
 */
public class Beruf {
    public String name;
    public String beschreibung;
    public String chatfarbe;
    public String regex;
    public int maximalLevel;
    public Faehigkeiten Skills;
    private YamlConfiguration konfiguration;
    private String rootSection;

    /**
     * @param konfiguration
     */
    public Beruf(YamlConfiguration konfiguration) {
        Log.SpigotLogger.Debug("caesreon.universe.spigot.merkur.modul.berufe.beruf.beruf()");
        regex = konfiguration.getString("regex");
        maximalLevel = konfiguration.getInt("maximal_level");

        Log.SpigotLogger.Debug(konfiguration.getString("name"));
        Log.SpigotLogger.Debug(konfiguration.getString(ColorCodeParser.ParseString(Objects.requireNonNull(konfiguration.getString("name")), regex)));

        name = ColorCodeParser.ParseString(Objects.requireNonNull(konfiguration.getString("name")), regex);
        beschreibung = ColorCodeParser.ParseString(Objects.requireNonNull(konfiguration.getString("beschreibung")), regex);
        chatfarbe = ColorCodeParser.ParseString(Objects.requireNonNull(konfiguration.getString("chatfarbe")), "&");
        this.konfiguration = konfiguration;
    }

    /**
     * Wird vermutlich nicht genutzt, anderer approach im Datenhandling der Berufe..
     *
     * @param Konfiguration
     * @param rootSection   - wird verwendet um waehrend der Initialisierung der Berufe
     *                      den Berufsnamen herauszufinden, da zu diesem Zeitpunkt die Konfiguration noch nicht verarbeitet ist
     */
    @Deprecated
    public Beruf(YamlConfiguration Konfiguration, String rootSection) {
        this.rootSection = rootSection;
        name = Konfiguration.getString(rootSection + ".name");
        beschreibung = Konfiguration.getString(rootSection + ".beschreibung");
        chatfarbe = ColorCodeParser.ParseString(Konfiguration.getString(rootSection + ".chatfarbe"), "&");

    }

    public YamlConfiguration getKonfiguration() {
        return konfiguration;
    }
}
