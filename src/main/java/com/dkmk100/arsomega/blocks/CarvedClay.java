package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.base_blocks.MagicAnimatable;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class CarvedClay extends HorizontalBlock implements MagicAnimatable {
    boolean autoConvert;
    Block clayType;
    Supplier<EntityType<? extends Entity>> entityType;
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public CarvedClay(Properties p_i48440_1_,boolean autoConvert,Block clay, EntityType<? extends Entity> entity) {
        super(p_i48440_1_);
        this.autoConvert = autoConvert;
        this.clayType=clay;
        this.entityType= () -> entity;
    }

    public CarvedClay(Properties p_i48440_1_, boolean autoConvert, Block clay, Supplier<EntityType<? extends Entity>> supplier) {
        super(p_i48440_1_);
        this.autoConvert = autoConvert;
        this.clayType=clay;

        this.entityType=supplier;
    }

    @Override
    public void onPlace(BlockState p_220082_1_, World world, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
        super.onPlace(p_220082_1_, world, p_220082_3_, p_220082_4_, p_220082_5_);
        if (autoConvert && world instanceof ServerWorld) {
            CheckGolem(p_220082_3_, (ServerWorld) world);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(FACING);
    }

    void CheckGolem(BlockPos pos, ServerWorld world) {
        if (pos.getY() > 2) {
            BlockPos below = pos.below();
            Block block = world.getBlockState(below).getBlock();
            if (block == clayType) {
                BlockPos below2 = below.below();
                Block block2 = world.getBlockState(below2).getBlock();
                if (block2 == clayType) {
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    world.setBlockAndUpdate(below, Blocks.AIR.defaultBlockState());
                    world.setBlockAndUpdate(below2, Blocks.AIR.defaultBlockState());
                    Entity golem = entityType.get().spawn(world,null,null, below2, SpawnReason.MOB_SUMMONED,true,false);
                    world.addFreshEntity(golem);
                }
            }
        }
    }

    @Override
    public void Animate(BlockPos pos, ServerWorld world) {
        CheckGolem(pos,world);
    }
}
