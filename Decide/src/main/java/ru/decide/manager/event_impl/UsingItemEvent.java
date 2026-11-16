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
public class UsingItemEvent
extends CancellableEvent {
    byte type;

    @Generated
    public byte getType() {
        return this.type;
    }

    @Generated
    public void setType(byte type) {
        this.type = type;
    }

    @Generated
    public UsingItemEvent(byte type) {
        this.type = type;
    }
}

