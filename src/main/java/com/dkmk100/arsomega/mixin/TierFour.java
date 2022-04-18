package com.dkmk100.arsomega.mixin;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.common.spell.validation.BaseSpellValidationError;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphMaxTierValidator;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Field;

import java.util.List;

@Mixin(GlyphMaxTierValidator.class)
public class TierFour {
    /*
    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/common/spell/validation/GlyphMaxTierValidator;digestSpellPart(Lnet/minecraft/util/Unit;ILcom/hollingsworth/arsnouveau/api/spell/AbstractSpellPart;Ljava/util/List;)V", cancellable = true, remap = false)
    protected void digestSpellPart(Unit context, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors, CallbackInfo ci) {
        try {
            Field field = TierFour.class.getDeclaredField("maxTier");
            field.setAccessible(true);
            int tier = field.getInt(this);
            if (spellPart instanceof ICustomTier && tier < 3) {
                validationErrors.add(new BaseSpellValidationError(position, spellPart, "glyph_tier"));
            }
        }
        catch (Exception e){
            validationErrors.add(new BaseSpellValidationError(position, spellPart, "error"));
        }
    }

     */
}
