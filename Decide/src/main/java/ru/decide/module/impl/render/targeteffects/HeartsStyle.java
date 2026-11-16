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
public final class HeartsStyle
extends TargetEffectStyle {
    public static final int DEFAULT_FROM = -50340;
    public static final int DEFAULT_TO = -28749;
    private static final int HEARTS = 6;

    public HeartsStyle() {
        super(128, 0.92f, 0.002f, TargetEffectPalette.gradient(-50340, -28749));
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
        for (int index = 0; index < 6; ++index) {
            float angle = frame.clock * 1.3f + (float)Math.PI * 2 * (float)index / 6.0f;
            float alongAngle = frame.clock * 2.5f + (float)index;
            double x = frame.x + (double)(class_3532.method_15362((double)angle) * frame.radius);
            double z = frame.z + (double)(class_3532.method_15374((double)angle) * frame.radius);
            double y = frame.y + (double)(frame.height() * (0.55f + 0.3f * class_3532.method_15374((double)(frame.clock * 1.1f + (float)index * 2.1f))));
            int color = this.color((float)index / 6.0f + frame.clock * 0.08f);
            float size = 0.11f * frame.appear;
            TargetEffectRender.worldPixel(frame, TargetEffectRender.HEART, x, y, z, class_3532.method_15362((double)alongAngle), class_3532.method_15374((double)alongAngle), size, color, frame.appear);
            TargetEffectRender.glow(frame.matrices, frame, x, y, z, size * 1.8f, color, frame.appear * 0.25f, frame.clock * 20.0f);
        }
    }

    @Override
    public void sprinkle(class_1309 target, float radius) {
        if (this.random.nextFloat() >= 0.2f) {
            return;
        }
        TargetEffectParticles.Particle particle = this.particles.spawn(3, 22 + this.random.nextInt(14), 0.07f + 0.04f * this.random.nextFloat(), this.color(this.random.nextFloat()));
        this.around(particle, target, this.angle(), radius * (0.5f + 0.6f * this.random.nextFloat()), target.method_17682() * this.random.nextFloat()).move(this.random.nextGaussian() * 0.005, 0.02f + this.random.nextFloat() * 0.02f, this.random.nextGaussian() * 0.005);
    }
}

