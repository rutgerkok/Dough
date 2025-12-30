package nl.rutgerkok.doughworldgenerator;

import io.papermc.paper.datapack.Datapack;
import nl.rutgerkok.doughworldgenerator.config.PluginInternalConfig;
import nl.rutgerkok.doughworldgenerator.mapitem.BiomeGridUpdaters;
import nl.rutgerkok.doughworldgenerator.mapitem.MapViewProvider;
import org.bukkit.Server;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;

import java.nio.file.Path;

public class DoughMain extends JavaPlugin implements MapViewProvider {

    // Fields set during onEnable
    @UnknownNullability
    private PluginInternalConfig internalConfig;
    @UnknownNullability
    private PluginLogger logger;
    @UnknownNullability
    private BiomeGridUpdaters biomeGridUpdaters;

    @Override
    public MapView getMapView() {
        int mapId = this.internalConfig.mapItemId;
        if (mapId < 0) {
            // No map created yet, create a new one
            return createAndStoreMapView();
        } else {
            MapView mapView = getServer().getMap(mapId);
            if (mapView == null) {
                // Map was deleted, create a new one
                return createAndStoreMapView();
            }
            return mapView;
        }
    }

    /**
     * Gets the biome grid updaters.
     * @return The biome grid updaters.
     */
    public MapView getAutoUpdateableMap() {
        if (this.biomeGridUpdaters == null) {
            throw new IllegalStateException("Plugin not yet enabled");
        }
        return this.biomeGridUpdaters.getAutoUpdateableMap();
    }

    @Override
    public boolean isOurMapId(int id) {
        return id == this.internalConfig.mapItemId;
    }

    /**
     * Creates a new map view, saves its ID in the config, and returns it.
     * @return The new map view.
     */
    private MapView createAndStoreMapView() {
        Server server = getServer();
        MapView newMapView = server.createMap(server.getWorlds().getFirst());
        this.internalConfig.mapItemId = newMapView.getId();
        saveInternalConfig();
        return newMapView;
    }

    @Override
    public void onEnable() {
        this.logger = new PluginLogger(getComponentLogger());

        // Read the config, update Minecraft version if necessary
        this.internalConfig = PluginInternalConfig.load(getDataPath(), logger);
        this.internalConfig.minecraftVersion = getServer().getMinecraftVersion();
        saveInternalConfig();

        VanillaDatapackExtractor extractor = new VanillaDatapackExtractor(logger, getVanillaDatapackPath());
        if (!extractor.extractIfNecessary()) {
            setEnabled(false);
            return;
        }

        checkForDatapack();

        this.biomeGridUpdaters = new BiomeGridUpdaters(this, this);
        this.biomeGridUpdaters.registerEvents();
    }

    @Override
    public void onDisable() {
        if (this.biomeGridUpdaters != null) {
            this.biomeGridUpdaters.stopPendingTasks();
        }
    }

    private void saveInternalConfig() {
        this.internalConfig.save(getDataPath(), logger);
    }

    private Path getVanillaDatapackPath() {
        return getDataPath().resolve(Constants.VANILLA_DATAPACKS_FOLDER).resolve(this.internalConfig.minecraftVersion);
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
