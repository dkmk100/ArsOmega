package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class AdvancedAmplify extends AbstractAugment {

    public static AdvancedAmplify INSTANCE = new AdvancedAmplify();

    private AdvancedAmplify() {
        super("advanced_amplify", "Advanced Amplify");
    }

    @Override
    public int getDefaultManaCost() {
        return 180;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addAmplification(2.0D);
        return super.applyModifiers(builder, spellPart);
    }

    @Override
    public String getBookDescription() {
        return "A more powerful version of amplify that is less mana efficient";
    }
}
