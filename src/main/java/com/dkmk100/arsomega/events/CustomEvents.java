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
                CustomStructure("demonic_dungeon",ExperimentalStructureInit.demonBiomes,false,0,175,205));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("wizard_tower",new String[]{},false,-8,300,350));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("demonic_fossil",ExperimentalStructureInit.demonBiomes,true,5,75,110));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("demon_pen",ExperimentalStructureInit.demonBiomes,false,-2,200,250));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("demon_temple",ExperimentalStructureInit.demonBiomes,false,0,225,270));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("demon_chair",ExperimentalStructureInit.demonBiomes,false,0,300,350));
        ExperimentalStructureInit.RegisterStructure(new
                CustomStructure("wizard_dungeon",new String[]{},false,0,200,250));
    }
}
