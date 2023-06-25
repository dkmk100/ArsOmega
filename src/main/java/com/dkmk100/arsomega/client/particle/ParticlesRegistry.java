package com.dkmk100.arsomega.client.particle;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.client.particle.ColorParticleTypeData;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

@Mod.EventBusSubscriber(
        modid = ArsOmega.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ParticlesRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "arsomega");
    public static RegistryObject<ParticleType<ColorParticleTypeData>> DARK_GLOW_TYPE = PARTICLES.register("dark_glow", () -> {
        return new GlowParticleType();
    });;

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent evt) {
        Minecraft.getInstance().particleEngine.register(DARK_GLOW_TYPE.get(), DarkGlowParticleData::new);
    }

}
