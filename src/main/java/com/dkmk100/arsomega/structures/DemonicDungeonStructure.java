package com.dkmk100.arsomega.structures;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.init.StructureInit;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class DemonicDungeonStructure extends Structure<NoFeatureConfig> {

    public DemonicDungeonStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Start::new;
    }

    @Override
    public String getFeatureName() {
        return ArsOmega.MOD_ID + ":demonic_dungeon";
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
        BlockPos centerOfChunk = new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8);

        // Grab height of land. Will stop at first non-air block.
        int landHeight = chunkGenerator.getFirstOccupiedHeight(centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Type.WORLD_SURFACE_WG);

        // Grabs column of blocks at given position. In overworld, this column will be made of stone, water, and air.
        // In nether, it will be netherrack, lava, and air. End will only be endstone and air. It depends on what block
        // the chunk generator will place for that dimension.
        IBlockReader columnOfBlocks = chunkGenerator.getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ());

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

    public static class Start extends StructureStart {

        public Start(Structure<?> structure, int chunkX, int chunkZ, MutableBoundingBox boundingBox, int reference, long seed) {
            super(structure, chunkX, chunkZ, boundingBox, reference, seed);
        }

        @Override
        public void generatePieces(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biome, IFeatureConfig config) {
            ArsOmega.LOGGER.debug("PLACING STRUCTURE");
            //Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
            int x = chunkX * 16;
            int z = chunkZ * 16;
            int y = generator.getBaseHeight(x + 8, z + 8, Heightmap.Type.WORLD_SURFACE_WG);
            BlockPos pos = new BlockPos(x, y, z);

            ResourceLocation location = new ResourceLocation(ArsOmega.MOD_ID, "demonic_dungeon");
            JigsawManager.addPieces(registries, new VillageConfig(() -> registries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(location), 10),
                    AbstractVillagePiece::new, generator, templateManagerIn, pos, this.pieces, this.random, false, false);

            this.calculateBoundingBox();

            ArsOmega.LOGGER.debug("Rundown House at " +
                    ((StructurePiece)this.pieces.get(0)).getBoundingBox().x0 + " " +
                    ((StructurePiece)this.pieces.get(0)).getBoundingBox().y0 + " " +
                    ((StructurePiece)this.pieces.get(0)).getBoundingBox().z0);
        }

    }
}

