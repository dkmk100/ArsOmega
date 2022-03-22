package com.dkmk100.arsomega.entities;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TornadoEntity extends Entity {
    int ticks = 0;

    public TornadoEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    void spawnParticles(ServerWorld world){
        world.despawn(this);
        this.remove();
    }
    void spawnParticles(Vector3d vec, ServerWorld world){
        for(int i = 0; i < 10; ++i) {
            (world).sendParticles(ParticleTypes.SWEEP_ATTACK, vec.x + ParticleUtil.inRange(-0.2D, 0.2D), vec.y + 0.5D + ParticleUtil.inRange(-0.2D, 0.2D), vec.z + ParticleUtil.inRange(-0.2D, 0.2D), 30, ParticleUtil.inRange(-0.2D, 0.2D), ParticleUtil.inRange(-0.2D, 0.2D), ParticleUtil.inRange(-0.2D, 0.2D), 0.3D);
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {

    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return null;
    }

    @Override
    public void tick() {

        super.tick();
    }
}
