package com.dkmk100.arsomega.glyphs;

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

public class PropagateUnderfoot extends AbstractEffect {
    public static PropagateUnderfoot INSTANCE = new PropagateUnderfoot("propagate_underfoot","Propagate Underfoot");

    private PropagateUnderfoot(String tag, String description) {
        super(tag,description);
    }

    public void sendPacket(Level world, SpellStats stats, @Nullable LivingEntity shooter, SpellContext spellContext, Entity target) {
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
            Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
            SpellContext newContext = (new SpellContext(newSpell, shooter)).withColors(spellContext.colors);
            SpellResolver resolver = new EntitySpellResolver(newContext);
            int offset = 1 + (int)Math.round(stats.getAmpMultiplier());
            resolver.onResolveEffect(shooter.getCommandSenderWorld(),shooter,new BlockHitResult(target.position(), Direction.UP, target.blockPosition().below(offset), true));
        }
    }

    public void sendPacket(Level world, SpellStats stats, @Nullable LivingEntity shooter, SpellContext spellContext, BlockPos pos) {
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
            Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
            SpellContext newContext = (new SpellContext(newSpell, shooter)).withColors(spellContext.colors);
            SpellResolver resolver = new EntitySpellResolver(newContext);
            int offset = 1 + (int)Math.round(stats.getAmpMultiplier());
            resolver.onResolveEffect(shooter.getCommandSenderWorld(),shooter,new BlockHitResult(new Vec3(pos.getX(),pos.getY(),pos.getZ()), Direction.UP, pos.below(offset), true));
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        this.sendPacket(world, spellStats, shooter, spellContext,rayTraceResult.getBlockPos());
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        this.sendPacket(world, spellStats, shooter, spellContext,rayTraceResult.getEntity());
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addExtendTimeConfig(builder, 1);
        this.addGenericInt(builder, 20, "Base duration in ticks.", "base_duration");
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    @Override
    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE});
    }

    @Override
    public String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment, or decreased with Duration Down.";
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.ONE;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.MANIPULATION});
    }
}
