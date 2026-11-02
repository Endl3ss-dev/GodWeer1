/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.event_impl;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.events.Event;

@Environment(value=EnvType.CLIENT)
public class ActionEvent
extends Event {
    private boolean sprintState;

    @Generated
    public boolean isSprintState() {
        return this.sprintState;
    }

    @Generated
    public void setSprintState(boolean sprintState) {
        this.sprintState = sprintState;
    }

    @Generated
    public String toString() {
        return "ActionEvent(sprintState=" + this.isSprintState() + ")";
    }

    @Generated
    public ActionEvent(boolean sprintState) {
        this.sprintState = sprintState;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ActionEvent)) {
            return false;
        }
        ActionEvent other = (ActionEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return this.isSprintState() == other.isSprintState();
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ActionEvent;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + (this.isSprintState() ? 79 : 97);
        return result;
    }
}

