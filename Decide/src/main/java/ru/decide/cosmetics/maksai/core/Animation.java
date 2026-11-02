/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.maksai.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class Animation {
    private Animation() {
    }

    public static int frameAt(long tick, int frames, int frametime) {
        if (frames > 1 && frametime > 0) {
            long t = Math.floorMod(tick, (long)frames * (long)frametime);
            return (int)(t / (long)frametime);
        }
        return 0;
    }
}

