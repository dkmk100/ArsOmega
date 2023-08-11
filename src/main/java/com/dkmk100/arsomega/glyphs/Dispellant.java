package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class Dispellant extends AbstractEffect implements ILimitedPotion {

    public static Dispellant INSTANCE = new Dispellant("dispellant", "Dispellant");

    public Dispellant(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;

            //limit to first level
            this.applyLimitedEffect(living,  ModPotions.DISPELLANT.get(), spellStats, 0);
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

    @Override
    public int getBaseDuration() {
        return 6;
    }

    //low time cause the effect is sorta OP lol
    @Override
    public int getExtendTimeDuration() {
        return 3;
    }
}
