/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render.emotions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum LegacyEmotion {
    DEB(100000, "deb", "\u0414\u0435\u0431", 2500L),
    FLOSS(100001, "floss", "\u0424\u043b\u043e\u0441\u0441", 4500L),
    MASTURBATE(100002, "masturbate", "\u041c\u0430\u0441\u0442\u0443\u0440\u0431\u0430\u0446\u0438\u044f", 3500L),
    HELLO(100003, "hello", "\u041f\u0440\u0438\u0432\u0435\u0442\u0441\u0442\u0432\u0438\u0435", 2500L),
    GET_GRIDDY(100004, "get_griddy", "Get Griddy", 5500L),
    HAPPY(100005, "happy", "\u0420\u0430\u0434\u043e\u0441\u0442\u044c", 2500L),
    SIX_SEVEN(100006, "six_seven", "67", 3000L);

    public static final int ID_BASE = 100000;
    private final int id;
    private final String key;
    private final String localized;
    private final long durationMs;

    private LegacyEmotion(int id, String key, String localized, long durationMs) {
        this.id = id;
        this.key = key;
        this.localized = localized;
        this.durationMs = durationMs;
    }

    public int id() {
        return this.id;
    }

    public String key() {
        return this.key;
    }

    public String localized() {
        return this.localized;
    }

    public long durationMs() {
        return this.durationMs;
    }

    public double durationTicks() {
        return (double)this.durationMs / 50.0;
    }

    public static LegacyEmotion byId(int id) {
        for (LegacyEmotion value : LegacyEmotion.values()) {
            if (value.id != id) continue;
            return value;
        }
        return null;
    }
}

