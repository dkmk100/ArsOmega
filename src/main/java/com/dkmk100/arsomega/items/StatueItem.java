package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.blocks.StatueTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StatueItem extends BasicBlockItem{
    public StatueItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component>components, TooltipFlag flag) {
        super.appendHoverText(stack,level,components,flag);
        Entity entity = getEntity(stack,level);
        if(entity!=null){
            List<Component> otherComponents = new ArrayList<>();

            MutableComponent entityComponent = Component.empty();

            if(entity instanceof LivingEntity living){
                if(living.isBaby()){
                    entityComponent.append("Baby ");
                }
                for(EquipmentSlot slot : EquipmentSlot.values()){
                   ItemStack item = living.getItemBySlot(slot);
                   if(!item.isEmpty()){
                       otherComponents.add(Component.literal("with ").append(item.getHoverName()));
                   }
                }
            }

            entityComponent.append(Component.translatable(entity.getType().toString()));
            components.add(Component.literal("Entity: ").append(entityComponent));
            components.addAll(otherComponents);
        }
    }

    public static Entity getEntity(ItemStack stack, @Nullable Level level){
        if(stack.hasTag()){
            if(!stack.getTag().contains("BlockEntityTag")){
                return null;
            }
            CompoundTag blockTag = stack.getTag().getCompound("BlockEntityTag");
            CompoundTag entityTag = blockTag.getCompound("entity");
            String backupId = blockTag.getString("entity_backup_id");
            ArsOmega.LOGGER.info("entity tag: "+entityTag);
            ArsOmega.LOGGER.info("backup id: "+backupId);
            return StatueTile.getEntity(null,entityTag,backupId,level);
        }
        return null;
    }
}
