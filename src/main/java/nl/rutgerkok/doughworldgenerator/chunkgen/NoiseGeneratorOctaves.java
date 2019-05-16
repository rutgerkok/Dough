package nl.rutgerkok.doughworldgenerator.chunkgen;

import java.util.Random;

final class NoiseGeneratorOctaves {
    public static double a(final double var0) {
        return var0 - MathHelper.lFloor(var0 / 3.3554432E7 + 0.5) * 3.3554432E7;

    }

    /**
     * Collection of noise generation functions. Output is combined to produce
     * different octaves of noise.
     */
    private final NoiseGeneratorImproved[] generatorCollection;
    public NoiseGeneratorOctaves(Random seed, int octavesIn) {
        this.generatorCollection = new NoiseGeneratorImproved[octavesIn];

        for (int i = 0; i < octavesIn; ++i) {
            this.generatorCollection[i] = new NoiseGeneratorImproved(seed);
        }
    }

    public double a(final double var0, final double var2, final double var4, final double var6, final double var8,
            final boolean var10) {
        double var11 = 0.0;
        double var12 = 1.0;

        for (final NoiseGeneratorImproved var13 : this.generatorCollection) {
            var11 += var13.a(a(var0 * var12), var10 ? (-var13.b) : a(var2 * var12), a(var4 * var12), var6 * var12,
                    var8 * var12) / var12;
            var12 /= 2.0;
        }
        return var11;
    }

    public NoiseGeneratorImproved a(final int var0) {
        return this.generatorCollection[var0];
    }

}
