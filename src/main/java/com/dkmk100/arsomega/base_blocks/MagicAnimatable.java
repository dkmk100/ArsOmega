package com.dkmk100.arsomega.base_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface MagicAnimatable {
    void Animate(BlockPos pos, ServerLevel world);
}
