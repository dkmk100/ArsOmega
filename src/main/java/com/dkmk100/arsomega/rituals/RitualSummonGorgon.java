package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;

public class RitualSummonGorgon extends AbstractRitual {
    @Override
    protected void tick() {
        Level level = getWorld();
        BlockPos pos = getPos();
        if(level.isClientSide()){
            ParticleUtil.spawnRitualAreaEffect(pos, level, level.random, getCenterColor(), 6);
        }
        else if(level instanceof ServerLevel){
            if(level.getGameTime() % 60 == 0){
                this.incrementProgress();
                if(this.getProgress() > 3){
                    this.setFinished();
                }

                EntityType target = RegistryHandler.GORGON.get();
                BlockPos newPos = findValidPos(level, 5, pos);
                target.spawn((ServerLevel) level, null, null, newPos, MobSpawnType.MOB_SUMMONED, false, false);
            }
        }
    }

    BlockPos findValidPos(Level level, int range, BlockPos centerGround){
        int maxIterations = 5;
        for(int i=0;i<maxIterations;i++){
            BlockPos newPos = centerGround.offset(level.random.nextInt(-range, range),1,level.random.nextInt(-range, range));
            if(level.getBlockState(newPos).isAir() && level.getBlockState(newPos.above()).isAir()){
                return newPos;
            }
        }

        return centerGround.above();
    }

    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName("summon_gorgon");
    }

    @Override
    public ParticleColor getCenterColor() {
        return ParticleColor.GREEN;
    }
}
