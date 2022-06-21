package com.dkmk100.arsomega.util;

import net.minecraft.network.FriendlyByteBuf;

public class SigilPattern {
    private final boolean[][] recipe;
    public final int tileX;
    public final int tileY;


    public final int sizeY;
    public final int sizeX;

    public final int sourceCost;

    public SigilPattern(boolean[][] recipe, int tileX, int tileY, int sourceCost){
        this.recipe = recipe;
        this.tileX = tileX;
        this.tileY = tileY;
        this.sizeY = recipe.length;
        this.sizeX = recipe[0].length;
        this.sourceCost = sourceCost;
    }

    public boolean isFilled(int x, int y){
        return recipe[y][x];
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeInt(sourceCost);
        buf.writeInt(tileX);
        buf.writeInt(tileY);
        int recipeSize = sizeY;
        int size2 = sizeX;
        buf.writeInt(recipeSize);
        buf.writeInt(size2);
        for (int i = 0; i < recipeSize; i++) {
            for(int i2 = 0;i2 < size2; i2++){
                buf.writeBoolean(recipe[i][i2]);
            }
        }
    }
}
