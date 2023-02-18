package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class PoisonFlowerGlyph extends AbstractEffect implements IDamageEffect {
    public static PoisonFlowerGlyph INSTANCE = new PoisonFlowerGlyph("poison_flower", "Poison Flower");
    public PoisonFlowerGlyph(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            float amp = (float) spellStats.getAmpMultiplier();
            float damage = (float)(2.0 + 0.5 * amp);
            this.attemptDamage(world,shooter,spellStats,spellContext,resolver,living,new EntityDamageSource(DamageSource.CACTUS.getMsgId(),shooter), damage);
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 100 + 20*Math.round(amp)));
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
            float amp = (float) spellStats.getAmpMultiplier();
            Block block = world.getBlockState(pos).getBlock();
            if (block == Blocks.GRASS_BLOCK || ForgeRegistries.BLOCKS.tags().getTag(BlockTags.DIRT).contains(block)) {
                BlockPos above = pos.above();
                Block block2 = world.getBlockState(above).getBlock();
                if (block2 == Blocks.AIR) {
                    world.setBlockAndUpdate(above,RegistryHandler.POISON_FLOWER.get().defaultBlockState().setValue(BlockStateProperties.LEVEL,Math.min(Math.round(amp * 3),15)));
                }
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
    public SpellTier getTier() {
        return SpellTier.ONE;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.NATURE});
    }
}
