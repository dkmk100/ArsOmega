package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.entities.EntityWitherBound;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityAllyVex;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class WitherSummon extends TierFourEffect {

    public static WitherSummon INSTANCE = new WitherSummon("wither_summon", "Wither Summon");

    public WitherSummon(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(world instanceof ServerWorld && this.canSummon(shooter)) {
            Vector3d vector3d = this.safelyGetHitPos(rayTraceResult);
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
                int ticks = (int) ((20.0 * 20) + 60 * spellStats.getDurationMultiplier());
                BlockPos blockpos = pos.offset(-2 + shooter.getRandom().nextInt(5), 2, -2 + shooter.getRandom().nextInt(5));
                EntityWitherBound wither = null;
                wither = new EntityWitherBound(world, shooter);
                wither.moveTo(blockpos, 0.0F, 0.0F);
                wither.finalizeSpawn((IServerWorld) world, world.getCurrentDifficultyAt(blockpos), SpawnReason.MOB_SUMMONED, (ILivingEntityData) null, (CompoundNBT) null);
                wither.setHealth(wither.getMaxHealth() * healthPercent);
                wither.setOwner(shooter);
                //wither.setBoundOrigin(blockpos);
                wither.setLimitedLife(ticks);
                this.summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, wither);
                shooter.addEffect(new EffectInstance(ModPotions.SUMMONING_SICKNESS, ticks));
            }
            else {
                double amp = spellStats.getAmpMultiplier();
                if (amp > 10) {
                    amp = 10;
                }
                //always at least half to make nether star farming not too easy
                float healthPercent = 0.5f + (float) ((amp + 1) / 21);
                CreatureEntity test = (CreatureEntity) EntityType.WITHER.spawn((ServerWorld) world, null, null, pos, SpawnReason.MOB_SUMMONED, true, false);
                test.setHealth(test.getMaxHealth() * healthPercent);
                world.addFreshEntity(test);
                shooter.addEffect(new EffectInstance(ModPotions.SUMMONING_SICKNESS, 12000));
            }
        }
    }

    @Override
    public int getManaCost() {
        return 2000;
    }

    @Override
    public String getBookDescription() {
        return "Summons the wither, add cursed bind to force it to obey you.";
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addGenericInt(builder, 15, "Base duration in seconds", "duration");
        this.addExtendTimeConfig(builder, 10);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE,CursedBind.INSTANCE);
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.DEMONIC,SpellSchools.CONJURATION});
    }
}
