package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.potions.BindEffect;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;

public class RitualPetrification extends AbstractRitual {
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
            if(this.needsManaNow()){
                return;
            }
            else{
                this.setNeedsMana(true);
            }
            this.incrementProgress();
            if (this.getProgress() > 30) {
                List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AABB(this.getPos())).inflate(5.0D));
                for (LivingEntity entity : entities) {
                    Collection<MobEffectInstance> effects = entity.getActiveEffects();
                    MobEffectInstance[] array = effects.toArray(new MobEffectInstance[0]);
                    MobEffectInstance[] var9 = array;
                    int var10 = array.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        MobEffectInstance e = var9[var11];
                        if (e.isCurativeItem(new ItemStack(Items.MILK_BUCKET))||e.isCurativeItem(new ItemStack(ItemsRegistry.CLEANSING_GEM))||e.getEffect() instanceof BindEffect) {
                            entity.removeEffect(e.getEffect());
                        }
                    }
                    entity.addEffect(new MobEffectInstance(ModPotions.STONE_PETRIFICATION, 800,1,false,false));
                }
                this.setFinished();
            }
            else if (this.getProgress() >= 5 && this.getProgress()<=27) {
                List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AABB(this.getPos())).inflate(5.0D));
                for (LivingEntity entity : entities) {
                    entity.addEffect(new MobEffectInstance(com.hollingsworth.arsnouveau.common.potions.ModPotions.SNARE_EFFECT, 40, 0, false, false));
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 500;
    }
    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(24,255,25);
    }
    @Override
    public boolean consumesMana() {
        return true;
    }

    @Override
    public String getID() {
        return "petrification";
    }
}
