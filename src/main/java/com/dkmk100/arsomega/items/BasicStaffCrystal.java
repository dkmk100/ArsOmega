package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.client.renderer.ColoredItemRenderer;
import com.dkmk100.arsomega.util.ResourceUtil;
import com.dkmk100.arsomega.util.StatsModifier;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;
import software.bernie.ars_nouveau.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class BasicStaffCrystal extends ModularStaffCrystal implements DyeableLeatherItem, IAnimatable {
    StatsModifier modifier;
    boolean mixColors;
    int color;
    public BasicStaffCrystal(Properties properties, StatsModifier modifier, int color, boolean mixColors) {
        super(properties);
        this.modifier = modifier;
        this.color = color;
        this.mixColors = mixColors;
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        return false;
    }

    @Override
    public ResourceLocation getModel(ItemStack crystal, ItemStack staffStack, ModularStaff staff){
        return ResourceUtil.getModelResource("staff_gem");
    }

    @Override
    public Color getColor(ItemStack crystal, ItemStack staffStack, ModularStaff staff){
        if(hasCustomColor(crystal)){
            return Color.ofOpaque(getColor(crystal));
        }
        else if(mixColors) {
            ParticleColor color = staff.getSpellCaster(staffStack).getSpell().color;

            Color baseColor = Color.ofOpaque(getColor(crystal));

            int r = (color.getRedInt() + baseColor.getRed()) / 2;
            int g = (color.getGreenInt() + baseColor.getGreen()) / 2;
            int b = (color.getBlueInt() + baseColor.getBlue()) / 2;
            return Color.ofRGB(r,g,b);
        }
        else{
            ParticleColor color = staff.getSpellCaster(staffStack).getSpell().color;
            return Color.ofOpaque(color.getColor());
        }
    }

    @Override
    public Spell modifySpell(Spell spell, ItemStack crystal, ItemStack staffStack, ModularStaff staff) {
        StatsModifier modifier = new StatsModifier(this.modifier);

        return modifier.ModifySpell(spell);
    }

    @Override
    public void addBonusesTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> components) {
        this.modifier.addTooltip(components);
    }

    @Override
    public int getColor(ItemStack p_41122_) {
        int defaultColor = this.color;
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : defaultColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        components.add(Component.literal("Can be dyed"));
    }

    public AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new ColoredItemRenderer<>("staff_gem");

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}
