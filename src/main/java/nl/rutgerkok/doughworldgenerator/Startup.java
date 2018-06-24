package nl.rutgerkok.doughworldgenerator;

import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import nl.rutgerkok.doughworldgenerator.chunkgen.ChunkGeneratorOverworld;
import nl.rutgerkok.doughworldgenerator.chunkgen.OverworldGenSettings;
import nl.rutgerkok.worldgeneratorapi.WorldGeneratorApi;
import nl.rutgerkok.worldgeneratorapi.WorldRef;

public class Startup extends JavaPlugin {

    private PluginConfig pluginConfig;
    private WorldGeneratorApi worldGeneratorApi;

    private ChunkGeneratorOverworld createChunkGenerator(World world) {
        WorldRef worldRef = WorldRef.of(world);

        Configuration config = getConfig();
        ConfigurationSection section = config.getConfigurationSection(world.getName());
        if (section == null) {
            section = config.createSection(world.getName());
        }
        pluginConfig.readSettings(worldRef, section);
        pluginConfig.writeSettings(worldRef, config.createSection(world.getName()));

        OverworldGenSettings overworldSettings = new OverworldGenSettings(pluginConfig, worldRef);
        ChunkGeneratorOverworld base = new ChunkGeneratorOverworld(overworldSettings);

        return base;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        WorldRef world = WorldRef.ofName(worldName);
        return worldGeneratorApi.buildTerrainGenerator(world, this::createChunkGenerator).create();
    }

    @Override
    public void onEnable() {
        worldGeneratorApi = WorldGeneratorApi.getInstance(this, 0, 1);
        pluginConfig = new PluginConfig(this, worldGeneratorApi.getPropertyRegistry());

        // Save config after all worlds are enabled
        getServer().getScheduler().runTask(this, this::saveConfig);
    }
}
