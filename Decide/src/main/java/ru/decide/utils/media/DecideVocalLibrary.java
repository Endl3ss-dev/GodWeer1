/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.media;

import com.sun.jna.Library;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface DecideVocalLibrary
extends Library {
    public static final int FLAG_RUNNING = 1;
    public static final int FLAG_ENDPOINT = 2;
    public static final int FLAG_SILENT = 4;

    public int VocalStart(String var1);

    public void VocalStop();

    public void VocalDeinitialize();

    public float VocalLevel();

    public float VocalPeak();

    public int VocalFrames();

    public int VocalFlags();

    public int VocalApiVersion();
}

