package com.dkmk100.arsomega.client.models;

import com.dkmk100.arsomega.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;

public class ColoredItemModel<T extends Item & IAnimatable> extends AnimatedGeoModel<T> {
    String name;

    public ColoredItemModel(String name){
        super();
        this.name = name;
    }

    @Override
    public ResourceLocation getModelResource(T o) {
        return ResourceUtil.getModelResource(name);
    }

    @Override
    public ResourceLocation getTextureResource(T o) {
        return ResourceUtil.getItemTextureResource(name);
    }

    @Override
    public ResourceLocation getAnimationResource(T o) {
        return ResourceUtil.getAnimationResource(name);
    }
}
