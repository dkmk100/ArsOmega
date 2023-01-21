package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;

public class GreaterAOE extends AbstractAugment {
    public static GreaterAOE INSTANCE = new GreaterAOE("greater_aoe","Greater AOE");

    public GreaterAOE(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag),description);
    }

    public int getDefaultManaCost() {
        return 105;
    }

    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addAOE(2.0);
        return super.applyModifiers(builder, spellPart);
    }
}
