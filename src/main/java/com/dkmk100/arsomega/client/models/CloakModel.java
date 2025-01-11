package com.dkmk100.arsomega.client.models;

import com.dkmk100.arsomega.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;

public class CloakModel <T extends Item & IAnimatable> extends AnimatedGeoModel<T> {
    String name;
    String textureName;

    public CloakModel(String name){
        super();
        this.name = name;
        this.textureName = name;
    }

    public CloakModel(String name, String textureName){
        super();
        this.name = name;
        this.textureName = textureName;
    }


    @Override
    public ResourceLocation getModelResource(T o) {
        return ResourceUtil.getModelResource(name);
    }

    @Override
    public ResourceLocation getTextureResource(T o) {
        return ResourceUtil.getTextureResource(textureName);
    }

    @Override
    public ResourceLocation getAnimationResource(T o) {
        return ResourceUtil.getAnimationResource(name);
    }
}