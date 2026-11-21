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
public final class CircleStyle
extends TargetEffectStyle {
    public static final int DEFAULT_FROM = -16718337;
    public static final int DEFAULT_TO = -45096;
    private static final int SEGMENTS = 64;
    private float level;
    private float rise;

    public CircleStyle() {
        super(96, 0.93f, -0.0015f, TargetEffectPalette.gradient(-16718337, -45096));
    }

    @Override
    public void drawGround(TargetEffectFrame frame) {
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        class_4588 buf = TargetEffectRender.solid(frame);
        int color = this.color(frame.clock * 0.1f);
        float y = (float)frame.y + frame.groundOffset + 0.02f;
        TargetEffectRender.disc(buf, matrix, frame.x, y, frame.z, frame.radius + 0.3f, TargetEffectRender.argb(color, 0.22f * frame.groundAlpha), TargetEffectRender.argb(color, 0.0f), 48);
    }

    @Override
    public void drawTrails(TargetEffectFrame frame) {
        float angle;
        int index;
        this.level = frame.height() * (0.5f - 0.5f * class_3532.method_15362((double)(frame.clock * 1.7f)));
        this.rise = class_3532.method_15374((double)(frame.clock * 1.7f));
        float curtainHeight = -this.rise * 0.55f;
        class_4588 buf = TargetEffectRender.solid(frame);
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        float previousX = 0.0f;
        float previousZ = 0.0f;
        int previousColor = 0;
        boolean hasPrevious = false;
        for (index = 0; index <= 64; ++index) {
            angle = (float)Math.PI * 2 * (float)index / 64.0f;
            float x = (float)frame.x + class_3532.method_15362((double)angle) * frame.radius;
            float z = (float)frame.z + class_3532.method_15374((double)angle) * frame.radius;
            int color = this.color((float)index / 64.0f + frame.clock * 0.12f);
            if (hasPrevious) {
                TargetEffectRender.curtain(buf, matrix, previousX, previousZ, x, z, frame.y + (double)this.level, curtainHeight, curtainHeight, previousColor, color, 0.55f * frame.appear);
            }
            previousX = x;
            previousZ = z;
            previousColor = color;
            hasPrevious = true;
        }
        for (index = 0; index <= 64; ++index) {
            angle = (float)Math.PI * 2 * (float)index / 64.0f;
            double x = frame.x + (double)(class_3532.method_15362((double)angle) * frame.radius);
            double z = frame.z + (double)(class_3532.method_15374((double)angle) * frame.radius);
            int color = this.color((float)index / 64.0f + frame.clock * 0.12f);
            float spin = (float)index * 5.6f + frame.clock * 30.0f;
            TargetEffectRender.glow(frame.matrices, frame, x, frame.y + (double)this.level, z, 0.07f, color, 0.5f * frame.appear, spin);
            TargetEffectRender.glow(frame.matrices, frame, x, frame.y + (double)this.level, z, 0.025f, 0xFFFFFF, 0.55f * frame.appear, spin);
        }
    }

    @Override
    public void sprinkle(class_1309 target, float radius) {
        if (this.random.nextFloat() >= 0.5f) {
            return;
        }
        float angle = this.angle();
        TargetEffectParticles.Particle particle = this.particles.spawn(1, 18 + this.random.nextInt(12), 0.06f + 0.05f * this.random.nextFloat(), this.color(angle / ((float)Math.PI * 2)));
        particle.at(target.method_23317() + Math.cos(angle) * (double)radius, target.method_23318() + (double)this.level, target.method_23321() + Math.sin(angle) * (double)radius).move(Math.cos(angle) * 0.01, (double)(-this.rise) * 0.02, Math.sin(angle) * 0.01);
    }
}

