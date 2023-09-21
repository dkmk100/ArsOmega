package com.dkmk100.arsomega.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class StatueUtils {
    public static Entity CreateClientPlayer(GameProfile profile, Level level) {
        return StatueClientPlayer.create(profile, (ClientLevel) level);
    }
}
