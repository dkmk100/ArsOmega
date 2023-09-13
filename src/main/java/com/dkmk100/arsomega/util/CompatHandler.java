package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.glyphs.IIgnoreBuffs;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

public class CompatHandler {
    public static boolean shouldIgnoreBuffs(AbstractSpellPart part){
        return part instanceof IIgnoreBuffs;
    }
}
