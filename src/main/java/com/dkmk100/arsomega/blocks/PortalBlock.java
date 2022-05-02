package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.events.CommonEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class PortalBlock extends Block {
    int damage;
    protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public PortalBlock(Properties properties, int damage) {
        super(properties);
        this.damage=damage;
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
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if(world instanceof ServerLevel) {
            ResourceKey<Level> registrykey = ServerLevel.OVERWORLD;
            ServerLevel dest = world.getServer().getLevel(registrykey);
            teleportEntity(dest, entity, (ServerLevel) world);
        }
    }

    void teleportEntity(ServerLevel dest, Entity target, ServerLevel oldWorld){
        if(!(oldWorld.dimensionType()!=dest.dimensionType())) {
            BlockPos pos = new BlockPos(target.getX(), target.getY() - 5, target.getZ());
            CommonEvents.teleportEntity(target, pos, dest, oldWorld);
            dest.setBlockAndUpdate(pos.below(),Blocks.OBSIDIAN.defaultBlockState());
        }
    }
}
