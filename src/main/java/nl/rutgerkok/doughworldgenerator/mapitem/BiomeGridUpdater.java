package nl.rutgerkok.doughworldgenerator.mapitem;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Input;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.Nullable;

import java.io.Closeable;

public class BiomeGridUpdater implements Closeable {

    private static final int[] SCALES = {1, 2, 4, 8, 16, 32, 64, 128};

    private BiomeGrid biomeGrid;
    private final Location location;
    private int scaleIndex;
    private @Nullable ScheduledTask mapUpdateTask;

    public BiomeGridUpdater(Location location) {
        this.location = location;
        this.biomeGrid = new BiomeGrid(location, 1);
    }

    BiomeGrid getBiomeGrid() {
        return biomeGrid;
    }

    void handlePlayerInput(Input input, Plugin plugin) {
        int scale = SCALES[scaleIndex];
        AsyncScheduler scheduler = plugin.getServer().getAsyncScheduler();

        if (input.isLeft() || input.isRight() || input.isForward() || input.isBackward()) {
            // Move
            if (input.isLeft()) {
                int deltaX = -scale * BiomeGrid.AXIS_LENGTH / 4;
                location.add(deltaX, 0, 0);
            }
            if (input.isRight()) {
                int deltaX = scale * BiomeGrid.AXIS_LENGTH / 4;
                location.add(deltaX, 0, 0);
            }
            if (input.isForward()) {
                int deltaZ = -scale * BiomeGrid.AXIS_LENGTH / 4;
                location.add(0, 0, deltaZ);
            }
            if (input.isBackward()) {
                int deltaZ = scale * BiomeGrid.AXIS_LENGTH / 4;
                location.add(0, 0, deltaZ);
            }
        } else if (input.isJump()) {
            // Zoom in
            scaleIndex = Math.max(0, scaleIndex - 1);
        } else if (input.isSneak()) {
            // Zoom out
            scaleIndex = Math.min(SCALES.length - 1, scaleIndex + 1);
        } else {
            // No relevant input
            return;
        }

        // Update biome grid
        int newScale = SCALES[scaleIndex];
        biomeGrid = new BiomeGrid(location, newScale);

        // Schedule map update
        cancelAnyUpdateTask();
        drawMap(plugin, scheduler);
    }

    void drawMap(Plugin plugin, AsyncScheduler scheduler) {
        scheduler.runNow(plugin, task -> {
            this.mapUpdateTask = task;
            BiomeGridFillTask fillTask = new BiomeGridFillTask(biomeGrid, task);
            fillTask.run();
            this.mapUpdateTask = null;
        });
    }

    @Override
    public void close() {
        cancelAnyUpdateTask();
    }

    private void cancelAnyUpdateTask() {
        // We make a local copy, in case another thread sets it to null in between the null check and the cancel call
        ScheduledTask task = this.mapUpdateTask;
        if (task != null) {
            task.cancel();
            this.mapUpdateTask = null;
        }
    }
}
