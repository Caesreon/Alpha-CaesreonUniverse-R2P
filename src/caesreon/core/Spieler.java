package caesreon.core;

import caesreon.core.hilfsklassen.SpielerUT;
import caesreon.core.minecraft.Inventar;
import caesreon.core.skynet.SpielerLookup;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class Spieler {
    private String spielername;
    private UUID uuid;
    private long zuerst_gesehen;
    private long zuletzt_gesehen;
    private Player bukkitSpieler;
    private Boolean istVSpieler;
    private Inventar inventar;

    public Spieler(String spielername, UUID uuid) {
        this.spielername = spielername;
        this.uuid = uuid;
        bukkitSpieler = SpielerUT.getPlayer(uuid);
        istVSpieler = false;
    }

    public Spieler(String spielername, UUID uuid, Boolean vSpieler) {
        this.spielername = spielername;
        this.uuid = uuid;
        bukkitSpieler = null;
        istVSpieler = true;
    }

    public Spieler(String spielername) {
        this.spielername = spielername;
        Log.SpigotLogger.Debug(spielername);
        try {
            String[] temp = Objects.requireNonNull(SpielerLookup.SchliesseConnection.getSpielerDatenSatzAnhandSpielername(spielername));
            this.uuid = UUID.fromString(temp[0]);
            this.zuerst_gesehen = Long.parseLong(temp[1]);
            this.zuletzt_gesehen = Long.parseLong(Objects.requireNonNull(temp[2]));
            bukkitSpieler = SpielerUT.getPlayer(uuid);
            istVSpieler = false;
            Log.SpigotLogger.Debug("<Spieler>:" + Objects.requireNonNull(uuid).toString());
        } catch (Exception e) {
            try {
                this.uuid = SpielerLookup.SchliesseConnection.getUUID(spielername);
                bukkitSpieler = null;
                istVSpieler = true;
            } catch (Exception ex) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
    }

    public Spieler(UUID uuid) {
        this.uuid = uuid;
        try {
            String[] temp = Objects.requireNonNull(SpielerLookup.SchliesseConnection.getSpielerDatenSatzAnhandUUID(uuid));
            this.spielername = temp[0];
            this.zuerst_gesehen = Long.parseLong(temp[1]);
            this.zuletzt_gesehen = Long.parseLong(Objects.requireNonNull(temp[2]));
            bukkitSpieler = SpielerUT.getPlayer(uuid);
            istVSpieler = false;
        } catch (Exception e) {
            try {
                this.spielername = SpielerLookup.SchliesseConnection.getSpielername(uuid);
                bukkitSpieler = null;
                istVSpieler = true;
            } catch (Exception ex) {
                Log.SpigotLogger.Debug(e.toString());
            }
        }
        istVSpieler = false;
    }

    public Spieler(Player spieler) {
        this.spielername = spieler.getDisplayName();
        this.uuid = spieler.getUniqueId();
        String[] temp = Objects.requireNonNull(SpielerLookup.SchliesseConnection.getSpielerDatenSatzAnhandUUID(uuid));
        this.zuerst_gesehen = Long.parseLong(temp[1]);
        this.zuletzt_gesehen = Long.parseLong(Objects.requireNonNull(temp[2]));
        bukkitSpieler = spieler;
        istVSpieler = false;
    }

    public Boolean istOnline() {
        if (bukkitSpieler != null)
            return getBukkitSpieler().isOnline();
        return false;
    }

    public String getSpielername() {
        return spielername;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getBukkitSpieler() {
        return bukkitSpieler;
    }

    public Boolean getIstVSpieler() {
        return istVSpieler;
    }

    public long getZuerst_gesehen() {
        return zuerst_gesehen;
    }

    public long getZuletzt_gesehen() {
        return zuletzt_gesehen;
    }

}
