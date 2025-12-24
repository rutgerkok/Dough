package nl.rutgerkok.doughworldgenerator.generator;

import nl.rutgerkok.doughworldgenerator.config.Noise;
import nl.rutgerkok.doughworldgenerator.config.WorldConfig;
import nl.rutgerkok.doughworldgenerator.util.JsonUtil;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static nl.rutgerkok.doughworldgenerator.util.JsonUtil.*;

public final class DatapackGenerator {

    public static final List<Integer> FORMAT_VERSION = List.of(94, 1); // Minecraft 1.21.11 uses version 94.1

    private final DensityFunctionWriter densityFunctionWriter;
    private final WorldPresetWriter worldPresetWriter;
    private final NoiseWriter noiseWriter;

    /**
     * Creates a new datapack generator.
     * @param vanillaDatapackPath The folder containing the vanilla world generation datapack files. Must contain
     *                           "data" and "reports" subfolders.
     */
    public DatapackGenerator(Path vanillaDatapackPath) {
        this.densityFunctionWriter = new DensityFunctionWriter(vanillaDatapackPath);
        this.worldPresetWriter = new WorldPresetWriter(vanillaDatapackPath);
        this.noiseWriter = new NoiseWriter();
    }

    public void write(Path outputFolder, WorldConfig config) throws IOException {
        Files.createDirectories(outputFolder);
        writePackMcMeta(outputFolder);

        this.worldPresetWriter.writeWorldPreset(outputFolder, config);
        this.noiseWriter.writeNoiseFiles(outputFolder, config);
        this.densityFunctionWriter.writeDensityFunction(outputFolder, config, "jaggedness");
        this.densityFunctionWriter.writeDensityFunction(outputFolder, config, "factor");
        this.densityFunctionWriter.writeDensityFunction(outputFolder, config, "offset");
    }

    private void writePackMcMeta(Path outputFolder) throws IOException {
        Map<String, Object> root = new HashMap<>();
        root.put("pack", Map.of(
                "min_format", FORMAT_VERSION,
                "max_format", FORMAT_VERSION,
                "description", "Dough World Generator Datapack"
        ));

        // Write as JSON using Google GSON
        JsonUtil.writeJsonFile(outputFolder.resolve("pack.mcmeta"), root);
    }
}
