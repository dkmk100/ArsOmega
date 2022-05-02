package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import javax.annotation.Nullable;

public class CursedBind extends AbstractAugment implements ICustomTier {

    public static CursedBind INSTANCE = new CursedBind();

    private CursedBind() {
        super("cursed_bind", "Cursed Bind");
    }

    public int getManaCost() {
        return 1000;
    }

    public ISpellTier.Tier getTier() {
        return Tier.THREE;
    }

    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addAmplification(2.0D);
        return super.applyModifiers(builder, spellPart);
    }

    public String getBookDescription() {
        return "A more powerful version of amplify that is less mana efficient";
    }

    @Override
    public int getCustomTier() {
        return 4;
    }
}

