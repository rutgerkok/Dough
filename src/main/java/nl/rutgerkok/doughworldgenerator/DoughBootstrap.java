package nl.rutgerkok.doughworldgenerator;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import nl.rutgerkok.doughworldgenerator.config.InvalidConfigException;
import nl.rutgerkok.doughworldgenerator.config.PluginInternalConfig;
import nl.rutgerkok.doughworldgenerator.config.WorldConfig;
import nl.rutgerkok.doughworldgenerator.generator.DatapackGenerator;
import nl.rutgerkok.doughworldgenerator.mapitem.MapCommand;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("unused") // Referenced in paper-plugin.yml
public class DoughBootstrap implements PluginBootstrap {


    @Override
    public void bootstrap(BootstrapContext context) {
        String minecraftVersion = SharedConstants.getCurrentVersion().name();

        PluginLogger logger = new PluginLogger(context.getLogger());
        PluginInternalConfig internalConfig = PluginInternalConfig.load(context.getDataDirectory(), logger);

        // Load world config
        WorldConfig worldConfig = getWorldConfig(context, logger);
        if (worldConfig == null) {
            return;
        }

        // Find previously extracted vanilla datapack
        Path vanillaDatapackPath = getVanillaDatapackPath(context, minecraftVersion);
        if (vanillaDatapackPath == null || internalConfig.levelDatFile.isEmpty()) {
            logger.info("No vanilla datapack extracted yet, will do so later. Cannot apply custom world generation settings yet.");
            return; // Vanilla datapack not yet extracted, cannot register our datapack. Needs to be extracted in DoughMain, at this stage Minecraft would crash
        }

        // Generate our datapack
        Path datapackPath = context.getDataDirectory().resolve(Constants.GENERATED_DATAPACK_NAME);
        try {
            DatapackGenerator datapackGenerator = new DatapackGenerator(vanillaDatapackPath);
            datapackGenerator.write(datapackPath, Path.of(internalConfig.levelDatFile), worldConfig);
        } catch (IOException e) {
            logger.severe("Failed to generate datapack", e);
            return;
        }

        // Register datapack
        registerDatapack(context, datapackPath, logger);

        // Register commands
        registerCommands(context);
    }

    private static @Nullable Path getVanillaDatapackPath(BootstrapContext context, String minecraftVersion) {
        Path vanillaDatapackPath = context.getDataDirectory().resolve(Constants.VANILLA_DATAPACKS_FOLDER).resolve(minecraftVersion);
        if (!Files.isDirectory(vanillaDatapackPath)) {
            return null; // Not found
        }
        return vanillaDatapackPath;
    }

    private void registerCommands(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(
                event -> {
                    LiteralCommandNode<CommandSourceStack> doughCommand = Commands.literal("dough")
                            .then(MapCommand.command())
                            .build();
                    event.registrar().register(context.getPluginMeta(), doughCommand,
                            "Commands for Dough World Generator plugin", List.of());
                }
        ));
    }

    private static void registerDatapack(BootstrapContext context, Path datapackPath, PluginLogger logger) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    try {
                        if (event.registrar().discoverPack(datapackPath, "provided") == null) {
                            logger.severe("Failed to register generated datapack: unknown error", new RuntimeException());
                        }
                    } catch (IOException e) {
                        logger.severe("Failed to register generated datapack", e);
                    }
                }
        ));
    }

    private static @Nullable WorldConfig getWorldConfig(BootstrapContext context, PluginLogger logger) {
        Path worldConfigFile = context.getDataDirectory().resolve(Constants.WORLD_CONFIG_FILE_NAME);
        WorldConfig worldConfig;
        try {
            worldConfig = WorldConfig.load(worldConfigFile);
        } catch (InvalidConfigException e) {
            e.log(logger);
            return null;
        } catch (IOException e) {
            logger.severe("Failed to load world config", e);
            return null;
        }
        return worldConfig;
    }


}
