/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 */
package ru.decide.module.impl.render.hiteffects;

import java.util.Arrays;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import ru.decide.module.impl.render.hiteffects.HitEffectRender;

@Environment(value=EnvType.CLIENT)
final class HitEffectParticles {
    static final int DOT = 0;
    static final int SPARK = 1;
    static final int CRIT = 2;
    static final int HEART = 3;
    static final int STAR = 4;
    private final Particle[] pool;
    private final Random random = new Random();
    private final float drag;
    private final float buoyancy;
    private int next;
    private int live;

    HitEffectParticles(int capacity, float drag, float buoyancy) {
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

    boolean hasLive() {
        return this.live > 0;
    }

    void render(HitEffectRender.Frame frame) {
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

        private void render(HitEffectRender.Frame frame) {
            float elapsed = (float)this.age + frame.partialTick;
            float progress = elapsed / (float)this.life;
            float alpha = class_3532.method_15363((float)(progress * 5.0f), (float)0.0f, (float)1.0f) * (1.0f - progress * progress);
            if (alpha <= 0.002f && (this.shape == 0 || this.shape == 1)) {
                return;
            }
            double px = this.lastX + (this.x - this.lastX) * (double)frame.partialTick - frame.camera.field_1352;
            double py = this.lastY + (this.y - this.lastY) * (double)frame.partialTick - frame.camera.field_1351;
            double pz = this.lastZ + (this.z - this.lastZ) * (double)frame.partialTick - frame.camera.field_1350;
            float pop = Math.min(1.0f, 0.4f + elapsed * 0.2f);
            float fade = class_3532.method_15363((float)(((float)this.life - elapsed) / 5.0f), (float)0.0f, (float)1.0f);
            float phase = this.seed * ((float)Math.PI * 2);
            float spin = this.seed * 360.0f + (float)this.age * 2.7f;
            switch (this.shape) {
                case 0: {
                    HitEffectRender.glow(frame, px, py, pz, this.size, this.rgb, alpha * 0.8f, spin);
                    HitEffectRender.glow(frame, px, py, pz, this.size * 0.35f, 0xFFFFFF, alpha * 0.64f, spin);
                    break;
                }
                case 1: {
                    HitEffectRender.trail(frame, px, py, pz, this.motionX, this.motionY, this.motionZ, this.size, this.rgb, alpha);
                    break;
                }
                case 2: {
                    HitEffectRender.pixel(frame, HitEffectRender.CRIT, px, py, pz, this.size, 0.0f, this.rgb, 1.0f);
                    break;
                }
                case 3: {
                    HitEffectRender.pixel(frame, HitEffectRender.HEART, px + (double)(class_3532.method_15374((double)(elapsed * 0.2f + phase)) * 0.05f), py, pz, this.size * pop * fade, 0.0f, this.rgb, 1.0f);
                    break;
                }
                case 4: {
                    float starSpin = (float)Math.toDegrees(class_3532.method_15374((double)(elapsed * 0.3f + phase)) * 0.35f);
                    HitEffectRender.pixel(frame, HitEffectRender.STAR, px, py, pz, this.size * pop * fade, starSpin, this.rgb, 1.0f);
                    break;
                }
                default: {
                    HitEffectRender.glow(frame, px, py, pz, this.size * 2.2f, this.rgb, alpha, spin);
                }
            }
        }
    }
}

