package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.staff.StaffAnimationController;
import com.dkmk100.arsomega.client.staff.StaffModel;
import com.dkmk100.arsomega.client.staff.StaffRenderer;
import com.dkmk100.arsomega.util.ResourceUtil;
import com.dkmk100.arsomega.util.StatsModifier;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.renderer.item.WandRenderer;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.dkmk100.arsomega.items.Staff.*;

public class ModularStaff extends SwordItem implements IAnimatable, ICasterTool, DyeableLeatherItem {
    public AnimationFactory factory = new AnimationFactory(this);
    StatsModifier statsModifier;

    public ModularStaff(Tier iItemTier, int baseDamage, float baseAttackSpeed, StatsModifier mod) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsOmega.itemGroup));
        statsModifier = mod;
    }
    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        AnimationController<?> controller = event.getController();
        StaffAnimationController<?> staffController = (StaffAnimationController<?>) controller;

        AnimationBuilder builder = (new AnimationBuilder()).addAnimation("idle", true);
        staffController.setAnimation(builder,event);

        return PlayState.CONTINUE;
    }

    protected String getStaffName(ItemStack stack){
        return "staff";
    }

    protected String getStaffBaseName(ItemStack stack){
        return getStaffName(stack) + "_base";
    }

    protected String getStaffHeadName(ItemStack stack){
        return getStaffName(stack) + "_head";
    }

    protected String getStaffGemName(ItemStack stack){
        return getStaffName(stack) + "_gem";
    }

    protected String getPartName(ItemStack stack, StaffModelPart part){
        String str;
        switch (part){
            case BASE -> str = getStaffBaseName(stack);
            case GEM -> str = getStaffGemName(stack);
            case HEAD -> str = getStaffHeadName(stack);
            default -> str = getStaffName(stack) + "_part";
        }
        return str;
    }

    public enum StaffModelPart{
        BASE, HEAD, GEM;
    }

    public ResourceLocation getModel(ItemStack stack, StaffModelPart part){
        if(part == StaffModelPart.GEM){
            ItemStack crystalStack = getCrystal(stack);
            if(!crystalStack.isEmpty() && crystalStack.getItem() instanceof ModularStaffCrystal crystalItem){
                return crystalItem.getModel(crystalStack, stack,this);
            }
            return ResourceUtil.getModelResource("empty");
        }
        return ResourceUtil.getModelResource(getPartName(stack, part));

    }

    public ResourceLocation getTexture(ItemStack stack, StaffModelPart part){
        return ResourceUtil.getItemTextureResource(getPartName(stack, part));
    }

    public ResourceLocation getAnimation(ItemStack stack, StaffModelPart part){
        return ResourceUtil.getAnimationResource(getPartName(stack, part));
    }

    ItemStack getCrystal(ItemStack stack){
        if(stack.hasTag() && stack.getTag().contains("crystal")){
            return ItemStack.of(stack.getTag().getCompound("crystal"));
        }
        else{
            return ItemStack.EMPTY;
        }
    }

    void setCrystalNBT(ItemStack stack, ItemStack crystal){
        if(crystal == null || crystal.isEmpty()){
            if(stack.hasTag() && stack.getTag().contains("crystal")){
                stack.getTag().remove("crystal");
            }
        }
        else{
            CompoundTag compound = new CompoundTag();
            crystal.save(compound);
            stack.getOrCreateTag().put("crystal",compound);
        }
    }

    boolean hasUpgrades(ItemStack stack){
        return false;
    }

    void removeAndDropUpgrades(ItemStack stack, Level world, BlockPos pos){

    }

    void removeAndDropCrystal(ItemStack stack, Level world, BlockPos pos, @Nullable Player player){
        ItemStack crystal = getCrystal(stack);
        if(crystal != null){
            if(player!=null && player.addItem(crystal)){
                //added item in if statement lol
            }
            else {
                //spawn item in world
                ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), crystal);
                world.addFreshEntity(entity);
            }

            //remove crystal from staff
            setCrystalNBT(stack, ItemStack.EMPTY);
        }
    }

    public void addCrystal(ItemStack staff, ItemStack crystal, Level world, BlockPos pos, @Nullable Player player){
        //remove and give back old crystal
        removeAndDropCrystal(staff, world, pos, player);
        //new crystal
        ItemStack crystalCopy = crystal.copy();
        crystalCopy.setCount(1);
        setCrystalNBT(staff,crystalCopy);
        //remove original crystal
        crystal.shrink(1);
    }

    public Color getColor(ItemStack stack, StaffModelPart part){
        if(part == StaffModelPart.GEM){
            ItemStack crystalStack = getCrystal(stack);
            if(!crystalStack.isEmpty() && crystalStack.getItem() instanceof ModularStaffCrystal crystalItem) {
                return crystalItem.getColor(crystalStack, stack, this);
            }
            else{
                ParticleColor color = this.getSpellCaster(stack).getSpell().color;
                return Color.ofOpaque(color.getColor());
            }
        }
        else if(part == StaffModelPart.BASE || part == StaffModelPart.HEAD) {
            int color = getColor(stack);
            return Color.ofOpaque(color);
        }
        else{
            return Color.WHITE;
        }
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack tableStack) {
        ItemStack inHand = player.getItemInHand(handIn);
        if(inHand.getItem() instanceof ModularStaffCrystal crystal){
            addCrystal(tableStack, inHand, world, pos, player);
            return true;
        }
        else if(inHand.getItem() instanceof ShearsItem){
            if(hasUpgrades(tableStack)){
                removeAndDropUpgrades(tableStack,world,pos);
            }
            else{
                removeAndDropCrystal(tableStack,world,pos,player);
            }
            return true;
        }
        else {
            return ICasterTool.super.onScribe(world, pos, player, handIn, tableStack);
        }
    }

    //we override to change the default color
    @Override
    public int getColor(ItemStack p_41122_) {
        int defaultColor = Color.WHITE.getColor();
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : defaultColor;
    }

    Spell modifySpell(Spell spell, ItemStack staff){
        //clone stats modifier
        StatsModifier modifier = new StatsModifier(this.statsModifier);

        ItemStack crystal = getCrystal(staff);
        if(!crystal.isEmpty() && crystal.getItem() instanceof ModularStaffCrystal crystalItem){
            crystalItem.modifySpell(spell, crystal, staff, this);
        }

        return modifier.ModifySpell(spell);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        ISpellCaster caster = this.getSpellCaster(stack);

        Spell spell = caster.getSpell();

        spell = this.modifySpell(spell, stack);

        SpellContext context = new SpellContext(worldIn, spell,playerIn);
        SpellResolver resolver = new SpellResolver(context);

        if(spell.isEmpty() || !(spell.recipe.get(0) instanceof AbstractCastMethod)){
            PortUtil.sendMessageNoSpam(playerIn, Component.literal("No spell"));
            return new InteractionResultHolder<>(InteractionResult.PASS,stack);
        }

        ItemStack crystal = getCrystal(stack);
        if(crystal.isEmpty()){
            PortUtil.sendMessageNoSpam(playerIn, Component.literal("Cannot cast spell without a staff crystal. "));
            return new InteractionResultHolder<>(InteractionResult.PASS,stack);
        }

        try {
            //sorry for all the reflection shenanigans
            ISpellValidator validator = ArsNouveauAPI.getInstance().getSpellCastingSpellValidator();
            List<SpellValidationError> validationErrors = validator.validate(spell.recipe);
            for(SpellValidationError error : validationErrors){
                if(!(AugmentError.isInstance(error))){
                    PortUtil.sendMessageNoSpam(playerIn, error.makeTextComponentExisting());
                    return new InteractionResultHolder<>(InteractionResult.PASS,stack);
                }
            }

            if((boolean) enoughMana.invoke(resolver,playerIn)) {
                caster.getSpell().getCastMethod().onCast(stack, playerIn, worldIn, (SpellStats) getStats.invoke(resolver), context, resolver);
                resolver.expendMana();
            }
            else{
                return new InteractionResultHolder<>(InteractionResult.PASS,stack);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS,stack);
    }

    @Override
    public void registerControllers(AnimationData data) {
        for(StaffModelPart part : StaffModelPart.values()) {
            data.addAnimationController(new StaffAnimationController(this, part.name(), 20.0F, this::predicate, part));
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public boolean isScribedSpellValid(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch((s) -> {
            return s instanceof AbstractCastMethod;
        });
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.wand.invalid"));
    }

    @Override
    public boolean setSpell(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        //only add the necessary projectile
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodProjectile.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;

        return ICasterTool.super.setSpell(caster, player, hand, stack, spell);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        tooltip2.add(Component.literal("WARNING: THIS ITEM IS WIP").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        this.getInformation(stack, worldIn, tooltip2, flagIn);
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        tooltip2.add(Component.literal("Can be dyed"));

        ItemStack crystal = getCrystal(stack);
        if(!crystal.isEmpty()){
            tooltip2.add(Component.literal("Crystal: ").append(crystal.getHoverName()));
            if(crystal.getItem() instanceof ModularStaffCrystal crystalItem) {
                tooltip2.add(Component.literal("Crystal Bonuses: "));
                crystalItem.addBonusesTooltip(stack, worldIn, tooltip2);
            }

        }

        tooltip2.add(Component.literal("Staff Bonuses: "));
        statsModifier.addTooltip(tooltip2);

        tooltip2.add(Component.literal("Remove upgrades and crystal with shears on a scribe's table."));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new StaffRenderer(new StaffModel());

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}

