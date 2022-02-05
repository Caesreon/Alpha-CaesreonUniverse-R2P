package caesreon.core;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;


public class ColorCodeParser {
    public final static String ColorPrefixParagraph = "§";
    private static String[] TempData;
    private static ArrayList<String> L = new ArrayList<>();

    /**
     * Bevorzugte Methode zum einlesen von GUI-Strings
     *
     * @param input Der String, welcher verarbeitet werden
     * @param regex Die Regular Expression, anhand welche der ColorCode identifiziert wird
     * @return Der mit den ColorCodes verarbeitete String
     * @implNote zum Einlesen von regulären Konfigurations-Strings ParseString() benutzen!!
     */
    @Deprecated
    public static String Parse(String input, String regex) {
        if (regex.equals("§")) {
            regex = "&";
            input = input.replace("§", "&");
        }

        if (regex.equals("&")) {
            SplitString(entferneUmlauteInString(input), regex);
        }
        TempData = L.toArray(new String[0]);
        //Hier wird String wieder zusammengefund returned
        return String.join("", TempData) + "\n";
    }

    /**
     * Bevorzugte Methode zum einlesen von Konfigurations-Strings
     *
     * @param input Der String, welcher verarbeitet werden
     * @param regex Die Regular Expression, anhand welche der ColorCode identifiziert wird
     * @return Der mit den ColorCodes verarbeitete String
     */
    public static String ParseString(String input, String regex) {
        Log.SpigotLogger.Verbose(input + ":" + regex);
        if (!input.contains(regex)) {
            Log.SpigotLogger.Debug(input + "kein regex vorhanden");
            return entferneUmlauteInString(input);
        } else if (regex.equals("§")) {
            regex = "&";
            input = input.replace("§", "&");
            SplitString(entferneUmlauteInString(input), regex);
        } else if (regex.equals("&")) {
            SplitString(entferneUmlauteInString(input), regex);
        }

        TempData = L.toArray(new String[0]);
        Log.SpigotLogger.Verbose(String.join("", TempData));
        //Hier wird String wieder zusammengefund returned
        return String.join("", TempData);
    }

    public static List<String> ParseList(List<String> Liste, String Regex) {
        List<String> Data = new ArrayList<>();
        for (String s : Liste) {

            if (Regex.equals("§")) {
                Regex = "&";
            }

            if (Regex.equals("&")) {
                SplitString(entferneUmlauteInString(s), Regex);
            }
            TempData = L.toArray(new String[0]);
            Data.add(String.join("", TempData));
        }
        //Hier wird String wieder zusammengefund returned
        return Data;
    }


    private static String entferneUmlauteInString(String S) {
        S = S.replace("\\u00C4", "Ä");
        S = S.replace("\\u00D6", "Ö");
        S = S.replace("\\u00DC", "Ü");
        S = S.replace("\\u00F4", "ä");
        S = S.replace("\\u00F6", "ö");
        S = S.replace("\\u00FC", "ü");
        S = S.replace("\\umlS", "ß");
        S = S.replace("\\umlAE", "Ä");
        S = S.replace("\\umlOE", "Ö");
        S = S.replace("\\umlUE", "Ü");
        S = S.replace("\\umlae", "ä");
        S = S.replace("\\umloe", "ö");
        S = S.replace("\\umlue", "ü");
        S = S.replace("\\umls", "ß");
        return S;
    }

    private static void SplitString(String Data, String Regex) {
        //Reinitaliseren der Arrays
        String[] subStrings;
        L = new ArrayList<>();
        subStrings = Data.split(Regex);
        for (String S : subStrings) {
            S = Regex + S;
            try {
                Switch_V1(S);
            } catch (Exception e) {
                //Wenn String eine leere Zeile ist, soll diese einfach nur als Zeilenumbruch zurgegeben werden
                ProcessString();
            }
        }

    }

    private static void Switch_V1(String S) {
        switch (S.charAt(0) + String.valueOf(S.charAt(1))) {
            case CodesV1.Black:
                zuListeHinzufuegen_V1(CodesV1.Black, ChatColor.BLACK, S);
                break;
            case CodesV1.DarkBlue:
                zuListeHinzufuegen_V1(CodesV1.DarkBlue, ChatColor.DARK_BLUE, S);
                break;
            case CodesV1.DarkGreen:
                zuListeHinzufuegen_V1(CodesV1.DarkGreen, ChatColor.DARK_GREEN, S);
                break;
            case CodesV1.DarkAqua:
                zuListeHinzufuegen_V1(CodesV1.DarkAqua, ChatColor.DARK_AQUA, S);
                break;
            case CodesV1.DarkRed:
                zuListeHinzufuegen_V1(CodesV1.DarkRed, ChatColor.DARK_RED, S);
                break;
            case CodesV1.DarkPurple:
                zuListeHinzufuegen_V1(CodesV1.DarkPurple, ChatColor.DARK_PURPLE, S);
                break;
            case CodesV1.Gold:
                zuListeHinzufuegen_V1(CodesV1.Gold, ChatColor.GOLD, S);
                break;
            case CodesV1.Gray:
                zuListeHinzufuegen_V1(CodesV1.Gray, ChatColor.GRAY, S);
                break;
            case CodesV1.DarkGray:
                zuListeHinzufuegen_V1(CodesV1.DarkGray, ChatColor.DARK_GRAY, S);
                break;
            case CodesV1.Blue:
                zuListeHinzufuegen_V1(CodesV1.Blue, ChatColor.BLUE, S);

                break;
            case CodesV1.Green:
                zuListeHinzufuegen_V1(CodesV1.Green, ChatColor.GREEN, S);
                break;
            case CodesV1.Aqua:
                zuListeHinzufuegen_V1(CodesV1.Aqua, ChatColor.AQUA, S);
                break;
            case CodesV1.LightPurple:
                zuListeHinzufuegen_V1(CodesV1.LightPurple, ChatColor.LIGHT_PURPLE, S);
                break;
            case CodesV1.Red:
                zuListeHinzufuegen_V1(CodesV1.Red, ChatColor.RED, S);
                break;
            case CodesV1.Yellow:
                zuListeHinzufuegen_V1(CodesV1.Yellow, ChatColor.YELLOW, S);
                break;
            case CodesV1.White:
                zuListeHinzufuegen_V1(CodesV1.White, ChatColor.WHITE, S);
                break;
            case CodesV1.Bold:
                zuListeHinzufuegen_V1(CodesV1.Bold, ChatColor.BOLD, S);
                break;
            case CodesV1.Durchgestrichen:
                zuListeHinzufuegen_V1(CodesV1.Durchgestrichen, ChatColor.STRIKETHROUGH, S);
                break;
            case CodesV1.Italic:
                zuListeHinzufuegen_V1(CodesV1.Italic, ChatColor.ITALIC, S);
                break;
            default:
                zuListeHinzufuegen(S);
                break;
        }
    }

    //TODO: Der Hier macht die Dopplungen EL Probleme macher
    private static void zuListeHinzufuegen_V1(String ColorCode, ChatColor Color, String S) {
        try {
            if (!L.contains(S)
                    || !L.contains(CodesV1.Black + S)
                    || !L.contains(CodesV1.Aqua + S)
                    || !L.contains(CodesV1.Red + S)
                    || !L.contains(CodesV1.Blue + S)
                    || !L.contains(CodesV1.Gold + S)
                    || !L.contains(CodesV1.White + S)
                    || !L.contains(CodesV1.Green + S)
                    || !L.contains(CodesV1.Gray + S)
                    || !L.contains(CodesV1.DarkGray + S)
                    || !L.contains(CodesV1.DarkGreen + S)
                    || !L.contains(CodesV1.DarkPurple + S)
                    || !L.contains(CodesV1.LightPurple + S)
                    || !L.contains(CodesV1.DarkRed + S)
            ) {
                L.add(ProcessString(ColorCode, Color, S));
            }
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
    }

    private static void zuListeHinzufuegen(String S) {
        if (!L.contains(S)) {
            L.add(ProcessString(S));
        }
    }

    private static String ProcessString(String Farbe, ChatColor Color, String Input) {
        String Temp = "";
        try {
            Temp = Color + Input.substring(2) + ChatColor.RESET;
        } catch (Exception e) {
            Log.SpigotLogger.Debug(e.toString());
        }
        return Temp;
    }

    private static String ProcessString(String Input) {
        return Input.substring(1) + ChatColor.RESET;
    }

    private static String ProcessString() {
        return "";
    }

    protected static class CodesV1 {
        public static final String Black = "&0";
        public static final String DarkBlue = "&1";
        public static final String DarkGreen = "&2";
        public static final String DarkAqua = "&3";
        public static final String DarkRed = "&4";
        public static final String DarkPurple = "&5";
        public static final String Gold = "&6";
        public static final String Gray = "&7";
        public static final String DarkGray = "&8";
        public static final String Blue = "&9";
        public static final String Green = "&a";
        public static final String Aqua = "&b";
        public static final String Red = "&c";
        public static final String LightPurple = "&d";
        public static final String Yellow = "&e";
        public static final String White = "&f";

        public static final String Random = "&k";
        public static final String Bold = "&l";
        public static final String Durchgestrichen = "&m";
        public static final String Unterstrich = "&n";
        public static final String Italic = "&o";
        public static final String Reset = "&r";
    }

}
