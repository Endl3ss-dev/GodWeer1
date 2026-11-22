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
import ru.decide.utils.media.OptMediaArtist;

@Environment(value=EnvType.CLIENT)
public interface OptMediaLibrary
extends Library {
    public boolean GetCurrentMediaInfo(OptMediaArtist var1);

    public void FreeMediaInfo(OptMediaArtist var1);

    public boolean Play();

    public boolean Pause();

    public boolean TogglePlayPause();

    public boolean SkipNext();

    public boolean SkipPrevious();

    public void SeekToMs(long var1);

    public float GetVolume();

    public boolean SetVolume(float var1);

    public void Deinitialize();
}

