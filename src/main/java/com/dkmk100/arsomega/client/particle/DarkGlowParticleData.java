package com.dkmk100.arsomega.client.particle;

import com.hollingsworth.arsnouveau.client.particle.ColorParticleTypeData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleGlow;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;


public class DarkGlowParticleData  implements ParticleProvider<ColorParticleTypeData> {
    private final SpriteSet spriteSet;
    public static final String NAME = "dark_glow";

    public DarkGlowParticleData(SpriteSet sprite) {
        this.spriteSet = sprite;
    }

    public Particle createParticle(ColorParticleTypeData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new ParticleDarkGlow(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(), data.alpha, data.size, data.age, this.spriteSet, data.disableDepthTest);
    }

    public static ParticleOptions createData(ParticleColor color) {
        return new ColorParticleTypeData(ParticlesRegistry.DARK_GLOW_TYPE.get(), color, false);
    }

    public static ParticleOptions createData(ParticleColor color, boolean disableDepthTest) {
        return new ColorParticleTypeData(ParticlesRegistry.DARK_GLOW_TYPE.get(), color, disableDepthTest, 0.25F, 0.75F, 36);
    }

    public static ParticleOptions createData(ParticleColor color, boolean disableDepthTest, float size, float alpha, int age) {
        return new ColorParticleTypeData(ParticlesRegistry.DARK_GLOW_TYPE.get(),color, disableDepthTest, size, alpha, age);
    }

    public static ParticleOptions createData(ParticleColor color, float size, float alpha, int age) {
        return new ColorParticleTypeData(ParticlesRegistry.DARK_GLOW_TYPE.get(), color, false, size, alpha, age);
    }
}

