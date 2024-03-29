package com.dkmk100.arsomega.crafting;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.dkmk100.arsomega.util.SigilPattern;
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
        this.CleanupChalk(world,centerPos,recipe.pattern,result);
    }

    public void CleanupChalk(Level world, BlockPos centerPos, SigilPattern pattern, SigilValidationResult result){
        this.CleanupChalk(world,centerPos,pattern,result.rotation);
    }

    public boolean isSigilValid(Level world, BlockPos centerPos, SigilRecipe recipe, SigilValidationResult result){
        return isSigilValid(world,centerPos,recipe,result.rotation);
    }

    public SigilValidationResult ValidateSigil(Level world, BlockPos centerPos, SigilRecipe recipe){
            return ValidateSigil(world,centerPos,recipe.pattern);
    }

    public SigilValidationResult ValidateSigil(Level world, BlockPos centerPos, SigilPattern pattern){
        for(Rotation rotation : Rotation.values()){
            SigilValidationResult result = ValidateSigil(world,centerPos,pattern,rotation);
            if(result.succeded){
                return result;
            }
        }
        return new SigilValidationResult(false);
    }

    private boolean isSigilValid(Level world, BlockPos centerPos, SigilRecipe recipe, Rotation rotation) {
        return ValidateSigil(world,centerPos,recipe.pattern,rotation).succeded;
    }

    private void CleanupChalk(Level world, BlockPos centerPos, SigilPattern pattern, Rotation rotation){
        for(int y = 0; y< pattern.sizeY; y++){
            for(int x = 0; x< pattern.sizeX; x++){
                int relativeX = x - pattern.tileX;
                int relativeY = y - pattern.tileY;

                //skip center pos as this has the crafting block which is immune to being checked
                if(relativeX == 0 && relativeY == 0){
                    continue;
                }

                boolean value = pattern.isFilled(x,y);

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

    private SigilValidationResult ValidateSigil(Level world, BlockPos centerPos, SigilPattern pattern, Rotation rotation) {
        for(int y = 0; y< pattern.sizeY; y++){
            for(int x = 0; x< pattern.sizeX; x++){
                int relativeX = x - pattern.tileX;
                int relativeY = y - pattern.tileY;

                //skip center pos as this has the crafting block which is immune to being checked
                if(relativeX == 0 && relativeY == 0){
                    continue;
                }

                boolean value = pattern.isFilled(x,y);

                //technically relativeY is the z since we're converting from 2d recipe to 3d blockpos lol

                BlockPos test = new BlockPos(relativeX,0,relativeY);
                test = test.rotate(rotation);
                BlockState state = world.getBlockState(centerPos.offset(test));


                if(value && state.getBlock() != RegistryHandler.CHALK_BLOCK.get()){
                    //missing chalk
                    return new SigilValidationResult(false);
                }
                else if(!value && !BlockAllowed(state, world, centerPos.offset(test))){
                    //extra block in empty space on recipe
                    return new SigilValidationResult(false);
                }
            }
        }
        return new SigilValidationResult(rotation);
    }

    public boolean BlockAllowed(BlockState state, Level world, BlockPos pos){
        return state.isAir() || state.getBlock() instanceof SourceJar || (!state.isFaceSturdy(world, pos, Direction.UP) && state.getBlock() != RegistryHandler.CHALK_BLOCK.get());
    }
}
