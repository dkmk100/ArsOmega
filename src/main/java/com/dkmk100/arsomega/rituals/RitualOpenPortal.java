package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.blocks.PortalBlockEntity;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RitualOpenPortal extends AbstractRitual {

    public RitualOpenPortal(){
        super();
    }

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

        if (!world.isClientSide && world.getGameTime() % 10L == 0L) {
            if(this.needsSourceNow()){
                return;
            }
            else{
                this.setNeedsSource(true);
            }
            this.incrementProgress();
            if (this.getProgress() > 20) {
                BlockPos pos = this.getPos().above();

                world.setBlockAndUpdate(pos, RegistryHandler.PORTAL_BLOCK.get().defaultBlockState());
                ItemStack crystal = this.getConsumedItems().get(0);
                if(crystal!=null && crystal.getTag().contains("dimension")) {
                    BlockEntity tile = world.getBlockEntity(pos);
                    if (tile != null && tile instanceof PortalBlockEntity) {
                        ((PortalBlockEntity) tile).targetDim = crystal.getTag().getString("dimension");
                    }
                }

                this.setFinished();
            }
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        int consumed = this.getConsumedItems().size();
        if(consumed==0){
            return stack.getItem()== RegistryHandler.DIMENSION_CRYSTAL.get();
        }

        return false;
    }

    @Override
    public boolean canStart() {
        return this.getConsumedItems().size() > 0;
    }

    @Override
    public int getSourceCost() {
        return 5000;
    }

    @Override
    public boolean consumesSource() {
        return true;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName("open_portal");
    }
}
