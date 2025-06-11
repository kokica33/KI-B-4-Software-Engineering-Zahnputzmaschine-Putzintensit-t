package src.main.java.de.thd.zahnputzmaschine;

import src.main.java.de.thd.zahnputzmaschine.controller.ToothbrushController;
import java.util.Scanner;

/**
 * Hauptklasse für die Zahnputzmaschinen-Simulation
 * Bietet eine einfache Konsolen-Schnittstelle zur Interaktion
 *
 * @author [Dein Name]
 * @version 1.0 - Iteration 1
 */
public class Main {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  ZAHNPUTZMASCHINE - SIMULATOR v1.0");
        System.out.println("  Iteration 1: Grundfunktionalität");
        System.out.println("===========================================\n");

        ToothbrushController controller = new ToothbrushController();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        printHelp();

        while (running) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "b":
                case "button":
                    System.out.println("\n" + ANSI_YELLOW + "[BUTTON PRESSED]" + ANSI_RESET);
                    controller.buttonPress();
                    printStatus(controller);
                    break;

                case "s":
                case "status":
                    printStatus(controller);
                    break;

                case "h":
                case "help":
                    printHelp();
                    break;

                case "q":
                case "quit":
                    running = false;
                    System.out.println("\nZahnbürste wird ausgeschaltet...");
                    break;

                default:
                    System.out.println("Unbekannter Befehl. Tippe 'h' für Hilfe.");
            }
        }

        scanner.close();
        System.out.println("Programm beendet.");
    }

    private static void printHelp() {
        System.out.println("\n--- BEDIENUNG ---");
        System.out.println("b/button - Knopf drücken (Intensität wechseln)");
        System.out.println("s/status - Aktuellen Status anzeigen");
        System.out.println("h/help   - Diese Hilfe anzeigen");
        System.out.println("q/quit   - Programm beenden");
        System.out.println("-----------------");
    }

    private static void printStatus(ToothbrushController controller) {
        System.out.println("\n--- AKTUELLER STATUS ---");
        System.out.println("Intensität: " + getColoredIntensity(controller));
        System.out.println("Frequenz: " + controller.getCurrentIntensity().getFrequency() + " Schwingungen/min");
        System.out.println("Zeit im Zustand: " + (controller.getTimeInCurrentState() / 1000) + " Sekunden");
        System.out.println("------------------------");
    }

    private static String getColoredIntensity(ToothbrushController controller) {
        switch (controller.getCurrentIntensity()) {
            case OFF:
                return "AUS";
            case GENTLE:
                return ANSI_GREEN + "SANFT" + ANSI_RESET;
            case NORMAL:
                return ANSI_BLUE + "NORMAL" + ANSI_RESET;
            case INTENSE:
                return ANSI_RED + "INTENSIV" + ANSI_RESET;
            default:
                return "UNBEKANNT";
        }
    }
}