package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.glyphs.TornadoGlyph;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.ColoredProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.Entity.RemovalReason;

public class EntityWhirlpool  extends ColoredProjectile {

    public static final EntityDataAccessor<Integer> AOE = SynchedEntityData.defineId(EntityWhirlpool.class, EntityDataSerializers.INT);

    int amplify = 0;

    int ticksLeft = 200;

    int accelerate = 0;

    int checkTicks = 3;

    List<Entity> targets = new ArrayList<>();


    @Override
    public EntityType<?> getType() {
        return RegistryHandler.WHIRLPOOL.get();
    }

    @Override
    public void remove(RemovalReason reason) {
        for (Entity entity : targets) {
            if(entity==null || !entity.isAlive()){
                continue;
            }
            entity.setNoGravity(false);
        }
        super.remove(reason);
    }

    public EntityWhirlpool(EntityType<? extends EntityWhirlpool> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityWhirlpool(Level worldIn, LivingEntity shooter) {
        super(RegistryHandler.WHIRLPOOL.get(),worldIn, shooter);
    }

    public void setDuration(int ticks){
        ticksLeft = ticks;
    }

    public void setAoe(int amount){
        entityData.set(AOE,amount);
    }

    public void setAccelerate(int amount){
        accelerate = amount;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AOE, 0);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        final int checkRate = 8;
        super.tick();
        int aoe = this.entityData.get(AOE);
        boolean active = level.isWaterAt(new BlockPos(position()).above());
        if (level.isClientSide) {
            ParticleUtil.spawnOrb(this.level, this.getParticleColor(), new BlockPos(this.position().add(0, 4 + 2 * aoe, 0)),5);
            ParticleUtil.spawnLight(this.level, this.getParticleColor(), this.position().add(0, 0.5, 0), 20);
            if (active) {
                ParticleUtil.spawnRitualAreaEffect(new BlockPos(this.position().add(0, 2.5 + (aoe * 1.8), 0)), this.level, this.random, this.getParticleColor(), 3 + (aoe * 2));
            }
        } else {
            int radius = 12 + (aoe * 5);
            checkTicks--;
            if (checkTicks <= 0) {
                checkTicks = checkRate;
                float checkRadius = radius + 0.5f;//check slightly further incase something enters
                float checkRSq = checkRadius * checkRadius;//cache to not multiply in the loop
                for (Entity entity : targets) {
                    if(entity==null || !entity.isAlive()){
                        continue;
                    }
                    entity.setNoGravity(false);
                }
                List<Entity> entities = this.level.getEntities(this, AABB.ofSize(this.position(), radius, radius, radius));
                targets = new ArrayList<>();
                for (Entity entity : entities) {
                    if (Math.abs(entity.position().distanceToSqr(this.position())) <= radius * radius && entity.isAlive()) {
                        targets.add(entity);
                    }
                }
            }
            List<Entity> toRemove = new ArrayList<>();
            for (Entity entity : targets) {
                if(entity==null || !entity.isAlive()){
                    if(entity!=null) {
                        toRemove.add(entity);
                    }
                    continue;
                }
                if (active && Math.abs(entity.position().distanceToSqr(this.position())) <= radius * radius) {
                    if(entity.position().x == this.position().x && entity.position().z == this.position().z){
                        ArsOmega.LOGGER.warn("entity in same column as tornado");
                        continue;
                    }
                    Vec3 toEntity = entity.position().subtract(this.position());
                    Vec3 pos = this.position();
                    double distance = Math.sqrt(toEntity.x()*toEntity.x() + toEntity.z() * toEntity.z());//avoid using .distance because we don't use y
                    Vec3 normalizedTo = toEntity.scale(1/distance);
                    double entityAngle = Math.asin(normalizedTo.z);
                    if (toEntity.x < 0) {
                        if(entityAngle < 0){
                            entityAngle = (-1 * Math.PI) - entityAngle;
                        }
                        else {
                            entityAngle = Math.PI - entityAngle;
                        }
                    }
                    double desiredAngle = entityAngle + 0.2;

                    Vec3 angleDir = new Vec3(Math.cos(desiredAngle) * distance, 0, Math.sin(desiredAngle) * distance);

                    if(distance>1.5) //getting closer at this point is just disappointing lol
                    {
                        angleDir = angleDir.scale(0.98 - 0.015 * accelerate);//to make it spiral in
                    }
                    Vec3 newPos = new Vec3(pos.x + angleDir.x, entity.position().y, pos.z + angleDir.z);
                    Vec3 dir = newPos.subtract(entity.position());
                    if(Double.isNaN(dir.x) || Double.isNaN(dir.y)){
                        ArsOmega.LOGGER.error("Nan dir value (w): "+dir.toString());
                        throw new ArithmeticException("Error: NaN direction on whirlpool. Please report on Ars Omega github page.");
                    }
                    dir.scale(1/Math.sqrt(dir.x*dir.x + dir.z * dir.z));
                    if(toEntity.y > 1) {
                        dir = new Vec3(dir.x, -0.1, dir.z);//negative dir
                    }
                    else{
                        dir = new Vec3(dir.x, 0, dir.z);
                    }

                    double speed = 0.45 + 0.1*accelerate + (0.015+0.002*accelerate)*Math.abs(entity.position().distanceTo(this.position()));

                    entity.setDeltaMovement(dir.multiply(speed, 0.8, speed));
                    entity.hasImpulse = true;
                    entity.hurtMarked = true;
                    entity.setNoGravity(true);
                    if(this.tickCount % 6 == 0 && entity instanceof LivingEntity)//no item damage
                    {
                        entity.hurt(TornadoGlyph.TORNADO_DAMAGE,2.0f);
                    }
                } else {
                    toRemove.add(entity);
                    entity.setNoGravity(false);
                }
            }
            for(Entity entity : toRemove){
                targets.remove(entity);
            }
            ticksLeft--;
            if (ticksLeft <= 0) {
                remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        entityData.set(AOE, compound.getInt("aoe"));
        amplify = compound.getInt("amp");
        accelerate = compound.getInt("accelerate");
        ticksLeft = compound.getInt("lifetime");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("aoe", entityData.get(AOE));
        compound.putInt("amp", amplify);
        compound.putInt("accelerate", accelerate);
        compound.putInt("lifetime", ticksLeft);
    }
}
