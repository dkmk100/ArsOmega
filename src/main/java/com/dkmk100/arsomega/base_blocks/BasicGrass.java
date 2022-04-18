package com.dkmk100.arsomega.base_blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.IPlantable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BasicGrass extends Block {
    public BasicGrass(Properties properties) {
        super(properties);
    }
    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return true;
    }
}
