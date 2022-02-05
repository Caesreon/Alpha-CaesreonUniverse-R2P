package caesreon.universe.spigot.merkur.modul.berufe;

import caesreon.core.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SpielerStatistiken enthaelt eine Liste mit allen Berufen und den dazugehoerigen Leveln eines
 * Spielers. Gespeichert wird dieses Objekt einmalig zusammen mit dem Spieler
 * in der Hashmap berufeCachedSpielstaende.
 *
 * @author Coriolanus_S
 */
public class SpielerStatistiken {
    Map<String, Spielstand> spielstaende = new HashMap<>();

    public SpielerStatistiken() {
    }

    public Map<String, Spielstand> getSpielstaende() {
        return spielstaende;
    }

    /**
     * Fuegt einen Spielstand eines spezifischen Spielers in die Liste SpielstandStatistik
     *
     * @param spielstand - der hinzuzufuegende Spielstand
     */
    public void spielstandZuSpielstaendeHinzufuegen(Spielstand spielstand) {
        Log.SpigotLogger.Debug("spielstandZuSpielstaendeHinzufuegen()");
        spielstaende.put(spielstand.getBeruf().toString(), spielstand);
    }

    /**
     * Gibt alle gespeicherten Spielstaende zurück
     *
     * @return
     */
    public List<Spielstand> erhalteSpielstaende() {
        return new ArrayList<Spielstand>(spielstaende.values());
    }

    public Spielstand erhalteSpielsstandAnhandBeruf(Beruf beruf) {
        return spielstaende.get(beruf.toString());

    }
}