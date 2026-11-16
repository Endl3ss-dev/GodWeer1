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
public final class SoulsStyle
extends TargetEffectStyle {
    public static final int DEFAULT_FROM = -14039553;
    public static final int DEFAULT_TO = -5222913;
    private static final int STRANDS = 5;
    private static final int STEPS = 22;

    public SoulsStyle() {
        super(128, 0.94f, 0.0012f, TargetEffectPalette.gradient(-14039553, -5222913));
    }

    @Override
    public void drawGround(TargetEffectFrame frame) {
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        class_4588 buf = TargetEffectRender.solid(frame);
        int color = this.color(frame.clock * 0.1f);
        float y = (float)frame.y + frame.groundOffset + 0.02f;
        TargetEffectRender.disc(buf, matrix, frame.x, y, frame.z, frame.radius + 0.35f, TargetEffectRender.argb(color, 0.3f * frame.groundAlpha), TargetEffectRender.argb(color, 0.0f), 48);
        TargetEffectRender.ring(buf, matrix, frame.x, y + 0.01f, frame.z, frame.radius + 0.15f, frame.radius + 0.21f, TargetEffectRender.argb(this.color(frame.clock * 0.1f + 0.5f), 0.45f * frame.groundAlpha), 64);
    }

    @Override
    public void drawTrails(TargetEffectFrame frame) {
        float height = frame.height();
        for (int strand = 0; strand < 5; ++strand) {
            for (int step = 0; step < 22; ++step) {
                float progress = (float)step / 21.0f;
                float angle = frame.clock * 1.5f + (float)strand * ((float)Math.PI * 2) / 5.0f + progress * ((float)Math.PI * 2) * 1.3f;
                float orbit = frame.radius * (0.95f - 0.6f * progress);
                double x = frame.x + Math.cos(angle) * (double)orbit;
                double z = frame.z + Math.sin(angle) * (double)orbit;
                double y = frame.y + (double)0.12f + (double)(progress * height * 0.9f) + Math.sin(frame.clock * 2.3f + (float)strand * 1.4f + progress * 5.0f) * (double)0.05f;
                float fade = frame.appear * (0.25f + 0.75f * (1.0f - progress));
                int color = this.color(progress * 0.8f + frame.clock * 0.08f + (float)strand * 0.12f);
                float size = 0.085f * (0.65f + 0.35f * class_3532.method_15374((double)(frame.clock * 3.0f + (float)step * 0.7f + (float)strand)));
                TargetEffectRender.glow(frame.matrices, frame, x, y, z, size, color, fade * 0.55f, frame.clock * 40.0f + (float)step * 11.0f + (float)strand * 31.0f);
                if (step % 3 != 0) continue;
                TargetEffectRender.glow(frame.matrices, frame, x, y, z, size * 0.4f, 0xFFFFFF, fade * 0.6f, 0.0f);
            }
        }
    }

    @Override
    public void sprinkle(class_1309 target, float radius) {
        if (this.random.nextFloat() >= 0.45f) {
            return;
        }
        TargetEffectParticles.Particle particle = this.particles.spawn(0, 35 + this.random.nextInt(25), 0.04f + 0.04f * this.random.nextFloat(), this.color(this.random.nextFloat()));
        this.around(particle, target, this.angle(), radius * (0.5f + 0.8f * this.random.nextFloat()), target.method_17682() * this.random.nextFloat());
        this.drift(particle);
    }
}

