package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ForgeConfigSpec;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class DiamondGlyph extends TierFourEffect implements ConfigurableGlyph {

    public static DiamondGlyph INSTANCE = new DiamondGlyph("diamond", "Diamond");

    ForgeConfigSpec.IntValue maxTier;
    ForgeConfigSpec.IntValue focusBonus;
    ForgeConfigSpec.IntValue advancedFocusBonus;
    ForgeConfigSpec.IntValue extraAmpCost;

    public DiamondGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if ((world instanceof ServerLevel) && rayTraceResult instanceof BlockHitResult) {
            BlockPos pos = new BlockPos(((BlockHitResult) rayTraceResult).getBlockPos());

            if (world.getBlockState(pos).getBlock() != RegistryHandler.ALCHEMICAL_STONE.get()) {
                return;
            }
            double amp = spellStats.getAmpMultiplier();
            int tier = maxTier.get();
            int cost = extraAmpCost.get();


            if (shooter != null && CuriosApi.getCuriosHelper().findFirstCurio(shooter, RegistryHandler.FOCUS_OF_ADVANCED_ALCHEMY.get()).isPresent()) {
                amp += Math.max(advancedFocusBonus.get(), focusBonus.get());
            } else if (shooter != null && CuriosApi.getCuriosHelper().findFirstCurio(shooter, RegistryHandler.FOCUS_OF_ALCHEMY.get()).isPresent()) {
                amp += focusBonus.get();
            }

            BlockState state = Blocks.DIRT.defaultBlockState();
            if (!canSummon(shooter)) {
                if (amp > 30 + cost && tier >= 7) {
                    state = Blocks.CHISELED_RED_SANDSTONE.defaultBlockState();
                } else if(amp > 20 && tier >= 6) {
                    state = Blocks.RED_SAND.defaultBlockState();
                }else if(amp > 16 && tier >= 4) {
                    state = Blocks.SANDSTONE.defaultBlockState();
                }
                else{
                    state = Blocks.SAND.defaultBlockState();
                }
            } else if (amp > 30 + cost && tier >= 7) {
                state = Blocks.ANCIENT_DEBRIS.defaultBlockState();
            } else if (amp > 25 + cost && tier >= 6) {
                state = RegistryHandler.ALCHEMICAL_DIAMOND_ORE.get().defaultBlockState();
            } else if (amp > 19 + cost && tier >= 5) {
                state = Blocks.EMERALD_BLOCK.defaultBlockState();
            } else if (amp > 13 + cost && tier >= 4) {
                state = Blocks.GOLD_ORE.defaultBlockState();
            } else if (amp > 12 + cost && tier >= 4) {
                state = Blocks.QUARTZ_BLOCK.defaultBlockState();
            }else if (amp > 10 + cost && tier >= 3) {
                state = Blocks.REDSTONE_ORE.defaultBlockState();
            } else if (amp > 7 + cost && tier >= 3) {
                state = Blocks.LAPIS_ORE.defaultBlockState();
            } else if (amp > 2 + cost && tier >= 2) {
                state = Blocks.IRON_ORE.defaultBlockState();
            } else if (amp > 1 + cost && tier >= 1) {
                state = Blocks.COPPER_ORE.defaultBlockState();
            } else if (amp > 0) {
                state = Blocks.COAL_BLOCK.defaultBlockState();
            } else if (amp > -1) {
                state = Blocks.COAL_ORE.defaultBlockState();
            }
            world.setBlockAndUpdate(pos, state);
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 650;
    }

    @Override
    public String getBookDescription() {
        return "Converts a block into a valuable mineral, add levels of amplify to improve the material.";
    }

    @Override
    public void buildExtraConfig(ForgeConfigSpec.Builder builder) {
        this.maxTier = builder.comment("The max block tier that can be made, anything above this will never be made regardless of power leve (amp amount plus bonuses). Tier 0 is coal, order goes coal, copper, iron, redstone, gold, emerald, diamond, and netherite").defineInRange("maxTier",6,0,10);
        this.extraAmpCost = builder.comment("The extra power level required for each tier, a linear value. Numbers larger than 5 or smaller than -5 can break progression, so be careful.").defineInRange("extraAmpCost",0,-20,20);
        this.focusBonus = builder.comment("How many levels of amplify the focus of alchemy is worth").defineInRange("focusBonus",5,0,20);
        this.advancedFocusBonus = builder.comment("How many levels of amplify the advanced focus of alchemy is worth. Should be higher than the normal focus value.").defineInRange("advancedFocusBonus",10,0,20);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.ALCHEMY});
    }
}
