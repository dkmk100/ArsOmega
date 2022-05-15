package com.dkmk100.arsomega.entities;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class PlainRenderer <T extends Entity> extends EntityRenderer<T> {
    public PlainRenderer(EntityRendererManager p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity p_110775_1_) {
        return new ResourceLocation("blank");
    }
}
