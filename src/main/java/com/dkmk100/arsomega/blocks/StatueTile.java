package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.client.StatueUtils;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Game;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class StatueTile extends ModdedTile {

    @Nullable
    private Entity cachedEntity;
    private CompoundTag entityTag;
    private String entityBackupId;


    //todo: box these and add player name
    private UUID playerId = null;
    private CompoundTag playerProfile = null;


    public StatueTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.StatueType.get(), pos, state);
    }

    //always save players even though they are marked as don't save
    String getSaveId(Entity entity){
        if(entity instanceof Player){
            return EntityType.getKey(entity.getType()).toString();
        }
        else{
            return entity.getEncodeId();
        }
    }

    //fix some issues related to saving players
    void saveEntity(Entity entity, CompoundTag compound){
        String s = getSaveId(entity);
        if (s == null) {
            return;
        } else {
            compound.putString("id", s);
            entity.saveWithoutId(compound);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        Entity entity = getEntity();
        if(entity != null){
            CompoundTag compound = new CompoundTag();

            //use custom save function to fix issues with player
            saveEntity(entity,compound);
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
        if(playerId != null){
            tag.putUUID("player_id",playerId);
        }
        if(playerProfile != null){
            tag.put("player_info",playerProfile);
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
        if(tag.contains("player_id")){
            playerId = tag.getUUID("player_id");
        }
        else{
            playerId = null;
        }
        if(tag.contains("player_info")){
            playerProfile = tag.getCompound("player_info");
        }
        else{
            playerProfile = null;
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.cachedEntity = null;
        this.entityTag = null;
        super.onDataPacket(net, pkt);
    }

    Logger logger = LoggerContext.getContext().getLogger(StatueTile.class);
    public void setEntity(@Nullable Entity entity){
        if(entity == null){
            cachedEntity = null;
            entityTag = null;
            entityBackupId = null;
        }
        else{
            if(entity instanceof Player player){
                playerId = entity.getUUID();
                playerProfile = new CompoundTag();
                NbtUtils.writeGameProfile(playerProfile, player.getGameProfile());
            }
            entityTag = new CompoundTag();
            saveEntity(entity, entityTag);

            cachedEntity = EntityType.loadEntityRecursive(entityTag,level,Function.identity());

            entityBackupId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        }
    }

    public static class StatuePlayerInfo{
        public UUID uuid;
        public GameProfile profile;
        private StatuePlayerInfo(UUID uuid, GameProfile profile){
            this.uuid = uuid;
            this.profile = profile;
        }
        private StatuePlayerInfo(@NotNull UUID uuid, @NotNull CompoundTag profile){
            if(uuid == null){
                throw new NullPointerException("uuid cannot be null");
            }
            if(profile == null){
                throw new NullPointerException("profile cannot be null");
            }
            this.uuid = uuid;
            this.profile = NbtUtils.readGameProfile(profile);
        }

        public @Nullable static StatuePlayerInfo of(@Nullable UUID uuid, @Nullable CompoundTag profile){
            if(uuid == null || profile == null){
                return null;
            }
            else{
                return new StatuePlayerInfo(uuid,profile);
            }
        }

    }

    public static Entity getEntity(@Nullable Entity cached, CompoundTag tag, String backupId, @Nullable Level level, @Nullable StatuePlayerInfo player){
        if(cached != null){
            return cached;
        }

        if(player!=null && level.isClientSide){
            Entity p = StatueUtils.CreateClientPlayer(player.profile, level);
            p.load(tag);
            return p;
        }

        if(tag != null){
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
        Entity entity = getEntity(cachedEntity,entityTag,entityBackupId,getLevel(), StatuePlayerInfo.of(playerId,playerProfile));

        //set cached entity
        this.cachedEntity = entity;

        return entity;
    }
}
