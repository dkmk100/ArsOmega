package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public class Absorption extends AbstractEffect {

    public static Absorption INSTANCE = new Absorption("absorption", "Absorption");

    public Absorption(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;

            Collection<MobEffectInstance> effects = living.getActiveEffects();
            MobEffectInstance[] array = (MobEffectInstance[])effects.toArray(new MobEffectInstance[0]);
            MobEffectInstance[] var9 = array;
            int var10 = array.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                MobEffectInstance e = var9[var11];
                if (e.getEffect() == MobEffects.ABSORPTION) {
                    living.removeEffect(e.getEffect());
                }
            }

            this.applyPotion(living, MobEffects.ABSORPTION, spellStats, 45,35,true);
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 600;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.getPotionAugments();
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ABJURATION,Schools.LIFE});
    }
}
