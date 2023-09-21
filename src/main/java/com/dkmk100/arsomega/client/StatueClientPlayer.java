package com.dkmk100.arsomega.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StatueClientPlayer extends AbstractClientPlayer {

    public static StatueClientPlayer create(GameProfile profile, ClientLevel level){
        return new StatueClientPlayer(level, profile, null);
    }

    public StatueClientPlayer(ClientLevel p_234112_, GameProfile p_234113_, @Nullable ProfilePublicKey p_234114_) {
        super(p_234112_, p_234113_, p_234114_);
    }
}
