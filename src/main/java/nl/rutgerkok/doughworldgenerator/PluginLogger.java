package nl.rutgerkok.doughworldgenerator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

/**
 * Class to record log messages from the plugin.
 */
public final class PluginLogger {

    private final ComponentLogger componentLogger;

    public PluginLogger(ComponentLogger componentLogger) {
        this.componentLogger = componentLogger;
    }

    /**
     * Logs a severe message with an exception.
     * @param message The message
     * @param e The exception
     */
    public void severe(String message, Exception e) {
        componentLogger.error(message, e);
    }

    /**
     * Logs an info message.
     * @param message The message
     */
    public void info(String message) {
        componentLogger.info(message);
    }

    /**
     * Logs a warning message.
     * @param warning The warning message
     */
    public void warning(Component warning) {
        componentLogger.warn(warning);
    }

    /**
     * Logs a warning message.
     * @param warning The warning message
     */
    public void warning(String warning) {
        componentLogger.warn(warning);
    }
}
