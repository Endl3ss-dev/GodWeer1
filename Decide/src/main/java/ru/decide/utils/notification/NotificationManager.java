/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_2960
 */
package ru.decide.utils.notification;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import ru.decide.utils.animation.Animation;

@Environment(value=EnvType.CLIENT)
public class NotificationManager {
    private static final List<Entry> entries = new ArrayList<Entry>();

    public static void send(String text) {
        NotificationManager.add(text, Type.INFO, null, null, null, 3000L);
    }

    public static void send(String text, Type type) {
        NotificationManager.add(text, type, null, null, null, 3000L);
    }

    public static void send(String text, Type type, long ms) {
        NotificationManager.add(text, type, null, null, null, ms);
    }

    public static void send(String text, Type type, String glyph) {
        NotificationManager.add(text, type, glyph, null, null, 3000L);
    }

    public static void send(String text, Type type, String glyph, long ms) {
        NotificationManager.add(text, type, glyph, null, null, ms);
    }

    public static void send(String text, Type type, class_2960 texture) {
        NotificationManager.add(text, type, null, texture, null, 3000L);
    }

    public static void send(String text, Type type, class_2960 texture, long ms) {
        NotificationManager.add(text, type, null, texture, null, ms);
    }

    public static void send(String text, Type type, class_1799 item) {
        NotificationManager.add(text, type, null, null, item, 3000L);
    }

    public static void send(String text, Type type, class_1799 item, long ms) {
        NotificationManager.add(text, type, null, null, item, ms);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void add(String text, Type type, String glyph, class_2960 texture, class_1799 item, long ms) {
        List<Entry> list = entries;
        synchronized (list) {
            entries.add(new Entry(text, type, glyph, texture, item, ms));
        }
    }

    public static List<Entry> entries() {
        return entries;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        MODULE,
        EFFECT,
        INFO,
        WARNING;

    }

    @Environment(value=EnvType.CLIENT)
    public static class Entry {
        public final String text;
        public final Type type;
        public final Animation anim = new Animation();
        public boolean removing = false;
        private final long created = System.currentTimeMillis();
        private final long duration;
        public final String iconGlyph;
        public final class_2960 effectTexture;
        public final class_1799 itemStack;

        Entry(String text, Type type, String iconGlyph, class_2960 effectTexture, class_1799 itemStack, long duration) {
            this.text = text;
            this.type = type;
            this.iconGlyph = iconGlyph;
            this.effectTexture = effectTexture;
            this.itemStack = itemStack;
            this.duration = duration;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - this.created >= this.duration;
        }
    }
}

