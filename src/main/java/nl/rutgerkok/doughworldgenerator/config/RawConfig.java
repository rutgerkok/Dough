package nl.rutgerkok.doughworldgenerator.config;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public final class RawConfig {


    private static final String DEFAULT = "DEFAULT";

    private YamlConfiguration internalConfig;
    private Path configPath;

    /**
     * Loads the config from a file.
     * @param filePath The file path.
     * @return The raw config.
     */
    public static RawConfig load(Path filePath) {
        YamlConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(filePath.toFile());
        return new RawConfig(fileConfiguration, filePath);
    }

    private RawConfig(YamlConfiguration internalConfig, Path configPath) {
        this.internalConfig = internalConfig;
        this.configPath = configPath;
    }

    /**
     * Reads a formula from the config.
     * @param key The config key.
     * @param comment The comment to add to the config file.
     * @return The formula.
     * @throws InvalidConfigException If the formula stored in the config is invalid.
     */
    public Formula getFormula(String key, String comment) throws InvalidConfigException{

        String formulaString = internalConfig.getString(key);
        if (formulaString == null) {
            formulaString = "f(x) = x"; // By default, we use the identity function
        }

        try {
            Formula formula = new Formula(formulaString);
            internalConfig.set(key, formula.toString()); // Updates formatting
            internalConfig.setComments(key, toMultilineComment(comment));
            return formula;
        } catch (ParseException e) {
            throw new InvalidConfigException(e.getMessage(), this.configPath, key, formulaString, e.getErrorOffset());
        }
    }

    public @Nullable Noise getNoise(String key, String comment) throws InvalidConfigException {
        boolean hasAmplitudes = internalConfig.contains(key + ".amplitudes") && !DEFAULT.equals(internalConfig.get(key + ".amplitudes"));
        boolean hasFirstOctave = internalConfig.contains(key + ".first_octave") && !DEFAULT.equals(internalConfig.get(key + ".first_octave"));
        if (!hasAmplitudes && !hasFirstOctave) {
            // Both values absent is fine

            // Still set comments and default values
            internalConfig.set(key + ".amplitudes", DEFAULT);
            internalConfig.set(key + ".first_octave", DEFAULT);
            internalConfig.setComments(key, toMultilineComment(comment));
            return null;
        }
        if (!hasAmplitudes || !hasFirstOctave) {
            throw new InvalidConfigException("Both amplitudes and first_octave must be set", this.configPath, key, "", -1);
        }

        // Update comments
        internalConfig.setComments(key, toMultilineComment(comment));

        List<Double> doubleList = internalConfig.getDoubleList(key + ".amplitudes");
        int firstOctave = internalConfig.getInt(key + ".first_octave");

        if (doubleList.isEmpty()) {
            throw new InvalidConfigException("Amplitudes list cannot be empty", this.configPath, key + ".amplitudes", "[]", 0);
        }
        float[] floatArray = new float[doubleList.size()];
        for (int i = 0; i < doubleList.size(); i++) {
            floatArray[i] = doubleList.get(i).floatValue();
        }
        return new Noise(floatArray, firstOctave);
    }

    private List<String> toMultilineComment(String comment) {
        @SuppressWarnings("deprecation") // Let's use it until it's removed - there's no alternative in Paper API yet
        String wrapped = WordUtils.wrap(comment, 80);
        return Arrays.asList(wrapped.split("\n"));
    }

    /**
     * Saves the config to a file.
     * @param file The file path.
     * @throws IOException If an I/O error occurs.
     */
    public void save(Path file) throws IOException {
        internalConfig.save(file.toFile());
    }
}
