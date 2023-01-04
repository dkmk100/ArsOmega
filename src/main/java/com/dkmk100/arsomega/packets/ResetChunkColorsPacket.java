package com.dkmk100.arsomega.packets;


import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class ResetChunkColorsPacket extends BasicPacket {

    private int chunkX;
    private int chunkZ;

    public ResetChunkColorsPacket(){

    }

    public ResetChunkColorsPacket(int chunkX, int chunkZ){
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    @Override
    public void encode(FriendlyByteBuf output) {
        output.writeInt(chunkX);
        output.writeInt(chunkZ);
    }

    @Override
    public void decode(FriendlyByteBuf input) {
        chunkX = input.readInt();
        chunkZ = input.readInt();
    }

    @Override
    public void actionClient(Level level, Player player) {
        ((ClientLevel) level).onChunkLoaded(new ChunkPos(chunkX, chunkZ));
    }

    @Override
    public void actionServer(Level level, ServerPlayer player) {

    }
}