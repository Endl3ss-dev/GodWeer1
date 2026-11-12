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
import ru.decide.module.impl.render.targeteffects.TargetEffectParticles;
import ru.decide.module.impl.render.targeteffects.TargetEffectRender;
import ru.decide.module.impl.render.targeteffects.TargetEffectStyle;

@Environment(value=EnvType.CLIENT)
public final class DizzyStyle
extends TargetEffectStyle {
    public static final int DEFAULT_FROM = -10179;
    public static final int DEFAULT_TO = -2384;
    private static final int STARS = 5;

    public DizzyStyle() {
        super(96, 0.9f, -0.004f, TargetEffectPalette.gradient(-10179, -2384));
    }

    @Override
    public void drawGround(TargetEffectFrame frame) {
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        class_4588 buf = TargetEffectRender.solid(frame);
        float y = (float)frame.y + frame.groundOffset + 0.02f;
        TargetEffectRender.pixelRing(buf, matrix, frame.x, y, frame.z, frame.radius, 0.25f, 0.125f, 1.0f - frame.appear, frame.groundAlpha, frame.clock * 0.1f, frame.palette);
    }

    @Override
    public void drawTrails(TargetEffectFrame frame) {
        for (int index = 0; index < 5; ++index) {
            float angle = frame.clock * 2.4f + (float)Math.PI * 2 * (float)index / 5.0f;
            double x = frame.x + (double)(class_3532.method_15362((double)angle) * frame.radius * 0.55f);
            double z = frame.z + (double)(class_3532.method_15374((double)angle) * frame.radius * 0.55f);
            double y = frame.y + (double)frame.height() + (double)0.3f + (double)(class_3532.method_15374((double)(frame.clock * 4.0f + (float)index * 1.3f)) * 0.05f);
            float spin = (float)Math.toDegrees(class_3532.method_15374((double)(frame.clock * 3.0f + (float)index)) * 0.3f);
            TargetEffectRender.pixel(frame.matrices, frame, TargetEffectRender.STAR, x, y, z, 0.12f * frame.appear, spin, this.color((float)index / 5.0f + frame.clock * 0.1f), frame.appear);
        }
    }

    @Override
    public void sprinkle(class_1309 target, float radius) {
        if (this.random.nextFloat() >= 0.25f) {
            return;
        }
        TargetEffectParticles.Particle particle = this.particles.spawn(4, 8 + this.random.nextInt(6), 0.06f, this.color(this.random.nextFloat()));
        this.around(particle, target, this.angle(), radius * 0.55f * (0.5f + this.random.nextFloat()), target.method_17682() + 0.3f + (this.random.nextFloat() - 0.5f) * 0.3f);
        this.drift(particle);
    }
}

