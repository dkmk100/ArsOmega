package com.dkmk100.arsomega.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatableModel;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;

@Mixin(AnimationController.class)
public class AnimationControllerMixin<T extends IAnimatable> {
    @Redirect(remap = false, method = "process",at = @At(value = "INVOKE", target = "Lsoftware/bernie/ars_nouveau/geckolib3/core/controller/AnimationController;getModel(Lsoftware/bernie/ars_nouveau/geckolib3/core/IAnimatable;)Lsoftware/bernie/ars_nouveau/geckolib3/core/IAnimatableModel;"))
    IAnimatableModel dumb(AnimationController instance, @Coerce T modelFetcher){
        return null;
    }

}
