package nl.rutgerkok.doughworldgenerator.mapitem;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapId;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BiomeGridUpdaters implements Listener {

    private final MapViewProvider mapViewProvider;

    private final Map<UUID, BiomeGridUpdater> updatersPerPlayer = new HashMap<>();
    private final Plugin plugin;

    public BiomeGridUpdaters(Plugin plugin, MapViewProvider mapViewProvider) {
        this.mapViewProvider = mapViewProvider;
        this.plugin = plugin;
    }

    /**
     * Gets the map view that supports automatic biome grid updates, and responds to player input.
     * @return The map view.
     */
    public MapView getAutoUpdateableMap() {
        MapView map = this.mapViewProvider.getMapView();
        correctMapRenderers(map);
        return map;
    }

    /**
     * Gets the biome grid updater for the given player.
     * @param player The player.
     * @return The biome grid.
     */
    BiomeGrid getBiomeGridForPlayer(Player player) {
        BiomeGridUpdater updater = getOrCreateMapUpdater(player);
        return updater.getBiomeGrid();
    }

    public void registerEvents() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(this.plugin, task -> {
            removeInactiveUpdaters();
        }, 20 * 60, 20 * 60);
    }

    public void stopPendingTasks() {
        for (BiomeGridUpdater updater : updatersPerPlayer.values()) {
            updater.close();
        }
        updatersPerPlayer.clear();
    }

    private void removeInactiveUpdaters() {
        updatersPerPlayer.entrySet().removeIf(entry -> {
            UUID playerUuid = entry.getKey();
            BiomeGridUpdater updater = entry.getValue();
            Player player = plugin.getServer().getPlayer(playerUuid);
            if (player == null || !hasOurMapInHand(player)) {
                updater.close();
                return true;
            }
            return false;
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInput(PlayerInputEvent event) {
        Player player = event.getPlayer();
        if (hasOurMapInHand(player)) {
            BiomeGridUpdater updater = getOrCreateMapUpdater(player);
            updater.handlePlayerInput(event.getInput(), this.plugin);
        }
    }

    private BiomeGridUpdater getOrCreateMapUpdater(Player player) {
        return updatersPerPlayer.computeIfAbsent(player.getUniqueId(),uuid -> new BiomeGridUpdater(player.getLocation()));
    }

    private boolean hasOurMapInHand(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() != Material.FILLED_MAP) {
            return false;
        }
        MapId mapId = itemInHand.getData(DataComponentTypes.MAP_ID);
        if (mapId == null) {
            return false;
        }
        return mapViewProvider.isOurMapId(mapId.id());
    }

    private void correctMapRenderers(MapView map) {
        List<MapRenderer> renderers = map.getRenderers();
        if (renderers.size() == 1 && renderers.getFirst() instanceof DoughMapRenderer) {
            // Already correct
            return;
        }

        // Need to replace renderers
        for (MapRenderer existingRenderer : List.copyOf(renderers)) {
            map.removeRenderer(existingRenderer);
        }
        map.addRenderer(new DoughMapRenderer(this));
    }

}
