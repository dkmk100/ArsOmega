package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodOrbit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PropagateOrbit  extends AbstractEffect {
    public static PropagateOrbit INSTANCE = new PropagateOrbit("propagate_orbit","Propagate Orbit");

    private PropagateOrbit(String tag, String description) {
        super(tag,description);
    }

    public void sendPacket(World world, RayTraceResult rayTraceResult, @Nullable LivingEntity shooter, SpellContext spellContext, SpellStats stats) {
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
            Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
            SpellContext newContext = (new SpellContext(newSpell, shooter)).withColors(spellContext.colors);
            SpellResolver resolver = new EntitySpellResolver(newContext);
            //List<AbstractAugment> newAugments = new ArrayList<AbstractAugment>();//stats.getAugments()
            List<AbstractAugment> newAugments = stats.getAugments();
            MethodOrbit.INSTANCE.summonProjectiles(world,shooter,resolver,newAugments);
        }
    }

    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        PropagateProjectile.INSTANCE.sendPacket(world,rayTraceResult,shooter,spellContext,spellStats);
    }

    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        this.sendPacket(world, rayTraceResult, shooter, spellContext,spellStats);
    }

    public int getManaCost() {
        return 300;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAccelerate.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentSensitive.INSTANCE});
    }

    public String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment, or decreased with Duration Down.";
    }

    public Tier getTier() {
        return Tier.THREE;
    }

    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 2, 2, 2);
    }

    @Nullable
    public Item getCraftingReagent() {
        return Items.REPEATER;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.MANIPULATION});
    }
}
