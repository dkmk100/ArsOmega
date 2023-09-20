package com.dkmk100.arsomega.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtectionEnchantment.class)
public class ProtectionMixin {
    @Inject(at = @At("HEAD"), method = "getDamageProtection", cancellable = true)
    void heartstopDamageProtection(int level, DamageSource source, CallbackInfoReturnable<Integer> cir){
        if(source.getMsgId().equals("heartstop")){
            cir.setReturnValue(0);
        }
    }
}
