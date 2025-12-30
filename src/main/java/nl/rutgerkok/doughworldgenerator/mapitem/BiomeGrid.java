package nl.rutgerkok.doughworldgenerator.mapitem;

import org.bukkit.Location;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

final class BiomeGrid {

    /**
     * Matches the length and width of a Minecraft map in pixels.
     */
    static final int AXIS_LENGTH = 128;

    private final Biome[] biomes = new Biome[AXIS_LENGTH * AXIS_LENGTH];

    private final int scaleFactor;
    private final Location startLocation;

    /**
     * The ID of the last update to this biome grid. This is incremented each
     * time a biome is changed. We start at a random value to avoid clients
     * thinking nothing has changed when a new grid is created.
     */
    final AtomicLong lastUpdateId = new AtomicLong((long) (Math.random() * 100000));

    public BiomeGrid(Location location, int scaleFactor) {
        this.startLocation = location.clone();
        if (this.startLocation.getWorld() == null) {
            throw new IllegalArgumentException("Location must have a world");
        }
        if (scaleFactor < 1) {
            throw new IllegalArgumentException("Scale factor must be positive, got: " + scaleFactor);
        }
        this.scaleFactor = scaleFactor;
    }

    private static int toIndex(int x, int z) {
        if (x < 0 || x >= AXIS_LENGTH || z < 0 || z >= AXIS_LENGTH) {
            throw new IllegalArgumentException("Coordinates out of bounds: " + x + ", " + z);
        }
        return x + z * AXIS_LENGTH;
    }

    public Biome getBiomeAtPixel(int x, int z) {
        return biomes[toIndex(x, z)];
    }

    /**
     * Sets the biome at the given pixel.
     * @param x The x coordinate of the pixel (0-{@value #AXIS_LENGTH}-1)
     * @param z The z coordinate of the pixel (0-{@value #AXIS_LENGTH}-1)
     * @param biome The biome.
     */
    public void setBiomeAtPixel(int x, int z, Biome biome) {
       lastUpdateId.incrementAndGet();
        biomes[toIndex(x, z)] = biome;
    }

    /**
     * Fills the entire biome grid with the given biome.
     * @param biome The biome.
     */
    public void fill(Biome biome) {
        Arrays.fill(this.biomes, biome);
        lastUpdateId.incrementAndGet();
    }

    /**
     * Gets the start location of this biome grid. This is the location that
     * corresponds to the pixel at (0, 0).
     *
     * @return The start location.
     */
    public Location getStartLocation() {
        return startLocation.clone();
    }

    /**
     * Gets the scale factor of this biome grid. A scale factor of 1 means each
     * pixel represents 1x1 blocks, a scale factor of 2 means each pixel
     * represents 2x2 blocks, and so on.
     *
     * @return The scale factor.
     */
    public int getScaleFactor() {
        return scaleFactor;
    }

}
