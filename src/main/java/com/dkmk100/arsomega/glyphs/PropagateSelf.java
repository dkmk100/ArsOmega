package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

public class PropagateSelf extends AbstractEffect {
    public static PropagateSelf INSTANCE = new PropagateSelf("propagate_self","Propagate Self");

    private PropagateSelf(String tag, String description) {
        super(tag,description);
    }

    public void sendPacket(World world, RayTraceResult rayTraceResult, @Nullable LivingEntity shooter, SpellContext spellContext) {
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
            Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
            SpellContext newContext = (new SpellContext(newSpell, shooter)).withColors(spellContext.colors);
            SpellResolver resolver = new EntitySpellResolver(newContext);
            resolver.onResolveEffect(shooter.getCommandSenderWorld(),shooter,new EntityRayTraceResult(shooter));
        }
    }

    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        this.sendPacket(world, rayTraceResult, shooter, spellContext);
    }

    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        this.sendPacket(world, rayTraceResult, shooter, spellContext);
    }

    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addExtendTimeConfig(builder, 1);
        this.addGenericInt(builder, 20, "Base duration in ticks.", "base_duration");
    }

    public int getManaCost() {
        return 50;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{});
    }

    public String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment, or decreased with Duration Down.";
    }

    public Tier getTier() {
        return Tier.ONE;
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
