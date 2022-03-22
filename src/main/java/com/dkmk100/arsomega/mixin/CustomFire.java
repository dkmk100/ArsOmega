package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.blocks.GorgonFire;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFireBlock.class)
public class CustomFire {
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/block/AbstractFireBlock;getState(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", cancellable = true, remap = false)
    private static void getState(IBlockReader p_235326_0_, BlockPos p_235326_1_, CallbackInfoReturnable<BlockState> cir){
        BlockPos blockpos = p_235326_1_.below();
        BlockState blockstate = p_235326_0_.getBlockState(blockpos);
        if(GorgonFire.canSurviveOnBlock(blockstate.getBlock())){
            cir.setReturnValue(RegistryHandler.GORGON_FIRE.get().defaultBlockState());
        }
    }
}
