/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597$class_4598
 *  org.joml.Quaternionf
 */
package ru.decide.module.impl.render.targeteffects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import org.joml.Quaternionf;
import ru.decide.module.impl.render.targeteffects.TargetEffectPalette;
import ru.decide.module.impl.render.targeteffects.TargetEffectRender;

@Environment(value=EnvType.CLIENT)
public final class TargetEffectFrame {
    public final class_4587 matrices;
    public final class_4597.class_4598 provider;
    public final class_243 camera;
    public final Quaternionf cameraRotation;
    public final class_1309 target;
    public final double x;
    public final double y;
    public final double z;
    public final float groundOffset;
    public final float radius;
    public final float clock;
    public final float appear;
    public final float groundAlpha;
    public final float partialTick;
    public final TargetEffectPalette palette;

    public TargetEffectFrame(class_4587 matrices, class_4597.class_4598 provider, class_243 camera, Quaternionf cameraRotation, class_1309 target, double x, double y, double z, float groundOffset, float radius, float clock, float appear, float groundAlpha, float partialTick, TargetEffectPalette palette) {
        this.matrices = matrices;
        this.provider = provider;
        this.camera = camera;
        this.cameraRotation = cameraRotation;
        this.target = target;
        this.x = x;
        this.y = y;
        this.z = z;
        this.groundOffset = groundOffset;
        this.radius = radius;
        this.clock = clock;
        this.appear = appear;
        this.groundAlpha = groundAlpha;
        this.partialTick = partialTick;
        this.palette = palette;
    }

    public float height() {
        return this.target == null ? 0.0f : this.target.method_17682();
    }

    public int color(float t, float alpha) {
        return TargetEffectRender.argb(this.palette.at(t), alpha);
    }
}

