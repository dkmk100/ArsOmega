package com.dkmk100.arsomega.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VoidBeastEntity extends MonsterEntity{
    public VoidBeastEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
        this.xpReward = 12;
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(4, new VoidBeastEntity.AttackGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 16.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new VoidBeastEntity.TargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.addGoal(3, new VoidBeastEntity.TargetGoal<>(this, IronGolemEntity.class));
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.ATTACK_DAMAGE,7)
                .add(Attributes.FOLLOW_RANGE,24);
    }

    @Override
    protected PathNavigator createNavigation(World worldIn) {
        return new GroundPathNavigator(this, worldIn);
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

    public boolean canBeAffected(EffectInstance effect) {
        if (effect.getEffect() == Effects.POISON) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {return 1.25F;}

    static class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(VoidBeastEntity voidBeast) {
            super(voidBeast, 1.0D, true);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return (double)(2.0F + attackTarget.getBbWidth());
        }
    }

    static class TargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        public TargetGoal(VoidBeastEntity voidBeast, Class<T> classTarget) {
            super(voidBeast, classTarget, true);
        }
    }
}
