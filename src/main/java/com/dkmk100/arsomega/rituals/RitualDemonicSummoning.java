package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class RitualDemonicSummoning  extends AbstractRitual {
    @Override
    protected void tick() {
        Level world = this.getWorld();
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 100; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(this.getCenterColor()), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }

        if (!world.isClientSide && world.getGameTime() % 20L == 0L) {
            if (this.needsSourceNow()) {
                return;
            } else {
                this.setNeedsSource(true);
            }
            this.incrementProgress();
            if (this.getProgress() > 10) {
                BlockPos pos = this.getPos();
                EntityType target = RegistryHandler.BASIC_DEMON.get();

                for (int i = 0; i < 5; i++)
                    target.spawn((ServerLevel) world, null, null, pos.above(), MobSpawnType.MOB_SUMMONED, false, false);
                    world.playSound(null,pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 2.0f, 1.0f);
                    this.setFinished();

            }


        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return false;
    }

    @Override
    public int getSourceCost() {
        return 1000;
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(250, 120, 5);
    }

    @Override
    public boolean consumesSource() {
        return true;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName("demonic_summoning");
    }
}
