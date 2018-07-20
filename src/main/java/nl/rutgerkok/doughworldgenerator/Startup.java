package nl.rutgerkok.doughworldgenerator;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import nl.rutgerkok.doughworldgenerator.chunkgen.ChunkGeneratorOverworld;
import nl.rutgerkok.doughworldgenerator.chunkgen.OverworldGenSettings;
import nl.rutgerkok.worldgeneratorapi.WorldGenerator;
import nl.rutgerkok.worldgeneratorapi.WorldGeneratorApi;
import nl.rutgerkok.worldgeneratorapi.WorldRef;

public class Startup extends JavaPlugin {

    private PluginConfig pluginConfig;
    private WorldGeneratorApi worldGeneratorApi;

    private void createChunkGenerator(WorldGenerator worldGenerator) {
        WorldRef worldRef = worldGenerator.getWorldRef();
        File file = new File(getDataFolder(), worldRef.getName() + ".yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        pluginConfig.readSettings(worldRef, config);

        config.getKeys(false).forEach(key -> config.set(key, null));
        pluginConfig.writeSettings(worldRef, config);
        try {
            config.save(file);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save configuration of world \"" + worldRef.getName() + "\"", e);
        }

        OverworldGenSettings overworldSettings = new OverworldGenSettings(pluginConfig, worldRef);
        worldGenerator.setBaseChunkGenerator(new ChunkGeneratorOverworld(overworldSettings));
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        WorldRef world = WorldRef.ofName(worldName);
        return worldGeneratorApi.createCustomGenerator(world, this::createChunkGenerator);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (args[0].equalsIgnoreCase("import")) {
            String remaining = String.join(" ", Arrays.asList(args).subList(1, args.length));
            LocalTime time = LocalTime.now();
            File out = new File(getDataFolder(), "export-" + time.getHour() + "-" + time.getMinute() + ".yml");
            try {
                new ImporterForCustomized(out).convert(remaining);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Error. " + e.getMessage());
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "Saved settings to " + out);
            return true;
        }
        return false;
    }

    @Override
    public void onEnable() {
        worldGeneratorApi = WorldGeneratorApi.getInstance(this, 0, 2);
        pluginConfig = new PluginConfig(this, worldGeneratorApi.getPropertyRegistry());
    }
}
