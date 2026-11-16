/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_2561
 *  net.minecraft.class_327
 *  net.minecraft.class_5481
 */
package ru.decide.optimization.velotune.perf;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_5481;
import ru.decide.optimization.velotune.perf.TextRefreshThrottle;

@Environment(value=EnvType.CLIENT)
public final class NameTagTextBuffer {
    private static final int MAX_PLAYERS = 4096;
    private static final int MAX_WIDTHS = 4096;
    private static final Map<Integer, TextEntry> PLAYER_TEXT = new HashMap<Integer, TextEntry>();
    private static final Map<WidthKey, WidthEntry> WIDTHS = new LinkedHashMap<WidthKey, WidthEntry>(256, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<WidthKey, WidthEntry> eldest) {
            return this.size() > 4096;
        }
    };

    private NameTagTextBuffer() {
    }

    public static void apply(class_10055 state, int updatesPerSecond) {
        if (state.field_53528 == 0) {
            return;
        }
        if (PLAYER_TEXT.size() > 4096) {
            PLAYER_TEXT.clear();
        }
        long now = System.nanoTime();
        TextEntry entry = PLAYER_TEXT.computeIfAbsent(state.field_53528, ignored -> new TextEntry());
        if (TextRefreshThrottle.shouldRefresh(entry.initialized, now - entry.capturedNanos, updatesPerSecond)) {
            entry.displayName = state.field_53337;
            entry.playerName = state.field_53525;
            entry.capturedNanos = now;
            entry.initialized = true;
        } else {
            state.field_53337 = entry.displayName;
            state.field_53525 = entry.playerName;
        }
    }

    public static int width(class_327 renderer, class_5481 text, int updatesPerSecond) {
        long now = System.nanoTime();
        WidthKey key = new WidthKey(renderer, text);
        WidthEntry entry = WIDTHS.get(key);
        if (entry == null || TextRefreshThrottle.shouldRefresh(true, now - entry.capturedNanos, updatesPerSecond)) {
            int width = renderer.method_30880(text);
            WIDTHS.put(key, new WidthEntry(width, now));
            return width;
        }
        return entry.width;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class TextEntry {
        private class_2561 displayName;
        private class_2561 playerName;
        private long capturedNanos;
        private boolean initialized;

        private TextEntry() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record WidthKey(class_327 renderer, class_5481 text) {
    }

    @Environment(value=EnvType.CLIENT)
    private record WidthEntry(int width, long capturedNanos) {
    }
}

