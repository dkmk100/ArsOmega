package com.dkmk100.arsomega.client.models;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.entities.EntityDemonRay;
import net.minecraft.resources.ResourceLocation;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;

public class RayModel extends AnimatedGeoModel<EntityDemonRay> {

    @Override
    public ResourceLocation getModelResource(EntityDemonRay object)
    {
        return new ResourceLocation(ArsOmega.MOD_ID, "geo/ray.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityDemonRay object) {
        return new ResourceLocation(ArsOmega.MOD_ID, "textures/entity/ray.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EntityDemonRay animatable) {
        return new ResourceLocation(ArsOmega.MOD_ID, "animations/ray.animation.json");
    }
}

