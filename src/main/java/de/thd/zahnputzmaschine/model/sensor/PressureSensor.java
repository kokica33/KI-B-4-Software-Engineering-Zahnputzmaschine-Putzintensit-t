package de.thd.zahnputzmaschine.model.sensor;

import de.thd.zahnputzmaschine.model.IntensityLevel;
import de.thd.zahnputzmaschine.util.SimpleLogger;

import java.util.Random;

public class PressureSensor implements Sensor {
    private static final SimpleLogger logger = new SimpleLogger(PressureSensor.class);

    public enum PressureStatus {
        NORMAL,
        WARNING,
        CRITICAL;
    }

    private static final double BASE_WARNING_THRESHOLD = 3.0;
    private static final double BASE_CRITICAL_THRESHOLD = 5.0;
    private static final double GENTLE_FACTOR = 1.0;
    private static final double NORMAL_FACTOR = 1.0;
    private static final double INTENSE_FACTOR = 1.0;

    private static final double MAX_PRESSURE = 10.0;
    private static final double MIN_PRESSURE = 0.0;

    private double currentPressure = 0.0;
    private boolean isCalibrated = false;
    private double calibrationOffset = 0.0;
    private Random random = new Random();
    private IntensityLevel currentIntensity = IntensityLevel.OFF;

    public double getCurrentValue() {
        return Math.max(MIN_PRESSURE, Math.min(MAX_PRESSURE, currentPressure + calibrationOffset));
    }

    public void setCurrentIntensity(IntensityLevel intensity) {
        this.currentIntensity = intensity;
        logger.info("Intensity updated to: " + intensity);
    }

    private double getThresholdFactor() {
        switch (currentIntensity) {
            case GENTLE:
                return 1.2;
            case NORMAL:
                return 1.0;
            case INTENSE:
                return 0.7; // Kritischer Schwellenwert bei 3.5N
            default:
                return 1.0;
        }
    }

    public boolean isWarningThresholdExceeded() {
        return getCurrentValue() >= getWarningThreshold();
    }

    public boolean isCriticalThresholdExceeded() {
        return getCurrentValue() >= getCriticalThreshold();
    }

    public void calibrate() {
        calibrationOffset = -currentPressure;
        isCalibrated = true;
    }

    public String getUnit() {
        return "N";
    }

    public String getName() {
        return "Pressure Sensor";
    }

    public double getWarningThreshold() {
        return BASE_WARNING_THRESHOLD * getThresholdFactor();
    }

    public double getCriticalThreshold() {
        return BASE_CRITICAL_THRESHOLD * getThresholdFactor();
    }

    public void setPressure(double pressure) {
        simulatePressure(pressure);
    }

    public void simulatePressure(double pressure) {
        currentPressure = Math.max(MIN_PRESSURE, Math.min(MAX_PRESSURE, pressure));
        logger.info("Simulated pressure: " + pressure);
    }

    public PressureStatus getPressureStatus() {
        if (isCriticalThresholdExceeded()) {
            return PressureStatus.CRITICAL;
        } else if (isWarningThresholdExceeded()) {
            return PressureStatus.WARNING;
        } else {
            return PressureStatus.NORMAL;
        }
    }

    public String getStatus() {
        PressureStatus status = getPressureStatus();
        switch (status) {
            case CRITICAL:
                return "KRITISCH";
            case WARNING:
                return "WARNUNG";
            default:
                return "OK";
        }
    }

    public boolean isCalibrated() {
        return isCalibrated;
    }

    public IntensityLevel getCurrentIntensity() {
        return currentIntensity;
    }
}
