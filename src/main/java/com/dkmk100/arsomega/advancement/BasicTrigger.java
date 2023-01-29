package com.dkmk100.arsomega.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class BasicTrigger extends SimpleCriterionTrigger<BasicTrigger.TriggerInstance> {

    final ResourceLocation ID;

    public BasicTrigger(ResourceLocation name){
        super();
        ID = name;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite predicate, DeserializationContext context) {
        return new TriggerInstance(ID, predicate);
    }

    public void Trigger(ServerPlayer player){
        this.trigger(player, (trigger) -> true);
    }


    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        public TriggerInstance(ResourceLocation name, EntityPredicate.Composite predicate) {
            super(name, predicate);
        }

    }
}
