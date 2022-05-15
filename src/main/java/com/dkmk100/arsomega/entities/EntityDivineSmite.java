package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.util.ReflectionHandler;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.function.Predicate;

public class EntityDivineSmite extends LightningBoltEntity {

    int aoe;
    boolean sensitive;

    public EntityDivineSmite(EntityType<? extends LightningBoltEntity> type, World world) {
        super(type,world);
    }

    public void setAoe(int amount){
        aoe = amount;
    }
    public void setSensitive(boolean bool){
        sensitive = bool;
    }

    @Override
    public void tick() {
        try {
            int life = ReflectionHandler.Entity.lightningLife.getInt(this);
            int flashes = ReflectionHandler.Entity.lightningFlash.getInt(this);
            ServerPlayerEntity cause = (ServerPlayerEntity) ReflectionHandler.Entity.lightningCause.get(this);

            this.baseTick();
            if (life == 2) {
                if(!sensitive){
                    //this.spawnFire(4);
                }

                this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
                this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
            }

            --life;
            if (life < 0) {
                if (flashes == 0) {
                    this.remove();
                } else if (life < -this.random.nextInt(10)) {
                    --flashes;
                    life = 1;
                    this.seed = this.random.nextLong();
                    //this.spawnFire(0);
                }
            }

            if (life >= 0) {
                if (!(this.level instanceof ServerWorld)) {
                    this.level.setSkyFlashTime(2);
                } else {
                    Predicate<? super Entity> test = (entity) -> {
                        return (!sensitive || entity instanceof LivingEntity) && entity.isAlive();
                    };
                    List<Entity> list = this.level.getEntities(this, new AxisAlignedBB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D).inflate(aoe), test);

                    for (Entity entity : list) {
                        if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this))

                        if(entity instanceof MobEntity && ((MobEntity)entity).getMobType() == CreatureAttribute.UNDEAD){
                            entity.hurt(DamageSource.MAGIC,getDamage()*2);//smite the entity for double damage

                            //activate any thunder hit overrides or similar, but don't damage again.
                            //we don't just damage twice in case something gets i-frames from damage.
                            float damage = getDamage();
                            this.setDamage(0);
                            entity.thunderHit((ServerWorld) this.level, this);
                            this.setDamage(damage);
                        }
                        else{
                            entity.thunderHit((ServerWorld) this.level, this);
                        }
                    }

                    if (cause != null) {
                        CriteriaTriggers.CHANNELED_LIGHTNING.trigger(cause, list);
                    }
                }
            }

            //set life and flashes again, since we aren't using the original vars
            ReflectionHandler.Entity.lightningLife.set(this,life);
            ReflectionHandler.Entity.lightningFlash.set(this,flashes);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
