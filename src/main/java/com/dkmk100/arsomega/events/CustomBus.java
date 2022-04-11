package com.dkmk100.arsomega.events;

import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.BusBuilder;

public class CustomBus extends EventBus {
    public static final CustomBus BUS = new CustomBus(BusBuilder.builder());
    public CustomBus(BusBuilder busBuilder) {
        super(busBuilder);
    }
}
