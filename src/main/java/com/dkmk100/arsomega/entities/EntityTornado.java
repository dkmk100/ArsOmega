package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.glyphs.TornadoGlyph;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.ColoredProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityTornado extends ColoredProjectile {

    public static final DataParameter<Integer> AOE = EntityDataManager.defineId(EntityTornado.class, DataSerializers.INT);

    int amplify = 0;

    int ticksLeft = 200;

    int accelerate = 0;

    int checkTicks = 3;

    List<Entity> targets = new ArrayList<>();

    public EntityTornado(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        super(RegistryHandler.TORNADO.get(),world);
    }

    @Override
    public EntityType<?> getType() {
        return RegistryHandler.TORNADO.get();
    }

    @Override
    public void remove(boolean bool) {
        for (Entity entity : targets) {
            if(entity==null || entity.removed){
                ArsOmega.LOGGER.info("removed entity"+entity.toString());
                continue;
            }
            entity.setNoGravity(false);
        }
        super.remove(bool);
    }

    public EntityTornado(EntityType<? extends EntityTornado> type, World worldIn) {
        super(type, worldIn);
    }

    public EntityTornado(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        ArsOmega.LOGGER.info("tick start");
        ArsOmega.LOGGER.info("tick count: "+this.tickCount);
        super.tick();
        ArsOmega.LOGGER.info("super tick");
        int aoe = this.entityData.get(AOE);
        if (level.isClientSide) {
            ParticleUtil.spawnParticleSphere(this.level, new BlockPos(this.position().add(0,4+2*aoe,0)), this.getParticleColor());
            ParticleUtil.spawnLight(this.level, this.getParticleColor(), this.position().add(0, 0.5, 0), 20);
            ParticleUtil.spawnRitualAreaEffect(new BlockPos(this.position().add(0,3+2*aoe,0)), this.level, this.random, this.getParticleColor(), 3 + (aoe * 2));
        } else {
            int radius = 12 + (aoe * 5);
            checkTicks--;
            if (checkTicks <= 0) {
            ArsOmega.LOGGER.info("check start");
                checkTicks = 2;
                for (Entity entity : targets) {
                    if(entity==null || entity.removed){
                        ArsOmega.LOGGER.info("removed entity"+entity.toString());
                        continue;
                    }
                    entity.setNoGravity(false);
                }
                List<Entity> entities = this.level.getEntities(this, AxisAlignedBB.ofSize(radius, radius, radius).move(this.position()));
                targets = new ArrayList<>();
                for (Entity entity : entities) {
                    ArsOmega.LOGGER.info("entity: "+entity.toString());
                    if (Math.abs(entity.position().distanceToSqr(this.position())) <= radius * radius && !entity.removed) {
                        ArsOmega.LOGGER.info("adding entity: "+entity.toString());
                        targets.add(entity);
                    }
                }
                ArsOmega.LOGGER.info("check end");
            }
            ArsOmega.LOGGER.info("behavior start");
            List<Entity> toRemove = new ArrayList<>();
            for (Entity entity : targets) {
                if(entity==null || entity.removed){
                    ArsOmega.LOGGER.info("removed entity"+entity.toString());
                    continue;
                }
                ArsOmega.LOGGER.info("entity: "+entity.toString());
                if (Math.abs(entity.position().distanceToSqr(this.position())) <= radius * radius) {
                    Vector3d toEntity = entity.position().subtract(this.position());
                    Vector3d pos = this.position();
                    double distance = Math.sqrt(toEntity.x()*toEntity.x() + toEntity.z() * toEntity.z());//avoid using .distance because we don't use y
                    Vector3d normalizedTo = toEntity.scale(1/distance);
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

                    Vector3d angleDir = new Vector3d(Math.cos(desiredAngle) * distance, 0, Math.sin(desiredAngle) * distance);

                    if(distance>1.5) //getting closer at this point is just disappointing lol
                    {
                        angleDir = angleDir.scale(0.98 - 0.015 * accelerate);//to make it spiral in
                    }

                    Vector3d newPos = new Vector3d(pos.x + angleDir.x, entity.position().y, pos.z + angleDir.z);
                    Vector3d dir = newPos.subtract(entity.position());
                    dir.scale(1/Math.sqrt(dir.x*dir.x + dir.z * dir.z));
                    if(toEntity.y< 4 + 2*aoe + accelerate) {
                        dir = new Vector3d(dir.x, 0.1, dir.z);
                    }
                    else{
                        dir = new Vector3d(dir.x, 0, dir.z);
                    }
                    double speed = 0.45 + 0.1*accelerate + (0.015+0.002*accelerate)*Math.abs(entity.position().distanceTo(this.position()));
                    /*
                    if (Math.abs(entity.position().distanceToSqr(this.position())) < 16) {
                        speed = 0.3;
                    }
                     */
                    entity.setDeltaMovement(dir.multiply(speed, 1, speed));
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
            ArsOmega.LOGGER.info("behavior end");
            ticksLeft--;
            if (ticksLeft <= 0) {
                remove();
            }
        }
    }

    @Override
    public void load(CompoundNBT compound) {
        super.load(compound);
        entityData.set(AOE, compound.getInt("aoe"));
        amplify = compound.getInt("amp");
        accelerate = compound.getInt("accelerate");
        ticksLeft = compound.getInt("lifetime");
    }

    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("aoe", entityData.get(AOE));
        compound.putInt("amp", amplify);
        compound.putInt("accelerate", accelerate);
        compound.putInt("lifetime", ticksLeft);
    }
}
