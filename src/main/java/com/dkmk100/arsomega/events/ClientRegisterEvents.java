package com.dkmk100.arsomega.events;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.renderLayer.CustomRenderType;
import com.dkmk100.arsomega.client.renderLayer.PetrificationLayer;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ArsOmega.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegisterEvents {


    @SubscribeEvent()
    public static void registerSpawnEggColors(RegisterColorHandlersEvent.Block event)
    {
        event.getBlockColors().register((state, getter, pos, p1) -> RegistryHandler.CHALK_LINE_1.get().GetColor(state,pos, getter), RegistryHandler.CHALK_LINE_1.get());
        event.getBlockColors().register((state, getter, pos, p1) -> RegistryHandler.CHALK_LINE_2.get().GetColor(state,pos, getter), RegistryHandler.CHALK_LINE_2.get());
        event.getBlockColors().register((state, getter, pos, p1) -> RegistryHandler.CHALK_LINE_3.get().GetColor(state,pos, getter), RegistryHandler.CHALK_LINE_3.get());
        event.getBlockColors().register((state, getter, pos, p1) -> RegistryHandler.CHALK_LINE_4.get().GetColor(state,pos, getter), RegistryHandler.CHALK_LINE_4.get());

    }

    @SubscribeEvent
    public static void registerShaders(final RegisterShadersEvent event){
        CustomRenderType.registerShaders(event);
    }

    @SubscribeEvent
    public static void addRenderLayers(final EntityRenderersEvent.AddLayers event){
        PetrificationLayer.addRenderLayers(event);
    }


}
