package de.thd.zahnputzmaschine.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Erweiterte Logger-Klasse für die Zahnputzmaschine
 * Iteration 2: zusätzliche Log-Level
 * @author Nikola Valchev
 */
public class SimpleLogger {
    private final String className;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Log-Level Enum
    public enum LogLevel {
        DEBUG(0, "DEBUG", "\u001B[36m"),    // Cyan
        INFO(1, "INFO", "\u001B[32m"),      // Green
        WARN(2, "WARN", "\u001B[33m"),      // Yellow
        ERROR(3, "ERROR", "\u001B[31m");    // Red

        private final int level;
        private final String name;
        private final String colorCode;

        LogLevel(int level, String name, String colorCode) {
            this.level = level;
            this.name = name;
            this.colorCode = colorCode;
        }
    }

    // Aktuelles Log-Level (kann konfiguriert werden)
    private static LogLevel currentLogLevel = LogLevel.INFO;
    private static final String RESET_COLOR = "\u001B[0m";
    private static boolean useColors = true;

    public SimpleLogger(Class<?> clazz) {
        this.className = clazz.getSimpleName();
    }

    /**
     * Info-Level Logging (wie in Iteration 1)
     * @param message Die Log-Nachricht
     */
    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    /**
     * Debug-Level Logging (NEU in Iteration 2)
     * @param message Die Log-Nachricht
     */
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    /**
     * Warning-Level Logging (NEU in Iteration 2)
     * @param message Die Log-Nachricht
     */
    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    /**
     * Error-Level Logging (NEU in Iteration 2)
     * @param message Die Log-Nachricht
     */
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    /**
     * Zentrale Log-Methode
     * @param level Das Log-Level
     * @param message Die Nachricht
     */
    private void log(LogLevel level, String message) {
        // Nur loggen wenn Level >= currentLogLevel
        if (level.level < currentLogLevel.level) {
            return;
        }

        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] [%s] [%s] %s",
                timestamp,
                level.name,
                className,
                message
        );

        // Mit oder ohne Farben ausgeben
        if (useColors && System.console() != null) {
            System.out.println(level.colorCode + logMessage + RESET_COLOR);
        } else {
            System.out.println(logMessage);
        }
    }

    // Statische Konfigurationsmethoden

    /**
     * Setzt das globale Log-Level
     * @param level Das neue Log-Level
     */
    public static void setLogLevel(LogLevel level) {
        currentLogLevel = level;
    }

    /**
     * Gibt das aktuelle Log-Level zurück
     * @return Das aktuelle Log-Level
     */
    public static LogLevel getLogLevel() {
        return currentLogLevel;
    }

    /**
     * Aktiviert/Deaktiviert farbige Ausgabe
     * @param enabled true für Farben, false für plain text
     */
    public static void setUseColors(boolean enabled) {
        useColors = enabled;
    }

    /**
     * Hilfsmethode zum Setzen des Log-Levels über String
     * @param levelName Name des Log-Levels (DEBUG, INFO, WARN, ERROR)
     */
    public static void setLogLevel(String levelName) {
        try {
            setLogLevel(LogLevel.valueOf(levelName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            System.err.println("Ungültiges Log-Level: " + levelName);
        }
    }
}
