package com.dkmk100.arsomega.client.block;

import com.dkmk100.arsomega.client.models.InfinityCrystalModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoBlockRenderer;

public class InfinityCrystalRenderer<T extends BlockEntity & IAnimatable> extends GeoBlockRenderer<T> {

    public InfinityCrystalRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        this(rendererProvider, new InfinityCrystalModel());
    }
    public InfinityCrystalRenderer(BlockEntityRendererProvider.Context rendererProvider, AnimatedGeoModel modelProvider) {
        super(rendererProvider, modelProvider);
    }
}
