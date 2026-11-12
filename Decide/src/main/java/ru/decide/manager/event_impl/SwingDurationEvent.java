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
public class SwingDurationEvent
extends CancellableEvent {
    private float animation;

    @Generated
    public float getAnimation() {
        return this.animation;
    }

    @Generated
    public void setAnimation(float animation) {
        this.animation = animation;
    }
}

