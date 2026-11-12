/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 */
package ru.decide.module.impl.render.targeteffects;

import java.util.Arrays;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import ru.decide.module.impl.render.targeteffects.TargetEffectFrame;
import ru.decide.module.impl.render.targeteffects.TargetEffectRender;

@Environment(value=EnvType.CLIENT)
final class TargetEffectParticles {
    static final int DOT = 0;
    static final int SPARKLE = 1;
    static final int STAR = 2;
    static final int HEART = 3;
    static final int CRIT = 4;
    private final Particle[] pool;
    private final Random random = new Random();
    private final float drag;
    private final float buoyancy;
    private int next;
    private int live;

    TargetEffectParticles(int capacity, float drag, float buoyancy) {
        this.pool = new Particle[capacity];
        Arrays.setAll(this.pool, index -> new Particle());
        this.drag = drag;
        this.buoyancy = buoyancy;
    }

    Particle spawn(int shape, int life, float size, int rgb) {
        Particle particle = this.pool[this.next];
        this.next = (this.next + 1) % this.pool.length;
        if (!particle.alive()) {
            ++this.live;
        }
        return particle.reset(shape, life, size, rgb, this.random.nextFloat());
    }

    void tick() {
        if (this.live == 0) {
            return;
        }
        this.live = 0;
        for (Particle particle : this.pool) {
            if (!particle.alive()) continue;
            particle.tick(this.drag, this.buoyancy);
            if (!particle.alive()) continue;
            ++this.live;
        }
    }

    void dodge(class_243 eye, class_243 gaze) {
        if (this.live == 0) {
            return;
        }
        for (Particle particle : this.pool) {
            if (!particle.alive()) continue;
            particle.dodge(eye, gaze);
        }
    }

    boolean hasLive() {
        return this.live > 0;
    }

    void render(TargetEffectFrame frame) {
        if (this.live == 0) {
            return;
        }
        for (Particle particle : this.pool) {
            if (!particle.alive()) continue;
            particle.render(frame);
        }
    }

    void clear() {
        for (Particle particle : this.pool) {
            particle.kill();
        }
        this.live = 0;
        this.next = 0;
    }

    @Environment(value=EnvType.CLIENT)
    static final class Particle {
        private int shape;
        private int life;
        private int age;
        private float size;
        private int rgb;
        private float seed;
        private double x;
        private double y;
        private double z;
        private double lastX;
        private double lastY;
        private double lastZ;
        private double motionX;
        private double motionY;
        private double motionZ;

        Particle() {
        }

        Particle at(double x, double y, double z) {
            this.x = this.lastX = x;
            this.y = this.lastY = y;
            this.z = this.lastZ = z;
            return this;
        }

        Particle move(double x, double y, double z) {
            this.motionX = x;
            this.motionY = y;
            this.motionZ = z;
            return this;
        }

        private Particle reset(int shape, int life, float size, int rgb, float seed) {
            this.shape = shape;
            this.life = life;
            this.size = size;
            this.rgb = rgb;
            this.seed = seed;
            this.age = 0;
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            return this;
        }

        private boolean alive() {
            return this.age < this.life;
        }

        private void kill() {
            this.age = this.life;
        }

        private void tick(float drag, float buoyancy) {
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;
            ++this.age;
            this.x += this.motionX;
            this.y += this.motionY;
            this.z += this.motionZ;
            this.motionX *= (double)drag;
            this.motionY = this.motionY * (double)drag + (double)buoyancy;
            this.motionZ *= (double)drag;
        }

        private void dodge(class_243 eye, class_243 gaze) {
            double dx = this.x - eye.field_1352;
            double dy = this.y - eye.field_1351;
            double dz = this.z - eye.field_1350;
            double along = dx * gaze.field_1352 + dy * gaze.field_1351 + dz * gaze.field_1350;
            if (along < 0.0 || along > 3.0) {
                return;
            }
            double sx = dx - gaze.field_1352 * along;
            double sy = dy - gaze.field_1351 * along;
            double sz = dz - gaze.field_1350 * along;
            double distance = Math.sqrt(sx * sx + sy * sy + sz * sz);
            if (distance >= 0.9) {
                return;
            }
            if (distance < 0.001) {
                sx = -gaze.field_1350;
                sy = 0.0;
                sz = gaze.field_1352;
                distance = Math.sqrt(sx * sx + sz * sz);
            }
            double push = 0.04 * (1.0 - distance / 0.9) * (1.0 - along / 3.0) / distance;
            this.motionX += sx * push;
            this.motionY += sy * push;
            this.motionZ += sz * push;
        }

        private void render(TargetEffectFrame frame) {
            float progress = ((float)this.age + frame.partialTick) / (float)this.life;
            float alpha = class_3532.method_15363((float)(progress * 5.0f), (float)0.0f, (float)1.0f) * (1.0f - progress * progress);
            if (alpha <= 0.002f) {
                return;
            }
            double px = this.lastX + (this.x - this.lastX) * (double)frame.partialTick - frame.camera.field_1352;
            double py = this.lastY + (this.y - this.lastY) * (double)frame.partialTick - frame.camera.field_1351;
            double pz = this.lastZ + (this.z - this.lastZ) * (double)frame.partialTick - frame.camera.field_1350;
            float grow = Math.min(1.0f, 0.4f + (float)this.age * 0.2f);
            float spin = this.seed * 360.0f + (float)this.age * 2.7f;
            int color = TargetEffectRender.argb(this.rgb, alpha);
            switch (this.shape) {
                case 0: {
                    TargetEffectRender.glow(frame.matrices, frame, px, py, pz, this.size * 2.4f, this.rgb, alpha * 0.75f, spin);
                    TargetEffectRender.glow(frame.matrices, frame, px, py, pz, this.size * 0.9f, 0xFFFFFF, alpha * 0.8f, spin);
                    break;
                }
                case 1: {
                    TargetEffectRender.glow(frame.matrices, frame, px, py, pz, this.size * 1.8f, this.rgb, alpha * 0.45f, spin);
                    TargetEffectRender.pixel(frame.matrices, frame, TargetEffectRender.SPARKLE, px, py, pz, this.size * 1.8f * grow, spin, this.rgb, alpha);
                    break;
                }
                case 2: {
                    TargetEffectRender.glow(frame.matrices, frame, px, py, pz, this.size * 1.6f, this.rgb, alpha * 0.35f, spin);
                    TargetEffectRender.pixel(frame.matrices, frame, TargetEffectRender.STAR, px, py, pz, this.size * 2.0f * grow, spin, this.rgb, alpha);
                    break;
                }
                case 3: {
                    TargetEffectRender.glow(frame.matrices, frame, px, py, pz, this.size * 1.6f, this.rgb, alpha * 0.3f, spin);
                    TargetEffectRender.pixel(frame.matrices, frame, TargetEffectRender.HEART, px + Math.sin((float)this.age * 0.2f + this.seed * 6.0f) * (double)0.05f, py, pz, this.size * 2.0f * grow, 0.0f, this.rgb, alpha);
                    break;
                }
                case 4: {
                    TargetEffectRender.glow(frame.matrices, frame, px, py, pz, this.size * 1.4f, this.rgb, alpha * 0.4f, spin);
                    TargetEffectRender.pixel(frame.matrices, frame, TargetEffectRender.CRIT, px, py, pz, this.size * 2.2f * grow, spin, this.rgb, alpha);
                    break;
                }
                default: {
                    TargetEffectRender.glow(frame.matrices, frame, px, py, pz, this.size * 2.2f, color, alpha, spin);
                }
            }
        }
    }
}

