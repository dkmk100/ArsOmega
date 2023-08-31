package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfinityCrystalTile extends SourceJarTile implements ITickable {
    public InfinityCrystalTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.InfinityCrystalType.get(),pos,state);
    }

    @Override
    public void tick() {
        long sourceFrequency = 1;
        int sourceAmount = 1;
        if (!this.level.isClientSide && getLevel().getGameTime() % sourceFrequency == 0) {
            this.addSource(sourceAmount);
        }
        super.tick();
    }
}
