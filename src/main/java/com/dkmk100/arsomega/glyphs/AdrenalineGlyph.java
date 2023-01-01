package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class AdrenalineGlyph extends AbstractEffect {
    public static AdrenalineGlyph INSTANCE = new AdrenalineGlyph("adrenaline", "Adrenaline");

    public AdrenalineGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public int getDefaultManaCost() {
        return 300;
    }
    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            int amp = (int)Math.round(spellStats.getAmpMultiplier());
            living.addEffect(new MobEffectInstance(ModPotions.ADRENALINE, 200 + 100*amp));
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200 + 100*amp,2));
            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200 + 100*amp,2));
            living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200 + 100*amp));
            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200 + 100*amp,2));
            living.addEffect(new MobEffectInstance(MobEffects.JUMP, 200 + 100*amp,2));
            living.addEffect(new MobEffectInstance(ModPotions.ADRENALINE, 200 + 100*amp));
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40 + 20*amp));
        }
    }

    @Override
    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE});
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.LIFE});
    }
}

