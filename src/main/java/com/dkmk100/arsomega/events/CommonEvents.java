package com.dkmk100.arsomega.events;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.items.ModSpawnEggItem;
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ArsOmega.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
        ModSpawnEggItem.initSpawnEggs();
    }

    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent e) {
        LivingEntity living = e.getEntityLiving();
        if (living != null && ((living.hasEffect(ModPotions.VINE_BIND)&&!living.isOnFire())||living.hasEffect(ModPotions.STONE_PETRIFICATION))) {
            living.setDeltaMovement(0.0D, 0.0D, 0.0D);
            living.setNoActionTime(10);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void attack(LivingAttackEvent e) {
        if (e.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) e.getSource().getEntity();
            if (living.hasEffect(ModPotions.STONE_PETRIFICATION)) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void breakEvent(PlayerEvent.BreakSpeed e){
        if(e.getEntityLiving().hasEffect(ModPotions.STONE_PETRIFICATION)){
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void explodeEvent(ExplosionEvent.Start e){
        if(e.getExplosion().getSourceMob()!=null) {
            if (e.getExplosion().getSourceMob().hasEffect(ModPotions.STONE_PETRIFICATION)) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void useEvent(LivingEntityUseItemEvent.Start e) {
        if (e.getEntityLiving().hasEffect(ModPotions.STONE_PETRIFICATION)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void placeEvent(BlockEvent.EntityPlaceEvent e){
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) e.getEntity();
            if (living.hasEffect(ModPotions.STONE_PETRIFICATION)) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerDamaged(LivingHurtEvent e) {
        if (e.getEntityLiving() != null) {
            LivingEntity living = e.getEntityLiving();
            if (living.hasEffect(ModPotions.STONE_PETRIFICATION) && !e.getSource().isBypassInvul()) {
                e.setAmount(0);
                e.setCanceled(true);
            } else {
                float damage = e.getAmount();
                if (living.hasEffect(ModPotions.LEAD_SKIN) && !e.getSource().isBypassArmor()) {
                    int amount = living.getEffect(ModPotions.LEAD_SKIN).getAmplifier() + 1;
                    for (int i = 0; i <= amount; i++) {
                        damage = damage * 0.8f;
                    }
                }
                if (e.getSource().isFire() || e.getSource().msgId.equals(RegistryHandler.FIRE_FOCUS_DAMAGE)) {
                    if (living.hasEffect(ModPotions.BURNED)) {
                        damage = damage * 1.5f;
                    }
                    if (living.hasEffect(ModPotions.SOUL_FIRE)) {
                        damage = damage * 2.0f;
                    }
                }
                e.setAmount(Math.max(0.0F, damage));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST,receiveCanceled = true)
    public static void playerDamagedFinal(LivingHurtEvent e) {
        if (e.getEntityLiving() != null) {
            LivingEntity living = e.getEntityLiving();
            if (living.hasEffect(ModPotions.STONE_PETRIFICATION) && !e.getSource().isBypassInvul()) {
                //make sure to cancel it, this is important
                e.setAmount(0);
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerKnockback(LivingKnockBackEvent e) {
        if (e.getEntityLiving() != null){
            LivingEntity living = e.getEntityLiving();
            if(living.hasEffect(ModPotions.STONE_PETRIFICATION)){
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void entityTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.player.hasEffect(ModPotions.STONE_PETRIFICATION) && !e.player.isOnGround() && !e.player.isCreative()) {
            e.player.getAbilities().flying = false;
        }
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
            //Entity entity2 = entity.getType().create(destinationWorld);
            Entity entity2 = EntityType.loadEntityRecursive(entity.serializeNBT(),destinationWorld,Function.identity());
            if (entity2 != null) {
                entity2.setPos(destPos.getX(),destPos.getY(),destPos.getZ());
                entity2.setDeltaMovement(entity.getDeltaMovement());
                destinationWorld.addDuringTeleport(entity2);
            }
            entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
            //destinationWorld.getProfiler().endTick();
            //originalWorld.entity ;
            //destinationWorld.resetUpdateEntityTick();
            //destinationWorld.getProfiler().endTick();
        }
    }
    /*
    public static void teleportToDimension(Entity entity, ServerWorld world, BlockPos pos){
        entity.changeDimension(world);
        //entity.setPos(pos.getX(),pos.getY(),pos.getZ());
    }
    //*/

}
