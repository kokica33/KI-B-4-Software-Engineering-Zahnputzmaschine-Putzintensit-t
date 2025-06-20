package de.thd.zahnputzmaschine.model.sensor;

import de.thd.zahnputzmaschine.hardware.HardwareMockStrategy;
import de.thd.zahnputzmaschine.util.SimpleLogger;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Erweiterte Drucksensor-Implementierung für Iteration 3
 * Bietet realistischere Simulation mit verschiedenen Benutzer-Szenarien
 *
 * @author Nikola Valchev
 * @version 3.0
 */
public class EnhancedPressureSensor extends PressureSensor {
    private static final SimpleLogger logger = new SimpleLogger(EnhancedPressureSensor.class);

    // Simulation Parameters
    private Timer simulationTimer;
    private UserSimulationMode simulationMode = UserSimulationMode.NORMAL;
    private double targetPressure = 2.0;
    private ConcurrentLinkedQueue<Double> pressureHistory;
    private static final int HISTORY_SIZE = 20;

    // Hardware Mock Integration
    private HardwareMockStrategy hardwareMock;

    /**
     * Benutzer-Simulationsmodi für Tests und Demos
     */
    public enum UserSimulationMode {
        GENTLE("Sanfter Benutzer", 1.0, 2.0, 0.1),
        NORMAL("Normaler Benutzer", 1.5, 2.5, 0.2),
        AGGRESSIVE("Aggressiver Benutzer", 3.0, 4.5, 0.3),
        VARIABLE("Variabler Benutzer", 0.5, 4.0, 0.5),
        REALISTIC("Realistisch", 1.0, 3.0, 0.15);

        private final String description;
        private final double minPressure;
        private final double maxPressure;
        private final double variance;

        UserSimulationMode(String desc, double min, double max, double var) {
            this.description = desc;
            this.minPressure = min;
            this.maxPressure = max;
            this.variance = var;
        }
    }

    public EnhancedPressureSensor() {
        super();
        this.pressureHistory = new ConcurrentLinkedQueue<>();
        logger.info("Enhanced PressureSensor für Iteration 3 initialisiert");
    }

    /**
     * Setzt Hardware-Mock für Integration
     */
    public void setHardwareMock(HardwareMockStrategy mock) {
        this.hardwareMock = mock;
    }

    /**
     * Startet realistische Drucksimulation
     */
    public void startSimulation(UserSimulationMode mode) {
        stopSimulation();
        this.simulationMode = mode;

        simulationTimer = new Timer("PressureSimulation");
        simulationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                simulateRealisticPressure();
            }
        }, 0, 50); // 20Hz Update-Rate

        logger.info("Drucksimulation gestartet: " + mode.description);
    }

    /**
     * Stoppt die Simulation
     */
    public void stopSimulation() {
        if (simulationTimer != null) {
            simulationTimer.cancel();
            simulationTimer = null;
            simulatePressure(0.0);
            logger.info("Drucksimulation gestoppt");
        }
    }

    /**
     * Simuliert realistische Druckwerte mit Übergängen
     */
    private void simulateRealisticPressure() {
        Random random = new Random();
        double basePressure;

        // Berechne Zieldruck basierend auf Modus
        switch (simulationMode) {
            case GENTLE:
                targetPressure = simulationMode.minPressure +
                        random.nextDouble() * (simulationMode.maxPressure - simulationMode.minPressure);
                break;

            case AGGRESSIVE:
                basePressure = simulationMode.minPressure +
                        random.nextDouble() * (simulationMode.maxPressure - simulationMode.minPressure);
                // Garantiere Druck über 3.0
                if (basePressure < 3.0) {
                    basePressure = 3.0 + random.nextDouble();
                }
                // 70% Chance für Druckspitze
                if (random.nextDouble() < 0.7) {
                    basePressure += 1.5 + random.nextDouble() * 2.5;
                }
                targetPressure = basePressure;
                break;

            case VARIABLE:
                // Sinusförmige Variation für variablen Benutzer
                double time = System.currentTimeMillis() / 1000.0;
                double sineWave = Math.sin(time * 0.5) * 0.5 + 0.5;
                targetPressure = simulationMode.minPressure +
                        sineWave * (simulationMode.maxPressure - simulationMode.minPressure);
                break;

            case REALISTIC:
                // Realistisches Putzverhalten mit Zonen
                long cycleTime = System.currentTimeMillis() % 30000; // 30s Zyklus
                if (cycleTime < 10000) {
                    // Vordere Zähne: mittlerer Druck
                    targetPressure = 2.0 + random.nextGaussian() * 0.3;
                } else if (cycleTime < 20000) {
                    // Backenzähne: höherer Druck
                    targetPressure = 2.5 + random.nextGaussian() * 0.4;
                } else {
                    // Zahnfleischmassage: leichter Druck
                    targetPressure = 1.5 + random.nextGaussian() * 0.2;
                }
                break;

            default:
                targetPressure = 2.0;
        }

        // Sanfte Annäherung an Zielwert (Trägheit)
        double currentPressure = getCurrentValue();
        double diff = targetPressure - currentPressure;
        double newPressure = currentPressure + diff * 0.15; // 15% Annäherung

        // Füge Sensorrauschen hinzu
        newPressure += random.nextGaussian() * simulationMode.variance;

        // Setze neuen Wert
        simulatePressure(Math.max(0, Math.min(10, newPressure)));

        // Aktualisiere Historie
        updateHistory(newPressure);

        // Integration mit Hardware-Mock
        if (hardwareMock != null) {
            // Mock liest denselben Wert
            // Dies ermöglicht synchronisierte Hardware-Simulation
        }
    }

    /**
     * Aktualisiert Druckhistorie für Analyse
     */
    private void updateHistory(double pressure) {
        pressureHistory.offer(pressure);
        if (pressureHistory.size() > HISTORY_SIZE) {
            pressureHistory.poll();
        }
    }

    /**
     * Gibt durchschnittlichen Druck der letzten Messungen zurück
     */
    public double getAveragePressure() {
        if (pressureHistory.isEmpty()) return 0.0;

        return pressureHistory.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Gibt Druckvarianz zurück (für Stabilitätsanalyse)
     */
    public double getPressureVariance() {
        if (pressureHistory.size() < 2) return 0.0;

        double avg = getAveragePressure();
        return pressureHistory.stream()
                .mapToDouble(p -> Math.pow(p - avg, 2))
                .average()
                .orElse(0.0);
    }

    /**
     * Prüft ob Druck stabil ist
     */
    public boolean isPressureStable() {
        return getPressureVariance() < 0.5; // Schwellwert für Stabilität
    }

    @Override
    public String toString() {
        return String.format("EnhancedPressureSensor[Mode=%s, Current=%.1fN, Avg=%.1fN, Stable=%s]",
                simulationMode, getCurrentValue(), getAveragePressure(), isPressureStable());
    }
}