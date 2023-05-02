package me.pignol.swift.client.event.events;

import me.pignol.swift.client.event.Stage;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;

public class EntityRemovedEvent extends Event {
    private final Entity entity;
    public EntityRemovedEvent(Entity entity) {
        this.setStage(Stage.PRE);
        this.entity = entity;
    }

    private void setStage(Stage pre) {
    }

    public Entity getEntity() {
        return this.entity;
    }
}
