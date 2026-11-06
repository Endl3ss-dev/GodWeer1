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
public class CameraPositionEvent
extends Event {
    private class_243 pos;

    @Generated
    public class_243 getPos() {
        return this.pos;
    }

    @Generated
    public void setPos(class_243 pos) {
        this.pos = pos;
    }

    @Generated
    public CameraPositionEvent(class_243 pos) {
        this.pos = pos;
    }
}

