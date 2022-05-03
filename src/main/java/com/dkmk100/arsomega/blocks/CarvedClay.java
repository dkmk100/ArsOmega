package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.base_blocks.MagicAnimatable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class CarvedClay extends HorizontalDirectionalBlock implements MagicAnimatable {
    boolean autoConvert;
    Block clayType;
    Supplier<EntityType<? extends Entity>> entityType;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public CarvedClay(Properties p_i48440_1_,boolean autoConvert,Block clay, EntityType<? extends Entity> entity) {
        super(p_i48440_1_);
        this.autoConvert = autoConvert;
        this.clayType=clay;
        this.entityType=() -> entity;
    }
    public CarvedClay(Properties p_i48440_1_, boolean autoConvert, Block clay, Supplier<EntityType<? extends Entity>> supplier) {
        super(p_i48440_1_);
        this.autoConvert = autoConvert;
        this.clayType=clay;

        this.entityType=supplier;
    }

    @Override
    public void onPlace(BlockState p_220082_1_, Level world, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
        super.onPlace(p_220082_1_, world, p_220082_3_, p_220082_4_, p_220082_5_);
        if (autoConvert && world instanceof ServerLevel) {
            CheckGolem(p_220082_3_, (ServerLevel) world);
        }
    }



    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
        return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(FACING);
    }

    void CheckGolem(BlockPos pos, ServerLevel world) {
        if (pos.getY() > 2) {
            BlockPos below = pos.below();
            Block block = world.getBlockState(below).getBlock();
            if (block == clayType) {
                BlockPos below2 = below.below();
                Block block2 = world.getBlockState(below2).getBlock();
                if (block == clayType) {
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    world.setBlockAndUpdate(below, Blocks.AIR.defaultBlockState());
                    world.setBlockAndUpdate(below2, Blocks.AIR.defaultBlockState());
                    Entity golem = entityType.get().spawn(world,null,null, below2, MobSpawnType.MOB_SUMMONED,true,false);
                    world.addFreshEntity(golem);
                }
            }
        }
    }

    @Override
    public void Animate(BlockPos pos, ServerLevel world) {
        CheckGolem(pos,world);
    }
}
