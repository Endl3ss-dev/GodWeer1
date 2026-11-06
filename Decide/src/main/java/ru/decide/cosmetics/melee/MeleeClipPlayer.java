/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1113
 */
package ru.decide.cosmetics.melee;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1113;
import ru.decide.cosmetics.melee.MeleeClip;
import ru.decide.cosmetics.melee.MeleePlayMode;
import ru.decide.cosmetics.melee.MeleeSounds;
import ru.decide.cosmetics.melee.MeleeTrack;

@Environment(value=EnvType.CLIENT)
final class MeleeClipPlayer {
    private static final int SOUND_CAPACITY = 4;
    private final List<class_1113> playing = new ArrayList<class_1113>(4);
    private MeleeClip clip;
    private MeleePlayMode mode = MeleePlayMode.ONCE_STOP;
    private float time;
    private int soundCursor;
    private boolean stopped = true;

    MeleeClipPlayer() {
    }

    void play(MeleeClip clip, MeleePlayMode mode) {
        this.stopSounds();
        this.clip = clip;
        this.mode = mode;
        this.time = 0.0f;
        this.soundCursor = 0;
        this.stopped = clip == null;
    }

    void clear() {
        this.stopSounds();
        this.clip = null;
        this.mode = MeleePlayMode.ONCE_STOP;
        this.time = 0.0f;
        this.soundCursor = 0;
        this.stopped = true;
    }

    private void stopSounds() {
        for (class_1113 sound : this.playing) {
            MeleeSounds.stop(sound);
        }
        this.playing.clear();
    }

    MeleeClip getClip() {
        return this.clip;
    }

    boolean isStopped() {
        return this.stopped || this.clip == null;
    }

    boolean isHolding() {
        return this.clip != null && this.mode == MeleePlayMode.ONCE_HOLD && this.time >= this.clip.getLength();
    }

    boolean isActive() {
        return this.clip != null && !this.stopped;
    }

    void setProgress(float progress) {
        if (this.clip == null) {
            return;
        }
        float length = this.clip.getLength();
        if (length <= 0.0f) {
            return;
        }
        this.time = Math.max(0.0f, Math.min(progress, 1.0f)) * length;
    }

    void advance(float delta) {
        if (this.clip == null || this.stopped) {
            return;
        }
        float length = this.clip.getLength();
        if (length <= 0.0f) {
            this.fireSounds(0.0f);
            return;
        }
        float next = this.time + delta;
        if (next < length) {
            this.time = next;
            this.fireSounds(next);
            return;
        }
        this.fireSounds(length);
        switch (this.mode) {
            case LOOP: {
                this.time = next % length;
                this.soundCursor = 0;
                this.fireSounds(this.time);
                break;
            }
            case ONCE_HOLD: {
                this.time = length;
                break;
            }
            default: {
                this.time = length;
                this.stopped = true;
            }
        }
    }

    void evaluate(float[] out) {
        if (this.clip == null) {
            return;
        }
        for (MeleeTrack track : this.clip.getTracks()) {
            track.sample(this.time, out);
        }
    }

    void markBones(boolean[] visible) {
        if (this.clip == null) {
            return;
        }
        for (MeleeTrack track : this.clip.getTracks()) {
            visible[track.getTarget()] = true;
        }
    }

    private void fireSounds(float time) {
        int count = this.clip.soundCount();
        while (this.soundCursor < count && this.clip.soundTime(this.soundCursor) <= time) {
            class_1113 sound = MeleeSounds.play(this.clip.soundName(this.soundCursor));
            ++this.soundCursor;
            if (sound == null) continue;
            this.pruneFinishedSounds();
            this.playing.add(sound);
        }
    }

    private void pruneFinishedSounds() {
        for (int i = this.playing.size() - 1; i >= 0; --i) {
            if (MeleeSounds.isActive(this.playing.get(i))) continue;
            this.playing.remove(i);
        }
    }
}

