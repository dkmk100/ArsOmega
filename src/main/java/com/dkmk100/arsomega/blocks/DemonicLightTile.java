package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class DemonicLightTile extends ModdedTile {
    public ParticleColor color;

    public DemonicLightTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.DemonicLightType.get(), pos, state);
        color = ParticleColor.defaultParticleColor();
    }

    public void load(CompoundTag nbt) {
        super.load(nbt);
        if(nbt.contains("color")) {
            this.color = ParticleColor.deserialize(nbt.getCompound("color"));
        }
        else{
            color = ParticleColor.defaultParticleColor();
            ArsOmega.LOGGER.error("missing particle color nbt at pos: "+getBlockPos());
        }
    }

    public void saveAdditional(CompoundTag tag) {
        if(color == null){
            color = ParticleColor.defaultParticleColor();
            ArsOmega.LOGGER.error("missing particle color at pos: "+getBlockPos());
        }
        tag.put("color", this.color.serialize());
    }
}
