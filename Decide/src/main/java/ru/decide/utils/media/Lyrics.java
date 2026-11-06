/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.media;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.media.LyricLine;
import ru.decide.utils.media.LyricWord;

@Environment(value=EnvType.CLIENT)
public final class Lyrics {
    public static final Lyrics EMPTY = new Lyrics(Collections.emptyList(), false);
    private final List<LyricLine> lines;
    private final boolean synced;

    public Lyrics(List<LyricLine> lines, boolean synced) {
        this.lines = lines;
        this.synced = synced;
    }

    public List<LyricLine> lines() {
        return this.lines;
    }

    public boolean synced() {
        return this.synced;
    }

    public boolean isEmpty() {
        return this.lines.isEmpty();
    }

    public int indexAt(long millis) {
        if (!this.synced) {
            return -1;
        }
        int low = 0;
        int high = this.lines.size() - 1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            LyricLine line = this.lines.get(middle);
            if (millis < line.startMillis()) {
                high = middle - 1;
                continue;
            }
            if (millis >= line.endMillis()) {
                low = middle + 1;
                continue;
            }
            return middle;
        }
        return -1;
    }

    public LyricLine lineAt(long millis) {
        int index = this.indexAt(millis);
        return index < 0 ? null : this.lines.get(index);
    }

    public LyricLine lineAfter(long millis) {
        for (LyricLine line : this.lines) {
            if (line.startMillis() <= millis) continue;
            return line;
        }
        return null;
    }

    public LyricWord wordAt(long millis) {
        LyricLine line = this.lineAt(millis);
        return line == null ? null : line.wordAt(millis);
    }

    public long millis() {
        long end = 0L;
        for (LyricLine line : this.lines) {
            if (line.endMillis() <= end) continue;
            end = line.endMillis();
        }
        return end;
    }
}

