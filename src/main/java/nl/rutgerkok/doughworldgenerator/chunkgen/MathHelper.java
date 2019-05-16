package nl.rutgerkok.doughworldgenerator.chunkgen;

public class MathHelper {

    public static double a(final double var0, final double var2, final double var4, final double var6, final double var8, final double var10) {
         return d(var2, d(var0, var4, var6), d(
 
                                               var0, var8, var10));
 
     }

    public static double a(final double var0, final double var2, final double var4, final double var6,
            final double var8, final double var10, final double var12, final double var14, final double var16,
            final double var18, final double var20) {
        // Is Mojang testing how many parameters a method can have, or what?

        return d(var4, a(var0, var2, var6, var8, var10, var12), a(
                var0, var2, var14, var16, var18, var20));

    }

    public static double b(final double var0, final double var2, final double var4) {
        if (var4 < 0.0) {
            return var0;
        }
        if (var4 > 1.0) {
            return var2;
        }
         return d(var4, var0, var2);
   }

    public static double clampedLerp(double lowerBnd, double upperBnd, double slide) {
        double returnValue;
        if (slide < 0.0D) {
            returnValue = lowerBnd;
        } else {
            returnValue = slide > 1.0D ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide;
        }
        return returnValue;
    }

    public static double d(final double var0, final double var2, final double var4) {
         return var2 + var0 * (var4 - var2);
     }

    public static int floor(final double var0) {
         
                 final int var = (int)var0;
              return (var0 < var) ? (var - 1) : var;
             }

    public static double j(double var0) {
        return var0 * var0 * var0 * (var0 * (var0 * 6.0 - 15.0) + 10.0);
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
