/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.melee;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.melee.MeleeTrack;

@Environment(value=EnvType.CLIENT)
public final class MeleeClip {
    private final String name;
    private final float length;
    private final MeleeTrack[] tracks;
    private final float[] soundTimes;
    private final String[] soundNames;

    MeleeClip(String name, float length, MeleeTrack[] tracks, float[] soundTimes, String[] soundNames) {
        this.name = name;
        this.length = length;
        this.tracks = tracks;
        this.soundTimes = soundTimes;
        this.soundNames = soundNames;
    }

    public String getName() {
        return this.name;
    }

    public float getLength() {
        return this.length;
    }

    public MeleeTrack[] getTracks() {
        return this.tracks;
    }

    public int soundCount() {
        return this.soundTimes.length;
    }

    public float soundTime(int index) {
        return this.soundTimes[index];
    }

    public String soundName(int index) {
        return this.soundNames[index];
    }
}

