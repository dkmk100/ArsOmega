package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.ForgeRegistries;

public class GorgonFire extends BaseFireBlock {
    public GorgonFire(Properties p_i241187_1_) {
        super(p_i241187_1_, 3.0F);
    }

    @Override
    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, LevelAccessor p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        return this.canSurvive(p_196271_1_, p_196271_4_, p_196271_5_) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState p_196260_1_, LevelReader p_196260_2_, BlockPos p_196260_3_) {
        return canSurviveOnBlock(p_196260_2_.getBlockState(p_196260_3_.below()).getBlock());
    }

    public static boolean canSurviveOnBlock(Block block) {
        return ForgeRegistries.BLOCKS.tags().getTag(RegistryHandler.GORGON_FIRE_BURNABLES).contains(block);
        //return true;
    }

    @Override
    protected boolean canBurn(BlockState p_196446_1_) {
        return true;
    }
}
