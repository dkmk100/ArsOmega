package com.dkmk100.arsomega.client.models;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;

public class GeckolibEntityModel <T extends LivingEntity & IAnimatable> extends AnimatedGeoModel<T> {
    String name;
    public GeckolibEntityModel(String name){
        this.name = name;
    }

    @Override
    public ResourceLocation getModelResource(T t) {
        return ResourceUtil.getModelResource(name);
    }

    @Override
    public ResourceLocation getTextureResource(T t) {
        return ResourceUtil.getEntityTextureResource(name);
    }

    @Override
    public ResourceLocation getAnimationResource(T t) {
        return ResourceUtil.getAnimationResource(name);
    }
}
