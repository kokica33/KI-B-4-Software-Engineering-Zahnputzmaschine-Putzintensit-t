package de.thd.zahnputzmaschine.model.sensor;

/**
 * Interface für alle Sensoren im System
 * @author Nikola Valchev
 */
public interface Sensor {
    /**
     * Gibt den aktuellen Sensorwert zurück
     * @return Aktueller Messwert
     */
    double getCurrentValue();

    /**
     * Prüft ob Warnschwelle überschritten
     * @return true wenn Warnschwelle überschritten
     */
    boolean isWarningThresholdExceeded();

    /**
     * Prüft, ob kritische Schwelle überschritten
     * @return true, wenn kritische Schwelle überschritten
     */
    boolean isCriticalThresholdExceeded();

    /**
     * Kalibriert den Sensor
     */
    void calibrate();

    /**
     * Gibt die Einheit des Sensors zurück
     * @return Einheit als String (z.B. "N" für Newton)
     */
    String getUnit();

    /**
     * Gibt den Sensornamen zurück
     * @return Name des Sensors
     */
    String getName();
}