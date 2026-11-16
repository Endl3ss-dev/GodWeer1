/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_2378
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_7923
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.cosmetics.melee;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_7923;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class MeleeSounds {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:Melee");
    private static final String SOUNDS_RESOURCE = "/assets/decide/sounds.json";
    private static final String SOUND_PREFIX = "melee.";
    private static final String NAMESPACE = "decide";
    private static final float VOLUME = 1.0f;
    private static final float PITCH = 1.0f;
    private static final Map<String, class_3414> EVENTS = new HashMap<String, class_3414>();

    private MeleeSounds() {
    }

    public static void initialize() {
        if (!EVENTS.isEmpty()) {
            return;
        }
        EVENTS.putAll(MeleeSounds.register(SOUND_PREFIX));
        LOGGER.info("\u0417\u0430\u0440\u0435\u0433\u0438\u0441\u0442\u0440\u0438\u0440\u043e\u0432\u0430\u043d\u043e \u0437\u0432\u0443\u043a\u043e\u0432 \u043d\u043e\u0436\u0435\u0439: {}", (Object)EVENTS.size());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Map<String, class_3414> register(String prefix) {
        HashMap<String, class_3414> events = new HashMap<String, class_3414>();
        try (InputStream in = MeleeSounds.class.getResourceAsStream(SOUNDS_RESOURCE);){
            if (in == null) {
                HashMap<String, class_3414> hashMap = events;
                return hashMap;
            }
            JsonObject root = JsonParser.parseString((String)new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            Iterator iterator = root.keySet().iterator();
            while (iterator.hasNext()) {
                String name = (String)iterator.next();
                if (!name.startsWith(prefix)) continue;
                class_2960 id = class_2960.method_60655((String)NAMESPACE, (String)name);
                events.put(name, (class_3414)class_2378.method_10230((class_2378)class_7923.field_41172, (class_2960)id, (Object)class_3414.method_47908((class_2960)id)));
            }
            return events;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return events;
    }

    public static class_1113 play(String name) {
        class_3414 event = EVENTS.get(name);
        if (event == null) {
            return null;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null) {
            return null;
        }
        class_1109 instance = class_1109.method_4757((class_3414)event, (float)1.0f, (float)1.0f);
        client.method_1483().method_4873((class_1113)instance);
        return instance;
    }

    public static void stop(class_1113 instance) {
        if (instance != null) {
            class_310.method_1551().method_1483().method_4870(instance);
        }
    }

    public static boolean isActive(class_1113 instance) {
        return instance != null && class_310.method_1551().method_1483().method_4877(instance);
    }
}

