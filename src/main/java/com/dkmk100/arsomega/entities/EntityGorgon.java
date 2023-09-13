package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;

public class EntityGorgon extends Monster implements IAnimatable {
    public EntityGorgon(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 8;

    }

    private AnimationFactory factory = new AnimationFactory(this);

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new EntityUtil.AttackGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new EntityUtil.TargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(3, new EntityUtil.TargetGoal<>(this, IronGolem.class));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 10)
                .add(Attributes.FOLLOW_RANGE, 24);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource p_217055_, DifficultyInstance p_217056_) {
        super.populateDefaultEquipmentSlots(p_217055_, p_217056_);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
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
        return new GroundPathNavigation(this, worldIn);
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
        return 1.6F;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.gorgon.walk", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<EntityGorgon>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
