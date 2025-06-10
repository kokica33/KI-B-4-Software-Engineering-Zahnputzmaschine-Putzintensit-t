package de.thd.zahnputzmaschine.model.state;

import de.thd.zahnputzmaschine.controller.ToothbrushController;
import de.thd.zahnputzmaschine.model.IntensityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Idle-Zustand: Zahnbürste ist ausgeschaltet.
 *
 * @author [Dein Name]
 * @version 1.0 - Iteration 1
 */
public class IdleState implements BrushState {
    private static final Logger logger = LoggerFactory.getLogger(IdleState.class);

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