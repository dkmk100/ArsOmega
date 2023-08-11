package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class Regen extends AbstractEffect implements ILimitedPotion {

    public static Regen INSTANCE = new Regen("regen", "Regen");

    public Regen(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;

            int focusLevel = 0;
            if(shooter!=null) {
                if (CuriosApi.getCuriosHelper().findFirstCurio(shooter, RegistryHandler.FOCUS_OF_LIFE.get()).isPresent()) {
                    focusLevel = 1;
                }
            }

            this.applyLimitedEffect(living, MobEffects.REGENERATION, spellStats, 2 + focusLevel); //TODO: focus of life affects this
        }
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 3);
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

    @Override
    public int getBaseDuration() {
        return 30;
    }

    @Override
    public int getExtendTimeDuration() {
        return 15;
    }
}