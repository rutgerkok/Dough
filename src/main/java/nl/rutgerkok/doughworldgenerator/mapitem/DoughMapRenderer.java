package nl.rutgerkok.doughworldgenerator.mapitem;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

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
            // No changes since last render
            return;
        }
        this.lastUpdateId = biomeGrid.lastUpdateId.get();

        for (int x = 0; x < BiomeGrid.AXIS_LENGTH; x++) {
            for (int y = 0; y < BiomeGrid.AXIS_LENGTH; y++) {
                Biome biome = biomeGrid.getBiomeAtPixel(x, y);
                Color color = this.biomeColors.getAwtColor(biome);
                canvas.setPixelColor(x, y, color);
            }
        }
    }
}
