package caesreon.universe.spigot.merkur.modul.wirtschaft;

import java.util.UUID;

public class WirtschaftsKonto {
    private UUID uuid;
    private double kontostand;
    private boolean erfolgreichSynchronisiert;

    /**
     * @param uuid           - Eindeutige UUID des Spielers oder vSpielers
     * @param geld           - der Kontostand
     * @param synchronisiert - true = erfolgreich aus Datenbank geladen, false = Fehlerhafter Datensatz durch
     *                       Fehler beim laden der Daten
     */
    public WirtschaftsKonto(UUID uuid, double geld, Boolean synchronisiert) {
        this.setUuid(uuid);
        this.setKontostand(geld);
        this.setErfolgreichSynchronisiert(synchronisiert);
    }


    public UUID getUuid() {
        return uuid;
    }


    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    public double getKontostand() {
        return kontostand;
    }


    public void setKontostand(double kontostand) {
        this.kontostand = kontostand;
    }


    public boolean isErfolgreichSynchronisiert() {
        return erfolgreichSynchronisiert;
    }


    public void setErfolgreichSynchronisiert(boolean erfolgreichSynchronisiert) {
        this.erfolgreichSynchronisiert = erfolgreichSynchronisiert;
    }
}
