package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class DimensionCrystal extends Item {
    public DimensionCrystal(String name, Properties p_i48487_1_) {
        super(p_i48487_1_);
        this.setRegistryName(ArsOmega.MOD_ID, name);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("dimension")) {
            tooltip2.add(new TextComponent("Dimension: " + stack.getTag().getString("dimension")));
        }
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world instanceof ServerLevel) {
            boolean changed = false;
            //String dimension = "minecraft:overworld";
            String dimension = world.dimension().location().toString();

            if (stack.hasTag() && stack.getTag().contains("dimension") && stack.getTag().getString("dimension") == dimension) {
                changed = false;
            } else {
                //don't override dimension crystals
                if(!stack.hasTag() || !stack.getTag().contains("dimension") || stack.getTag().getString("dimension") == "") {
                    changed = true;
                    stack.getOrCreateTag().putString("dimension", dimension);
                }
                else{
                    changed = false;
                    //hmm, no idea what do do here lol
                }
            }

            if (changed) {
                return InteractionResultHolder.success(stack);
            } else {
                return InteractionResultHolder.pass(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }
}
