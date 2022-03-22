package com.dkmk100.arsomega.util;

import net.minecraft.util.math.vector.Vector3d;

public class MathUtil {
    public static double Sigmoid (double x){
        double y = (1 + Math.exp(x*-1));
        return 1/y;
    };
    public static float Sigmoid (float x){
        float y = (float) (1 + Math.exp(x*-1));
        return 1/y;
    };
   public static float WeightAverage (float x1,int w1,float x2,int w2,float x3,int w3,float x4,int w4,float x5,int w5){
       float y=(x1*w1)+(x2*w2)+(x3*w3)+(x4*w4)+(x5*w5);
       y=y/(w1+w2+w3+w4+w5);
       return y;
   }
   public static double TestNoise(double x){
       double multiplier = 1.0;
       double f1 = Math.sin(Math.tan(x));
       double f2 = Math.cos(x-1);
       double f3 = Math.sqrt(Math.abs(Math.log(Math.abs(x))));
       double f4 = f1*f2*f3*multiplier;
       double f5 = 1/Math.exp(Math.abs(f4));
       return f5;
   }
   public static double TestNoise(double x, double y){
       double offsetx=75.0;
       double offsety=-10.0;
       double multx=1.01;
       double multy=-0.985;
       double fx = TestNoise((x+offsetx)*multx);
       double fy = TestNoise((y+offsety)*multy);
       double fxy = TestNoise(((x+offsetx)*multx+(y+offsety)*multy)/2);
       double f = (fx+fy+fxy)/3;
       return f;
   }
   public static Vector3d GetVecDir(Vector3d input){
       double dx = input.x;
       double dy = input.y;
       double dz = input.z;
       double d = Math.sqrt(dx*dx+dy*dy+dz*dz);
       dx/=d;
       dy/=d;
       dz/=d;
       return new Vector3d(dx,dy,dz);
   }
}
