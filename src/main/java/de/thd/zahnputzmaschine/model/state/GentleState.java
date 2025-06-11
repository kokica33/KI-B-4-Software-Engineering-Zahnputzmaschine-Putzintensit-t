package src.main.java.de.thd.zahnputzmaschine.model.state;

import src.main.java.de.thd.zahnputzmaschine.controller.ToothbrushController;
import src.main.java.de.thd.zahnputzmaschine.model.IntensityLevel;
import src.main.java.de.thd.zahnputzmaschine.util.SimpleLogger;

/**
 * Gentle-Zustand: Sanfte Putzintensität (15.000 Schwingungen/min)
 * Requirement: RF-001
 *
 * @author [Nikola Valchev]
 * @version 1.0 - Iteration 1
 */
public class GentleState implements BrushState {
    private static final SimpleLogger logger = new SimpleLogger(IdleState.class);

    @Override
    public void handle(ToothbrushController controller) {
        logger.info("Button pressed in GENTLE state - switching to NORMAL");
        controller.setState(new NormalState());
    }

    @Override
    public IntensityLevel getIntensityLevel() {
        return IntensityLevel.GENTLE;
    }

    @Override
    public void enter() {
        logger.info("=== Entering GENTLE mode ===");
        System.out.println("Zahnbürste läuft im SANFTEN Modus (15.000 Schwingungen/min)");
        System.out.println("LED: Grün");
    }

    @Override
    public void exit() {
        logger.info("Leaving GENTLE state");
    }
}
