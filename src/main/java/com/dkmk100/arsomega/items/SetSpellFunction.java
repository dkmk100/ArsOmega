package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
 
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;

public class SetSpellFunction extends LootItemConditionalFunction {

    Spell spell;
    String name;
    ParticleColor.IntWrapper color;
    boolean hasColor = false;
    protected SetSpellFunction(LootItemCondition[] p_80678_, Spell spell, String name, ParticleColor.IntWrapper color, boolean hasColor) {
        super(p_80678_);
        this.spell = spell;
        this.name = name;
        this.color = color;
        this.hasColor = hasColor;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext p_80680_) {
        ISpellCaster spellCaster = CasterUtil.getCaster(stack);
        spellCaster.setSpell(spell);
        if(hasColor) {
            spellCaster.setColor(color.toParticleColor());
        }
        stack.setHoverName((Component.literal(name)).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true)));
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return RegistryHandler.SET_SPELL_TYPE;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetSpellFunction>{

        @Override
        public SetSpellFunction deserialize(JsonObject object, JsonDeserializationContext context, LootItemCondition[] conditions) {
            ArsOmega.LOGGER.info("deserializing set spell function");
            String name = GsonHelper.getAsString(object, "name");
            JsonArray glyphs = GsonHelper.getAsJsonArray(object,"spell");
            ArrayList<AbstractSpellPart> components = new ArrayList<>();
            ParticleColor.IntWrapper color = new ParticleColor.IntWrapper(255,255,255);
            boolean hasColor = false;
            for(int i=0;i<glyphs.size();i++){
                String glyph = glyphs.get(i).getAsString();
                AbstractSpellPart component = ArsNouveauAPI.getInstance().getSpellpartMap().get(glyph);
                components.add(component);
            }
            try{
                JsonArray colors = GsonHelper.getAsJsonArray(object,"color");
                color = new ParticleColor.IntWrapper(colors.get(0).getAsInt(),colors.get(1).getAsInt(),colors.get(2).getAsInt());
                hasColor = true;
            }
            catch (Exception e){
                ArsOmega.LOGGER.error(e.getMessage());
            }
            return new SetSpellFunction(conditions,new Spell(components),name,color,hasColor);
        }
    }

}
