package caesreon.core;

import caesreon.core.interfaces.ILog;
import caesreon.core.system.ANSITerminalFarben;
import caesreon.main.BungeeMain;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

/**
 * Log-System mit verschiedenen Log-Leveln
 * Verfuegbare Level:
 * SEVERE (hoechstes Level)		fuer kritische Fehler
 * WARNING						fuer Warnungen
 * INFO						fuer Infos
 * CONFIG						fuer Konfiguration
 * FINE
 * FINER						fuer Debug-Meldungen
 * FINEST (geringstes Level)	fuer ausfuehrlichste Meldungen
 * //TODO: Vermutlich alle SpigotLogger im Core entfernern und durch allgemeinen SystemLogger / JavaLogger ersetzen
 */
public class Log {
    private static final String prefix = ChatColor.GOLD + "[CAE] ";

    /**
     * Logger, welcher ausschliesslich für die Spigot Komponente in diesem Programm zuständig ist.
     * @implNote Darf nicht in BungeeCord Methoden benutzt werden, da BungeeCord die Bukkit und Spigot  importe nicht kennt!
     */
    public static ILog SpigotLogger = new ILog() {
        @Override
        public void Error(String message) {
            Bukkit.getLogger().severe(Log.prefix + ChatColor.RED + message);
        }

        @Override
        public void Warning(String message) {
            Bukkit.getLogger().warning(Log.prefix + ChatColor.YELLOW + message);
        }

        @Override
        public void Info(String message) {
            Bukkit.getLogger().info(Log.prefix + ChatColor.WHITE + message);
        }

        @Override
        public void Dev(String message) {
            Bukkit.getLogger().info(Log.prefix + ChatColor.DARK_AQUA + message);
        }

        @Override
        public void Init(String message) {
            Bukkit.getLogger().info(Log.prefix + ChatColor.BLUE + message);
        }

        @Override
        public void InitErfolgreich(String message) {
            Bukkit.getLogger().info(Log.prefix + ChatColor.GREEN + message);
        }

        @Override
        public void Config(String message) {
            Bukkit.getLogger().config(Log.prefix + ChatColor.GRAY + message);
        }

        @Override
        public void Debug(String message) {
            Bukkit.getLogger().info(Log.prefix + ChatColor.AQUA + message);
        }

        @Override
        public void Debug(int message) {
            Bukkit.getLogger().fine(Log.prefix + ChatColor.AQUA + message);
        }

        @Override
        public void Debug(double message) {
            Bukkit.getLogger().fine(Log.prefix + ChatColor.AQUA + message);
        }

        @Override
        public void Verbose(String message) {
            Bukkit.getLogger().fine(Log.prefix + ChatColor.LIGHT_PURPLE + message);
        }
    };

    /**
     * Logger, welcher ausschliesslich für die BungeeCord Komponente in diesem Programm zuständig ist.
     * @implNote Darf nicht in Spigot Methoden benutzt werden, da Spigot die Bungee importe nicht kennt!
     */
    public static ILog BungeeLogger = new ILog() {
        @Override
        public void Error(String message) {
            BungeeMain.getInstance().getLogger().severe(Log.prefix + ChatColor.RED + message);
        }

        @Override
        public void Warning(String message) {
            BungeeMain.getInstance().getLogger().warning(Log.prefix + ChatColor.YELLOW + message);
        }

        @Override
        public void Info(String message) {
            BungeeMain.getInstance().getLogger().info(Log.prefix + ChatColor.WHITE + message);
        }

        @Override
        public void Dev(String message) {
            BungeeMain.getInstance().getLogger().info(Log.prefix + ChatColor.DARK_AQUA + message);
        }

        @Override
        public void Init(String message) {
            BungeeMain.getInstance().getLogger().info(Log.prefix + ChatColor.BLUE + message);
        }

        @Override
        public void InitErfolgreich(String message) {
            BungeeMain.getInstance().getLogger().info(Log.prefix + ChatColor.GREEN + message);
        }

        @Override
        public void Config(String message) {
            BungeeMain.getInstance().getLogger().config(Log.prefix + ChatColor.GRAY + message);
        }

        @Override
        public void Debug(String message) {
            BungeeMain.getInstance().getLogger().fine(Log.prefix + ChatColor.AQUA + message);
        }

        @Override
        public void Debug(int message) {
            BungeeMain.getInstance().getLogger().fine(Log.prefix + ChatColor.AQUA + message);
        }

        @Override
        public void Debug(double message) {
            BungeeMain.getInstance().getLogger().fine(Log.prefix + ChatColor.AQUA + message);
        }

        @Override
        public void Verbose(String message) {
            BungeeMain.getInstance().getLogger().finest(Log.prefix + ChatColor.LIGHT_PURPLE + message);
        }
    };

    /**
     * Nativer Logger welcher rein über die Java Konsole läuft und auf den UTF-8 ANSI Escape Zeichen basiert. Wird benötigt, um Core Funktionen außerhalb der Spigot und Bungee
     * Architektur zu loggen.
     */
    public static ILog JavaLogger = new ILog() {

        final String prefixANSI = ANSITerminalFarben.SchriftFarbe.GRUEN + "CAE";
        private final Logger nativeLogger = Logger.getLogger(prefixANSI);
        @Override
        public void Error(String message) {
            nativeLogger.severe(ANSITerminalFarben.SchriftFarbe.ROT + message);
        }

        @Override
        public void Warning(String message) {
            nativeLogger.warning(ANSITerminalFarben.SchriftFarbe.GELB + message);
        }

        @Override
        public void Info(String message) {
            nativeLogger.info(ANSITerminalFarben.SchriftFarbe.WEISS + message);
        }

        @Override
        public void Dev(String message) {
            nativeLogger.severe(ANSITerminalFarben.SchriftFarbe.CYAN + message);
        }

        @Override
        public void Init(String message) {
            nativeLogger.info(ANSITerminalFarben.SchriftFarbe.BLAU + message);
        }

        @Override
        public void InitErfolgreich(String message) {
            nativeLogger.severe(ANSITerminalFarben.SchriftFarbe.GRUEN + message);
        }

        @Override
        public void Config(String message) {
            nativeLogger.severe(ANSITerminalFarben.SchriftFarbe.BLAU + message);
        }

        @Override
        public void Debug(String message) {
            nativeLogger.fine(ANSITerminalFarben.SchriftFarbe.CYAN + message);
        }

        @Override
        public void Debug(int message) {
            nativeLogger.fine(ANSITerminalFarben.SchriftFarbe.CYAN + message);
        }

        @Override
        public void Debug(double message) {
            nativeLogger.fine(ANSITerminalFarben.SchriftFarbe.CYAN + message);
        }

        @Override
        public void Verbose(String message) {
            nativeLogger.finest(ANSITerminalFarben.SchriftFarbe.PURPUR + message);
        }

        /**
         * Methode zum ändern des Log-Levels während der Runtime
         * @param level Das gewünschte Log Level
         * @implNote setLevel kann hier nur vom nativen Logger benutzt werden. Der BukkitLogger hat diese Funktion nie vorgesehen
         * und funktioniert dort nicht
         */
        public void setLevel(java.util.logging.Level level) {
            nativeLogger.setLevel(level);
        }
    };
}
