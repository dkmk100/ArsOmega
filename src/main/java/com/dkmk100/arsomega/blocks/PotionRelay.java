package com.dkmk100.arsomega.blocks;

import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class PotionRelay extends TickableModBlock {
    public static final VoxelShape shape = Block.box(2.0, 3.0, 2.0, 15.0, 14.0, 15.0);


    public PotionRelay() {
        super(defaultProperties().lightLevel((blockState) -> {
            return 8;
        }).noOcclusion());
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PotionRelayTile(pos, state);
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        if (!world.isClientSide()) {
            BlockEntity var8 = world.getBlockEntity(pos);
            if (var8 instanceof PotionRelayTile) {
                PotionRelayTile relayTile = (PotionRelayTile)var8;
                relayTile.disabled = world.hasNeighborSignal(pos);
                relayTile.update();
            }
        }

    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }
}
