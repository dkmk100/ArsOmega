package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.ArsOmega;
//import net.minecraft.world.dimension.DimensionType;
//import net.minecraftforge.common.DimensionManager;
//import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsOmega.MOD_ID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBusSubscriber {
    /*
    @SubscribeEvent
    public static void RegisterDimensions(final RegisterDimensionsEvent event) {
        if(DimensionType.byName(RegistryHandler.DIMTYPE)==null){
            DimensionManager.registerDimension(RegistryHandler.DIMTYPE, RegistryHandler.VOID_WORLD.get(),null,true);
        }
    }
     */
}
