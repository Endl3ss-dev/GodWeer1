/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1111
 *  net.minecraft.class_1113
 *  net.minecraft.class_1113$class_1114
 *  net.minecraft.class_1144
 *  net.minecraft.class_1146
 *  net.minecraft.class_2960
 *  net.minecraft.class_3419
 */
package ru.decide.module.impl.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1111;
import net.minecraft.class_1113;
import net.minecraft.class_1144;
import net.minecraft.class_1146;
import net.minecraft.class_2960;
import net.minecraft.class_3419;

@Environment(value=EnvType.CLIENT)
public final class ScaledSoundInstance
implements class_1113 {
    private final class_1113 origin;
    private final float scale;

    private ScaledSoundInstance(class_1113 origin, float scale) {
        this.origin = origin;
        this.scale = scale;
    }

    public static class_1113 of(class_1113 original, float scale) {
        if (original == null || scale == 1.0f) {
            return original;
        }
        return new ScaledSoundInstance(original, scale);
    }

    public class_1113 origin() {
        return this.origin;
    }

    public class_2960 method_4775() {
        return this.origin.method_4775();
    }

    public class_1146 method_4783(class_1144 manager) {
        return this.origin.method_4783(manager);
    }

    public class_1111 method_4776() {
        return this.origin.method_4776();
    }

    public class_3419 method_4774() {
        return this.origin.method_4774();
    }

    public boolean method_4786() {
        return this.origin.method_4786();
    }

    public boolean method_4787() {
        return this.origin.method_4787();
    }

    public int method_4780() {
        return this.origin.method_4780();
    }

    public float method_4781() {
        return this.origin.method_4781() * this.scale;
    }

    public float method_4782() {
        return this.origin.method_4782();
    }

    public double method_4784() {
        return this.origin.method_4784();
    }

    public double method_4779() {
        return this.origin.method_4779();
    }

    public double method_4778() {
        return this.origin.method_4778();
    }

    public class_1113.class_1114 method_4777() {
        return this.origin.method_4777();
    }

    public boolean method_4785() {
        return this.origin.method_4785();
    }

    public String toString() {
        return "Scaled(" + String.valueOf(this.origin) + ")";
    }
}

