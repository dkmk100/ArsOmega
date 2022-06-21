package com.dkmk100.arsomega.client.models;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.entities.EntityDemonRay;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RayModel extends AnimatedGeoModel<EntityDemonRay> {

    @Override
    public ResourceLocation getModelLocation(EntityDemonRay object)
    {
        return new ResourceLocation(ArsOmega.MOD_ID, "geo/ray.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EntityDemonRay object) {
        return new ResourceLocation(ArsOmega.MOD_ID, "textures/entity/ray.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityDemonRay animatable) {
        return new ResourceLocation(ArsOmega.MOD_ID, "animations/ray.animation.json");
    }
}
