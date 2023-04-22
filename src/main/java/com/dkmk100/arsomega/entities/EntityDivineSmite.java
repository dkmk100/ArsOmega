package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class EntityDivineSmite extends LightningBolt {
    float aoe;
    boolean sensitive;
    public int extendTimes;

    public EntityDivineSmite(EntityType<? extends LightningBolt> type, Level world) {
        super(type,world);
    }

    public void setAoe(float amount){
        aoe = amount;
    }
    public void setSensitive(boolean bool){
        sensitive = bool;
    }

    @Override
    public void tick() {
        this.baseTick();
        try {
            int life = ReflectionHandler.Entity.lightningLife.getInt(this);
            int flashes = ReflectionHandler.Entity.lightningFlash.getInt(this);
            ServerPlayer cause = (ServerPlayer) ReflectionHandler.Entity.lightningCause.get(this);

            if (life == 2) {
                if (this.level.isClientSide()) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
                } else {
                    Difficulty difficulty = this.level.getDifficulty();


                    ReflectionHandler.Entity.powerRod.invoke(this);
                    //I'm not dealing with this right now, deal with it
                    //clearCopperOnLightningStrike(this.level, this.getStrikePosition());
                    this.gameEvent(GameEvent.LIGHTNING_STRIKE);
                }
            }

            --life;
            if (life < 0) {
                if (flashes == 0) {
                    if (this.level instanceof ServerLevel) {
                        Predicate<? super Entity> test = (entity) -> {
                            return (!sensitive || entity instanceof LivingEntity) && entity.isAlive();
                        };
                        List<Entity> list = this.level.getEntities(this, new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D).inflate(aoe), test);

                        for (ServerPlayer serverplayer : ((ServerLevel) this.level).getPlayers((p_147157_) -> {
                            return p_147157_.distanceTo(this) < 256.0F;
                        })) {
                            CriteriaTriggers.LIGHTNING_STRIKE.trigger(serverplayer, this, list);
                        }
                    }

                    this.discard();
                } else if (life < -this.random.nextInt(10)) {
                    --flashes;
                    life = 1;
                    this.seed = this.random.nextLong();
                    //this.spawnFire(0);
                }
            }

            if (life >= 0) {
                if (!(this.level instanceof ServerLevel)) {
                    this.level.setSkyFlashTime(2);
                } else {
                    Predicate<? super Entity> test = (entity) -> {
                        return (!sensitive || entity instanceof LivingEntity) && entity.isAlive();
                    };
                    List<Entity> list1 = this.level.getEntities(this, new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D).inflate(aoe), test);
                    for (Entity entity : list1) {
                        if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this)){
                            if(entity instanceof Mob && ((Mob)entity).getMobType() == MobType.UNDEAD){
                                entity.hurt(DamageSource.MAGIC,getDamage()*2);//smite the entity for double damage

                                //activate any thunder hit overrides or similar, but don't damage again.
                                //we don't just damage twice in case something gets i-frames from damage.
                                float damage = getDamage();
                                this.setDamage(0);
                                entity.thunderHit((ServerLevel) this.level, this);
                                this.setDamage(damage);
                            }
                            else{
                                entity.thunderHit((ServerLevel) this.level, this);
                            }

                            if (!this.level.isClientSide && !this.hitEntities.contains(entity.getId()) && entity instanceof LivingEntity) {
                                MobEffectInstance effectInstance = ((LivingEntity)entity).getEffect((MobEffect) ModPotions.SHOCKED_EFFECT.get());
                                int amp = effectInstance != null ? effectInstance.getAmplifier() : -1;
                                ((LivingEntity)entity).addEffect(new MobEffectInstance((MobEffect)ModPotions.SHOCKED_EFFECT.get(), 200 + 200 * this.extendTimes, Math.min(2, amp + 1)));
                            }
                        }
                    }

                    this.hitEntities.addAll(list1);
                    if (this.getCause() != null) {
                        CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.getCause(), list1);
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
