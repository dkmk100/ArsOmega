package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
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

public class Heartstop extends AbstractEffect {

    public static final DamageSource HEARTSTOP_DAMAGE = new DamageSource("heartstop").bypassArmor().bypassMagic();

    public static Heartstop INSTANCE = new Heartstop("heartstop", "Heartstop");

    public Heartstop(String tag, String description) {
            super(tag, description);
        }

    @Override
    public int getManaCost() {
        return 550;
    }
    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            float damage = (float)(2.0 + 0.5 * spellStats.getAmpMultiplier());
            float mult = 0;
            float add = 0;
            if(living.hasEffect(ModPotions.DEMONIC_CURSE)){
                mult += 2;
                add += 4;
            }
            if(living.hasEffect(com.hollingsworth.arsnouveau.common.potions.ModPotions.SHOCKED_EFFECT)){
                mult += 3;
                int amp = living.getEffect(com.hollingsworth.arsnouveau.common.potions.ModPotions.SHOCKED_EFFECT).getAmplifier();
                mult += 1.5f * amp;
                add += 2 * amp;
            }
            if(living.hasEffect(com.hollingsworth.arsnouveau.common.potions.ModPotions.SNARE_EFFECT)){
                mult += 1f;
                add += 1;
            }
            damage += add*0.15f;
            damage = damage * (1.0f + 0.1f*mult);
            damage += add*0.85f;

            this.dealDamage(world,shooter,damage,spellStats,living, HEARTSTOP_DAMAGE);
            living.addEffect(new EffectInstance(Effects.CONFUSION, 120));
        }
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
