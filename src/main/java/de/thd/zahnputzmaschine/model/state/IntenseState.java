package src.main.java.de.thd.zahnputzmaschine.model.state;

import src.main.java.de.thd.zahnputzmaschine.controller.ToothbrushController;
import src.main.java.de.thd.zahnputzmaschine.model.IntensityLevel;
import src.main.java.de.thd.zahnputzmaschine.util.SimpleLogger;

/**
 * Intense-Zustand: Starke Putzintensität (35.000 Schwingungen/min)
 * Requirement: RF-001
 *
 * @author [Dein Name]
 * @version 1.0 - Iteration 1
 */
public class IntenseState implements BrushState {
    private static final SimpleLogger logger = new SimpleLogger(IdleState.class);

    @Override
    public void handle(ToothbrushController controller) {
        logger.info("Button pressed in INTENSE state - switching to OFF");
        controller.setState(new IdleState());
    }

    @Override
    public IntensityLevel getIntensityLevel() {
        return IntensityLevel.INTENSE;
    }

    @Override
    public void enter() {
        logger.info("=== Entering INTENSE mode ===");
        System.out.println("Zahnbürste läuft im INTENSIVEN Modus (35.000 Schwingungen/min)");
        System.out.println("LED: Rot");
    }

    @Override
    public void exit() {
        logger.info("Leaving INTENSE state");
    }
}
