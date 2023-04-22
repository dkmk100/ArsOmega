package com.dkmk100.arsomega.blocks;

import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class CurseAltarBlock extends BaseEntityBlock {
    public CurseAltarBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49090_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CurseAltarTile(pos,state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if(world.isClientSide){
            return InteractionResult.PASS;
        }
        CurseAltarTile tile = (CurseAltarTile) world.getBlockEntity(pos);
        if(tile!=null){
            return tile.AddItem(player.getItemInHand(hand),player);
        }
        return InteractionResult.FAIL;
    }
}
