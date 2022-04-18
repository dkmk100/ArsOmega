package com.dkmk100.arsomega.entities;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class EntityUtil {
    static class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(PathfinderMob monster) {
            super(monster, 1.0D, true);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return (double)(2.0F + attackTarget.getBbWidth());
        }
    }

    static class TargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        public TargetGoal(Mob monster, Class<T> classTarget) {
            super(monster, classTarget, true);
        }
    }
}
