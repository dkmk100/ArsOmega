package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.events.CommonEvents;
import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class PortalBlock extends Block implements EntityBlock {
    protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    public PortalBlock(Properties properties) {
        super(properties);
    }
    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
        return COLLISION_SHAPE;
    }
    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return COLLISION_SHAPE;
    }
    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if(world instanceof ServerLevel) {
            String target = "minecraft:overworld";
            BlockEntity tile = world.getBlockEntity(pos);
            if(tile!=null && tile instanceof PortalBlockEntity){
                target = ((PortalBlockEntity)tile).targetDim;
            }
            try {
                ResourceKey<Level> registrykey = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(target));
                ServerLevel dest = world.getServer().getLevel(registrykey);
                teleportEntity(dest, entity, pos, (ServerLevel) world);
            }
            catch (Exception e){
                ArsOmega.LOGGER.error("Error on portal block");
                e.printStackTrace();
            }
        }
    }

    void teleportEntity(ServerLevel dest, Entity target, BlockPos pos, ServerLevel oldWorld){
        if((oldWorld.dimensionType()!=dest.dimensionType())) {
            ArsOmega.LOGGER.info("attempting to teleport");
            BlockPos pos2 = new BlockPos(target.getX(), pos.getY() - 5, target.getZ());
            CommonEvents.teleportEntity(target, pos2, dest, oldWorld);

            if(dest.getBlockState(pos2.below()).isAir()) {
                dest.setBlockAndUpdate(pos2.below(), Blocks.OBSIDIAN.defaultBlockState());
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalBlockEntity(pos,state);
    }
}
