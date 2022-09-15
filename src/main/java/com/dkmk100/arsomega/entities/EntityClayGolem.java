package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EntityClayGolem extends AbstractGolem implements IDispellable, ITooltipProvider, IWandable {

    public enum Mode{
        COMBAT, GUARD, PATROL, HUNT;
    };

    public enum Tier{
        MAGIC, MARVELOUS, MYSTIC, ARCANE
    }

    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(EntityClayGolem.class, EntityDataSerializers.BYTE);

    protected static final EntityDataAccessor<Byte> mode = SynchedEntityData.defineId(EntityClayGolem.class, EntityDataSerializers.BYTE);

    protected static final EntityDataAccessor<Optional<BlockPos>> patrol = SynchedEntityData.defineId(EntityClayGolem.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    protected Tier tier;
    protected static final EntityDataAccessor<Optional<UUID>> owner = SynchedEntityData.defineId(EntityClayGolem.class, EntityDataSerializers.OPTIONAL_UUID);

    public EntityClayGolem(EntityType<? extends AbstractGolem> p_i50267_1_, Level p_i50267_2_, Tier tier) {
        super(p_i50267_1_, p_i50267_2_);
        this.maxUpStep = 1.0F;
        this.tier = tier;
    }

    Mode getMode(){
        return Mode.values()[entityData.get(mode)];
    }
    void setMode(Mode newMode){
        entityData.set(mode,(byte)newMode.ordinal());
    }

    public void SetOwner(Player player){
        entityData.set(owner,Optional.of(player.getUUID()));
    }

    public List<DamageSource> getImmunities(){
        switch(tier){
            case ARCANE:
                return List.of(DamageSource.CACTUS,DamageSource.SWEET_BERRY_BUSH, DamageSource.DROWN, DamageSource.FALL, DamageSource.CRAMMING,
                        DamageSource.LAVA, DamageSource.IN_FIRE, DamageSource.ANVIL, DamageSource.ON_FIRE, DamageSource.DRAGON_BREATH);
            case MYSTIC:
                return List.of(DamageSource.CACTUS,DamageSource.SWEET_BERRY_BUSH, DamageSource.DROWN, DamageSource.FALL, DamageSource.CRAMMING,
                        DamageSource.LAVA, DamageSource.IN_FIRE, DamageSource.ANVIL);
            case MARVELOUS:
                return List.of(DamageSource.CACTUS,DamageSource.SWEET_BERRY_BUSH, DamageSource.DROWN, DamageSource.FALL, DamageSource.CRAMMING);
            case MAGIC:
            default:
                return List.of(DamageSource.CACTUS,DamageSource.SWEET_BERRY_BUSH, DamageSource.DROWN);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return getImmunities().contains(source) ? false : super.hurt(source, amount);
    }

    @Nullable
    BlockPos getPatrolPos (){
        Player player = getOwner();
        if(getMode()==Mode.GUARD && player!=null){
            return player.blockPosition();
        }
        return (BlockPos)((Optional)entityData.get(patrol)).orElse((Object)null);
    }

    boolean isAlly(LivingEntity target){
        if(target == getOwner()){
            return true;
        }
        if(target instanceof EntityClayGolem){
            Player otherPlayer = ((EntityClayGolem) target).getOwner();
            if(otherPlayer != null && otherPlayer == getOwner()){
                return true;
            }
        }
        return false;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new EntityClayGolem.CopyOwnerTargetGoal(this,
                (entity) -> getMode() == Mode.COMBAT && !isAlly(entity)));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(1, new GolemReturnGoal(this, this::getPatrolPos, 4, () -> {
            return this.getTarget() == null && (getMode() == Mode.PATROL || getMode() == Mode.GUARD);
        }, () -> getMode() == Mode.GUARD ? 1.0F : 0.1F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(2, new GolemHurtBy(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (entity) -> {
            Mode mode = getMode();
            if(mode == Mode.COMBAT){
                Player player = getOwner();

                boolean test = entity instanceof Mob && ((Mob)entity).getTarget() != null && !isAlly(entity) && ((Mob)entity).getTarget().equals(player) && player!=null || player !=null && !isAlly(entity) && entity.getKillCredit() != null && entity.getKillCredit().equals(player);
                return test;
            }
            else if(mode == Mode.GUARD ){
                Player player = getOwner();
                return entity instanceof LivingEntity && player != null && (player.getKillCredit() == entity ||
                        ((entity instanceof Mob && ((Mob)entity).getTarget() != null && !isAlly(entity) && ((Mob)entity).getTarget().equals(player) && player!=null || player !=null && !isAlly(entity) && entity.getKillCredit() != null && entity.getKillCredit().equals(player))
                                && entity.position().distanceTo(player.position()) < 15));
            }
            else {
                return entity instanceof Enemy && !(entity instanceof Creeper);
            }
        }));

    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(mode,(byte)0);
        this.entityData.define(patrol,Optional.empty());
        this.entityData.define(owner,Optional.empty());
    }

    public static AttributeSupplier.Builder createAttributes(Tier tier) {
        switch(tier){
            case ARCANE:
                return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 250.0D).add(Attributes.MOVEMENT_SPEED, 0.36D)
                        .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.ATTACK_DAMAGE, 22.0D).add(Attributes.FOLLOW_RANGE,32);
            case MYSTIC:
                return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 125.0D).add(Attributes.MOVEMENT_SPEED, 0.34D)
                        .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D).add(Attributes.ATTACK_DAMAGE, 17.0D).add(Attributes.FOLLOW_RANGE,24);
            case MARVELOUS:
                return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 90.0D).add(Attributes.MOVEMENT_SPEED, 0.32D)
                        .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D).add(Attributes.ATTACK_DAMAGE, 15.0D).add(Attributes.FOLLOW_RANGE,20);
            case MAGIC:
            default:
                return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 65.0D).add(Attributes.MOVEMENT_SPEED, 0.3D)
                        .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D).add(Attributes.ATTACK_DAMAGE, 12.0D).add(Attributes.FOLLOW_RANGE,16);
        }
    }

    @Override
    protected void doPush(Entity p_28839_) {
        if (p_28839_ instanceof Enemy && !(p_28839_ instanceof Creeper) && this.getRandom().nextInt(20) == 0) {
            this.setTarget((LivingEntity)p_28839_);
        }

        super.doPush(p_28839_);
    }

    public boolean canAttackType(EntityType<?> p_213358_1_) {
        return p_213358_1_ == EntityType.CREEPER ? false : super.canAttackType(p_213358_1_);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public boolean doHurtTarget(Entity p_70652_1_) {
        this.level.broadcastEntityEvent(this, (byte)4);
        float f = this.getAttackDamage();
        float f1 = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
        boolean flag = p_70652_1_.hurt(DamageSource.mobAttack(this), f1);
        if (flag) {
            p_70652_1_.setDeltaMovement(p_70652_1_.getDeltaMovement().add(0.0D, (double)0.4F, 0.0D));
            this.doEnchantDamageEffects(this, p_70652_1_);
        }

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }


    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void customServerAiStep() {
        if(level.getGameTime() % 60 == 0){
            float f = this.getHealth();
            this.heal(getSoloHealing());
            if (this.getHealth() != f) {
                float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
            }
        }
        super.customServerAiStep();
    }

    protected int getSoloHealing(){
        switch(tier){
            case ARCANE:
                return 10;
            case MYSTIC:
                return 5;
            case MARVELOUS:
                return 2;
            case MAGIC:
            default:
                return 0;
        }
    }

    protected Item getHealItem(){
        switch(tier){
            case ARCANE:
                return ItemsRegistry.ARCANE_CLAY;
            case MYSTIC:
                return ItemsRegistry.MYSTIC_CLAY;
            case MARVELOUS:
                return ItemsRegistry.MARVELOUS_CLAY;
            case MAGIC:
            default:
                return ItemsRegistry.MAGIC_CLAY;
        }
    }
    protected float getHealPercent(){
        switch(tier){
            case ARCANE:
                return 1;
            case MYSTIC:
                return 0.5f;
            case MARVELOUS:
                return 0.335f;
            case MAGIC:
            default:
                return 0.25f;
        }
    }

    @Override
    protected InteractionResult mobInteract(Player p_230254_1_, InteractionHand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
        Item item = itemstack.getItem();
        if (item != getHealItem()) {
            return InteractionResult.PASS;
        } else {
            float f = this.getHealth();
            this.heal(this.getMaxHealth()*getHealPercent());
            if (this.getHealth() == f) {
                return InteractionResult.PASS;
            } else {
                float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
                if (!p_230254_1_.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }
    }

    @Override
    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    @Override
    public void onFinishedConnectionFirst(@javax.annotation.Nullable BlockPos storedPos, @javax.annotation.Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null) {
            if(getMode()==Mode.PATROL) {
                entityData.set(patrol, Optional.of(storedPos));
                PortUtil.sendMessage(playerEntity, "Patrol point set");
            }
            else{
                PortUtil.sendMessage(playerEntity, "Can only set patrol point in patrol mode!");
            }
        }

    }

    @Override
    public void onWanded(Player playerEntity) {
        this.entityData.set(mode, (byte) ((this.entityData.get(mode) + 1) % Mode.values().length));
    }

    boolean hasOwner(){
        return entityData.get(owner).isPresent();
    }

    Player getOwner(){
        if(!hasOwner()){
            return null;
        }
        return this.level.getPlayerByUUID(entityData.get(owner).get());
    }

    @Override
    public void getTooltip(List<Component> list) {
        list.add(new TextComponent("Mode: "+getMode().toString()));
        if(getMode()==Mode.PATROL){
            Optional<BlockPos> patrolPoint = entityData.get(patrol);
            list.add(new TextComponent("Patrol Point: " + (patrolPoint.isPresent() ? patrolPoint.get().toShortString() : "none")));
        }
        if(hasOwner()) {
            Player owner = getOwner();
            if(owner == null){
                //maybe I should save owner name somewhere instead?
                list.add(new TextComponent("Owner Offline"));
            }
            else {
                list.add(new TextComponent("Owner: " +owner.getName().getContents()));
            }
        }
        else{
            list.add(new TextComponent("No Owner"));
        }
        list.add(new TextComponent("Health: " +
                Math.round(getHealth()) + "/" + Math.round(getMaxHealth()) + " (" + Math.round(getHealth()*100/getMaxHealth())+"%)"));

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if(entityData.get(owner).isPresent()) {
            tag.putUUID("owner", entityData.get(owner).get());
        }
        tag.putByte("mode",entityData.get(mode));
        NBTUtil.storeBlockPos(tag,"patrolPoint",entityData.get(patrol).orElse(null));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if(tag.contains("owner")){
            entityData.set(owner,Optional.of(tag.getUUID("owner")));
        }
        if(NBTUtil.hasBlockPos(tag,"patrolPoint")){
            entityData.set(patrol, Optional.of(NBTUtil.getBlockPos(tag,"patrolPoint")));
        }
        entityData.set(mode, tag.getByte("mode"));
        super.readAdditionalSaveData(tag);
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity livingEntity) {
        //later will make owner be able to dispel it
        return false;
    }

    class CopyOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
        protected Predicate<LivingEntity> canTarget;

        public CopyOwnerTargetGoal(PathfinderMob creature, Predicate<LivingEntity>  targeting) {
            super(creature, false);
            canTarget = targeting;
        }

        public boolean canUse() {
            return getOwner() != null && getOwner().getLastHurtMob() != null;
        }

        @Override
        protected boolean canAttack(@Nullable LivingEntity entity, TargetingConditions p_26152_) {
            if(!canTarget.test(entity)){
                return false;
            }
            else {
                return super.canAttack(entity, p_26152_);
            }
        }

        public void start() {
            LivingEntity target = getOwner().getLastHurtMob();
            if(canAttack(target,copyOwnerTargeting)) {
                EntityClayGolem.this.setTarget(target);
            }
            super.start();
        }
    }
    class GolemHurtBy extends HurtByTargetGoal{

        public GolemHurtBy(PathfinderMob p_26039_, Class<?>... p_26040_) {
            super(p_26039_, p_26040_);
        }

        @Override
        protected boolean canAttack(@Nullable LivingEntity target, TargetingConditions conditions) {
            if(isAlly(target)){
                return false;
            }
            return super.canAttack(target, conditions);
        }
    }

    class GolemReturnGoal extends GoBackHomeGoal{

        protected Supplier<Boolean> sup;
        protected Supplier<Float> returnChance;
        public GolemReturnGoal(Mob entity, Supplier<BlockPos> pos, int maxDistance, Supplier<Boolean> shouldGo, Supplier<Float> chance) {
            super(entity, pos, maxDistance,shouldGo);
            sup = shouldGo;
            returnChance = chance;
        }

        @Override
        public boolean canUse() {
            return level.random.nextFloat() < returnChance.get() && this.positionFrom != null && !this.isInRange(blockPosition()) && this.sup.get();
        }
    }
}

