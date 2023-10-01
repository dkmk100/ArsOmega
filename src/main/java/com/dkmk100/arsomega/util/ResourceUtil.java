package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.resources.ResourceLocation;

public class ResourceUtil {
    public static ResourceLocation getModelResource(String modelName){
        return new ResourceLocation(ArsOmega.MOD_ID,"geo/" + modelName + ".geo.json");
    }

    public static ResourceLocation getItemTextureResource(String modelName){
        return new ResourceLocation(ArsOmega.MOD_ID,"textures/items/" + modelName + ".png");
    }

    public static ResourceLocation getBlockTextureResource(String modelName){
        return new ResourceLocation(ArsOmega.MOD_ID,"textures/blocks/" + modelName + ".png");
    }

    public static ResourceLocation getEntityTextureResource(String modelName){
        return new ResourceLocation(ArsOmega.MOD_ID,"textures/entity/" + modelName + ".png");
    }

    public static ResourceLocation getAnimationResource(String modelName){
        return new ResourceLocation(ArsOmega.MOD_ID,"animations/" + modelName + ".animation.json");
    }
}
