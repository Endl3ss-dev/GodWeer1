/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render.richdog.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class PetAnimation {
    private float current = 0.0f;
    private float target = 0.0f;
    private float origin = 0.0f;
    private long startMs = 0L;
    private int durationMs = 1;

    public float animate(float destination, int ms) {
        if (destination == this.target) {
            return this.get();
        }
        this.origin = this.get();
        this.target = destination;
        this.durationMs = Math.max(1, ms);
        this.startMs = System.currentTimeMillis();
        return this.get();
    }

    public float get() {
        long elapsed = System.currentTimeMillis() - this.startMs;
        if (elapsed >= (long)this.durationMs) {
            this.current = this.target;
            return this.current;
        }
        float t = (float)elapsed / (float)this.durationMs;
        t = t * t * (3.0f - 2.0f * t);
        this.current = this.origin + (this.target - this.origin) * t;
        return this.current;
    }

    public boolean finished() {
        return System.currentTimeMillis() - this.startMs >= (long)this.durationMs;
    }
}

