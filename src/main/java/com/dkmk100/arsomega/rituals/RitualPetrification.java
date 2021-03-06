package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.potions.BindEffect;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import java.util.Collection;
import java.util.List;

public class RitualPetrification extends AbstractRitual {
    protected void tick() {
        World world = this.getWorld();
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 100; ++i) {
                Vector3d particlePos = (new Vector3d((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
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
            if (this.getProgress() > 40) {
                List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AxisAlignedBB(this.getPos())).inflate(5.0D));
                for (LivingEntity entity : entities) {
                    Collection<EffectInstance> effects = entity.getActiveEffects();
                    EffectInstance[] array = effects.toArray(new EffectInstance[0]);
                    EffectInstance[] var9 = array;
                    int var10 = array.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        EffectInstance e = var9[var11];
                        if (e.isCurativeItem(new ItemStack(Items.MILK_BUCKET))||e.isCurativeItem(new ItemStack(ItemsRegistry.CLEANSING_GEM))||e.getEffect() instanceof BindEffect) {
                            entity.removeEffect(e.getEffect());
                        }
                    }
                    entity.addEffect(new EffectInstance(ModPotions.STONE_PETRIFICATION, 1200,1,false,false));
                }
                this.setFinished();
            }
            else if (this.getProgress() >= 20 && this.getProgress()<=35) {
                List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AxisAlignedBB(this.getPos())).inflate(5.0D));
                for (LivingEntity entity : entities) {
                    entity.addEffect(new EffectInstance(com.hollingsworth.arsnouveau.common.potions.ModPotions.SNARE_EFFECT, 40, 0, false, false));
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 1000;
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
