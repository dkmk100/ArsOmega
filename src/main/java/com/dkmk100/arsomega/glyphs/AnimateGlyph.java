package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.base_blocks.MagicAnimatable;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class AnimateGlyph extends AbstractEffect {
    public static AnimateGlyph INSTANCE = new AnimateGlyph("animate_block", "Animate");
    public AnimateGlyph(String tag, String description) {
        super(tag, description);
    }


    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (world instanceof ServerWorld) {
            BlockPos pos = rayTraceResult.getBlockPos();
            Block block = world.getBlockState(pos).getBlock();
            if(block instanceof MagicAnimatable){
                MagicAnimatable anim = (MagicAnimatable) block;
                anim.Animate(pos,(ServerWorld)world);
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
        return this.augmentSetOf(new AbstractAugment[]{});
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.NATURE});
    }
}
