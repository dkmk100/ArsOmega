package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.base_blocks.MagicAnimatable;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class AnimateGlyph extends AbstractEffect {
    public static AnimateGlyph INSTANCE = new AnimateGlyph("animate_block", "Animate");
    public AnimateGlyph(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (world instanceof ServerLevel) {
            BlockPos pos = rayTraceResult.getBlockPos();
            Block block = world.getBlockState(pos).getBlock();
            if(block instanceof MagicAnimatable){
                MagicAnimatable anim = (MagicAnimatable) block;
                if(shooter != null && shooter instanceof Player) {
                    anim.Animate(pos, (ServerLevel) world, (Player)shooter);
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
        return this.augmentSetOf(new AbstractAugment[]{});
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ABJURATION,SpellSchools.CONJURATION});
    }
}
