package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
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
            if(this.needsSourceNow()){
                return;
            }
            else{
                this.setNeedsSource(true);
            }
            this.incrementProgress();
            if (this.getProgress() > 10) {
                BlockPos pos = this.getPos();
                EntityType target = EntityType.ZOMBIE;
                boolean chose = false;
                List<ItemStack> items = this.getConsumedItems();
                int amount = 5;
                boolean cursed = false;
                boolean strong = false;//so far unused, but might be something fun later
                for(ItemStack stack : items){
                    Item item = stack.getItem();
                    if(tagContains(Tags.Items.GEMS_DIAMOND,item)){
                        if(stack.getCount()<=0){
                            amount+=1;
                        }
                        else{
                            amount+=stack.getCount();
                        }
                    }
                    else if(item == Items.ENDER_EYE || item == RegistryHandler.DEMON_GEM.get()){
                        cursed = true;
                    }
                    else if(item == Items.NETHERITE_INGOT)
                    {
                        cursed = true;
                        strong = true;
                    }
                    else if(chose){

                    }
                    else if(tagContains(Tags.Items.LEATHER,item) || item == Items.BEEF){
                       target=EntityType.COW;
                       chose=true;
                    }
                    else if(item == Items.PORKCHOP){
                        target=EntityType.PIG;
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
                    else if(item == Items.SHULKER_SHELL){
                        target= EntityType.SHULKER;
                        chose=true;
                    }
                    else if(item == Items.ENDER_PEARL){
                        target= EntityType.ENDERMAN;
                        chose=true;
                    }
                    else if(item == Items.SPIDER_EYE){
                        target = EntityType.SPIDER;
                        chose=true;
                    }
                    else if(item == Items.BONE){
                        target = EntityType.SKELETON;
                        chose=true;
                    }
                    else if(item == Items.GOLD_BLOCK){
                        target = EntityType.PIGLIN;
                        chose=true;
                    }
                    else if(item == Items.GUNPOWDER){
                        target = EntityType.CREEPER;
                        chose=true;
                    }
                    else if(item == Items.EMERALD_BLOCK){
                        target = EntityType.HORSE;
                        chose=true;
                    }
                    else if(item == Items.ROTTEN_FLESH){
                        target = EntityType.ZOMBIE_VILLAGER;
                        chose=true;
                    }
                    else if(tagContains(ItemTags.SMALL_FLOWERS,item)){
                        target = EntityType.BEE;
                        chose=true;
                    }
                    else if(item == Items.GLOWSTONE_DUST){
                        target = EntityType.WITCH;
                        chose=true;
                    }
                }
                if(cursed){
                    if(target == EntityType.SPIDER){
                        amount = amount/2;
                        target = EntityType.CAVE_SPIDER;
                    }
                    else if(target == EntityType.ENDERMAN){
                        amount = amount*2;
                        target = EntityType.ENDERMITE;
                    }
                    else if(target == EntityType.COW){
                        amount = amount/2;
                        target = EntityType.MOOSHROOM;
                    }
                    else if(target == EntityType.SKELETON){
                        amount = amount/2;
                        target = EntityType.WITHER_SKELETON;
                    }
                    else if(target == EntityType.PIG){
                        amount = amount/2;
                        target = EntityType.PIGLIN_BRUTE;
                    }
                    else if(target == EntityType.PIGLIN){
                        amount = amount/2;
                        target = EntityType.ZOMBIFIED_PIGLIN;
                    }
                    else if(target == EntityType.ZOMBIE){
                        amount = amount-4;
                        target = EntityType.ZOMBIE_HORSE;
                    }
                    else if(target == EntityType.ZOMBIE){
                        amount = amount-4;
                        target = EntityType.VILLAGER;
                    }
                    else if(target == EntityType.ZOMBIE_VILLAGER){
                        amount = amount-4;
                        target = EntityType.SKELETON_HORSE;
                    }
                    else if(target == EntityType.BEE){
                        amount = amount+3;
                        target = EntityType.SILVERFISH;
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
    public int getSourceCost() {
        return 5;
    }
    boolean tagContains(TagKey<Item> tag, Item item){
        return ForgeRegistries.ITEMS.tags().getTag(tag).contains(item);
    }

    @Override
    public boolean consumesSource() {
        return true;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName("summoning");
    }
}
