package nl.rutgerkok.doughworldgenerator;

import java.util.Objects;
import java.util.Random;

import org.bukkit.HeightMap;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import nl.rutgerkok.worldgeneratorapi.BasePopulator;

public class DoughChunkGenerator extends ChunkGenerator {

    private final BasePopulator basePopulator;

    public DoughChunkGenerator(BasePopulator basePopulator) {
        this.basePopulator = Objects.requireNonNull(basePopulator, "basePopulator");
    }

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        this.basePopulator.generateNoise(worldInfo, random, chunkX, chunkZ, chunkData);
    }

    @Override
    public int getBaseHeight(WorldInfo worldInfo, Random random, int x, int z, HeightMap heightMap) {
        return this.basePopulator.getBaseHeight(worldInfo, random, x, z, heightMap);
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return true;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false; // We do this ourselves
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }

}
