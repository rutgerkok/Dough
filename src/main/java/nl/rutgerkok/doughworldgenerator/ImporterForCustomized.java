package nl.rutgerkok.doughworldgenerator;

import static com.google.common.collect.ImmutableSet.of;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * Converter for the old "Customized" world type.
 *
 */
final class ImporterForCustomized {

    private static final Set<String> CONVERT_TO_BIOME_SETTINGS = of("lower_limit_scale", "upper_limit_scale");
    private final File outputFile;
    private final Gson gson = new Gson();

    ImporterForCustomized(File outputFile) {
        this.outputFile = Objects.requireNonNull(outputFile, "outputFile");
    }

    void convert(String json) throws IOException {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();

        try {
            Map<String, Object> values = gson.fromJson(json, type);
            YamlConfiguration out = new YamlConfiguration();
            for (Entry<String, Object> entry : values.entrySet()) {
                String key = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entry.getKey());
                if (CONVERT_TO_BIOME_SETTINGS.contains(key)) {
                    out.createSection(key, ImmutableMap.of("default", entry.getValue()));
                } else {
                    out.set(key, entry.getValue());
                }
            }
            out.save(outputFile);
        } catch (JsonParseException e) {
            throw new IOException("Failed to parse JSON: " + e.getMessage());
        }
        
    }
}
