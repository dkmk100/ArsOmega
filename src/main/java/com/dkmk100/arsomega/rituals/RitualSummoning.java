package com.dkmk100.arsomega.rituals;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;

import java.util.List;

public class RitualSummoning extends AbstractRitual {
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
            if (this.getProgress() > 10) {
                BlockPos pos = this.getPos();
                EntityType target = EntityType.ZOMBIE;
                boolean chose = false;
                List<ItemStack> items = this.getConsumedItems();
                int amount = 5;
                for(ItemStack stack : items){
                    Item item = stack.getItem();
                    if(chose){
                        if(net.minecraftforge.common.Tags.Items.GEMS_DIAMOND.contains(item)){
                            if(stack.getCount()<=0){
                                amount+=1;
                            }
                            else{
                                amount+=stack.getCount();
                            }
                        }
                    }
                    else if(Tags.Items.LEATHER.contains(item)){
                       target=EntityType.COW;
                       chose=true;
                    }
                    else if(Tags.Items.FEATHERS.contains(item)){
                        target=EntityType.CHICKEN;
                        chose=true;
                    }
                    else if(Tags.Items.GUNPOWDER.contains(item)){
                        target=EntityType.CREEPER;
                        chose=true;
                    }
                    else if(item == Items.RABBIT_HIDE){
                        target= EntityType.RABBIT;
                        chose=true;
                    }
                    else if(item == Items.BLAZE_ROD){
                        target= EntityType.BLAZE;
                        chose=true;
                    }
                }
                int i=0;
                while(i<amount) {
                    target.spawn((ServerWorld) world, null, null, pos.above(), SpawnReason.MOB_SUMMONED, false, false);
                    i+=1;
                }
                this.setFinished();
            }
        }
    }
    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return stack.getItem()!=Items.DRAGON_EGG;//just in case lol
    }
    @Override
    public int getManaCost() {
        return 5;
    }

    @Override
    public boolean consumesMana() {
        return true;
    }

    @Override
    public String getID() {
        return "summoning";
    }
}
