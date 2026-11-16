/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_2960
 */
package ru.decide.module.impl.render.emotions;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_2960;
import ru.decide.cosmetics.maksai.render.Gfx;

@Environment(value=EnvType.CLIENT)
public final class EmotePreviews {
    private static final String PATH = "/assets/decide/emotes/previews/";
    private static final int MAX_CACHE = 64;
    private static final int MAX_LOADS_PER_FRAME = 4;
    private static final LinkedHashMap<Integer, class_2960> cache = new LinkedHashMap(16, 0.75f, true);
    private static final Set<Integer> failed = new HashSet<Integer>();
    private static int loads;

    private EmotePreviews() {
    }

    public static void beginFrame() {
        loads = 0;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static class_2960 preview(int id) {
        class_2960 old = cache.get(id);
        if (old != null) {
            return old;
        }
        if (failed.contains(id)) return null;
        if (loads >= 4) {
            return null;
        }
        ++loads;
        try (InputStream stream = EmotePreviews.class.getResourceAsStream(PATH + id + ".png");){
            if (stream == null) {
                failed.add(id);
                class_2960 class_29602 = null;
                return class_29602;
            }
            class_1011 image = class_1011.method_4309((InputStream)stream);
            if (image.method_4307() != 96 || image.method_4323() != 120) {
                image.close();
                failed.add(id);
                class_2960 class_29603 = null;
                return class_29603;
            }
            class_2960 key = class_2960.method_60655((String)"decide", (String)("dynamic/emote_preview_" + id));
            Gfx.register(key, image);
            cache.put(id, key);
            while (cache.size() > 64) {
                Iterator<Map.Entry<Integer, class_2960>> it = cache.entrySet().iterator();
                Map.Entry<Integer, class_2960> first = it.next();
                it.remove();
                Gfx.release(first.getValue());
            }
            class_2960 class_29604 = key;
            return class_29604;
        }
        catch (Exception e) {
            failed.add(id);
            return null;
        }
    }

    public static void clear() {
        for (class_2960 key : cache.values()) {
            Gfx.release(key);
        }
        cache.clear();
        failed.clear();
    }
}

