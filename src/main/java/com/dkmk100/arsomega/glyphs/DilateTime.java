package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;

public class DilateTime extends AbstractAugment {
    public static DilateTime INSTANCE = new DilateTime("dilate_time","Dilate Time");

    public DilateTime(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag),description);
    }

    public int getDefaultManaCost() {
        return 100;
    }

    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addDurationModifier(2.0);
        return super.applyModifiers(builder, spellPart);
    }
}
