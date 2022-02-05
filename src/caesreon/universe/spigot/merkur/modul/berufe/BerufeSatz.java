package caesreon.universe.spigot.merkur.modul.berufe;

import caesreon.core.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ein BerufeSatz soll eine Gruppe von Berufen enthalten. Ein BerufSatz wird beispielweise im
 * BerufeBelohnunsSatz benutzt, wo jedem Item eine Gruppe von Berufen zugeordnet wird welche eine
 * bestimmte Handlung, beispielweise das abbauen von Erzen, belohnt. Jeder BerufSatz steht also in einer
 * gewissen Relation welche ueber die Konfigurationsdateien der jeweiligen Berufe festgelegt wird
 *
 * @author Coriolanus_S
 */
public class BerufeSatz {
    public List<Beruf> berufeSatz = new ArrayList<Beruf>();

    public BerufeSatz(Beruf beruf) {
        berufeSatz.add(beruf);
    }

    public BerufeSatz(Beruf... berufe) {
        berufeSatz.addAll(Arrays.asList(berufe));
    }

    /**
     * Fuegt einen Beruf der BerufeSatz Liste hinzu
     *
     * @param beruf - der hinzuzufuegende Beruf
     */
    public void berufZuBerufeSetHinzufuegen(Beruf beruf) {
        Log.SpigotLogger.Debug("caesreon.universe.spigot.merkur.modul.berufe.BerufeSatz.berufZuBerufeSetHinzufuegen()");
        berufeSatz.add(beruf);
    }

    public void berufZuBerufeSetEntfernen(Beruf beruf) {
        berufeSatz.remove(beruf);
    }

    public List<Beruf> erhalteBerufeSatz() {
        return berufeSatz;
    }

    public Boolean enthaeltBeruf(Beruf beruf) {
        if (berufeSatz.contains(beruf))
            return true;
        return false;
    }

    public Boolean enthaeltString(String berufName) {
        for (Beruf b : berufeSatz) {
            if (b.name.equals(berufName))
                return true;
        }
        return false;
    }
}
