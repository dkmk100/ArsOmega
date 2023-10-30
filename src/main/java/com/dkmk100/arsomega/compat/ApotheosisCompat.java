package com.dkmk100.arsomega.compat;

import shadows.apotheosis.ench.table.EnchantingStatManager;

public class ApotheosisCompat {
    public static int getMaxEnchantLevel(){
        return (int) (4 * EnchantingStatManager.getAbsoluteMaxEterna());
    }
}
