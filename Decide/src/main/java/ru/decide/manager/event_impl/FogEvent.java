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
public class FogEvent
extends CancellableEvent {
    private float distance;
    private int color;

    @Generated
    public float getDistance() {
        return this.distance;
    }

    @Generated
    public int getColor() {
        return this.color;
    }

    @Generated
    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Generated
    public void setColor(int color) {
        this.color = color;
    }
}

