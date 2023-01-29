package com.dkmk100.arsomega.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

import java.util.Random;

public class ParticleDarkGlow extends TextureSheetParticle {
    public float colorR = 0.0F;
    public float colorG = 0.0F;
    public float colorB = 0.0F;
    public float initScale = 0.0F;
    public float initAlpha = 0.0F;
    public boolean disableDepthTest;

    public ParticleDarkGlow(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float a, float scale, int lifetime, SpriteSet sprite, boolean disableDepthTest) {
        super(worldIn, x, y, z, 0.0, 0.0, 0.0);
        this.hasPhysics = false;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
        if ((double)this.colorR > 1.0) {
            this.colorR /= 255.0F;
        }

        if ((double)this.colorG > 1.0) {
            this.colorG /= 255.0F;
        }

        if ((double)this.colorB > 1.0) {
            this.colorB /= 255.0F;
        }

        this.setColor(this.colorR, this.colorG, this.colorB);
        this.lifetime = (int)((float)lifetime * 0.5F);
        this.quadSize = 0.0F;
        this.initScale = scale;
        this.xd = vx * 2.0;
        this.yd = vy * 2.0;
        this.zd = vz * 2.0;
        this.initAlpha = a;
        this.pickSprite(sprite);
        this.disableDepthTest = disableDepthTest;
    }

    public ParticleRenderType getRenderType() {
        return this.disableDepthTest ? ParticleRenderTypes.DARK_EMBER_RENDER_NO_MASK : ParticleRenderTypes.DARK_EMBER_RENDER;
    }

    public int getLightColor(float pTicks) {
        return 255;
    }

    public void tick() {
        super.tick();
        if ((new Random()).nextInt(6) == 0) {
            ++this.age;
        }

        float lifeCoeff = (float)this.age / (float)this.lifetime;
        this.quadSize = this.initScale - this.initScale * lifeCoeff;
        this.alpha = this.initAlpha * (1.0F - lifeCoeff);
        this.oRoll = (float)(this.roll++);
    }

    public boolean isAlive() {
        return this.age < this.lifetime;
    }
}
