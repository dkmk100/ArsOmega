package com.dkmk100.arsomega.entities;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;

public class EntityUtil {
    static class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(CreatureEntity monster) {
            super(monster, 1.0D, true);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return (double)(2.0F + attackTarget.getBbWidth());
        }
    }

    static class TargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        public TargetGoal(MobEntity monster, Class<T> classTarget) {
            super(monster, classTarget, true);
        }
    }
}
