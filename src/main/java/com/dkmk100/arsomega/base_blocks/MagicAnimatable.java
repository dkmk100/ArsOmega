package com.dkmk100.arsomega.base_blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public interface MagicAnimatable {
    void Animate(BlockPos pos, ServerWorld world);
}
