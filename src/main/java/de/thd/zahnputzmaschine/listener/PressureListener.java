package de.thd.zahnputzmaschine.listener;

/**
 * Listener Interface für Druckänderungen
 * @author Nikola Valchev
 */
public interface PressureListener {
    /**
     * Wird bei jeder Druckänderung aufgerufen
     * @param pressure Aktueller Druck in Newton
     */
    void onPressureChanged(double pressure);

    /**
     * Wird aufgerufen, wenn Warnschwelle überschritten wird
     * @param pressure Aktueller Druck in Newton
     */
    void onWarningThreshold(double pressure);

    /**
     * Wird aufgerufen, wenn kritische Schwelle überschritten wird
     * @param pressure Aktueller Druck in Newton
     */
    void onCriticalThreshold(double pressure);

    /**
     * Wird aufgerufen, wenn Druck wieder normal ist
     */
    void onPressureNormal();
}
