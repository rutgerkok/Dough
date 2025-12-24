package nl.rutgerkok.doughworldgenerator.generator;

import nl.rutgerkok.doughworldgenerator.config.WorldConfig;
import nl.rutgerkok.doughworldgenerator.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

final class DensityFunctionWriter {
    private final Path vanillaDatapackPath;

    DensityFunctionWriter(Path vanillaDatapackPath) {
        this.vanillaDatapackPath = vanillaDatapackPath;
    }

    void writeDensityFunction(Path outputFolder, WorldConfig config, String functionName) throws IOException {
        Path inDataPackPath = Path.of("data", "minecraft", "worldgen", "density_function", "overworld", functionName + ".json");
        Path vanillaFile = vanillaDatapackPath.resolve(inDataPackPath);
        Path outputFile = outputFolder.resolve(inDataPackPath);

        // Read and modify file
        Map<String, Object> jsonContents = JsonUtil.readJsonFile(vanillaFile);
        modifyDensityFunction(jsonContents, config);

        // Write modified file
        Files.createDirectories(outputFile.getParent());
        JsonUtil.writeJsonFile(outputFile, jsonContents);
    }

    private void modifyDensityFunction(Map<String, Object> jsonContents, WorldConfig config) throws IOException {
        if ("minecraft:spline".equals(jsonContents.get("type"))) {
            // Found a terrain shaper!
            modifyTerrainShaper(JsonUtil.getMap(jsonContents, "spline"), config);
            return;
        }

        // Else, try to find nested density functions
        for (String key : new String[] { "argument", "argument1", "argument2" }) {
            if (jsonContents.get(key) instanceof Map<?, ?> map) {
                modifyDensityFunction(JsonUtil.castToMap(map), config);
            }
        }
    }

    private void modifyTerrainShaper(Map<String, Object> terrainShaper, WorldConfig config) throws IOException {
        boolean modifyContinentsLocation = "minecraft:overworld/continents".equals(terrainShaper.get("coordinate"));
        boolean modifyErosionLocation = "minecraft:overworld/erosion".equals(terrainShaper.get("coordinate"));
        for (Object point : JsonUtil.getList(terrainShaper, "points")) {
            Map<String, Object> pointMap = JsonUtil.castToMap(point);
            if (modifyContinentsLocation) {
                float existingLocation = JsonUtil.getFloat(pointMap, "location");
                pointMap.put("location", config.continentalnessFormula.evaluate(existingLocation));
            }
            if (modifyErosionLocation) {
                float existingLocation = JsonUtil.getFloat(pointMap, "location");
                pointMap.put("location", config.erosion.evaluate(existingLocation));
            }

            if (pointMap.get("value") instanceof Map<?, ?> map) {
                // Nested value
                modifyTerrainShaper(JsonUtil.castToMap(map), config);
            }
        }
    }
}
