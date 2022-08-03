package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.events.CommonEvents;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class DemonicRift extends Block {
    protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public DemonicRift(Properties properties) {
        super(properties);
    }
    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
        return COLLISION_SHAPE;
    }
    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return OUTLINE_SHAPE;
    }
    @Override
    public void entityInside(BlockState p_196262_1_, Level world, BlockPos p_196262_3_, Entity entity) {
        if(world instanceof ServerLevel) {
            ResourceKey<Level> registrykey = ResourceKey.create(Registry.DIMENSION_REGISTRY, RegistryHandler.DIMTYPE);
            ServerLevel demonDim = world.getServer().getLevel(registrykey);
            ServerLevel dest = world.dimensionType() == demonDim.dimensionType() ? world.getServer().overworld() : demonDim;
            this.teleportEntity(dest,entity,new BlockPos(entity.position()).above(),(ServerLevel)world);
        }
    }

    void teleportEntity(ServerLevel dest, Entity target, BlockPos pos, ServerLevel oldWorld){
        pos = new BlockPos(pos.getX(),Math.min(dest.getMinBuildHeight(),Math.max(pos.getY(),dest.getMaxBuildHeight())),pos.getZ());
        if((oldWorld.dimensionType()!=dest.dimensionType())) {
            BlockPos pos2 = new BlockPos(target.getX(), pos.getY() - 1, target.getZ());
            CommonEvents.teleportEntity(target, pos2, dest, oldWorld);

            if(dest.getBlockState(pos2.below()).isAir()) {
                dest.setBlockAndUpdate(pos2.below(), Blocks.OBSIDIAN.defaultBlockState());
            }
        }
    }
}

