package com.dkmk100.arsomega.base_blocks;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class WoodButton extends ButtonBlock {
    public WoodButton(Block.Properties properties) {
        super(true, properties);
    }

    protected SoundEvent getSound(boolean isOn) {
        return isOn ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
    }
}
