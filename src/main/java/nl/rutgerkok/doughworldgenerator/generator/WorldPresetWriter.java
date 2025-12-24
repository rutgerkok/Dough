package nl.rutgerkok.doughworldgenerator.generator;

import nl.rutgerkok.doughworldgenerator.config.Formula;
import nl.rutgerkok.doughworldgenerator.config.WorldConfig;
import nl.rutgerkok.doughworldgenerator.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static nl.rutgerkok.doughworldgenerator.util.JsonUtil.*;
import static nl.rutgerkok.doughworldgenerator.util.JsonUtil.readJsonFile;
import static nl.rutgerkok.doughworldgenerator.util.JsonUtil.writeJsonFile;

final class WorldPresetWriter {
    private final Path vanillaDatapackPath;

    WorldPresetWriter(Path vanillaDatapackPath) {
        this.vanillaDatapackPath = vanillaDatapackPath;
    }

    void writeWorldPreset(Path outputFolder, WorldConfig config) throws IOException {
        Path parametersFile = this.vanillaDatapackPath.resolve(Path.of("reports", "biome_parameters", "minecraft", "overworld.json"));
        Path inDatapackPath = Path.of("data", "minecraft", "worldgen", "world_preset", "normal.json");
        Path vanillaFile = this.vanillaDatapackPath.resolve(inDatapackPath);
        Path outputFile = outputFolder.resolve(inDatapackPath);

        // Read in the vanilla overworld generator
        Map<String, Object> contents = readJsonFile(vanillaFile);
        Map<String, Object> overworldGenerator = castToMap(traverseToKey(contents, "dimensions", "minecraft:overworld", "generator"));

        // Read in the reported biome parameters, to copy them to the new biome source
        Map<String, Object> reportedParameters = readJsonFile(parametersFile);
        List<Object> biomes = JsonUtil.getList(reportedParameters, "biomes");

        // Modify the biome source
        for (Object biomeObject : biomes) {
            Map<String, Object> biome = castToMap(biomeObject);
            Map<String, Object> parameters = castToMap(biome.get("parameters"));

            modifyBiomeParameters(parameters, "continentalness", config.continentalnessFormula);
            modifyBiomeParameters(parameters, "erosion", config.erosion);
            modifyBiomeParameters(parameters, "temperature", config.temperature);
            modifyBiomeParameters(parameters, "humidity", config.humidity);
            modifyBiomeParameters(parameters, "weirdness", config.weirdness);
        }

        // Insert the modified biome source back into the overworld generator
        overworldGenerator.put("biome_source", Map.of(
                "type", "minecraft:multi_noise",
                "biomes", biomes
        ));

        // Now write the modified file
        Files.createDirectories(outputFile.getParent());
        writeJsonFile(outputFile, contents);
    }

    private void modifyBiomeParameters(Map<String, Object> map, String noiseName, Formula formula) throws IOException {
        Object noiseValues = map.get(noiseName);
        if (noiseValues instanceof Number number) {
            // Single value instead of list
            map.put(noiseName, formula.evaluate(number.floatValue()));
            return;
        }

        // Assume the list case
        List<Object> noiseList = JsonUtil.castToList(noiseValues);
        for (int i = 0; i < noiseList.size(); i++) {
            Object obj = noiseList.get(i);
            if (!(obj instanceof Number number)) {
                throw new IOException("Expected number in biome parameter '" + noiseName + "', but found: " + obj);
            }
            noiseList.set(i, formula.evaluate(number.floatValue()));
        }
    }
}
