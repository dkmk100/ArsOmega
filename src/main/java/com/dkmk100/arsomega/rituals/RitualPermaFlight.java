package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;

public class RitualPermaFlight extends BasicConfigRitual{

    @Override
    protected void tick() {
        Level world = this.getWorld();
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 100; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(this.getCenterColor()), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }

        if (!world.isClientSide && world.getGameTime() % 20L == 0L) {
            if(this.needsManaNow()){
                return;
            }
            else{
                this.setNeedsMana(true);
            }
            this.incrementProgress();
            if (this.getProgress() > DURATION.get()) {
                List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AABB(this.getPos())).inflate(RANGE.get() * 2));
                for (LivingEntity entity : entities) {
                    if(entity instanceof Player){
                        entity.addEffect(new MobEffectInstance(ModPotions.PERMA_FLIGHT, 999999,0,false,false));
                        this.setFinished();
                    }
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 500;
    }
    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(240,245,255);
    }
    @Override
    public boolean consumesMana() {
        return true;
    }

    @Override
    public String getID() {
        return "perma_flight";
    }

    @Override
    protected int getDefaultDuration() {
        return 8;
    }

    @Override
    protected double getDefaultRange() {
        return 3.0;
    }
}
