package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodOrbit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

public class PropagateOrbit  extends AbstractEffect implements ConfigurableGlyph, IPropagator {
    public static PropagateOrbit INSTANCE = new PropagateOrbit("propagate_orbit","Propagate Orbit");

    private PropagateOrbit(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag),description);
    }

    public void sendPacket(Level world, HitResult rayTraceResult, @Nullable LivingEntity shooter, SpellContext spellContext, SpellStats stats) {
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
            Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
            SpellContext newContext = spellContext.clone().withSpell(newSpell);//need .withCaster??? todo: answer this question
            SpellResolver resolver = new EntitySpellResolver(newContext);
            //List<AbstractAugment> newAugments = new ArrayList<AbstractAugment>();//stats.getAugments()
            MethodOrbit.INSTANCE.summonProjectiles(world,shooter,resolver,stats);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        PropagateProjectile.INSTANCE.sendPacket(world,rayTraceResult,shooter,spellContext,spellStats);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        this.sendPacket(world, rayTraceResult, shooter, spellContext,spellStats);
    }

    @Override
    public int getDefaultManaCost() {
        return 300;
    }

    @Override
    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return MethodOrbit.INSTANCE.getCompatibleAugments();
    }

    @Override
    public String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment, or decreased with Duration Down.";
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 2, 2, 2);
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.MANIPULATION});
    }
}
