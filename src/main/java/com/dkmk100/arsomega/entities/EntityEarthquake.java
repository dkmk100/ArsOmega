package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.glyphs.TornadoGlyph;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.ColoredProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.entity.Entity.RemovalReason;

public class EntityEarthquake extends ColoredProjectile {

    public static final EntityDataAccessor<Float> AOE = SynchedEntityData.defineId(EntityTornado.class, EntityDataSerializers.FLOAT);

    public EntityEarthquake(Level worldIn, LivingEntity shooter) {
        super(RegistryHandler.EARTHQUAKE.get(),worldIn, shooter);
    }

    int amplify = 0;

    int ticksLeft = 50;

    int accelerate = 0;

    public void setDuration(int ticks){
        ticksLeft = ticks;
    }

    public void setAoe(float amount){
        entityData.set(AOE,amount);
    }

    public void setAccelerate(int amount){
        accelerate = amount;
    }
    public void setAmp(int amount){amplify = amount;}

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AOE, 0f);
    }

    public EntityEarthquake(EntityType<? extends EntityEarthquake> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        float aoe = this.entityData.get(AOE);
        if (level.isClientSide) {
            //particles! this is why AOE is synched data and the others arent
            ParticleUtil.spawnLight(this.level, this.getParticleColor(), this.position().add(0, 0.5, 0), 20);
            ParticleUtil.spawnRitualAreaEffect(new BlockPos(this.position().add(0,0.1,0)), this.level, this.random, this.getParticleColor(), 3 + Math.round(aoe * 2));
        } else {
            //apply the actual earthquake logic
            if(tickCount%8==0) {
                applyEarthquake((ServerLevel) level, amplify, accelerate, aoe, this.position());
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
        entityData.set(AOE, compound.getFloat("aoe"));
        amplify = compound.getInt("amp");
        accelerate = compound.getInt("accelerate");
        ticksLeft = compound.getInt("lifetime");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("aoe", entityData.get(AOE));
        compound.putInt("amp", amplify);
        compound.putInt("accelerate", accelerate);
        compound.putInt("lifetime", ticksLeft);
    }

    public static void applyEarthquake(ServerLevel level, int amp, int speed, float aoe, Vec3 position) {
        BlockPos quakePos = new BlockPos(position.x, position.y - 1, position.z);
        Random rand = level.getRandom();
        int rarity = 25 - Math.min(speed*3, 15);

        int range = 5 + Math.round(2 * aoe);
        int depth = 3;
        int height = 2 + Math.round(aoe);

        int blockDepth = 1;
        int blockHeight = 1;
        for (int x = range * -1; x < range; x++) {
            for (int z = range * -1; z < range; z++) {
                for(int y = -1*blockDepth;y<blockHeight;y++){
                    BlockPos pos = quakePos.offset(x, y, z);
                    boolean affectPos = rand.nextInt(rarity) == 0;
                    try {
                        BlockBehaviour.Properties properties = (BlockBehaviour.Properties) ReflectionHandler.blockProperties.get(level.getBlockState(pos).getBlock());
                        float breakTime = ReflectionHandler.destroyTime.getFloat(properties);
                        if (affectPos && breakTime > 0 && breakTime < 40) {
                            FallingBlockEntity falling = FallingBlockEntity.fall(level, pos, level.getBlockState(pos));
                            falling.setPos(falling.position().add(0, 0.6, 0));
                            falling.push((rand.nextInt(2) - 1) * 0.15, 0.8, (rand.nextInt(2) - 1) * 0.15);
                        }
                    }
                    catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(position.add(0, height - depth, 0), range*2, depth + height, range*2));
        for (LivingEntity entity : entities) {
            entity.hurt(DamageSource.FALL,5 + amp);
        }

    }
}
