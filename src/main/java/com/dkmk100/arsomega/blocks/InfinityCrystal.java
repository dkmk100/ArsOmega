package com.dkmk100.arsomega.blocks;

import com.hollingsworth.arsnouveau.common.block.ManaJar;
import com.hollingsworth.arsnouveau.common.block.tile.ManaJarTile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class InfinityCrystal extends ManaJar {
    public InfinityCrystal(Properties properties, String name){
        super(properties,name);
    }
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new InfinityCrystalTile();
    }
}
