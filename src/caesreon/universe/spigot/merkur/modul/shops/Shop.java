package caesreon.universe.spigot.merkur.modul.shops;

import caesreon.core.Log;
import caesreon.core.minecraft.Koordinate;

import java.util.UUID;

public class Shop {
    private UUID uuid;
    private Koordinate koordinateSchild;
    private Koordinate koordinateKiste;
    private String item_id;
    private int anzahl;
    private int preis;
    private String meta_daten;

    public Shop(String[] args) {
        uuid = UUID.fromString(args[0]);
        koordinateSchild = new Koordinate(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        koordinateKiste = new Koordinate(Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]));
        item_id = args[7];
        anzahl = Integer.parseInt(args[8]);
        preis = Integer.parseInt(args[9]);
        meta_daten = args[10];
        for (String s : args)
            Log.SpigotLogger.Verbose(s);
    }

    public Shop(String uuid, Koordinate kiste, Koordinate schild, String item_id, int anzahl, int preis, String meta) {
        this.uuid = UUID.fromString(uuid);
        this.koordinateSchild = schild;
        this.koordinateKiste = kiste;
        this.item_id = item_id;
        this.anzahl = anzahl;
        this.preis = preis;
        this.meta_daten = meta;
    }

    public Shop(Shop data) {
        this.uuid = data.getUuid();
        this.koordinateSchild = data.getKoordinateSchild();
        this.koordinateKiste = data.getKoordinateKiste();
        this.item_id = data.getItem_id();
        this.anzahl = data.getAnzahl();
        this.preis = data.getPreis();
        this.meta_daten = data.getMeta_daten();
    }

    public String toString() {
        return "uuid= " + uuid + " p1=" + koordinateSchild.toString()
                + " p2=" + koordinateKiste.toString() + " item_id=" + item_id + " anzahl=" + anzahl + " preis=" + preis
                + " meta=" + meta_daten;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Koordinate getKoordinateSchild() {
        return koordinateSchild;
    }

    public void setKoordinateSchild(Koordinate koordinateSchild) {
        this.koordinateSchild = koordinateSchild;
    }

    public Koordinate getKoordinateKiste() {
        return koordinateKiste;
    }

    public void setKoordinateKiste(Koordinate koordinateKiste) {
        this.koordinateKiste = koordinateKiste;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public void setAnzahl(int anzahl) {
        this.anzahl = anzahl;
    }

    public int getPreis() {
        return preis;
    }

    public void setPreis(int preis) {
        this.preis = preis;
    }

    public String getMeta_daten() {
        return meta_daten;
    }

    public void setMeta_daten(String meta_daten) {
        this.meta_daten = meta_daten;
    }
}