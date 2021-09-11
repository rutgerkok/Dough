package nl.rutgerkok.doughworldgenerator;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import nl.rutgerkok.worldgeneratorapi.WorldRef;
import nl.rutgerkok.worldgeneratorapi.property.FloatProperty;
import nl.rutgerkok.worldgeneratorapi.property.Property;
import nl.rutgerkok.worldgeneratorapi.property.PropertyRegistry;

public final class PluginConfig {

    public final FloatProperty baseHeight;
    public final FloatProperty baseSize;
    public final FloatProperty biomeDepthOffset;
    public final FloatProperty biomeDepthWeight;
    public final FloatProperty biomeScaleOffset;
    public final FloatProperty biomeScaleWeight;
    public final FloatProperty coordinateScale;
    public final FloatProperty depthNoiseScaleExponent;
    public final FloatProperty depthNoiseScaleX;
    public final FloatProperty depthNoiseScaleZ;
    public final FloatProperty heightScale;
    public final FloatProperty heightVariation;
    public final FloatProperty lowerLimitScale;
    public final FloatProperty lowerLimitScaleWeight;
    public final FloatProperty mainNoiseScaleX;
    public final FloatProperty mainNoiseScaleY;
    public final FloatProperty mainNoiseScaleZ;
    public final FloatProperty seaLevel;
    public final Property<BlockData> stoneBlock;
    public final FloatProperty stretchY;
    public final FloatProperty upperLimitScale;
    public final FloatProperty upperLimitScaleWeight;
    public final Property<BlockData> waterBlock;
    public final Property<Long> worldSeed;

    PluginConfig(Plugin plugin, PropertyRegistry registry) {
        baseHeight = registry.getFloat(PropertyRegistry.BASE_HEIGHT, 0.1f);
        baseSize = registry.getFloat(new NamespacedKey(plugin, "base_size"), 8.5f);
        biomeDepthOffset = registry.getFloat(new NamespacedKey(plugin, "biome_depth_offset"), 0);
        biomeDepthWeight = registry.getFloat(new NamespacedKey(plugin, "biome_depth_weight"), 1);
        biomeScaleOffset = registry.getFloat(new NamespacedKey(plugin, "biome_scale_offset"), 0);
        biomeScaleWeight = registry.getFloat(new NamespacedKey(plugin, "biome_scale_weight"), 1);
        coordinateScale = registry.getFloat(new NamespacedKey(plugin, "coordinate_scale"), 684.412f);
        depthNoiseScaleExponent = registry.getFloat(new NamespacedKey(plugin, "depth_noise_scale_exponent"), 0.5f);
        depthNoiseScaleX = registry.getFloat(new NamespacedKey(plugin, "depth_noise_scale_x"), 200);
        depthNoiseScaleZ = registry.getFloat(new NamespacedKey(plugin, "depth_noise_scale_z"), 200);
        waterBlock = registry.getProperty(new NamespacedKey(plugin, "water_block"),
                Material.WATER.createBlockData());
        worldSeed = registry.getProperty(new NamespacedKey(plugin, "world_seed"), -1L);
        heightScale = registry.getFloat(new NamespacedKey(plugin, "height_scale"), 684.412f);
        heightVariation = registry.getFloat(PropertyRegistry.HEIGHT_VARIATION, 0.1f);
        lowerLimitScale = registry.getFloat(new NamespacedKey(plugin, "lower_limit_scale"), 512);
        lowerLimitScaleWeight = registry.getFloat(new NamespacedKey(plugin, "lower_limit_scale_weight"), 0);
        mainNoiseScaleX = registry.getFloat(new NamespacedKey(plugin, "main_noise_scale_x"), 80);
        mainNoiseScaleY = registry.getFloat(new NamespacedKey(plugin, "main_noise_scale_y"), 160);
        mainNoiseScaleZ = registry.getFloat(new NamespacedKey(plugin, "main_noise_scale_z"), 80);
        seaLevel = registry.getFloat(new NamespacedKey(plugin, "sea_level"), 63);
        stoneBlock = registry.getProperty(new NamespacedKey(plugin, "stone_block"), Material.STONE.createBlockData());
        stretchY = registry.getFloat(new NamespacedKey(plugin, "stretch_y"), 12);
        upperLimitScale = registry.getFloat(new NamespacedKey(plugin, "upper_limit_scale"), 512);
        upperLimitScaleWeight = registry.getFloat(new NamespacedKey(plugin, "upper_limit_scale_weight"), 1.2f);

    }

    private void readBiomeSetting(WorldRef world, ConfigurationSection config, FloatProperty property) {
        ConfigurationSection section = config.getConfigurationSection(property.getKey().getKey());
        if (section == null) {
            return;
        }
        double defaultValue = section.getDouble("default", property.get(world));
        property.setWorldDefault(world, (float) defaultValue);
        for (Biome biome : Biome.values()) {
            double value = section.getDouble(biome.name().toLowerCase(Locale.ROOT), property.get(world, biome));
            property.setBiomeInWorldDefault(world, biome, (float) value);
        }
    }

    public void readSettings(WorldRef world, ConfigurationSection config) {
        readBiomeSetting(world, config, baseHeight);
        readWorldSetting(world, config, baseSize);
        readWorldSetting(world, config, biomeDepthOffset);
        readWorldSetting(world, config, biomeDepthWeight);
        readWorldSetting(world, config, biomeScaleOffset);
        readWorldSetting(world, config, biomeScaleWeight);
        readWorldSetting(world, config, coordinateScale);
        readWorldSetting(world, config, depthNoiseScaleExponent);
        readWorldSetting(world, config, depthNoiseScaleX);
        readWorldSetting(world, config, depthNoiseScaleZ);
        readWorldMaterialSetting(world, config, waterBlock);
        readWorldLongSetting(world, config, worldSeed);
        readWorldSetting(world, config, heightScale);
        readBiomeSetting(world, config, heightVariation);
        readBiomeSetting(world, config, lowerLimitScale);
        readBiomeSetting(world, config, lowerLimitScaleWeight);
        readWorldSetting(world, config, mainNoiseScaleX);
        readWorldSetting(world, config, mainNoiseScaleY);
        readWorldSetting(world, config, mainNoiseScaleZ);
        readWorldSetting(world, config, seaLevel);
        readWorldMaterialSetting(world, config, stoneBlock);
        readWorldSetting(world, config, stretchY);
        readBiomeSetting(world, config, upperLimitScale);
        readBiomeSetting(world, config, upperLimitScaleWeight);
    }

    private void readWorldLongSetting(WorldRef world, ConfigurationSection config, Property<Long> property) {
        long value = config.getLong(property.getKey().getKey(), property.get(world));
        property.setWorldDefault(world, value);
    }

    private void readWorldMaterialSetting(WorldRef world, ConfigurationSection config,
            Property<BlockData> property) {
        String value = config.getString(property.getKey().getKey(), property.get(world).getAsString());
        try {
            BlockData material = Bukkit.createBlockData(value);
            property.setWorldDefault(world, material);
        } catch (IllegalArgumentException e) {
            // Ignore
        }
    }

    private void readWorldSetting(WorldRef world, ConfigurationSection config, FloatProperty property) {
        float value = (float) config.getDouble(property.getKey().getKey(), property.get(world));
        property.setWorldDefault(world, value);
    }

    private void writeBiomeSetting(WorldRef world, ConfigurationSection config, FloatProperty property) {
        float defaultValue = property.get(world);
        ConfigurationSection section = config.createSection(property.getKey().getKey());
        section.set("default", Double.valueOf(defaultValue));
        for (Biome biome : Biome.values()) {
            float value = property.get(world, biome);
            if (Math.abs(defaultValue - value) < 0.00001) {
                continue;
            }
            section.set(biome.name().toLowerCase(Locale.ROOT), Double.valueOf(value));
        }
    }

    /**
     * Writes all settings to the given configuration.
     *
     * @param world
     *            The world to write the settings for.
     * @param config
     *            The config to write to.
     */
    public void writeSettings(WorldRef world, ConfigurationSection config) {
        writeBiomeSetting(world, config, baseHeight);
        writeWorldSetting(world, config, baseSize);
        writeWorldSetting(world, config, biomeDepthOffset);
        writeWorldSetting(world, config, biomeDepthWeight);
        writeWorldSetting(world, config, biomeScaleOffset);
        writeWorldSetting(world, config, biomeScaleWeight);
        writeWorldSetting(world, config, coordinateScale);
        writeWorldSetting(world, config, depthNoiseScaleExponent);
        writeWorldSetting(world, config, depthNoiseScaleX);
        writeWorldSetting(world, config, depthNoiseScaleZ);
        writeWorldMaterialSetting(world, config, waterBlock);
        writeWorldLongSetting(world, config, worldSeed);
        writeWorldSetting(world, config, heightScale);
        writeBiomeSetting(world, config, heightVariation);
        writeBiomeSetting(world, config, lowerLimitScale);
        writeBiomeSetting(world, config, lowerLimitScaleWeight);
        writeWorldSetting(world, config, mainNoiseScaleX);
        writeWorldSetting(world, config, mainNoiseScaleY);
        writeWorldSetting(world, config, mainNoiseScaleZ);
        writeWorldSetting(world, config, seaLevel);
        writeWorldMaterialSetting(world, config, stoneBlock);
        writeWorldSetting(world, config, stretchY);
        writeBiomeSetting(world, config, upperLimitScale);
        writeBiomeSetting(world, config, upperLimitScaleWeight);
    }

    private void writeWorldLongSetting(WorldRef world, ConfigurationSection config, Property<Long> property) {
        config.set(property.getKey().getKey(), property.get(world));
    }

    private void writeWorldMaterialSetting(WorldRef world, ConfigurationSection config,
            Property<BlockData> property) {
        config.set(property.getKey().getKey(), property.get(world).getAsString());
    }

    private void writeWorldSetting(WorldRef world, ConfigurationSection config, FloatProperty property) {
        config.set(property.getKey().getKey(), Double.valueOf(property.get(world)));
    }
}
