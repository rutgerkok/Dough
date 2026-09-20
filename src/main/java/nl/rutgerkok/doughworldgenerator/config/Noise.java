package nl.rutgerkok.doughworldgenerator.config;

/**
 * Noise configuration. <a href="https://minecraft.wiki/w/Noise">Minecraft Wiki</a>.
 * @param amplitudeModifiers The amplitudeModifiers for each octave.
 * @param baseOctave The base octave.
 * @param baseAmplitude The base amplitude.
 */
public record Noise(float[] amplitudeModifiers, int baseOctave, double baseAmplitude) {


}
