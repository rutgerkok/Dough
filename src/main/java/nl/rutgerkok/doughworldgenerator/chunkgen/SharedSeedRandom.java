package nl.rutgerkok.doughworldgenerator.chunkgen;

import java.util.Random;

final class SharedSeedRandom extends Random {

    private static final long serialVersionUID = 1L;

    public static Random func_205190_a(int p_205190_0_, int p_205190_1_, long p_205190_2_, long p_205190_4_) {
        return new Random(p_205190_2_ + p_205190_0_ * p_205190_0_ * 4987142 + p_205190_0_ * 5947611
                + p_205190_1_ * p_205190_1_ * 4392871L + p_205190_1_ * 389711 ^ p_205190_4_);
    }

    public SharedSeedRandom() {
    }

    public SharedSeedRandom(long p_i48691_1_) {
        super(p_i48691_1_);
    }

    public long func_202422_a(int p_202422_1_, int p_202422_2_) {
        long i = p_202422_1_ * 341873128712L + p_202422_2_ * 132897987541L;
        this.setSeed(i);
        return i;
    }

    public void func_202423_a(int p_202423_1_) {
        for (int i = 0; i < p_202423_1_; ++i) {
            this.next(1);
        }
    }

    public long func_202424_a(long p_202424_1_, int p_202424_3_, int p_202424_4_) {
        this.setSeed(p_202424_1_);
        long i = this.nextLong() | 1L;
        long j = this.nextLong() | 1L;
        long k = p_202424_3_ * i + p_202424_4_ * j ^ p_202424_1_;
        this.setSeed(k);
        return k;
    }

    public long func_202425_c(long p_202425_1_, int p_202425_3_, int p_202425_4_) {
        this.setSeed(p_202425_1_);
        long i = this.nextLong();
        long j = this.nextLong();
        long k = p_202425_3_ * i ^ p_202425_4_ * j ^ p_202425_1_;
        this.setSeed(k);
        return k;
    }

    public long func_202426_b(long p_202426_1_, int p_202426_3_, int p_202426_4_) {
        long i = p_202426_1_ + p_202426_3_ + 10000 * p_202426_4_;
        this.setSeed(i);
        return i;
    }

    public long func_202427_a(long p_202427_1_, int p_202427_3_, int p_202427_4_, int p_202427_5_) {
        long i = p_202427_3_ * 341873128712L + p_202427_4_ * 132897987541L + p_202427_1_ + p_202427_5_;
        this.setSeed(i);
        return i;
    }

    @Override
    protected int next(int p_next_1_)
    {
        return super.next(p_next_1_);
    }
}
