package nl.rutgerkok.doughworldgenerator.mapitem;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.map.*;

import java.awt.*;

public class DoughMapRenderer extends MapRenderer {

    private final BiomeColors biomeColors = new BiomeColors();
    private final BiomeGridUpdaters biomeGridUpdaters;

    private long lastUpdateId = -1;

    DoughMapRenderer(BiomeGridUpdaters biomeGridUpdaters) {
        super(true);
        this.biomeGridUpdaters = biomeGridUpdaters;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        BiomeGrid biomeGrid = this.biomeGridUpdaters.getBiomeGridForPlayer(player);
        if (this.lastUpdateId == biomeGrid.lastUpdateId.get()) {
            // No changes since last render, just re-render player
            renderPlayer(canvas, biomeGrid, player);
            return;
        }
        this.lastUpdateId = biomeGrid.lastUpdateId.get();

        renderBiomes(canvas, biomeGrid);
        renderPlayer(canvas, biomeGrid, player);
    }

    private void renderBiomes(MapCanvas canvas, BiomeGrid biomeGrid) {
        for (int x = 0; x < BiomeGrid.AXIS_LENGTH; x++) {
            for (int y = 0; y < BiomeGrid.AXIS_LENGTH; y++) {
                Biome biome = biomeGrid.getBiomeAtPixel(x, y);
                Color color = this.biomeColors.getAwtColor(biome);
                canvas.setPixelColor(x, y, color);
            }
        }
    }

    private static void renderPlayer(MapCanvas canvas, BiomeGrid biomeGrid, Player player) {
        MapCursorCollection cursors = new MapCursorCollection();

        // Add new player cursor
        Location playerLocation = player.getLocation();
        BiomeGrid.MarkerCoords coords = biomeGrid.toMarkerCoords(playerLocation);
        if (coords.inMap()) {
            float normalizedYaw = playerLocation.getYaw();
            if (normalizedYaw < 0) {
                normalizedYaw += 360;
            }
            byte direction = (byte) ((byte) ((normalizedYaw + 11.25) / 22.5) & 0xf);
            MapCursor cursor = new MapCursor((byte) coords.x(), (byte) coords.z(), direction, MapCursor.Type.PLAYER, true);
            cursors.addCursor(cursor);
        } else {
            // Player is outside the map area - different cursor
            MapCursor cursor = new MapCursor((byte) coords.x(), (byte) coords.z(), (byte) 0, MapCursor.Type.PLAYER_OFF_MAP, true);
            cursors.addCursor(cursor);
        }

        canvas.setCursors(cursors);
    }
}
