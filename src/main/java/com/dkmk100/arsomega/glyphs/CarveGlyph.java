package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CarveGlyph extends AbstractEffect {
    public static CarveGlyph INSTANCE = new CarveGlyph("carve", "Carve");
    public CarveGlyph(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }


    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (world instanceof ServerLevel) {
            BlockPos pos = rayTraceResult.getBlockPos();
            Block block = world.getBlockState(pos).getBlock();
            BlockState stripped = AxeItem.getAxeStrippingState(world.getBlockState(pos));
            if(stripped!=null){
                world.setBlockAndUpdate(pos, stripped);
            }
            else if (block == Blocks.PUMPKIN) {
                world.setBlockAndUpdate(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING,getSidewaysDirection(rayTraceResult)));
            }
            else if (block == RegistryHandler.MAGIC_CLAY_BLOCK.get()) {
                world.setBlockAndUpdate(pos, RegistryHandler.MAGIC_CLAY_CARVED.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING,getSidewaysDirection(rayTraceResult)));
            }
            else if (block == RegistryHandler.MARVELOUS_CLAY_BLOCK.get()) {
                world.setBlockAndUpdate(pos, RegistryHandler.MARVELOUS_CLAY_CARVED.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING,getSidewaysDirection(rayTraceResult)));
            }
            else if (block == RegistryHandler.MYSTIC_CLAY_BLOCK.get()) {
                world.setBlockAndUpdate(pos, RegistryHandler.MYSTIC_CLAY_CARVED.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING,getSidewaysDirection(rayTraceResult)));
            }
            else if (block == RegistryHandler.ARCANE_CLAY_BLOCK.get()) {
                world.setBlockAndUpdate(pos, RegistryHandler.ARCANE_CLAY_CARVED.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING,getSidewaysDirection(rayTraceResult)));
            }

        }
    }

    Direction getSidewaysDirection(BlockHitResult rayTrace){
        Direction dir = rayTrace.getDirection().getOpposite().getOpposite();
        if(dir == Direction.UP || dir == Direction.DOWN){
            dir = Direction.NORTH;
        }
        return dir;
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
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.NATURE});
    }
}
