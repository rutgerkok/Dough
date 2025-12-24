package nl.rutgerkok.doughworldgenerator.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class JsonUtil {

    private JsonUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Writes the given data as a pretty-printed JSON file to the given path.
     *
     * @param path The path to write to.
     * @param data The data to write. Must be a map containing only JSON-serializable values, like lists, maps, strings,
     *             numbers, and booleans.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeJsonFile(Path path, Map<String, ?> data) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = Files.newBufferedWriter(path)) {
            gson.toJson(data, writer);
        }
    }
}
