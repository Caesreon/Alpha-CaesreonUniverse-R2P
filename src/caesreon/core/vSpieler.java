package caesreon.core;

import caesreon.core.skynet.SpielerLookup;

import java.util.UUID;

public class vSpieler {
    private String spielername;
    private UUID uuid;

    public vSpieler(String spielername, UUID uuid) {
        this.spielername = spielername;
        this.uuid = uuid;
    }

    public vSpieler(String spielername) {
        this.spielername = spielername;
        this.uuid = SpielerLookup.SchliesseConnection.getUUID(spielername);
    }

    public vSpieler(UUID uuid) {
        this.spielername = SpielerLookup.SchliesseConnection.getSpielername(uuid);
        this.uuid = uuid;
    }

    public String getSpielername() {
        return spielername;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Spieler zuSpieler() {
        return new Spieler(spielername, uuid, true);
    }
}
