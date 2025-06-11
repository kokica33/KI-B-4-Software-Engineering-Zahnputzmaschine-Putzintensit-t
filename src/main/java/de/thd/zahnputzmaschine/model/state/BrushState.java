package src.main.java.de.thd.zahnputzmaschine.model.state;

import src.main.java.de.thd.zahnputzmaschine.controller.ToothbrushController;
import src.main.java.de.thd.zahnputzmaschine.model.IntensityLevel;

/**
 * State Pattern: Interface für alle Zustände der Zahnbürste.
 *
 * @author [Nikola Valchev]
 * @version 1.0 - Iteration 1
 */
public interface BrushState {
    /**
     * Behandelt Benutzerinteraktionen im aktuellen Zustand.
     */
    void handle(ToothbrushController controller);

    /**
     * Gibt die Intensitätsstufe des Zustands zurück.
     */
    IntensityLevel getIntensityLevel();

    /**
     * Wird beim Eintritt in diesen Zustand aufgerufen.
     */
    void enter();

    /**
     * Wird beim Verlassen dieses Zustands aufgerufen.
     */
    void exit();
}