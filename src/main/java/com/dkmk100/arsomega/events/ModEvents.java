package com.dkmk100.arsomega.events;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsOmega.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ModEvents {
    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event){
        RegistryHandler.RegisterItems(event);
    }
}
