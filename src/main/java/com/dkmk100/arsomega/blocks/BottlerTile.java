package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BottlerTile extends ModdedTile implements ITickable {
    public BottlerTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public void tick(Level level, BlockState state, BlockPos pos) {


    }


    //public BottlerTile(BlockPos pos, BlockState state) {
        //super(RegistryHandler.PotionBottlerType.get(), pos, state);
    //}
}
