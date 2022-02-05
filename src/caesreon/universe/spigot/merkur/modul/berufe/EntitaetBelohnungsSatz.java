package caesreon.universe.spigot.merkur.modul.berufe;

/**
 * Diese Klasse soll alle Items bzw Entitaeten in Relation zu ihrer Belohnung und ihrem Beruf festhalten
 *
 * @author Coriolanus_S
 */
public class EntitaetBelohnungsSatz {
    public String entitaet;
    public int einkommen;
    public int erfahrung;
    public Beruf beruf;

    /**
     * @param entitaet Item oder Wesen
     * @param einkommen
     * @param erfahrung
     * @param beruf
     */
    public EntitaetBelohnungsSatz(String entitaet, int einkommen, int erfahrung, Beruf beruf) {
        this.entitaet = entitaet;
        this.einkommen = einkommen;
        this.erfahrung = erfahrung;
        this.beruf = beruf;
    }
}
