package nl.rutgerkok.doughworldgenerator.chunkgen;



import java.util.Random;


public final class NoiseGeneratorImproved {
    private static double a(final int var0, final double var1, final double var3, final double var5) {
        final int var6 = var0 & 0xF;
        return NoiseGenerator3Handler.a(NoiseGenerator3Handler.a[var6], var1, var3, var5);
    }

    private final byte[] d;
    public final double a;
    public final double b;

    public final double c;

    public NoiseGeneratorImproved(final Random var0) {
        this.a = var0.nextDouble() * 256.0;
        this.b = var0.nextDouble() * 256.0;
        this.c = var0.nextDouble() * 256.0;

        this.d = new byte[256];

        for (int var = 0; var < 256; ++var) {
            this.d[var] = (byte)var;

        }
        for (int var = 0; var < 256; ++var) {
            final int var2 = var0.nextInt(256 - var);
            final byte var3 = this.d[var];
            this.d[var] = this.d[var + var2];
            this.d[var + var2] = var3;
        }
    }

    public double a(final double var0, final double var2, final double var4, final double var6, final double var8) {
        final double var9 = var0 + this.a;
        final double var10 = var2 + this.b;
        final double var11 = var4 + this.c;

        final int var12 = MathHelper.floor(var9);
        final int var13 = MathHelper.floor(var10);
        final int var14 = MathHelper.floor(var11);


        final double var15 = var9 - var12;
        final double var16 = var10 - var13;
        final double var17 = var11 - var14;


        final double var18 = MathHelper.j(var15);
        final double var19 = MathHelper.j(var16);
        final double var20 = MathHelper.j(var17);

        double var22;
        if (var6 != 0.0) {
            final double var21 = Math.min(var8, var16);
            var22 = MathHelper.floor(var21 / var6) * var6;
        }          else {
            var22 = 0.0;

        }
        return this.a(var12, var13, var14, var15, var16 - var22, var17, var18, var19, var20);
    }

    private int a(final int var0) {
        return this.d[var0 & 0xFF] & 0xFF;

    }

    public double a(final int var0, final int var1, final int var2, final double var3, final double var5,
            final double var7, final double var9, final double var11, final double var13) {
        final int var14 = this.a(var0) + var1;
        final int var15 = this.a(var14) + var2;
        final int var16 = this.a(var14 + 1) + var2;

        final int var17 = this.a(var0 + 1) + var1;
        final int var18 = this.a(var17) + var2;
        final int var19 = this.a(var17 + 1) + var2;


        final double var20 = a(this.a(var15), var3, var5, var7);
        final double var21 = a(this.a(var18), var3 - 1.0, var5, var7);
        final double var22 = a(this.a(var16), var3, var5 - 1.0, var7);
        final double var23 = a(this.a(var19), var3 - 1.0, var5 - 1.0, var7);
        final double var24 = a(this.a(var15 + 1), var3, var5, var7 - 1.0);
        final double var25 = a(this.a(var18 + 1), var3 - 1.0, var5, var7 - 1.0);
        final double var26 = a(this.a(var16 + 1), var3, var5 - 1.0, var7 - 1.0);
        final double var27 = a(this.a(var19 + 1), var3 - 1.0, var5 - 1.0, var7 - 1.0);


        return MathHelper.a(var9, var11, var13, var20, var21, var22, var23, var24, var25, var26, var27);
    }
}
