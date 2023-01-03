package com.dkmk100.arsomega.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class EntityDemonRay extends FlyingMob implements Enemy, IAnimatable {
    public EntityDemonRay(EntityType<? extends FlyingMob> type, Level world) {
        super(type,world);
        this.xpReward = 5;
        this.moveControl = new RayMoveControl(this);
    }

    Vec3 moveTargetPoint = Vec3.ZERO;
    BlockPos anchorPoint = BlockPos.ZERO;

    private AnimationFactory factory = new AnimationFactory(this);

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33126_, DifficultyInstance p_33127_, MobSpawnType type, @Nullable SpawnGroupData p_33129_, @Nullable CompoundTag p_33130_) {
        if(type == MobSpawnType.SPAWN_EGG) {
            this.anchorPoint = this.blockPosition().above(5);
        }
        else{
            this.anchorPoint = new BlockPos(this.blockPosition().getX(), Math.min(75,Math.max(65,this.blockPosition().getY() + 5)),this.blockPosition().getZ());
        }
        return super.finalizeSpawn(p_33126_, p_33127_, type, p_33129_, p_33130_);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(this.getDeltaMovement().y <= 0)
        {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("glide.animation", true));
        }
        else{
            event.getController().setAnimation(new AnimationBuilder().addAnimation("fly.animation", true));
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3,new CircleAnchorGoal());
        super.registerGoals();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE,10)
                .add(Attributes.FOLLOW_RANGE,32);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<EntityDemonRay>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    abstract class FlyingTargetGoal extends Goal {
        public FlyingTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return EntityDemonRay.this.moveTargetPoint.distanceToSqr(EntityDemonRay.this.getX(), EntityDemonRay.this.getY(), EntityDemonRay.this.getZ()) < 4.0D;
        }
    }

    class CircleAnchorGoal extends EntityDemonRay.FlyingTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        public boolean canUse() {
            return true;
            //return EntityDemonRay.this.getTarget() == null;
        }

        public void start() {
            this.distance = 5.0F + EntityDemonRay.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + EntityDemonRay.this.random.nextFloat() * 9.0F;
            this.clockwise = EntityDemonRay.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick() {
            if (EntityDemonRay.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + EntityDemonRay.this.random.nextFloat() * 9.0F;
            }

            if (EntityDemonRay.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (EntityDemonRay.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = EntityDemonRay.this.random.nextFloat() * 2.0F * (float)Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (EntityDemonRay.this.moveTargetPoint.y < EntityDemonRay.this.getY() && !EntityDemonRay.this.level.isEmptyBlock(EntityDemonRay.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (EntityDemonRay.this.moveTargetPoint.y > EntityDemonRay.this.getY() && !EntityDemonRay.this.level.isEmptyBlock(EntityDemonRay.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(EntityDemonRay.this.anchorPoint)) {
                EntityDemonRay.this.anchorPoint = EntityDemonRay.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float)Math.PI / 180F);
            EntityDemonRay.this.moveTargetPoint = Vec3.atLowerCornerOf(EntityDemonRay.this.anchorPoint).add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
        }
    }

    class RayMoveControl extends MoveControl {
        private float speed = 0.1F;

        public RayMoveControl(Mob p_33241_) {
            super(p_33241_);
        }

        public void tick() {
            if (EntityDemonRay.this.horizontalCollision) {
                EntityDemonRay.this.setYRot(EntityDemonRay.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double d0 = EntityDemonRay.this.moveTargetPoint.x - EntityDemonRay.this.getX();
            double d1 = EntityDemonRay.this.moveTargetPoint.y - EntityDemonRay.this.getY();
            double d2 = EntityDemonRay.this.moveTargetPoint.z - EntityDemonRay.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > (double)1.0E-5F) {
                double d4 = 1.0D - Math.abs(d1 * (double)0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = EntityDemonRay.this.getYRot();
                float f1 = (float) Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(EntityDemonRay.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180F / (float)Math.PI));
                EntityDemonRay.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                EntityDemonRay.this.yBodyRot = EntityDemonRay.this.getYRot();
                if (Mth.degreesDifferenceAbs(f, EntityDemonRay.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }

                float f4 = (float)(-(Mth.atan2(-d1, d3) * (double)(180F / (float)Math.PI)));
                EntityDemonRay.this.setXRot(f4);
                float f5 = EntityDemonRay.this.getYRot() + 90.0F;
                double d6 = (double)(this.speed * Mth.cos(f5 * ((float)Math.PI / 180F))) * Math.abs(d0 / d5);
                double d7 = (double)(this.speed * Mth.sin(f5 * ((float)Math.PI / 180F))) * Math.abs(d2 / d5);
                double d8 = (double)(this.speed * Mth.sin(f4 * ((float)Math.PI / 180F))) * Math.abs(d1 / d5);
                Vec3 vec3 = EntityDemonRay.this.getDeltaMovement();
                EntityDemonRay.this.setDeltaMovement(vec3.add((new Vec3(d6, d8, d7)).subtract(vec3).scale(0.2D)));
            }

        }
    }
}
