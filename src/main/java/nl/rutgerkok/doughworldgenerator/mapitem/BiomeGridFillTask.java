package nl.rutgerkok.doughworldgenerator.mapitem;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;

/**
 * Task that fills a BiomeGrid with biome data. Designed to be run on a worker thread.
 */
final class BiomeGridFillTask implements Runnable {

    private final BiomeGrid biomeGrid;
    private final BiomeProvider biomeProvider;
    private final World world;
    private final ScheduledTask task;

    /**
     * Creates a new biome grid fill task.
     * @param biomeGrid The biome grid to fill.
     * @param task The scheduled task, used to check for cancellation. (If the task is
     *             canceled, the run method will return early without finishing the grid.)
     */
    BiomeGridFillTask(BiomeGrid biomeGrid, ScheduledTask task) {
        this.task = task;
        this.biomeGrid = biomeGrid;
        this.world = biomeGrid.getStartLocation().getWorld();
        BiomeProvider provider = world.getBiomeProvider();
        if (provider == null) {
            provider = world.vanillaBiomeProvider();
        }
        this.biomeProvider = provider;
    }

    @Override
    public void run() {
        Location startLocation = biomeGrid.getStartLocation();
        int scaleFactor = biomeGrid.getScaleFactor();
        int blockY = startLocation.getBlockY();

        for (int z = 0; z < BiomeGrid.AXIS_LENGTH; z++) {
            if (task.isCancelled()) {
                return;
            }
            for (int x = 0; x < BiomeGrid.AXIS_LENGTH; x++) {
                int blockX = startLocation.getBlockX() + x * scaleFactor;
                int blockZ = startLocation.getBlockZ() + z * scaleFactor;
                Biome biome = this.biomeProvider.getBiome(this.world, blockX, blockY, blockZ);
                biomeGrid.setBiomeAtPixel(x, z, biome);
            }
        }
    }
}
