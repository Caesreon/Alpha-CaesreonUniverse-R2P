package caesreon.universe.spigot.jupiter.modul.auktionshaus;

import java.util.Date;

public class Auktion {
    public String AuktionsName = null;
    public Date StartAuction = null;
    public Date EndAuction = null;
    public String Spielername = null;
    public Double Mindestpreis = null;
    public Double GebotenerPreis = null;

    public Auktion() {
    }

    public Auktion(String Auktionsname, Date StartAuction, Date EndAuction, Double Mindestpreis, Double GebotenerPreis) {
        this.AuktionsName = Auktionsname;
        this.StartAuction = StartAuction;
        this.EndAuction = EndAuction;
        this.Mindestpreis = Mindestpreis;
        this.GebotenerPreis = GebotenerPreis;
    }

}
