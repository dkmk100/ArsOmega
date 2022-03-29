package com.dkmk100.arsomega.util;

import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.block.AbstractBlock;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

public class ReflectionHandler {
    public static Field blockProperties;
    public static Field destroyTime;
    public static Method startConverting;
    public static Field noiseFeatures;
    public static Field dimensionDefaults;
    public static Field structureConfig;
    public static Field structureFeatures;

    public static void Initialize() throws NoSuchFieldException, IllegalAccessException {
        blockProperties = ObfuscationReflectionHelper.findField(AbstractBlock.class,"field_235684_aB_");
        blockProperties.setAccessible(true);
        destroyTime = ObfuscationReflectionHelper.findField(AbstractBlock.Properties.class,"field_200959_g");
        destroyTime.setAccessible(true);
        startConverting = ObfuscationReflectionHelper.findMethod(ZombieVillagerEntity.class,"func_191991_a",new Class[]{UUID.class,int.class});
        startConverting.setAccessible(true);
        noiseFeatures = ObfuscationReflectionHelper.findField(Structure.class,"field_236384_t_");
        noiseFeatures.setAccessible(true);
        RemoveFinal(noiseFeatures);
        dimensionDefaults = ObfuscationReflectionHelper.findField(DimensionStructuresSettings.class,"field_236191_b_");
        dimensionDefaults.setAccessible(true);
        RemoveFinal(dimensionDefaults);
        structureConfig = ObfuscationReflectionHelper.findField(DimensionStructuresSettings.class,"field_236193_d_");
        structureConfig.setAccessible(true);
        RemoveFinal(structureConfig);
        structureFeatures = ObfuscationReflectionHelper.findField(FlatGenerationSettings.class,"field_202247_j");
        structureFeatures.setAccessible(true);
        RemoveFinal(structureFeatures);

    }

    public static void RemoveFinal(Field field) throws NoSuchFieldException, IllegalAccessException {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

}
