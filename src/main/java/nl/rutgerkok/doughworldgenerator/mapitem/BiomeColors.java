package nl.rutgerkok.doughworldgenerator.mapitem;

import org.bukkit.block.Biome;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BiomeColors {

    private final Map<Biome, Color> biomeColorMap = new HashMap<>();

    BiomeColors() {
        // Colors from
        // https://twitter.com/henrikkniberg/status/1429700450807341057
        // https://github.com/toolbox4minecraft/amidst/blob/86ffd6004a3ab2a88b9325de420a596665f0df75/biome/test.json

        addBiome(Biome.OCEAN, rgb(0, 0, 112));
        addBiome(Biome.PLAINS, rgb(141, 179, 96));
        addBiome(Biome.DESERT, rgb(250, 148, 24));
        addBiome(Biome.WINDSWEPT_HILLS, rgb(96, 96, 96)); // Original Extreme Hills, later Mountains
        addBiome(Biome.FOREST, rgb(5, 102, 33));
        addBiome(Biome.TAIGA, rgb(11, 2, 89));
        addBiome(Biome.SWAMP, rgb(7, 249, 178));
        addBiome(Biome.MANGROVE_SWAMP, rgb(5, 175, 124));
        addBiome(Biome.RIVER, rgb(0, 0, 255));
        addBiome(Biome.NETHER_WASTES, rgb(255, 0, 0));
        addBiome(Biome.THE_END, rgb(128, 128, 255));
        addBiome(Biome.FROZEN_OCEAN, rgb(112, 112, 214));
        addBiome(Biome.FROZEN_RIVER, rgb(160, 160, 255));
        addBiome(Biome.SNOWY_PLAINS, rgb(255, 255, 255)); // SNOWY_TUNDRA
        // addBiome(Biome.SNOWY_MOUNTAINS, rgb(160, 160, 160));
        addBiome(Biome.MUSHROOM_FIELDS, rgb(255, 0, 255));
        // addBiome(Biome.MUSHROOM_FIELD_SHORE, rgb(160, 0, 255));
        addBiome(Biome.BEACH, rgb(250, 222, 85));
        // addBiome(Biome.DESERT_HILLS, rgb(210, 95, 18));
        // addBiome(Biome.WOODED_HILLS, rgb(34, 85, 28));
        // addBiome(Biome.TAIGA_HILLS, rgb(22, 57, 51));
        // addBiome(Biome.MOUNTAIN_EDGE, rgb(114, 120, 154));
        addBiome(Biome.JUNGLE, rgb(83, 123, 9));
        // addBiome(Biome.JUNGLE_HILLS, rgb(44, 66, 5));
        addBiome(Biome.SPARSE_JUNGLE, rgb(98, 139, 23));
        addBiome(Biome.DEEP_OCEAN, rgb(0, 0, 48));
        addBiome(Biome.STONY_SHORE, rgb(162, 162, 132));
        addBiome(Biome.SNOWY_BEACH, rgb(250, 240, 192));
        addBiome(Biome.BIRCH_FOREST, rgb(48, 116, 68));
        // addBiome(Biome.BIRCH_FOREST_HILLS, rgb(31, 5, 50));
        addBiome(Biome.DARK_FOREST, rgb(64, 81, 26));
        addBiome(Biome.SNOWY_TAIGA, rgb(35, 168, 173));
        // addBiome(Biome.SNOWY_TAIGA_HILLS, rgb(36, 63, 54));
        addBiome(Biome.OLD_GROWTH_PINE_TAIGA, rgb(89, 102, 81)); // Was GIANT_TREE_TAIGA
        // addBiome(Biome.GIANT_TREE_TAIGA_HILLS, rgb(69, 7, 62));
        addBiome(Biome.WINDSWEPT_FOREST, rgb(80, 112, 80));
        addBiome(Biome.SAVANNA, rgb(189, 18, 95));
        addBiome(Biome.SAVANNA_PLATEAU, rgb(167, 157, 100));
        addBiome(Biome.BADLANDS, rgb(217, 69, 21));
        addBiome(Biome.WOODED_BADLANDS, rgb(17, 151, 101)); // Was WOODED_BADLANDS_PLATEAU
        // addBiome(Biome.BADLANDS_PLATEAU, rgb(202, 140, 101));
        addBiome(Biome.SMALL_END_ISLANDS, rgb(128, 128, 255));
        addBiome(Biome.END_MIDLANDS, rgb(128, 128, 255));
        addBiome(Biome.END_HIGHLANDS, rgb(128, 128, 255));
        addBiome(Biome.END_BARRENS, rgb(128, 128, 255));
        addBiome(Biome.WARM_OCEAN, rgb(0, 0, 172));
        addBiome(Biome.LUKEWARM_OCEAN, rgb(0, 0, 144));
        addBiome(Biome.COLD_OCEAN, rgb(32, 32, 112));
        // addBiome(Biome.DEEP_WARM_OCEAN, rgb(0, 0, 80));
        addBiome(Biome.DEEP_LUKEWARM_OCEAN, rgb(0, 0, 64));
        addBiome(Biome.DEEP_COLD_OCEAN, rgb(32, 32, 56));
        addBiome(Biome.DEEP_FROZEN_OCEAN, rgb(64, 64, 144));
        addBiome(Biome.THE_VOID, rgb(0, 0, 0));
        addBiome(Biome.SUNFLOWER_PLAINS, rgb(181, 219, 136));
        // addBiome(Biome.DESERT_LAKES, rgb(255, 188, 64));
        addBiome(Biome.WINDSWEPT_GRAVELLY_HILLS, rgb(136, 136, 136)); // Was GRAVELLY_MOUNTAINS
        addBiome(Biome.FLOWER_FOREST, rgb(45, 142, 73));
        // addBiome(Biome.TAIGA_MOUNTAINS, rgb(51, 142, 19));
        // addBiome(Biome.SWAMP_HILLS, rgb(47, 255, 18));
        addBiome(Biome.ICE_SPIKES, rgb(180, 20, 220));
        // addBiome(Biome.MODIFIED_JUNGLE, rgb(123, 13, 49));
        // addBiome(Biome.MODIFIED_JUNGLE_EDGE, rgb(138, 179, 63));
        addBiome(Biome.OLD_GROWTH_BIRCH_FOREST, rgb(88, 156, 108)); // Was TALL_BIRCH_FOREST
        // addBiome(Biome.TALL_BIRCH_HILLS, rgb(71, 15, 90));
        // addBiome(Biome.DARK_FOREST_HILLS, rgb(104, 121, 66));
        // addBiome(Biome.SNOWY_TAIGA_MOUNTAINS, rgb(89, 125, 114));
        addBiome(Biome.OLD_GROWTH_SPRUCE_TAIGA, rgb(129, 142, 121)); // Was GIANT_SPRUCE_TAIGA
        // addBiome(Biome.GIANT_SPRUCE_TAIGA_HILLS, rgb(109, 119, 102));
        // addBiome(Biome.MODIFIED_GRAVELLY_MOUNTAINS, rgb(120, 52, 120));
        addBiome(Biome.WINDSWEPT_SAVANNA, rgb(229, 218, 135)); // Was SHATTERED_SAVANNA
        // addBiome(Biome.SHATTERED_SAVANNA_PLATEAU, rgb(207, 197, 140));
        addBiome(Biome.ERODED_BADLANDS, rgb(255, 109, 61));
        // addBiome(Biome.MODIFIED_WOODED_BADLANDS_PLATEAU, rgb(216, 191, 141));
        addBiome(Biome.BAMBOO_JUNGLE, rgb(118, 142, 20));
        // addBiome(Biome.BAMBOO_JUNGLE_HILLS, rgb(59, 71, 10));
        addBiome(Biome.SOUL_SAND_VALLEY, rgb(82, 41, 33));
        addBiome(Biome.CRIMSON_FOREST, rgb(221, 8, 8));
        addBiome(Biome.WARPED_FOREST, rgb(73, 144, 123));
        addBiome(Biome.BASALT_DELTAS, rgb(45, 52, 54));
        addBiome(Biome.DRIPSTONE_CAVES, rgb(89, 62, 42));
        addBiome(Biome.LUSH_CAVES, rgb(137, 232, 79));
        addBiome(Biome.DEEP_DARK, rgb(10, 10, 10));
        addBiome(Biome.MEADOW, rgb(20, 94, 97));
        addBiome(Biome.GROVE, rgb(143, 201, 183));
        addBiome(Biome.SNOWY_SLOPES, rgb(205, 205, 229));
        addBiome(Biome.FROZEN_PEAKS, rgb(240, 240, 240));
        addBiome(Biome.JAGGED_PEAKS, rgb(20, 94, 97));
        addBiome(Biome.STONY_PEAKS, rgb(120, 52, 120)); // Copied from MODIFIED_GRAVELLY_MOUNTAINS
    }

    private void addBiome(Biome biome, Color rgb) {
        this.biomeColorMap.put(biome, rgb);
    }

    private Color rgb(int r, int g, int b) {
        return new Color(r, g, b);
    }

    /**
     * Gets the color for the given biome.
     * @param biome The biome.
     * @return The color, or black if unknown.
     */
    public Color getAwtColor(Biome biome) {
        return this.biomeColorMap.getOrDefault(biome, Color.BLACK);
    }

}
