package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityWitherBound;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class WitherSummon extends TierFourEffect {

    public static WitherSummon INSTANCE = new WitherSummon("wither_summon", "Wither Summon");

    public WitherSummon(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(world instanceof ServerLevel && this.canSummon(shooter)) {
            Vec3 vector3d = this.safelyGetHitPos(rayTraceResult);
            BlockPos pos = new BlockPos(vector3d);
            if(spellStats.hasBuff(CursedBind.INSTANCE)) {

                //to nerf wither minion
                double amp = spellStats.getAmpMultiplier();
                double amp2 = 0;//runnoff, currently unused but might add extra attack power or something later
                if (amp > 10) {
                    amp2 = amp-10;
                    amp = 10;
                }
                float healthPercent = (float) ((amp + 1) / 11);
                //spawn bound wither
                int ticks = (int) ((20.0 * 45) + 160 * spellStats.getDurationMultiplier());
                BlockPos blockpos = pos.offset(-2 + shooter.getRandom().nextInt(5), 2, -2 + shooter.getRandom().nextInt(5));
                EntityWitherBound wither = new EntityWitherBound(world, shooter);
                wither.moveTo(blockpos, 0.0F, 0.0F);
                wither.finalizeSpawn((ServerLevelAccessor) world, world.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                wither.setHealth(wither.getMaxHealth() * healthPercent);
                wither.setOwner(shooter);
                wither.setLimitedLife(ticks);
                this.summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, wither);
                shooter.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS.get(), ticks));
                if(shooter instanceof ServerPlayer) {
                    RegistryHandler.USE_CURSED_BIND.Trigger((ServerPlayer) shooter);
                }
            }
            else {
                double amp = spellStats.getAmpMultiplier();
                if (amp > 10) {
                    amp = 10;
                }
                //always at least half to make nether star farming not too easy
                float healthPercent = 0.5f + (float) ((amp + 1) / 21);
                PathfinderMob test = (PathfinderMob) EntityType.WITHER.spawn((ServerLevel) world, null, null, pos, MobSpawnType.MOB_SUMMONED, true, false);
                test.setHealth(test.getMaxHealth() * healthPercent);
                world.addFreshEntity(test);
                shooter.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS_EFFECT.get(),
 12000));
            }
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
        return augmentSetOf(AugmentAmplify.INSTANCE,CursedBind.INSTANCE, AugmentExtendTime.INSTANCE);
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.DEMONIC,SpellSchools.CONJURATION});
    }
}
