package caesreon.universe.spigot.merkur.modul.berufe;

import caesreon.core.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Diese Klasse soll mit Init() des Servers alle Berufe festhalten welche ein spezifisches Item belohnen.
 * Dies wird ermöglicht in dem festgehalten wird, welch Berufe als "BerufeSatz" das dementsprechende Item
 * entlohnt. Durch einen for loop sollen dann im BerufeHandler die jeweiligen Berufe und ihre Konfigurationen entnommen
 * werden und der Spieler entsprechend der hinterlegten Konfiguration belohnt werden
 * <p>
 * WICHTIG :Memo an mich selbst.
 * <p>
 * Wer sagt denn, das hier  die aktuelle Belohnung hinterlegt sein. Alles was die Klasse aussagen muss
 * ist ob dieses Item oder diese Entitaet in den Berufkonfigurationen erfasst wurde und wenn ja welche Berufe
 * dieses entlohnen.
 * <p>
 * Das heisst im Umkehrschluss, das ich eine weitere Methode schreiben nuss, welche gezielt nach den Berufen sucht
 * welche von den Berufesaetzen angegeben werden
 *
 * @author Coriolanus_S
 */
public class BerufeBelohnungsSatz {
    public static void itemUndBerufEntfernen(String item, Beruf beruf) {

    }

    /**
     * Diese Methode prueft ueber die in MapEnthaltEntitaetItem Klasse ob die jeweilige Entitaet in der fuer
     * die jeweilige Handlung, bspw. bauen, hinterlegte Map enthalten ist.
     * Wenn ja, laed er den BerufeSatz, fuegt den neuen Beruf zum neuen Berufesatz hinzu und  entfernt danach den alten
     * BerufeSatz aus der Map um danach den neuen Berufesatz in der Map zu speichern.
     * <p>
     * Ist dies nicht der Fakkm wird ein voellig neuer Berufesatz initialisiert und in der jeweiligen HashMap gespeichert.
     *
     * @param handlung               - bspw. "bauen" "abbauen" "toeten" "zaehmen"
     * @param entitaetBelohnungsSatz - der jeweilige Entitaetbelohnungssatz mit Informationen ueber Art der entitaet, einkommen, erfahrung und beruf
     */
    public void updateBerufeSetFuerHandlungsBelohnung(String handlung, EntitaetBelohnungsSatz entitaetBelohnungsSatz) {
        Log.SpigotLogger.Verbose("BerufeBelohnungsSatz updateBerufeSetFuerHandlungsBelohnung: " + handlung);
        switch (handlung) {
            case "bauen":
                if (MapEnthaeltEntitaetItem.bauen(entitaetBelohnungsSatz.entitaet)) {
                    Log.SpigotLogger.Verbose("BerufeBelohnungsSatz case: " + handlung);
                    Log.SpigotLogger.Verbose(handlung);
                    BerufeSatz temp = ItemUndEntitaetenBelohnungen.bauen.get(entitaetBelohnungsSatz.entitaet);
                    ItemUndEntitaetenBelohnungen.bauen.remove(entitaetBelohnungsSatz.entitaet);
                    temp.berufZuBerufeSetHinzufuegen(entitaetBelohnungsSatz.beruf);
                    ItemUndEntitaetenBelohnungen.bauen.put(entitaetBelohnungsSatz.entitaet, temp);
                } else {
                    Log.SpigotLogger.Verbose("BerufeBelohnungsSatz case: " + handlung);
                    Log.SpigotLogger.Verbose("BerufeBelohnungsSatz entitaet: " + entitaetBelohnungsSatz.entitaet);
                    BerufeSatz temp = new BerufeSatz(entitaetBelohnungsSatz.beruf);
                    ItemUndEntitaetenBelohnungen.bauen.put(entitaetBelohnungsSatz.entitaet, temp);
                }
                break;
            case "abbauen":
                if (MapEnthaeltEntitaetItem.abbauen(entitaetBelohnungsSatz.entitaet)) {
                    BerufeSatz temp = ItemUndEntitaetenBelohnungen.abbauen.get(entitaetBelohnungsSatz.entitaet);
                    ItemUndEntitaetenBelohnungen.abbauen.remove(entitaetBelohnungsSatz.entitaet);
                    temp.berufZuBerufeSetHinzufuegen(entitaetBelohnungsSatz.beruf);
                    ItemUndEntitaetenBelohnungen.abbauen.put(entitaetBelohnungsSatz.entitaet, temp);
                } else {
                    BerufeSatz temp = new BerufeSatz(entitaetBelohnungsSatz.beruf);
                    ItemUndEntitaetenBelohnungen.abbauen.put(entitaetBelohnungsSatz.entitaet, temp);
                }
                break;
            case "zaehmen":
                if (MapEnthaeltEntitaetItem.zaehmen(entitaetBelohnungsSatz.entitaet)) {
                    BerufeSatz temp = ItemUndEntitaetenBelohnungen.zaehmen.get(entitaetBelohnungsSatz.entitaet);
                    ItemUndEntitaetenBelohnungen.zaehmen.remove(entitaetBelohnungsSatz.entitaet);
                    temp.berufZuBerufeSetHinzufuegen(entitaetBelohnungsSatz.beruf);
                    ItemUndEntitaetenBelohnungen.zaehmen.put(entitaetBelohnungsSatz.entitaet, temp);
                } else {
                    BerufeSatz temp = new BerufeSatz(entitaetBelohnungsSatz.beruf);
                    ItemUndEntitaetenBelohnungen.zaehmen.put(entitaetBelohnungsSatz.entitaet, temp);
                }
                break;
            case "toeten":
                if (MapEnthaeltEntitaetItem.toeten(entitaetBelohnungsSatz.entitaet)) {
                    BerufeSatz temp = ItemUndEntitaetenBelohnungen.toeten.get(entitaetBelohnungsSatz.entitaet);
                    ItemUndEntitaetenBelohnungen.toeten.remove(entitaetBelohnungsSatz.entitaet);
                    temp.berufZuBerufeSetHinzufuegen(entitaetBelohnungsSatz.beruf);
                    ItemUndEntitaetenBelohnungen.toeten.put(entitaetBelohnungsSatz.entitaet, temp);
                } else {
                    BerufeSatz temp = new BerufeSatz(entitaetBelohnungsSatz.beruf);
                    ItemUndEntitaetenBelohnungen.toeten.put(entitaetBelohnungsSatz.entitaet, temp);
                }
                break;
            case "craften":
                if (MapEnthaeltEntitaetItem.craften(entitaetBelohnungsSatz.entitaet)) {
                    BerufeSatz temp = ItemUndEntitaetenBelohnungen.craften.get(entitaetBelohnungsSatz.entitaet);
                    ItemUndEntitaetenBelohnungen.craften.remove(entitaetBelohnungsSatz.entitaet);
                    temp.berufZuBerufeSetHinzufuegen(entitaetBelohnungsSatz.beruf);
                    ItemUndEntitaetenBelohnungen.craften.put(entitaetBelohnungsSatz.entitaet, temp);
                } else {
                    BerufeSatz temp = new BerufeSatz(entitaetBelohnungsSatz.beruf);
                    ItemUndEntitaetenBelohnungen.craften.put(entitaetBelohnungsSatz.entitaet, temp);
                }
                break;
        }
    }

    public static class ItemUndEntitaetenBelohnungen {
        //public List<EntitaetBelohnungsSatz> bauen = new ArrayList<EntitaetBelohnungsSatz>();

        public static Map<String, BerufeSatz> bauen = new HashMap<>();
        public static Map<String, BerufeSatz> abbauen = new HashMap<>();
        public static Map<String, BerufeSatz> zaehmen = new HashMap<>();
        public static Map<String, BerufeSatz> toeten = new HashMap<>();
        public static Map<String, BerufeSatz> craften = new HashMap<>();
        public static Map<String, BerufeSatz> zuechten = new HashMap<>();
    }

    /**
     * Klasse mit Booleans, welche prueft ob ein bestimmtes Item oder eine bestimmte Entitaet in einer der Berufe
     * Hashmaps enthalten ist
     *
     * @author Coriolanus
     */
    static class MapEnthaeltEntitaetItem {
        public static Boolean bauen(String item) {
            return ItemUndEntitaetenBelohnungen.bauen.containsKey(item);
        }

        public static Boolean abbauen(String item) {
            return ItemUndEntitaetenBelohnungen.abbauen.containsKey(item);
        }

        public static Boolean craften(String item) {
            return ItemUndEntitaetenBelohnungen.craften.containsKey(item);
        }

        public static Boolean zaehmen(String item) {
            return ItemUndEntitaetenBelohnungen.zaehmen.containsKey(item);
        }

        public static Boolean toeten(String item) {
            return ItemUndEntitaetenBelohnungen.toeten.containsKey(item);
        }

        public static Boolean zuechten(String item) {
            return ItemUndEntitaetenBelohnungen.zuechten.containsKey(item);
        }
    }

}
