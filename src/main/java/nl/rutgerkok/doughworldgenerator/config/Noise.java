package nl.rutgerkok.doughworldgenerator.config;

/**
 * Noise configuration. <a href="https://minecraft.wiki/w/Noise">Minecraft Wiki</a>.
 * @param amplitudes The amplitudes for each octave.
 * @param firstOctave The first octave.
 */
public record Noise(float[] amplitudes, int firstOctave) {
}
