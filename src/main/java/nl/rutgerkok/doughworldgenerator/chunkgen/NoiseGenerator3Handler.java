package nl.rutgerkok.doughworldgenerator.chunkgen;

final class NoiseGenerator3Handler {

    public static int[][] a;
    static {
        a = new int[][] { { 1, 1, 0 }, { -1, 1, 0 }, { 1, -1, 0 }, { -1, -1, 0 }, { 1, 0, 1 }, { -1, 0, 1 },
                { 1, 0, -1 }, { -1, 0, -1 }, { 0, 1, 1 }, { 0, -1, 1 }, { 0, 1, -1 }, { 0, -1, -1 }, { 1, 1, 0 },
                { 0, -1, 1 }, { -1, 1, 0 }, { 0, -1, -1 } };
    }

    protected static double a(final int[] var0, final double var1, final double var3, final double var5) {
        return var0[0] * var1 + var0[1] * var3 + var0[2] * var5;

    }
}