package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.blocks.StatueBlock;
import com.dkmk100.arsomega.blocks.StatueTile;
import com.dkmk100.arsomega.capabilitysyncer.OmegaStatusesCapability;
import com.dkmk100.arsomega.capabilitysyncer.OmegaStatusesCapabilityAttacher;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;

public class PetrificationEffect extends MobEffect {
    public static final DamageSource PETRIFY = (new DamageSource("petrify")).bypassArmor().bypassInvul().bypassMagic();

    public PetrificationEffect() {
        super(MobEffectCategory.HARMFUL, 9211020);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "0def8a21-e182-42c8-8461-1bd6127cca30", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList();//does not include cleansing gem on purpose, ritual is complete
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int level) {
        int duration = entity.getEffect(this).getDuration();
        if(duration < 50){
            onPetrificationEnd(entity, level);
        }
        else if(level > 0){
            int durationDiv = duration / 80;
            int progress = Math.max(0, 10 - durationDiv);
            LazyOptional<OmegaStatusesCapability> optional = OmegaStatusesCapabilityAttacher.getLivingEntityCapability(entity).cast();
            optional.ifPresent((cap) -> {cap.setPetrificationProgress(progress);} );
        }
    }

    @Override
    public boolean isDurationEffectTick(int tick, int level) {
        return tick % 20 == 0;
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap manager, int level) {
        super.addAttributeModifiers(entity,manager,level);
        LazyOptional<OmegaStatusesCapability> optional = OmegaStatusesCapabilityAttacher.getLivingEntityCapability(entity).cast();
        optional.ifPresent((cap) -> {cap.setPetrified(true,level);});
        ArsOmega.LOGGER.info("set petrified to true");
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap manager, int level) {
        super.removeAttributeModifiers(entity, manager, level);
        LazyOptional<OmegaStatusesCapability> optional = OmegaStatusesCapabilityAttacher.getLivingEntityCapability(entity).cast();
        optional.ifPresent((cap) -> {cap.setPetrified(false,level);});
    }

    void onPetrificationEnd(LivingEntity entity, int level){
        if (level >= 1) {
            //don't petrify already dead entities to prevent all sorts of major issues
            if(entity.isDeadOrDying()){
                return;
            }
            if(entity instanceof Player player){
                if(player.getAbilities().instabuild && player.getAbilities().invulnerable){
                    return;//don't petrify players in creative mode
                }
            }
            //set petrified on entity to false before it dies
            LazyOptional<OmegaStatusesCapability> optional = OmegaStatusesCapabilityAttacher.getLivingEntityCapability(entity).cast();
            optional.ifPresent((cap) -> {cap.setPetrified(false,level);});

            //spawn statue, will be swapped for a real statue later
            Level world = entity.getLevel();
            Direction dir = entity.getDirection();
            BlockPos pos = entity.blockPosition();
            BlockState state = RegistryHandler.STATUE.get().defaultBlockState().setValue(StatueBlock.FACING,dir);
            world.setBlockAndUpdate(pos, state);

            BlockEntity be = world.getBlockEntity(pos);
            if(be instanceof StatueTile tile){
                tile.setEntity(entity);
            }

            //kill entity
            entity.setHealth(1);
            entity.hurt(PETRIFY, Float.MAX_VALUE);

            /*
            ArmorStand ent = new ArmorStand(entity.getCommandSenderWorld(), entity.getX(), entity.getY(), entity.getZ());
            entity.getCommandSenderWorld().addFreshEntity(ent);
            ent.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.STONE, 1));
            entity.getCommandSenderWorld().addFreshEntity(new ItemEntity(entity.getCommandSenderWorld(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Items.STONE, 1)));
            ent.setPose(entity.getPose());
            ent.setYHeadRot(entity.getYHeadRot());
            ent.setYBodyRot(entity.yBodyRot);

             */
        }
    }
}
