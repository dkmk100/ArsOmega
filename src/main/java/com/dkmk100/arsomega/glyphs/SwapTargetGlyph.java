package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
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
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        spellContext.setCanceled(true);
        if (rayTraceResult.getEntity() instanceof LivingEntity) {
            if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
                Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
                SpellContext newContext = (new SpellContext(newSpell, shooter)).withColors(spellContext.colors);
                SpellResolver.resolveEffects(shooter.getCommandSenderWorld(), (LivingEntity) rayTraceResult.getEntity(), new EntityHitResult(shooter), newSpell, newContext);
            }
        }
    }

    public int getDefaultManaCost() {
        return 500;
    }

    @Override
    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{});
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
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.MANIPULATION});
    }
}
