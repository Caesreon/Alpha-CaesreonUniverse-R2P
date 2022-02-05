package caesreon.universe.spigot.merkur.modul.regionmarkt;

import caesreon.core.Log;
import caesreon.core.minecraft.BlockVector3;
import caesreon.core.minecraft.Koordinate;

public class RegionsDatenSatz {
    public String plotname;
    public String Welt;
    public BlockVector3 MinVektor3;
    public BlockVector3 MaxVektor3;
    public BlockVector3 infoSchild;
    public String Owners;
    public String Members;
    public int Preis;
    public String Mietbar;
    public int Mietdauer;
    public String Verkaufbar;
    public long zeitstempel;
    public String plottyp;

    /*
     * Kleine Anmkerung fuer mich: DefaultDomain brauch ich in dieser Klasse nicht, da der Besitzer des Grundstücks anhand
     * eines String.contains(UUID) abgefragt wird.
     */
    RegionsDatenSatz(String[] data) {
        plotname = data[0];
        Welt = data[1];
        MinVektor3 = new BlockVector3(data[2]);
        MaxVektor3 = new BlockVector3(data[3]);
        Owners = data[4];
        Members = data[5];
        Mietbar = data[6];
        Mietdauer = Integer.parseInt(data[7]);
        Verkaufbar = data[8];
        Preis = Integer.parseInt(data[9]);
        zeitstempel = Long.parseLong(data[10]);
        infoSchild = new BlockVector3(data[11]);
        plottyp = RegionsTypen.getPlotTypFromString(plotname);
        if (Verkaufbar.contains("false")) {
            Verkaufbar = "Nein";
        }
        if (Verkaufbar.contains("true")) {
            Verkaufbar = "Ja";
        }
        Log.SpigotLogger.Verbose(toString());
    }

    public Koordinate getKoordinate()
    {
        return new Koordinate(MinVektor3.getX(), MinVektor3.getZ());
    }

    public String toString() {
        return plotname + ":" + Welt + ":" + MinVektor3 + ":" + MaxVektor3 + ":" + ":" + Owners + ":" + Members + "Mietdauer" + Mietdauer +
                ":" + Verkaufbar + ":" + Preis + ":" + zeitstempel;
    }
}
