package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockUtil.class)
public class BlockUtilMixin {
    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/api/util/BlockUtil;destroyRespectsClaim(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true, remap = false)
    private static void checkKeysPressed(LivingEntity caster, World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(caster.hasEffect(ModPotions.NO_BREAK)){
            cir.setReturnValue(false);
        }
    }
}
