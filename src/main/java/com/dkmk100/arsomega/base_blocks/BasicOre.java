package com.dkmk100.arsomega.base_blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BasicOre extends OreBlock {
    int xpDrop;
    public BasicOre(Properties properties, int xpDropAmount) {
        super(properties);
        xpDrop=xpDropAmount;
    }
    @Override
    public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch){
        return xpDrop;
    }
}


