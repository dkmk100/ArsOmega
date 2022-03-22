package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.ItemsRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.util.ISlotHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class DiamondGlyph extends TierFourEffect {

    public static DiamondGlyph INSTANCE = new DiamondGlyph("diamond", "Diamond");

    public DiamondGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if((world instanceof ServerWorld) && rayTraceResult instanceof BlockRayTraceResult) {
            BlockPos pos = new BlockPos(((BlockRayTraceResult) rayTraceResult).getBlockPos());
            double amp = spellStats.getAmpMultiplier();

            if(CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS_ADVANCED,shooter).isPresent()){
                amp+=10;
            }
            else if(CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS,shooter).isPresent()){
                amp+=5;
            }

            BlockState state = Blocks.DIRT.defaultBlockState();
            if(amp > 30){
                state = Blocks.NETHERITE_BLOCK.defaultBlockState();
            }
            else if(amp > 25){
                state = Blocks.DIAMOND_BLOCK.defaultBlockState();
            }
            else if(amp > 15){
                state = Blocks.EMERALD_BLOCK.defaultBlockState();
            }
            else if(amp>10){
                state = Blocks.GOLD_BLOCK.defaultBlockState();
            }
            else if(amp>7){
                state = Blocks.REDSTONE_BLOCK.defaultBlockState();
            }
            else if(amp>4){
                state = Blocks.IRON_BLOCK.defaultBlockState();
            }
            else if(amp>1){
                state = Blocks.IRON_ORE.defaultBlockState();
            }
            else if(amp>0){
                state = Blocks.COAL_BLOCK.defaultBlockState();
            }
            else if(amp>-1){
                state = Blocks.COAL_ORE.defaultBlockState();
            }
            world.setBlockAndUpdate(pos,state);
        }
    }

    @Override
    public int getManaCost() {
        return 500;
    }

    @Override
    public String getBookDescription() {
        return "Converts a block into a valuable mineral, add levels of amplify to improve the material.";
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
