package nl.rutgerkok.doughworldgenerator.config;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class WorldConfig {

    /**
     * Loads the world config from the given file.
     * @param worldConfigFile The world config file.
     * @return The loaded world config.
     * @throws InvalidConfigException If the config is invalid.
     */
    public static WorldConfig load(Path worldConfigFile) throws InvalidConfigException, IOException {
        RawConfig rawConfig = RawConfig.load(worldConfigFile);
        WorldConfig worldConfig = new WorldConfig(rawConfig);
        rawConfig.save(worldConfigFile);
        return worldConfig;
    }

    public final Formula continentalnessFormula;
    public final @Nullable Noise continentalnessNoise;
    public final Formula erosion;
    public final @Nullable Noise erosionNoise;
    public final Formula temperature;
    public final @Nullable Noise temperatureNoise;
    public final Formula humidity;
    public final @Nullable Noise humidityNoise;
    public final Formula weirdness;
    public final @Nullable Noise weirdnessNoise;

    WorldConfig(RawConfig rawConfig) throws InvalidConfigException {
        this.continentalnessFormula = rawConfig.getFormula("main_shape.continentalness.formula",
                "Adjustment of continentalness for biomes and landmass. For example, use 'f(x) = sub(x, 0.1)' to subtract 0.1 from the" +
                        " original value, making all biomes generate at lower continentalness, therefore generating more" +
                        " landmass. More complicated formulas are also possible. This example adds a little bump around -0.2," +
                        " so the formerly coastal areas turn into oceans, while leaving the extreme values unchanged:" +
                        " 'f(x) = sum(x, mul(0.2, gauss(x, -0.2, 0.3)))'");
        this.continentalnessNoise = rawConfig.getNoise("main_shape.continentalness.noise",
                "Noise values for the continentalness, to increase/decrease bumpyness. If not set, the default Minecraft values are used." +
                        " See https://misode.github.io/worldgen/noise/ for more information and presets.");
        this.erosion = rawConfig.getFormula("main_shape.erosion.formula", "Adjustment for erosion noise.");
        this.erosionNoise = rawConfig.getNoise("main_shape.erosion.noise","Noise values for the erosion.");
        this.temperature = rawConfig.getFormula("main_shape.temperature.formula", "Adjustment for temperature noise.");
        this.temperatureNoise = rawConfig.getNoise("main_shape.temperature.noise", "Noise values for the temperature.");
        this.humidity = rawConfig.getFormula("main_shape.humidity.formula", "Adjustment for humidity noise.");
        this.humidityNoise = rawConfig.getNoise("main_shape.humidity.noise", "Noise values for the humidity.");
        this.weirdness = rawConfig.getFormula("main_shape.weirdness.formula", "Adjustment for weirdness noise.");
        this.weirdnessNoise = rawConfig.getNoise("main_shape.weirdness.noise", "Noise values for the weirdness.");
    }


}
