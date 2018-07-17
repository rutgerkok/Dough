package nl.rutgerkok.doughworldgenerator.chunkgen;

import java.util.Objects;

import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;

import nl.rutgerkok.doughworldgenerator.PluginConfig;
import nl.rutgerkok.worldgeneratorapi.WorldRef;
import nl.rutgerkok.worldgeneratorapi.property.FloatProperty;
import nl.rutgerkok.worldgeneratorapi.property.Property;

public class OverworldGenSettings {

    private final FloatProperty baseHeight;
    private final FloatProperty seaLevel;
    private final Property<BlockData> stoneBlock;
    private final Property<BlockData> waterBlock;
    private final WorldRef world;
    private final FloatProperty baseSize;
    private final FloatProperty biomeDepthOffset;
    private final FloatProperty biomeDepthWeight;
    private final FloatProperty biomeScaleOffset;
    private final FloatProperty biomeScaleWeight;
    private final FloatProperty coordinateScale;
    private final FloatProperty depthNoiseScaleExponent;
    private final FloatProperty depthNoiseScaleX;
    private final FloatProperty depthNoiseScaleZ;
    private final Property<Long> worldSeed;
    private final FloatProperty heightScale;
    private final FloatProperty heightVariation;
    private final FloatProperty lowerLimitScale;
    private final FloatProperty lowerLimitScaleWeight;
    private final FloatProperty mainNoiseScaleX;
    private final FloatProperty mainNoiseScaleY;
    private final FloatProperty mainNoiseScaleZ;
    private final FloatProperty stretchY;
    private final FloatProperty upperLimitScale;
    private final FloatProperty upperLimitScaleWeight;


    public OverworldGenSettings(PluginConfig config, WorldRef world) {
        this.world = Objects.requireNonNull(world, "world");
        this.baseHeight = config.baseHeight;
        this.seaLevel = config.seaLevel;
        this.stoneBlock = config.stoneBlock;
        this.waterBlock = config.waterBlock;
        this.baseSize = config.baseSize;
        this.biomeDepthOffset = config.biomeDepthOffset;
        this.biomeDepthWeight = config.biomeDepthWeight;
        this.biomeScaleOffset = config.biomeScaleOffset;
        this.biomeScaleWeight = config.biomeScaleWeight;
        this.coordinateScale = config.coordinateScale;
        this.depthNoiseScaleExponent = config.depthNoiseScaleExponent;
        this.depthNoiseScaleX = config.depthNoiseScaleX;
        this.depthNoiseScaleZ = config.depthNoiseScaleZ;
        this.heightScale = config.heightScale;
        this.heightVariation = config.heightVariation;
        this.lowerLimitScale = config.lowerLimitScale;
        this.lowerLimitScaleWeight = config.lowerLimitScaleWeight;
        this.mainNoiseScaleX = config.mainNoiseScaleX;
        this.mainNoiseScaleY = config.mainNoiseScaleY;
        this.mainNoiseScaleZ = config.mainNoiseScaleZ;
        this.stretchY = config.stretchY;
        this.upperLimitScale = config.upperLimitScale;
        this.upperLimitScaleWeight = config.upperLimitScaleWeight;
        this.worldSeed = config.worldSeed;
    }

    public float getBaseHeight(Biome biome) {
        return baseHeight.get(world, biome);
    }

    public double getBaseSize() {
        return baseSize.get(world);
    }

    public float getBiomeDepthOffset() {
        return biomeDepthOffset.get(world);
    }

    public float getBiomeDepthWeight() {
        return biomeDepthWeight.get(world);
    }

    public float getBiomeScaleOffset() {
        return biomeScaleOffset.get(world);
    }

    public float getBiomeScaleWeight() {
        return biomeScaleWeight.get(world);
    }

    public float getCoordinateScale() {
        return coordinateScale.get(world);
    }

    public double getDepthNoiseScaleExponent() {
        return depthNoiseScaleExponent.get(world);
    }

    public double getDepthNoiseScaleX() {
        return depthNoiseScaleX.get(world);
    }

    public double getDepthNoiseScaleZ() {
        return depthNoiseScaleZ.get(world);
    }

    public float getHeightScale() {
        return heightScale.get(world);
    }

    public float getHeightVariation(Biome biome) {
        return heightVariation.get(world, biome);
    }

    public double getLowerLimitScale(Biome biome) {
        // == 512 * TerrainControl's Volatility1
        return lowerLimitScale.get(world, biome);
    }

    public double getLowerLimitScaleWeight(Biome biome) {
        return lowerLimitScaleWeight.get(world, biome);
    }

    public float getMainNoiseScaleX() {
        return mainNoiseScaleX.get(world);
    }

    public float getMainNoiseScaleY() {
        return mainNoiseScaleY.get(world);
    }

    public float getMainNoiseScaleZ() {
        return mainNoiseScaleZ.get(world);
    }

    public int getSeaLevel() {
        return (int) seaLevel.get(world);
    }

    public long getSeed() {
        return worldSeed.get(world);
    }

    public BlockData getStoneBlock() {
        return stoneBlock.get(world);
    }

    public double getStretchY() {
        return stretchY.get(world);
    }

    public double getUpperLimitScale(Biome biome) {
        // == 512 * TerrainControl's Volatility2
        return upperLimitScale.get(world, biome);
    }

    public double getUpperLimitScaleWeight(Biome biome) {
        return upperLimitScaleWeight.get(world, biome);
    }

    public BlockData getWaterBlock() {
        return waterBlock.get(world);
    }
}
