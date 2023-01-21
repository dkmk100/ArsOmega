package com.dkmk100.arsomega.spell_sigils;

import com.dkmk100.arsomega.glyphs.Scald;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ScaldSigil extends AbstractSpellSigil {
    public ScaldSigil() {
        super();
    }

    @Override
    public void OnActivateClient(Level world, BlockPos pos, Player player){
        world.playSound(player,pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS,1.0f,1.0f);
        ParticleUtil.spawnRitualAreaEffect(pos, world, world.getRandom(), ParticleColor.makeRandomColor(25,5,255,world.getRandom()), 5, 2, 15);
    }

    @Override
    public void OnActivate(ServerLevel world, BlockPos pos, Player player) {
        int range = 5;
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(pos.getX(),pos.getY(),pos.getZ()),range*2,range,range*2));
        for(LivingEntity target : targets){
            if(target!=player){
                target.hurt(Scald.SCALD(player,player),45);
                target.addEffect(new MobEffectInstance(ModPotions.BURNED.get(),600));
            }
        }
        world.playSound(player,pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS,1.0f,1.0f);
    }


    @Override
    protected int GetCost() {
        return 300;
    }

    @Override
    protected String[] GetPattern() {
        return new String[]{
                " xxxxx ",
                "xx   xx",
                "x x x x",
                "x  S  x",
                "x x x x",
                "xx   xx",
                " xxxxx "
        };
    }
}
