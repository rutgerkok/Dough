package nl.rutgerkok.doughworldgenerator.config;

import nl.rutgerkok.doughworldgenerator.Constants;
import nl.rutgerkok.doughworldgenerator.PluginLogger;

import java.io.IOException;
import java.nio.file.Path;

public class PluginInternalConfig {

    public String minecraftVersion = "";
    public int mapItemId = -1;

    public PluginInternalConfig() {

    }

    /**
     * Loads the internal config from disk.
     * @param dataDirectory The plugin's data directory. (Not the config file itself.)
     * @param logger Logger to log errors to.
     * @return The loaded config, or defaults if loading failed.
     */
    public static PluginInternalConfig load(Path dataDirectory, PluginLogger logger) {
        Path configPath = dataDirectory.resolve(Constants.INTERNAL_CONFIG_FILE_NAME);
        RawConfig rawConfig = RawConfig.load(configPath);
        PluginInternalConfig internalConfig = new PluginInternalConfig();
        try {
            internalConfig.update(rawConfig);
        } catch (InvalidConfigException e) {
            logger.severe("Failed to load internal config, using defaults", e);
        }
        return internalConfig;
    }

    /**
     * Saves the internal config to disk.
     * @param dataDirectory The plugin's data directory. (Not the config file itself.)
     * @param logger Logger to log errors to.
     */
    public void save(Path dataDirectory, PluginLogger logger) {
        Path configPath = dataDirectory.resolve(Constants.INTERNAL_CONFIG_FILE_NAME);
        RawConfig rawConfig = RawConfig.createEmpty(configPath);
        try {
            update(rawConfig);
            rawConfig.saveToDisk();
        } catch (InvalidConfigException | IOException e) {
            logger.severe("Failed to save internal config", e);
        }
    }

    /**
     * Can be used to read settings from a config, or to write them to a config.
     * @param config If you pass a config with existing values, those values will be read, overwriting the current values.
     *               If you pass a config without values, the current values will be written to it.
     * @throws InvalidConfigException
     *            If the config contains invalid values (for example, a non-integer for an integer setting).
     */
    public void update(RawConfig config) throws InvalidConfigException {
        this.minecraftVersion = config.getString("minecraft_version", minecraftVersion,
                "The Minecraft version of the server. At startup, this version is not yet available, so we read it from here.");
        this.mapItemId = config.getInt("map_item_id", mapItemId,
                "The map item ID (known as damage value before Minecraft 1.13) to use for the biome map item. If set to -1, a new map ID will be assigned automatically, and then this value will be updated.");
    }
}
