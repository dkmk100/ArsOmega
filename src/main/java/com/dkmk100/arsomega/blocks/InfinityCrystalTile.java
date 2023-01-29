package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfinityCrystalTile extends SourceJarTile {
    public InfinityCrystalTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.InfinityCrystalType.get(),pos,state);
    }
    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            this.addSource(10);
        }
        super.tick();
    }
}
