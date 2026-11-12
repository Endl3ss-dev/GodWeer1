/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.optimization.velotune.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.optimization.velotune.input.WindowsRawInputBuffer;

@Environment(value=EnvType.CLIENT)
public final class RawInputManager {
    private static final WindowsRawInputBuffer BUFFER = new WindowsRawInputBuffer();

    private RawInputManager() {
    }

    public static WindowsRawInputBuffer buffer() {
        return BUFFER;
    }
}

