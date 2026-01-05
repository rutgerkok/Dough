package nl.rutgerkok.doughworldgenerator.config;

import io.papermc.paper.plugin.configuration.PluginMeta;

public final class CacheKey {

    /**
     * Generates a cache key based on the world config, plugin version and Minecraft version.
     * @param pluginMeta The plugin meta, used to get the plugin version.
     * @param worldConfig The world config.
     * @param minecraftVersion The Minecraft version.
     * @return The generated cache key.
     */
    public static String generate(PluginMeta pluginMeta, WorldConfig worldConfig, String minecraftVersion) {
        return worldConfig.cacheKey + "-" + pluginMeta.getVersion() + "-" + minecraftVersion;
    }
}
