/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2596
 */
package ru.decide.manager.event_impl;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2596;
import ru.decide.manager.events.CancellableEvent;

@Environment(value=EnvType.CLIENT)
public class EventPacket
extends CancellableEvent {
    private class_2596<?> packet;
    private Type type;

    public boolean isSend() {
        return this.type.equals((Object)Type.SEND);
    }

    @Generated
    public class_2596<?> getPacket() {
        return this.packet;
    }

    @Generated
    public Type getType() {
        return this.type;
    }

    @Generated
    public void setPacket(class_2596<?> packet) {
        this.packet = packet;
    }

    @Generated
    public void setType(Type type) {
        this.type = type;
    }

    @Generated
    public EventPacket(class_2596<?> packet, Type type) {
        this.packet = packet;
        this.type = type;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        SEND,
        RECEIVE;

    }
}

