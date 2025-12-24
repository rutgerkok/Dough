package nl.rutgerkok.doughworldgenerator.generator;

import nl.rutgerkok.doughworldgenerator.config.Noise;
import nl.rutgerkok.doughworldgenerator.config.WorldConfig;
import nl.rutgerkok.doughworldgenerator.util.JsonUtil;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class NoiseWriter {

    void writeNoiseFiles(Path outputFolder, WorldConfig config) throws IOException {
        Path noisesFolder = outputFolder.resolve(Path.of("data", "minecraft", "worldgen", "noise"));
        writeNoiseFile(noisesFolder, "continentalness", config.continentalnessNoise);
        writeNoiseFile(noisesFolder, "erosion", config.erosionNoise);
        writeNoiseFile(noisesFolder, "weirdness", config.weirdnessNoise);
        writeNoiseFile(noisesFolder, "temperature", config.temperatureNoise);
        writeNoiseFile(noisesFolder, "humidity", config.humidityNoise);
    }

    private void writeNoiseFile(Path noisesFolder, String noiseName, @Nullable Noise noiseValue) throws IOException {
        Path outputFile = noisesFolder.resolve(noiseName + ".json");
        if (noiseValue == null) {
            // Make sure there's no file, so that Minecraft uses defaults
            if (Files.exists(outputFile)) {
                Files.delete(outputFile);
            }
            return;
        }
        Files.createDirectories(outputFile.getParent());
        JsonUtil.writeJsonFile(outputFile, Map.of(
                "amplitudes", noiseValue.amplitudes(),
                "firstOctave", noiseValue.firstOctave()
        ));
    }
}
