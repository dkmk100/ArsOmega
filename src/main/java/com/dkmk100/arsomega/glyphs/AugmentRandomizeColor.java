package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

public class AugmentRandomizeColor extends AbstractAugment {

    public static AugmentRandomizeColor INSTANCE = new AugmentRandomizeColor("random_color_augment","Randomize Color");

    public AugmentRandomizeColor(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public int getDefaultManaCost() {
        return 4;
    }
}
