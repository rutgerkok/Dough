package nl.rutgerkok.doughworldgenerator;

import nl.rutgerkok.doughworldgenerator.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

final class DatapackGenerator {

    public static final List<Integer> FORMAT_VERSION = List.of(94, 1); // Minecraft 1.21.11 uses version 94.1

    private final Path vanillaDatapackPath;

    /**
     * Creates a new datapack generator.
     * @param vanillaDatapackPath The folder containing the vanilla world generation datapack files. Must contain
     *                           "data" and "reports" subfolders.
     */
    DatapackGenerator(Path vanillaDatapackPath) {
        this.vanillaDatapackPath = vanillaDatapackPath;
    }

    public void write(Path outputFolder) throws IOException {
        Files.createDirectories(outputFolder);
        writePackMcMeta(outputFolder);
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
