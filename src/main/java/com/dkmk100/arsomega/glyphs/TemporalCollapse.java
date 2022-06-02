package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;

public class TemporalCollapse  extends AbstractAugment {
    public static TemporalCollapse INSTANCE = new TemporalCollapse("temporal_collapse","Temporal Collapse");

    public TemporalCollapse(String name, String description) {
        super(name,description);
    }

    public int getDefaultManaCost() {
        return 80;
    }

    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addDurationModifier(-3.0);
        return super.applyModifiers(builder, spellPart);
    }

    @Override
    public SpellTier getTier() {
        return TierFourEffect.FOUR;
    }
}
