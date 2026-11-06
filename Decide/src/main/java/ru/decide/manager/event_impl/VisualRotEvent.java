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
import ru.decide.manager.events.CancellableEvent;

@Environment(value=EnvType.CLIENT)
public class VisualRotEvent
extends CancellableEvent {
    private float yaw;
    private float pitch;
    private float bodyaw;

    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Generated
    public float getPitch() {
        return this.pitch;
    }

    @Generated
    public float getBodyaw() {
        return this.bodyaw;
    }

    @Generated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Generated
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Generated
    public void setBodyaw(float bodyaw) {
        this.bodyaw = bodyaw;
    }

    @Generated
    public VisualRotEvent(float yaw, float pitch, float bodyaw) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.bodyaw = bodyaw;
    }
}

