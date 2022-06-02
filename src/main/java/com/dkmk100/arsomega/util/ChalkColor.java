package com.dkmk100.arsomega.util;

import net.minecraft.util.Mth;

public class ChalkColor {
    public final float minRed;
    public final float minGreen;
    public final float minBlue;
    public final float redMult;
    public final float greenMult;
    public final float blueMult;


    public ChalkColor(float minRed, float minGreen, float minBlue, float redMult, float greenMult, float blueMult){
        this.minRed = minRed;
        this.minGreen = minGreen;
        this.minBlue = minBlue;
        this.redMult = redMult;
        this.greenMult = greenMult;
        this.blueMult = blueMult;
    }

    public int getColor(int tier, int maxTier){
        float percent = clamp((float)tier/(float)maxTier);
        float r = minRed + percent*redMult;
        float g = minGreen + percent*greenMult;
        float b = minBlue + percent*blueMult;
        return Mth.color(clamp(r),clamp(g),clamp(b));
    }

    float clamp(float target){
        return Math.min(Math.max(target,0.0f),1.0f);
    }
}
