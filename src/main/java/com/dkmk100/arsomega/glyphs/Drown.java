package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class Drown extends TierFourEffect implements IDamageEffect {

    public static Drown INSTANCE = new Drown("drown", "drown");

    public Drown(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        double amp = spellStats.getAmpMultiplier() + 2;
        BlockPos pos = rayTraceResult.getEntity().blockPosition();
        int depth = 0;
        int i=0;
        while(i<15){
            pos = pos.above();
            if(world.getBlockState(pos).getBlock() == Blocks.WATER){
                depth+=1;
            }
            else{
                break;
            }
        }


        if(rayTraceResult.getEntity() instanceof LivingEntity){
            LivingEntity living = (LivingEntity)rayTraceResult.getEntity();
            if(!living.hasEffect(MobEffects.WATER_BREATHING)){
                if(living.isDamageSourceBlocked(DamageSource.DROWN)){
                    this.attemptDamage(world,shooter,spellStats,spellContext,resolver,living,DamageSource.MAGIC, (float) amp * 0.25f * depth);
                }
                else {
                    this.attemptDamage(world,shooter,spellStats,spellContext,resolver,living,DamageSource.DROWN, (float) amp * 0.5f * depth);
                }
            }
        }
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public int getDefaultManaCost() {
        return 700;
    }

    @Override
    public String getBookDescription() {
        return "Corrodes blocks and damages entities";
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_WATER});
    }
}
