package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;

public class RitualProtection extends AbstractRitual {
    protected void tick() {
        Level world = this.getWorld();
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 40; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(4.0D, 4.0D, 4.0D));
                world.addParticle(ParticleLineData.createData(this.getCenterColor()), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }

        if (!world.isClientSide && world.getGameTime() % 40L == 0L) {
            if(this.needsSourceNow()){
                return;
            }
            else{
                this.setNeedsSource(true);
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
                List<Player> entities = this.getWorld().getEntitiesOfClass(Player.class, (new AABB(this.getPos())).inflate(12.0D+aoe*2).inflate(10,0,10));
                for (Player entity : entities) {
                    entity.addEffect(new MobEffectInstance(ModPotions.NO_BREAK.get(), 140));
                }
            }
        }
    }

    @Override
    public int getSourceCost() {
        return 2;
    }
    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(160,105,250);
    }
    @Override
    public boolean consumesSource() {
        return true;
    }
    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return stack.getItem() == ArsNouveauAPI.getInstance().getGlyphItem(AugmentAOE.INSTANCE);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName("protection");
    }
}
