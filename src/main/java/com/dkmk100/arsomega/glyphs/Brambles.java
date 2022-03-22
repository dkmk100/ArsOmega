package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class Brambles extends AbstractEffect {
    public static Brambles INSTANCE = new Brambles("brambles", "Brambles");
    public Brambles(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            float amp = (float) spellStats.getAmpMultiplier();
            float damage = (float)(3.0 + 0.5 * amp);
            this.dealDamage(world,shooter,damage,spellStats,living, DamageSource.CACTUS);
        }
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (world instanceof ServerWorld) {
            BlockPos pos = rayTraceResult.getBlockPos();
            BlockPos hitPos = rayTraceResult.isInside() ? pos : pos.relative(rayTraceResult.getDirection());
            float amp = (float) spellStats.getAmpMultiplier();
            if(amp>=8){
                world.setBlockAndUpdate(hitPos, RegistryHandler.BRAMBLE_4.get().defaultBlockState());
            }
            else if(amp>=4){
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
    public int getManaCost() {
        return 80;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE});
    }

    @Override
    public ISpellTier.Tier getTier() {
        return ISpellTier.Tier.ONE;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.NATURE});
    }
}
