package nl.rutgerkok.doughworldgenerator.mapitem;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapId;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import nl.rutgerkok.doughworldgenerator.Constants;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;


public class BiomeGridUpdaters implements Listener {

    private final MapViewProvider mapViewProvider;

    private final Map<UUID, BiomeGridUpdater> updatersPerPlayer = new HashMap<>();
    private final Plugin plugin;

    public BiomeGridUpdaters(Plugin plugin, MapViewProvider mapViewProvider) {
        this.mapViewProvider = mapViewProvider;
        this.plugin = plugin;
    }

    /**
     * Deletes the map item from the player's hands, if it is our map. Useful if the player somehow obtained a map,
     * but lacks permission to use it.
     *
     * @param player The player.
     */
    private void deleteMapItem(Player player) {
        PlayerInventory inventory = player.getInventory();
        if (isOurMap(inventory.getItemInMainHand())) {
            inventory.setItemInMainHand(null);
        }
        if (isOurMap(inventory.getItemInOffHand())) {
            inventory.setItemInOffHand(null);
        }
    }

    /**
     * Gets the map view that supports automatic biome grid updates, and responds to player input.
     *
     * @return The map view.
     */
    public MapView getAutoUpdateableMap() {
        return this.mapViewProvider.getMapView();
    }

    /**
     * Gets the biome grid updater for the given player.
     *
     * @param player The player.
     * @return The biome grid.
     */
    BiomeGrid getBiomeGridForPlayer(Player player) {
        BiomeGridUpdater updater = getOrCreateMapUpdater(player);
        return updater.getBiomeGrid();
    }

    private BiomeGridUpdater getOrCreateMapUpdater(Player player) {
        return updatersPerPlayer.computeIfAbsent(player.getUniqueId(), uuid -> new BiomeGridUpdater(player.getLocation()));
    }

    private boolean hasOurMapInHand(Player player) {
        PlayerInventory inventory = player.getInventory();
        if (isOurMap(inventory.getItemInMainHand())) {
            return true;
        }
        return isOurMap(inventory.getItemInOffHand());
    }

    private void initializeMapRenderers(MapView map) {
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

    private boolean isOurMap(@Nullable ItemStack stack) {
        if (stack == null || stack.getType() != Material.FILLED_MAP) {
            return false;
        }
        MapId mapId = stack.getData(DataComponentTypes.MAP_ID);
        if (mapId == null) {
            return false;
        }
        return mapViewProvider.isOurMapId(mapId.id());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerArmSwing(PlayerArmSwingEvent event) {
        // Handles left clicks, more reliable than PlayerInteractEvent for left clicks
        // (PlayerInteractEvent only fires if the client thinks there will be a result of the interaction)
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        if (hasOurMapInHand(player)) {
            event.setCancelled(true);
            Location mapCenter = getBiomeGridForPlayer(player).getCenterLocation();
            player.sendActionBar(text("Teleporting to map center...", Constants.SUCCESS_COLOR));
            World world = mapCenter.getWorld();
            mapCenter.getWorld().getChunkAtAsync(mapCenter, true).thenAccept(chunk -> {
                Block block = world.getHighestBlockAt(mapCenter, HeightMap.MOTION_BLOCKING);
                Location targetLocation = block.getLocation().add(0.5, 1, 0.5);
                targetLocation.setYaw(180); // Face north
                player.teleportAsync(targetLocation).thenRun(() ->
                        player.sendMessage(text("Teleported to highest block at map center.", Constants.SUCCESS_COLOR)));
            });
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHeldItemChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (!isOurMap(newItem)) {
            return;
        }

        if (!player.hasPermission("dough.map")) {
            deleteMapItem(player);
            return;
        }

        // Center map at player's location when switching to our map
        // (also forces a map redraw, which might be needed)
        BiomeGridUpdater updater = getOrCreateMapUpdater(player);
        updater.centerMapAt(player.getLocation(), plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInput(PlayerInputEvent event) {
        // Detects when the player uses the map controls (left, right, forward, backward, jump, sneak)
        Player player = event.getPlayer();
        if (hasOurMapInHand(player)) {
            if (!player.hasPermission("dough.map")) {
                deleteMapItem(player);
                return;
            }

            BiomeGridUpdater updater = getOrCreateMapUpdater(player);
            updater.handlePlayerKeyPress(event.getInput(), this.plugin);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!hasOurMapInHand(player)) {
            return;
        }

        if (!player.hasPermission("dough.map")) {
            deleteMapItem(player);
            return;
        }

        // Center map at player's location when logging in, and already holding our map
        // (also forces a map redraw, which might be needed)
        BiomeGridUpdater updater = getOrCreateMapUpdater(player);
        updater.centerMapAt(player.getLocation(), plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (hasOurMapInHand(player)) {
            if (!player.isGliding()) {
                // Prevent horizontal movement while holding the map (and being on the ground)
                Location from = event.getFrom();
                Location to = event.getTo();
                to.setX(from.getX());
                if (player.isFlying()) {
                    // When performing creative flight, also prevent vertical movement
                    to.setY(from.getY());
                }
                to.setZ(from.getZ());
                event.setTo(to);
            }
        }
    }

    @EventHandler
    public void onServerStart(ServerLoadEvent event) {
        // Ensure correct renderers on our map (needs to wait until worlds are loaded)
        MapView map = getAutoUpdateableMap();
        initializeMapRenderers(map);
    }

    /**
     * Registers events and the cleanup task.
     */
    public void register() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(this.plugin, task ->
                removeInactiveUpdaters(), 20 * 60, 20 * 60);
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

    public void stopPendingTasks() {
        for (BiomeGridUpdater updater : updatersPerPlayer.values()) {
            updater.close();
        }
        updatersPerPlayer.clear();
    }
}


