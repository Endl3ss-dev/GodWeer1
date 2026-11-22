/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.media;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.media.LyricWord;

@Environment(value=EnvType.CLIENT)
public final class LyricLine {
    private final long startMillis;
    private final long endMillis;
    private final String text;
    private final List<LyricWord> words;

    public LyricLine(long startMillis, long endMillis, String text, List<LyricWord> words) {
        this.startMillis = startMillis;
        this.endMillis = endMillis;
        this.text = text;
        this.words = words;
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

    public List<LyricWord> words() {
        return this.words;
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

    public LyricWord wordAt(long millis) {
        for (LyricWord word : this.words) {
            if (!word.contains(millis)) continue;
            return word;
        }
        return null;
    }

    public String toString() {
        return "LyricLine(" + this.startMillis + ".." + this.endMillis + " '" + this.text + "')";
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LyricLine)) {
            return false;
        }
        LyricLine line = (LyricLine)other;
        return this.startMillis == line.startMillis && this.endMillis == line.endMillis && this.text.equals(line.text) && this.words.equals(line.words);
    }

    public int hashCode() {
        int result = Long.hashCode(this.startMillis);
        result = 31 * result + Long.hashCode(this.endMillis);
        result = 31 * result + this.text.hashCode();
        result = 31 * result + this.words.hashCode();
        return result;
    }
}

