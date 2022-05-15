package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class DimensionCrystal extends Item {
    public DimensionCrystal(String name, Properties p_i48487_1_) {
        super(p_i48487_1_);
        this.setRegistryName(ArsOmega.MOD_ID, name);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        //if(stack.getTag().contains("biome")){

        boolean changed = false;


        String dim = world.dimension().toString();

        if(stack.hasTag()&&stack.getTag().getString("dimension")==dim){
            changed = false;
        }
        else{
            stack.getOrCreateTag().putString("dimension",dim);
            ArsOmega.LOGGER.info("Dimension: "+dim);
            changed = true;
        }

        if(changed){
            return ActionResult.success(stack);
        }
        else{
            return ActionResult.pass(stack);
        }
    }
}
