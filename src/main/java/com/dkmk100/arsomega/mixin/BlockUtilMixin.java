package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockUtil.class)
public class BlockUtilMixin {
    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/api/util/BlockUtil;destroyRespectsClaim(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", cancellable = true, remap = false)
    private static void checkBreak(LivingEntity caster, Level world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(caster != null && caster.hasEffect(ModPotions.NO_BREAK.get())){
            cir.setReturnValue(false);
        }
    }
}
