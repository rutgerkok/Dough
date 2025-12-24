package nl.rutgerkok.doughworldgenerator;

import io.papermc.paper.datapack.Datapack;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class DoughMain extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginLogger logger = new PluginLogger(getComponentLogger());
        VanillaDatapackExtractor extractor = new VanillaDatapackExtractor(logger, getVanillaDatapackPath(), getServer().getMinecraftVersion());
        if (!extractor.extractIfNecessary()) {
            setEnabled(false);
            return;
        }

        checkForDatapack();
    }

    private Path getVanillaDatapackPath() {
        return getDataPath().resolve(Constants.VANILLA_DATAPACKS_FOLDER);
    }

    private void checkForDatapack() {
        boolean foundDatapack = false;
        String datapackName = getPluginMeta().getName() + "/provided";
        for (Datapack datapack : getServer().getDatapackManager().getEnabledPacks()) {
            if (datapackName.equals(datapack.getName())) {
                foundDatapack = true;
                break;
            }
        }
        if (!foundDatapack) {
            getLogger().severe("The Dough datapack was not found! Was there an error during startup?");
        }
    }
}
