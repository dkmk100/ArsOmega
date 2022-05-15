package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BiomeCrystal extends Item {
    public BiomeCrystal(String name, Properties p_i48487_1_) {
        super(p_i48487_1_);
        this.setRegistryName(name);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("biome")) {
            tooltip2.add(new StringTextComponent("Biome: " + stack.getTag().getString("biome")));
        }
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world instanceof ServerWorld) {

            boolean changed = false;

            String biome = "minecraft:ocean";
            BlockPos pos = player.blockPosition();

            Biome biome2 = world.getChunkAt(pos).getBiomes().getNoiseBiome(pos.getX(), pos.getY(), pos.getZ());

            if (biome2 != null) {
                biome = biome2.getRegistryName().toString();
            }

            if (stack.hasTag() && stack.getTag().getString("biome") == biome) {
                changed = false;
            } else {
                changed = true;
                stack.getOrCreateTag().putString("biome", biome);
                ArsOmega.LOGGER.info("Biome: " + biome);
            }

            if (changed) {
                return ActionResult.success(stack);
            } else {
                return ActionResult.pass(stack);
            }
        }
        return ActionResult.pass(stack);
    }
}
