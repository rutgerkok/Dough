package nl.rutgerkok.doughworldgenerator;

import io.papermc.paper.datapack.Datapack;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class DoughMain extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginLogger logger = new PluginLogger(getComponentLogger());
        VanillaDatapackExtractor extractor = new VanillaDatapackExtractor(logger, getVanillaDatapackPath());
        if (!extractor.extractIfNecessary()) {
            setEnabled(false);
            return;
        }

        checkForDatapack();
    }

    private Path getVanillaDatapackPath() {
        String minecraftVersion = getServer().getMinecraftVersion();
        return getDataPath().resolve("vanilla-datapacks-do-not-edit").resolve(minecraftVersion);
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
            getLogger().severe("The Dough datapack was not found! Did you disable the datapack?");
        }
    }
}
