package com.dkmk100.arsomega.items;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModSpawnEggItem extends SpawnEggItem {
    public ModSpawnEggItem(RegistryObject<? extends EntityType<?>> entityTypeIn, int primaryColorIn, int secondaryColorIn, Properties builder) {
        super(null, primaryColorIn, secondaryColorIn, builder);
        entityTypeSupplier=Lazy.of(entityTypeIn);
        UNADDED_EGGS.add(this);
    }
    protected static final List<ModSpawnEggItem> UNADDED_EGGS = new ArrayList<>();

    private final Lazy<? extends EntityType<?>> entityTypeSupplier;
    public static void initSpawnEggs(){
        final Map<EntityType<?>, SpawnEggItem> EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class,null,"field_195987_b");
        DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior(){

            @Override
            protected ItemStack execute(IBlockSource source, ItemStack stack) {
                Direction direction=source.getBlockState().getValue(DispenserBlock.FACING);
                EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
                type.spawn(source.getLevel(),stack,null,source.getPos(), SpawnReason.DISPENSER, direction != Direction.DOWN, false);
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
    public EntityType<?> getType(CompoundNBT nbt){
        return this.entityTypeSupplier.get();
    }
}
