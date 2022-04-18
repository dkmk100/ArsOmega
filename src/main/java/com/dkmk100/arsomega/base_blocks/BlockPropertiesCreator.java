package com.dkmk100.arsomega.base_blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class BlockPropertiesCreator {
    public BlockBehaviour.Properties create(Material material, float hardness, float resistance, SoundType soundType, boolean needsTool){
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(material).strength(hardness,resistance).sound(soundType);
        if(needsTool){
            properties = properties.requiresCorrectToolForDrops();
        }
        return properties;
    }
}
