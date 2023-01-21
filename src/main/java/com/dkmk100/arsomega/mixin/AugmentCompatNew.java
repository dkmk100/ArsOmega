package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.glyphs.AdvancedAmplify;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.validation.ActionAugmentationPolicyValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.SpellPhraseValidator;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mixin(ActionAugmentationPolicyValidator.class)
public class AugmentCompatNew {

    private ResourceLocation getLimitValidatorName(AbstractAugment augment){
        ResourceLocation loc = augment.getRegistryName();
        if(augment instanceof AdvancedAmplify){
            loc = AugmentAmplify.INSTANCE.getRegistryName();
        }
        return loc;
    }



    @ModifyVariable(remap = false, at = @At("HEAD"), ordinal = 0, method = "Lcom/hollingsworth/arsnouveau/common/spell/validation/ActionAugmentationPolicyValidator;validatePhrase(Lcom/hollingsworth/arsnouveau/common/spell/validation/SpellPhraseValidator$SpellPhrase;Ljava/util/List;)V")
    public SpellPhraseValidator.SpellPhrase getDecoyPhase(SpellPhraseValidator.SpellPhrase phrase){

        Map<ResourceLocation, List<SpellPhraseValidator.SpellPhrase.SpellPartPosition<AbstractAugment>>> newMap = new HashMap<>();
        List<AbstractAugment> augments = phrase.getAugments();
        int i = 0;
        for(AbstractAugment augment : augments){
            ResourceLocation key = getLimitValidatorName(augment);
            if (!newMap.containsKey(key)) {
                newMap.put(key, new LinkedList());
            }
            (newMap.get(key)).add(new SpellPhraseValidator.SpellPhrase.SpellPartPosition(augment, phrase.getFirstPosition() + i + 1));
            i++;
        }

        try {
            SpellPhraseValidator.SpellPhrase decoy = (SpellPhraseValidator.SpellPhrase) ReflectionHandler.phraseConstructor.newInstance(phrase.getAction(), augments, newMap, phrase.getFirstPosition());
            return decoy;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
