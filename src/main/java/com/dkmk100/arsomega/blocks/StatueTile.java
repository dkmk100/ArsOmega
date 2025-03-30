package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.StatueClientUtils;
import com.dkmk100.arsomega.util.StatueUtils;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class StatueTile extends ModdedTile {

    @Nullable
    private Entity cachedEntity;
    private CompoundTag entityTag;
    private String entityBackupId;

    private StatueUtils.StatuePlayerInfo playerInfo = null;

    static Logger logger = LoggerContext.getContext().getLogger(StatueTile.class);



    public StatueTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.StatueType.get(), pos, state);
    }



    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        Entity entity = getEntity();
        if(entity != null){
            CompoundTag compound = new CompoundTag();

            //use custom save function to fix issues with player
            StatueUtils.saveEntity(entity,compound);
            tag.put("entity",compound);

            entityBackupId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
            tag.putString("entity_backup_id",entityBackupId);
        }
        else if(entityTag != null){
            tag.put("entity",entityTag);

            if(entityBackupId != null){
                tag.putString("entity_backup_id",entityBackupId);
            }
        }

        if(playerInfo != null){
            tag.put("player",playerInfo.save());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("entity")){
            entityTag = tag.getCompound("entity");

        }
        if(tag.contains("entity_backup_id")){
            entityBackupId = tag.getString("entity_backup_id");
        }
        if(tag.contains("player")){
            playerInfo = StatueUtils.StatuePlayerInfo.load(tag.getCompound("player"));
        }
        else {
            //TODO remove this in the 1.20 port
            //legacy way statues stored player data

            UUID playerId = null;
            CompoundTag playerProfile = null;

            if (tag.contains("player_id")) {
                playerId = tag.getUUID("player_id");
            } else {
                playerId = null;
            }
            if (tag.contains("player_info")) {
                playerProfile = tag.getCompound("player_info");
            } else {
                playerProfile = null;
            }
            playerInfo = StatueUtils.StatuePlayerInfo.of(playerId, playerProfile);
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.cachedEntity = null;
        this.entityTag = null;
        super.onDataPacket(net, pkt);
    }

    public void setEntity(@Nullable Entity entity){
        if(entity == null){
            cachedEntity = null;
            entityTag = null;
            entityBackupId = null;
        }
        else{
            if(entity instanceof Player player){
                playerInfo = StatueUtils.StatuePlayerInfo.of(player);

                //TODO: fix crashes related to too much data here
                //likely through custom save code
                entityTag = new CompoundTag();
                StatueUtils.saveEntity(entity, entityTag);

                cachedEntity = EntityType.loadEntityRecursive(entityTag,level,Function.identity());

                entityBackupId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
            }
            else{
                entityTag = new CompoundTag();
                StatueUtils.saveEntity(entity, entityTag);

                cachedEntity = EntityType.loadEntityRecursive(entityTag,level,Function.identity());

                entityBackupId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
            }
        }
    }

    public static Entity getEntity(@Nullable Entity cached, CompoundTag tag, String backupId, @Nullable Level level, @Nullable StatueUtils.StatuePlayerInfo player){
        if(cached != null){
            return cached;
        }

        if(player!=null){
            if(level.isClientSide) {
                Entity p = StatueClientUtils.CreateClientPlayer(player,level);
                p.load(tag);
                return p;
            }
            else{
                return null;
            }
        }
        else if(tag != null){
            AtomicReference<Entity> entity = new AtomicReference<>(EntityType.loadEntityRecursive(tag, level, Function.identity()));
            if(entity.get() == null){
                if(backupId != null && level != null){
                    EntityType.byString(backupId).ifPresent((type) -> {
                        entity.set((Entity) type.create(level));
                    });
                }
            }
            cached = entity.get();
            return cached;
        }

        return null;
    }

    @Nullable
    public Entity getEntity() {
        Entity entity = getEntity(cachedEntity,entityTag,entityBackupId,getLevel(), this.playerInfo);

        //set cached entity
        this.cachedEntity = entity;

        return entity;
    }
}
