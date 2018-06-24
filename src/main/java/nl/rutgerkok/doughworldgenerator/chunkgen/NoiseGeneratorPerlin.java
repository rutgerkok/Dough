package nl.rutgerkok.doughworldgenerator.chunkgen;

import java.util.Random;

final class NoiseGeneratorPerlin {
    private final NoiseGeneratorSimplex[] noiseLevels;
    private final int levels;

    public NoiseGeneratorPerlin(Random p_i45470_1_, int p_i45470_2_) {
        this.levels = p_i45470_2_;
        this.noiseLevels = new NoiseGeneratorSimplex[p_i45470_2_];

        for (int i = 0; i < p_i45470_2_; ++i) {
            this.noiseLevels[i] = new NoiseGeneratorSimplex(p_i45470_1_);
        }
    }

    public double[] func_202644_a(double p_202644_1_, double p_202644_3_, int p_202644_5_, int p_202644_6_, double p_202644_7_, double p_202644_9_, double p_202644_11_)
    {
        return this.func_202645_a(p_202644_1_, p_202644_3_, p_202644_5_, p_202644_6_, p_202644_7_, p_202644_9_, p_202644_11_, 0.5D);
    }

    public double[] func_202645_a(double p_202645_1_, double p_202645_3_, int p_202645_5_, int p_202645_6_, double p_202645_7_, double p_202645_9_, double p_202645_11_, double p_202645_13_)
    {
        double[] adouble = new double[p_202645_5_ * p_202645_6_];
        double d0 = 1.0D;
        double d1 = 1.0D;

        for (int i = 0; i < this.levels; ++i)
        {
            this.noiseLevels[i].add(adouble, p_202645_1_, p_202645_3_, p_202645_5_, p_202645_6_, p_202645_7_ * d1 * d0, p_202645_9_ * d1 * d0, 0.55D / d0);
            d1 *= p_202645_11_;
            d0 *= p_202645_13_;
        }

        return adouble;
    }

    public double getValue(double p_151601_1_, double p_151601_3_)
    {
        double d0 = 0.0D;
        double d1 = 1.0D;

        for (int i = 0; i < this.levels; ++i)
        {
            d0 += this.noiseLevels[i].getValue(p_151601_1_ * d1, p_151601_3_ * d1) / d1;
            d1 /= 2.0D;
        }

        return d0;
    }
}
