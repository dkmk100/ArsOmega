package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;

public class RitualCleansing extends AbstractRitual {
    protected void tick() {
        Level world = this.getWorld();
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 100; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(this.getCenterColor()), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }

        if (!world.isClientSide && world.getGameTime() % 20L == 0L) {
            if(this.needsSourceNow()){
                return;
            }
            else{
                this.setNeedsSource(true);
            }
            this.incrementProgress();
            if (this.getProgress() > 20) {
                List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AABB(this.getPos())).inflate(5.0D));
                for (LivingEntity entity : entities) {
                    Collection<MobEffectInstance> effects = entity.getActiveEffects();
                    MobEffectInstance[] array = effects.toArray(new MobEffectInstance[0]);
                    MobEffectInstance[] var9 = array;
                    int var10 = array.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        MobEffectInstance e = var9[var11];
                        if (e.isCurativeItem(new ItemStack(Items.MILK_BUCKET))||e.isCurativeItem(new ItemStack(RegistryHandler.CLEANSING_GEM.get()))||e.getEffect() == ModPotions.SUMMONING_SICKNESS_EFFECT.get()) {
                            entity.removeEffect(e.getEffect());
                        }
                    }
                    if(entity instanceof ZombieVillager){
                        //used to convert here, no point really
                    }
                    this.setFinished();
                }
            }
        }
    }

    @Override
    public int getSourceCost() {
        return 10;
    }
    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(220,240,255);
    }
    @Override
    public boolean consumesSource() {
        return true;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsOmega.MOD_ID, "cleansing");
    }
}
