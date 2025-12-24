package nl.rutgerkok.doughworldgenerator;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

public class DoughBootstrap implements PluginBootstrap {


    @Override
    public void bootstrap(BootstrapContext context) {
        Path datapackPath = context.getDataDirectory().resolve("generated-datapack-do-not-edit");

        DatapackGenerator datapackGenerator = new DatapackGenerator(context.getDataDirectory());
        try {
            datapackGenerator.write(datapackPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write datapack", e);
        }

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    try {
                        if (event.registrar().discoverPack(datapackPath, "provided") == null) {
                            throw new RuntimeException("Failed to register generated datapack: unknown error");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to register generated datapack", e);
                    }
                }
        ));
    }


}
