package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.events.CommonEvents;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MirrorPortalBlock extends Block implements EntityBlock {
    protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    public MirrorPortalBlock(Properties properties) {
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tile = level.getBlockEntity(pos);
        if(hand== InteractionHand.MAIN_HAND || (!player.getItemInHand(InteractionHand.OFF_HAND).isEmpty() && player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())) {
            if (tile instanceof MirrorPortalBlockEntity && level instanceof ServerLevel) {
                InteractionResult result = ((MirrorPortalBlockEntity) tile).OnRightClick(player.getItemInHand(hand), player, hand);
                ((MirrorPortalBlockEntity) tile).setChanged();
                return result;
            }
        }
        return super.use(state,level,pos,player,hand,hit);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if(world instanceof ServerLevel) {
            BlockEntity tile = world.getBlockEntity(pos);

            if(tile instanceof MirrorPortalBlockEntity){
                MirrorPortalBlockEntity mirror = (MirrorPortalBlockEntity) tile;
                if(mirror.active) {
                    if (entity instanceof ItemEntity) {
                        ItemEntity item = (ItemEntity) entity;
                        if(mirror.OnTossItem(item.getItem())) {
                            item.remove(Entity.RemovalReason.DISCARDED);
                        }
                    } else if (entity instanceof EntityProjectileSpell) {
                        EntityProjectileSpell proj = (EntityProjectileSpell) entity;
                        mirror.OnSpellReceived(proj);
                        proj.remove(Entity.RemovalReason.DISCARDED);
                    }
                }
                mirror.setChanged();
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MirrorPortalBlockEntity(pos,state);
    }
}

