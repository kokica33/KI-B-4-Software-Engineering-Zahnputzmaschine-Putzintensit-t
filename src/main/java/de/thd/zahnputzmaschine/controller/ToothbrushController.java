package src.main.java.de.thd.zahnputzmaschine.controller;

import src.main.java.de.thd.zahnputzmaschine.model.IntensityLevel;
import src.main.java.de.thd.zahnputzmaschine.model.state.BrushState;
import src.main.java.de.thd.zahnputzmaschine.model.state.IdleState;
import src.main.java.de.thd.zahnputzmaschine.util.SimpleLogger;

/**
 * Controller für die Zahnbürste - verwaltet den aktuellen Zustand
 * und koordiniert Zustandsübergänge.
 * Requirement: RF-002 (Ein-Button-Bedienung)
 *
 * @author [Dein Name]
 * @version 1.0 - Iteration 1
 */
public class ToothbrushController {
    private static final SimpleLogger logger = new SimpleLogger(ToothbrushController.class);
    private BrushState currentState;
    private long stateChangeTimestamp;

    /**
     * Konstruktor - initialisiert die Zahnbürste im ausgeschalteten Zustand
     */
    public ToothbrushController() {
        logger.info("Initializing ToothbrushController");
        this.currentState = new IdleState();
        this.currentState.enter();
        this.stateChangeTimestamp = System.currentTimeMillis();
    }

    /**
     * Simuliert einen Knopfdruck - delegiert an den aktuellen Zustand
     */
    public void buttonPress() {
        logger.info("Button pressed - current state: " + currentState.getClass().getSimpleName());
        currentState.handle(this);
    }

    /**
     * Wechselt den Zustand der Zahnbürste
     * @param newState Der neue Zustand
     */
    public void setState(BrushState newState) {
        logger.info("State transition: " + currentState.getClass().getSimpleName() +
                " -> " + newState.getClass().getSimpleName());

        currentState.exit();
        currentState = newState;
        currentState.enter();
        stateChangeTimestamp = System.currentTimeMillis();
    }

    /**
     * Gibt die aktuelle Intensitätsstufe zurück
     * @return Die aktuelle Intensitätsstufe
     */
    public IntensityLevel getCurrentIntensity() {
        return currentState.getIntensityLevel();
    }

    /**
     * Gibt den aktuellen Zustand zurück (für Tests)
     * @return Der aktuelle Zustand
     */
    public BrushState getCurrentState() {
        return currentState;
    }

    /**
     * Gibt die Zeit seit dem letzten Zustandswechsel zurück
     * @return Zeit in Millisekunden
     */
    public long getTimeInCurrentState() {
        return System.currentTimeMillis() - stateChangeTimestamp;
    }
}
