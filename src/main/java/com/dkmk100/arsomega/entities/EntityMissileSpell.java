package com.dkmk100.arsomega.entities;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.SpellPrismBlock;
import com.hollingsworth.arsnouveau.common.entity.ColoredProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect.EffectType;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.java.games.input.Component;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity;

import javax.annotation.Nullable;

public class EntityMissileSpell extends EntityProjectileSpell {
    public int age;
    public SpellResolver spellResolver;
    public int pierceLeft;
    public int numSensitive;
    public boolean activateOnEmpty;
    int maxAge = 200;
    @Nullable Entity caster;
    public Set<BlockPos> hitList = new HashSet();

    public EntityMissileSpell(EntityType<? extends EntityMissileSpell> entityType, World world) {
        super(entityType, world);
    }

    public EntityMissileSpell(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityMissileSpell(World world, SpellResolver resolver) {
        super(world, resolver.spellContext.caster);
        this.spellResolver = resolver;
        this.pierceLeft = resolver.spell.getBuffsAtIndex(0, resolver.spellContext.caster, AugmentPierce.INSTANCE);
        this.numSensitive = resolver.spell.getBuffsAtIndex(0, resolver.spellContext.caster, AugmentSensitive.INSTANCE);
        resolver.spellContext.colors.makeVisible();
        this.setColor(resolver.spellContext.colors);
        this.maxAge = 200;
        this.activateOnEmpty = true;
    }
    public EntityMissileSpell(World world, SpellResolver resolver, int maxAge, boolean activate, @Nullable Entity caster){
        super(world, resolver.spellContext.caster);
        this.spellResolver = resolver;
        this.pierceLeft = resolver.spell.getBuffsAtIndex(0, resolver.spellContext.caster, AugmentPierce.INSTANCE);
        this.numSensitive = resolver.spell.getBuffsAtIndex(0, resolver.spellContext.caster, AugmentSensitive.INSTANCE);
        resolver.spellContext.colors.makeVisible();
        this.setColor(resolver.spellContext.colors);
        this.maxAge = maxAge;
        this.activateOnEmpty = activate;
    }

    public EntityMissileSpell(World world, LivingEntity shooter) {
        super(world, shooter);
    }

    @Override
    public void tick() {
        ++this.age;
        Vector3d vector3d = this.getDeltaMovement();
        if (this.age > this.maxAge) {
            ExplodeMissile();
            this.remove();
        } else {
            this.xOld = this.getX();
            this.yOld = this.getY();
            this.zOld = this.getZ();
            if (this.inGround) {
                this.inGround = false;
                this.setDeltaMovement(this.getDeltaMovement());
            }

            Vector3d vector3d2 = this.position();
            Vector3d vector3d3 = vector3d2.add(vector3d);
            RayTraceResult raytraceresult = this.level.clip(new RayTraceContext(vector3d2, vector3d3, this.numSensitive > 0 ? BlockMode.OUTLINE : BlockMode.COLLIDER, FluidMode.NONE, this));
            if (raytraceresult != null && ((RayTraceResult)raytraceresult).getType() != Type.MISS) {
                vector3d3 = ((RayTraceResult)raytraceresult).getLocation();
            }

            EntityRayTraceResult entityraytraceresult = this.findHitEntity(vector3d2, vector3d3);
            if (entityraytraceresult != null) {
                raytraceresult = entityraytraceresult;
            }

            if (raytraceresult != null && raytraceresult instanceof EntityRayTraceResult) {
                Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
                Entity entity1 = this.getOwner();
                if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity)entity1).canHarmPlayer((PlayerEntity)entity)) {
                    raytraceresult = null;
                }
            }

            if (raytraceresult != null && ((RayTraceResult)raytraceresult).getType() != Type.MISS && !ForgeEventFactory.onProjectileImpact(this, (RayTraceResult)raytraceresult)) {
                this.onHit((RayTraceResult)raytraceresult);
                this.hasImpulse = true;
            }

            if (raytraceresult != null && ((RayTraceResult)raytraceresult).getType() == Type.MISS && raytraceresult instanceof BlockRayTraceResult) {
                BlockRegistry.PORTAL_BLOCK.onProjectileHit(this.level, this.level.getBlockState(new BlockPos(((RayTraceResult)raytraceresult).getLocation())), (BlockRayTraceResult)raytraceresult, this);
            }

            Vector3d vec3d = this.getDeltaMovement();
            double x = this.getX() + vec3d.x;
            double y = this.getY() + vec3d.y;
            double z = this.getZ() + vec3d.z;
            if (!this.isNoGravity()) {
                Vector3d vec3d1 = this.getDeltaMovement();
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
    }

    protected void ActivateSpellAtPos(Vector3d pos){
        if(!this.level.isClientSide() && this.spellResolver != null){
            final int sideOffset = 3;
            final int upOffset = 1;
            Vector3d offset = new Vector3d(sideOffset,upOffset,sideOffset);
            AxisAlignedBB axis = new AxisAlignedBB(pos.x+offset.x,pos.y+offset.y,pos.z+offset.z,pos.x-offset.x,pos.y-offset.y,pos.z-offset.z);
            List<Entity> entities = this.level.getEntities((Entity) null,axis,null);
            boolean foundEntity = false;
            for(Entity entity : entities){
                if(entity instanceof LivingEntity && entity!=caster){
                    foundEntity = true;
                    ActivateSpellAtEntity((LivingEntity)entity);
                }
            }
            if(!foundEntity){
                Vector3d vector3d2 = this.position();
                Vector3d dist = this.position().subtract(this.getOwner().position());
                this.spellResolver.onResolveEffect(this.level, (LivingEntity)this.getOwner(), new BlockRayTraceResult(vector3d2, Direction.getNearest(dist.x,dist.y,dist.z),new BlockPos(vector3d2),true));
            }
        }
    }
    protected void ActivateSpellAtEntity(LivingEntity entity){
        this.spellResolver.onResolveEffect(this.level, (LivingEntity)this.getOwner(), new EntityRayTraceResult(entity));
    }


    @Override
    protected void attemptRemoval() {
        --this.pierceLeft;
        if (this.pierceLeft < 0) {
            ExplodeMissile();
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove();
        }
    }

    protected void ExplodeMissile(){
        this.ActivateSpellAtPos(this.position());
        Networking.sendToNearby(this.level, new BlockPos(this.position()), new PacketANEffect(EffectType.BURST, new BlockPos(this.position()), this.getParticleColorWrapper(), new int[0]));
    }

    public EntityMissileSpell(SpawnEntity packet, World world) {
        super(ModEntities.SPELL_PROJ, world);
    }

    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("maxAge")) {
            this.maxAge = tag.getInt("maxAge");
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("maxAge", this.maxAge);
    }
}

