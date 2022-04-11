package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.block.tile.ManaJarTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;

public class InfinityCrystalTile extends ManaJarTile {
    public InfinityCrystalTile() {
        super(RegistryHandler.InfinityCrystalType.get());
    }
    @Override
    public void tick() {
        super.tick();
        this.addMana(1);
    }
}
