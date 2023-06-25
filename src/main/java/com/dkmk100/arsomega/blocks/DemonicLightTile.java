package com.dkmk100.arsomega.blocks;

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
    }

    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.color = ParticleColor.deserialize(nbt.getCompound("color"));
    }

    public void saveAdditional(CompoundTag tag) {
        tag.put("color", this.color.serialize());
    }
}
