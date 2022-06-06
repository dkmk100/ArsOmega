package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PortalBlockEntity extends BlockEntity {

    public String targetDim = "minecraft:overworld";

    public PortalBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public PortalBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(RegistryHandler.PortalType.get(), p_155229_, p_155230_);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("targetDim", targetDim);
    }

    @Override
    public void load(CompoundTag tag) {
        if(tag.contains("targetDim")) {
            this.targetDim = tag.getString("targetDim");
        }
        super.load(tag);
    }
}
