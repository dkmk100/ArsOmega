package com.dkmk100.arsomega.events;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsOmega.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value= Dist.DEDICATED_SERVER)
public class ServerEvents {

}
