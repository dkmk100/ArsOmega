package com.dkmk100.arsomega.mixin;


import com.dkmk100.arsomega.potions.ModPotions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffect.class)
public class EffectMixin{

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/world/effect/MobEffect;applyEffectTick(Lnet/minecraft/world/entity/LivingEntity;I)V", cancellable = true)
    public void applyEffectTick(LivingEntity entity, int p_19468_, CallbackInfo ci) {
        if (this == (Object) MobEffects.REGENERATION && entity.hasEffect(ModPotions.BLOOD_CLOT)) {
            ci.cancel();
        }
    }
}
