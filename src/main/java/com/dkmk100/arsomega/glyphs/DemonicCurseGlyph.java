package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.resources.ResourceLocation;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DemonicCurseGlyph extends TierFourEffect implements IPotionEffect {

    public static DemonicCurseGlyph INSTANCE = new DemonicCurseGlyph("demonic_curse", "Demonic Curse");

    public DemonicCurseGlyph(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;

            //capped by amp limit
            this.applyConfigPotion(living, ModPotions.DEMONIC_CURSE.get(), spellStats);
        }
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (world instanceof ServerLevel) {
            if(spellStats.hasBuff(AugmentSensitive.INSTANCE)) {
                double aoeBuff = spellStats.getAoeMultiplier();
                BlockPos pos = rayTraceResult.getBlockPos();
                List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, aoeBuff, 0);

                Iterator var12 = posList.iterator();
                while (var12.hasNext()) {
                    BlockPos pos1 = (BlockPos) var12.next();
                    Block block = world.getBlockState(pos1).getBlock();
                    if (ForgeRegistries.BLOCKS.tags().getTag(BlockTags.DIRT).contains(block)) {
                        world.setBlock(pos1, RegistryHandler.CURSED_EARTH.get().defaultBlockState(), 3);
                    } else if (block == Blocks.GRASS_BLOCK) {
                        world.setBlock(pos1, RegistryHandler.CURSED_EARTH.get().defaultBlockState(), 3);
                    } else if (block == Blocks.SOUL_SAND || block == Blocks.SOUL_SOIL) {
                        world.setBlock(pos1, RegistryHandler.VENGEFUL_SOUL_SAND.get().defaultBlockState(), 3);
                    } else if (ForgeRegistries.BLOCKS.tags().getTag(BlockTags.SAND).contains(block)) {
                        world.setBlock(pos1, RegistryHandler.VENGEFUL_SOUL_SAND.get().defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    @Override
    public int getBaseDuration() {
        return 5;
    }

    @Override
    public int getExtendTimeDuration() {
        return 5;
    }

    @Override
    public int getDefaultManaCost() {
        return 400;
    }

    @Override
    public String getBookDescription() {
        return "A powerful harming effect that is almost impossible to cure";
    }
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        Set potionAugments =  this.getPotionAugments();
        ArrayList<AbstractAugment> list = new ArrayList<AbstractAugment>(potionAugments);
        list.add(AugmentAOE.INSTANCE);
        list.add(AugmentSensitive.INSTANCE);
        return Collections.unmodifiableSet(new HashSet(list));
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.DEMONIC});
    }

}
