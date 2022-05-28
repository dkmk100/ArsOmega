package com.dkmk100.arsomega.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class PlainRenderer<T extends Entity> extends EntityRenderer<T> {
    public PlainRenderer(EntityRendererProvider.Context p_174304_) {
        super(p_174304_);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity p_114482_) {
        return new ResourceLocation("arsomega","none");
    }
}
