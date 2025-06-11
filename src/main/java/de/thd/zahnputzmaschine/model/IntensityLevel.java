package src.main.java.de.thd.zahnputzmaschine.model;

/**
 * Definiert die verfügbaren Intensitätsstufen der Zahnbürste.
 *
 * @author [Nikola Valchev]
 * @version 1.0 - Iteration 1
 */
public enum IntensityLevel {
    OFF(0, "Aus"),
    GENTLE(15000, "Sanft"),
    NORMAL(25000, "Normal"),
    INTENSE(35000, "Stark");

    private final int frequency;
    private final String displayName;

    IntensityLevel(int frequency, String displayName) {
        this.frequency = frequency;
        this.displayName = displayName;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getDisplayName() {
        return displayName;
    }
}
