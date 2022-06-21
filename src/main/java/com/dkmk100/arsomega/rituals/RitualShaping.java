package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.blocks.PortalBlockEntity;
import com.dkmk100.arsomega.crafting.SigilRecipe;
import com.dkmk100.arsomega.crafting.SigilValidator;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class RitualShaping extends AbstractRitual {

    int sourceNeeded = 0;
    SigilRecipe chosenRecipe = null;
    SigilValidator.SigilValidationResult sigilResult;

    @Override
    protected void tick() {
        Level world = this.getWorld();
        BlockPos pos = this.getPos();
        if (world.isClientSide) {
            for (int i = 0; i < 100; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(this.getCenterColor()), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }

        //40 to make it slightly less annoying tps wise lol
        if (!world.isClientSide && world.getGameTime() % 40L == 0L) {
            if(this.needsManaNow()){
                return;
            }
            if(chosenRecipe == null){
                SigilValidator validator = SigilValidator.INSTANCE;
                List<SigilRecipe> recipes = world.getRecipeManager().getAllRecipesFor(RegistryHandler.SIGIL_TYPE);
                for(SigilRecipe recipe : recipes){

                    SigilValidator.SigilValidationResult result = validator.ValidateSigil(world,pos,recipe);
                    if(result.succeded()){
                        chosenRecipe = recipe;
                        sigilResult = result;

                        if(chosenRecipe.pattern.sourceCost == 0){
                            this.setNeedsMana(false);
                        }
                        else{
                            //quick check for mana to not delay craft on mana things
                            //we don't do this every time so we can get the ritual brazier to say missing mana lol
                            //range of 6 for parity with normal brazier range
                            if(SourceUtil.takeSourceNearbyWithParticles(pos,world,6,chosenRecipe.pattern.sourceCost) != null){
                                this.setNeedsMana(false);
                            }
                            else {
                                sourceNeeded = chosenRecipe.pattern.sourceCost;
                                this.setNeedsMana(true);
                            }
                        }
                        break;
                    }

                }
            }
            if(!this.needsManaNow() && chosenRecipe != null){
                SigilValidator validator = SigilValidator.INSTANCE;
                if(validator.isSigilValid(world,pos,chosenRecipe,sigilResult)) {
                    this.spawnAtLocation(chosenRecipe.output, 1.0f, pos);
                    validator.CleanupChalk(world,pos,chosenRecipe,sigilResult);
                    this.setFinished();
                    return;
                }
                else{
                    //reset recipe
                    this.chosenRecipe = null;
                    this.sigilResult = null;
                }
            }
        }
    }

    @Nullable
    public ItemEntity spawnAtLocation(ItemStack p_70099_1_, float p_70099_2_, BlockPos pos) {
        if (p_70099_1_.isEmpty()) {
            return null;
        } else if (getWorld().isClientSide) {
            return null;
        } else {
            ItemEntity itementity = new ItemEntity(this.getWorld(), pos.getX(),pos.getY()+p_70099_2_,pos.getZ(), p_70099_1_);
            itementity.setDefaultPickUpDelay();
            this.getWorld().addFreshEntity(itementity);
            return itementity;
        }
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(250,250,250);
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return false;
    }


    @Override
    public int getManaCost() {
        return sourceNeeded;
    }

    @Override
    public boolean consumesMana() {
        return true;
    }

    @Override
    public String getID() {
        return "shaping";
    }
}
