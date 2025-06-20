package de.thd.zahnputzmaschine.hardware;

import de.thd.zahnputzmaschine.model.IntensityLevel;
import de.thd.zahnputzmaschine.util.SimpleLogger;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Erweiterte Motor-Controller für Iteration 3
 * Simuliert realistische Motorbewegungen mit Trägheit und Vibrationsmuster
 *
 * @author Nikola Valchev
 * @version 3.0
 */
public class EnhancedMotorController implements HardwareMockStrategy {
    private static final SimpleLogger logger = new SimpleLogger(EnhancedMotorController.class);

    // Motor-Eigenschaften basierend auf IntensityLevel
    private static final int GENTLE_RPM = 4000;
    private static final int NORMAL_RPM = 6500;
    private static final int INTENSE_RPM = 9000;

    // Aktuelle Werte
    private int targetSpeed = 0;
    private int currentSpeed = 0;
    private boolean isRunning = false;
    private IntensityLevel currentIntensity = IntensityLevel.OFF;

    // Vibrationsmuster
    private int[] vibrationPattern = {1000, 0}; // Standard: konstant
    private Timer vibrationTimer;
    private boolean vibrationActive = true;

    // Motor-Simulation
    private Timer motorSimulationTimer;
    private static final double ACCELERATION_RATE = 0.12; // 12% pro Update
    private static final int UPDATE_INTERVAL_MS = 50; // 20Hz

    // Zusätzliche Features für Iteration 3
    private long totalRuntime = 0;
    private long sessionStartTime = 0;
    private int vibrationCycles = 0;

    public EnhancedMotorController() {
        logger.info("Enhanced MotorController initialisiert");
    }

    /**
     * Startet Motor mit gegebener Intensität
     */
    public void startMotor(IntensityLevel intensity) {
        if (intensity == IntensityLevel.OFF) {
            stopMotor();
            return;
        }

        this.currentIntensity = intensity;
        this.targetSpeed = getSpeedForIntensity(intensity);

        if (!isRunning) {
            isRunning = true;
            sessionStartTime = System.currentTimeMillis();
            startMotorSimulation();
            logger.info("Motor gestartet: " + intensity + " (" + targetSpeed + " RPM)");
        } else {
            logger.info("Motor-Intensität geändert: " + intensity + " (" + targetSpeed + " RPM)");
        }

        // Setze Vibrationsmuster basierend auf Intensität
        updateVibrationPattern(intensity);
    }

    /**
     * Stoppt den Motor
     */
    public void stopMotor() {
        if (isRunning) {
            isRunning = false;
            targetSpeed = 0;
            currentIntensity = IntensityLevel.OFF;

            // Stoppt den Simulationstimer sofort
            if (motorSimulationTimer != null) {
                motorSimulationTimer.cancel();
                motorSimulationTimer = null;
            }

            // Aktualisiert Laufzeit
            if (sessionStartTime > 0) {
                totalRuntime += System.currentTimeMillis() - sessionStartTime;
                sessionStartTime = 0;
            }

            // Stoppt Vibrationstimer
            if (vibrationTimer != null) {
                vibrationTimer.cancel();
                vibrationTimer = null;
            }

            logger.info("Motor wird gestoppt. Session-Laufzeit: " +
                    (totalRuntime / 1000) + " Sekunden");
        }
    }

    /**
     * Konvertiert IntensityLevel zu RPM
     */
    private int getSpeedForIntensity(IntensityLevel level) {
        switch (level) {
            case GENTLE: return GENTLE_RPM;
            case NORMAL: return NORMAL_RPM;
            case INTENSE: return INTENSE_RPM;
            default: return 0;
        }
    }

    /**
     * Aktualisiert Vibrationsmuster basierend auf Intensität
     */
    private void updateVibrationPattern(IntensityLevel intensity) {
        switch (intensity) {
            case GENTLE:
                // Sanftes Pulsieren
                setVibrationPattern(new int[]{300, 200});
                break;
            case NORMAL:
                // Konstante Vibration
                setVibrationPattern(new int[]{1000, 0});
                break;
            case INTENSE:
                // Intensives Pulsieren mit Variation
                setVibrationPattern(new int[]{200, 100, 400, 100});
                break;
            default:
                setVibrationPattern(new int[]{0, 1000});
        }
    }

    /**
     * Startet Motor-Simulation mit Trägheit
     */
    private void startMotorSimulation() {
        if (motorSimulationTimer != null) {
            motorSimulationTimer.cancel();
        }

        motorSimulationTimer = new Timer("MotorSimulation");
        motorSimulationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateMotorSpeed();
            }
        }, 0, UPDATE_INTERVAL_MS);
    }

    /**
     * Aktualisiert Motorgeschwindigkeit mit realistischer Trägheit
     */
    private void updateMotorSpeed() {
        if (currentSpeed != targetSpeed) {
            // Sofortige Anpassung für Testumgebung
            if (System.getProperty("testMode") != null) {
                currentSpeed = targetSpeed;
            } else {
                // Bestehende Beschleunigungslogik
                int diff = targetSpeed - currentSpeed;
                int change = (int)(diff * ACCELERATION_RATE);
                currentSpeed += change;
            }

            // Stoppe Timer wenn Motor aus
            if (currentSpeed == 0 && targetSpeed == 0) {
                if (motorSimulationTimer != null) {
                    motorSimulationTimer.cancel();
                    motorSimulationTimer = null;
                }
            }
        }
    }

    /**
     * Triggert spezielle Vibration für Warnungen
     */
    public void triggerWarningVibration() {
        // Speichere aktuelles Pattern
        int[] originalPattern = vibrationPattern.clone();

        // Setze Warn-Pattern: 3x kurz vibrieren
        setVibrationPattern(new int[]{100, 100, 100, 100, 100, 500});

        // Nach 2 Sekunden zurück zum Original
        Timer resetTimer = new Timer();
        resetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setVibrationPattern(originalPattern);
            }
        }, 2000);

        logger.info("Warn-Vibration ausgelöst");
    }

    /**
     * Triggert Intervall-Vibration (alle 30 Sekunden)
     */
    public void triggerIntervalVibration() {
        // Kurze Doppelvibration
        int[] originalPattern = vibrationPattern.clone();
        setVibrationPattern(new int[]{200, 200, 200, 800});

        Timer resetTimer = new Timer();
        resetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setVibrationPattern(originalPattern);
            }
        }, 1600);
    }

    // HardwareMockStrategy Implementation

    @Override
    public double readPressure() {
        // Wird vom Sensor gehandhabt
        return 0.0;
    }

    @Override
    public void setMotorSpeed(int speed) {
        this.targetSpeed = Math.max(0, Math.min(10000, speed));
    }

    @Override
    public boolean isMotorRunning() {
        return isRunning && currentSpeed > 0;
    }


    @Override
    public void setVibrationPattern(int[] pattern) {
        this.vibrationPattern = pattern;
        vibrationCycles++;

        if (vibrationTimer != null) {
            vibrationTimer.cancel();
        }

        if (isRunning && pattern.length >= 2) {
            startVibrationPattern();
        }
    }

    /**
     * Startet Vibrationsmuster-Ausführung
     */
    private void startVibrationPattern() {
        if (vibrationPattern == null || vibrationPattern.length == 0) {
            return;
        }

        vibrationTimer = new Timer("VibrationPattern");
        final int[] index = {0};
        vibrationActive = true;

        TimerTask vibrationTask = new TimerTask() {
            @Override
            public void run() {
                if (!isRunning) {
                    vibrationTimer.cancel();
                    return;
                }

                vibrationActive = (index[0] % 2 == 0);
                index[0] = (index[0] + 1) % vibrationPattern.length;

                if (index[0] < vibrationPattern.length) {
                    int nextDelay = vibrationPattern[index[0]];
                    vibrationTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startVibrationPattern();
                        }
                    }, nextDelay);
                }
            }
        };

        vibrationTimer.schedule(vibrationTask, vibrationPattern[0]);
    }


    public String getHardwareStatus() {
        int oscillations = currentSpeed * 2; // Umrechnung RPM → Schwingungen/min
        return String.format("Motor[Oscillations=%d/min, Target=%d/min]",
                oscillations, targetSpeed * 2);
    }

    // Getter für GUI
    public int getCurrentSpeed() { return currentSpeed; }
    public int getTargetSpeed() { return targetSpeed; }
    public IntensityLevel getCurrentIntensity() { return currentIntensity; }
    public boolean isVibrationActive() { return vibrationActive && isRunning; }
    public long getTotalRuntime() { return totalRuntime; }
    public int getVibrationCycles() { return vibrationCycles; }

    /**
     * Berechnet aktuelle Intensität in Prozent
     */
    public int getIntensityPercent() {
        if (!isRunning) return 0;
        return (int)((currentSpeed / 10000.0) * 100);
    }
    // Neue reset Methode
    public void resetStatistics() {
        totalRuntime = 0;
        vibrationCycles = 0;
        sessionStartTime = 0;
        logger.info("Motorstatistiken zurückgesetzt");
    }

    // Synchronisierungsmethode für Tests
    public void waitUntilTargetSpeedReached() throws InterruptedException {
        while (Math.abs(currentSpeed - targetSpeed) > 100) {
            Thread.sleep(50);
        }
    }

    public void waitForTargetSpeed() throws InterruptedException {
        while (Math.abs(currentSpeed - targetSpeed) > 100) {
            Thread.sleep(10);
        }
    }
}
