package de.thd.zahnputzmaschine.hardware;

/**
 * Interface für Hardware-Mock-Strategie
 * Iteration 3: Hardware-Software Integration
 *
 * @author Nikola Valchev
 * @version 3.0
 */
public interface HardwareMockStrategy {

    /**
     * Liest den aktuellen Druckwert vom Sensor
     * @return Druckwert in Newton
     */
    double readPressure();

    /**
     * Setzt die Motorgeschwindigkeit
     * @param speed Geschwindigkeit in RPM
     */
    void setMotorSpeed(int speed);

    /**
     * Prüft ob der Motor läuft
     * @return true wenn Motor läuft
     */
    boolean isMotorRunning();

    /**
     * Setzt das Vibrationsmuster
     * @param pattern Array mit Vibrationsmuster [on_ms, off_ms, ...]
     */
    void setVibrationPattern(int[] pattern);

    /**
     * Gibt den aktuellen Hardware-Status zurück
     * @return Status-String für Debugging
     */
    String getHardwareStatus();
}