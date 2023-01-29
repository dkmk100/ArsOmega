package com.dkmk100.arsomega.client.particle;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.client.particle.ColorParticleTypeData;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(
        modid = ArsOmega.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ParticlesRegistry {
    @ObjectHolder("arsomega:dark_glow")
    public static ParticleType<ColorParticleTypeData> DARK_GLOW_TYPE;


    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        IForgeRegistry<ParticleType<?>> r = event.getRegistry();
        r.register((ParticleType)(new GlowParticleType()).setRegistryName("dark_glow"));
    }

    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent evt) {
        Minecraft.getInstance().particleEngine.register(DARK_GLOW_TYPE, DarkGlowParticleData::new);
    }
}
