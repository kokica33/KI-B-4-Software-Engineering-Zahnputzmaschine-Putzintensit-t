package de.thd.zahnputzmaschine;

import de.thd.zahnputzmaschine.controller.ToothbrushController;
import de.thd.zahnputzmaschine.listener.StateChangeListener;
import de.thd.zahnputzmaschine.model.IntensityLevel;
import de.thd.zahnputzmaschine.model.state.BrushState;
import de.thd.zahnputzmaschine.system.LEDColor;
import java.util.Scanner;

/**
 * Hauptklasse der Zahnputzmaschine - Finale Version mit allen Features
 * @author Nikola Valchev
 */
public class Main {
    private static ToothbrushController controller;
    private static Scanner scanner;
    private static boolean running = true;

    // Variablen für kontinuierliche Drucksimulation
    private static double currentSimulatedPressure = 0.0;
    private static final double PRESSURE_INCREMENT = 0.5;
    private static boolean continuousPressureMode = false;

    public static void main(String[] args) {
        controller = new ToothbrushController();
        scanner = new Scanner(System.in);

        // State Change Listener für Demo hinzufügen
        controller.addStateChangeListener(new StateChangeListener() {
            @Override
            public void onStateChanged(BrushState oldState, BrushState newState) {
                System.out.println("[STATE] Zustandswechsel: " +
                        oldState.getClass().getSimpleName() + " -> " +
                        newState.getClass().getSimpleName());
            }
        });

        printWelcome();

        // Haupt-Loop
        while (running) {
            System.out.print("\n> ");
            String input = scanner.nextLine().toLowerCase().trim();
            processInput(input);

            // Sensoren prüfen nach jeder Eingabe
            controller.checkSensors();
        }

        scanner.close();
        System.out.println("\nAuf Wiedersehen!");
    }

    private static void printWelcome() {
        System.out.println("================================================");
        System.out.println("     ZAHNPUTZMASCHINE - SIMULATOR v2.0         ");
        System.out.println("     Iteration 2: Drucksensor & Warnsystem     ");
        System.out.println("================================================");
        System.out.println(" Entwickelt von: Nikola Valchev                ");
        System.out.println(" TH Deggendorf - Software Engineering SS2025   ");
        System.out.println("================================================");
        System.out.println("\nGeben Sie 'h' fuer Hilfe ein.");
    }

    private static void processInput(String input) {
        if (input.isEmpty()) return;

        String[] parts = input.split(" ");
        String command = parts[0];

        switch (command) {
            case "b":
                controller.buttonPress();
                // BONUS-TIPP: Reset Druck beim Ausschalten
                if (controller.getCurrentIntensity() == IntensityLevel.OFF) {
                    currentSimulatedPressure = 0.0;
                    continuousPressureMode = false;
                    System.out.println("[RESET] Druck automatisch zurueckgesetzt");
                }
                break;

            case "s":
                printStatus();
                break;

            case "p": // Absoluter Druckwert oder Status anzeigen
                if (parts.length > 1) {
                    try {
                        double pressure = Double.parseDouble(parts[1]);
                        currentSimulatedPressure = pressure;
                        continuousPressureMode = true;
                        applyPressure();
                    } catch (NumberFormatException e) {
                        System.out.println("[ERROR] Ungueltiger Druckwert. Beispiel: p 3.5");
                    }
                } else {
                    // Ohne Parameter: Zeige aktuellen Druck
                    System.out.println("[PRESSURE] Aktueller simulierter Druck: " +
                            String.format("%.1f", currentSimulatedPressure) + " N");
                    if (!continuousPressureMode) {
                        System.out.println("[INFO] Starte mit 'p <wert>' oder nutze p+ / p-");
                    }
                }
                break;

            case "p+":
            case "+":
                increasePressure(parts.length > 1 ? parseIncrement(parts[1]) : PRESSURE_INCREMENT);
                break;

            case "p-":
            case "-":
                decreasePressure(parts.length > 1 ? parseIncrement(parts[1]) : PRESSURE_INCREMENT);
                break;

            case "p0":
            case "0":
                resetPressure();
                break;

            case "pp":
                toggleContinuousPressure();
                break;

            case "d": // Drucksensor Status
                printSensorStatus();
                break;

            case "c": // Kalibrieren
                controller.getPressureSensor().calibrate();
                System.out.println("[OK] Drucksensor kalibriert");
                break;

            case "r": // Zufälliger Druck
                double randomPressure = Math.random() * 8;
                currentSimulatedPressure = randomPressure;
                continuousPressureMode = true;
                controller.getPressureSensor().simulateRealisticPressure(randomPressure, 0.5);
                System.out.println("[RANDOM] Zufaelliger Druck: " +
                        String.format("%.1f", controller.getPressureSensor().getCurrentValue()) + " N");
                controller.checkSensors();
                break;

            case "e": // Emergency Shutdown Toggle
                boolean enabled = !controller.isEmergencyShutdownEnabled();
                controller.setEmergencyShutdownEnabled(enabled);
                System.out.println("[EMERGENCY] Notabschaltung: " + (enabled ? "AKTIVIERT" : "DEAKTIVIERT"));
                break;

            case "t": // Threshold-Übersicht
                demonstratePressureThresholds();
                break;

            case "h":
                printHelp();
                break;

            case "q":
                running = false;
                break;

            default:
                System.out.println("[ERROR] Unbekannter Befehl. 'h' fuer Hilfe.");
        }
    }

    // Hilfsmethoden für Drucksimulation

    private static void increasePressure(double increment) {
        if (controller.getCurrentIntensity() == IntensityLevel.OFF) {
            System.out.println("[INFO] Zahnbuerste ist aus. Bitte erst einschalten.");
            return;
        }

        continuousPressureMode = true;
        double newPressure = currentSimulatedPressure + increment;

        if (newPressure > 10.0) {
            newPressure = 10.0;
            System.out.println("[MAX] Maximaler Druck erreicht!");
        }

        currentSimulatedPressure = newPressure;
        System.out.println("[+] Druck erhoeht um " + increment + " N");
        applyPressure();
    }

    private static void decreasePressure(double decrement) {
        if (!continuousPressureMode) {
            System.out.println("[INFO] Kein aktiver Druck. Nutze 'p <wert>' zum Starten.");
            return;
        }

        double newPressure = currentSimulatedPressure - decrement;

        if (newPressure < 0.0) {
            newPressure = 0.0;
        }

        currentSimulatedPressure = newPressure;
        System.out.println("[-] Druck verringert um " + decrement + " N");
        applyPressure();
    }

    private static void resetPressure() {
        currentSimulatedPressure = 0.0;
        continuousPressureMode = false;
        System.out.println("[RESET] Druck auf 0 N zurueckgesetzt");
        controller.getPressureSensor().simulatePressure(0.0);
        controller.checkSensors();
    }

    private static void toggleContinuousPressure() {
        if (controller.getCurrentIntensity() == IntensityLevel.OFF) {
            System.out.println("[INFO] Zahnbuerste ist aus. Bitte erst einschalten.");
            return;
        }

        if (!continuousPressureMode) {
            continuousPressureMode = true;
            if (currentSimulatedPressure == 0.0) {
                currentSimulatedPressure = 2.0;
            }
            System.out.println("[CONTINUOUS] Kontinuierlicher Druck-Modus aktiviert");
            applyPressure();
        } else {
            continuousPressureMode = false;
            System.out.println("[CONTINUOUS] Kontinuierlicher Druck-Modus deaktiviert");
        }
    }

    private static void applyPressure() {
        controller.getPressureSensor().simulatePressure(currentSimulatedPressure);
        controller.checkSensors();

        var sensor = controller.getPressureSensor();
        String status = getCompactStatus(sensor);
        String bar = getPressureBar(currentSimulatedPressure, sensor);

        System.out.println("[PRESSURE] " + String.format("%.1f N", currentSimulatedPressure) +
                " | " + bar + " | " + status);

        if (sensor.isWarningThresholdExceeded() && !sensor.isCriticalThresholdExceeded()) {
            System.out.println("[INFO] Warnschwelle (" +
                    String.format("%.1f N", sensor.getWarningThreshold()) +
                    ") ueberschritten fuer " + controller.getCurrentIntensity());
        } else if (sensor.isCriticalThresholdExceeded()) {
            System.out.println("[INFO] Kritische Schwelle (" +
                    String.format("%.1f N", sensor.getCriticalThreshold()) +
                    ") ueberschritten fuer " + controller.getCurrentIntensity());
        }
    }

    private static String getCompactStatus(de.thd.zahnputzmaschine.model.sensor.PressureSensor sensor) {
        if (sensor.isCriticalThresholdExceeded()) {
            return "KRITISCH!";
        } else if (sensor.isWarningThresholdExceeded()) {
            return "WARNUNG";
        }
        return "OK";
    }

    private static String getPressureBar(double pressure, de.thd.zahnputzmaschine.model.sensor.PressureSensor sensor) {
        int barLength = 20;
        int filledLength = (int) ((pressure / 10.0) * barLength);

        StringBuilder bar = new StringBuilder("[");

        for (int i = 0; i < barLength; i++) {
            double positionPressure = (i / (double) barLength) * 10.0;

            if (i < filledLength) {
                if (positionPressure >= sensor.getCriticalThreshold()) {
                    bar.append("#");
                } else if (positionPressure >= sensor.getWarningThreshold()) {
                    bar.append("!");
                } else {
                    bar.append("=");
                }
            } else {
                bar.append("-");
            }
        }

        bar.append("]");
        return bar.toString();
    }

    private static double parseIncrement(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return PRESSURE_INCREMENT;
        }
    }

    private static void printStatus() {
        System.out.println("\n+----------------------------------+");
        System.out.println("|        AKTUELLER STATUS          |");
        System.out.println("+----------------------------------+");
        System.out.println("| Zustand: " + String.format("%-23s",
                controller.getCurrentState().getClass().getSimpleName()) + "|");
        System.out.println("| Intensitaet: " + String.format("%-19s",
                controller.getCurrentIntensity()) + "|");
        System.out.println("| Frequenz: " + String.format("%-22s",
                controller.getCurrentIntensity().getFrequency() + " Hz") + "|");
        System.out.println("| Zeit im Zustand: " + String.format("%-15s",
                (controller.getTimeInCurrentState() / 1000) + " s") + "|");

        // LED Status
        LEDColor[] leds = controller.getWarningSystem().getLEDStatus();
        System.out.println("| Status-LED: " + String.format("%-20s",
                leds[0].getDisplayName()) + "|");
        if (leds[1] != LEDColor.OFF) {
            System.out.println("| Warn-LED: " + String.format("%-22s",
                    leds[1].getDisplayName()) + "|");
        }

        System.out.println("+----------------------------------+");
    }

    private static void printSensorStatus() {
        System.out.println("\n+----------------------------------+");
        System.out.println("|      DRUCKSENSOR STATUS          |");
        System.out.println("+----------------------------------+");

        var sensor = controller.getPressureSensor();

        // Aktuelle Werte
        String statusLine = String.format("| Druck: %.1f %s - Status: %-7s |",
                sensor.getCurrentValue(),
                sensor.getUnit(),
                getStatusText(sensor));
        System.out.println(statusLine);

        // Modus-Information
        System.out.println("| Modus: " + String.format("%-25s",
                controller.getCurrentIntensity() + " (" +
                        controller.getCurrentIntensity().getFrequency() + " Hz)") + "|");

        // Dynamische Schwellwerte
        System.out.println("| Warnschwelle: " + String.format("%-18s",
                String.format("%.1f N", sensor.getWarningThreshold())) + "|");
        System.out.println("| Kritische Schwelle: " + String.format("%-12s",
                String.format("%.1f N", sensor.getCriticalThreshold())) + "|");

        // Schwellwert-Info
        String thresholdInfo = "";
        switch (controller.getCurrentIntensity()) {
            case GENTLE:
                thresholdInfo = "Mehr Druck erlaubt";
                break;
            case NORMAL:
                thresholdInfo = "Standard-Schwellen";
                break;
            case INTENSE:
                thresholdInfo = "Reduzierter Druck!";
                break;
            default:
                thresholdInfo = "Keine Ueberwachung";
        }
        System.out.println("| Info: " + String.format("%-26s", thresholdInfo) + "|");

        System.out.println("| Kalibriert: " + String.format("%-20s",
                sensor.isCalibrated() ? "Ja" : "Nein") + "|");
        System.out.println("| Warnsystem: " + String.format("%-20s",
                controller.getWarningSystem().isActive() ? "Aktiv" : "Inaktiv") + "|");
        System.out.println("+----------------------------------+");
    }

    private static String getStatusText(de.thd.zahnputzmaschine.model.sensor.PressureSensor sensor) {
        if (sensor.isCriticalThresholdExceeded()) {
            return "KRITISCH";
        } else if (sensor.isWarningThresholdExceeded()) {
            return "WARNUNG";
        }
        return "OK";
    }

    private static void demonstratePressureThresholds() {
        System.out.println("\n+----------------------------------+");
        System.out.println("|   DRUCKSCHWELLEN-UEBERSICHT      |");
        System.out.println("+----------------------------------+");
        System.out.println("| Modus    | Warnung | Kritisch   |");
        System.out.println("|----------|---------|------------|");
        System.out.println("| GENTLE   | 3.6 N   | 6.0 N      |");
        System.out.println("| NORMAL   | 3.0 N   | 5.0 N      |");
        System.out.println("| INTENSE  | 1.8 N   | 3.0 N      |");
        System.out.println("+----------------------------------+");
        System.out.println("| Bei hoeherer Intensitaet ist     |");
        System.out.println("| weniger Druck erlaubt!           |");
        System.out.println("+----------------------------------+");
    }

    private static void printHelp() {
        System.out.println("\n+----------------------------------+");
        System.out.println("|           HILFE                  |");
        System.out.println("+----------------------------------+");
        System.out.println("| BASIS-BEFEHLE:                   |");
        System.out.println("| b     - Button druecken          |");
        System.out.println("| s     - Status anzeigen          |");
        System.out.println("| h     - Diese Hilfe              |");
        System.out.println("| q     - Beenden                  |");
        System.out.println("|                                  |");
        System.out.println("| DRUCK-SIMULATION:                |");
        System.out.println("| p <N> - Druck setzen (0-10 N)    |");
        System.out.println("| p+    - Druck erhoehen (+0.5 N)  |");
        System.out.println("| p-    - Druck verringern (-0.5 N)|");
        System.out.println("| p+ <N>- Druck erhoehen um N      |");
        System.out.println("| p- <N>- Druck verringern um N    |");
        System.out.println("| p0    - Druck auf 0 zuruecksetzen|");
        System.out.println("| pp    - Kontinuierlicher Modus   |");
        System.out.println("| p     - Aktuellen Druck zeigen   |");
        System.out.println("|                                  |");
        System.out.println("| SENSOR-BEFEHLE:                  |");
        System.out.println("| d     - Drucksensor Status       |");
        System.out.println("| c     - Sensor kalibrieren       |");
        System.out.println("| r     - Zufaelliger Druck        |");
        System.out.println("| t     - Schwellwert-Uebersicht   |");
        System.out.println("| e     - Notabschaltung toggle    |");
        System.out.println("|                                  |");
        System.out.println("| Beispiele:                       |");
        System.out.println("| p 2.5 -> p+ -> p+ -> p-          |");
        System.out.println("| p+ 1.0 (erhoeht um 1 Newton)     |");
        System.out.println("+----------------------------------+");
    }
}