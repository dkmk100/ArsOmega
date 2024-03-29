package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Hellfire extends TierFourEffect implements IDamageEffect {

    private static DamageSource HELLFIRE = new DamageSource("hellfire").setIsFire();

    public static DamageSource makeHellfire(LivingEntity shooter){
        if(shooter == null){
            return HELLFIRE;
        }
        return new EntityDamageSource("hellfire",shooter).setIsFire();
    }

    public static Hellfire INSTANCE = new Hellfire("hellfire", "hellfire");

    public Hellfire(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        double amp = spellStats.getAmpMultiplier() + 2;
        int time = 20 + (int) Math.round(10 * spellStats.getDurationMultiplier());

        if(rayTraceResult.getEntity() instanceof LivingEntity){
            LivingEntity living = (LivingEntity)rayTraceResult.getEntity();
            living.setRemainingFireTicks(living.getRemainingFireTicks()+time);
            living.addEffect(new MobEffectInstance(ModPotions.BURNED.get(),20));
        }
        this.attemptDamage(world,shooter,spellStats,spellContext,resolver,rayTraceResult.getEntity(), makeHellfire(shooter), (float)amp*3f);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!spellStats.hasBuff(AugmentSensitive.INSTANCE)) {
            if (world.getBlockState(rayTraceResult.getBlockPos().above()).getMaterial().isReplaceable()) {
                Direction face = rayTraceResult.getDirection();
                double aoe = spellStats.getAoeMultiplier() * 2 + 4;
                Iterator var7 = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, aoe,0).iterator();

                while(var7.hasNext()) {
                    BlockPos pos = (BlockPos)var7.next();
                    BlockPos blockpos1 = pos.relative(face);
                    if (BaseFireBlock.canBePlacedAt(world, blockpos1, face) && BlockUtil.destroyRespectsClaim(this.getPlayer(shooter, (ServerLevel)world), world, blockpos1)) {
                        BlockState blockstate1 = BaseFireBlock.getState(world, blockpos1);
                        world.setBlock(blockpos1, blockstate1, 11);
                    }
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
        return 800;
    }

    @Override
    public String getBookDescription() {
        return "Corrodes blocks and damages entities";
    }
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE,AugmentAOE.INSTANCE,AugmentSensitive.INSTANCE, AugmentFortune.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_FIRE});
    }
}

