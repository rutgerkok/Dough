package nl.rutgerkok.doughworldgenerator;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import nl.rutgerkok.doughworldgenerator.config.InvalidConfigException;
import nl.rutgerkok.doughworldgenerator.config.WorldConfig;
import nl.rutgerkok.doughworldgenerator.generator.DatapackGenerator;

import java.io.IOException;
import java.nio.file.Path;

@SuppressWarnings("unused") // Referenced in paper-plugin.yml
public class DoughBootstrap implements PluginBootstrap {


    @Override
    public void bootstrap(BootstrapContext context) {
        PluginLogger logger = new PluginLogger(context.getLogger());

        // Load world config
        Path worldConfigFile = context.getDataDirectory().resolve(Constants.WORLD_CONFIG_FILE_NAME);
        WorldConfig worldConfig;
        try {
            worldConfig = WorldConfig.load(worldConfigFile);
        } catch (InvalidConfigException e) {
            e.log(logger);
            return;
        } catch (IOException e) {
            logger.severe("Failed to load world config", e);
            return;
        }

        // Find previously extracted vanilla datapack
        Path datapackPath = context.getDataDirectory().resolve(Constants.GENERATED_DATAPACK_NAME);
        Path vanillaDatapackPath = VanillaDatapackExtractor.getVersionSpecificFolder(context.getDataDirectory().resolve(Constants.VANILLA_DATAPACKS_FOLDER));
        if (vanillaDatapackPath == null) {
            logger.info("No vanilla datapack extracted yet, will do so later. Cannot apply custom world generation settings yet.");
            return; // Vanilla datapack not yet extracted, cannot register our datapack
        }

        // Generate our datapack
        try {
            DatapackGenerator datapackGenerator = new DatapackGenerator(vanillaDatapackPath);
            datapackGenerator.write(datapackPath, worldConfig);
        } catch (IOException e) {
            logger.severe("Failed to generate datapack", e);
            return;
        }

        // Register datapack
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


}
