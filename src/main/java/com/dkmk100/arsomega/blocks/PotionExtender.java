package com.dkmk100.arsomega.blocks;

import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import com.hollingsworth.arsnouveau.common.block.tile.PotionMelderTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;


public class PotionExtender extends TickableModBlock {
    public PotionExtender(Properties properties) {
        super(properties);
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PotionExtenderTile(pos, state);
    }

}
