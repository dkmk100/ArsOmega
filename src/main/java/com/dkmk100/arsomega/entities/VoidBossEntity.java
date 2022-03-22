package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class VoidBossEntity extends MonsterEntity {
    private int minionTimer = 100;
    private int rangedMinionTimer = 0;
    private int minionsSpawned;
    private final ServerBossInfo bossInfo = (ServerBossInfo) (new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenScreen(true);

    public VoidBossEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
        this.xpReward = 125;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(4, new VoidBossEntity.AttackGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 24.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new VoidBossEntity.TargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.addGoal(3, new VoidBossEntity.TargetGoal<>(this, IronGolemEntity.class));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }

    }
    @Override
    public void setCustomName(@Nullable ITextComponent pName) {
        super.setCustomName(pName);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
        super.dropCustomDeathLoot(p_213333_1_, p_213333_2_, p_213333_3_);
        ItemEntity itementity = this.spawnAtLocation(Items.NETHER_STAR);
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }
    }

    @Override
    protected PathNavigator createNavigation(World worldIn) {
        return new GroundPathNavigator(this, worldIn);
    }

    @Override
    public void tick() {

        super.tick();
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.31F)
                .add(Attributes.ATTACK_DAMAGE,22)
                .add(Attributes.FOLLOW_RANGE,32)
                .add(Attributes.KNOCKBACK_RESISTANCE,0.35D);
    }


    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDER_DRAGON_GROWL ;
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
    protected float getDamageAfterMagicAbsorb(DamageSource source, float amount) {
        amount = super.getDamageAfterMagicAbsorb(source,amount);
        if(source.isExplosion()||source==DamageSource.WITHER){
            amount=amount/2;
        }
        if(source==DamageSource.CRAMMING||source==DamageSource.DROWN||source==DamageSource.IN_WALL){
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
        rangedMinionTimer += 1;
        if (this.tickCount % 25 == 0) {
            this.heal(Math.round(0.35f * healthRatio));
        }
        Vector3d entityPos =this.getPosition(0);
        BlockPos pos = new BlockPos(entityPos.x,entityPos.y,entityPos.z);
        if(!this.level.isClientSide() && this.getHealth()!=this.getMaxHealth()){
            if (minionTimer >= (250 - ((this.getMaxHealth() - this.getHealth()) / 12))) {
                if (minionsSpawned % 2 == 0) {
                    //RegistryHandler.VOID_BEAST.get().spawn((ServerWorld) this.level, null, null, pos, SpawnReason.EVENT, true, false);
                } else {
                    EntityType.CREEPER.spawn((ServerWorld) this.level, null, null, pos, SpawnReason.EVENT, true, false);
                }
                minionsSpawned += 1;
                minionTimer = 0;
            }
            if (rangedMinionTimer >= (350 - ((this.getMaxHealth() - this.getHealth()) / 12))) {
                EntityType.SKELETON.spawn((ServerWorld) this.level, null, null, pos, SpawnReason.EVENT, true, false);
                rangedMinionTimer = 0;
            }
        }
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }


    @Override
    public void startSeenByPlayer(ServerPlayerEntity pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossInfo.addPlayer(pPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }


    public boolean canBeAffected(EffectInstance effect) {
        if (effect.getEffect() == Effects.POISON || effect.getEffect() == Effects.MOVEMENT_SLOWDOWN) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 1.75F;
    }

    static class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(VoidBossEntity voidBoss) {
            super(voidBoss, 1.0D, true);
        }


        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return (double) (1.5F + attackTarget.getBbWidth());
        }
    }

    static class TargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        public TargetGoal(VoidBossEntity voidBoss, Class<T> classTarget) {
            super(voidBoss, classTarget, true);
        }
    }
}
