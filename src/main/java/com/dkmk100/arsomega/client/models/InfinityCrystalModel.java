package com.dkmk100.arsomega.client.models;

import com.dkmk100.arsomega.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;

public class InfinityCrystalModel extends AnimatedGeoModel {
    @Override
    public ResourceLocation getModelResource(Object o) {
        return ResourceUtil.getModelResource("infinity_crystal");
    }

    @Override
    public ResourceLocation getTextureResource(Object o) {
        return ResourceUtil.getBlockTextureResource("infinity_crystal");
    }

    @Override
    public ResourceLocation getAnimationResource(Object o) {
        return ResourceUtil.getAnimationResource("infinity_crystal");
    }
}
