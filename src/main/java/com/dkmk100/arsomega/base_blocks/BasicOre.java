package com.dkmk100.arsomega.base_blocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BasicOre extends OreBlock {
    int xpDrop;
    public BasicOre(Properties properties, int xpDropAmount) {
        super(properties);
        xpDrop=xpDropAmount;
    }
    @Override
    public int getExpDrop(BlockState state, LevelReader reader, BlockPos pos, int fortune, int silktouch){
        return xpDrop;
    }
}


