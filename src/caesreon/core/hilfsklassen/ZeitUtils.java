package caesreon.core.hilfsklassen;

import caesreon.core.Log;

import java.util.Date;

public class ZeitUtils {
    public static long erhalteAktuellenZeitstempel() {
        Date aktuellesDatum = new Date();
        Log.SpigotLogger.Debug(String.valueOf(aktuellesDatum.getTime()));
        return aktuellesDatum.getTime();
    }

    public static long erhalteZeitstempelVonDatum(Date datum) {
        Log.SpigotLogger.Debug(String.valueOf(datum.getTime()));
        return datum.getTime();
    }

    public static Date erhalteDatevonZeitstempel(long zeitstempel) {
        Date temp = new Date();
        temp.setTime(zeitstempel);
        Log.SpigotLogger.Debug(String.valueOf(zeitstempel));
        return temp;
    }

}
