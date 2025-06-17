package de.thd.zahnputzmaschine.controller;

import de.thd.zahnputzmaschine.listener.PressureListener;
import de.thd.zahnputzmaschine.listener.StateChangeListener;
import de.thd.zahnputzmaschine.model.IntensityLevel;  // <-- WICHTIG für die neuen Zeilen!
import de.thd.zahnputzmaschine.model.sensor.PressureSensor;
import de.thd.zahnputzmaschine.model.state.*;
import de.thd.zahnputzmaschine.system.LEDColor;
import de.thd.zahnputzmaschine.system.WarningSystem;
import de.thd.zahnputzmaschine.util.SimpleLogger;

import java.util.ArrayList;
import java.util.List;
/**
 * Erweiterte Controller-Klasse für die Zahnputzmaschine
 * Iteration 2: mit Sensor-Integration und Observer Pattern
 * @author Nikola Valchev
 */
public class ToothbrushController {
    private static final SimpleLogger logger = new SimpleLogger(ToothbrushController.class);

    private BrushState currentState;
    private long stateChangeTimestamp;

    // Neue Komponenten für Iteration 2
    private final PressureSensor pressureSensor;
    private final WarningSystem warningSystem;
    private final List<StateChangeListener> stateListeners;
    private final List<PressureListener> pressureListeners;

    private boolean emergencyShutdownEnabled = true;
    private static final double EMERGENCY_SHUTDOWN_THRESHOLD = 7.0; // Newton
    private static final long EMERGENCY_SHUTDOWN_DELAY = 3000; // 3 Sekunden
    private long criticalPressureStartTime = 0;

    public ToothbrushController() {
        // Initialisierung wie bisher
        this.currentState = new IdleState();
        this.stateChangeTimestamp = System.currentTimeMillis();

        // Neue Komponenten initialisieren
        this.pressureSensor = new PressureSensor();
        this.warningSystem = new WarningSystem();
        this.stateListeners = new ArrayList<>();
        this.pressureListeners = new ArrayList<>();

        // WarningSystem als PressureListener registrieren
        addPressureListener(warningSystem);

        // Status LED setzen
        updateStatusLED();

        // Initial state enter
        currentState.enter();
        //Update Drucksensor mit aktueller Intensität
        IntensityLevel newIntensity = currentState.getIntensityLevel();
        pressureSensor.setCurrentIntensity(newIntensity);
        pressureSensor.setCurrentIntensity(currentState.getIntensityLevel());

        logger.info("Druckschwellen aktualisiert für " + newIntensity + ":");
        logger.info("  - Warnung bei: " + pressureSensor.getWarningThreshold() + " N");
        logger.info("  - Kritisch bei: " + pressureSensor.getCriticalThreshold() + " N");
        logger.info("ToothbrushController mit Sensor-System initialisiert");
    }

    /**
     * Behandelt Button-Press Events
     */
    public void buttonPress() {
        logger.info("Button pressed in state: " + currentState.getClass().getSimpleName());
        currentState.handle(this);
    }

    /**
     * Setzt einen neuen Zustand und benachrichtigt Listener
     * @param newState Der neue Zustand
     */
    /**
     * Setzt einen neuen Zustand und benachrichtigt Listener
     * @param newState Der neue Zustand
     */
    public void setState(BrushState newState) {
        BrushState oldState = currentState;

        // Exit old state
        currentState.exit();

        // Change state
        currentState = newState;
        stateChangeTimestamp = System.currentTimeMillis();

        // Enter new state
        currentState.enter();

        // WICHTIG: Update Drucksensor mit neuer Intensität
        IntensityLevel newIntensity = currentState.getIntensityLevel();
        pressureSensor.setCurrentIntensity(newIntensity);

        logger.info("Druckschwellen aktualisiert für " + newIntensity + ":");
        logger.info("  - Warnung bei: " + pressureSensor.getWarningThreshold() + " N");
        logger.info("  - Kritisch bei: " + pressureSensor.getCriticalThreshold() + " N");

        // Update LEDs
        updateStatusLED();

        // Notify listeners
        notifyStateChangeListeners(oldState, newState);

        // Activate/Deactivate warning system based on state
        if (currentState instanceof IdleState) {
            warningSystem.deactivate();
        } else {
            warningSystem.activate();
        }

        logger.info("State changed from " + oldState.getClass().getSimpleName() +
                " to " + newState.getClass().getSimpleName());
    }

    /**
     * Prüft alle Sensoren und reagiert entsprechend
     */
    public void checkSensors() {
        double pressure = pressureSensor.getCurrentValue();

        // Benachrichtige Pressure Listeners
        notifyPressureListeners(pressure);

        // Emergency Shutdown Check
        if (emergencyShutdownEnabled && pressure >= EMERGENCY_SHUTDOWN_THRESHOLD) {
            handleCriticalPressure(pressure);
        } else {
            criticalPressureStartTime = 0; // Reset timer
        }
    }

    /**
     * Behandelt kritischen Druck mit verzögerter Abschaltung - ASCII Version
     */
    private void handleCriticalPressure(double pressure) {
        if (!(currentState instanceof IdleState)) {
            if (criticalPressureStartTime == 0) {
                criticalPressureStartTime = System.currentTimeMillis();
                logger.warn("Kritischer Druck erkannt: " + pressure + " N. Starte Countdown...");
            }

            long elapsedTime = System.currentTimeMillis() - criticalPressureStartTime;
            long remainingTime = (EMERGENCY_SHUTDOWN_DELAY - elapsedTime) / 1000;

            if (elapsedTime >= EMERGENCY_SHUTDOWN_DELAY) {
                emergencyShutdown();
            } else {
                System.out.println("[TIME] Automatische Abschaltung in " + remainingTime + " Sekunden...");
            }
        }
    }

    /**
     * Führt eine Notabschaltung durch - ASCII Version
     */
    public void emergencyShutdown() {
        logger.error("NOTABSCHALTUNG! Kritischer Druck zu lange ueberschritten.");
        System.out.println("[!!!] NOTABSCHALTUNG AKTIVIERT!");
        setState(new IdleState());
        criticalPressureStartTime = 0;
    }

    /**
     * Aktualisiert die Status-LED basierend auf dem aktuellen Zustand
     */
    private void updateStatusLED() {
        LEDColor color = LEDColor.OFF;

        if (currentState instanceof IdleState) {
            color = LEDColor.OFF;
        } else if (currentState instanceof GentleState) {
            color = LEDColor.GREEN;
        } else if (currentState instanceof NormalState) {
            color = LEDColor.BLUE;
        } else if (currentState instanceof IntenseState) {
            color = LEDColor.RED;
        }

        warningSystem.setStatusLED(color);
    }

    /**
     * Benachrichtigt alle StateChangeListener
     */
    private void notifyStateChangeListeners(BrushState oldState, BrushState newState) {
        for (StateChangeListener listener : stateListeners) {
            listener.onStateChanged(oldState, newState);
        }
    }

    /**
     * Benachrichtigt alle PressureListener
     */
    private void notifyPressureListeners(double pressure) {
        boolean wasWarning = false;
        boolean wasCritical = false;

        for (PressureListener listener : pressureListeners) {
            listener.onPressureChanged(pressure);

            if (pressureSensor.isCriticalThresholdExceeded()) {
                listener.onCriticalThreshold(pressure);
                wasCritical = true;
            } else if (pressureSensor.isWarningThresholdExceeded()) {
                listener.onWarningThreshold(pressure);
                wasWarning = true;
            } else if (!wasWarning && !wasCritical) {
                listener.onPressureNormal();
            }
        }
    }

    // Listener Management

    public void addStateChangeListener(StateChangeListener listener) {
        stateListeners.add(listener);
    }

    public void removeStateChangeListener(StateChangeListener listener) {
        stateListeners.remove(listener);
    }

    public void addPressureListener(PressureListener listener) {
        pressureListeners.add(listener);
    }

    public void removePressureListener(PressureListener listener) {
        pressureListeners.remove(listener);
    }

    // Getter methods

    public IntensityLevel getCurrentIntensity() {
        return currentState.getIntensityLevel();
    }

    public BrushState getCurrentState() {
        return currentState;
    }

    public long getTimeInCurrentState() {
        return System.currentTimeMillis() - stateChangeTimestamp;
    }

    public PressureSensor getPressureSensor() {
        return pressureSensor;
    }

    public WarningSystem getWarningSystem() {
        return warningSystem;
    }

    public void setEmergencyShutdownEnabled(boolean enabled) {
        this.emergencyShutdownEnabled = enabled;
    }

    public boolean isEmergencyShutdownEnabled() {
        return emergencyShutdownEnabled;
    }
}
