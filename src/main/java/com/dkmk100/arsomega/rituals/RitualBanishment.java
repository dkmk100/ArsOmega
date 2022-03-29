package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.events.CommonEvents;
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import com.hollingsworth.arsnouveau.common.ritual.RitualFlight;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.lang.reflect.Field;
import java.util.List;

public class RitualBanishment  extends AbstractRitual {
    protected void tick() {
        World world = this.getWorld();
        TileEntity tile = world.getBlockEntity(this.getPos());
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 100; ++i) {
                Vector3d particlePos = (new Vector3d((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(new ParticleColor(255f, 100f, 4f)), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }

        if (!world.isClientSide && world.getGameTime() % 20L == 0L) {
            if (this.needsManaNow()) {
                return;
            } else {
                this.setNeedsMana(true);
            }

            this.incrementProgress();
            if (this.getProgress() > 15) {
                List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AxisAlignedBB(this.getPos())).inflate(5.0D));
                for (LivingEntity entity : entities) {
                    RegistryKey<World> registrykey = RegistryKey.create(Registry.DIMENSION_REGISTRY, RegistryHandler.DIMTYPE);
                    ServerWorld dest = world.getServer().getLevel(registrykey);
                    BlockPos pos = entity.blockPosition();
                    dest.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());
                    dest.setBlockAndUpdate(pos.above(),Blocks.AIR.defaultBlockState());
                    dest.setBlockAndUpdate(pos.above(2),Blocks.AIR.defaultBlockState());
                    dest.setBlockAndUpdate(pos.below(),Blocks.OBSIDIAN.defaultBlockState());
                    CommonEvents.teleportEntity(entity, new BlockPos(entity.position()).above(), dest, (ServerWorld) world);
                }
                this.setFinished();
            }
        }
    }

    @Override
    public int getManaCost() {
        return 2000;
    }

    @Override
    public boolean consumesMana() {
        return true;
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(250,120,5);
    }

    @Override
    public String getID() {
        return "banishment";
    }
}