package com.dkmk100.arsomega.spell_sigils;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class HealSigil extends AbstractSpellSigil{

    @Override
    public void OnActivateClient(Level world, BlockPos pos, Player player){
        world.playSound(player,pos, SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.PLAYERS,1.0f,1.0f);
        ParticleUtil.spawnRitualAreaEffect(pos, world, world.getRandom(), ParticleColor.makeRandomColor(100,10,10,world.getRandom()), 5, 2, 15);
    }

    @Override
    public void OnActivate(ServerLevel world, BlockPos pos, Player player) {
        int range = 5;
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(pos.getX(),pos.getY(),pos.getZ()),range*2,range,range*2));
        for(LivingEntity target : targets){
            target.heal(20f);
        }
        world.playSound(player,pos, SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.PLAYERS,1.0f,1.0f);
    }


    @Override
    protected int GetCost() {
        return 300;
    }

    @Override
    protected String[] GetPattern() {
        return new String[]{
                "   xxx   ",
                "   x x   ",
                "  xxxxx  ",
                "xxxx xxxx",
                "x x S x x",
                "xxxx xxxx",
                "  xxxxx  ",
                "   x x   ",
                "   xxx   "
        };
    }
}
