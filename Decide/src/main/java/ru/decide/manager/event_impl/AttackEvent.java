/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 */
package ru.decide.manager.event_impl;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import ru.decide.manager.events.CancellableEvent;

@Environment(value=EnvType.CLIENT)
public class AttackEvent
extends CancellableEvent {
    private class_1297 target;

    @Generated
    public class_1297 getTarget() {
        return this.target;
    }

    @Generated
    public void setTarget(class_1297 target) {
        this.target = target;
    }

    @Generated
    public AttackEvent(class_1297 target) {
        this.target = target;
    }
}

