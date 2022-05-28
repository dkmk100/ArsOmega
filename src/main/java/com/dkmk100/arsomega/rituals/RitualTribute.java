package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
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
                int quality = 0;
                for(ItemStack stack : items){
                    int q2 = 0;
                    if(tagContains(Tags.Items.GEMS_DIAMOND,stack.getItem())){
                        q2 +=3;
                    }
                    else{
                        q2+=1;
                    }
                    if(stack.getCount()<=0){
                        quality += q2;
                    }
                    else{
                        quality += q2 * stack.getCount();
                    }
                }
                if(quality>=20)
                {
                    ItemEntity itementity = this.spawnAtLocation(new ItemStack(ItemsRegistry.WILDEN_TRIBUTE), 1, pos);
                    if (itementity != null) {
                        itementity.setExtendedLifetime();
                    }

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

        return tagContains(Tags.Items.GEMS_EMERALD,stack.getItem()) || tagContains(Tags.Items.GEMS_DIAMOND,stack.getItem())|| tagContains(Tags.Items.INGOTS_GOLD,stack.getItem());
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
