package com.dkmk100.arsomega.items;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.Item.Properties;

public class ModSpawnEggItem extends ForgeSpawnEggItem {
    String myName;

    public ModSpawnEggItem(RegistryObject<? extends EntityType<? extends Mob>> entityTypeIn, int primaryColorIn, int secondaryColorIn, Properties builder) {
        super(() -> entityTypeIn.get(), primaryColorIn, secondaryColorIn, builder);
        entityTypeSupplier=Lazy.of(entityTypeIn);
        //UNADDED_EGGS.add(this);
    }
    //protected static final List<ModSpawnEggItem> UNADDED_EGGS = new ArrayList<>();

    private final Lazy<? extends EntityType<?>> entityTypeSupplier;
    /*
    public static void initSpawnEggs(){

        final Map<EntityType<?>, SpawnEggItem> EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class,null,"f_43201_");

        DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Direction direction=source.getBlockState().getValue(DispenserBlock.FACING);
                EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
                type.spawn(source.getLevel(),stack,null,source.getPos(), MobSpawnType.DISPENSER, direction != Direction.DOWN, false);
                stack.shrink(1);
                return stack;
            }

        };
        for(final SpawnEggItem spawnEgg : UNADDED_EGGS)
        {
            EGGS.put(spawnEgg.getType(null),spawnEgg);
            DispenserBlock.registerBehavior(spawnEgg,dispenseBehavior);
        }
        UNADDED_EGGS.clear();
    }


    @Override
    public EntityType<?> getType(CompoundTag nbt){
        return this.entityTypeSupplier.get();
    }
     */
}
