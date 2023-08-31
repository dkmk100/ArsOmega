package com.dkmk100.arsomega.glyphs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class LightUtil {
    public static int getLightValue(BlockPos pos, Level world){
        int skyLightIgnored = world.isNight() ? 14 : 0;

        int light = world.getLightEngine().getRawBrightness(pos,skyLightIgnored);
        return light;
    }
}
