package de.thd.zahnputzmaschine.integration;

import de.thd.zahnputzmaschine.controller.ToothbrushController;
import de.thd.zahnputzmaschine.hardware.EnhancedMotorController;
import de.thd.zahnputzmaschine.model.IntensityLevel;
import de.thd.zahnputzmaschine.model.sensor.EnhancedPressureSensor;
import de.thd.zahnputzmaschine.model.sensor.PressureSensor;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests für Hardware-Software Schnittstelle
 * Iteration 3: Testet das Zusammenspiel aller Komponenten
 *
 * @author Nikola Valchev
 * @version 3.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HardwareIntegrationTest {

    private ToothbrushController controller;
    private EnhancedPressureSensor enhancedSensor;
    private EnhancedMotorController motorController;

    @BeforeEach
    public void setUp() {
        controller = new ToothbrushController();
        enhancedSensor = new EnhancedPressureSensor();
        motorController = new EnhancedMotorController();

        // Hardware-Mock verbinden
        enhancedSensor.setHardwareMock(motorController);
    }

    @AfterEach
    public void tearDown() {
        enhancedSensor.stopSimulation();
        motorController.stopMotor();
    }

    /**
     * TC-3.1: Test automatische Intensitätsanpassung bei Druckänderung
     */
    @Test
    @Order(1)
    @DisplayName("Automatische Intensitätsanpassung bei verschiedenen Drücken")
    public void testAutomaticIntensityAdjustment() throws InterruptedException {
        // Given: System startet im GENTLE Modus
        controller.buttonPress(); // OFF -> GENTLE
        assertEquals(IntensityLevel.GENTLE, controller.getCurrentIntensity());

        // When: Normaler Druck simuliert
        enhancedSensor.startSimulation(EnhancedPressureSensor.UserSimulationMode.NORMAL);
        Thread.sleep(200); // Warte auf Stabilisierung

        double normalPressure = enhancedSensor.getCurrentValue();
        assertTrue(normalPressure > 1.0 && normalPressure < 3.0,
                "Normaler Druck sollte zwischen 1-3 Newton liegen");

        // When: Aggressiver Druck simuliert
        enhancedSensor.startSimulation(EnhancedPressureSensor.UserSimulationMode.AGGRESSIVE);
        Thread.sleep(500);

        double aggressivePressure = enhancedSensor.getAveragePressure();
        assertTrue(aggressivePressure > 3.0,
                "Aggressiver Druck sollte über 3 Newton liegen");

        // Then: System sollte gewarnt haben
        PressureSensor.PressureStatus status = enhancedSensor.getPressureStatus();
        assertNotEquals(PressureSensor.PressureStatus.NORMAL, status,
                "Bei hohem Druck sollte Warnung aktiv sein");
    }

    /**
     * TC-3.2: Test Modi-spezifische Motorsteuerung
     */
    @Test
    @Order(2)
    @DisplayName("Motor passt sich an verschiedene Intensitätsstufen an")
    public void testMotorIntensityModes() throws InterruptedException {
        // Test GENTLE Mode
        controller.buttonPress(); // OFF -> GENTLE
        motorController.startMotor(IntensityLevel.GENTLE);
        Thread.sleep(300); // Warte auf Motor-Hochlauf

        int gentleSpeed = motorController.getCurrentSpeed();
        assertTrue(gentleSpeed > 3500 && gentleSpeed < 4500,
                "GENTLE Modus sollte ca. 4000 RPM haben");

        // Test NORMAL Mode
        controller.buttonPress(); // GENTLE -> NORMAL
        motorController.startMotor(IntensityLevel.NORMAL);
        Thread.sleep(300);

        int normalSpeed = motorController.getCurrentSpeed();
        assertTrue(normalSpeed > 6000 && normalSpeed < 7000,
                "NORMAL Modus sollte ca. 6500 RPM haben");

        // Test INTENSE Mode
        controller.buttonPress(); // NORMAL -> INTENSE
        motorController.startMotor(IntensityLevel.INTENSE);
        Thread.sleep(300);

        int intenseSpeed = motorController.getCurrentSpeed();
        assertTrue(intenseSpeed > 8500 && intenseSpeed < 9500,
                "INTENSE Modus sollte ca. 9000 RPM haben");

        // Verify smooth transitions
        assertTrue(intenseSpeed > normalSpeed && normalSpeed > gentleSpeed,
                "Geschwindigkeiten sollten mit Intensität steigen");
    }

    /**
     * TC-3.3: Test Echtzeit-Feedback und Visualisierung
     */
    @Test
    @Order(3)
    @DisplayName("Echtzeit-Datenerfassung für Visualisierung")
    public void testRealtimeDataCollection() throws InterruptedException {
        // Start system
        controller.buttonPress(); // Start
        enhancedSensor.startSimulation(EnhancedPressureSensor.UserSimulationMode.VARIABLE);
        motorController.startMotor(IntensityLevel.NORMAL);

        // Collect data over time
        int samples = 20;
        double minPressure = Double.MAX_VALUE;
        double maxPressure = Double.MIN_VALUE;

        for (int i = 0; i < samples; i++) {
            Thread.sleep(50); // 20Hz sampling

            double pressure = enhancedSensor.getCurrentValue();
            minPressure = Math.min(minPressure, pressure);
            maxPressure = Math.max(maxPressure, pressure);

            // Verify data is in valid range
            assertTrue(pressure >= 0 && pressure <= 10,
                    "Druckwerte sollten zwischen 0-10 Newton liegen");
        }

        // Variable mode should show variation
        double variation = maxPressure - minPressure;
        assertTrue(variation > 0.5,
                "Variable Modus sollte deutliche Druckschwankungen zeigen");

        // Check average calculation
        double average = enhancedSensor.getAveragePressure();
        assertTrue(average > minPressure && average < maxPressure,
                "Durchschnitt sollte zwischen Min und Max liegen");
    }

    /**
     * TC-3.4: Test Vibrations-Feedback bei Warnung
     */
    @Test
    @Order(4)
    @DisplayName("Vibrations-Warnung bei kritischem Druck")
    public void testVibrationWarningFeedback() throws InterruptedException {
        // Start in INTENSE mode (niedrigere Druckschwelle)
        controller.buttonPress(); // GENTLE
        controller.buttonPress(); // NORMAL
        controller.buttonPress(); // INTENSE

        motorController.startMotor(IntensityLevel.INTENSE);
        enhancedSensor.setCurrentIntensity(IntensityLevel.INTENSE);

        // Simulate high pressure
        enhancedSensor.setPressure(4.0); // Über kritischer Schwelle für INTENSE
        Thread.sleep(100);

        // Trigger warning vibration
        motorController.triggerWarningVibration();

        // Verify motor is still running
        assertTrue(motorController.isMotorRunning(),
                "Motor sollte trotz Warnung weiterlaufen");

        // Verify warning was triggered
        assertEquals(PressureSensor.PressureStatus.CRITICAL,
                enhancedSensor.getPressureStatus(),
                "Kritischer Status sollte erkannt werden");
    }

    /**
     * TC-3.5: Test Motor-Trägheit und sanfte Übergänge
     */
    @Test
    @Order(5)
    @DisplayName("Motor-Trägheit bei Geschwindigkeitsänderungen")
    public void testMotorInertiaSimulation() throws InterruptedException {
        motorController.startMotor(IntensityLevel.NORMAL);

        // Sammle Geschwindigkeitswerte während Hochlauf
        int[] speeds = new int[10];
        for (int i = 0; i < 10; i++) {
            Thread.sleep(50);
            speeds[i] = motorController.getCurrentSpeed();
        }

        // Verify gradual acceleration
        boolean gradualIncrease = true;
        for (int i = 1; i < speeds.length; i++) {
            if (speeds[i] < speeds[i-1]) {
                gradualIncrease = false;
                break;
            }
        }

        assertTrue(gradualIncrease,
                "Motor sollte graduell beschleunigen");

        // Test deceleration
        motorController.stopMotor();
        Thread.sleep(200);

        int deceleratingSpeed = motorController.getCurrentSpeed();
        assertTrue(deceleratingSpeed < speeds[speeds.length-1],
                "Motor sollte beim Stoppen abbremsen");
    }

    /**
     * TC-3.6: Test Sensor-Stabilität
     */
    @Test
    @Order(6)
    @DisplayName("Sensor-Stabilität und Rauschunterdrückung")
    public void testSensorStability() throws InterruptedException {
        enhancedSensor.startSimulation(EnhancedPressureSensor.UserSimulationMode.GENTLE);
        motorController.startMotor(IntensityLevel.GENTLE);

        // Warte auf Stabilisierung
        Thread.sleep(1000);

        // Check stability
        assertTrue(enhancedSensor.isPressureStable(),
                "Sanfter Modus sollte stabilen Druck zeigen");

        double variance = enhancedSensor.getPressureVariance();
        assertTrue(variance < 0.5,
                "Druckvarianz sollte niedrig sein im sanften Modus");
    }

    /**
     * TC-3.7: Integration Test - Kompletter Putzzyklus
     */
    @Test
    @Order(7)
    @DisplayName("Vollständiger Putzzyklus mit allen Features")
    public void testCompleteBrushingCycle() throws InterruptedException {
        // Phase 1: Start
        assertEquals(IntensityLevel.OFF, controller.getCurrentIntensity());

        controller.buttonPress(); // Start GENTLE
        motorController.startMotor(IntensityLevel.GENTLE);
        enhancedSensor.startSimulation(EnhancedPressureSensor.UserSimulationMode.REALISTIC);
        Thread.sleep(500);

        assertTrue(motorController.isMotorRunning(), "Motor sollte laufen");

        // Phase 2: Moduswechsel
        controller.buttonPress(); // NORMAL
        motorController.startMotor(IntensityLevel.NORMAL);
        Thread.sleep(500);

        // Phase 3: Intervall-Vibration (simuliere 30 Sekunden)
        motorController.triggerIntervalVibration();

        // Phase 4: Hoher Druck
        enhancedSensor.setPressure(4.5);
        motorController.triggerWarningVibration();
        Thread.sleep(200);

        // Phase 5: Stop
        controller.buttonPress(); // INTENSE
        controller.buttonPress(); // OFF
        motorController.stopMotor();
        enhancedSensor.stopSimulation();

        // Verify final state
        assertEquals(IntensityLevel.OFF, controller.getCurrentIntensity());
        assertFalse(motorController.isMotorRunning());
        assertEquals(0, motorController.getCurrentSpeed());

        // Check statistics
        assertTrue(motorController.getTotalRuntime() > 0,
                "Laufzeit sollte erfasst worden sein");
        assertTrue(motorController.getVibrationCycles() > 0,
                "Vibrations-Zyklen sollten gezählt worden sein");
    }
}
