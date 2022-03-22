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
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

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
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        if (!worldIn.isClientSide && worldIn.getGameTime() % 5L == 0L && !stack.hasTag()) {
            CompoundNBT tag = new CompoundNBT();
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

    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!stack.hasTag()) {
            return new ActionResult(ActionResultType.SUCCESS, stack);
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
            RayTraceResult result = playerIn.pick(5.0D, 0.0F, isSensitive);
            if (result instanceof BlockRayTraceResult && worldIn.getBlockEntity(((BlockRayTraceResult)result).getBlockPos()) instanceof ScribesTile) {
                return new ActionResult(ActionResultType.SUCCESS, stack);
            } else if (result instanceof BlockRayTraceResult && !playerIn.isShiftKeyDown() && worldIn.getBlockEntity(((BlockRayTraceResult)result).getBlockPos()) != null && !(worldIn.getBlockEntity(((BlockRayTraceResult)result).getBlockPos()) instanceof IntangibleAirTile) && !(worldIn.getBlockEntity(((BlockRayTraceResult)result).getBlockPos()) instanceof PhantomBlockTile)) {
                return new ActionResult(ActionResultType.SUCCESS, stack);
            } else if (!worldIn.isClientSide && stack.hasTag()) {
                if (getMode(stack.getOrCreateTag()) == 0 && playerIn instanceof ServerPlayerEntity) {
                    ServerPlayerEntity player = (ServerPlayerEntity)playerIn;

                    //modified to use getTrueTier
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> {
                        return player;
                    }), new PacketOpenSpellBook(stack.getTag(), this.getTrueTier(), getUnlockedSpellString(player.getItemInHand(handIn).getOrCreateTag())));

                    return new ActionResult(ActionResultType.CONSUME, stack);
                } else {
                    EntityRayTraceResult entityRes = MathUtil.getLookedAtEntity(playerIn, 25);
                    if (entityRes != null && entityRes.getEntity() instanceof LivingEntity) {
                        resolver.onCastOnEntity(stack, playerIn, (LivingEntity)entityRes.getEntity(), handIn);
                        return new ActionResult(ActionResultType.CONSUME, stack);
                    } else if (result.getType() != RayTraceResult.Type.BLOCK && (!isSensitive || !(result instanceof BlockRayTraceResult))) {
                        resolver.onCast(stack, playerIn, worldIn);
                        return new ActionResult(ActionResultType.CONSUME, stack);
                    } else {
                        ItemUseContext context = new ItemUseContext(playerIn, handIn, (BlockRayTraceResult)result);
                        resolver.onCastOnBlock(context);
                        return new ActionResult(ActionResultType.CONSUME, stack);
                    }
                }
            } else {
                return new ActionResult(ActionResultType.CONSUME, stack);
            }
        }
    }

    public boolean onScribe(World world, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack stack) {
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

            PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.spell_book.copied", new Object[]{unlocked}));
            return true;
        }
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    public Spell getCurrentRecipe(ItemStack stack) {
        return getRecipeFromTag(stack.getTag(), getMode(stack.getTag()));
    }

    public static Spell getRecipeFromTag(CompoundNBT tag, int r_slot) {
        String recipeStr = getRecipeString(tag, r_slot);
        return Spell.deserialize(recipeStr);
    }

    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    public static void setSpellName(CompoundNBT tag, String name, int slot) {
        tag.putString(slot + "_name", name);
    }

    public static String getSpellName(CompoundNBT tag, int slot) {
        return slot == 0 ? (new TranslationTextComponent("ars_nouveau.spell_book.create_mode")).getString() : tag.getString(slot + "_name");
    }

    public static void setSpellColor(CompoundNBT tag, ParticleColor.IntWrapper color, int slot) {
        tag.putString(slot + "_color", color.serialize());
    }

    public static ParticleColor.IntWrapper getSpellColor(CompoundNBT tag, int slot) {
        String key = slot + "_color";
        return !tag.contains(key) ? new ParticleColor.IntWrapper(255, 25, 180) : ParticleColor.IntWrapper.deserialize(tag.getString(key));
    }

    public static String getSpellName(CompoundNBT tag) {
        return getSpellName(tag, getMode(tag));
    }

    public static String getRecipeString(CompoundNBT tag, int spell_slot) {
        return tag.getString(spell_slot + "recipe");
    }

    public static void setRecipe(CompoundNBT tag, String recipe, int spell_slot) {
        tag.putString(spell_slot + "recipe", recipe);
    }

    public static int getMode(CompoundNBT tag) {
        return tag.getInt("mode");
    }

    public static void setMode(CompoundNBT tag, int mode) {
        tag.putInt("mode", mode);
    }

    public static List<AbstractSpellPart> getUnlockedSpells(CompoundNBT tag) {
        return SpellRecipeUtil.getSpellsFromString(tag.getString("spells"));
    }

    public static String getUnlockedSpellString(CompoundNBT tag) {
        return tag.getString("spells");
    }

    public static boolean unlockSpell(CompoundNBT tag, AbstractSpellPart spellPart) {
        if (containsSpell(tag, spellPart)) {
            return false;
        } else {
            String newSpells = tag.getString("spells") + "," + spellPart.getTag();
            tag.putString("spells", newSpells);
            return true;
        }
    }

    public static void unlockSpell(CompoundNBT tag, String spellTag) {
        String newSpells = tag.getString("spells") + "," + spellTag;
        tag.putString("spells", newSpells);
    }

    public static boolean containsSpell(CompoundNBT tag, AbstractSpellPart spellPart) {
        return getUnlockedSpells(tag).contains(spellPart);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (stack.hasTag()) {
            tooltip.add(new StringTextComponent(getSpellName(stack.getTag())));
            tooltip.add(new TranslationTextComponent("ars_nouveau.spell_book.select", new Object[]{((ITextComponent) KeyBinding.createNameSupplier(ModKeyBindings.OPEN_SPELL_SELECTION.getKeyBinding().getName()).get()).getString()}));
            tooltip.add(new TranslationTextComponent("ars_nouveau.spell_book.craft", new Object[]{((ITextComponent)KeyBinding.createNameSupplier(ModKeyBindings.OPEN_BOOK.getKeyBinding().getName()).get()).getString()}));
        }

        tooltip.add((new TranslationTextComponent("tooltip.ars_nouveau.caster_level", new Object[]{this.getTrueTier()})).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE)));
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
