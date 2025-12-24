package nl.rutgerkok.doughworldgenerator.config;

import nl.rutgerkok.doughworldgenerator.PluginLogger;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

import static net.kyori.adventure.text.Component.text;

public class InvalidConfigException extends Exception {

    private final Path file;
    private final String setting;
    private final @Nullable String value;
    private final int charIndex;

    /**
     * Creates a new exception.
     * @param message The error message.
     * @param file The file where the error occurred.
     *             @param setting The name of the setting that caused the error.
     *                            @param value The invalid value, or null if there is no value stored.
     * @param charIndex The character index where the error occurred, or -1 if unknown.
     */
    public InvalidConfigException(String message, Path file, String setting, @Nullable String value, int charIndex) {
        super(message);

        this.file = file;
        this.setting = setting;
        this.value = value;
        this.charIndex = charIndex;
    }

    public void log(PluginLogger logger) {
        String value = this.value != null ? '"' + this.value + '"' : "<no value specified>";

        logger.warning(text().append(text("Invalid configuration: ")).append(text(getMessage())).build());
        logger.warning(text().append(text("File: ")).append(text(file.toString())).build());
        logger.warning(text().append(text("Setting: ")).append(text(setting)).build());
        logger.warning(text("Value: " + value));
        if (charIndex > 0) {
            // Point to error location
            logger.warning(text("        " + " ".repeat(charIndex - 1) + '^'));
        }
    }
}
