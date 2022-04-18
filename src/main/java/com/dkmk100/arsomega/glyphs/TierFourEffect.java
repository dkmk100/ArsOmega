package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;

import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.resources.ResourceLocation;

public abstract class TierFourEffect extends AbstractEffect {
    public TierFourEffect(String tag, String description) {
        super(tag, description);
    }

    public static SpellTier FOUR = new SpellTier(new ResourceLocation("ars_omega","four"),4);

    @Override
    public SpellTier getTier() {
        return FOUR;
    }

}
