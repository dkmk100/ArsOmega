package com.dkmk100.arsomega.entities;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BossMusic extends AbstractTickableSoundInstance {
    //Taken from Meet Your Fight, which is MIT licence.
    //Originaly from Botania.
    private final LivingEntity boss;

    public BossMusic(LivingEntity boss, SoundEvent sound, RandomSource random) {
        super(sound, SoundSource.RECORDS,random);
        this.boss = boss;
        x = boss.getX();
        y = boss.getY();
        z = boss.getZ();
        looping = true;
    }

    @Override
    public void tick() {
        if (boss.isAlive()) {
            x = boss.getX();
            y = boss.getY();
            z = boss.getZ();
        }
        else stop();
    }

}