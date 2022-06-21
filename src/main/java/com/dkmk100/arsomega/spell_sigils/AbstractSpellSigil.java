package com.dkmk100.arsomega.spell_sigils;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.crafting.SigilValidator;
import com.dkmk100.arsomega.items.DescribedItem;
import com.dkmk100.arsomega.items.ItemPropertiesCreator;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.dkmk100.arsomega.util.SigilPattern;
import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.core.jmx.Server;
import org.apache.logging.log4j.core.tools.Generate;

public abstract class AbstractSpellSigil extends DescribedItem implements IDisplayMana {
    public final SigilPattern pattern;

    private static Properties properties = ItemPropertiesCreator.creator.create(ArsOmega.itemGroup,64);

    public AbstractSpellSigil(String name) {
        super(name, properties, "Note: still WIP! Spell sigils are activated on chalk, and if the correct shape has been drawn, will cast a powerful effect at the location. For more information, check the worn notebook.");
        this.pattern = GeneratePattern();
    }

    public abstract void OnActivate(ServerLevel world, BlockPos pos, Player player);

    //note: no packet for it yet, so only shows up for the caster RN. Will fix later.
    public abstract void OnActivateClient(Level world, BlockPos pos, Player player);

    protected abstract int GetCost();
    protected abstract String[] GetPattern();

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if(RegistryHandler.CHALK_BLOCK.get().isValidChalk(world.getBlockState(pos))){
            SigilValidator.SigilValidationResult result = SigilValidator.INSTANCE.ValidateSigil(world,pos,pattern);
            if(result.succeded()) {
                if(world.isClientSide()){
                    OnActivateClient(world,pos,player);
                }
                else if(enoughMana(player)){
                    OnActivate((ServerLevel) world, pos, player);
                    if(!player.getAbilities().instabuild) {
                        context.getItemInHand().shrink(1);
                    }
                    expendMana(player);
                    SigilValidator.INSTANCE.CleanupChalk(world, pos, pattern, result);
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    private boolean enoughMana(LivingEntity entity) {
        int totalCost = this.pattern.sourceCost;
        IManaCap manaCap = (IManaCap) CapabilityRegistry.getMana(entity).orElse((IManaCap) null);
        if (manaCap == null) {
            return false;
        } else {
            boolean canCast = (double)totalCost <= manaCap.getCurrentMana() || entity instanceof Player && ((Player)entity).isCreative();
            if (!canCast && !entity.getCommandSenderWorld().isClientSide) {
                PortUtil.sendMessageNoSpam(entity, new TranslatableComponent("ars_nouveau.spell.no_mana"));
            }

            return canCast;
        }
    }

    private void expendMana(LivingEntity entity) {
        int totalCost = this.pattern.sourceCost;
        CapabilityRegistry.getMana(entity).ifPresent((mana) -> {
            mana.removeMana((double)totalCost);
        });
    }

    private SigilPattern GeneratePattern(){
        String[] stringPattern = GetPattern();
        int size1 = stringPattern.length;
        int size2 = stringPattern[0].length();
        int y = size1/2;
        int x = size2/2;
        boolean[][] recipe = new boolean[size1][];

        int i2 = 0;
        for (String row : stringPattern) {
            recipe[i2] = new boolean[row.length()];
            for (int i = 0; i < row.length(); i++) {
                char ch = row.charAt(i);
                if (ch == 'x' || ch == 'X') {
                    recipe[i2][i] = true;
                } else if (ch == ' ') {
                    recipe[i2][i] = false;
                } else {
                    recipe[i2][i] = false;
                    //center on pos
                    x = i;
                    y = i2;
                }
            }
            i2 += 1;
        }

        return new SigilPattern(recipe, x, y, this.GetCost());
    }
}
