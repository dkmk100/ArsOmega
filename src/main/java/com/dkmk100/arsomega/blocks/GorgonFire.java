package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.block.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class GorgonFire extends AbstractFireBlock {
    public GorgonFire(AbstractBlock.Properties p_i241187_1_) {
        super(p_i241187_1_, 3.0F);
    }

    @Override
    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        return this.canSurvive(p_196271_1_, p_196271_4_, p_196271_5_) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
        return canSurviveOnBlock(p_196260_2_.getBlockState(p_196260_3_.below()).getBlock());
    }

    public static boolean canSurviveOnBlock(Block p_235577_0_) {
        return RegistryHandler.GORGON_FIRE_BURNABLES.contains(p_235577_0_);
    }

    protected boolean canBurn(BlockState p_196446_1_) {
        return true;
    }
}
