package com.dkmk100.arsomega.events;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.empathy_api.EmpathySpell;
import com.dkmk100.arsomega.enchants.ProactiveSpellcaster;
import com.dkmk100.arsomega.items.CursedPendant;
import com.dkmk100.arsomega.items.ModSpawnEggItem;
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.nbt.CompoundTag;
 
  
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ArsOmega.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void onProjectileHit(final ProjectileImpactEvent event){
        HitResult result = event.getRayTraceResult();
        if(result instanceof EntityHitResult){
            Entity ent = ((EntityHitResult) result).getEntity();
            if(ent instanceof Player){
                Player player = (Player)ent;
                if (CuriosApi.getCuriosHelper().findFirstCurio(player, RegistryHandler.ENCHANTERS_CLOAK.get()).isPresent()) {
                    handleCloak(event,player);
                }
            }
        }
    }
    @SubscribeEvent
    public static void OnEffectResolvePre(EffectResolveEvent.Pre event){
        Optional<SlotResult> curio = CuriosApi.getCuriosHelper().findFirstCurio(event.shooter, (stack) -> stack.getItem() instanceof CursedPendant);
        if(event.shooter != null && curio.isPresent()){
            CursedPendant.ApplyCursePre(curio.get().stack(),event);
        }
    }

    @SubscribeEvent
    public static void OnEffectResolvePost(EffectResolveEvent.Post event){
        Optional<SlotResult> curio = CuriosApi.getCuriosHelper().findFirstCurio(event.shooter, (stack) -> stack.getItem() instanceof CursedPendant);
        if(event.shooter != null && curio.isPresent()){
            CursedPendant.ApplyCursePost(curio.get().stack(),event);
        }
    }

    private static void handleCloak(final ProjectileImpactEvent event, Player player){
        Projectile projectile = event.getProjectile();

        double angleTolerance = Math.PI/2.3;
        //fix to other rotation type
        double playerAngle = player.getYHeadRot() + 90;

        //clamp
        while(playerAngle > 360){
            playerAngle-=360;
        }
        while(playerAngle < -360){
            playerAngle+=360;
        }

        //convert
        playerAngle = playerAngle/180;
        playerAngle = playerAngle * Math.PI;

        //we use this to get movement direction, much better than trying to BS the angle of hit.
        Vec3 toEntity = projectile.getDeltaMovement();
        //Vec3 pos = player.position();

        double distance = Math.sqrt(toEntity.x()*toEntity.x() + toEntity.z() * toEntity.z());//avoid using .distance because we don't use y
        if(distance==0){
            return;
        }
        Vec3 normalizedTo = toEntity.scale(1/distance);

        double entityAngle = Math.asin(normalizedTo.z);
        if (toEntity.x < 0) {
            if(entityAngle < 0){
                entityAngle = (-1 * Math.PI) - entityAngle;
            }
            else {
                entityAngle = Math.PI - entityAngle;
            }
        }

        double angleDif = Math.abs(Math.min(Math.abs(playerAngle - entityAngle), (Math.PI * 2)-Math.abs((playerAngle)-entityAngle)));

        if(angleDif < angleTolerance){
            //reflect
            Vec3 targetDelta = projectile.getDeltaMovement().scale(-1);
            projectile.setYRot(projectile.getXRot() * -1);
            //projectile.setPos(projectile.position().add(targetDelta));
            projectile.setDeltaMovement(targetDelta);
            //stop hit
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent e) {
        LivingEntity living = e.getEntity();
        if (living != null && ((living.hasEffect(ModPotions.VINE_BIND.get())&&!living.isOnFire())||living.hasEffect(ModPotions.STONE_PETRIFICATION.get()))) {
            living.setDeltaMovement(0.0D, 0.0D, 0.0D);
            living.setNoActionTime(10);
        }
    }

    @SubscribeEvent
    public static void Teleport(EntityTeleportEvent.EnderEntity e){
        LivingEntity entity = e.getEntityLiving();
        if(entity.hasEffect(ModPotions.DEMONIC_ANCHORING.get())){
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void UseItemOnBlock(PlayerInteractEvent.RightClickBlock event){
        castSpell(event.getEntity(),event.getItemStack());
    }

    @SubscribeEvent
    public static void UseItemOnBlock(PlayerInteractEvent.EntityInteract event){
        castSpell(event.getEntity(),event.getItemStack());
    }

    @SubscribeEvent
    public static void UseItemOnBlock(PlayerInteractEvent.RightClickItem event){
        castSpell(event.getEntity(),event.getItemStack());
    }

    public static boolean castSpell(Player playerIn, ItemStack s) {
        //to not make tags if there are none
        if(s.hasTag()) {
            ProactiveSpellcaster proCaster = new ProactiveSpellcaster(s);
            if ((double) s.getEnchantmentLevel(RegistryHandler.PROACTIVE_ENCHANT.get()) * 0.25 >= Math.random() && proCaster.getSpell().isValid()) {
                proCaster.castSpell(playerIn.getCommandSenderWorld(), playerIn, InteractionHand.MAIN_HAND, null);
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void attack(LivingAttackEvent e) {
        if (e.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) e.getSource().getEntity();
            if (living.hasEffect(ModPotions.STONE_PETRIFICATION.get())) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void breakEvent(PlayerEvent.BreakSpeed e){
        if(e.getEntity().hasEffect(ModPotions.STONE_PETRIFICATION.get())){
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void explodeEvent(ExplosionEvent.Start e){
        if(e.getExplosion().getSourceMob()!=null) {
            if (e.getExplosion().getSourceMob().hasEffect(ModPotions.STONE_PETRIFICATION.get())) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void useEvent(LivingEntityUseItemEvent.Start e) {
        if (e.getEntity().hasEffect(ModPotions.STONE_PETRIFICATION.get())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent()
    public static void finishUseEvent(LivingEntityUseItemEvent.Finish e) {
        ItemStack stack = e.getItem();

        if(stack.isEdible()){
            if(stack.hasTag() && stack.getTag().contains("hasEmpathy")){
                CompoundTag tag = stack.getTag();
                if(tag.getBoolean("hasEmpathy") && tag.contains("empathySpell")){
                    EmpathySpell spell = new EmpathySpell(tag.getCompound("empathySpell"),e.getEntity().getLevel());
                    float strength = tag.contains("empathyStrength") ? tag.getFloat("empathyStrength") : 0.6f;
                    float alignment = tag.contains("empathyAlignment") ? tag.getFloat("empathyAlignment") : 0.0f;
                    spell.CastSpell(e.getEntity(),strength,alignment);
                }
            }
        }
    }

    @SubscribeEvent
    public static void placeEvent(BlockEvent.EntityPlaceEvent e){
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) e.getEntity();
            if (living.hasEffect(ModPotions.STONE_PETRIFICATION.get())) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerDamaged(LivingHurtEvent e) {
        if (e.getEntity() != null) {
            LivingEntity living = e.getEntity();
            if (living.hasEffect(ModPotions.STONE_PETRIFICATION.get()) && !e.getSource().isBypassInvul()) {
                e.setAmount(0);
                e.setCanceled(true);
            } else {
                float damage = e.getAmount();
                if (living.hasEffect(ModPotions.LEAD_SKIN.get()) && !e.getSource().isBypassArmor()) {
                    int amount = living.getEffect(ModPotions.LEAD_SKIN.get()).getAmplifier() + 1;
                    for (int i = 0; i <= amount; i++) {
                        damage = damage * 0.8f;
                    }
                }
                if (e.getSource().isFire() || e.getSource().msgId.equals(RegistryHandler.FIRE_FOCUS_DAMAGE)) {
                    if (living.hasEffect(ModPotions.BURNED.get())) {
                        damage = damage * 1.5f;
                    }
                    if (living.hasEffect(ModPotions.SOUL_FIRE.get())) {
                        damage = damage * 2.0f;
                    }
                }
                e.setAmount(Math.max(0.0F, damage));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST,receiveCanceled = true)
    public static void playerDamagedFinal(LivingHurtEvent e) {
        if (e.getEntity() != null) {
            LivingEntity living = e.getEntity();
            if (living.hasEffect(ModPotions.STONE_PETRIFICATION.get()) && !e.getSource().isBypassInvul()) {
                //make sure to cancel it, this is important
                e.setAmount(0);
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerKnockback(LivingKnockBackEvent e) {
        if (e.getEntity() != null){
            LivingEntity living = e.getEntity();
            if(living.hasEffect(ModPotions.STONE_PETRIFICATION.get())){
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void entityTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.player.hasEffect(ModPotions.STONE_PETRIFICATION.get()) && !e.player.isOnGround() && !e.player.isCreative()) {
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
            Entity entity2 = EntityType.loadEntityRecursive(entity.serializeNBT(),destinationWorld,Function.identity());
            if (entity2 != null) {
                entity2.setPos(destPos.getX(),destPos.getY(),destPos.getZ());
                entity2.setDeltaMovement(entity.getDeltaMovement());
                destinationWorld.addDuringTeleport(entity2);
            }
            entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
        }
    }
    /*
    public static void teleportToDimension(Entity entity, ServerWorld world, BlockPos pos){
        entity.changeDimension(world);
        //entity.setPos(pos.getX(),pos.getY(),pos.getZ());
    }
    //*/

}
