package com.dkmk100.arsomega.base_blocks;

import net.minecraft.world.level.block.PressurePlateBlock;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WoodPressurePlate extends PressurePlateBlock {
    public WoodPressurePlate(Properties propertiesIn) {
        super(PressurePlateBlock.Sensitivity.EVERYTHING, propertiesIn);
    }
}
