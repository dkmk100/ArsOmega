package com.dkmk100.arsomega.rituals;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class RitualSummoning extends AbstractRitual {
    @Override
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
            if (this.getProgress() > 10) {
                BlockPos pos = this.getPos();
                EntityType target = EntityType.ZOMBIE;
                boolean chose = false;
                List<ItemStack> items = this.getConsumedItems();
                int amount = 5;
                for(ItemStack stack : items){
                    Item item = stack.getItem();
                    if(chose){
                        if(tagContains(Tags.Items.GEMS_DIAMOND,item)){
                            if(stack.getCount()<=0){
                                amount+=1;
                            }
                            else{
                                amount+=stack.getCount();
                            }
                        }
                    }
                    else if(tagContains(Tags.Items.LEATHER,item)){
                       target=EntityType.COW;
                       chose=true;
                    }
                    else if(tagContains(Tags.Items.FEATHERS,item)){
                        target=EntityType.CHICKEN;
                        chose=true;
                    }
                    else if(tagContains(Tags.Items.GUNPOWDER,item)){
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
                    target.spawn((ServerLevel) world, null, null, pos.above(), MobSpawnType.MOB_SUMMONED, false, false);
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
    boolean tagContains(TagKey tag, Item item){
        return ForgeRegistries.ITEMS.tags().getTag(tag).contains(item);
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
