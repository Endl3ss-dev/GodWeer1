/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.event_impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.events.Event;

@Environment(value=EnvType.CLIENT)
public class EventRotation
extends Event {
    public float yaw;
    public float pitch;
    public float partialTicks;

    public EventRotation(float yaw, float pitch, float partialTicks) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.partialTicks = partialTicks;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}

