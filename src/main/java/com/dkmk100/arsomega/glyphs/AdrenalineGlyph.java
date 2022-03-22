package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class AdrenalineGlyph extends AbstractEffect {
    public static AdrenalineGlyph INSTANCE = new AdrenalineGlyph("adrenaline", "Adrenaline");

    public AdrenalineGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public int getManaCost() {
        return 300;
    }
    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            int amp = (int)Math.round(spellStats.getAmpMultiplier());
            living.addEffect(new EffectInstance(ModPotions.ADRENALINE, 200 + 100*amp));
            living.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 200 + 100*amp,2));
            living.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 200 + 100*amp,2));
            living.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 200 + 100*amp));
            living.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 200 + 100*amp,2));
            living.addEffect(new EffectInstance(Effects.JUMP, 200 + 100*amp,2));
            living.addEffect(new EffectInstance(ModPotions.ADRENALINE, 200 + 100*amp));
            living.addEffect(new EffectInstance(Effects.CONFUSION, 40 + 20*amp));
        }
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE});
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.LIFE});
    }
}

