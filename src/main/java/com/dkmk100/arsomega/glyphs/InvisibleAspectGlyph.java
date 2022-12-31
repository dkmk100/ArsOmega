/*
package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import DamageSource;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class InvisibleAspectGlyph extends AbstractEffect {

    public static final DamageSource HEARTSTOP_DAMAGE = (new DamageSource("heartstop")).bypassArmor().bypassMagic();

    public static Heartstop INSTANCE = new Heartstop("heartstop", "Heartstop");

    public InvisibleAspectGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public int getSourceCost() {
        return 650;
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {

    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentFortune.INSTANCE});
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.LIFE});
    }
}
 */
