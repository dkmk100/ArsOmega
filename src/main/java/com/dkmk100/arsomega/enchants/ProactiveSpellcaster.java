package com.dkmk100.arsomega.enchants;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ProactiveSpellcaster extends SpellCaster {
    static final int slotId = 0;
    public ProactiveSpellcaster(ItemStack stack) {
        this(stack.getOrCreateTag());
        //to fix the super with empty
        this.stack = stack;
    }

    public ProactiveSpellcaster(CompoundTag tag) {
        super(ItemStack.EMPTY);
        this.stack = null;
        int i = slotId;
        CompoundTag tag2 = tag.getCompound(this.getTagID());
        this.setCurrentSlot(this.getCurrentSlot());
        if (tag2.contains("spell_" + i)) {
            Spell spell = Spell.deserialize(tag2.getString("spell_" + i));
            this.setSpell(spell, i);
        }

        if (tag2.contains("spell_name_" + i)) {
            this.setSpellName(tag2.getString("spell_name_" + i), i);
        }

        if (tag2.contains("spell_color_" + i)) {
            this.setColor(ParticleColor.IntWrapper.deserialize(tag2.getString("spell_color_" + i)), i);
        }

        if (tag2.contains("spell_sound_" + i)) {
            this.setSound(ConfiguredSpellSound.fromTag(tag2.getCompound("spell_sound_" + i)), i);
        }

    }

    @Override
    public CompoundTag writeTag(CompoundTag tag) {
        tag.putInt("current_slot", this.getCurrentSlot());
        tag.putString("flavor", this.getFlavorText());

        int i = slotId;
        tag.putString("spell_" + i, this.getSpell(i).serialize());
        tag.putString("spell_name_" + i, this.getSpellName(i));
        tag.putString("spell_color_" + i, this.getColor(i).serialize());
        tag.put("spell_sound_" + i, this.getSound(i).serialize());

        return tag;
    }

    public String getTagID() {
        return "arsomega_proactiveCaster";
    }

    @Override
    public int getCurrentSlot() {
        return slotId;
    }

    @NotNull
    @Override
    public Spell getSpell(int slot) {
        return super.getSpell(slot);
    }
}
