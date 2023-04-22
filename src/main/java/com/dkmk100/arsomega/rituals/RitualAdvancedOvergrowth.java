package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.glyphs.AdvancedGrow;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.Iterator;
import java.util.List;

public class RitualAdvancedOvergrowth extends BasicConfigRitual {
    public RitualAdvancedOvergrowth() {
    }

    protected void tick() {
        Level world = this.getWorld();
        BlockPos pos = this.getPos();
        if (this.getWorld().isClientSide) {
            ParticleUtil.spawnRitualAreaEffect(this.getPos(), this.getWorld(), this.rand, this.getCenterColor(), 6);
        } else {
            if (this.getWorld().getGameTime() % 128L != 0L)//sped up from the 200 in normal overgrowth
            {
                return;
            }
            boolean didWorkOnce;
            Iterator var5;
            if (this.isAnimalGrowth()) {
                //no extra range here, don't think it's necessary
                List<AgeableMob> animals = this.getWorld().getEntitiesOfClass(AgeableMob.class, (new AABB(this.getPos())).inflate(5.0));
                didWorkOnce = false;
                var5 = animals.iterator();

                while(var5.hasNext()) {
                    AgeableMob a = (AgeableMob)var5.next();
                    if (a.isBaby()) {
                        //slightly higher
                        a.ageUp(700, true);
                        didWorkOnce = true;
                    }
                }
            } else {
                double range = RANGE.get();
                didWorkOnce = false;
                var5 = BlockPos.betweenClosed(pos.offset(range, -1, range), pos.offset(-range, 1, -range)).iterator();

                while(var5.hasNext()) {
                    BlockPos b = (BlockPos)var5.next();
                    //greater odds too
                    if (this.rand.nextInt(12) == 0 && AdvancedGrow.GrowBlock(b,(ServerLevel)world))
                    {
                        didWorkOnce = true;
                    }
                }
            }
            if (didWorkOnce) {
                this.setNeedsSource(true);
            }
        }

    }

    @Override
    protected double getDefaultRange() {
        //range increased by 1 from default overgrowth ritual
        return 6;
    }

    public boolean isAnimalGrowth() {
        return this.didConsumeItem(Items.BONE_BLOCK);
    }

    public int getSourceCost() {
        //slightly higher cost
        return 750;
    }

    public boolean canConsumeItem(ItemStack stack) {
        return this.getConsumedItems().isEmpty() && stack.getItem() == Items.BONE_BLOCK;
    }

    public String getLangName() {
        return "Advanced Overgrowth";
    }

    public String getLangDescription() {
        return "A more powerful version of Overgrowth that works faster and can affect more crops";
    }

    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName("advanced_overgrowth");
    }

    public ParticleColor getCenterColor() {
        return ParticleColor.makeRandomColor(20, 255, 30, this.rand);
    }
}

