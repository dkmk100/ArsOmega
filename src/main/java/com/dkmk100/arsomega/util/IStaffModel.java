package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.items.ModularStaff;
import net.minecraft.world.item.ItemStack;
import software.bernie.ars_nouveau.geckolib3.core.builder.Animation;

public interface IStaffModel {
    public Animation getAnimation(String name, ModularStaff staff, ItemStack stack, ModularStaff.StaffModelPart part);
}
