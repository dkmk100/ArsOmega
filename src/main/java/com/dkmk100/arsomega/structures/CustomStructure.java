package com.dkmk100.arsomega.structures;

import com.dkmk100.arsomega.ArsOmega;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
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
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class CustomStructure  extends Structure<NoFeatureConfig> {

    public String name;
    boolean underground;
    int extraHeight;

    public int maxSpacing;
    public int minSpacing;

    public String[] biomes;

    public CustomStructure(String name, String[] biomes) {
        super(NoFeatureConfig.CODEC);
        this.name = name;
        this.minSpacing = 100;
        this.maxSpacing = 150;
        this.extraHeight = 0;
        this.underground = false;
        this.biomes = biomes;
    }
    public CustomStructure(String name, String[] biomes, boolean underground) {
        super(NoFeatureConfig.CODEC);
        this.name = name;
        this.minSpacing = 100;
        this.maxSpacing = 150;
        this.extraHeight = 0;
        this.underground = underground;
        this.biomes = biomes;
    }
    public CustomStructure(String name, String[] biomes, boolean underground, int extraHeight) {
        super(NoFeatureConfig.CODEC);
        this.name = name;
        this.extraHeight = extraHeight;
        this.underground = underground;
        this.biomes = biomes;
    }
    public CustomStructure(String name, String[] biomes, boolean underground, int extraHeight, int spacing) {
        super(NoFeatureConfig.CODEC);
        this.name = name;
        this.extraHeight = extraHeight;
        this.minSpacing = spacing;
        this.maxSpacing = Math.round(spacing * 1.5f);
        this.underground = underground;
        this.biomes = biomes;
    }
    public CustomStructure(String name, String[] biomes, boolean underground, int extraHeight, int spacing, int maxSpacing) {
        super(NoFeatureConfig.CODEC);
        this.name = name;
        this.extraHeight = extraHeight;
        this.minSpacing = spacing;
        this.maxSpacing = maxSpacing;
        this.underground = underground;
        this.biomes = biomes;
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
        return ArsOmega.MOD_ID + ":" + name;
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

    public class Start extends StructureStart {

        public Start(Structure<?> structure, int chunkX, int chunkZ, MutableBoundingBox boundingBox, int reference, long seed) {
            super(structure, chunkX, chunkZ, boundingBox, reference, seed);
        }

        @Override
        public void generatePieces(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biome, IFeatureConfig config) {
            //Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
            int x = chunkX * 16;
            int z = chunkZ * 16;
            float y = generator.getBaseHeight(x + 8, z + 8, Heightmap.Type.WORLD_SURFACE_WG);
            if(underground){
                y = 20 + (y / 2f);
            }
            y = Math.max(5,Math.min(y+extraHeight,240));
            BlockPos pos = new BlockPos(x, y, z);

            ResourceLocation location = new ResourceLocation(ArsOmega.MOD_ID, name);
            JigsawManager.addPieces(registries, new VillageConfig(() -> registries.registry(Registry.TEMPLATE_POOL_REGISTRY).get().get(location), 10),
                    AbstractVillagePiece::new, generator, templateManagerIn, pos, this.pieces, this.random, false, false);
            this.calculateBoundingBox();
        }
    }
}