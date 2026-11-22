/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4587
 */
package ru.decide.manager.event_impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import ru.decide.manager.events.CancellableEvent;

@Environment(value=EnvType.CLIENT)
public class GlassHandsRenderEvent
extends CancellableEvent {
    private final Phase phase;
    private final class_4587 matrices;
    private final float tickDelta;

    public GlassHandsRenderEvent(Phase phase, class_4587 matrices, float tickDelta) {
        this.phase = phase;
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public class_4587 getMatrices() {
        return this.matrices;
    }

    public float getTickDelta() {
        return this.tickDelta;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Phase {
        PRE,
        POST;

    }
}

