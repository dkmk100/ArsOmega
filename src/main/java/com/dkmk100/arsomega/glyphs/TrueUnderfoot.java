package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class TrueUnderfoot extends AbstractCastMethod {
    public static TrueUnderfoot INSTANCE = new TrueUnderfoot();

    public TrueUnderfoot() {
        super(RegistryHandler.getGlyphName("true_underfoot"), "True Underfoot");
    }

    public CastResolveType onCast(@Nullable ItemStack stack, LivingEntity caster, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(caster.position(), Direction.UP, caster.blockPosition().below(), true));
        return CastResolveType.SUCCESS;
    }

    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        LivingEntity caster = context.getPlayer();
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(caster.position(), Direction.UP, caster.blockPosition().below(), true));
        return CastResolveType.SUCCESS;
    }

    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(caster.position(), Direction.UP, caster.blockPosition().below(), true));
        return CastResolveType.SUCCESS;
    }

    public CastResolveType onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(caster.position(), Direction.UP, caster.blockPosition().below(), true));
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
        return "Targets the spell on the block beneath the player.";
    }
}

