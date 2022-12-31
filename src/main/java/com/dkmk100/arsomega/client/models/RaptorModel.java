package com.dkmk100.arsomega.client.models;


import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.entities.EntityDemonRaptor;
import net.minecraft.resources.ResourceLocation;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;

public class RaptorModel extends AnimatedGeoModel<EntityDemonRaptor> {

    @Override
    public ResourceLocation getModelResource(EntityDemonRaptor object)
    {
        return new ResourceLocation(ArsOmega.MOD_ID, "geo/dino.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityDemonRaptor object) {
        return new ResourceLocation(ArsOmega.MOD_ID, "textures/entity/dino.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EntityDemonRaptor animatable) {
        return new ResourceLocation(ArsOmega.MOD_ID, "animations/dino.animation.json");
    }

}
