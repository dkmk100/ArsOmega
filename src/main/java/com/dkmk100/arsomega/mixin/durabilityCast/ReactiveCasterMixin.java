package com.dkmk100.arsomega.mixin.durabilityCast;

import com.dkmk100.arsomega.IReactiveFlag;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReactiveCaster.class)
public class ReactiveCasterMixin extends SpellCaster {
    public ReactiveCasterMixin(ItemStack stack) {
        super(stack);
    }

    @Inject(at = @At("RETURN"), method = "Lcom/hollingsworth/arsnouveau/common/spell/casters/ReactiveCaster;getSpellResolver(Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;)Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;", cancellable = true, remap = false)
    private void getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn, CallbackInfoReturnable<SpellResolver> cir) {
        SpellResolver returnVal = cir.getReturnValue();
        try{
            LogManager.getLogger().info("reactive get spell resolver");
            IReactiveFlag flag = (IReactiveFlag) returnVal;
            flag.setTrueItem(this.stack);
            flag.setReactive(true);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
