package de.thd.zahnputzmaschine.system;

/**
 * Enum für LED-Farben
 * @author Nikola Valchev
 */
public enum LEDColor {
    OFF("Aus"),
    GREEN("Grün"),
    BLUE("Blau"),
    RED("Rot"),
    YELLOW("Gelb"),
    YELLOW_BLINKING("Gelb blinkend"),
    RED_BLINKING("Rot blinkend");

    private final String displayName;

    LEDColor(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
