package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;

public class LesserAOE extends AbstractAugment {
    public static LesserAOE INSTANCE = new LesserAOE("lesser_aoe","Lesser AOE");

    public LesserAOE(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag),description);
    }

    public int getDefaultManaCost() {
        return 25;
    }

    public SpellTier getTier() {
        return SpellTier.ONE;
    }

    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addAOE(0.5);
        return super.applyModifiers(builder, spellPart);
    }
}
