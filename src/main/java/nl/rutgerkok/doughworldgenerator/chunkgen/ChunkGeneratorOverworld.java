package nl.rutgerkok.doughworldgenerator.chunkgen;

import org.bukkit.block.Biome;

import nl.rutgerkok.worldgeneratorapi.BaseNoiseGenerator;
import nl.rutgerkok.worldgeneratorapi.BiomeGenerator;

public class ChunkGeneratorOverworld implements BaseNoiseGenerator {
    private final NoiseGeneratorOctaves minLimitPerlinNoise;
    private final NoiseGeneratorOctaves maxLimitPerlinNoise;
    private final NoiseGeneratorOctaves mainPerlinNoise;
    private final OverworldGenSettings settings;
    private final NoiseGeneratorOctaves depthNoise;
    private final float[] biomeWeights;

    public ChunkGeneratorOverworld(OverworldGenSettings settings) {
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom(settings.getSeed());
        this.minLimitPerlinNoise = new NoiseGeneratorOctaves(sharedseedrandom, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(sharedseedrandom, 16);
        this.mainPerlinNoise = new NoiseGeneratorOctaves(sharedseedrandom, 8);
        new NoiseGeneratorPerlin(sharedseedrandom, 4); // Kept for seed setting side-effect
        new NoiseGeneratorOctaves(sharedseedrandom, 10); // Kept for seed setting side-effect
        this.depthNoise = new NoiseGeneratorOctaves(sharedseedrandom, 16);
        this.biomeWeights = new float[25];

        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.sqrt(i * i + j * j + 0.2F);
                this.biomeWeights[i + 2 + (j + 2) * 5] = f;
            }
        }

        this.settings = settings;
    }

    private double a(Biome biome, int i, int j, int k, double d0, double d1, double d2, double d3, double d2z) {

        double d4 = 0.0;
        double d5 = 0.0;
        double d6 = 0.0;
        double d7 = 1.0;

        for (int l = 0; l < 16; ++l) {
            final double d8 = NoiseGeneratorOctaves.a(i * d0 * d7);
            final double d9 = NoiseGeneratorOctaves.a(j * d1 * d7);
            final double d10 = NoiseGeneratorOctaves.a(k * d0 * d7);
            final double d11 = d1 * d7;

            d4 += this.minLimitPerlinNoise.a(l).a(d8, d9, d10, d11, j * d11) / d7;
            d5 += this.maxLimitPerlinNoise.a(l).a(d8, d9, d10, d11, j * d11) / d7;
            if (l < 8) {
                d6 += this.mainPerlinNoise.a(l).a(NoiseGeneratorOctaves.a(i * d2 * d7),
                        NoiseGeneratorOctaves.a(j * d3 * d7), NoiseGeneratorOctaves.a(k * d2z * d7), d3 * d7,
                        j * d3 * d7) / d7;

            }
            d7 /= 2.0;

        }

        double dd2 = d4 / this.settings.getLowerLimitScale(biome);
        double dd3 = d5 / this.settings.getUpperLimitScale(biome);
        double dd4 = (d6 / 10.0D + 1.0D) / 2.0D;
        double dd5;
        if (dd4 < this.settings.getLowerLimitScaleWeight(biome)) {
            dd5 = dd2;
        } else if (dd4 > this.settings.getUpperLimitScaleWeight(biome)) {
            dd5 = dd3;
        } else {
            dd5 = dd2 + (dd3 - dd2) * dd4;
        }
        return dd5;
    }

    private double[] a(BiomeGenerator biomeGenerator, Biome biome, int i, int j) {
        final double[] adouble = new double[2];

        float f2 = 0.0F;
        float f3 = 0.0F;
        float f4 = 0.0F;

        for (int j1 = -2; j1 <= 2; ++j1) {
            for (int k1 = -2; k1 <= 2; ++k1) {
                Biome biome1 = biomeGenerator.getZoomedOutBiome(i + j1, j + k1);
                float f5 = this.settings.getBiomeDepthOffset()
                        + settings.getBaseHeight(biome1) * this.settings.getBiomeDepthWeight();
                float f6 = this.settings.getBiomeScaleOffset()
                        + settings.getHeightVariation(biome1) * this.settings.getBiomeScaleWeight();

                float f7 = this.biomeWeights[j1 + 2 + (k1 + 2) * 5] / (f5 + 2.0F);

                if (settings.getBaseHeight(biome1) > settings.getBaseHeight(biome)) {
                    f7 /= 2.0F;
                }

                f2 += f6 * f7;
                f3 += f5 * f7;
                f4 += f7;
            }
        }

        f2 = f2 / f4;
        f3 = f3 / f4;
        f2 = f2 * 0.9F + 0.1F;
        f3 = (f3 * 4.0F - 1.0F) / 8.0F;
        adouble[0] = f3 + this.c(i, j);
        adouble[1] = f2;
        return adouble;
    }

    protected void a(BiomeGenerator biomeGenerator, final double[] adouble, final int i, final int j, final double d0,
            final double d1,
            final double d2, final double d3, double d2z, final int k, final int l) {
        Biome biome = biomeGenerator.getZoomedOutBiome(i, j);
        final double[] adouble2 = this.a(biomeGenerator, biome, i, j);
        final double d4 = adouble2[0];
        final double d5 = adouble2[1];
        final double d6 = this.g();
        final double d7 = this.h();

        for (int i2 = 0; i2 < this.i(); ++i2) {
            double d8 = this.a(biome, i, i2, j, d0, d1, d2, d3, d2z);

            d8 -= this.a(d4, d5, i2);

            if (i2 > d6) {
                d8 = MathHelper.b(d8, l, (i2 - d6) / k);
            } else if (i2 < d7) {
                d8 = MathHelper.b(d8, -30.0, (d7 - i2) / (d7 - 1.0));

            }
            adouble[i2] = d8;

        }

    }

    private double a(double d0, double d1, int i) {
        final double d2 = this.settings.getBaseSize();
                double d3 = (i - (d2 + d0 * d2 / 8.0 * 4.0)) * this.settings.getStretchY() * 128.0 / 256.0 / d1;
      
             if (d3 < 0.0) {
                  d3 *= 4.0;
           
                   }
                      return d3;
    }

    private double c(final int i, final int j) {
            double d0 = this.depthNoise.a(i * this.settings.getDepthNoiseScaleX(), 10.0,
                    j * this.settings.getDepthNoiseScaleZ(), 1.0, 0.0, true) / 8000.0;

            if (d0 < 0.0) {
                d0 = -d0 * 0.3;

            }
            d0 = d0 * 3.0 - 2.0;
            if (d0 < 0.0) {
                d0 /= 28.0;
            } else {
                if (d0 > 1.0) {
                    d0 = 1.0;

                }
                d0 /= 40.0;

            }
            return d0;

        }

    protected double g() {
        return this.i() - 4;
    }

    @Override
    public void getNoise(BiomeGenerator biomeGenerator, double[] buffer, int x, int z) {
        final double d0 = settings.getCoordinateScale();
        final double d2 = settings.getHeightScale();
        final double d3 = settings.getCoordinateScale() / settings.getMainNoiseScaleX();
        final double d4 = settings.getHeightScale() / settings.getMainNoiseScaleY();
        final double d5 = settings.getCoordinateScale() / settings.getMainNoiseScaleZ();

        this.a(biomeGenerator, buffer, x, z, d0, d2, d3, d4, d5, 3, -10);

    }

    @Override
    public TerrainSettings getTerrainSettings() {
        TerrainSettings settings = new TerrainSettings();
        settings.stoneBlock = this.settings.getStoneBlock();
        settings.waterBlock = this.settings.getWaterBlock();
        settings.seaLevel = this.settings.getSeaLevel();
        return settings;
    }

    protected double h() {
        return 0.0;

    }

    private int i() {
        return 33;
    }

}
