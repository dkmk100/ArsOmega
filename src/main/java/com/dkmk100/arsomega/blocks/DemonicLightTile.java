package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class DemonicLightTile extends ModdedTile {
    public int red = 255;
    public int green = 125;
    public int blue = 255;

    public DemonicLightTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.DemonicLightType.get(), pos, state);
    }

    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.red = nbt.getInt("red");
        this.red = this.red > 0 ? this.red : 255;
        this.green = nbt.getInt("green");
        this.green = this.green > 0 ? this.green : 125;
        this.blue = nbt.getInt("blue");
        this.blue = this.blue > 0 ? this.blue : 255;
    }

    public void saveAdditional(CompoundTag tag) {
        tag.putInt("red", this.red);
        tag.putInt("green", this.green);
        tag.putInt("blue", this.blue);
    }
}
