package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraftforge.common.ForgeConfigSpec;

public class CursedBind  extends AbstractAugment {

    public static CursedBind INSTANCE = new CursedBind();

    private CursedBind() {
        super("cursed_bind", "Cursed Bind");
    }

    public int getDefaultManaCost() {
        return 1500;
    }

    public SpellTier getTier() {
        return TierFourEffect.FOUR;
    }

    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        return super.applyModifiers(builder, spellPart);
    }

    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, 1);
    }
}
