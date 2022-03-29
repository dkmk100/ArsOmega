package com.dkmk100.arsomega.events;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.init.ExperimentalStructureInit;
import com.dkmk100.arsomega.structures.CustomStructure;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CustomEvents {
    @SubscribeEvent
    public static void onRegisterStructures(final RegisterStructuresEvent event){
        ArsOmega.LOGGER.info("Caught structure register event");
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("demonic_dungeon",ExperimentalStructureInit.demonBiomes,false,0,100,150));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("test_structure",new String[]{},false,0,300,350));
    }
}
