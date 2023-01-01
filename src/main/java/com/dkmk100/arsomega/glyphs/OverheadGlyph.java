package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class OverheadGlyph extends AbstractCastMethod {
    public static OverheadGlyph INSTANCE = new OverheadGlyph("overhead","overhead");

    public OverheadGlyph(String tag, String description) {
        super(tag,description);
    }

    public CastResolveType onCast(@Nullable ItemStack stack, LivingEntity caster, Level world, SpellStats stats, SpellContext context, SpellResolver resolver) {
        Vec3 pos = caster.getEyePosition().add(0,1,0);
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(pos, Direction.DOWN, new BlockPos(pos), true));
        return CastResolveType.SUCCESS;
    }

    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        LivingEntity caster = context.getPlayer();
        Vec3 pos = caster.getEyePosition().add(0,1,0);
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(pos, Direction.DOWN, new BlockPos(pos), true));
        return CastResolveType.SUCCESS;
    }

    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        Vec3 pos = caster.getEyePosition().add(0,1,0);
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(pos, Direction.DOWN, new BlockPos(pos), true));
        return CastResolveType.SUCCESS;
    }

    public CastResolveType onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(caster.position().add(0,2,0), Direction.DOWN, caster.blockPosition().above(2), true));
        return CastResolveType.SUCCESS;
    }

    public int getDefaultManaCost() {
        return 5;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[0]);
    }

    public String getBookDescription() {
        return "Targets the spell on the block above the player.";
    }
}

