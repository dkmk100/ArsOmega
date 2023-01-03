package com.dkmk100.arsomega.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class BasicPacket {
    public abstract void encode(FriendlyByteBuf output);

    public abstract void decode(FriendlyByteBuf input);

    @OnlyIn(Dist.CLIENT)
    public abstract void actionClient(Level level, Player player);

    public abstract void actionServer(Level level, ServerPlayer player);
}
