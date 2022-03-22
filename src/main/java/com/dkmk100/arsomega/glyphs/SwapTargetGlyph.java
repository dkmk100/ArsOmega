package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
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

public class SwapTargetGlyph extends AbstractEffect {
    public static SwapTargetGlyph INSTANCE = new SwapTargetGlyph("swap_target","swap_target");

    private SwapTargetGlyph(String tag, String description) {
        super(tag,description);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        spellContext.setCanceled(true);
        if (rayTraceResult.getEntity() instanceof LivingEntity) {
            if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
                Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
                SpellContext newContext = (new SpellContext(newSpell, shooter)).withColors(spellContext.colors);
                SpellResolver.resolveEffects(shooter.getCommandSenderWorld(), (LivingEntity) rayTraceResult.getEntity(), new EntityRayTraceResult(shooter), newSpell, newContext);
            }
        }
    }

    public int getManaCost() {
        return 5;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{});
    }

    public String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment, or decreased with Duration Down.";
    }

    public Tier getTier() {
        return Tier.THREE;
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
