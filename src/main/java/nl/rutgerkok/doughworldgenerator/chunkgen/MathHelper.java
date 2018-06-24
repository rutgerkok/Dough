package nl.rutgerkok.doughworldgenerator.chunkgen;

public class MathHelper {

    public static double clampedLerp(double lowerBnd, double upperBnd, double slide) {
        if (slide < 0.0D) {
            return lowerBnd;
        } else {
            return slide > 1.0D ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide;
        }
    }

    public static long lFloor(double value) {
        if (value < 0) {
            // -3.5 becomes -3 when cast to long, but should be -4 for correct result
            return ((long) value) - 1;
        }
        return (long) value;
    }

    static float sqrt(float f) {
        return (float) Math.sqrt(f);
    }

}
