package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class Soulfire extends AbstractEffect {

    public static Soulfire INSTANCE = new Soulfire("soulfire", "soulfire");

    public Soulfire(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        double amp = spellStats.getAmpMultiplier() + 1;
        int time = spellStats.getBuffCount(AugmentExtendTime.INSTANCE);

        if(rayTraceResult.getEntity() instanceof LivingEntity){
            LivingEntity living = (LivingEntity)rayTraceResult.getEntity();
            living.addEffect(new EffectInstance(ModPotions.SOUL_FIRE,80 + 40*time));
            living.setRemainingFireTicks(living.getRemainingFireTicks()+40+20*time);
        }
    }

    @Override
    public int getManaCost() {
        return 300;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Override
    public String getBookDescription() {
        return "Corrodes blocks and damages entities";
    }
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_FIRE});
    }
}

