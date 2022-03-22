package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.events.CommonEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.Tags;

public class PortalBlock extends Block {
    int damage;
    protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public PortalBlock(Properties properties, int damage) {
        super(properties);
        this.damage=damage;
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
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        if(world instanceof ServerWorld) {
            RegistryKey<World> registrykey = ServerWorld.OVERWORLD;
            ServerWorld dest = world.getServer().getLevel(registrykey);
            teleportEntity(dest, entity, (ServerWorld) world);
        }
    }

    void teleportEntity(ServerWorld dest, Entity target, ServerWorld oldWorld){
        if(!(oldWorld.dimensionType()!=dest.dimensionType())) {
            BlockPos pos = new BlockPos(target.getPosition(0).x, target.getPosition(0).y - 5, target.getPosition(0).z);
            CommonEvents.teleportEntity(target, pos, dest, oldWorld);
            dest.setBlockAndUpdate(pos.below(),Blocks.OBSIDIAN.defaultBlockState());
            //dest.setBlockAndUpdate(pos.above(8),Blocks.OBSIDIAN.defaultBlockState()); //new portal?
        }
    }
}
