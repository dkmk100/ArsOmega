package com.dkmk100.arsomega.capabilitysyncer;

import com.dkmk100.arsomega.ArsOmega;
import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.simple.SimpleChannel;

public class OmegaStatusesCapability extends LivingEntityCapability {

    boolean petrified = false;

    int petrificationProgress = 0;
    int petrificationLevel = 0;

    public void setPetrificationProgress(int value){
        if(value > 10){
            value = 10;
        }
        if(value < 0){
            value = 0;
        }
        petrificationProgress = value;
        this.updateTracking();
    }

    public void setPetrified(boolean value, int level){
        petrified = value;
        if(value == false){
            setPetrificationProgress(0);
            petrificationLevel = 0;
        }
        else{
            petrificationLevel = level;
        }
        this.updateTracking();
    }

    public boolean isPetrified() {
        return petrified;
    }

    public int getPetrificationLevel(){
        return petrificationLevel;
    }

    public int getPetrificationProgress(){
        return petrificationProgress;
    }

    protected OmegaStatusesCapability(LivingEntity entity) {
        super(entity);
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.livingEntity.getId(), OmegaStatusesCapabilityAttacher.LOCATION, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return OmegaStatusesCapabilityAttacher.channel;
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag nbt = new CompoundTag();

        nbt.putBoolean("petrified",petrified);
        nbt.putInt("petrify_progress",petrificationProgress);
        nbt.putInt("petrify_level",petrificationLevel);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        if(nbt.contains("petrified")) {
            petrified = nbt.getBoolean("petrified");
        }
        if(nbt.contains("petrify_progress")) {
            petrificationProgress = nbt.getInt("petrify_progress");
        }
        if(nbt.contains("petrify_level")){
            petrificationLevel = nbt.getInt("petrify_level");
        }
    }
}
