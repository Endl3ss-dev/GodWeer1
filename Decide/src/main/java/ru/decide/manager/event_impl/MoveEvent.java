/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 */
package ru.decide.manager.event_impl;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import ru.decide.manager.events.Event;

@Environment(value=EnvType.CLIENT)
public class MoveEvent
extends Event {
    private class_243 movement;

    @Generated
    public class_243 getMovement() {
        return this.movement;
    }

    @Generated
    public void setMovement(class_243 movement) {
        this.movement = movement;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MoveEvent)) {
            return false;
        }
        MoveEvent other = (MoveEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        class_243 this$movement = this.getMovement();
        class_243 other$movement = other.getMovement();
        return !(this$movement == null ? other$movement != null : !this$movement.equals(other$movement));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MoveEvent;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        class_243 $movement = this.getMovement();
        result = result * 59 + ($movement == null ? 43 : $movement.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "MoveEvent(movement=" + String.valueOf(this.getMovement()) + ")";
    }

    @Generated
    public MoveEvent(class_243 movement) {
        this.movement = movement;
    }
}

