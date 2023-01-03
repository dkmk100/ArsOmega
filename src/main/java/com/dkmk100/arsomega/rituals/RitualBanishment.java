package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.events.CommonEvents;
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.lang.reflect.Field;
import java.util.List;

public class RitualBanishment extends AbstractRitual {
    protected void tick() {
        Level world = this.getWorld();
        BlockEntity tile = world.getBlockEntity(this.getPos());
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 100; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(new ParticleColor(255f, 100f, 4f)), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }

        if (!world.isClientSide && world.getGameTime() % 20L == 0L) {
            if (this.needsSourceNow()) {
                return;
            } else {
                this.setNeedsSource(true);
            }

            this.incrementProgress();
            if (this.getProgress() > 15) {
                List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AABB(this.getPos())).inflate(5.0D));
                for (LivingEntity entity : entities) {
                    ResourceKey<Level> registrykey = ResourceKey.create(Registry.DIMENSION_REGISTRY, RegistryHandler.DIMTYPE);
                    ServerLevel dest = world.getServer().getLevel(registrykey);
                    BlockPos pos = entity.blockPosition();
                    CommonEvents.teleportEntity(entity, new BlockPos(entity.position()).above(), dest, (ServerLevel) world);
                    dest.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
                    dest.setBlockAndUpdate(pos.above(),Blocks.AIR.defaultBlockState());
                    dest.setBlockAndUpdate(pos.above(2),Blocks.AIR.defaultBlockState());
                    dest.setBlockAndUpdate(pos.below(),Blocks.OBSIDIAN.defaultBlockState());

                    //play in both worlds lol, why not
                    dest.playSound(null,pos, SoundEvents.PORTAL_TRAVEL, SoundSource.MASTER, 2.0f, 1.0f);
                    world.playSound(null,pos, SoundEvents.PORTAL_TRAVEL, SoundSource.MASTER, 2.0f, 1.0f);
                }
                this.setFinished();
            }
        }
    }

    @Override
    public int getSourceCost() {
        return 2000;
    }

    @Override
    public boolean consumesSource() {
        return true;
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(250,120,5);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsOmega.MOD_ID,"banishment");
    }
}