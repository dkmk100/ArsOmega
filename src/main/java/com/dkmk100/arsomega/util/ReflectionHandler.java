package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
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
    public static Field xRot;
    public static Field yRot;

    public static class Entity{
        public static Field witherHeadUpdates;
        public static Field witherIdleHeads;
        public static Field witherBlocksTick;
        public static Field witherBossBar;
        public static Method witherRangedPos;
        public static Method witherRangedEntity;
        protected static void Initialize(){
            witherHeadUpdates = ObfuscationReflectionHelper.findField(WitherBoss.class,"f_31427_");
            witherHeadUpdates.setAccessible(true);
            RemoveFinal(witherHeadUpdates);
            witherIdleHeads = ObfuscationReflectionHelper.findField(WitherBoss.class,"f_31428_");
            witherIdleHeads.setAccessible(true);
            RemoveFinal(witherIdleHeads);
            witherBlocksTick = ObfuscationReflectionHelper.findField(WitherBoss.class,"f_31429_");
            witherBlocksTick.setAccessible(true);
            witherBossBar = ObfuscationReflectionHelper.findField(WitherBoss.class,"f_31430_");
            witherBossBar.setAccessible(true);
            RemoveFinal(witherBossBar);
            witherRangedPos = ObfuscationReflectionHelper.findMethod(WitherBoss.class,"m_31448_",int.class,double.class,double.class,double.class,boolean.class);
            witherRangedEntity = ObfuscationReflectionHelper.findMethod(WitherBoss.class,"m_31457_",int.class, LivingEntity.class);
        }
    }

    public static void Initialize() {
        blockProperties = ObfuscationReflectionHelper.findField(BlockBehaviour.class, "f_60439_");
        blockProperties.setAccessible(true);
        destroyTime = ObfuscationReflectionHelper.findField(BlockBehaviour.Properties.class, "f_60888_");
        destroyTime.setAccessible(true);
        xRot = ObfuscationReflectionHelper.findField(net.minecraft.world.entity.Entity.class, "f_19858_");
        xRot.setAccessible(true);
        RemoveFinal(xRot);
        yRot = ObfuscationReflectionHelper.findField(net.minecraft.world.entity.Entity.class, "f_19857_");
        yRot.setAccessible(true);
        RemoveFinal(yRot);
        Entity.Initialize();
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
