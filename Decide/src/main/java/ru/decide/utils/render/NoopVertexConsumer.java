/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4588
 */
package ru.decide.utils.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4588;

@Environment(value=EnvType.CLIENT)
public final class NoopVertexConsumer
implements class_4588 {
    public static final NoopVertexConsumer INSTANCE = new NoopVertexConsumer();

    private NoopVertexConsumer() {
    }

    public class_4588 method_22912(float x, float y, float z) {
        return this;
    }

    public class_4588 method_1336(int r, int g, int b, int a) {
        return this;
    }

    public class_4588 method_39415(int argb) {
        return this;
    }

    public class_4588 method_22913(float u, float v) {
        return this;
    }

    public class_4588 method_60796(int u, int v) {
        return this;
    }

    public class_4588 method_22921(int u, int v) {
        return this;
    }

    public class_4588 method_22914(float x, float y, float z) {
        return this;
    }

    public class_4588 method_75298(float width) {
        return this;
    }
}

