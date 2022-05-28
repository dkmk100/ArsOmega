package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.glyphs.AdvancedAmplify;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.ibm.icu.impl.CollectionSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(AbstractSpellPart.class)
public class AugmentCompat {

    //pretty sure this class is useless now, but will keep around for another version or two incase I need it for reference
    /*
    @Inject(at = @At("RETURN"), method = "Lcom/hollingsworth/arsnouveau/api/spell/AbstractSpellPart;augmentSetOf([Lcom/hollingsworth/arsnouveau/api/spell/AbstractAugment;)Ljava/util/Set;", cancellable = true, remap = false)
    protected <AbstractArgument> void argumentSetOf(AbstractAugment[] augments, CallbackInfoReturnable<Set<AbstractAugment>> cir) {
        if(augments!=null) {
            List<AbstractArgument> list2 = new ArrayList<AbstractArgument>((Collection<? extends AbstractArgument>) Arrays.asList(augments));
            if(list2.contains(AugmentAmplify.INSTANCE)) {
                list2.add((AbstractArgument) AdvancedAmplify.INSTANCE);
                cir.setReturnValue(Collections.unmodifiableSet(new HashSet(list2)));
            }
        }
    }
     */


}
