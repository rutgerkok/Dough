package nl.rutgerkok.doughworldgenerator.generator;

import net.minecraft.nbt.*;
import nl.rutgerkok.doughworldgenerator.config.Formula;
import nl.rutgerkok.doughworldgenerator.config.WorldConfig;
import nl.rutgerkok.doughworldgenerator.util.JsonUtil;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nl.rutgerkok.doughworldgenerator.util.JsonUtil.*;
import static nl.rutgerkok.doughworldgenerator.util.JsonUtil.readJsonFile;
import static nl.rutgerkok.doughworldgenerator.util.JsonUtil.writeJsonFile;

final class WorldPresetWriter {
    private final Path vanillaDatapackPath;

    WorldPresetWriter(Path vanillaDatapackPath) {
        this.vanillaDatapackPath = vanillaDatapackPath;
    }

    void writeWorldPreset(Path outputFolder, Path levelDatFile, WorldConfig config) throws IOException {
        Path inDatapackPath = Path.of("data", "minecraft", "worldgen", "world_preset", "normal.json");
        Path vanillaFile = this.vanillaDatapackPath.resolve(inDatapackPath);
        Path outputFile = outputFolder.resolve(inDatapackPath);

        // Read in the vanilla overworld generator
        Map<String, Object> contents = readJsonFile(vanillaFile);
        Map<String, Object> overworldGenerator = castToMap(traverseToKey(contents, "dimensions", "minecraft:overworld", "generator"));

        List<Object> biomes = assembleBiomeList(config);

        // Insert the modified biome source back into the overworld generator
        overworldGenerator.put("biome_source", Map.of(
                "type", "minecraft:multi_noise",
                "biomes", biomes
        ));

        // Now write the modified file
        Files.createDirectories(outputFile.getParent());
        writeJsonFile(outputFile, contents);

        // Also write the level.dat file (if it exists. Otherwise, it's likely a new world)
        if (Files.exists(levelDatFile)) {
            writeLevelDat(levelDatFile, biomes);
        }
    }

    private List<Object> assembleBiomeList(WorldConfig config) throws IOException {
        Path parametersFile = this.vanillaDatapackPath.resolve(Path.of("reports", "biome_parameters", "minecraft", "overworld.json"));

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
        return biomes;
    }

    /**
     * Modifies the level.dat file to use the given world config as multi-noise biome source. The reason we need to
     * do this, is that normally world presets are only applied when creating a new world. However, we want to also
     * apply the settings to already existing worlds.
     * <p>This method needs to be called during the bootstrap phase.</p>
     * @param biomes The biome list to write.
     * @param levelDatFile The level.dat file to modify.
     * @throws IOException If an I/O error occurs.
     */
    private void writeLevelDat(Path levelDatFile, List<Object> biomes) throws IOException {
        // Load the level.dat file as an NBT object
        CompoundTag levelDat = NbtIo.readCompressed(levelDatFile, NbtAccounter.unlimitedHeap());
        CompoundTag generatorTag = levelDat.getCompoundOrEmpty("Data")
                .getCompoundOrEmpty("WorldGenSettings")
                .getCompoundOrEmpty("dimensions")
                .getCompoundOrEmpty("minecraft:overworld")
                .getCompoundOrEmpty("generator");

        ListTag biomeListTag = convertToNbt(biomes);

        // Put the new biome source into the generator tag
        CompoundTag biomeSourceTag = new CompoundTag();
        biomeSourceTag.putString("type", "minecraft:multi_noise");
        biomeSourceTag.put("biomes", biomeListTag);
        generatorTag.put("biome_source", biomeSourceTag);

        // Backup the original level.dat file
        Path backupFile = levelDatFile.resolveSibling("level-before-dough-backup.dat_old");
        if (!Files.exists(backupFile)) {
            Files.move(levelDatFile, backupFile);
        }

        // Save the modified level.dat file
        NbtIo.writeCompressed(levelDat, levelDatFile.resolveSibling("level.dat"));
    }

    private static @NonNull ListTag convertToNbt(List<Object> biomes) throws IOException {
        ListTag biomeListTag = new ListTag();
        // Convert the biome list to NBT format
        for (Object biomeObject : biomes) {
            Map<String, Object> biomeMap = JsonUtil.castToMap(biomeObject);

            CompoundTag biomeTag = new CompoundTag();
            biomeTag.putString("biome", (String) biomeMap.get("biome"));

            Map<String, Object> parametersMap = JsonUtil.castToMap(biomeMap.get("parameters"));
            CompoundTag parametersTag = new CompoundTag();
            for (Map.Entry<String, Object> entry : parametersMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Number number) {
                    // Single number
                    parametersTag.putFloat(key, number.floatValue());
                } else if (value instanceof List<?> list) {
                    // List of numbers
                    List<Tag> floatList = list.stream()
                            .map(obj -> ((Number) obj).floatValue())
                            .map(number -> (Tag) FloatTag.valueOf(number))
                            .toList();
                    parametersTag.put(key, new ListTag(floatList));
                } else {
                    throw new IOException("Expected float or float list for biome parameter '" + key + "', but found: " + value);
                }
            }
            biomeTag.put("parameters", parametersTag);
            biomeListTag.add(biomeTag);
        }
        return biomeListTag;
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
