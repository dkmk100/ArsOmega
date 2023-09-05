package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.client.renderLayer.PetrificationGeoLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoEntityRenderer;

@Mixin(GeoEntityRenderer.class)
public class GeoEntityRendererMixin {

    @Inject(at = @At("TAIL"), method = "<init>", remap = false)
    private void init(EntityRendererProvider.Context renderManager, AnimatedGeoModel modelProvider, CallbackInfo ci){
        ((GeoEntityRenderer) (Object) this).addLayer(new PetrificationGeoLayer((GeoEntityRenderer) (Object) this));

    }
}
