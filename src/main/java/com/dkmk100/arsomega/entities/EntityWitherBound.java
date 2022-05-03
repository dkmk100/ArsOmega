package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.common.entity.IFollowingSummon;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.shadowed.eliotlash.mclib.utils.MathHelper;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityWitherBound extends WitherBoss implements IFollowingSummon, ISummon {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.OPTIONAL_UUID);

    private LivingEntity owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean limitedLifespan;
    private int limitedLifeTicks;

    private final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (entity) -> entity instanceof Mob && ((Mob)entity).getTarget() != null && ((Mob)entity).getTarget().equals(this.owner) && this.owner!=null || this.owner!=null && entity.getKillCredit() != null && entity.getKillCredit().equals(this.owner);

    private final TargetingConditions BOUND_TARGETING_CONDITIONS = TargetingConditions.forCombat().range(20.0D).selector(LIVING_ENTITY_SELECTOR);

    @Override
    protected void customServerAiStep() {
        if (this.getInvulnerableTicks() > 0) {
            int j1 = this.getInvulnerableTicks() - 1;
            if (j1 <= 0) {
                Explosion.BlockInteraction explosion$mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
                this.level.explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, explosion$mode);
                if (!this.isSilent()) {
                    this.level.globalLevelEvent(1023, this.blockPosition(), 0);
                }
            }

            this.setInvulnerableTicks(j1);
            if (this.tickCount % 10 == 0) {
                this.heal(10.0F);
            }

        } else {
            //don't super AI step lol, we override for a reason

            try {

                final int[] nextHeadUpdate = (int[]) ReflectionHandler.Entity.witherHeadUpdates.get(this);

                final int[] idleHeadUpdates = (int[]) ReflectionHandler.Entity.witherIdleHeads.get(this);

                final ServerBossEvent bossEvent = (ServerBossEvent) ReflectionHandler.Entity.witherBossBar.get(this);


                LivingEntity myOwner = getOwnerFromID();
                int destroyBlocksTick = ReflectionHandler.Entity.witherBlocksTick.getInt(this);

                for (int i = 1; i < 3; ++i) {
                    if (this.tickCount >= nextHeadUpdate[i - 1]) {
                        nextHeadUpdate[i - 1] = this.tickCount + 8 + this.random.nextInt(8);//buffed from 10, 10 in normal wither
                        if (getTarget() != null) {
                            int j3 = i - 1;
                            int k3 = idleHeadUpdates[i - 1];
                            idleHeadUpdates[j3] = idleHeadUpdates[i - 1] + 1;
                            if (k3 > 15) {
                                float f = 10.0F;
                                float f1 = 5.0F;
                                double d0 = Mth.nextDouble(this.random, this.getX() - 10.0D, this.getX() + 10.0D);
                                double d1 = Mth.nextDouble(this.random, this.getY() - 5.0D, this.getY() + 5.0D);
                                double d2 = Mth.nextDouble(this.random, this.getZ() - 10.0D, this.getZ() + 10.0D);
                                ReflectionHandler.Entity.witherRangedPos.invoke(this, i + 1, d0, d1, d2, true);
                                idleHeadUpdates[i - 1] = 0;
                            }
                        }
                        int k1 = this.getAlternativeTarget(i);
                        if (k1 > 0) {
                            Entity entity = this.level.getEntity(k1);
                            if (entity != null && entity.isAlive() && !(this.distanceToSqr(entity) > 16900.0D) && this.hasLineOfSight(entity)) {
                                if (entity instanceof Player && (((Player) entity).getAbilities().invulnerable || entity == myOwner)) {
                                    this.setAlternativeTarget(i, 0);
                                } else {
                                    ReflectionHandler.Entity.witherRangedEntity.invoke(this, i + 1, (LivingEntity) entity);
                                    nextHeadUpdate[i - 1] = this.tickCount + 15 + this.random.nextInt(8);//buffed from 40, 20 in default wither
                                    idleHeadUpdates[i - 1] = 0;
                                }
                            } else {
                                this.setAlternativeTarget(i, 0);
                            }
                        } else {
                            List<LivingEntity> list = this.level.getNearbyEntities(LivingEntity.class, BOUND_TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));

                            for (int j2 = 0; j2 < 10 && !list.isEmpty(); ++j2) {
                                LivingEntity livingentity = list.get(this.random.nextInt(list.size()));
                                if (livingentity != this && livingentity.isAlive() && this.hasLineOfSight(livingentity)) {
                                    if (livingentity instanceof Player) {
                                        if (!((Player) livingentity).getAbilities().invulnerable && livingentity != myOwner) {
                                            this.setAlternativeTarget(i, livingentity.getId());
                                        }
                                    } else {
                                        this.setAlternativeTarget(i, livingentity.getId());
                                    }
                                    break;
                                }

                                list.remove(livingentity);
                            }
                        }
                    }
                }

                if (this.getTarget() != null) {
                    this.setAlternativeTarget(0, this.getTarget().getId());
                } else {
                    this.setAlternativeTarget(0, 0);
                }

                if (destroyBlocksTick > 0) {
                    --destroyBlocksTick;
                    if (destroyBlocksTick == 0 && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
                        int i1 = Mth.floor(this.getY());
                        int l1 = Mth.floor(this.getX());
                        int i2 = Mth.floor(this.getZ());
                        boolean flag = false;

                        for (int k2 = -1; k2 <= 1; ++k2) {
                            for (int l2 = -1; l2 <= 1; ++l2) {
                                for (int j = 0; j <= 3; ++j) {
                                    int i3 = l1 + k2;
                                    int k = i1 + j;
                                    int l = i2 + l2;
                                    BlockPos blockpos = new BlockPos(i3, k, l);
                                    BlockState blockstate = this.level.getBlockState(blockpos);
                                    if (blockstate.canEntityDestroy(this.level, blockpos, this) && net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this, blockpos, blockstate)) {
                                        flag = this.level.destroyBlock(blockpos, true, this) || flag;
                                    }
                                }
                            }
                        }

                        if (flag) {
                            this.level.levelEvent((Player) null, 1022, this.blockPosition(), 0);
                        }
                    }
                }

                if (this.tickCount % 40 == 0) {
                    this.heal(1.0F);
                }

                bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
                if (myOwner != null) {
                    if(myOwner.getName()!=null) {
                        bossEvent.setName(new TextComponent(myOwner.getName().getContents() + "'s Bound Wither"));
                    }
                    else{
                        bossEvent.setName(new TextComponent("Unbound Wither"));
                    }
                }
                else{

                    bossEvent.setName(new TextComponent("Unbound Wither"));
                }
                ReflectionHandler.Entity.witherHeadUpdates.set(this, nextHeadUpdate);
                ReflectionHandler.Entity.witherIdleHeads.set(this, idleHeadUpdates);
                ReflectionHandler.Entity.witherBlocksTick.set(this, destroyBlocksTick);//since it's an int which is a primitive type
            } catch (Exception e) {
                ArsOmega.LOGGER.error(e.toString());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public EntityType<?> getType() {
        return RegistryHandler.WITHER_BOUND.get();//need to replace
    }

    public EntityWitherBound(Level p_i50190_2_, LivingEntity owner) {
        super(RegistryHandler.WITHER_BOUND.get(), p_i50190_2_);
        this.owner = owner;
        this.limitedLifespan = false;
        this.setOwnerId(owner.getUUID());
        //this.moveControl = new EntityWitherBound.MoveHelperController(this);
    }

    public EntityWitherBound(EntityType<? extends WitherBoss> type, Level p_i50226_2_) {
        super(RegistryHandler.WITHER_BOUND.get(), p_i50226_2_);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityWitherBound.MoveRandomGoal());
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 30, 40.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new EntityWitherBound.CopyOwnerTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Mob.class, 10, false, true,
                (entity) -> entity instanceof Mob && ((Mob)entity).getTarget() != null && ((Mob)entity).getTarget().equals(this.owner) || entity instanceof LivingEntity && ((LivingEntity) entity).getKillCredit() != null && ((LivingEntity) entity).getKillCredit().equals(this.owner))
        );
    }

    @Override
    public Level getWorld() {
        return this.level;
    }

    @Override
    public PathNavigation getPathNav() {
        return this.navigation;
    }

    @Override
    public LivingEntity getSummoner() {
        return null;
    }

    @Override
    public Mob getSelfEntity() {
        return null;
    }

    @Nullable
    public UUID getOwnerId() {
        return (UUID)((Optional)this.entityData.get(OWNER_ID)).orElse((Object)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_ID, Optional.ofNullable(p_184754_1_));
    }

    public LivingEntity getOwnerFromID() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        try {
            this.entityData.define(OWNER_ID, Optional.empty());
        }
        catch (Exception e){
            ArsOmega.LOGGER.error(e.getMessage());
            try {
                this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
            }
            catch (Exception e2){
                ArsOmega.LOGGER.error(e2.getMessage());
            }
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.boundOrigin != null) {
            compound.putInt("BoundX", this.boundOrigin.getX());
            compound.putInt("BoundY", this.boundOrigin.getY());
            compound.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.limitedLifespan) {
            compound.putInt("LifeTicks", this.limitedLifeTicks);
        }

        if (this.getOwnerID() == null) {
            compound.putUUID("OwnerUUID", Util.NIL_UUID);
        } else {
            compound.putUUID("OwnerUUID", this.getOwnerID());
        }

    }

    public void setLimitedLife(int p_190653_1_) {
        this.limitedLifespan = true;
        this.limitedLifeTicks = p_190653_1_;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BoundX")) {
            this.boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
        }

        if (compound.contains("LifeTicks")) {
            this.setLimitedLife(compound.getInt("LifeTicks"));
        }

        UUID s;
        if (compound.contains("OwnerUUID", 8)) {
            s = compound.getUUID("OwnerUUID");
        } else {
            String s1 = compound.getString("Owner");
            s = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s1);
        }

        if (s != null) {
            try {
                this.setOwnerID(s);
                this.setOwner(getOwnerFromID());
            } catch (Throwable var4) {
            }
        }

    }

    @Override
    protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
        //we just don't drop any custom loot, this is a minion after all
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        this.onSummonDeath(this.level, cause, false);
    }

    @Override
    public int getTicksLeft() {
        return this.limitedLifeTicks;
    }

    @Override
    public void setTicksLeft(int ticks) {
        this.limitedLifeTicks = ticks;
    }

    @Override
    @Nullable
    public UUID getOwnerID() {
        return !((Optional)this.getEntityData().get(OWNER_ID)).isPresent() ? this.getUUID() : (UUID)((Optional)this.getEntityData().get(OWNER_ID)).get();
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.getEntityData().set(OWNER_ID, Optional.ofNullable(uuid));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 10;
            this.hurt(DamageSource.OUT_OF_WORLD, 20.0F);
        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !EntityWitherBound.this.getMoveControl().hasWanted() && EntityWitherBound.this.random.nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = boundOrigin;
            if (blockpos == null) {
                blockpos = new BlockPos(EntityWitherBound.this.blockPosition());
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityWitherBound.this.random.nextInt(15) - 7, EntityWitherBound.this.random.nextInt(11) - 5, EntityWitherBound.this.random.nextInt(15) - 7);
                if (EntityWitherBound.this.level.isEmptyBlock(blockpos1)) {
                    EntityWitherBound.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 0.25);
                    if (EntityWitherBound.this.getTarget() == null) {
                        EntityWitherBound.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    class DoNothingGoal extends Goal {
        public DoNothingGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return EntityWitherBound.this.getInvulnerableTicks() > 0;
        }
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
        this.setOwnerID(owner.getUUID());
    }

    class CopyOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

        public CopyOwnerTargetGoal(PathfinderMob creature) {
            super(creature, false);
        }

        public boolean canUse() {
            return EntityWitherBound.this.owner != null && EntityWitherBound.this.owner.getLastHurtMob() != null;
        }

        public void start() {
            EntityWitherBound.this.setTarget(EntityWitherBound.this.owner.getLastHurtMob());
            super.start();
        }
    }
}
