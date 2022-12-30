package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.glyphs.TornadoGlyph;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.ColoredProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.mojang.math.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import java.util.Optional;

import net.minecraft.world.entity.Entity.RemovalReason;

public class EntityTornado extends ColoredProjectile {

    public static final EntityDataAccessor<Integer> AOE = SynchedEntityData.defineId(EntityTornado.class, EntityDataSerializers.INT);

    int amplify = 0;

    int ticksLeft = 200;

    int accelerate = 0;

    int checkTicks = 1;

    List<Entity> targets = new ArrayList<>();


    @Override
    public EntityType<?> getType() {
        return RegistryHandler.TORNADO.get();
    }

    @Override
    public void remove(RemovalReason reason) {
        for (Entity entity : targets) {
            if(entity==null || !entity.isAlive()){
                ArsOmega.LOGGER.info("removed entity"+entity.toString());
                continue;
            }
            entity.setNoGravity(false);
        }
        super.remove(reason);
    }

    public EntityTornado(EntityType<? extends EntityTornado> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityTornado(Level worldIn, LivingEntity shooter) {
        super(RegistryHandler.TORNADO.get(),worldIn, shooter);
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
        boolean active = level.getBlockState(new BlockPos(position()).above()).isAir();
        int aoe = this.entityData.get(AOE);
        if (level.isClientSide) {
            ParticleUtil.spawnParticleSphere(this.level, new BlockPos(this.position().add(0, 4 + 2 * aoe, 0)), this.getParticleColor());
            ParticleUtil.spawnLight(this.level, this.getParticleColor(), this.position().add(0, 0.5, 0), 20);
            if (active) {
                ParticleUtil.spawnRitualAreaEffect(new BlockPos(this.position().add(0, 3 + 2 * aoe, 0)), this.level, this.random, this.getParticleColor(), 3 + (aoe * 2));
            }
        }  else {
            if(level.getGameTime() % 15 == 0){
                level.playSound(null,this.position().x,this.position().y,this.position().z, SoundEvents.ARROW_SHOOT, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
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
                List<Entity> entities = this.level.getEntities(this, AABB.ofSize(this.position(), checkRadius, checkRadius, checkRadius));
                targets = new ArrayList<>();

                for (Entity entity : entities) {
                    if (entity.isAlive() && Math.abs(entity.position().distanceToSqr(this.position())) <= checkRSq) {
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
                if (Math.abs(entity.position().distanceToSqr(this.position())) <= radius * radius && active) {
                    if(entity.position().x == this.position().x && entity.position().z == this.position().z){
                        ArsOmega.LOGGER.warn("entity in same column as tornado");
                        continue;
                    }
                    Vec3 toEntity = entity.position().subtract(this.position());
                    Vec3 pos = this.position();

                    double distance = Math.sqrt(toEntity.x()*toEntity.x() + toEntity.z() * toEntity.z());//avoid using .distance because we don't use y
                    if(distance==0){
                        ArsOmega.LOGGER.warn("zero distance");
                    }
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

                    if(dir.x==0 && dir.z==0){
                        ArsOmega.LOGGER.info("zero dir...");
                    }
                    else{
                        dir.scale(1/Math.sqrt(dir.x*dir.x + dir.z * dir.z));
                    }

                    if(toEntity.y< 4 + 2*aoe + accelerate) {
                        dir = new Vec3(dir.x, 0.1, dir.z);
                    }
                    else{
                        dir = new Vec3(dir.x, 0, dir.z);
                    }

                    double speed = 0.45 + 0.1*accelerate + (0.015+0.002*accelerate)*Math.abs(entity.position().distanceTo(this.position()));


                    if(Double.isNaN(dir.x) || Double.isNaN(dir.y)){
                        ArsOmega.LOGGER.error("Nan dir value (t): "+dir.toString());
                        throw new ArithmeticException("Error: NaN direction on tornado. Please report on Ars Omega github page.");
                    }
                    else {
                        entity.setDeltaMovement(dir.multiply(speed, 1, speed));
                    }
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
