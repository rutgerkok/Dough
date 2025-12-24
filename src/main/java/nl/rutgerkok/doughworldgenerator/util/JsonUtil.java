package nl.rutgerkok.doughworldgenerator.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private JsonUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<Object> castToList(Object input) throws IOException {
        if (!(input instanceof List<?> list)) {
            throw new IOException("Expected a list, but got: " + input);
        }
        @SuppressWarnings("unchecked") // All lists in JSON are lists of objects
        List<Object> objectList = (List<Object>) list;
        return objectList;
    }

    /**
     * Casts the given object to a map from strings to objects.
     * @param input The object to cast.
     * @return The casted map.
     * @throws IOException If the input is not a map.
     * @see #traverseToKey(Map, String...) for traversing to a key in a nested map.
     * @see #getMap(Map, String) for getting a map if you already know you expect a map at the key.
     */
    public static Map<String, Object> castToMap(Object input) throws IOException {
        if (!(input instanceof Map<?, ?> map)) {
            throw new IOException("Expected a map, but got: " + input);
        }
        @SuppressWarnings("unchecked") // We know the map is from strings to objects
        Map<String, Object> stringObjectMap = (Map<String, Object>) map;
        return stringObjectMap;
    }

    public static float getFloat(Map<String, Object> input, String key) {
        Object value = input.get(key);
        if (value instanceof Number number) {
            return number.floatValue();
        } else {
            throw new IllegalArgumentException("Expected a number for key \"" + key + "\", but got: " + value);
        }
    }

    public static List<Object> getList(Map<String, Object> input, String key) throws IOException {
        Object value = input.get(key);
        if (value == null) {
            throw new IOException("Key \"" + key + "\" not found in JSON. Available keys: " + List.copyOf(input.keySet()));
        }
        if (!(value instanceof List<?>)) {
            throw new IOException("Expected \"" + key + "\" to be a list, but got: " + value);
        }
        return castToList(value);
    }

    public static Map<String, Object> getMap(Map<String, Object> input, String key) throws IOException {
        Object value = input.get(key);
        if (!(value instanceof Map<?, ?>)) {
            throw new IOException("Expected \"" + key + "\" to be a map, but got: " + value);
        }
        return castToMap(value);
    }

    /**
     * Reads a JSON file and returns its contents as a map.
     *
     * @param path The path to read from.
     * @return The contents of the JSON file as a map.
     * @throws IOException If an I/O error occurs.
     */
    public static Map<String, Object> readJsonFile(Path path) throws IOException {
        Gson gson = new Gson();
        @SuppressWarnings("unchecked") // We know the root is a map of strings to objects
        Map<String, Object> returnValue = (Map<String, Object>) gson.fromJson(Files.newBufferedReader(path), Map.class);
        return returnValue;
    }

    /**
     * Traverses the given map to the given keys. So for keys "a", "b", and "c", this method
     * returns contents.get("a").get("b").get("c"), and checks that "a" and "b" are maps.
     *
     * @param contents The root map.
     * @param keys     The keys to traverse.
     * @return The object at the final key.
     * @throws IOException If a key is not found, or if an expected map is not a map.
     * @see #castToMap(Object) for casting objects to maps.
     */
    public static Object traverseToKey(Map<String, Object> contents, String... keys) throws IOException {
        Object current = contents;
        for (String key : keys) {
            if (!(current instanceof Map<?, ?> map)) {
                throw new IOException("Expected a map while traversing to key \"" + key + "\", but got: " + current);
            }
            Map<String, Object> stringObjectMap = castToMap(map);
            current = stringObjectMap.get(key);
            if (current == null) {
                throw new IOException("Key \"" + key + "\" not found while traversing JSON");
            }
        }
        return current;
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
