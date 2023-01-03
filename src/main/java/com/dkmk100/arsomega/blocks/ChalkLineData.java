package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.ibm.icu.impl.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChalkLineData {

    public UUID owner;
    public Spell spell;
    public int charges;
    public ParticleColor color;

    public List<Pair<Entity,Long>> hits = new ArrayList<>();

    final int hitDelay = 20;
    final int playerHitDelay = 10;

    public BlockPos dataPos;

    public boolean CanHitEntity(Entity entity, long gameTime){
        boolean blocked = false;
        List<Pair<Entity,Long>> toRemove = new ArrayList<>();
        for (var hit : hits){
            int del = entity instanceof Player ? playerHitDelay : hitDelay;
            if(hit.second + del <= gameTime){
                toRemove.add(hit);
            }
            else if(hit.first == entity){
                blocked = true;
            }
        }

        for(var hit : toRemove){
            hits.remove(hit);
        }
        return !blocked && spell.recipe.get(0) instanceof MethodTouch;
    }

    public void AddEntity(Entity entity, long gameTime){
        hits.add(Pair.of(entity,gameTime));
    }


    public ChalkLineData(Spell spell){
        this.spell = spell;
        this.color = new ParticleColor(255,255,255);
    }
    public ChalkLineData(CompoundTag tag){
        this.spell = Spell.deserialize(tag.getString("spell"));
        this.charges = tag.getInt("charges");
        if (tag.contains("uuid")) {
            this.owner = tag.getUUID("uuid");
        }

        this.color = ParticleColor.IntWrapper.deserialize(tag.getString("color")).toParticleColor();
    }

    public void Serialize(CompoundTag tag){
        tag.putString("spell", this.spell.serialize());
        tag.putInt("charges", this.charges);
        if (this.owner != null) {
            tag.putUUID("uuid", this.owner);
        }

        if (this.color != null) {
            tag.putString("color", this.color.toWrapper().serialize());
        }
    }
}
