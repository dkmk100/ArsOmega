package com.dkmk100.arsomega.compat;

import com.dkmk100.arsomega.glyphs.IIgnoreBuffs;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraftforge.fml.ModList;

public class CompatHandler {
    public static boolean shouldIgnoreBuffs(AbstractSpellPart part){
        return part instanceof IIgnoreBuffs;
    }

    public static boolean apotheosisInstalled(){
        return ModList.get().isLoaded("apotheosis");
    }

    public static int getMaxEnchantLevel(){
        return apotheosisInstalled() ? ApotheosisCompat.getMaxEnchantLevel() : 30;
    }
}
