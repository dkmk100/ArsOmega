package com.dkmk100.arsomega.base_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface MagicAnimatable {
    void Animate(BlockPos pos, ServerLevel world, @Nullable Player player);
}
