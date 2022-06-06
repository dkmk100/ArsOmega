package com.dkmk100.arsomega.crafting;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class SigilValidator {
    public static SigilValidator INSTANCE = new SigilValidator();

    public class SigilValidationResult{
        Rotation rotation = Rotation.NONE;
        boolean succeded = false;
        public SigilValidationResult(boolean success){
            this.succeded = success;
        }
        public SigilValidationResult(Rotation rotation){
            this.succeded = true;
            this.rotation = rotation;
        }
        public boolean succeded(){
            return succeded;
        }
    }

    public void CleanupChalk(Level world, BlockPos centerPos, SigilRecipe recipe, SigilValidationResult result){
        this.CleanupChalk(world,centerPos,recipe,result.rotation);
    }

    public boolean isSigilValid(Level world, BlockPos centerPos, SigilRecipe recipe, SigilValidationResult result){
        return isSigilValid(world,centerPos,recipe,result.rotation);
    }

    public SigilValidationResult ValidateSigil(Level world, BlockPos centerPos, SigilRecipe recipe){
        for(Rotation rotation : Rotation.values()){
            SigilValidationResult result = ValidateSigil(world,centerPos,recipe,rotation);
            if(result.succeded){
                return result;
            }
        }
        return new SigilValidationResult(false);
    }

    private boolean isSigilValid(Level world, BlockPos centerPos, SigilRecipe recipe, Rotation rotation) {
        return ValidateSigil(world,centerPos,recipe,rotation).succeded;
    }

    public void CleanupChalk(Level world, BlockPos centerPos, SigilRecipe recipe, Rotation rotation){
        for(int y = 0; y< recipe.sizeY; y++){
            for(int x = 0; x< recipe.sizeX; x++){
                int relativeX = x - recipe.tileX;
                int relativeY = y - recipe.tileY;

                //skip center pos as this has the crafting block which is immune to being checked
                if(relativeX == 0 && relativeY == 0){
                    continue;
                }

                boolean value = recipe.isFilled(x,y);

                //technically relativeY is the z since we're converting from 2d recipe to 3d blockpos lol

                BlockPos test = new BlockPos(relativeX,0,relativeY);
                test = test.rotate(rotation);
                BlockPos pos = centerPos.offset(test);
                BlockState state = world.getBlockState(pos);


                if(value && state.getBlock() == RegistryHandler.CHALK_BLOCK.get()){
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private SigilValidationResult ValidateSigil(Level world, BlockPos centerPos, SigilRecipe recipe, Rotation rotation) {
        for(int y = 0; y< recipe.sizeY; y++){
            for(int x = 0; x< recipe.sizeX; x++){
                int relativeX = x - recipe.tileX;
                int relativeY = y - recipe.tileY;

                //skip center pos as this has the crafting block which is immune to being checked
                if(relativeX == 0 && relativeY == 0){
                    continue;
                }

                boolean value = recipe.isFilled(x,y);

                //technically relativeY is the z since we're converting from 2d recipe to 3d blockpos lol

                BlockPos test = new BlockPos(relativeX,0,relativeY);
                test = test.rotate(rotation);
                BlockState state = world.getBlockState(centerPos.offset(test));


                if(value && state.getBlock() != RegistryHandler.CHALK_BLOCK.get()){
                    //missing chalk
                    return new SigilValidationResult(false);
                }
                else if(!value && !state.isAir() && !BlockAllowed(state)){
                    //extra block in empty space on recipe
                    return new SigilValidationResult(false);
                }
            }
        }
        return new SigilValidationResult(rotation);
    }

    public boolean BlockAllowed(BlockState state){
        return state.getBlock() instanceof SourceJar;
    }
}
