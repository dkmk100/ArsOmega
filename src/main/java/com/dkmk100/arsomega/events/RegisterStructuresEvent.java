package com.dkmk100.arsomega.events;

import net.minecraftforge.eventbus.api.Event;

public class RegisterStructuresEvent extends Event {
    String description;
    public RegisterStructuresEvent(String description){
        super();
        this.description = description;
    }

}
