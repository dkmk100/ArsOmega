package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class LevelUtil {

    @Nullable
    public static ItemEntity spawnAtLocation(ItemStack stack, float yOffset, BlockPos pos, Level world) {
        if (stack.isEmpty()) {
            return null;
        } else if (world.isClientSide) {
            return null;
        } else {
            ItemEntity itementity = new ItemEntity(world, pos.getX(),pos.getY()+yOffset,pos.getZ(), stack);
            itementity.setDefaultPickUpDelay();
            world.addFreshEntity(itementity);
            return itementity;
        }
    }

    public static int getRawLightValue(BlockPos pos, Level world){
        return world.getLightEngine().getRawBrightness(pos,0);
    }

    public static int getAdjustedLightValue(BlockPos pos, Level world){
        int skyLightIgnored = world.isNight() ? 14 : 0;

        int light = world.getLightEngine().getRawBrightness(pos,skyLightIgnored);
        return light;
    }

    //based on Ars's warp method in the blink glyph
    public static void anchoringImmuneWarp(Entity entity, BlockPos warpPos) {
        if (entity == null) return;
        Level world = entity.level;
        if (entity instanceof LivingEntity living){
            //on purpose not the ender entity event so that it isn't cancelled by demonic anchoring
            EntityTeleportEvent event = new EntityTeleportEvent(living, warpPos.getX(), warpPos.getY(), warpPos.getZ());
            if (event.isCanceled()) return;
        }
        ((ServerLevel) entity.level).sendParticles(ParticleTypes.PORTAL, entity.getX(), entity.getY() + 1, entity.getZ(),
                4, (world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);

        entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
        Networking.sendToNearby(world, entity, new PacketWarpPosition(entity.getId(), entity.getX(), entity.getY(), entity.getZ(), entity.getXRot(), entity.getYRot()));
        entity.level.playSound(null, entity.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1.0f, 1.0f);
        ((ServerLevel) entity.level).sendParticles(ParticleTypes.PORTAL, entity.blockPosition().getX() + 0.5, entity.blockPosition().getY() + 1.0, entity.blockPosition().getZ() + 0.5,
                4, (world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
    }

    public static void sendToNearby(Level world, BlockPos pos, Component message, int rangeSide, int rangeUp, boolean noSpam){
        AABB box = new AABB(pos).inflate(rangeSide, rangeUp, rangeSide);
        List<Player> players = world.getNearbyPlayers(TargetingConditions.forNonCombat(), null, box);
        for(Player player : players){
            if(noSpam){
                PortUtil.sendMessageNoSpam(player,message);
            }
            else{
                PortUtil.sendMessage(player,message);
            }
        }
    }

    public static BlockPos getPosInWorld(ServerLevel newWorld, BlockPos oldPos, ServerLevel oldWorld){
        DimensionType oldType = oldWorld.dimensionType();
        DimensionType newType = newWorld.dimensionType();
        float coordRatio = (float) DimensionType.getTeleportationScale(oldWorld.dimensionType(),newWorld.dimensionType());
        int x = Math.round(oldPos.getX() * coordRatio);
        int z = Math.round(oldPos.getZ() * coordRatio);
        float heightInOld = (oldPos.getY() - oldType.minY()) / ((float) oldType.height());
        if(heightInOld < 0.1f){
            heightInOld = 0.1f;
        }
        if(heightInOld > 0.9f){
            heightInOld = 0.9f;
        }

        int y = Math.round((newType.height() * heightInOld) + newType.minY());
        if(oldWorld.dimension().location().equals(RegistryHandler.DIMTYPE)){
            if(y<45){
                y = 45;
            }
        }

        return new BlockPos(x,y,z);
    }

    public static void teleportEntity(Entity entity, BlockPos destPos, ServerLevel destinationWorld, ServerLevel originalWorld) {

        // makes sure chunk is made
        destinationWorld.getChunk(destPos);

        if (entity instanceof Player) {
            ((ServerPlayer) entity).teleportTo(
                    destinationWorld,
                    destPos.getX() + 0.5D,
                    destPos.getY() + 1D,
                    destPos.getZ() + 0.5D,
                    entity.getRotationVector().y,
                    entity.getRotationVector().x);
        }
        else {
            Entity entity2 = EntityType.loadEntityRecursive(entity.serializeNBT(),destinationWorld, Function.identity());
            if (entity2 != null) {
                entity2.setPos(destPos.getX(),destPos.getY(),destPos.getZ());
                entity2.setDeltaMovement(entity.getDeltaMovement());
                destinationWorld.addDuringTeleport(entity2);
            }
            entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
        }
    }
}
