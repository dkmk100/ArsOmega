package com.dkmk100.arsomega.blocks;

import com.hollingsworth.arsnouveau.common.block.ModBlock;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Random;

public class DemonicLightBlock  extends ModBlock implements EntityBlock, SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0);

    public DemonicLightBlock() {
        super(defaultProperties().lightLevel((bs) -> {
            return (Integer)bs.getValue(SconceBlock.LIGHT_LEVEL) == 0 ? 14 : (Integer)bs.getValue(SconceBlock.LIGHT_LEVEL);
        }).noCollission().noOcclusion().dynamicShape().strength(0.0F, 0.0F));
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DemonicLightTile(pos, state);
    }

    public boolean isRandomlyTicking(BlockState state) {
        return false;
    }

    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SconceBlock.LIGHT_LEVEL, BlockStateProperties.WATERLOGGED});
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof DemonicLightTile) {
            Random random = context.getLevel().random;
            DemonicLightTile tile = (DemonicLightTile)context.getLevel().getBlockEntity(context.getClickedPos());
            tile.red = Math.max(10, random.nextInt(255));
            tile.green = Math.max(10, random.nextInt(255));
            tile.blue = Math.max(10, random.nextInt(255));
        }

        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return (BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    public BlockState updateShape(BlockState stateIn, Direction side, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if ((Boolean)stateIn.getValue(BlockStateProperties.WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        return stateIn;
    }
}
