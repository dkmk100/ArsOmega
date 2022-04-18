package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

public class ReflectionHandler {
    public static Field blockProperties;
    public static Field destroyTime;
    //public static Method startConverting;
    //public static Field noiseFeatures;
    public static Field xRot;
    public static Field yRot;
    //public static Field dimensionDefaults;
    //public static Field structureConfig;
    //public static Field structureFeatures;

    public static void Initialize() throws NoSuchFieldException, IllegalAccessException {
        blockProperties = ObfuscationReflectionHelper.findField(BlockBehaviour.class,"f_60439_");
        blockProperties.setAccessible(true);
        destroyTime = ObfuscationReflectionHelper.findField(BlockBehaviour.Properties.class,"f_60888_");
        destroyTime.setAccessible(true);
        /*
        startConverting = ObfuscationReflectionHelper.findMethod(ZombieVillager.class,"func_191991_a",new Class[]{UUID.class,int.class});
        startConverting.setAccessible(true);
        noiseFeatures = ObfuscationReflectionHelper.findField(StructureFeature.class,"field_236384_t_");
        noiseFeatures.setAccessible(true);
        RemoveFinal(noiseFeatures);
         */
        xRot = ObfuscationReflectionHelper.findField(Entity.class,"f_19858_");
        xRot.setAccessible(true);
        RemoveFinal(xRot);
        yRot = ObfuscationReflectionHelper.findField(Entity.class,"f_19857_");
        yRot.setAccessible(true);
        RemoveFinal(yRot);
        /*
        dimensionDefaults = ObfuscationReflectionHelper.findField(StructureSettings.class,"field_236191_b_");
        dimensionDefaults.setAccessible(true);
        RemoveFinal(dimensionDefaults);
        structureConfig = ObfuscationReflectionHelper.findField(StructureSettings.class,"field_236193_d_");
        structureConfig.setAccessible(true);
        RemoveFinal(structureConfig);
        structureFeatures = ObfuscationReflectionHelper.findField(FlatLevelGeneratorSettings.class,"field_202247_j");
        structureFeatures.setAccessible(true);
        RemoveFinal(structureFeatures);

         */

    }

    public static void RemoveFinal(Field field) {
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }
        catch (Exception e){
            ArsOmega.LOGGER.error(e.getMessage());
            try {
                var lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
                VarHandle MODIFIERS = lookup.findVarHandle(Field.class, "modifiers", int.class);
                MODIFIERS.set(field, field.getModifiers() & ~Modifier.FINAL);
            }
            catch (Exception e2){
                ArsOmega.LOGGER.error(e2.getMessage());
            }
        }
    }

}
