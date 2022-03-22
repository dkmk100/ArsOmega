package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import javax.annotation.Nullable;

public class AdvancedAmplify extends AbstractAugment {

    public static AdvancedAmplify INSTANCE = new AdvancedAmplify();

    private AdvancedAmplify() {
        super("advanced_amplify", "Advanced Amplify");
    }

    public int getManaCost() {
        return 180;
    }

    @Nullable
    public Item getCraftingReagent() {
        return Items.DIAMOND_BLOCK;
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
}
