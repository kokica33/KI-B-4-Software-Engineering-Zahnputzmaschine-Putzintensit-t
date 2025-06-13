package de.thd.zahnputzmaschine.model.state;

import de.thd.zahnputzmaschine.controller.ToothbrushController;
import de.thd.zahnputzmaschine.model.IntensityLevel;
import de.thd.zahnputzmaschine.util.SimpleLogger;

/**
 * Idle-Zustand: Zahnbürste ist ausgeschaltet.
 *
 * @author Nikola Valchev
 * @version 1.0 - Iteration 1
 */
public class IdleState implements BrushState {
    private static final SimpleLogger logger = new SimpleLogger(IdleState.class);

    @Override
    public void handle(ToothbrushController controller) {
        logger.info("Button pressed in IDLE state - switching to GENTLE");
        controller.setState(new GentleState());
    }

    @Override
    public IntensityLevel getIntensityLevel() {
        return IntensityLevel.OFF;
    }

    @Override
    public void enter() {
        logger.info("=== Zahnbürste AUSGESCHALTET ===");
        System.out.println("Zahnbürste ausgeschaltet");
    }

    @Override
    public void exit() {
        logger.info("Leaving IDLE state");
    }
}