package com.dkmk100.arsomega.enchants;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.IReactiveFlag;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

public class ProactiveSpellcaster extends SpellCaster {
    static final int slotId = 0;
    public ProactiveSpellcaster(ItemStack stack) {
        this(stack.getOrCreateTag());
        //to fix the super with empty
        this.stack = stack;
    }

    public ProactiveSpellcaster(CompoundTag tag) {
        super(tag);
    }

    /*
    public ProactiveSpellcaster(CompoundTag tag) {
        super(ItemStack.EMPTY);
        this.stack = null;
        int i = slotId;
        CompoundTag tag2 = tag.getCompound(this.getTagID().toString());
        this.setCurrentSlot(this.getCurrentSlot());
        if (tag2.contains("spell_" + i)) {
            Spell spell = Spell.fromTag(tag.getCompound("spell" + i));
            this.setSpell(spell, i);
        }


    }

     */



    @Override
    public SpellResolver getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn) {
        SpellResolver resolver = (SpellResolver) (playerIn instanceof Player && !(playerIn instanceof FakePlayer) ? super.getSpellResolver(context, worldIn, playerIn, handIn) : new EntitySpellResolver(context));
        IReactiveFlag flag = (IReactiveFlag) resolver;
        flag.setReactive(true);
        flag.setTrueItem(this.stack);
        return resolver;
    }


    /*
    @Override
    public CompoundTag writeTag(CompoundTag tag) {
        tag.putInt("current_slot", this.getCurrentSlot());
        tag.putString("flavor", this.getFlavorText());

        int i = slotId;
        tag.put("spell" + i, this.getSpell(i).serialize());

        return tag;
    }
     */

    @Override
    public ResourceLocation getTagID() {
        return new ResourceLocation(ArsOmega.MOD_ID,"proactive_caster");
    }

    /*
    @Override
    public int getCurrentSlot() {
        return slotId;
    }

    @NotNull
    @Override
    public Spell getSpell(int slot) {
        return super.getSpell(slot);
    }
     */
}
