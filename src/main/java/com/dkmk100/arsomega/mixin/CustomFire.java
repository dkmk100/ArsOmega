package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.blocks.GorgonFire;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseFireBlock.class)
public class CustomFire {
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/world/level/block/BaseFireBlock;getState(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", cancellable = true)
    private static void getState(BlockGetter p_235326_0_, BlockPos p_235326_1_, CallbackInfoReturnable<BlockState> cir){
        BlockPos blockpos = p_235326_1_.below();
        BlockState blockstate = p_235326_0_.getBlockState(blockpos);
        if(GorgonFire.canSurviveOnBlock(blockstate.getBlock())){
            cir.setReturnValue(RegistryHandler.GORGON_FIRE.get().defaultBlockState());
        }
    }
}
