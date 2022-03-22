package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.conditions.BlockStateProperty;
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PoisonFlowerGlyph extends AbstractEffect {
    public static PoisonFlowerGlyph INSTANCE = new PoisonFlowerGlyph("poison_flower", "Poison Flower");
    public PoisonFlowerGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            float amp = (float) spellStats.getAmpMultiplier();
            float damage = (float)(2.0 + 0.5 * amp);
            this.dealDamage(world,shooter,damage,spellStats,living, DamageSource.CACTUS);
            living.addEffect(new EffectInstance(Effects.POISON, 100 + 20*Math.round(amp)));
        }
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (world instanceof ServerWorld) {
            BlockPos pos = rayTraceResult.getBlockPos();
            float amp = (float) spellStats.getAmpMultiplier();
            Block block = world.getBlockState(pos).getBlock();
            if (block == Blocks.GRASS_BLOCK || net.minecraftforge.common.Tags.Blocks.DIRT.contains(block)) {
                BlockPos above = pos.above();
                Block block2 = world.getBlockState(above).getBlock();
                if (block2 == Blocks.AIR) {
                    world.setBlockAndUpdate(above,RegistryHandler.POISON_FLOWER.get().defaultBlockState().setValue(BlockStateProperties.LEVEL,Math.min(Math.round(amp),15)));
                }
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
    public Tier getTier() {
        return Tier.ONE;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.NATURE});
    }
}
