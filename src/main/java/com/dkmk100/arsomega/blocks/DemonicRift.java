package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.events.CommonEvents;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DemonicRift extends Block {
    protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public DemonicRift(Properties properties) {
        super(properties);
    }
    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        return COLLISION_SHAPE;
    }
    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return OUTLINE_SHAPE;
    }
    @Override
    public void entityInside(BlockState p_196262_1_, World world, BlockPos p_196262_3_, Entity entity) {
        if(world instanceof ServerWorld) {
            RegistryKey<World> registrykey = RegistryKey.create(Registry.DIMENSION_REGISTRY, RegistryHandler.DIMTYPE);
            ServerWorld demonDim = world.getServer().getLevel(registrykey);
            ServerWorld dest = world.dimensionType() == demonDim.dimensionType() ? world.getServer().overworld() : demonDim;
            CommonEvents.teleportEntity(entity,new BlockPos(entity.position()).above(),dest,(ServerWorld)world);
        }
    }
}

