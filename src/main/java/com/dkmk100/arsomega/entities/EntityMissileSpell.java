package com.dkmk100.arsomega.entities;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect.EffectType;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.entity.Entity.RemovalReason;

public class EntityMissileSpell extends EntityProjectileSpell {
    public int age;
    public SpellResolver spellResolver;
    public float aoe;
    public boolean activateOnEmpty;
    int maxAge = 200;
    @Nullable Entity caster;
    public Set<BlockPos> hitList = new HashSet();

    public EntityMissileSpell(EntityType<? extends EntityMissileSpell> entityType, Level world) {
        super(entityType, world);
    }

    public EntityMissileSpell(Level world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityMissileSpell(Level world, SpellResolver resolver) {
        super(RegistryHandler.ENTITY_MISSILE.get(), world, resolver);
        this.aoe = resolver.spell.getBuffsAtIndex(0,null, AugmentAOE.INSTANCE);
        this.maxAge = 200;
        this.activateOnEmpty = true;
    }
    public EntityMissileSpell(Level world, SpellResolver resolver, int maxAge, boolean activate, float aoe, @Nullable Entity caster){
        super(RegistryHandler.ENTITY_MISSILE.get(), world, resolver);
        this.spellResolver = resolver;
        this.aoe = aoe;
        this.maxAge = maxAge;
        this.activateOnEmpty = activate;
    }

    public EntityMissileSpell(Level world, LivingEntity shooter) {
        super(world, shooter);
    }

    @Override
    public void tick() {
        ++this.age;
        Vec3 vector3d = this.getDeltaMovement();
        if (this.age > this.maxAge) {
            ExplodeMissile();
            this.remove(RemovalReason.DISCARDED);
        } else {
            this.xOld = this.getX();
            this.yOld = this.getY();
            this.zOld = this.getZ();
            if (this.onGround) {
                this.onGround = false;
                this.setDeltaMovement(this.getDeltaMovement());
            }

            Vec3 vector3d2 = this.position();
            Vec3 vector3d3 = vector3d2.add(vector3d);
            HitResult raytraceresult = this.level.clip(new ClipContext(vector3d2, vector3d3, this.numSensitive > 0 ? Block.OUTLINE : Block.COLLIDER, Fluid.NONE, this));
            if (raytraceresult != null && ((HitResult)raytraceresult).getType() != Type.MISS) {
                vector3d3 = ((HitResult)raytraceresult).getLocation();
            }

            EntityHitResult entityraytraceresult = this.findHitEntity(vector3d2, vector3d3);
            if (entityraytraceresult != null) {
                raytraceresult = entityraytraceresult;
            }

            if (raytraceresult != null && raytraceresult instanceof EntityHitResult) {
                Entity entity = ((EntityHitResult)raytraceresult).getEntity();
                Entity entity1 = this.getOwner();
                if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                    raytraceresult = null;
                }
                //fixes missile bug from before
                this.setPos(entity.position());
                ExplodeMissile();
            }

            if (raytraceresult != null && ((HitResult)raytraceresult).getType() != Type.MISS && !ForgeEventFactory.onProjectileImpact(this, (HitResult)raytraceresult)) {
                this.onHit((HitResult)raytraceresult);
                this.hasImpulse = true;
            }

            if (raytraceresult != null && ((HitResult)raytraceresult).getType() == Type.MISS && raytraceresult instanceof BlockHitResult) {
                BlockRegistry.PORTAL_BLOCK.onProjectileHit(this.level, this.level.getBlockState(new BlockPos(((HitResult)raytraceresult).getLocation())), (BlockHitResult)raytraceresult, this);
            }

            Vec3 vec3d = this.getDeltaMovement();
            double x = this.getX() + vec3d.x;
            double y = this.getY() + vec3d.y;
            double z = this.getZ() + vec3d.z;
            if (!this.isNoGravity()) {
                Vec3 vec3d1 = this.getDeltaMovement();
                this.setDeltaMovement(vec3d1.x, vec3d1.y, vec3d1.z);
            }

            this.setPos(x, y, z);
            if (this.level.isClientSide && this.age > 2) {
                for(int i = 0; i < 10; ++i) {
                    double deltaX = this.getX() - this.xOld;
                    double deltaY = this.getY() - this.yOld;
                    double deltaZ = this.getZ() - this.zOld;
                    double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8.0D);

                    for(double j = 0.0D; j < dist; ++j) {
                        double coeff = j / dist;
                        this.level.addParticle(GlowParticleData.createData(this.getParticleColor()), (double)((float)(this.xo + deltaX * coeff)), (double)((float)(this.yo + deltaY * coeff)), (double)((float)(this.zo + deltaZ * coeff)), (double)(0.0125F * (this.random.nextFloat() - 0.5F)), (double)(0.0125F * (this.random.nextFloat() - 0.5F)), (double)(0.0125F * (this.random.nextFloat() - 0.5F)));
                    }
                }
            }

        }
        this.baseTick();
    }

    protected void ActivateSpellAtPos(Vec3 pos){
        if(!this.level.isClientSide() && this.spellResolver != null){
            float sideOffset = 5f + 1.3f * aoe;
            float upOffset = 2f + aoe;
            Vec3 offset = new Vec3(sideOffset,upOffset,sideOffset);
            AABB axis = new AABB(pos.x+offset.x,pos.y+offset.y,pos.z+offset.z,pos.x-offset.x,pos.y-offset.y,pos.z-offset.z);
            List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class,axis, entity -> true);
            boolean foundEntity = false;
            for(LivingEntity entity : entities){
                if(entity!=caster){
                    foundEntity = true;
                    ActivateSpellAtEntity(entity);
                }
            }
            if(!foundEntity){
                Vec3 vector3d2 = this.position();
                Vec3 dist;
                if(this.getOwner()==null) {
                    dist = new Vec3(0,1,0);
                }
                else{
                    dist = this.position().subtract(this.getOwner().position());
                }
                this.spellResolver.onResolveEffect(this.level, new BlockHitResult(vector3d2, Direction.getNearest(dist.x,dist.y,dist.z),new BlockPos(vector3d2),true));
            }
        }
    }
    protected void ActivateSpellAtEntity(LivingEntity entity){
        this.spellResolver.onResolveEffect(this.level, new EntityHitResult(entity));
    }


    @Override
    protected void attemptRemoval() {
        --this.pierceLeft;
        if (this.pierceLeft < 0) {
            ExplodeMissile();
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    protected void ExplodeMissile(){
        this.ActivateSpellAtPos(this.position());
        Networking.sendToNearby(this.level, new BlockPos(this.position()), new PacketANEffect(EffectType.BURST, new BlockPos(this.position()), this.getParticleColorWrapper(), new int[0]));
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("maxAge")) {
            this.maxAge = tag.getInt("maxAge");
        }
        if (tag.contains("aoe")) {
            this.aoe = tag.getFloat("aoe");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("maxAge", this.maxAge);
        tag.putFloat("aoe", this.aoe);
    }
}
