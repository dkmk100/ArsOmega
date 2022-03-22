package com.dkmk100.arsomega.base_blocks;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class WoodButton extends AbstractButtonBlock {
    public WoodButton(Block.Properties properties) {
        super(true, properties);
    }

    protected SoundEvent getSound(boolean isOn) {
        return isOn ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
    }
}
