package nl.rutgerkok.doughworldgenerator;

import static com.google.common.collect.ImmutableSet.of;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * Converter for the old "Customized" world type.
 *
 */
final class ImporterForCustomized {

    private static final Set<String> CONVERT_TO_BIOME_SETTINGS = of("lower_limit_scale", "upper_limit_scale");
    private final File outputFile;

    ImporterForCustomized(File outputFile) {
        this.outputFile = Objects.requireNonNull(outputFile, "outputFile");
    }

    void convert(String json) throws IOException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> values = (Map<String, Object>) JSONValue.parseWithException(json);
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
        } catch (ParseException e) {
            throw new IOException("Failed to parse JSON: " + e.getMessage());
        }
        
    }
}
