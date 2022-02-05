package caesreon.core.hilfsklassen;

import caesreon.core.ColorCodeParser;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;

public class StringUT {
    private static String[] SubStrings;
    private static String[] TempData;
    private static ArrayList<String> L = new ArrayList<>();

    public static String ZufallsToken(int Laenge) {
        return RandomStringUtils.randomAlphanumeric(Laenge).toUpperCase();
    }

    public static String ParseColorCodes(String Input, String Regex) {
        return ColorCodeParser.Parse(Input, Regex);
    }

    public final static String IgnoreCase(String Input) {
        return Input.toLowerCase();
    }

    /**
     * Diese Methode ermittelt die Indexposition eines bestimmten Zeichens anhand einer bestimmten Hauefigkeit innerhalb eines Strings.
     * Angenommen es gibt einen String '1.2.3.4' und ich möchte die Indexposition des 2. '.' wissen, so ermittelt diese Methode die Indexposition 3
     *
     * @param Input
     * @param Char
     * @param AnzahlChars
     * @return
     */
    public static int findeVonDefiniertesZeichenPositionInString(String Input, String Char, int AnzahlChars) {
        int result = -1;
        char[] ca = Input.toCharArray();
        for (int i = 0; i < ca.length; ++i) {
            if (ca[i] == Char.charAt(0)) --AnzahlChars;
            if (AnzahlChars == 0) return i;
        }
        return result;
    }
}