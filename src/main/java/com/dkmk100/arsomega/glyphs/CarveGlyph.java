package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
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

public class CarveGlyph extends AbstractEffect {
    public static CarveGlyph INSTANCE = new CarveGlyph("carve", "Carve");
    public CarveGlyph(String tag, String description) {
        super(tag, description);
    }


    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (world instanceof ServerWorld) {
            BlockPos pos = rayTraceResult.getBlockPos();
            Block block = world.getBlockState(pos).getBlock();
            BlockState stripped = AxeItem.getAxeStrippingState(world.getBlockState(pos));
            if(stripped!=null){
                world.setBlockAndUpdate(pos, stripped);
            }
            if (block == Blocks.PUMPKIN) {

                world.setBlockAndUpdate(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(HorizontalBlock.FACING,rayTraceResult.getDirection().getOpposite().getOpposite()));
            }
            else if (block == RegistryHandler.MAGIC_CLAY_BLOCK.get()) {
                world.setBlockAndUpdate(pos, RegistryHandler.MAGIC_CLAY_CARVED.get().defaultBlockState().setValue(HorizontalBlock.FACING,rayTraceResult.getDirection().getOpposite().getOpposite()));
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
