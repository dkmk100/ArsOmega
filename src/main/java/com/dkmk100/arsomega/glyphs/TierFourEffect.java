package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;

public abstract class TierFourEffect extends AbstractEffect implements ICustomTier {
    public TierFourEffect(String tag, String description) {
        super(tag, description);
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }
    @Override
    public int getCustomTier() {
        return 4;
    }

    @Override
    public boolean isTierFour() {
        return true;
    }


}
