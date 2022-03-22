package com.dkmk100.arsomega.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class PotionAmplifier extends Block {
    public PotionAmplifier(Properties properties) {
        super(properties);
    }

    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PotionAmplifierTile();
    }

}
