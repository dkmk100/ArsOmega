package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

public class PropagateOverhead extends AbstractEffect implements IIgnoreBuffs, IPropagator {
    public static PropagateOverhead INSTANCE = new PropagateOverhead("propagate_overhead","Propagate Overhead");

    private PropagateOverhead(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag),description);
    }

    @Override
    public Integer getTypeIndex() {
        return 8;
    }

    public void sendPacket(Level world, SpellStats stats, @Nullable LivingEntity shooter, SpellContext spellContext, Entity target) {
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
            Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
            SpellContext newContext = spellContext.clone().withSpell(newSpell);
            SpellResolver resolver = new EntitySpellResolver(newContext);
            int offset = 3 + (int)Math.round(stats.getAmpMultiplier());
            resolver.onResolveEffect(shooter.getCommandSenderWorld(),new BlockHitResult(target.position(), Direction.DOWN, target.blockPosition().above(offset), true));
        }
    }

    public void sendPacket(Level world, SpellStats stats, @Nullable LivingEntity shooter, SpellContext spellContext, BlockPos pos) {
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
            Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
            SpellContext newContext = spellContext.clone().withSpell(newSpell);
            SpellResolver resolver = new EntitySpellResolver(newContext);
            int offset = 1 + (int)Math.round(stats.getAmpMultiplier());
            resolver.onResolveEffect(shooter.getCommandSenderWorld(),new BlockHitResult(new Vec3(pos.getX(),pos.getY(),pos.getZ()), Direction.DOWN, pos.above(offset), true));
        }
    }

    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        this.sendPacket(world, spellStats, shooter, spellContext,rayTraceResult.getBlockPos());
    }

    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        this.sendPacket(world, spellStats, shooter, spellContext,rayTraceResult.getEntity());
    }

    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addExtendTimeConfig(builder, 1);
        this.addGenericInt(builder, 20, "Base duration in ticks.", "base_duration");
    }

    public int getDefaultManaCost() {
        return 50;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE});
    }

    public String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment, or decreased with Duration Down.";
    }

    public SpellTier getTier() {
        return SpellTier.ONE;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.MANIPULATION});
    }
}
