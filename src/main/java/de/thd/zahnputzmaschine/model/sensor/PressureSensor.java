package de.thd.zahnputzmaschine.model.sensor;

import de.thd.zahnputzmaschine.model.IntensityLevel;
import de.thd.zahnputzmaschine.util.SimpleLogger;
import java.util.Random;

/**
 * Drucksensor für die Zahnputzmaschine mit dynamischen Schwellwerten
 * Die Druckschwellen passen sich an die aktuelle Intensität an
 * @author Nikola Valchev
 */
public class PressureSensor implements Sensor {
    private static final SimpleLogger logger = new SimpleLogger(PressureSensor.class);

    // Basis-Schwellwerte (für OFF-Zustand)
    private static final double BASE_WARNING_THRESHOLD = 3.0;
    private static final double BASE_CRITICAL_THRESHOLD = 5.0;

    // Schwellwert-Faktoren für verschiedene Intensitäten
    // Je höher die Intensität, desto niedriger die erlaubten Druckwerte
    private static final double GENTLE_FACTOR = 1.2;    // 120% der Basis = mehr Druck erlaubt
    private static final double NORMAL_FACTOR = 1.0;    // 100% der Basis = Standard
    private static final double INTENSE_FACTOR = 0.6;   // 60% der Basis = weniger Druck erlaubt

    private static final double MAX_PRESSURE = 10.0;
    private static final double MIN_PRESSURE = 0.0;

    private double currentPressure = 0.0;
    private boolean isCalibrated = false;
    private double calibrationOffset = 0.0;
    private Random random = new Random();

    // Aktuelle Intensität für dynamische Schwellwerte
    private IntensityLevel currentIntensity = IntensityLevel.OFF;

    @Override
    public double getCurrentValue() {
        return Math.max(MIN_PRESSURE, Math.min(MAX_PRESSURE, currentPressure + calibrationOffset));
    }

    /**
     * Setzt die aktuelle Intensität für dynamische Schwellwerte
     * @param intensity Die aktuelle Intensitätsstufe
     */
    public void setCurrentIntensity(IntensityLevel intensity) {
        if (this.currentIntensity != intensity) {
            this.currentIntensity = intensity;
            logger.info("Druckschwellen angepasst fuer Intensitaet: " + intensity);
            logger.debug("Neue Warnschwelle: " + getWarningThreshold() + " N");
            logger.debug("Neue kritische Schwelle: " + getCriticalThreshold() + " N");
        }
    }

    /**
     * Berechnet den Schwellwert-Faktor basierend auf der Intensität
     */
    private double getThresholdFactor() {
        switch (currentIntensity) {
            case GENTLE:
                return GENTLE_FACTOR;
            case NORMAL:
                return NORMAL_FACTOR;
            case INTENSE:
                return INTENSE_FACTOR;
            default: // OFF
                return NORMAL_FACTOR;
        }
    }

    @Override
    public boolean isWarningThresholdExceeded() {
        return getCurrentValue() >= getWarningThreshold();
    }

    @Override
    public boolean isCriticalThresholdExceeded() {
        return getCurrentValue() >= getCriticalThreshold();
    }

    @Override
    public void calibrate() {
        logger.info("Kalibriere Drucksensor...");
        calibrationOffset = -currentPressure;
        isCalibrated = true;
        logger.info("Drucksensor kalibriert. Offset: " + calibrationOffset);
    }

    @Override
    public String getUnit() {
        return "N";
    }

    @Override
    public String getName() {
        return "Drucksensor";
    }

    /**
     * Gibt die aktuelle Warnschwelle basierend auf der Intensität zurück
     */
    public double getWarningThreshold() {
        return BASE_WARNING_THRESHOLD * getThresholdFactor();
    }

    /**
     * Gibt die aktuelle kritische Schwelle basierend auf der Intensität zurück
     */
    public double getCriticalThreshold() {
        return BASE_CRITICAL_THRESHOLD * getThresholdFactor();
    }

    /**
     * Simuliert einen Druckwert (für Tests und Demo)
     * @param pressure Druck in Newton
     */
    public void simulatePressure(double pressure) {
        if (pressure < MIN_PRESSURE || pressure > MAX_PRESSURE) {
            logger.warn("Ungueltiger Druckwert: " + pressure + ". Muss zwischen 0 und 10 sein.");
            // Clamp to valid range
            pressure = Math.max(MIN_PRESSURE, Math.min(MAX_PRESSURE, pressure));
        }
        this.currentPressure = pressure;
        logger.info("Druck simuliert: " + pressure + " N");

        // Zeige Warnung wenn Schwellwert für aktuelle Intensität überschritten
        if (isWarningThresholdExceeded() && !isCriticalThresholdExceeded()) {
            logger.warn("Warnung fuer " + currentIntensity + "-Modus bei " + pressure + " N!");
        } else if (isCriticalThresholdExceeded()) {
            logger.error("Kritisch fuer " + currentIntensity + "-Modus bei " + pressure + " N!");
        }
    }

    /**
     * Simuliert realistisches Rauschen im Sensor
     * @param baseValue Basiswert
     * @param noiseLevel Rauschlevel (0.0 - 1.0)
     */
    public void simulateRealisticPressure(double baseValue, double noiseLevel) {
        double noise = (random.nextDouble() - 0.5) * noiseLevel;
        simulatePressure(baseValue + noise);
    }

    /**
     * Gibt Statusinformationen zurück - ASCII Version ohne Emojis
     * @return Formatierter Status-String
     */
    public String getStatus() {
        double value = getCurrentValue();
        String status = "OK";
        if (isCriticalThresholdExceeded()) {
            status = "KRITISCH";
        } else if (isWarningThresholdExceeded()) {
            status = "WARNUNG";
        }

        return String.format("Druck: %.1f %s - Status: %s", value, getUnit(), status);
    }

    /**
     * Gibt erweiterte Statusinformationen mit Schwellwerten zurück
     */
    public String getDetailedStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Drucksensor-Status:\n");
        sb.append(String.format("  Aktueller Druck: %.1f %s\n", getCurrentValue(), getUnit()));
        sb.append(String.format("  Intensitaet: %s\n", currentIntensity));
        sb.append(String.format("  Warnschwelle: %.1f %s (%.0f%% von Basis)\n",
                getWarningThreshold(), getUnit(), getThresholdFactor() * 100));
        sb.append(String.format("  Kritische Schwelle: %.1f %s\n", getCriticalThreshold(), getUnit()));
        sb.append(String.format("  Status: %s\n", getStatus()));
        return sb.toString();
    }

    public boolean isCalibrated() {
        return isCalibrated;
    }

    public IntensityLevel getCurrentIntensity() {
        return currentIntensity;
    }
}
