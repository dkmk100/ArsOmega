package com.dkmk100.arsomega.client.staff;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.items.ModularStaff;
import com.dkmk100.arsomega.util.IStaffModel;
import com.dkmk100.arsomega.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.builder.Animation;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;
import software.bernie.ars_nouveau.geckolib3.file.AnimationFile;
import software.bernie.ars_nouveau.geckolib3.geo.exception.GeckoLibException;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoModel;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;
import software.bernie.ars_nouveau.geckolib3.resource.GeckoLibCache;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class StaffModel extends AnimatedGeoModel implements IStaffModel {

    @Override
    public Animation getAnimation(String name, IAnimatable animatable) {
        return super.getAnimation(name, animatable);
    }

    @Override
    public Animation getAnimation(String name, ModularStaff staff, ItemStack stack, ModularStaff.StaffModelPart part){
        AnimationFile animation = GeckoLibCache.getInstance().getAnimations().get(this.getAnimation(stack, part));
        if (animation == null) {
            throw new GeckoLibException(this.getAnimationResource(staff), "Could not find animation file. Please double check name.");
        } else {
            return animation.getAnimation(name);
        }
    }

    @Override
    public ResourceLocation getModelResource(Object o) {
        return null;
    }

    @Override
    public ResourceLocation getTextureResource(Object o) {
        return null;
    }

    @Override
    public ResourceLocation getAnimationResource(Object o) {
        return null;
    }

    public ResourceLocation getModel(ItemStack stack, ModularStaff.StaffModelPart part) {
        return getOnStaffOrDefault(
                stack, (staff) -> staff.getModel(stack, part),
                () -> {
                    throw new IllegalStateException("stack isn't a staff");
                });
    }

    public ResourceLocation getTexture(ItemStack stack, ModularStaff.StaffModelPart part) {
        return getOnStaffOrDefault(
                stack, (staff) -> staff.getTexture(stack, part),
                () -> {
                    throw new IllegalStateException("stack isn't a staff");
                });
    }

    public ResourceLocation getAnimation(ItemStack stack, ModularStaff.StaffModelPart part) {
        return getOnStaffOrDefault(
                stack, (staff) -> staff.getAnimation(stack, part),
                () -> {
                    throw new IllegalStateException("stack isn't a staff");
                });
    }

    public Color getColor(ItemStack stack, ModularStaff.StaffModelPart part){
        return getOnStaffOrDefault(
                stack, (staff) -> staff.getColor(stack, part),
                () -> {
                    throw new IllegalStateException("stack isn't a staff");
                });
    }

    private <T> T getOnStaffOrDefault(ItemStack stack, Function<ModularStaff,T> func, Supplier<T> def){
        if(stack.getItem() instanceof ModularStaff staff){
            return func.apply(staff);
        }
        else{
            return def.get();
        }
    }
}
