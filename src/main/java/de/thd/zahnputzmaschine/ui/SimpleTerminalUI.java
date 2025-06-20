package de.thd.zahnputzmaschine.ui;

import de.thd.zahnputzmaschine.controller.ToothbrushController;
import de.thd.zahnputzmaschine.model.IntensityLevel;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Optimiertes ASCII-UI mit korrekten Rahmenberechnungen
 *
 * @author Nikola Valchev
 * @version 3.4
 */
public class SimpleTerminalUI {

    private static final int WIDTH = 50;

    // Rahmenkonstanten
    private static final String HORIZONTAL_LINE = "-";
    private static final String VERTICAL_LINE = "|";
    private static final String CORNER = "+";
    private static final String DOUBLE_HORIZONTAL = "=";
    private static final String DOUBLE_VERTICAL = "|";
    private static final String DOUBLE_CORNER = "+";

    /**
     * Zeigt die Haupt-Ansicht (minimal)
     */
    public static void showMainView(ToothbrushController controller) {
        clearScreen();

        IntensityLevel level = controller.getCurrentIntensity();
        double pressure = controller.getPressureSensor().getCurrentValue();

        // Header
        printHeader("ZAHNPUTZMASCHINE v3.4");
        showBrushVisualization(level);
        printLine();
        printStatusLine(level, pressure, controller);
        printLine();
        printCommands();
        printFooter();
    }

    /**
     * Zeigt erweiterte Hilfe mit allen Details
     */
    public static void showHelpView(ToothbrushController controller) {
        clearScreen();

        IntensityLevel level = controller.getCurrentIntensity();
        double pressure = controller.getPressureSensor().getCurrentValue();
        long runtime = controller.getTimeInCurrentState() / 1000;

        // Erweiterter Header
        printDoubleHeader("ZAHNPUTZMASCHINE - ERWEITERTE ANSICHT");

        // Zeit und Status
        System.out.printf("%s Zeit: %-10s Modus: %-12s Laufzeit: %02d:%02d %s\n",
                DOUBLE_VERTICAL,
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                level.getDisplayName(),
                runtime / 60, runtime % 60,
                DOUBLE_VERTICAL);

        printDoubleLine();

        // Intensitäts-Balken
        System.out.printf("%s Intensitaet: ", DOUBLE_VERTICAL);
        printProgressBar(level == IntensityLevel.OFF ? 0 : level.ordinal() * 33, 20);
        System.out.printf(" %5d/min %s\n", level.getFrequency(), DOUBLE_VERTICAL);

        // Druck-Balken
        System.out.printf("%s Druck:    ", DOUBLE_VERTICAL);
        printPressureBar(pressure, 20);
        System.out.printf(" %.2f N %-10s %s\n", pressure, "", DOUBLE_VERTICAL);

        printDoubleLine();

        // Motor-Visualisierung
        System.out.printf("%s MOTOR STATUS:%s\n", DOUBLE_VERTICAL, " ".repeat(35) + DOUBLE_VERTICAL);
        showMotorGauge(level);

        printDoubleLine();

        // Druck-Verlauf
        System.out.printf("%s DRUCK-VERLAUF:%s\n", DOUBLE_VERTICAL, " ".repeat(34) + DOUBLE_VERTICAL);
        showPressureHistory(pressure);

        printDoubleLine();

        // Alle Befehle
        System.out.printf("%s ALLE BEFEHLE:%s\n", DOUBLE_VERTICAL, " ".repeat(35) + DOUBLE_VERTICAL);
        printCommand("B", "Button druecken (Modus wechseln)");
        printCommand("P <n>", "Druck setzen (0-10 Newton)");
        printCommand("P+", "Druck erhoehen (+0.5 N)");
        printCommand("P-", "Druck verringern (-0.5 N)");
        printCommand("P0", "Druck zuruecksetzen");
        printCommand("H", "Erweiterte Ansicht");
        printCommand("Q", "Programm beenden");

        printDoubleLine();

        // Warnungen
        if (controller.getPressureSensor().isCriticalThresholdExceeded()) {
            printWarning("!!! WARNUNG: KRITISCHER DRUCK !!!");
        } else if (controller.getPressureSensor().isWarningThresholdExceeded()) {
            printWarning("! ACHTUNG: Hoher Druck erkannt");
        }

        printDoubleFooter();
        System.out.println("\nDruecke ENTER um zur Hauptansicht zurueckzukehren...");
    }

    // Hilfsmethoden für UI-Elemente
    private static void printHeader(String title) {
        System.out.println(CORNER + HORIZONTAL_LINE.repeat(WIDTH) + CORNER);
        printCentered(title);
        System.out.println(CORNER + HORIZONTAL_LINE.repeat(WIDTH) + CORNER);
    }

    private static void printDoubleHeader(String title) {
        System.out.println(DOUBLE_CORNER + DOUBLE_HORIZONTAL.repeat(WIDTH) + DOUBLE_CORNER);
        printDoubleCentered(title);
        System.out.println(DOUBLE_CORNER + DOUBLE_HORIZONTAL.repeat(WIDTH) + DOUBLE_CORNER);
    }

    private static void printFooter() {
        System.out.println(CORNER + HORIZONTAL_LINE.repeat(WIDTH) + CORNER);
    }

    private static void printDoubleFooter() {
        System.out.println(DOUBLE_CORNER + DOUBLE_HORIZONTAL.repeat(WIDTH) + DOUBLE_CORNER);
    }

    private static void printLine() {
        System.out.println(CORNER + HORIZONTAL_LINE.repeat(WIDTH) + CORNER);
    }

    private static void printDoubleLine() {
        System.out.println(DOUBLE_CORNER + DOUBLE_HORIZONTAL.repeat(WIDTH) + DOUBLE_CORNER);
    }

    private static void printCentered(String text) {
        if (text.length() > WIDTH) {
            text = text.substring(0, WIDTH);
        }
        int padding = (WIDTH - text.length()) / 2;
        String leftSpaces = " ".repeat(padding);
        String rightSpaces = " ".repeat(WIDTH - text.length() - padding);
        System.out.println(VERTICAL_LINE + leftSpaces + text + rightSpaces + VERTICAL_LINE);
    }

    private static void printDoubleCentered(String text) {
        if (text.length() > WIDTH) {
            text = text.substring(0, WIDTH);
        }
        int padding = (WIDTH - text.length()) / 2;
        String leftSpaces = " ".repeat(padding);
        String rightSpaces = " ".repeat(WIDTH - text.length() - padding);
        System.out.println(DOUBLE_VERTICAL + leftSpaces + text + rightSpaces + DOUBLE_VERTICAL);
    }

    private static void printStatusLine(IntensityLevel level, double pressure, ToothbrushController controller) {
        String modePart = "Modus: " + level.getDisplayName();
        String pressurePart = "Druck: " + String.format("%.1f N %s", pressure, getPressureStatus(controller));
        int spaceWidth = WIDTH - modePart.length() - pressurePart.length();

        if (spaceWidth < 0) {
            // Kürze den längeren Teil bei Überlauf
            int overflow = -spaceWidth;
            if (modePart.length() > pressurePart.length()) {
                modePart = modePart.substring(0, modePart.length() - overflow - 3) + "...";
            } else {
                pressurePart = pressurePart.substring(0, pressurePart.length() - overflow - 3) + "...";
            }
            spaceWidth = 0;
        }

        String spaces = " ".repeat(spaceWidth);
        System.out.println(VERTICAL_LINE + modePart + spaces + pressurePart + VERTICAL_LINE);
    }

    private static void printCommands() {
        String commands = "Befehle: [B] Button  [P] Druck  [H] Hilfe  [Q] Ende";
        // Sicherstellen, dass der Befehlstext nicht zu lang ist
        if (commands.length() > WIDTH) {
            commands = commands.substring(0, WIDTH);
        }
        int padding = WIDTH - commands.length();
        String spaces = " ".repeat(padding);
        System.out.println(VERTICAL_LINE + commands + spaces + VERTICAL_LINE);
    }

    private static void printCommand(String cmd, String description) {
        String line = "[" + cmd + "] - " + description;
        // Sicherstellen, dass die Zeile nicht zu lang ist
        if (line.length() > WIDTH - 4) {
            line = line.substring(0, WIDTH - 7) + "...";
        }
        int padding = WIDTH - line.length() - 4;
        String spaces = " ".repeat(padding);
        System.out.println(DOUBLE_VERTICAL + " " + line + spaces + " " + DOUBLE_VERTICAL);
    }

    private static void printWarning(String message) {
        if (message.length() > WIDTH) {
            message = message.substring(0, WIDTH);
        }
        int padding = (WIDTH - message.length()) / 2;
        String leftSpaces = " ".repeat(padding);
        String rightSpaces = " ".repeat(WIDTH - message.length() - padding);
        System.out.println(DOUBLE_VERTICAL + leftSpaces + message + rightSpaces + DOUBLE_VERTICAL);
    }

    /**
     * Zeigt kompakte Zahnbürsten-Visualisierung
     */
    private static void showBrushVisualization(IntensityLevel level) {
        System.out.println(VERTICAL_LINE + " ".repeat(WIDTH) + VERTICAL_LINE);

        if (level == IntensityLevel.OFF) {
            printCentered("  [=====]  ");
            printCentered("  |_____|  ");
            printCentered("    AUS    ");
        } else {
            String vibration = getVibrationString(level);
            String brush = vibration + "[=====]" + vibration;
            printCentered(brush);
            printCentered("  |_____|  ");
            printCentered("  AKTIV  ");
        }
        System.out.println(VERTICAL_LINE + " ".repeat(WIDTH) + VERTICAL_LINE);
    }

    /**
     * Zeigt Motor-Anzeige
     */
    private static void showMotorGauge(IntensityLevel level) {
        String gaugeLabel = "0%    25%    50%    75%   100%";
        printDoubleCentered(gaugeLabel);

        int position = level == IntensityLevel.OFF ? 0 : (level.ordinal() * 33) / 5;
        StringBuilder gauge = new StringBuilder();
        for (int i = 0; i <= 20; i++) {
            if (i == position) gauge.append("V");
            else if (i % 5 == 0) gauge.append("|");
            else gauge.append("-");
        }

        printDoubleCentered(gauge.toString());
    }

    /**
     * Zeigt Druck-Verlauf mit einfacher Grafik
     */
    private static void showPressureHistory(double currentPressure) {
        printDoubleCentered("   Hoch  +------------------------+   ");
        printDoubleCentered("         |      *    **           |   ");
        printDoubleCentered("   Mittel|   ***  **    *         |   ");
        printDoubleCentered("         | **              **   * |   ");
        printDoubleCentered("   Niedrig+------------------------+   ");
        printDoubleCentered("         Vor 30s         Jetzt        ");
    }

    /**
     * Zeigt einfachen Fortschrittsbalken
     */
    private static void printProgressBar(int percent, int width) {
        int filled = (percent * width) / 100;
        System.out.print("[");
        for (int i = 0; i < width; i++) {
            if (i < filled) System.out.print("#");
            else System.out.print("-");
        }
        System.out.print("]");
    }

    /**
     * Zeigt Druck-Balken mit Farbcodierung (durch Zeichen)
     */
    private static void printPressureBar(double pressure, int width) {
        int percent = (int)((pressure / 10.0) * 100);
        int filled = (percent * width) / 100;

        System.out.print("[");
        for (int i = 0; i < width; i++) {
            if (i < filled) {
                if (pressure > 3.0) System.out.print("!");
                else if (pressure > 2.0) System.out.print("=");
                else System.out.print("#");
            } else {
                System.out.print(".");
            }
        }
        System.out.print("]");
    }

    private static String getVibrationString(IntensityLevel level) {
        return switch (level) {
            case GENTLE -> "~";
            case NORMAL -> "~~";
            case INTENSE -> "~~~";
            default -> "";
        };
    }

    private static String getPressureStatus(ToothbrushController controller) {
        if (controller.getPressureSensor().isCriticalThresholdExceeded()) {
            return "[!!!]";
        } else if (controller.getPressureSensor().isWarningThresholdExceeded()) {
            return "[!]";
        }
        return "[OK]";
    }

    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback
            for (int i = 0; i < 30; i++) System.out.println();
        }
    }
}