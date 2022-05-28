package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

import java.util.List;

public class EntityBossDemonKing extends Monster {
    private int minionTimer = 100;
    private int rangedMinionTimer = 0;
    private int minionsSpawned;
    private final ServerBossEvent bossInfo = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);

    public EntityBossDemonKing(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 125;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new EntityUtil.AttackGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 24.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new EntityUtil.TargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(3, new EntityUtil.TargetGoal<>(this, IronGolem.class));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }

    }
    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
        super.dropCustomDeathLoot(p_213333_1_, p_213333_2_, p_213333_3_);
        ItemEntity itementity = this.spawnAtLocation(ItemsRegistry.DEMON_CRYSTAL);
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new GroundPathNavigation(this, worldIn);
    }

    @Override
    public void tick() {

        super.tick();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.25F)
                .add(Attributes.ATTACK_DAMAGE,22)
                .add(Attributes.FOLLOW_RANGE,32)
                .add(Attributes.KNOCKBACK_RESISTANCE,0.35D);
    }


    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDER_DRAGON_GROWL;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.15F, 1.0F);
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource source, float amount) {
        amount = super.getDamageAfterMagicAbsorb(source,amount);
        if(source.isExplosion()||source==DamageSource.WITHER||source.isFire()){
            amount=amount/2;
        }
        if(source==DamageSource.CRAMMING||source==DamageSource.DROWN||source==DamageSource.IN_WALL||source==DamageSource.IN_FIRE){
            amount = 0;
        }
        return amount;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        float healthRatio = this.getMaxHealth()/this.getHealth();
        if(healthRatio>10){healthRatio=10;}
        minionTimer += 1;

        if (this.tickCount % 25 == 0) {
            this.heal(Math.round(0.4f * healthRatio*2f)/2f);
        }
        Vec3 entityPos =this.getPosition(0);
        BlockPos pos = new BlockPos(entityPos.x,entityPos.y,entityPos.z);
        if(!this.level.isClientSide() && this.getHealth()!=this.getMaxHealth()){
            List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(this.position(),8,3,8), entity -> entity.getClass()!=Player.class);
            if(nearby.size()<=15) {
                LivingEntity spawn = null;
                if (minionTimer >= (250 - ((this.getMaxHealth() - this.getHealth()) / 12))) {
                    if (minionsSpawned % 2 == 0) {
                        spawn = (LivingEntity) RegistryHandler.BASIC_DEMON.get().spawn((ServerLevel) this.level, null, null, pos, MobSpawnType.EVENT, true, false);
                    } else {
                        spawn = (LivingEntity) RegistryHandler.STRONG_DEMON.get().spawn((ServerLevel) this.level, null, null, pos, MobSpawnType.EVENT, true, false);
                    }
                    minionsSpawned += 1;
                    minionTimer = 0;
                } else {
                    rangedMinionTimer += 1;
                    if (rangedMinionTimer >= (350 - ((this.getMaxHealth() - this.getHealth()) / 12))) {
                        spawn = (LivingEntity) EntityType.SKELETON.spawn((ServerLevel) this.level, null, null, pos, MobSpawnType.EVENT, true, false);
                        rangedMinionTimer = 0;
                    }
                }
                if (spawn != null) {
                    spawn.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100000, 3));
                    spawn.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100000, 2));
                    spawn.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 0));
                    spawn.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 0));
                    this.getCommandSenderWorld().addFreshEntity(spawn);
                }
            }
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }


    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossInfo.addPlayer(pPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }


    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        MobEffect e = effect.getEffect();
        if (e == MobEffects.POISON || e == MobEffects.MOVEMENT_SLOWDOWN || e == ModPotions.SNARE_EFFECT || e == com.dkmk100.arsomega.potions.ModPotions.DEMONIC_CURSE) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.75F;
    }

}
