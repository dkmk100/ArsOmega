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
    /*
    @Redirect(at = @At(value = "INVOKE",
                    target = "Lcom/hollingsworth/arsnouveau/api/spell/AbstractSpellPart;getCompatibleAugments()Ljava/util/Set;"),method = "getCompatibleAugments()Ljava/util/Set;",remap = false)
    private static Set<AbstractAugment> getCompatibleAugments(AbstractSpellPart target)
    {
        System.out.println("redirect test");
        Set<AbstractAugment> baseCompatibleAugments = target.getCompatibleAugments();
        if (baseCompatibleAugments.contains(AugmentAmplify.INSTANCE)) {
            HashSet<AbstractAugment> changed = new HashSet<>(baseCompatibleAugments);
            changed.add(AdvancedAmplify.INSTANCE);
            return Collections.unmodifiableSet(changed);
        }

        HashSet<AbstractAugment> changed = new HashSet<>(baseCompatibleAugments);
        changed.add(AdvancedAmplify.INSTANCE);
        return Collections.unmodifiableSet(changed);

        //return baseCompatibleAugments;
    }

     //*/
    ///*
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
    //*/
    /*
    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/api/spell/AbstractSpellPart;getCompatibleAugments()Ljava/util/Set;", cancellable = true, remap = false)
    public void getCompatibleAugments(CallbackInfoReturnable<Set<AbstractAugment>> cir) {
        System.out.println("inject test");
        Set<AbstractAugment> baseCompatibleAugments = cir.getReturnValue();
        if (baseCompatibleAugments.contains(AugmentAmplify.INSTANCE)) {
            HashSet<AbstractAugment> changed = new HashSet<>(baseCompatibleAugments);
            changed.add(AdvancedAmplify.INSTANCE);
            cir.setReturnValue(Collections.unmodifiableSet(changed));
        }
        else {
            HashSet<AbstractAugment> changed = new HashSet<>(baseCompatibleAugments);
            changed.add(AdvancedAmplify.INSTANCE);
            cir.setReturnValue(Collections.unmodifiableSet(changed));
            //cir.setReturnValue(baseCompatibleAugments);
        }
    }
     //*/


}
