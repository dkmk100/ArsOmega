package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;

public class WitherSummon extends TierFourEffect {

    public static WitherSummon INSTANCE = new WitherSummon("wither_summon", "Wither Summon");

    public WitherSummon(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(world instanceof ServerLevel && this.canSummon(shooter)) {
            Vec3 vector3d = this.safelyGetHitPos(rayTraceResult);
            BlockPos pos = new BlockPos(vector3d);
            double amp = spellStats.getAmpMultiplier();
            if(amp > 10){
                amp = 10;
            }
            float healthPercent = (float) ((amp+1)/11);
            PathfinderMob test = (PathfinderMob) EntityType.WITHER.spawn((ServerLevel)world,null,null,pos, MobSpawnType.MOB_SUMMONED,true,false);
            test.setHealth(test.getMaxHealth() * healthPercent);
            world.addFreshEntity(test);
            shooter.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS, 12000));
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 2000;
    }

    @Override
    public String getBookDescription() {
        return "Summons the wither, add cursed bind to force it to obey you.";
    }
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE);
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.DEMONIC,SpellSchools.CONJURATION});
    }
}
