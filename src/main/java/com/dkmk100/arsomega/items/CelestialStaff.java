package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.rituals.RitualChangeBiome;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class CelestialStaff extends BasicItem{
    public CelestialStaff(Properties properties, String name) {
        super(properties, name,true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        tooltip2.add(new TextComponent("power absorbed: "+getPower(stack)));
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    public static int getPower(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("absorbedPower")) {
            return stack.getTag().getInt("absorbedPower");
        }
        return 0;
    }
    public static void setPower(ItemStack stack, int power){
        stack.getOrCreateTag().putInt("absorbedPower",power);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(level instanceof ServerLevel){
            ServerLevel world = (ServerLevel) level;
            String biome = "minecraft:ocean";
            BlockPos pos = player.blockPosition();

            Biome biome2 = world.getBiome(pos).value();

            if (biome2 != null) {
                biome = biome2.getRegistryName().toString();
            }
            if(biome.equals("arsomega:demon_biome")) {
                int power = getPower(stack);
                RegistryAccess reg = world.registryAccess();
                Registry<Biome> a = reg.registry(Registry.BIOME_REGISTRY).get();
                Holder<Biome> holder = a.getHolderOrThrow(Biomes.PLAINS);
                final int sidewaysRange = 7;
                final int upRange = 6;
                final int sideSpacing = 1;
                final int upSpacing = 1;
                for (int x = -1 * sidewaysRange; x <= sidewaysRange; x++) {
                    for (int z = -1 * sidewaysRange; z <= sidewaysRange; z++) {
                        for (int i = -1 * upRange; i <= upRange; i++) {
                            BlockPos newPos = new BlockPos(pos.getX() + sideSpacing * x, pos.getY() + upSpacing * i, pos.getZ() + sideSpacing * z);

                            //lack of griefing protection in this thing is intentional game design

                            RitualChangeBiome.setBiome(world, newPos, holder);
                        }
                        for(int i = pos.getY() + 10; i > pos.getY() - 10; i--){
                            BlockPos newPos = new BlockPos(pos.getX() + x, i, pos.getZ() + z);
                            if(world.getBlockState(newPos).getBlock() == ItemsRegistry.DEMONIC_STONE){
                                world.setBlockAndUpdate(newPos, Blocks.COARSE_DIRT.defaultBlockState());
                                power+=5;//gains power from consuming the stone, why not
                                break;
                            }
                        }
                    }
                }

                PortUtil.sendMessageNoSpam(player,new TextComponent("Consumed Demonic Energy"));

                ChunkPos chunkPos = world.getChunkAt(pos).getPos();
                RitualChangeBiome.updateChunkAfterBiomeChange(world, chunkPos);

                power += 50;
                setPower(stack, power);
                return InteractionResultHolder.success(stack);
            }
        }

        return super.use(level,player,hand);
    }
}
