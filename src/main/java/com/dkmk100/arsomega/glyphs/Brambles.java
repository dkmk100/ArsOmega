package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class Brambles extends AbstractEffect implements IDamageEffect {
    public static Brambles INSTANCE = new Brambles("brambles", "Brambles");
    public Brambles(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            float amp = (float) spellStats.getAmpMultiplier();
            float damage = (float)(3.0 + 0.5 * amp);
            this.attemptDamage(world,shooter,spellStats,spellContext,resolver,living,new EntityDamageSource(DamageSource.CACTUS.getMsgId(),shooter), damage);
        }
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (world instanceof ServerLevel) {
            BlockPos pos = rayTraceResult.getBlockPos();
            BlockPos hitPos = rayTraceResult.isInside() ? pos : pos.relative(rayTraceResult.getDirection());
            float amp = (float) spellStats.getAmpMultiplier();
            if(amp>=3){
                world.setBlockAndUpdate(hitPos, RegistryHandler.BRAMBLE_4.get().defaultBlockState());
            }
            else if(amp>=2){
                world.setBlockAndUpdate(hitPos, RegistryHandler.BRAMBLE_3.get().defaultBlockState());
            }
            else if(amp>=1){
                world.setBlockAndUpdate(hitPos, RegistryHandler.BRAMBLE_2.get().defaultBlockState());
            }
            else{
                world.setBlockAndUpdate(hitPos, RegistryHandler.BRAMBLE_1.get().defaultBlockState());
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 80;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE});
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.NATURE});
    }
}
