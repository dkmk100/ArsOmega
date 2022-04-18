/*
package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.spell.method.MethodUnderfoot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class OverheadGlyph extends AbstractCastMethod {
    public static OverheadGlyph INSTANCE = new OverheadGlyph("overhead","overhead");

    static final int OFFSET = 3;

    public OverheadGlyph(String tag, String description) {
        super(tag,description);
    }

    public void onCast(@Nullable ItemStack stack, LivingEntity caster, Level world, List<AbstractAugment> augments, SpellContext context, SpellResolver resolver) {

        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new BlockHitResult(caster.position().add(0,OFFSET,0), Direction.UP, caster.blockPosition().above(OFFSET), true));
        resolver.expendMana(caster);
    }

    public void onCastOnBlock(UseOnContext context, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        LivingEntity caster = context.getPlayer();
        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new BlockHitResult(caster.position().add(0,OFFSET,0), Direction.UP, caster.blockPosition().above(OFFSET), true));
        resolver.expendMana(caster);
    }

    public void onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new BlockHitResult(caster.position().add(0,OFFSET,0), Direction.UP, caster.blockPosition().above(OFFSET), true));
        resolver.expendMana(caster);
    }

    public void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new BlockHitResult(caster.position().add(0,2,0), Direction.UP, caster.blockPosition().above(2), true));
        resolver.expendMana(caster);
    }

    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, List<AbstractAugment> augments, SpellResolver resolver) {
        return false;
    }

    public boolean wouldCastOnBlockSuccessfully(UseOnContext context, List<AbstractAugment> augments, SpellResolver resolver) {
        return false;
    }

    public boolean wouldCastOnBlockSuccessfully(BlockHitResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellResolver resolver) {
        return false;
    }

    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, List<AbstractAugment> augments, SpellResolver resolver) {
        return false;
    }

    public int getManaCost() {
        return 5;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[0]);
    }

    public String getBookDescription() {
        return "Targets the spell on the block beneath the player.";
    }

    @Nullable
    public Item getCraftingReagent() {
        return Items.STONE_PRESSURE_PLATE;
    }
}
 */
