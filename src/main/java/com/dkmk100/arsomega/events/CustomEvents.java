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
                CustomStructure("demonic_dungeon",ExperimentalStructureInit.demonBiomes,false,0,75,100));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("wizard_tower",new String[]{},false,-8,200,250));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("demonic_fossil",ExperimentalStructureInit.demonBiomes,true,5,50,75));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("demon_pen",ExperimentalStructureInit.demonBiomes,false,-2,100,110));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("demon_temple",ExperimentalStructureInit.demonBiomes,false,0,85,100));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("demon_chair",ExperimentalStructureInit.demonBiomes,false,0,150,200));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("wizard_dungeon",new String[]{},true,0,90,120));
    }
}
