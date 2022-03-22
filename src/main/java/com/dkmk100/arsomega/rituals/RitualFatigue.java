package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import java.util.List;

public class RitualFatigue extends AbstractRitual {
    protected void tick() {
        World world = this.getWorld();
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 100; ++i) {
                Vector3d particlePos = (new Vector3d((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(3.0D, 3.0D, 3.0D));
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
            if (this.getProgress() % 2 == 0) {
                List<ItemStack> items = this.getConsumedItems();
                int aoe = 0;
                for(ItemStack stack : items){
                    if(stack.getCount()<=0){
                        aoe += 1;
                    }
                    else{
                        aoe += stack.getCount();
                    }
                }
                List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AxisAlignedBB(this.getPos())).inflate(12.0D + aoe*2).inflate(10,0,10));
                for (LivingEntity entity : entities) {
                    if(entity instanceof PlayerEntity){
                        entity.addEffect(new EffectInstance(Effects.DIG_SLOWDOWN, 180,1));
                    }
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 1;
    }
    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(250,250,130);
    }
    @Override
    public boolean consumesMana() {
        return true;
    }
    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return stack.getItem() == ArsRegistry.GLYPH_AOE;
    }

    @Override
    public String getID() {
        return "fatigue";
    }
}
