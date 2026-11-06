/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.media;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class LyricWord {
    private final long startMillis;
    private final long endMillis;
    private final String text;
    private final int begin;
    private final int end;

    public LyricWord(long startMillis, long endMillis, String text, int begin, int end) {
        this.startMillis = startMillis;
        this.endMillis = endMillis;
        this.text = text;
        this.begin = begin;
        this.end = end;
    }

    public long startMillis() {
        return this.startMillis;
    }

    public long endMillis() {
        return this.endMillis;
    }

    public String text() {
        return this.text;
    }

    public int begin() {
        return this.begin;
    }

    public int end() {
        return this.end;
    }

    public long durationMillis() {
        return Math.max(0L, this.endMillis - this.startMillis);
    }

    public boolean contains(long millis) {
        return millis >= this.startMillis && millis < this.endMillis;
    }

    public float progress(long millis) {
        long duration = this.durationMillis();
        if (duration <= 0L) {
            return millis >= this.startMillis ? 1.0f : 0.0f;
        }
        return Math.min(1.0f, Math.max(0.0f, (float)(millis - this.startMillis) / (float)duration));
    }

    public String toString() {
        return "LyricWord(" + this.startMillis + ".." + this.endMillis + " '" + this.text + "' " + this.begin + ".." + this.end + ")";
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LyricWord)) {
            return false;
        }
        LyricWord w = (LyricWord)other;
        return this.startMillis == w.startMillis && this.endMillis == w.endMillis && this.begin == w.begin && this.end == w.end && this.text.equals(w.text);
    }

    public int hashCode() {
        int result = Long.hashCode(this.startMillis);
        result = 31 * result + Long.hashCode(this.endMillis);
        result = 31 * result + this.text.hashCode();
        result = 31 * result + Integer.hashCode(this.begin);
        result = 31 * result + Integer.hashCode(this.end);
        return result;
    }
}

