/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1309
 *  net.minecraft.class_3532
 *  net.minecraft.class_4588
 *  org.joml.Matrix4f
 */
package ru.decide.module.impl.render.targeteffects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;
import net.minecraft.class_3532;
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import ru.decide.module.impl.render.targeteffects.TargetEffectFrame;
import ru.decide.module.impl.render.targeteffects.TargetEffectPalette;
import ru.decide.module.impl.render.targeteffects.TargetEffectRender;
import ru.decide.module.impl.render.targeteffects.TargetEffectStyle;

@Environment(value=EnvType.CLIENT)
public final class MarkerStyle
extends TargetEffectStyle {
    public static final int DEFAULT_FROM = -50373;
    public static final int DEFAULT_TO = -20422;

    public MarkerStyle() {
        super(64, 0.9f, -0.01f, TargetEffectPalette.gradient(-50373, -20422));
    }

    @Override
    public void drawGround(TargetEffectFrame frame) {
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        class_4588 buf = TargetEffectRender.solid(frame);
        float y = (float)frame.y + frame.groundOffset + 0.02f;
        float pulse = frame.clock * 0.7f;
        pulse -= (float)((int)pulse);
        TargetEffectRender.pixelRing(buf, matrix, frame.x, y, frame.z, frame.radius, 0.25f, 0.125f, 1.0f - frame.appear, frame.groundAlpha, frame.clock * 0.15f, frame.palette);
        TargetEffectRender.pixelRing(buf, matrix, frame.x, y, frame.z, frame.radius * (0.25f + 0.75f * pulse), 0.1875f, 0.125f, Math.max(1.0f - frame.appear, pulse), frame.groundAlpha, frame.clock * 0.15f + 0.5f, frame.palette);
    }

    @Override
    public void drawTrails(TargetEffectFrame frame) {
        float size = 0.22f * frame.appear;
        float angle = frame.clock * 2.0f;
        float alongX = class_3532.method_15362((double)angle);
        float alongZ = class_3532.method_15374((double)angle);
        double y = frame.y + (double)frame.height() + (double)0.3f + (double)size + (double)(Math.abs(class_3532.method_15374((double)(frame.clock * 3.0f))) * 0.18f);
        int color = this.color(frame.clock * 0.1f);
        TargetEffectRender.worldPixel(frame, TargetEffectRender.ARROW, frame.x, y, frame.z, alongX, alongZ, size, color, frame.appear);
        TargetEffectRender.glow(frame.matrices, frame, frame.x, y, frame.z, size * 1.6f, color, frame.appear * 0.35f, frame.clock * 30.0f);
    }

    @Override
    public void sprinkle(class_1309 target, float radius) {
    }
}

