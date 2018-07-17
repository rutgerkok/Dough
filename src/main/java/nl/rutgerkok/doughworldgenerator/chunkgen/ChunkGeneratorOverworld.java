package nl.rutgerkok.doughworldgenerator.chunkgen;

import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import nl.rutgerkok.worldgeneratorapi.BaseChunkGenerator;
import nl.rutgerkok.worldgeneratorapi.BiomeGenerator;

public class ChunkGeneratorOverworld implements BaseChunkGenerator {
    private final NoiseGeneratorOctaves minLimitPerlinNoise;
    private final NoiseGeneratorOctaves maxLimitPerlinNoise;
    private final NoiseGeneratorOctaves mainPerlinNoise;
    private final OverworldGenSettings settings;
    private final NoiseGeneratorOctaves depthNoise;
    private final float[] biomeWeights;

    private final BlockData stoneBlock;
    private final BlockData waterBlock;

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
        this.stoneBlock = this.settings.getStoneBlock();
        this.waterBlock = this.settings.getWaterBlock();
    }

    private void calculateNoise(Biome[] biomes, int p_202108_2_, int p_202108_3_, int p_202108_4_,
            double[] p_202108_5_) {
        double[] adouble = this.depthNoise.func_202646_a(p_202108_2_, p_202108_4_, 5, 5, this.settings.getDepthNoiseScaleX(),
                this.settings.getDepthNoiseScaleZ(), this.settings.getDepthNoiseScaleExponent());
        float f = this.settings.getCoordinateScale();
        float f1 = this.settings.getHeightScale();
        double[] adouble1 = this.mainPerlinNoise.func_202647_a(p_202108_2_, p_202108_3_, p_202108_4_, 5, 33, 5,
                f / this.settings.getMainNoiseScaleX(), f1 / this.settings.getMainNoiseScaleY(),
                f / this.settings.getMainNoiseScaleZ());
        double[] adouble2 = this.minLimitPerlinNoise.func_202647_a(p_202108_2_, p_202108_3_, p_202108_4_, 5, 33, 5,
                f, f1, f);
        double[] adouble3 = this.maxLimitPerlinNoise.func_202647_a(p_202108_2_, p_202108_3_, p_202108_4_, 5, 33, 5,
                f, f1, f);
        int i = 0;
        int j = 0;

        for (int k = 0; k < 5; ++k) {
            for (int l = 0; l < 5; ++l) {
                float f2 = 0.0F;
                float f3 = 0.0F;
                float f4 = 0.0F;
                Biome biome = biomes[k + 2 + (l + 2) * 10];

                for (int j1 = -2; j1 <= 2; ++j1) {
                    for (int k1 = -2; k1 <= 2; ++k1) {
                        Biome biome1 = biomes[k + j1 + 2 + (l + k1 + 2) * 10];
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
                double d7 = adouble[j] / 8000.0D;

                if (d7 < 0.0D) {
                    d7 = -d7 * 0.3D;
                }

                d7 = d7 * 3.0D - 2.0D;

                if (d7 < 0.0D) {
                    d7 = d7 / 2.0D;

                    if (d7 < -1.0D) {
                        d7 = -1.0D;
                    }

                    d7 = d7 / 1.4D;
                    d7 = d7 / 2.0D;
                } else {
                    if (d7 > 1.0D) {
                        d7 = 1.0D;
                    }

                    d7 = d7 / 8.0D;
                }

                ++j;
                double d8 = f3;
                double d9 = f2;
                d8 = d8 + d7 * 0.2D;
                d8 = d8 * this.settings.getBaseSize() / 8.0D;
                double d0 = this.settings.getBaseSize() + d8 * 4.0D;

                for (int l1 = 0; l1 < 33; ++l1) {
                    double d1 = (l1 - d0) * this.settings.getStretchY() * 128.0D / 256.0D / d9;

                    if (d1 < 0.0D) {
                        d1 *= 4.0D;
                    }

                    double d2 = adouble2[i] / this.settings.getLowerLimitScale(biome);
                    double d3 = adouble3[i] / this.settings.getUpperLimitScale(biome);
                    double d4 = (adouble1[i] / 10.0D + 1.0D) / 2.0D;
                    double d5;
                    if (d4 < this.settings.getLowerLimitScaleWeight(biome)) {
                        d5 = d2;
                    } else if (d4 > this.settings.getUpperLimitScaleWeight(biome)) {
                        d5 = d3;
                    } else {
                        d5 = d2 + (d3 - d2) * d4;
                    }
                    d5 -= d1;

                    if (l1 > 29) {
                        double d6 = (l1 - 29) / 3.0F;
                        d5 = d5 * (1.0D - d6) - 10.0D * d6;
                    }

                    p_202108_5_[i] = d5;
                    ++i;
                }
            }
        }
    }

    private void setBlocksInChunk(BiomeGenerator biomeGenerator, int chunkX, int chunkZ, ChunkData primer) {
        Biome[] abiome = biomeGenerator.getZoomedOutBiomes(chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
        double[] adouble = new double[825];
        this.calculateNoise(abiome, chunkX * 4, 0, chunkZ * 4, adouble);

        for (int i = 0; i < 4; ++i) {
            int j = i * 5;
            int k = (i + 1) * 5;

            for (int l = 0; l < 4; ++l) {
                int i1 = (j + l) * 33;
                int j1 = (j + l + 1) * 33;
                int k1 = (k + l) * 33;
                int l1 = (k + l + 1) * 33;

                for (int i2 = 0; i2 < 32; ++i2) {
                    double d1 = adouble[i1 + i2];
                    double d2 = adouble[j1 + i2];
                    double d3 = adouble[k1 + i2];
                    double d4 = adouble[l1 + i2];
                    double d5 = (adouble[i1 + i2 + 1] - d1) * 0.125D;
                    double d6 = (adouble[j1 + i2 + 1] - d2) * 0.125D;
                    double d7 = (adouble[k1 + i2 + 1] - d3) * 0.125D;
                    double d8 = (adouble[l1 + i2 + 1] - d4) * 0.125D;

                    for (int j2 = 0; j2 < 8; ++j2) {
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for (int k2 = 0; k2 < 4; ++k2) {
                            double d16 = (d11 - d10) * 0.25D;
                            double lvt_48_1_ = d10 - d16;

                            for (int l2 = 0; l2 < 4; ++l2) {
                                int x = i * 4 + k2, y = i2 * 8 + j2, z = l * 4 + l2;

                                if ((lvt_48_1_ += d16) > 0.0D) {
                                    primer.setBlock(x, y, z, this.stoneBlock);
                                } else if (i2 * 8 + j2 < this.settings.getSeaLevel()) {
                                    primer.setBlock(x, y, z, this.waterBlock);
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    @Override
    public void setBlocksInChunk(GeneratingChunk chunk) {
        setBlocksInChunk(chunk.getBiomeGenerator(), chunk.getChunkX(), chunk.getChunkZ(),
                chunk.getBlocksForChunk());
    }
}
