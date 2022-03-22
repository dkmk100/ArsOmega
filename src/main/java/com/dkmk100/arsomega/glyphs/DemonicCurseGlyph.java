package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DemonicCurseGlyph extends TierFourEffect {

    public static DemonicCurseGlyph INSTANCE = new DemonicCurseGlyph("demonic_curse", "Demonic Curse");

    public DemonicCurseGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            this.applyPotionWithCap(living, ModPotions.DEMONIC_CURSE, spellStats,5,5,4);
        }
    }

    @Override
    public int getManaCost() {
        return 400;
    }

    @Override
    public String getBookDescription() {
        return "A powerful harming effect that is almost impossible to cure";
    }
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return POTION_AUGMENTS;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.DEMONIC});
    }

}
