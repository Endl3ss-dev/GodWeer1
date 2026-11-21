/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1309
 *  net.minecraft.class_1538
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_310
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 */
package ru.decide.utils.render.killeffect;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1538;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_3417;
import net.minecraft.class_3419;

@Environment(value=EnvType.CLIENT)
public final class LightningFlash {
    private static final LightningFlash INSTANCE = new LightningFlash();
    private static final long COLOR_LIFETIME_MS = 5000L;
    private static final Map<UUID, ColorFlash> colorFlashes = new ConcurrentHashMap<UUID, ColorFlash>();

    private LightningFlash() {
    }

    public static LightningFlash get() {
        return INSTANCE;
    }

    public void spawn(class_1309 entity, int color) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || entity == null) {
            return;
        }
        class_1538 bolt = new class_1538(class_1299.field_6112, (class_1937)mc.field_1687);
        bolt.method_23327(entity.method_23317(), entity.method_23318(), entity.method_23321());
        LightningFlash.register(bolt.method_5667(), color);
        mc.field_1687.method_53875((class_1297)bolt);
        mc.field_1687.method_8396((class_1297)mc.field_1724, class_2338.method_49637((double)entity.method_23317(), (double)entity.method_23318(), (double)entity.method_23321()), class_3417.field_14865, class_3419.field_15252, 150.0f, ThreadLocalRandom.current().nextFloat());
    }

    public static Integer colorOf(UUID boltId) {
        long now = System.currentTimeMillis();
        ColorFlash flash = colorFlashes.get(boltId);
        if (flash == null) {
            return null;
        }
        if (flash.expiresAt < now) {
            colorFlashes.remove(boltId);
            return null;
        }
        return flash.color;
    }

    public static void register(UUID boltId, int color) {
        long now = System.currentTimeMillis();
        colorFlashes.entrySet().removeIf(e -> ((ColorFlash)e.getValue()).expiresAt < now);
        colorFlashes.put(boltId, new ColorFlash(color, now + 5000L));
    }

    public void clear() {
        colorFlashes.clear();
    }

    @Environment(value=EnvType.CLIENT)
    private record ColorFlash(int color, long expiresAt) {
    }
}

