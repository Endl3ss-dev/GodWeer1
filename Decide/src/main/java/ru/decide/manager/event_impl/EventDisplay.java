/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 */
package ru.decide.manager.event_impl;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import ru.decide.manager.events.Event;

@Environment(value=EnvType.CLIENT)
public class EventDisplay
extends Event {
    private class_332 drawContext;
    private float partialTicks;

    @Generated
    public class_332 getDrawContext() {
        return this.drawContext;
    }

    @Generated
    public float getPartialTicks() {
        return this.partialTicks;
    }

    @Generated
    public EventDisplay(class_332 drawContext, float partialTicks) {
        this.drawContext = drawContext;
        this.partialTicks = partialTicks;
    }
}

