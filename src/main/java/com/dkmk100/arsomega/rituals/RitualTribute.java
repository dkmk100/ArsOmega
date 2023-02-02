package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.ibm.icu.impl.Pair;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class RitualTribute extends AbstractRitual {
    @Nullable
    public ItemEntity spawnAtLocation(ItemStack p_199701_1_, BlockPos pos) {
        return this.spawnAtLocation(p_199701_1_, 0.0F, pos);
    }
    @Nullable
    public ItemEntity spawnAtLocation(ItemStack p_70099_1_, float p_70099_2_, BlockPos pos) {
        if (p_70099_1_.isEmpty()) {
            return null;
        } else if (getWorld().isClientSide) {
            return null;
        } else {
            ItemEntity itementity = new ItemEntity(this.getWorld(), pos.getX(),pos.getY()+p_70099_2_,pos.getZ(), p_70099_1_);
            itementity.setDefaultPickUpDelay();
            this.getWorld().addFreshEntity(itementity);
            return itementity;
        }
    }

    //the integer is amount of gold nuggets of value
    List<Pair<TagKey<Item>, Integer>> validItems = List.of(
            Pair.of(Tags.Items.NUGGETS_GOLD,1),
            Pair.of(Tags.Items.INGOTS_GOLD,9),
            Pair.of(Tags.Items.GEMS_DIAMOND,9*5),
            Pair.of(Tags.Items.STORAGE_BLOCKS_GOLD,9*9),
            Pair.of(Tags.Items.STORAGE_BLOCKS_DIAMOND,9*9*5),
            Pair.of(Tags.Items.GEMS_EMERALD,12),
            Pair.of(Tags.Items.STORAGE_BLOCKS_EMERALD,12 * 9),
            Pair.of(Tags.Items.GEMS_AMETHYST,6),
            Pair.of(Tags.Items.STORAGE_BLOCKS_AMETHYST,6*4)
    );
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

        if (!world.isClientSide && world.getGameTime() % 10L == 0L) {
            if(this.needsManaNow()){
                return;
            }
            else{
                this.setNeedsMana(true);
            }
            this.incrementProgress();
            if (this.getProgress() > 10) {
                BlockPos pos = this.getPos();
                List<ItemStack> items = this.getConsumedItems();
                int quality = 0;//1 quality is 1 gold nugget
                for(ItemStack stack : items){
                    Optional<Pair<TagKey<Item>,Integer>> item = validItems.stream().filter((pair) -> tagContains(pair.first,stack.getItem())).findFirst();
                    if(item.isPresent()){
                        quality += item.get().second;
                    }
                }
                while(quality>=180)//20 ingots * 9 points per ingot
                {
                    ItemEntity itementity = this.spawnAtLocation(new ItemStack(ItemsRegistry.WILDEN_TRIBUTE), 1, pos);
                    if (itementity != null) {
                        itementity.setExtendedLifetime();
                    }
                    quality-=180;
                }
                ItemEntity itementity2 = this.spawnAtLocation(new ItemStack(ArsRegistry.TRIBUTE_RITUAL),1,pos);
                if (itementity2 != null) {
                    itementity2.setExtendedLifetime();
                }
                this.setFinished();
            }
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return validItems.stream() .filter((pair) -> tagContains(pair.first,stack.getItem())).findAny().isPresent();
        //return tagContains(Tags.Items.GEMS_EMERALD,stack.getItem()) || tagContains(Tags.Items.GEMS_DIAMOND,stack.getItem())|| tagContains(Tags.Items.INGOTS_GOLD,stack.getItem());
    }
    boolean tagContains(TagKey tag, Item item){
        return ForgeRegistries.ITEMS.tags().getTag(tag).contains(item);
    }
    @Override
    public int getManaCost() {
        return 500;
    }

    @Override
    public boolean consumesMana() {
        return true;
    }

    @Override
    public String getID() {
        return "tribute";
    }
}
