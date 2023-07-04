package com.dkmk100.arsomega.client.particle;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.client.particle.ColorParticleTypeData;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.logging.LogManager;

@Mod.EventBusSubscriber(
        modid = ArsOmega.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class ParticlesRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "arsomega");
    public static RegistryObject<ParticleType<ColorParticleTypeData>> DARK_GLOW_TYPE = PARTICLES.register("dark_glow", () -> new GlowParticleType());;

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent evt) {
        ArsOmega.LOGGER.info("registering particle factories");
        evt.register(DARK_GLOW_TYPE.get(), DarkGlowParticleData::new);
    }

    public static void RegisterParticles(IEventBus bus){
        ArsOmega.LOGGER.info("registering particles");
        PARTICLES.register(bus);
    }

}
