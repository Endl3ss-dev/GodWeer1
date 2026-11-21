/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.util.anim;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class TimerUtil {
    private long lastMS = System.currentTimeMillis();

    public TimerUtil() {
        this.resetCounter();
    }

    public static TimerUtil create() {
        return new TimerUtil();
    }

    public long getTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public void setTime(long l) {
        this.lastMS = l;
    }

    public void setLastMS(long l) {
        this.lastMS = System.currentTimeMillis() + l;
    }

    public long getLastMS() {
        return this.lastMS;
    }

    public boolean isReached(long l) {
        return System.currentTimeMillis() - this.lastMS > l;
    }

    public void resetCounter() {
        this.lastMS = System.currentTimeMillis();
    }
}

