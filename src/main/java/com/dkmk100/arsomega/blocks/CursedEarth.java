package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IPlantable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CursedEarth extends GrassBlock {

    static class Stats {
        private int minTickTime = 18;
        public int maxTickTime = 32;

        public int maxMobs = 450;

        public int minPlayerDist = 3;

        public int amountToSpawn = 1;

        public int maxNearbyMobs = 8;
    }

    Stats stats = new Stats();


    public CursedEarth(Properties p_53685_) {
        super(p_53685_);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        world.scheduleTick(pos, state.getBlock(), stats.minTickTime + world.random.nextInt(stats.maxTickTime - stats.minTickTime + 1));
    }

    public void applyTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!world.isAreaLoaded(pos, 3))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading

        world.scheduleTick(pos, state.getBlock(), stats.minTickTime + random.nextInt(stats.maxTickTime - stats.minTickTime + 1));
        //dont spawn in water
        if (!world.getFluidState(pos.above()).isEmpty()) return;
        //don't spawn in peaceful
        if (world.getDifficulty() == Difficulty.PEACEFUL) return;
        //mobcap used because mobs are laggy in large numbers

        List<Entity> result = new ArrayList<>();
        (world).getEntities().getAll().forEach(result::add);

        AABB nearbyEntityBox = new AABB(pos).inflate(7, 4, 7);
        if (world.getEntitiesOfClass(LivingEntity.class, nearbyEntityBox, e -> e instanceof Monster).size() > stats.maxNearbyMobs)
            return;

        long mobcount = result.stream().count();

        if (mobcount > stats.maxMobs) return;
        int r = stats.minPlayerDist;
        if (world.getEntitiesOfClass(Player.class, new AABB(-r, -r, -r, r, r, r)).size() > 0) return;

        for (int i = 0; i < stats.amountToSpawn; i++) {
            Mob mob = findMonsterToSpawn(world, pos.above(), random);
            if (mob != null) {
                mob.setPos(new Vec3(pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5));
                if (!world.noCollision(mob)) return;
                world.addFreshEntity(mob);
            }
        }

    }


    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        applyTick(state,world,pos,random);
    }


    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return false;
    }


    @Override
    public boolean isBonemealSuccess(Level p_53697_, RandomSource p_53698_, BlockPos p_53699_, BlockState p_53700_) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel p_53687_, RandomSource p_53688_, BlockPos p_53689_, BlockState p_53690_) {

    }


    //Adapted from the Doomed Grass mod
    //https://github.com/embeddedt/DoomedGrass/tree/main
    private Mob findMonsterToSpawn(ServerLevel world, BlockPos pos, RandomSource rand) {
        ServerChunkCache s = (ServerChunkCache) world.getChunkSource();
        List<MobSpawnSettings.SpawnerData> entries = s.getGenerator()
                .getMobsAt(world.getBiome(pos), ((ServerLevel) world).structureManager(), MobCategory.MONSTER, pos.above())
                .unwrap().stream().toList();

        if (entries.size() > 0) {
            int found = rand.nextInt(entries.size());
            MobSpawnSettings.SpawnerData entry = entries.get(found);
            BlockPos.MutableBlockPos spawnMutable = new BlockPos.MutableBlockPos();
            spawnMutable.set(pos);
            if (!SpawnPlacements.checkSpawnRules(entry.type, world, MobSpawnType.NATURAL, pos, world.random))
                return null;
            EntityType type = entry.type;
            Entity ent = type.create(world);
            //cursed earth only works with hostiles
            if (!(ent instanceof Mob)) return null;
            ((Mob) ent).finalizeSpawn(world, world.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
            return (Mob) ent;
        }
        return null;
    }
}
