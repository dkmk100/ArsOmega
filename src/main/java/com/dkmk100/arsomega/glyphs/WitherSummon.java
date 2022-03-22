package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
            double amp = spellStats.getAmpMultiplier();
            if(amp > 10){
                amp = 10;
            }
            float healthPercent = (float) ((amp+1)/11);
            CreatureEntity test = (CreatureEntity) EntityType.WITHER.spawn((ServerWorld)world,null,null,pos, SpawnReason.MOB_SUMMONED,true,false);
            test.setHealth(test.getMaxHealth() * healthPercent);
            world.addFreshEntity(test);
            shooter.addEffect(new EffectInstance(ModPotions.SUMMONING_SICKNESS, 12000));
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
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE);
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.DEMONIC,SpellSchools.CONJURATION});
    }
}
