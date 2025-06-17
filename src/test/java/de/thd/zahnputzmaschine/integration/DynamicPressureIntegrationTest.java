package de.thd.zahnputzmaschine.integration;

import de.thd.zahnputzmaschine.controller.ToothbrushController;
import de.thd.zahnputzmaschine.model.IntensityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integrationstests für dynamische Druckschwellen
 * @author Nikola Valchev
 */
public class DynamicPressureIntegrationTest {

    private ToothbrushController controller;

    @BeforeEach
    public void setUp() {
        controller = new ToothbrushController();
    }

    @Test
    public void testPressureThresholdsChangeWithIntensity() {
        // Start - OFF
        assertEquals(3.0, controller.getPressureSensor().getWarningThreshold(), 0.01);

        // Wechsel zu GENTLE
        controller.buttonPress();
        assertEquals(IntensityLevel.GENTLE, controller.getCurrentIntensity());
        assertEquals(3.6, controller.getPressureSensor().getWarningThreshold(), 0.01);

        // Wechsel zu NORMAL
        controller.buttonPress();
        assertEquals(IntensityLevel.NORMAL, controller.getCurrentIntensity());
        assertEquals(3.0, controller.getPressureSensor().getWarningThreshold(), 0.01);

        // Wechsel zu INTENSE
        controller.buttonPress();
        assertEquals(IntensityLevel.INTENSE, controller.getCurrentIntensity());
        assertEquals(1.8, controller.getPressureSensor().getWarningThreshold(), 0.01);
    }

    @Test
    public void testSamePressureDifferentReactions() {
        // Simuliere konstanten Druck von 2.5N
        double testPressure = 2.5;

        // In GENTLE: sollte keine Warnung geben
        controller.buttonPress(); // GENTLE
        controller.getPressureSensor().simulatePressure(testPressure);
        assertFalse(controller.getPressureSensor().isWarningThresholdExceeded());

        // In INTENSE: sollte Warnung geben
        controller.buttonPress(); // NORMAL
        controller.buttonPress(); // INTENSE
        controller.getPressureSensor().simulatePressure(testPressure);
        assertTrue(controller.getPressureSensor().isWarningThresholdExceeded());
    }

    @Test
    public void testCriticalPressureInDifferentModes() {
        // Test kritische Schwelle

        // In GENTLE: 5N ist noch nicht kritisch
        controller.buttonPress(); // GENTLE
        controller.getPressureSensor().simulatePressure(5.0);
        assertFalse(controller.getPressureSensor().isCriticalThresholdExceeded());

        // In INTENSE: 5N ist definitiv kritisch
        controller.buttonPress(); // NORMAL
        controller.buttonPress(); // INTENSE
        controller.getPressureSensor().simulatePressure(5.0);
        assertTrue(controller.getPressureSensor().isCriticalThresholdExceeded());
    }

    @Test
    public void testAutomaticThresholdUpdateOnStateChange() {
        // Prüfe, dass Schwellen automatisch beim Zustandswechsel aktualisiert werden
        double initialWarning = controller.getPressureSensor().getWarningThreshold();

        controller.buttonPress(); // OFF -> GENTLE
        double gentleWarning = controller.getPressureSensor().getWarningThreshold();

        // Schwelle sollte sich geändert haben
        assertNotEquals(initialWarning, gentleWarning);
        assertEquals(3.6, gentleWarning, 0.01);
    }

    @Test
    public void testPressureStatusDuringCompleteCycle() {
        // Test Druckstatus während kompletten Zyklus
        double testPressure = 3.2;

        // OFF - keine Warnung (Standard-Schwellen)
        controller.getPressureSensor().simulatePressure(testPressure);
        assertTrue(controller.getPressureSensor().isWarningThresholdExceeded());

        // GENTLE - keine Warnung (höhere Schwellen)
        controller.buttonPress();
        controller.getPressureSensor().simulatePressure(testPressure);
        assertFalse(controller.getPressureSensor().isWarningThresholdExceeded());

        // NORMAL - Warnung (Standard-Schwellen)
        controller.buttonPress();
        controller.getPressureSensor().simulatePressure(testPressure);
        assertTrue(controller.getPressureSensor().isWarningThresholdExceeded());

        // INTENSE - sogar kritisch! (niedrige Schwellen)
        controller.buttonPress();
        controller.getPressureSensor().simulatePressure(testPressure);
        assertTrue(controller.getPressureSensor().isCriticalThresholdExceeded());

        // Zurück zu OFF
        controller.buttonPress();
        assertEquals(IntensityLevel.OFF, controller.getCurrentIntensity());
    }
}