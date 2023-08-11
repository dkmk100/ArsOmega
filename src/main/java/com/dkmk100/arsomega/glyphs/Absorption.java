package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class Absorption extends AbstractEffect implements IPotionEffect {

    public static Absorption INSTANCE = new Absorption("absorption", "Absorption");

    public Absorption(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
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

            int focusLevel = 0;
            if(shooter!=null) {
                if (CuriosApi.getCuriosHelper().findFirstCurio(shooter, RegistryHandler.FOCUS_OF_LIFE.get()).isPresent()) {
                    focusLevel = 1;
                }
            }



            int ticks = getBaseDuration() * 20 + getExtendTimeDuration() * spellStats.getDurationInTicks();
            int amp = (int)spellStats.getAmpMultiplier() + 2 * focusLevel;
            living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, ticks, amp, false, true, true));

        }
    }

    @Override
    public int getDefaultManaCost() {
        return 400;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 4);
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
        return 45;
    }

    @Override
    public int getExtendTimeDuration() {
        return 35;
    }
}
