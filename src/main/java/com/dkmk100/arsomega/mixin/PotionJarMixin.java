package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.blocks.BottlerTile;
import com.hollingsworth.arsnouveau.common.block.PotionJar;
import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionJar.class)
public class PotionJarMixin {
    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/common/block/PotionJar;use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", cancellable = true, remap = false)
    public void use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        ArsOmega.LOGGER.info("Potion Jar Use");
        if (worldIn.isClientSide) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        } else {
            PotionJarTile tile = (PotionJarTile)worldIn.getBlockEntity(pos);
            if (tile == null) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            } else {
                ItemStack stack = player.getItemInHand(handIn);
                Potion potion = PotionUtils.getPotion(stack);
                if (BottlerTile.isPotion(stack) && potion != Potions.EMPTY) {
                    if (tile.getAmount() == 0) {
                        tile.setPotion(stack);
                        tile.addAmount(100);
                        if (!player.isCreative()) {
                            player.addItem(new ItemStack(BottlerTile.getBottleItem(stack)));
                            stack.shrink(1);
                        }
                    } else if (tile.isMixEqual(stack) && tile.getCurrentFill() < tile.getMaxFill()) {
                        tile.addAmount(100);
                        if (!player.isCreative()) {
                            player.addItem(new ItemStack(BottlerTile.getBottleItem(stack)));
                            stack.shrink(1);
                        }
                    }

                    worldIn.sendBlockUpdated(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
                }
                if (BottlerTile.isBottle(stack) && tile.getCurrentFill() >= 100) {
                    ItemStack potionStack = new ItemStack(BottlerTile.getPotionItem(stack));
                    PotionUtils.setPotion(potionStack, tile.getPotion());
                    PotionUtils.setCustomEffects(potionStack, tile.getCustomEffects());
                    player.addItem(potionStack);
                    player.getItemInHand(handIn).shrink(1);
                    tile.addAmount(-100);
                }
                return;
            }
        }
    }

}
