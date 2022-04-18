/*
package com.dkmk100.arsomega.structures;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.init.StructureInit;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature.StructureStartFactory;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class DemonicDungeonStructure extends StructureFeature<NoneFeatureConfiguration> {

    public DemonicDungeonStructure(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return Start::new;
    }

    @Override
    public String getFeatureName() {
        return ArsOmega.MOD_ID + ":demonic_dungeon";
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed, WorldgenRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoneFeatureConfiguration featureConfig) {
        BlockPos centerOfChunk = new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8);

        // Grab height of land. Will stop at first non-air block.
        int landHeight = chunkGenerator.getFirstOccupiedHeight(centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Types.WORLD_SURFACE_WG);

        // Grabs column of blocks at given position. In overworld, this column will be made of stone, water, and air.
        // In nether, it will be netherrack, lava, and air. End will only be endstone and air. It depends on what block
        // the chunk generator will place for that dimension.
        BlockGetter columnOfBlocks = chunkGenerator.getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ());

        // Combine the column of blocks with land height and you get the top block itself which you can test.
        BlockState topBlock = columnOfBlocks.getBlockState(centerOfChunk.above(landHeight));

        // Now we test to make sure our structure is not spawning on water or other fluids.
        // You can do height check instead too to make it spawn at high elevations.
        return topBlock.getFluidState().isEmpty(); //landHeight > 100;
        //return true;
    }

/*
    public ChunkPos getStartPositionForPosition(ChunkGenerator generator, Random rand, int chunkX, int chunkZ, int offsetX, int offsetZ){
        int maxDistance = 150;
        int minDistance = 85;
        int xTemp = chunkX + maxDistance * offsetX;
        int ztemp = chunkZ + maxDistance * offsetZ;
        int xTemp2 = xTemp < 0 ? xTemp - maxDistance + 1 : xTemp;
        int zTemp2 = ztemp < 0 ? ztemp - maxDistance + 1 : ztemp;
        int validChunkX = xTemp2 / maxDistance;
        int validChunkZ = zTemp2 / maxDistance;

        ((SharedSeedRandom) rand).setLargeFeatureWithSalt(134789, validChunkX, validChunkZ, this.getSeedModifier());
        validChunkX = validChunkX * maxDistance;
        validChunkZ = validChunkZ * maxDistance;
        validChunkX = validChunkX + rand.nextInt(maxDistance - minDistance);
        validChunkZ = validChunkZ + rand.nextInt(maxDistance - minDistance);
        return new ChunkPos(validChunkX,validChunkZ);
    }


    protected int getSeedModifier(){
        return 648972147;
    }
    //*/
/*
    public static class Start extends StructureStart {

        public Start(StructureFeature<?> structure, int chunkX, int chunkZ, BoundingBox boundingBox, int reference, long seed) {
            super(structure, chunkX, chunkZ, boundingBox, reference, seed);
        }

        @Override
        public void generatePieces(RegistryAccess registries, ChunkGenerator generator, StructureManager templateManagerIn, int chunkX, int chunkZ, Biome biome, FeatureConfiguration config) {
            ArsOmega.LOGGER.debug("PLACING STRUCTURE");
            //Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
            int x = chunkX * 16;
            int z = chunkZ * 16;
            int y = generator.getBaseHeight(x + 8, z + 8, Heightmap.Types.WORLD_SURFACE_WG);
            BlockPos pos = new BlockPos(x, y, z);

            ResourceLocation location = new ResourceLocation(ArsOmega.MOD_ID, "demonic_dungeon");
            JigsawPlacement.addPieces(registries, new JigsawConfiguration(() -> registries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(location), 10),
                    PoolElementStructurePiece::new, generator, templateManagerIn, pos, this.pieces, this.random, false, false);

            this.calculateBoundingBox();

            ArsOmega.LOGGER.debug("Rundown House at " +
                    ((StructurePiece)this.pieces.get(0)).getBoundingBox().x0 + " " +
                    ((StructurePiece)this.pieces.get(0)).getBoundingBox().y0 + " " +
                    ((StructurePiece)this.pieces.get(0)).getBoundingBox().z0);
        }

    }
}
 */
