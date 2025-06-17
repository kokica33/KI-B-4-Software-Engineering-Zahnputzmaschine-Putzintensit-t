package de.thd.zahnputzmaschine.listener;

import de.thd.zahnputzmaschine.model.state.BrushState;

/**
 * Listener Interface für Zustandsänderungen
 * @author Nikola Valchev
 */
public interface StateChangeListener {
    /**
     * Wird aufgerufen wenn sich der Zustand ändert
     * @param oldState Alter Zustand
     * @param newState Neuer Zustand
     */
    void onStateChanged(BrushState oldState, BrushState newState);
}
