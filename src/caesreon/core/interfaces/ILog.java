package caesreon.core.interfaces;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public interface ILog {
    String prefix = ChatColor.GOLD + "[CAE] ";

    void Error(String message);

    /**
     * Logging fuer Warnungen
     *
     * @param message Nachricht
     */
    void Warning(String message);

    /**
     * Logging fuer Informationen
     *
     * @param message Nachricht
     */
    void Info(String message);

    /**
     * Logging welches nur beim Development eines Moduls genutzt wird um Dinge nachzuvollziehen
     *
     * @param message Nachricht
     */
    void Dev(String message);

    /**
     * Logging fuer initiale Startprozesse
     *
     * @param message Nachricht
     */
    void Init(String message);

    /**
     * Logging fuer erfolgreiche Initialisierungen
     *
     * @param message Nachricht
     */
    void InitErfolgreich(String message);

    /**
     * Logging fuer Konfigurations-Werte
     *
     * @param message Nachricht
     */
    void Config(String message);

    /**
     * Logging fuer Debug-Meldungen
     *
     * @param message Nachricht
     */
    void Debug(String message);

    /**
     * Logging fuer Debug-Meldungen
     *
     * @param message Nachricht
     */
    void Debug(int message);

    /**
     * Logging fuer Debug-Meldungen
     *
     * @param message Nachricht
     */
    void Debug(double message);

    /**
     * Logging fuer Verbose-Meldungen
     *
     * @param message Nachricht
     */
    void Verbose(String message);
}
