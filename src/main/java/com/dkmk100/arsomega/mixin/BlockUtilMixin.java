package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.chat.Component;
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
        //Ritual of area protection
        //TODO: make this use a list of active rituals instead
        if(caster != null && caster.hasEffect(ModPotions.NO_BREAK.get())){
            cir.setReturnValue(false);
        }

        //Enchanter's wool is spell immune
        if(world.getBlockState(pos).is(RegistryHandler.ENCHANTERS_WOOL.get())){
            PortUtil.sendMessageNoSpam(caster, Component.literal("Enchanter's wool is immune to spells!"));
            cir.setReturnValue(false);
        }
    }
}
