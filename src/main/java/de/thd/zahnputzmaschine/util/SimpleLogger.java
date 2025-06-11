package src.main.java.de.thd.zahnputzmaschine.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleLogger {
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String className;

    public SimpleLogger(Class<?> clazz) {
        this.className = clazz.getSimpleName();
    }

    public void info(String message) {
        System.out.printf("[%s] %s - %s%n",
                LocalDateTime.now().format(formatter),
                className,
                message
        );
    }

    // Überladene Methode für Nachrichten mit einem Parameter
    public void info(String message, Object arg1) {
        String formattedMessage = message.replace("{}", String.valueOf(arg1));
        info(formattedMessage);
    }

    // Überladene Methode für Nachrichten mit zwei Parametern
    public void info(String message, Object arg1, Object arg2) {
        String formattedMessage = message
                .replaceFirst("\\{\\}", String.valueOf(arg1))
                .replaceFirst("\\{\\}", String.valueOf(arg2));
        info(formattedMessage);
    }

}
