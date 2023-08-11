package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.IPotionEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public interface ILimitedPotion extends IPotionEffect {
    default void applyLimitedEffect(LivingEntity living, MobEffect potionEffect, SpellStats stats, int maxLevel){
        int ticks = getBaseDuration() * 20 + getExtendTimeDuration() * stats.getDurationInTicks();
        int amp = (int)stats.getAmpMultiplier();
        amp = Math.min(amp,maxLevel);
        amp = Math.max(amp,0);
        living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, ticks, amp, false, true, true));
    }
}
