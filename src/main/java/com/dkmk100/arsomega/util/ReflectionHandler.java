package com.dkmk100.arsomega.util;

import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.block.AbstractBlock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public class ReflectionHandler {
    public static Field blockProperties;
    public static Field destroyTime;
    public static Method startConverting;

    public static void Initialize(){
        blockProperties = ObfuscationReflectionHelper.findField(AbstractBlock.class,"field_235684_aB_");
        blockProperties.setAccessible(true);
        destroyTime = ObfuscationReflectionHelper.findField(AbstractBlock.Properties.class,"field_200959_g");
        destroyTime.setAccessible(true);
        startConverting = ObfuscationReflectionHelper.findMethod(ZombieVillagerEntity.class,"func_191991_a",new Class[]{UUID.class,int.class});
        startConverting.setAccessible(true);
    }
}
