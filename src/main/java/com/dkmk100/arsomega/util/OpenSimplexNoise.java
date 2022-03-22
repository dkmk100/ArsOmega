package com.dkmk100.arsomega.util;
/*
 * 2014 OpenSimplex Noise in Java.
 * by Kurt Spencer
 *
 * Updated Dec 2019 and Feb 2020:
 * - New lattice-symmetric gradient sets
 * - Optional alternate lattice orientation evaluators
 *
 * This implementation has been updated to slightly improve its output, but it is recommented to first
 * try the newer OpenSimplex2S or OpenSimplex2F noise. These are located in the OpenSimplex2 repo:
 * https://github.com/KdotJPG/OpenSimplex2
 *
 * In the event that the output of this OpenSimplex continues to better fit your project's needs than
 * either OpenSimplex2 variant, an updated backport of DigitalShadow's optimization is available here:
 * https://github.com/KdotJPG/OpenSimplex2/blob/master/java/legacy/OpenSimplex.java
 *
 * This is mostly kept here for reference. In particular, the 4D code is very slow.
 */

public class OpenSimplexNoise {

    private static final double STRETCH_CONSTANT_2D = -0.211324865405187;    // (1/Math.sqrt(2+1)-1)/2;
    private static final double SQUISH_CONSTANT_2D = 0.366025403784439;      // (Math.sqrt(2+1)-1)/2;
    private static final double STRETCH_CONSTANT_3D = -1.0 / 6;              // (1/Math.sqrt(3+1)-1)/3;
    private static final double SQUISH_CONSTANT_3D = 1.0 / 3;                // (Math.sqrt(3+1)-1)/3;
    private static final double STRETCH_CONSTANT_4D = -0.138196601125011;    // (1/Math.sqrt(4+1)-1)/4;
    private static final double SQUISH_CONSTANT_4D = 0.309016994374947;      // (Math.sqrt(4+1)-1)/4;

    private static final long DEFAULT_SEED = 0;

    private static final int PSIZE = 2048;
    private static final int PMASK = 2047;

    private static short[] perm;
    private static Grad2[] permGrad2;
    //private Grad3[] permGrad3;
    //private Grad4[] permGrad4;

    public OpenSimplexNoise() {
        this(DEFAULT_SEED);
    }

    public OpenSimplexNoise(short[] perm) {
        this.perm = perm;
        permGrad2 = new Grad2[PSIZE];
        //permGrad3 = new Grad3[PSIZE];
        //permGrad4 = new Grad4[PSIZE];

        for (int i = 0; i < PSIZE; i++) {
            permGrad2[i] = GRADIENTS_2D[perm[i]];
            //permGrad3[i] = GRADIENTS_3D[perm[i]];
            //permGrad4[i] = GRADIENTS_4D[perm[i]];
        }
    }

    public OpenSimplexNoise(long seed) {
        perm = new short[PSIZE];
        permGrad2 = new Grad2[PSIZE];
        //permGrad3 = new Grad3[PSIZE];
        //permGrad4 = new Grad4[PSIZE];
        short[] source = new short[PSIZE];
        for (short i = 0; i < PSIZE; i++)
            source[i] = i;
        for (int i = PSIZE - 1; i >= 0; i--) {
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            int r = (int)((seed + 31) % (i + 1));
            if (r < 0)
                r += (i + 1);
            perm[i] = source[r];
            permGrad2[i] = GRADIENTS_2D[perm[i]];
            //permGrad3[i] = GRADIENTS_3D[perm[i]];
            //permGrad4[i] = GRADIENTS_4D[perm[i]];
            source[r] = source[i];
        }
    }

    // 2D OpenSimplex Noise.
    public static double eval(double x, double y) {

        // Place input coordinates onto grid.
        double stretchOffset = (x + y) * STRETCH_CONSTANT_2D;
        double xs = x + stretchOffset;
        double ys = y + stretchOffset;

        // Floor to get grid coordinates of rhombus (stretched square) super-cell origin.
        int xsb = fastFloor(xs);
        int ysb = fastFloor(ys);

        // Compute grid coordinates relative to rhombus origin.
        double xins = xs - xsb;
        double yins = ys - ysb;

        // Sum those together to get a value that determines which region we're in.
        double inSum = xins + yins;

        // Positions relative to origin point.
        double squishOffsetIns = inSum * SQUISH_CONSTANT_2D;
        double dx0 = xins + squishOffsetIns;
        double dy0 = yins + squishOffsetIns;

        // We'll be defining these inside the next block and using them afterwards.
        double dx_ext, dy_ext;
        int xsv_ext, ysv_ext;

        double value = 0;

        // Contribution (1,0)
        double dx1 = dx0 - 1 - SQUISH_CONSTANT_2D;
        double dy1 = dy0 - 0 - SQUISH_CONSTANT_2D;
        double attn1 = 2 - dx1 * dx1 - dy1 * dy1;
        if (attn1 > 0) {
            attn1 *= attn1;
            value += attn1 * attn1 * extrapolate(xsb + 1, ysb + 0, dx1, dy1);
        }

        // Contribution (0,1)
        double dx2 = dx0 - 0 - SQUISH_CONSTANT_2D;
        double dy2 = dy0 - 1 - SQUISH_CONSTANT_2D;
        double attn2 = 2 - dx2 * dx2 - dy2 * dy2;
        if (attn2 > 0) {
            attn2 *= attn2;
            value += attn2 * attn2 * extrapolate(xsb + 0, ysb + 1, dx2, dy2);
        }

        if (inSum <= 1) { // We're inside the triangle (2-Simplex) at (0,0)
            double zins = 1 - inSum;
            if (zins > xins || zins > yins) { // (0,0) is one of the closest two triangular vertices
                if (xins > yins) {
                    xsv_ext = xsb + 1;
                    ysv_ext = ysb - 1;
                    dx_ext = dx0 - 1;
                    dy_ext = dy0 + 1;
                } else {
                    xsv_ext = xsb - 1;
                    ysv_ext = ysb + 1;
                    dx_ext = dx0 + 1;
                    dy_ext = dy0 - 1;
                }
            } else { // (1,0) and (0,1) are the closest two vertices.
                xsv_ext = xsb + 1;
                ysv_ext = ysb + 1;
                dx_ext = dx0 - 1 - 2 * SQUISH_CONSTANT_2D;
                dy_ext = dy0 - 1 - 2 * SQUISH_CONSTANT_2D;
            }
        } else { // We're inside the triangle (2-Simplex) at (1,1)
            double zins = 2 - inSum;
            if (zins < xins || zins < yins) { // (0,0) is one of the closest two triangular vertices
                if (xins > yins) {
                    xsv_ext = xsb + 2;
                    ysv_ext = ysb + 0;
                    dx_ext = dx0 - 2 - 2 * SQUISH_CONSTANT_2D;
                    dy_ext = dy0 + 0 - 2 * SQUISH_CONSTANT_2D;
                } else {
                    xsv_ext = xsb + 0;
                    ysv_ext = ysb + 2;
                    dx_ext = dx0 + 0 - 2 * SQUISH_CONSTANT_2D;
                    dy_ext = dy0 - 2 - 2 * SQUISH_CONSTANT_2D;
                }
            } else { // (1,0) and (0,1) are the closest two vertices.
                dx_ext = dx0;
                dy_ext = dy0;
                xsv_ext = xsb;
                ysv_ext = ysb;
            }
            xsb += 1;
            ysb += 1;
            dx0 = dx0 - 1 - 2 * SQUISH_CONSTANT_2D;
            dy0 = dy0 - 1 - 2 * SQUISH_CONSTANT_2D;
        }

        // Contribution (0,0) or (1,1)
        double attn0 = 2 - dx0 * dx0 - dy0 * dy0;
        if (attn0 > 0) {
            attn0 *= attn0;
            value += attn0 * attn0 * extrapolate(xsb, ysb, dx0, dy0);
        }

        // Extra Vertex
        double attn_ext = 2 - dx_ext * dx_ext - dy_ext * dy_ext;
        if (attn_ext > 0) {
            attn_ext *= attn_ext;
            value += attn_ext * attn_ext * extrapolate(xsv_ext, ysv_ext, dx_ext, dy_ext);
        }

        return value;
    }

    private static double extrapolate(int xsb, int ysb, double dx, double dy)
    {
        Grad2 grad = permGrad2[perm[xsb & PMASK] ^ (ysb & PMASK)];
        return grad.dx * dx + grad.dy * dy;
    }
    private static int fastFloor(double x) {
        int xi = (int)x;
        return x < xi ? xi - 1 : xi;
    }
    public static class Grad2 {
        double dx, dy;
        public Grad2(double dx, double dy) {
            this.dx = dx; this.dy = dy;
        }
    }
    private static final double N2 = 7.69084574549313;


    private static final Grad2[] GRADIENTS_2D = new Grad2[PSIZE];
    static {
        Grad2[] grad2 = {
                new Grad2(0.130526192220052, 0.99144486137381),
                new Grad2(0.38268343236509, 0.923879532511287),
                new Grad2(0.608761429008721, 0.793353340291235),
                new Grad2(0.793353340291235, 0.608761429008721),
                new Grad2(0.923879532511287, 0.38268343236509),
                new Grad2(0.99144486137381, 0.130526192220051),
                new Grad2(0.99144486137381, -0.130526192220051),
                new Grad2(0.923879532511287, -0.38268343236509),
                new Grad2(0.793353340291235, -0.60876142900872),
                new Grad2(0.608761429008721, -0.793353340291235),
                new Grad2(0.38268343236509, -0.923879532511287),
                new Grad2(0.130526192220052, -0.99144486137381),
                new Grad2(-0.130526192220052, -0.99144486137381),
                new Grad2(-0.38268343236509, -0.923879532511287),
                new Grad2(-0.608761429008721, -0.793353340291235),
                new Grad2(-0.793353340291235, -0.608761429008721),
                new Grad2(-0.923879532511287, -0.38268343236509),
                new Grad2(-0.99144486137381, -0.130526192220052),
                new Grad2(-0.99144486137381, 0.130526192220051),
                new Grad2(-0.923879532511287, 0.38268343236509),
                new Grad2(-0.793353340291235, 0.608761429008721),
                new Grad2(-0.608761429008721, 0.793353340291235),
                new Grad2(-0.38268343236509, 0.923879532511287),
                new Grad2(-0.130526192220052, 0.99144486137381)
        };
        for (int i = 0; i < grad2.length; i++) {
            grad2[i].dx /= N2;
            grad2[i].dy /= N2;
        }
        for (int i = 0; i < PSIZE; i++) {
            GRADIENTS_2D[i] = grad2[i % grad2.length];
        }

    }

}