/*
package com.dkmk100.arsomega.books;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellBookRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.hollingsworth.arsnouveau.common.block.tile.PhantomBlockTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOpenSpellBook;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.UseAnim;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.util.text.*;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item.Properties;

public class CustomSpellBookOld extends Item implements ISpellTier, IScribeable, IDisplayMana, IAnimatable {
    public static final String BOOK_MODE_TAG = "mode";
    public static final String UNLOCKED_SPELLS = "spells";
    public static int SEGMENTS = 10;
    public int tier;
    AnimationFactory factory = new AnimationFactory(this);

    public CustomSpellBookOld(String name, int tier, int segments) {
        super((new Properties()).stacksTo(1).tab(ArsNouveau.itemGroup).setISTER(() -> {
            return SpellBookRenderer::new;
        }));
        this.setRegistryName(ArsOmega.MOD_ID, name);
        this.tier = tier;
        SEGMENTS = segments;
    }

    public CustomSpellBookOld(Properties properties, int tier, int segments) {
        super(properties);
        this.tier = tier;
        this.SEGMENTS = segments;
    }

    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        if (!worldIn.isClientSide && worldIn.getGameTime() % 5L == 0L && !stack.hasTag()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("mode", 0);
            StringBuilder starting_spells = new StringBuilder();
            if (stack.getItem() == ItemsRegistry.creativeSpellBook) {
                ArsNouveauAPI.getInstance().getSpell_map().values().forEach((s) -> {
                    starting_spells.append(",").append(s.getTag().trim());
                });
            } else {
                ArsNouveauAPI.getInstance().getDefaultStartingSpells().forEach((s) -> {
                    starting_spells.append(",").append(s.getTag().trim());
                });
            }

            tag.putString("spells", starting_spells.toString());
            stack.setTag(tag);
        }

        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!stack.hasTag()) {
            return new InteractionResultHolder(InteractionResult.SUCCESS, stack);
        } else {
            ManaCapability.getMana(playerIn).ifPresent((iMana) -> {
                if (iMana.getBookTier() < this.tier) {
                    iMana.setBookTier(this.tier);
                }

                if (iMana.getGlyphBonus() < getUnlockedSpells(stack.getTag()).size()) {
                    iMana.setGlyphBonus(getUnlockedSpells(stack.getTag()).size());
                }

            });
            SpellResolver resolver = new SpellResolver((new SpellContext(this.getCurrentRecipe(stack), playerIn)).withColors(getSpellColor(stack.getOrCreateTag(), getMode(stack.getOrCreateTag()))));
            boolean isSensitive = resolver.spell.getBuffsAtIndex(0, playerIn, AugmentSensitive.INSTANCE) > 0;
            HitResult result = playerIn.pick(5.0D, 0.0F, isSensitive);
            if (result instanceof BlockHitResult && worldIn.getBlockEntity(((BlockHitResult)result).getBlockPos()) instanceof ScribesTile) {
                return new InteractionResultHolder(InteractionResult.SUCCESS, stack);
            } else if (result instanceof BlockHitResult && !playerIn.isShiftKeyDown() && worldIn.getBlockEntity(((BlockHitResult)result).getBlockPos()) != null && !(worldIn.getBlockEntity(((BlockHitResult)result).getBlockPos()) instanceof IntangibleAirTile) && !(worldIn.getBlockEntity(((BlockHitResult)result).getBlockPos()) instanceof PhantomBlockTile)) {
                return new InteractionResultHolder(InteractionResult.SUCCESS, stack);
            } else if (!worldIn.isClientSide && stack.hasTag()) {
                if (getMode(stack.getOrCreateTag()) == 0 && playerIn instanceof ServerPlayer) {
                    ServerPlayer player = (ServerPlayer)playerIn;

                    //modified to use getTrueTier
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> {
                        return player;
                    }), new PacketOpenSpellBook(stack.getTag(), this.getTrueTier(), getUnlockedSpellString(player.getItemInHand(handIn).getOrCreateTag())));

                    return new InteractionResultHolder(InteractionResult.CONSUME, stack);
                } else {
                    EntityHitResult entityRes = MathUtil.getLookedAtEntity(playerIn, 25);
                    if (entityRes != null && entityRes.getEntity() instanceof LivingEntity) {
                        resolver.onCastOnEntity(stack, playerIn, (LivingEntity)entityRes.getEntity(), handIn);
                        return new InteractionResultHolder(InteractionResult.CONSUME, stack);
                    } else if (result.getType() != HitResult.Type.BLOCK && (!isSensitive || !(result instanceof BlockHitResult))) {
                        resolver.onCast(stack, playerIn, worldIn);
                        return new InteractionResultHolder(InteractionResult.CONSUME, stack);
                    } else {
                        UseOnContext context = new UseOnContext(playerIn, handIn, (BlockHitResult)result);
                        resolver.onCastOnBlock(context);
                        return new InteractionResultHolder(InteractionResult.CONSUME, stack);
                    }
                }
            } else {
                return new InteractionResultHolder(InteractionResult.CONSUME, stack);
            }
        }
    }

    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack stack) {
        if (!(player.getItemInHand(handIn).getItem() instanceof com.hollingsworth.arsnouveau.common.items.SpellBook)) {
            return false;
        } else {
            List<AbstractSpellPart> spellParts = getUnlockedSpells(player.getItemInHand(handIn).getTag());
            int unlocked = 0;
            Iterator var8 = spellParts.iterator();

            while(var8.hasNext()) {
                AbstractSpellPart spellPart = (AbstractSpellPart)var8.next();
                if (unlockSpell(stack.getTag(), spellPart)) {
                    ++unlocked;
                }
            }

            PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.spell_book.copied", new Object[]{unlocked}));
            return true;
        }
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public Spell getCurrentRecipe(ItemStack stack) {
        return getRecipeFromTag(stack.getTag(), getMode(stack.getTag()));
    }

    public static Spell getRecipeFromTag(CompoundTag tag, int r_slot) {
        String recipeStr = getRecipeString(tag, r_slot);
        return Spell.deserialize(recipeStr);
    }

    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return true;
    }

    public static void setSpellName(CompoundTag tag, String name, int slot) {
        tag.putString(slot + "_name", name);
    }

    public static String getSpellName(CompoundTag tag, int slot) {
        return slot == 0 ? (new TranslatableComponent("ars_nouveau.spell_book.create_mode")).getString() : tag.getString(slot + "_name");
    }

    public static void setSpellColor(CompoundTag tag, ParticleColor.IntWrapper color, int slot) {
        tag.putString(slot + "_color", color.serialize());
    }

    public static ParticleColor.IntWrapper getSpellColor(CompoundTag tag, int slot) {
        String key = slot + "_color";
        return !tag.contains(key) ? new ParticleColor.IntWrapper(255, 25, 180) : ParticleColor.IntWrapper.deserialize(tag.getString(key));
    }

    public static String getSpellName(CompoundTag tag) {
        return getSpellName(tag, getMode(tag));
    }

    public static String getRecipeString(CompoundTag tag, int spell_slot) {
        return tag.getString(spell_slot + "recipe");
    }

    public static void setRecipe(CompoundTag tag, String recipe, int spell_slot) {
        tag.putString(spell_slot + "recipe", recipe);
    }

    public static int getMode(CompoundTag tag) {
        return tag.getInt("mode");
    }

    public static void setMode(CompoundTag tag, int mode) {
        tag.putInt("mode", mode);
    }

    public static List<AbstractSpellPart> getUnlockedSpells(CompoundTag tag) {
        return SpellRecipeUtil.getSpellsFromString(tag.getString("spells"));
    }

    public static String getUnlockedSpellString(CompoundTag tag) {
        return tag.getString("spells");
    }

    public static boolean unlockSpell(CompoundTag tag, AbstractSpellPart spellPart) {
        if (containsSpell(tag, spellPart)) {
            return false;
        } else {
            String newSpells = tag.getString("spells") + "," + spellPart.getTag();
            tag.putString("spells", newSpells);
            return true;
        }
    }

    public static void unlockSpell(CompoundTag tag, String spellTag) {
        String newSpells = tag.getString("spells") + "," + spellTag;
        tag.putString("spells", newSpells);
    }

    public static boolean containsSpell(CompoundTag tag, AbstractSpellPart spellPart) {
        return getUnlockedSpells(tag).contains(spellPart);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (stack.hasTag()) {
            tooltip.add(new TextComponent(getSpellName(stack.getTag())));
            tooltip.add(new TranslatableComponent("ars_nouveau.spell_book.select", new Object[]{((Component) KeyMapping.createNameSupplier(ModKeyBindings.OPEN_SPELL_SELECTION.getKeyBinding().getName()).get()).getString()}));
            tooltip.add(new TranslatableComponent("ars_nouveau.spell_book.craft", new Object[]{((Component)KeyMapping.createNameSupplier(ModKeyBindings.OPEN_BOOK.getKeyBinding().getName()).get()).getString()}));
        }

        tooltip.add((new TranslatableComponent("tooltip.ars_nouveau.caster_level", new Object[]{this.getTrueTier()})).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
    }

    public Tier getTier() {
        Tier tier = Tier.ONE;
        if(this.tier>2){
            tier = Tier.THREE;
        }
        else if(this.tier>1){
            tier = Tier.TWO;
        }
        return tier;
    }

    public boolean isTierFour(){
        return this.tier>3;
    }

    public int getTrueTier(){
        return this.tier;
    }


    public void registerControllers(AnimationData data) {
    }

    public AnimationFactory getFactory() {
        return this.factory;
    }
}
 */
