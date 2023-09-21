package com.dkmk100.arsomega.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public class StatueUtils {
    public static Entity CreateClientPlayer(GameProfile profile, ClientLevel level){
        return StatueClientPlayer.create(profile,level);
    }
}
