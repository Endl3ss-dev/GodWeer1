/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_10799
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 */
package ru.decide.cosmetics.maksai.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_10799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;

@Environment(value=EnvType.CLIENT)
public final class Gfx {
    private Gfx() {
    }

    public static class_1043 register(class_2960 id, class_1011 image) {
        class_1043 tex = new class_1043(() -> ((class_2960)id).toString(), image);
        class_310.method_1551().method_1531().method_4616(id, (class_1044)tex);
        return tex;
    }

    public static void release(class_2960 id) {
        class_310.method_1551().method_1531().method_4615(id);
    }

    public static void image(class_332 g, class_2960 id, int x, int y, int w, int h, float u, float v, int rw, int rh, int tw, int th) {
        g.method_25302(class_10799.field_56883, id, x, y, u, v, w, h, rw, rh, tw, th);
    }
}

