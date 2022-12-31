package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class Dispellant extends AbstractEffect {

    public static Dispellant INSTANCE = new Dispellant("dispellant", "Dispellant");

    public Dispellant(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            //low time cause the effect is sorta OP lol
            this.applyPotion(living, ModPotions.DISPELLANT, spellStats, 6,3,true);
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 800;
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
        return this.setOf(new SpellSchool[]{SpellSchools.ABJURATION,Schools.ALCHEMY});
    }
}
