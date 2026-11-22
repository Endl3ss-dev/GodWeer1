/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 */
package ru.decide.module.impl.render.targeteffects;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import ru.decide.module.impl.render.targeteffects.TargetEffectFrame;
import ru.decide.module.impl.render.targeteffects.TargetEffectPalette;
import ru.decide.module.impl.render.targeteffects.TargetEffectParticles;

@Environment(value=EnvType.CLIENT)
public abstract class TargetEffectStyle {
    protected final TargetEffectParticles particles;
    protected final Random random = new Random();
    private final TargetEffectPalette defaultPalette;
    private TargetEffectPalette palette;

    protected TargetEffectStyle(int capacity, float drag, float buoyancy, TargetEffectPalette palette) {
        this.particles = new TargetEffectParticles(capacity, drag, buoyancy);
        this.defaultPalette = palette;
        this.palette = palette;
    }

    public abstract void drawGround(TargetEffectFrame var1);

    public abstract void drawTrails(TargetEffectFrame var1);

    public abstract void sprinkle(class_1309 var1, float var2);

    public void setPalette(TargetEffectPalette palette) {
        this.palette = palette;
    }

    public TargetEffectPalette defaultPalette() {
        return this.defaultPalette;
    }

    protected int color(float t) {
        return this.palette.at(t);
    }

    public void dodge(class_243 eye, class_243 gaze) {
        this.particles.dodge(eye, gaze);
    }

    public void tick() {
        this.particles.tick();
    }

    public void render(TargetEffectFrame frame, boolean ground, boolean trails) {
        if (ground && frame.groundAlpha > 0.001f) {
            this.drawGround(frame);
        }
        if (trails && frame.appear > 0.001f) {
            this.drawTrails(frame);
        }
        this.particles.render(frame);
    }

    public void renderParticles(TargetEffectFrame frame) {
        this.particles.render(frame);
    }

    public boolean hasParticles() {
        return this.particles.hasLive();
    }

    public void clear() {
        this.particles.clear();
    }

    protected float angle() {
        return this.random.nextFloat() * ((float)Math.PI * 2);
    }

    protected TargetEffectParticles.Particle around(TargetEffectParticles.Particle particle, class_1309 target, float angle, float radius, float yOffset) {
        return particle.at(target.method_23317() + Math.cos(angle) * (double)radius, target.method_23318() + (double)yOffset, target.method_23321() + Math.sin(angle) * (double)radius);
    }

    protected void drift(TargetEffectParticles.Particle particle) {
        particle.move(this.random.nextGaussian() * 0.003, (double)this.random.nextFloat() * 0.01, this.random.nextGaussian() * 0.003);
    }
}

