package de.thd.zahnputzmaschine.model.sensor;

import de.thd.zahnputzmaschine.model.IntensityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für dynamische Druckschwellen
 * @author Nikola Valchev
 */
public class DynamicPressureSensorTest {

    private PressureSensor sensor;

    @BeforeEach
    public void setUp() {
        sensor = new PressureSensor();
    }

    @Test
    public void testGentleModeThresholds() {
        // GENTLE Mode - erlaubt mehr Druck
        sensor.setCurrentIntensity(IntensityLevel.GENTLE);

        assertEquals(3.6, sensor.getWarningThreshold(), 0.01);
        assertEquals(6.0, sensor.getCriticalThreshold(), 0.01);

        // Test: 3.5N sollte OK sein in GENTLE (aber wäre Warnung in NORMAL)
        sensor.simulatePressure(3.5);
        assertFalse(sensor.isWarningThresholdExceeded());

        // Test: 4.0N sollte Warnung sein
        sensor.simulatePressure(4.0);
        assertTrue(sensor.isWarningThresholdExceeded());
        assertFalse(sensor.isCriticalThresholdExceeded());

        // Test: 6.5N sollte kritisch sein
        sensor.simulatePressure(6.5);
        assertTrue(sensor.isCriticalThresholdExceeded());
    }

    @Test
    public void testNormalModeThresholds() {
        // NORMAL Mode - Standard-Schwellen
        sensor.setCurrentIntensity(IntensityLevel.NORMAL);

        assertEquals(3.0, sensor.getWarningThreshold(), 0.01);
        assertEquals(5.0, sensor.getCriticalThreshold(), 0.01);

        // Test: 2.5N sollte OK sein
        sensor.simulatePressure(2.5);
        assertFalse(sensor.isWarningThresholdExceeded());

        // Test: 3.5N sollte Warnung sein
        sensor.simulatePressure(3.5);
        assertTrue(sensor.isWarningThresholdExceeded());
        assertFalse(sensor.isCriticalThresholdExceeded());

        // Test: 5.5N sollte kritisch sein
        sensor.simulatePressure(5.5);
        assertTrue(sensor.isCriticalThresholdExceeded());
    }

    @Test
    public void testIntenseModeThresholds() {
        // INTENSE Mode - reduzierte Schwellen (weniger Druck erlaubt!)
        sensor.setCurrentIntensity(IntensityLevel.INTENSE);

        assertEquals(1.8, sensor.getWarningThreshold(), 0.01);
        assertEquals(3.0, sensor.getCriticalThreshold(), 0.01);

        // Test: 1.5N sollte noch OK sein
        sensor.simulatePressure(1.5);
        assertFalse(sensor.isWarningThresholdExceeded());

        // Test: 2.0N sollte schon Warnung sein!
        sensor.simulatePressure(2.0);
        assertTrue(sensor.isWarningThresholdExceeded());
        assertFalse(sensor.isCriticalThresholdExceeded());

        // Test: 3.5N sollte kritisch sein (wäre in NORMAL nur Warnung)
        sensor.simulatePressure(3.5);
        assertTrue(sensor.isCriticalThresholdExceeded());
    }

    @Test
    public void testThresholdTransitions() {
        // Test dass sich Schwellen beim Moduswechsel ändern
        sensor.simulatePressure(2.5); // Setze konstanten Druck

        // In GENTLE: sollte OK sein
        sensor.setCurrentIntensity(IntensityLevel.GENTLE);
        assertFalse(sensor.isWarningThresholdExceeded());

        // In NORMAL: sollte OK sein
        sensor.setCurrentIntensity(IntensityLevel.NORMAL);
        assertFalse(sensor.isWarningThresholdExceeded());

        // In INTENSE: sollte Warnung sein!
        sensor.setCurrentIntensity(IntensityLevel.INTENSE);
        assertTrue(sensor.isWarningThresholdExceeded());
    }

    @Test
    public void testOffModeThresholds() {
        // OFF Mode sollte Standard-Schwellen verwenden
        sensor.setCurrentIntensity(IntensityLevel.OFF);

        assertEquals(3.0, sensor.getWarningThreshold(), 0.01);
        assertEquals(5.0, sensor.getCriticalThreshold(), 0.01);
    }

    @Test
    public void testBoundaryValuesWithDynamicThresholds() {
        // Test Grenzwerte bei verschiedenen Modi
        sensor.setCurrentIntensity(IntensityLevel.GENTLE);

        // Genau an der Warnschwelle
        sensor.simulatePressure(3.6);
        assertTrue(sensor.isWarningThresholdExceeded());

        // Knapp unter der Warnschwelle
        sensor.simulatePressure(3.59);
        assertFalse(sensor.isWarningThresholdExceeded());

        // Wechsel zu INTENSE
        sensor.setCurrentIntensity(IntensityLevel.INTENSE);

        // Was in GENTLE OK war, ist jetzt kritisch!
        sensor.simulatePressure(3.6);
        assertTrue(sensor.isCriticalThresholdExceeded());
    }

    @Test
    public void testStatusStringWithModes() {
        sensor.setCurrentIntensity(IntensityLevel.GENTLE);
        sensor.simulatePressure(2.0);
        assertTrue(sensor.getStatus().contains("OK"));

        sensor.setCurrentIntensity(IntensityLevel.INTENSE);
        // Gleicher Druck, anderer Status!
        assertTrue(sensor.getStatus().contains("WARNUNG"));
    }
}

