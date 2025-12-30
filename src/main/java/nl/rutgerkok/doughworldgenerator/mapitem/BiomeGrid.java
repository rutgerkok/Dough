package nl.rutgerkok.doughworldgenerator.mapitem;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicLong;

final class BiomeGrid {

    /**
     * Coordinates of a pixel in the biome grid, along with whether the
     * pixel is inside the map area. If the pixel is outside the map area,
     * the coordinates will be clamped to the nearest edge.
     * @param x The x coordinate of the marker (-128-127). Will be clamped to map bounds.
     * @param z The z coordinate of the marker (-128-127). Will be clamped to map bounds.
     * @param inMap Whether the pixel is  inside the map area.
     */
    record MarkerCoords(int x, int z, boolean inMap) {}

    /**
     * Matches the length and width of a Minecraft map in pixels.
     */
    static final int AXIS_LENGTH = 128;

    private final @Nullable Biome[] biomes = new Biome[AXIS_LENGTH * AXIS_LENGTH];

    private final int scaleFactor;
    private final Location centerLocation;

    /**
     * The ID of the last update to this biome grid. This is incremented each
     * time a biome is changed. We start at a random value to avoid clients
     * thinking nothing has changed when a new grid is created.
     */
    final AtomicLong lastUpdateId = new AtomicLong((long) (Math.random() * 100000));

    public BiomeGrid(Location location, int scaleFactor) {
        this.centerLocation = location.clone();
        if (this.centerLocation.getWorld() == null) {
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

    public @Nullable Biome getBiomeAtPixel(int x, int z) {
        return biomes[toIndex(x, z)];
    }

    /**
     * Gets the center location of this biome grid.
     * @return The center location.
     */
    public Location getCenterLocation() {
        return centerLocation;
    }

    /**
     * Sets the biome at the given pixel.
     * @param x The x coordinate of the pixel (0-{@value #AXIS_LENGTH}-1)
     * @param z The z coordinate of the pixel (0-{@value #AXIS_LENGTH}-1)
     * @param biome The biome.
     */
    public void setBiomeAtPixel(int x, int z, @Nullable Biome biome) {
        lastUpdateId.incrementAndGet();
        biomes[toIndex(x, z)] = biome;
    }

    /**
     * Gets the start location of this biome grid. This is the location that
     * corresponds to the pixel at (0, 0).
     *
     * @return The start location.
     */
    public Location getStartLocation() {
        Location startLocation = centerLocation.clone();
        int offset = (AXIS_LENGTH / 2) * scaleFactor;
        startLocation.add(-offset, 0, -offset);
        return startLocation;
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

    public MarkerCoords toMarkerCoords(Location location) {
        double mapStartWorldCoordsX = centerLocation.getX() - (AXIS_LENGTH / 2.0) * scaleFactor;
        double mapStartWorldCoordsZ = centerLocation.getZ() - (AXIS_LENGTH / 2.0) * scaleFactor;

        double pixelX = (location.getX() - mapStartWorldCoordsX) / scaleFactor;
        double pixelZ = (location.getZ() - mapStartWorldCoordsZ) / scaleFactor;
        boolean inMap = pixelX >= 0 && pixelX < AXIS_LENGTH && pixelZ >= 0 && pixelZ < AXIS_LENGTH;

        // Clamp to map bounds
        if (pixelX < 0) {
            pixelX = 0;
        } else if (pixelX >= AXIS_LENGTH) {
            pixelX = AXIS_LENGTH - 1;
        }
        if (pixelZ < 0) {
            pixelZ = 0;
        } else if (pixelZ >= AXIS_LENGTH) {
            pixelZ = AXIS_LENGTH - 1;
        }

        // Markers don't use the pixel coords from 0 to 127, but from -128 to 127
        double markerX = (pixelX * 2) - AXIS_LENGTH;
        double markerZ = (pixelZ * 2) - AXIS_LENGTH;

        return new MarkerCoords((int) Math.round(markerX), (int) Math.round(markerZ), inMap);
    }
}
