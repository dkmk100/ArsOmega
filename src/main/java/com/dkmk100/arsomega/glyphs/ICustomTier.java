package com.dkmk100.arsomega.glyphs;

public interface ICustomTier {
    int getCustomTier();
    default boolean isTierFour(){
        return getCustomTier() >= 4;
    }
}
