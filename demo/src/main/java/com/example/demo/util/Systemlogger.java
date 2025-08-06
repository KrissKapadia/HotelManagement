package com.example.demo.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A utility class to provide a pre-configured logger for the application.
 * This ensures all log messages are sent to the console.
 */
public class Systemlogger {

    private static final Logger logger = Logger.getLogger("com.example.demo");

    static {
        // Remove existing handlers to prevent duplicate output
        for (java.util.logging.Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }

        // Create a console handler to send logs to the terminal
        ConsoleHandler consoleHandler = new ConsoleHandler();

        // Set a simple, readable format for the log messages
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getLevel() + ": " + record.getMessage() + System.lineSeparator();
            }
        });

        // Set the logging level. FINEST is for all messages, INFO for informational messages and above.
        consoleHandler.setLevel(Level.INFO);
        logger.setLevel(Level.INFO);

        // Add the console handler to the logger
        logger.addHandler(consoleHandler);
    }

    /**
     * Retrieves the pre-configured logger instance.
     * @return The logger instance.
     */
    public static Logger getLogger() {
        return logger;
    }
}
