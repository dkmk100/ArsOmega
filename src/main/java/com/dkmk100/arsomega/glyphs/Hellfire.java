package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
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
import java.util.Set;

public class Hellfire extends TierFourEffect {

    public static DamageSource HELLFIRE = new DamageSource("hellfire").setIsFire();

    public static Hellfire INSTANCE = new Hellfire("hellfire", "hellfire");

    public Hellfire(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        double amp = spellStats.getAmpMultiplier() + 4;
        int time = spellStats.getBuffCount(AugmentExtendTime.INSTANCE);

        if(rayTraceResult.getEntity() instanceof LivingEntity){
            LivingEntity living = (LivingEntity)rayTraceResult.getEntity();
            living.setRemainingFireTicks(living.getRemainingFireTicks()+20+10*time);
            living.addEffect(new MobEffectInstance(ModPotions.BURNED,20));
        }
        this.dealDamage(world,shooter,(float)amp*3f,spellStats,rayTraceResult.getEntity(),HELLFIRE);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (!spellStats.hasBuff(AugmentSensitive.INSTANCE)) {
            if (world.getBlockState(rayTraceResult.getBlockPos().above()).getMaterial().isReplaceable()) {
                Direction face = rayTraceResult.getDirection();
                int aoe = spellStats.getBuffCount(AugmentAOE.INSTANCE) * 2 + 4;
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

