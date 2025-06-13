package de.thd.zahnputzmaschine.model.state;

import de.thd.zahnputzmaschine.controller.ToothbrushController;
import de.thd.zahnputzmaschine.model.IntensityLevel;
import de.thd.zahnputzmaschine.util.SimpleLogger;

/**
 * Normal-Zustand: Normale Putzintensität (25.000 Schwingungen/min)
 * Requirement: RF-001
 *
 * @author Nikola Valchev
 * @version 1.0 - Iteration 1
 */
public class NormalState implements BrushState {
    private static final SimpleLogger logger = new SimpleLogger(IdleState.class);

    @Override
    public void handle(ToothbrushController controller) {
        logger.info("Button pressed in NORMAL state - switching to INTENSE");
        controller.setState(new IntenseState());
    }

    @Override
    public IntensityLevel getIntensityLevel() {
        return IntensityLevel.NORMAL;
    }

    @Override
    public void enter() {
        logger.info("=== Entering NORMAL mode ===");
        System.out.println("Zahnbürste läuft im NORMALEN Modus (25.000 Schwingungen/min)");
        System.out.println("LED: Blau");
    }

    @Override
    public void exit() {
        logger.info("Leaving NORMAL state");
    }
}
