package com.dkmk100.arsomega.base_blocks;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockPropertiesCreator {
    public Properties create(Material material, float hardness, float resistance, int harvestLevel, SoundType soundType, ToolType harvestTool){
        Properties properties = Properties.of(material).strength(hardness,resistance).harvestLevel(harvestLevel).harvestTool(harvestTool).sound(soundType);
        return properties;
    }
}
