package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.renderer.GorgonLaserRenderer;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.builder.ILoopType;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;

public class EntityGorgon extends Monster implements IAnimatable {
    public EntityGorgon(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        this.xpReward = 14;
    }

    private static final EntityDataAccessor<String> ANIM_STATE = SynchedEntityData.defineId(EntityGorgon.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Integer> ATTACK_TARGET = SynchedEntityData.defineId(EntityGorgon.class, EntityDataSerializers.INT);

    void setLaserTarget(Entity entity) {
        if(entity == null){
            this.entityData.set(ATTACK_TARGET, 0);
        }
        else {
            this.entityData.set(ATTACK_TARGET, entity.getId());
        }
    }

    public boolean hasLaserTarget() {
        return this.entityData.get(ATTACK_TARGET) != 0;
    }

    LivingEntity clientCachedTarget = null;

    @Nullable
    public LivingEntity getLaserTarget() {
        int targetId = this.entityData.get(ATTACK_TARGET);
        if(targetId == 0){
            return null;
        } else if (this.level.isClientSide) {
            if (this.clientCachedTarget != null) {
                return clientCachedTarget;
            } else {
                Entity entity = this.level.getEntity(this.entityData.get(ATTACK_TARGET));
                if (entity instanceof LivingEntity) {
                    this.clientCachedTarget = (LivingEntity)entity;
                    return this.clientCachedTarget;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    private AnimationFactory factory = new AnimationFactory(this);


    @Nullable
    protected RandomStrollGoal randomStrollGoal;

    @Override
    protected void registerGoals() {
        randomStrollGoal = new WaterAvoidingRandomStrollGoal(this, 0.7D);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(5, new EntityUtil.AttackGoal(this));

        //guardian has duration of 80
        GorgonLaserStats stats = new GorgonLaserStats(80,0.3f,4);

        this.goalSelector.addGoal(4, new GorgonLaserGoal(this, stats));

        this.goalSelector.addGoal(6, randomStrollGoal);
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new EntityUtil.TargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(3, new EntityUtil.TargetGoal<>(this, IronGolem.class));
    }

    public int getLaserColor(){
        return new ParticleColor(124, 255, 87).getColor();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, "idle");
        this.entityData.define(ATTACK_TARGET, 0);
    }

    private int getSwingDuration() {
        int x = 2;
        if (MobEffectUtil.hasDigSpeed(this)) {
            return x + 6 - (1 + MobEffectUtil.getDigSpeedAmplification(this));
        } else {
            return x + (this.hasEffect(MobEffects.DIG_SLOWDOWN) ? 6 + (1 + this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2 : 6);
        }
    }

    @Override
    protected void updateSwingTime() {
        int i = this.getSwingDuration();
        if (this.swinging) {
            ++this.swingTime;
            if (this.swingTime >= i) {
                this.swingTime = 0;
                this.swinging = false;
            }
        } else {
            this.swingTime = 0;
        }

        this.attackAnim = (float)this.swingTime / (float)i;
    }


    @Override
    public void swing(InteractionHand p_21012_, boolean p_21013_) {
        ItemStack stack = this.getItemInHand(p_21012_);
        if (!stack.isEmpty() && stack.onEntitySwing(this)) return;
        if (!this.swinging || this.swingTime >= this.getSwingDuration() / 2 || this.swingTime < 0) {
            this.swingTime = -1;
            this.swinging = true;
            this.swingingArm = p_21012_;
            if (this.level instanceof ServerLevel) {
                ClientboundAnimatePacket clientboundanimatepacket = new ClientboundAnimatePacket(this, p_21012_ == InteractionHand.MAIN_HAND ? 0 : 3);
                ServerChunkCache serverchunkcache = ((ServerLevel)this.level).getChunkSource();
                if (p_21013_) {
                    serverchunkcache.broadcastAndSend(this, clientboundanimatepacket);
                } else {
                    serverchunkcache.broadcast(this, clientboundanimatepacket);
                }
            }
        }
    }

    @Override
    public void setTarget(LivingEntity living) {
        if(living != getLaserTarget()){
            setLaserTarget(null);
        }
        super.setTarget(living);

    }

    @Override
    public void tick() {
        super.tick();
        if(this.level.isClientSide()){
            return;
        }

        if(this.isNoAi()){
            entityData.set(ANIM_STATE, "idle");
        }
        else{
            if(swinging){
                entityData.set(ANIM_STATE, "attack.sword");
            }
            else if(getNavigation() instanceof GorgonPathNavigation nav){
                if(nav.isInProgress()){
                    if(nav.getSpeedModifier() > 0.9){
                        entityData.set(ANIM_STATE, "run");
                    }
                    else{
                        entityData.set(ANIM_STATE, "walk");
                    }
                }
                else{
                    entityData.set(ANIM_STATE, "idle");
                }
            }
            else{
                entityData.set(ANIM_STATE, "idle");
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource p_217055_, DifficultyInstance p_217056_) {
        super.populateDefaultEquipmentSlots(p_217055_, p_217056_);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.IRON_SWORD));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance dif, MobSpawnType type, @Nullable SpawnGroupData p_21437_, @Nullable CompoundTag p_21438_) {
        var data = super.finalizeSpawn(level, dif, type, p_21437_, p_21438_);

        this.populateDefaultEquipmentSlots(level.getRandom(), dif);

        return data;

    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new GorgonPathNavigation(this, worldIn);
    }

    private class GorgonPathNavigation extends GroundPathNavigation{
        public GorgonPathNavigation(Mob p_26448_, Level p_26449_) {
            super(p_26448_, p_26449_);
        }

        public double getSpeedModifier(){
            return this.speedModifier;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_GROWL;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return sizeIn.height * 0.9F;
    }


    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        String animState = getEntityData().get(ANIM_STATE);
        ILoopType loopType = ILoopType.EDefaultLoopTypes.LOOP;
        if(animState.contains(".")){
            loopType = ILoopType.EDefaultLoopTypes.PLAY_ONCE;
        }
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.gorgon." + animState, loopType));
        return PlayState.CONTINUE;
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) {
        return 0f;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<EntityGorgon>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    static class GorgonLaserStats{
        int duration;
        float threshold;
        float baseDamage;
        public GorgonLaserStats(int duration, float threshold, float baseDamage){
            this.duration = duration;
            this.threshold = threshold;
            this.baseDamage = baseDamage;
        }
        public int getAttackDuration(){
            return duration;
        }

        public int getStartDelay(){
            return 25;//guardian is 10
        }

        public void onHitTarget(LivingEntity target, EntityGorgon gorgon){
            float damage = baseDamage;

            if (gorgon.level.getDifficulty() == Difficulty.HARD) {
                damage += 2.0F;
            }
            else if (gorgon.level.getDifficulty() == Difficulty.NORMAL) {
                damage += 1.0F;
            }

            if(target.getHealth() < target.getMaxHealth() * this.threshold){
                target.addEffect(new MobEffectInstance(ModPotions.STONE_PETRIFICATION.get(), 150,1,false,false));
            }
            else {
                target.hurt(DamageSource.indirectMagic(gorgon, gorgon), damage);
                if(!target.isAlive()){
                    target.setHealth(1);
                    target.addEffect(new MobEffectInstance(ModPotions.STONE_PETRIFICATION.get(), 150,1,false,false));
                }
            }

            //target.hurt(DamageSource.mobAttack(gorgon), (float)gorgon.getAttributeValue(Attributes.ATTACK_DAMAGE));
        }
    }

    static class GorgonLaserGoal extends Goal {
        private final EntityGorgon gorgon;

        private int attackTime;

        private final GorgonLaserStats stats;

        private int startAttackTime;


        public GorgonLaserGoal(EntityGorgon gorgon,GorgonLaserStats stats) {
            this.gorgon = gorgon;
            this.stats = stats;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.gorgon.getTarget();
            boolean targetValid = target != null && target.isAlive() && !target.hasEffect(ModPotions.STONE_PETRIFICATION.get());

            return targetValid && this.gorgon.distanceToSqr(this.gorgon.getTarget()) > 9.0D;
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        public void start() {
            this.attackTime = -1 * stats.getStartDelay();
            startAttackTime = attackTime;

            this.gorgon.getNavigation().stop();

            LivingEntity livingentity = gorgon.getTarget();
            if (livingentity != null) {
                gorgon.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
            }

            gorgon.hasImpulse = true;
            ArsOmega.LOGGER.info("gorgon laser start");
        }

        public void stop() {
            gorgon.setLaserTarget(null);
            ArsOmega.LOGGER.info("gorgon laser stop");
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = gorgon.getTarget();
            if (livingentity != null) {

                gorgon.getNavigation().stop();

                gorgon.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);

                if (!gorgon.hasLineOfSight(livingentity)) {
                    //unlike the guardian, blocking a gorgon makes it lose focus on you slowly
                    gorgon.setLaserTarget(null);

                    this.attackTime -= 4;//lose focus quickly so the fight is more fair

                    if(this.attackTime < startAttackTime){
                        gorgon.setTarget(null);
                    }

                } else {
                    ++this.attackTime;

                    if(this.attackTime > 0) {
                        //make sure laser is visible
                        gorgon.setLaserTarget(livingentity);

                        if (this.attackTime == 0) {
                            if (!gorgon.isSilent()) {
                                //todo: attack sound
                            }
                        }
                        else if (this.attackTime >= stats.getAttackDuration()) {
                            stats.onHitTarget(livingentity, gorgon);

                            gorgon.setTarget(null);
                            gorgon.setLaserTarget(null);
                        }
                    }

                    super.tick();
                }
            }
        }
    }
}
