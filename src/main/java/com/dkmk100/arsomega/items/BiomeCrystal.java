package com.dkmk100.arsomega.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeCrystal extends BasicItem {

    public BiomeCrystal(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("biome")) {
            tooltip2.add(Component.literal("Biome: " + stack.getTag().getString("biome")));
        }
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world instanceof ServerLevel) {

            boolean changed = false;

            BlockPos pos = player.blockPosition();

            Holder<Biome> biomeHolder = world.getBiome(pos);

            Optional<? extends Registry<Biome>> registryOptional = world.registryAccess().registry(Registry.BIOME_REGISTRY);

            if (registryOptional.isEmpty()) {
                return InteractionResultHolder.pass(stack);
            }
            Registry<Biome> registry = registryOptional.get();
            ResourceLocation resourceLocation = registry.getKey(biomeHolder.value());



            //doesn't work and IDK why
            //ResourceLocation resourceLocation = ForgeRegistries.BIOMES.getKey(biomeHolder.value());


            if (resourceLocation == null) {
                return InteractionResultHolder.pass(stack);
            }
            String biome = resourceLocation.toString();

            if (stack.hasTag() && stack.getTag().getString("biome") == biome) {
                changed = false;
            } else {
                //don't override crystal
                if(!stack.hasTag() || !stack.getTag().contains("biome") || stack.getTag().getString("biome") == "") {
                    changed = true;
                    stack.getOrCreateTag().putString("biome", biome);
                }
                else{
                    changed = false;
                    //not sure what do do here
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

