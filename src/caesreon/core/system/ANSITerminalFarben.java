package caesreon.core.system;

public class ANSITerminalFarben {

    public static class Hintergrund {
        public static final String SCHWARZ = "\u001B[40m";
        public static final String ROT = "\u001B[41m";
        public static final String GRUEN = "\u001B[42m";
        public static final String GELB = "\u001B[43m";
        public static final String BLAU = "\u001B[44m";
        public static final String PURPUR = "\u001B[45m";
        public static final String CYAN = "\u001B[46m";
        public static final String WEISS = "\u001B[47m";
        public static final String RESET = "\u001B[0m";
    }
    public static class SchriftFarbe{
        public static final String ROT = "\u001B[31m";
        public static final String GRUEN = "\u001B[32m";
        public static final String GELB = "\u001B[33m";
        public static final String BLAU = "\u001B[34m";
        public static final String PURPUR = "\u001B[35m";
        public static final String CYAN = "\u001B[36m";
        public static final String WEISS = "\u001B[37m";
        public static final String SCHWARZ = "\u001B[30m";
    }
}
