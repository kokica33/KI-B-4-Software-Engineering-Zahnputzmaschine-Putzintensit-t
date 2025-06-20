package de.thd.zahnputzmaschine;

import de.thd.zahnputzmaschine.controller.ToothbrushController;
import de.thd.zahnputzmaschine.listener.StateChangeListener;
import de.thd.zahnputzmaschine.model.IntensityLevel;
import de.thd.zahnputzmaschine.model.state.BrushState;
import de.thd.zahnputzmaschine.ui.SimpleTerminalUI;

import java.util.Scanner;

/**
 * Hauptklasse der Zahnputzmaschine - Minimalistischer Modus
 * Reduzierte Befehle: B, P, H, Q
 *
 * @author Nikola Valchev
 * @version 3.0
 */
public class Main {
    private static ToothbrushController controller;
    private static Scanner scanner;
    private static boolean running = true;
    private static double currentPressure = 0.0;

    public static void main(String[] args) {
        controller = new ToothbrushController();
        scanner = new Scanner(System.in);


        // State Change Listener - zeigt automatisch neue Ansicht
        controller.addStateChangeListener(new StateChangeListener() {
            @Override
            public void onStateChanged(BrushState oldState, BrushState newState) {
                // Automatisches Update der Anzeige bei Zustandswechsel
                SimpleTerminalUI.showMainView(controller);
            }
        });

        // Initiale Anzeige
        SimpleTerminalUI.showMainView(controller);

        // Haupt-Loop
        while (running) {
            System.out.print("\n> ");
            String input = scanner.nextLine().toUpperCase().trim();
            processInput(input);
        }

        scanner.close();
        System.out.println("\nAuf Wiedersehen!");
    }

    private static void processInput(String input) {
        // Parse command and arguments
        String[] parts = input.split(" ");
        String command = parts[0];

        switch (command) {
            case "B": // Button
                controller.buttonPress();
                controller.checkSensors();
                // Anzeige wird automatisch durch StateChangeListener aktualisiert
                break;

            case "P": // Pressure
                handlePressureCommand(parts);
                break;

            case "P+": // Druck erhöhen
                adjustPressure(0.5);
                break;

            case "P-": // Druck verringern
                adjustPressure(-0.5);
                break;

            case "P0": // Druck zurücksetzen
                currentPressure = 0.0;
                controller.getPressureSensor().simulatePressure(0.0);
                controller.checkSensors();
                SimpleTerminalUI.showMainView(controller);
                break;

            case "H": // Help - erweiterte Ansicht
                SimpleTerminalUI.showHelpView(controller);
                waitForEnter();
                SimpleTerminalUI.showMainView(controller);
                break;

            case "Q": // Quit
                running = false;
                break;

            default:
                System.out.println("Unbekannter Befehl. Nutze: B, P, H oder Q");
                // Zeige Hauptansicht nochmal
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    // ignore
                }
                SimpleTerminalUI.showMainView(controller);
        }
    }

    private static void handlePressureCommand(String[] parts) {
        if (parts.length > 1) {
            try {
                double pressure = Double.parseDouble(parts[1]);
                if (pressure < 0 || pressure > 10) {
                    System.out.println("Druck muss zwischen 0 und 10 Newton liegen!");
                    Thread.sleep(1500);
                } else {
                    currentPressure = pressure;
                    controller.getPressureSensor().simulatePressure(pressure);
                    controller.checkSensors();
                    SimpleTerminalUI.showMainView(controller);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ungueltige Zahl! Verwendung: P <0-10>");
                try { Thread.sleep(1500); } catch (InterruptedException ie) {}
                SimpleTerminalUI.showMainView(controller);
            } catch (InterruptedException e) {
                // ignore
            }
        } else {
            System.out.println("Verwendung: P <wert> (z.B. P 2.5)");
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
            SimpleTerminalUI.showMainView(controller);
        }
    }

    private static void adjustPressure(double delta) {
        double newPressure = currentPressure + delta;
        if (newPressure < 0) newPressure = 0;
        if (newPressure > 10) newPressure = 10;

        currentPressure = newPressure;
        controller.getPressureSensor().simulatePressure(currentPressure);
        controller.checkSensors();
        SimpleTerminalUI.showMainView(controller);
    }

    private static void waitForEnter() {
        try {
            scanner.nextLine();
        } catch (Exception e) {
            // ignore
        }
    }
}