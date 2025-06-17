package de.thd.zahnputzmaschine.system;

import de.thd.zahnputzmaschine.listener.PressureListener;
import de.thd.zahnputzmaschine.util.SimpleLogger;

/**
 * Warnsystem für die Zahnputzmaschine - ASCII Version
 * @author Nikola Valchev
 */
public class WarningSystem implements PressureListener {
    private static final SimpleLogger logger = new SimpleLogger(WarningSystem.class);

    private LEDColor statusLED = LEDColor.OFF;
    private LEDColor warningLED = LEDColor.OFF;
    private boolean isActive = false;
    private long lastWarningTime = 0;
    private static final long WARNING_COOLDOWN = 1000; // 1 Sekunde

    @Override
    public void onPressureChanged(double pressure) {
        // Logging nur bei signifikanten Änderungen
        if (Math.abs(pressure) > 0.1) {
            logger.debug("Druck geaendert: " + pressure + " N");
        }
    }

    @Override
    public void onWarningThreshold(double pressure) {
        if (System.currentTimeMillis() - lastWarningTime > WARNING_COOLDOWN) {
            logger.warn("Warnschwelle ueberschritten: " + pressure + " N");
            activateWarning();
            lastWarningTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onCriticalThreshold(double pressure) {
        logger.error("Kritische Schwelle ueberschritten: " + pressure + " N");
        activateCriticalWarning();
    }

    @Override
    public void onPressureNormal() {
        logger.info("Druck wieder normal");
        deactivateWarnings();
    }

    /**
     * Aktiviert das Warnsystem
     */
    public void activate() {
        isActive = true;
        logger.info("Warnsystem aktiviert");
    }

    /**
     * Deaktiviert das Warnsystem
     */
    public void deactivate() {
        isActive = false;
        deactivateWarnings();
        logger.info("Warnsystem deaktiviert");
    }

    /**
     * Aktiviert Warnung (gelbe LED)
     */
    private void activateWarning() {
        if (isActive) {
            warningLED = LEDColor.YELLOW_BLINKING;
            System.out.println("[!] WARNUNG: Zu hoher Druck!");
            System.out.println("    LED: " + warningLED.getDisplayName());
        }
    }

    /**
     * Aktiviert kritische Warnung (rote LED)
     */
    private void activateCriticalWarning() {
        if (isActive) {
            warningLED = LEDColor.RED_BLINKING;
            System.out.println("[!!!] KRITISCH: Viel zu hoher Druck!");
            System.out.println("      LED: " + warningLED.getDisplayName());
            System.out.println("      Bitte Druck reduzieren!");
        }
    }

    /**
     * Deaktiviert alle Warnungen
     */
    private void deactivateWarnings() {
        if (warningLED != LEDColor.OFF) {
            warningLED = LEDColor.OFF;
            System.out.println("[OK] Druck wieder normal");
        }
    }

    /**
     * Setzt die Status-LED (für normale Betriebszustände)
     * @param color LED-Farbe
     */
    public void setStatusLED(LEDColor color) {
        this.statusLED = color;
    }

    /**
     * Gibt den aktuellen LED-Status zurück
     * @return Array mit [statusLED, warningLED]
     */
    public LEDColor[] getLEDStatus() {
        return new LEDColor[] { statusLED, warningLED };
    }

    public boolean isActive() {
        return isActive;
    }
}