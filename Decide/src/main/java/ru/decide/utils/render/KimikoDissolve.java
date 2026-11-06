/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_1309
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_9799
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package ru.decide.utils.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_9799;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.decide.utils.render.ModelBoxCapture;

@Environment(value=EnvType.CLIENT)
public final class KimikoDissolve {
    private static final int MAX_PARTICLES = 4000;
    private static final int MAX_LIGHT = 0xF000F0;
    private static final class_2960 GLOW_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/particles/glow.png");
    private static final KimikoDissolve INSTANCE = new KimikoDissolve();
    private final class_9799 allocator = new class_9799(65536);
    private class_4597.class_4598 provider;
    private final List<ModelParticle> particles = new ArrayList<ModelParticle>();
    private final List<class_1309> pending = new ArrayList<class_1309>();
    private final Random random = new Random();
    private static class_1921 cachedLayer;

    private KimikoDissolve() {
    }

    public static KimikoDissolve get() {
        return INSTANCE;
    }

    public synchronized void queue(class_1309 entity, float partialTick, Settings settings) {
        if (entity == null || settings == null) {
            return;
        }
        this.pending.add(entity);
    }

    private synchronized void spawnPending(float partialTick, Settings settings) {
        if (this.pending.isEmpty()) {
            return;
        }
        ArrayList<class_1309> due = new ArrayList<class_1309>(this.pending);
        this.pending.clear();
        for (class_1309 entity : due) {
            this.spawn(entity, partialTick, settings);
        }
    }

    private synchronized void spawn(class_1309 entity, float partialTick, Settings settings) {
        List<ModelBoxCapture.Box> boxes;
        try {
            boxes = ModelBoxCapture.capture(entity, partialTick, false);
        }
        catch (Throwable ignored) {
            return;
        }
        if (boxes.isEmpty()) {
            return;
        }
        class_243 origin = new class_243(class_3532.method_16436((double)partialTick, (double)entity.field_6038, (double)entity.method_23317()), class_3532.method_16436((double)partialTick, (double)entity.field_5971, (double)entity.method_23318()), class_3532.method_16436((double)partialTick, (double)entity.field_5989, (double)entity.method_23321()));
        ArrayList<float[]> volumes = new ArrayList<float[]>(boxes.size());
        for (ModelBoxCapture.Box box : boxes) {
            float[] half = box.half();
            volumes.add(new float[]{half[0] * half[1] * half[2]});
        }
        float chaos = Math.max(0.0f, settings.chaos());
        int budget = 4000 - this.particles.size();
        if (budget <= 0) {
            return;
        }
        int count = Math.min(Math.max(1, settings.count()), budget);
        for (int i = 0; i < count; ++i) {
            class_243 point = this.sample(boxes, volumes, origin);
            class_243 away = point.method_1020(this.center(boxes, origin));
            if (away.method_1027() < 1.0E-5) {
                away = new class_243(this.random.nextDouble() - 0.5, this.random.nextDouble() * 0.4, this.random.nextDouble() - 0.5);
            }
            double driftScale = (0.32 + this.random.nextDouble() * 0.58) * (double)chaos;
            class_243 drift = away.method_1029().method_1021(driftScale).method_1031(this.random.nextDouble() - 0.5, this.random.nextDouble() * 0.35, this.random.nextDouble() - 0.5).method_1021(0.12);
            this.particles.add(new ModelParticle(point, drift, System.currentTimeMillis(), this.random.nextInt(0xFFFFFF), this.random.nextFloat() * 360.0f, (this.random.nextFloat() - 0.5f) * 90.0f, this.random.nextFloat() * 6.2832f, 0.7f + this.random.nextFloat() * 0.6f));
        }
    }

    private class_243 sample(List<ModelBoxCapture.Box> boxes, List<float[]> volumes, class_243 origin) {
        float total = 0.0f;
        for (float[] v : volumes) {
            total += v[0];
        }
        if (total <= 0.0f) {
            return origin;
        }
        float pick = this.random.nextFloat() * total;
        ModelBoxCapture.Box chosen = boxes.get(0);
        for (int i = 0; i < volumes.size(); ++i) {
            if (!((pick -= volumes.get(i)[0]) <= 0.0f)) continue;
            chosen = boxes.get(i);
            break;
        }
        float[] half = chosen.half();
        float[] axes = chosen.axes();
        float a = this.random.nextFloat() * 2.0f - 1.0f;
        float b = this.random.nextFloat() * 2.0f - 1.0f;
        float c = this.random.nextFloat() * 2.0f - 1.0f;
        double px = chosen.cx() + axes[0] * a * half[0] + axes[3] * b * half[1] + axes[6] * c * half[2];
        double py = chosen.cy() + axes[1] * a * half[0] + axes[4] * b * half[1] + axes[7] * c * half[2];
        double pz = chosen.cz() + axes[2] * a * half[0] + axes[5] * b * half[1] + axes[8] * c * half[2];
        return new class_243(origin.field_1352 + px, origin.field_1351 + py, origin.field_1350 + pz);
    }

    private class_243 center(List<ModelBoxCapture.Box> boxes, class_243 origin) {
        double sx = 0.0;
        double sy = 0.0;
        double sz = 0.0;
        for (ModelBoxCapture.Box box : boxes) {
            sx += (double)box.cx();
            sy += (double)box.cy();
            sz += (double)box.cz();
        }
        int n = Math.max(1, boxes.size());
        return new class_243(origin.field_1352 + sx / (double)n, origin.field_1351 + sy / (double)n, origin.field_1350 + sz / (double)n);
    }

    public synchronized boolean isIdle() {
        return this.particles.isEmpty();
    }

    public synchronized void render(class_4587 matrices, class_243 cam, Settings settings, IntUnaryOperator colorProvider, float partialTick) {
        if (matrices == null || settings == null) {
            return;
        }
        this.spawnPending(partialTick, settings);
        if (this.particles.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        long holdMs = Math.max(0L, settings.holdMs());
        long fadeMs = Math.max(1L, settings.fadeMs());
        long maxLife = holdMs + fadeMs;
        this.particles.removeIf(p -> p.isDead(now, maxLife));
        if (this.particles.isEmpty()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        Quaternionf rotation = mc.field_1773.method_19418().method_23767();
        Vector3f right = rotation.transform(new Vector3f(1.0f, 0.0f, 0.0f));
        Vector3f up = rotation.transform(new Vector3f(0.0f, 1.0f, 0.0f));
        class_1921 layer = KimikoDissolve.renderLayer();
        if (this.provider == null) {
            this.provider = class_4597.method_22991((class_9799)this.allocator);
        }
        class_4588 consumer = this.provider.method_73477(layer);
        class_4587.class_4665 pose = matrices.method_23760();
        boolean drew = false;
        for (ModelParticle particle : this.particles) {
            float nz;
            float ny;
            float alpha = particle.alpha(now, holdMs, fadeMs);
            if (alpha <= 0.004f) continue;
            class_243 position = particle.position(now, holdMs, fadeMs, settings);
            float half = particle.size(now, holdMs, fadeMs, settings.size()) * 0.5f;
            int color = KimikoDissolve.multAlpha(colorProvider.applyAsInt(particle.colorSeed), alpha);
            float angle = (float)Math.toRadians(particle.angle(now));
            float sin = (float)Math.sin(angle);
            float cos = (float)Math.cos(angle);
            float rx = (right.x() * cos - up.x() * sin) * half;
            float ry = (right.y() * cos - up.y() * sin) * half;
            float rz = (right.z() * cos - up.z() * sin) * half;
            float ux = (right.x() * sin + up.x() * cos) * half;
            float uy = (right.y() * sin + up.y() * cos) * half;
            float uz = (right.z() * sin + up.z() * cos) * half;
            float nx = ry * uz - rz * uy;
            float nl = (float)Math.sqrt(nx * nx + (ny = rz * ux - rx * uz) * ny + (nz = rx * uy - ry * ux) * nz);
            if (nl > 1.0E-5f) {
                nx /= nl;
                ny /= nl;
                nz /= nl;
            } else {
                nz = 1.0f;
            }
            float cx = (float)(position.field_1352 - cam.field_1352);
            float cy = (float)(position.field_1351 - cam.field_1351);
            float cz = (float)(position.field_1350 - cam.field_1350);
            consumer.method_56824(pose, cx - rx - ux, cy - ry - uy, cz - rz - uz).method_39415(color).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_60831(pose, nx, ny, nz);
            consumer.method_56824(pose, cx - rx + ux, cy - ry + uy, cz - rz + uz).method_39415(color).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_60831(pose, nx, ny, nz);
            consumer.method_56824(pose, cx + rx + ux, cy + ry + uy, cz + rz + uz).method_39415(color).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_60831(pose, nx, ny, nz);
            consumer.method_56824(pose, cx + rx - ux, cy + ry - uy, cz + rz - uz).method_39415(color).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_60831(pose, nx, ny, nz);
            drew = true;
        }
        if (drew) {
            this.provider.method_22994(layer);
        }
    }

    private static int multAlpha(int color, float alpha) {
        int a = Math.round(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f);
        return color & 0xFFFFFF | a << 24;
    }

    private static float smoothStep(float value) {
        float t = class_3532.method_15363((float)value, (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private static float easeOutCubic(float value) {
        float inv = 1.0f - class_3532.method_15363((float)value, (float)0.0f, (float)1.0f);
        return 1.0f - inv * inv * inv;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clear() {
        KimikoDissolve kimikoDissolve = INSTANCE;
        synchronized (kimikoDissolve) {
            KimikoDissolve.INSTANCE.particles.clear();
        }
    }

    private static class_1921 renderLayer() {
        if (cachedLayer == null) {
            class_12247 renderSetup = class_12247.method_75927((RenderPipeline)class_10799.field_56904).method_75934("Sampler0", GLOW_TEXTURE).method_75937().method_75929(262144).method_75938();
            cachedLayer = class_1921.method_75940((String)"kimiko_dissolve_glow", (class_12247)renderSetup);
        }
        return cachedLayer;
    }

    @Environment(value=EnvType.CLIENT)
    public record Settings(int count, float size, long holdMs, long fadeMs, float rise, float chaos) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class ModelParticle {
        private final class_243 anchor;
        private final class_243 drift;
        private final long bornMs;
        private final int colorSeed;
        private final float startAngle;
        private final float spin;
        private final float phase;
        private final float sizeMul;

        ModelParticle(class_243 anchor, class_243 drift, long bornMs, int colorSeed, float startAngle, float spin, float phase, float sizeMul) {
            this.anchor = anchor;
            this.drift = drift;
            this.bornMs = bornMs;
            this.colorSeed = colorSeed;
            this.startAngle = startAngle;
            this.spin = spin;
            this.phase = phase;
            this.sizeMul = sizeMul;
        }

        int colorSeed() {
            return this.colorSeed;
        }

        boolean isDead(long nowMs, long maxLifeMs) {
            return nowMs - this.bornMs >= maxLifeMs;
        }

        float evaporation(long nowMs, long holdMs, long fadeMs) {
            return class_3532.method_15363((float)((float)(nowMs - this.bornMs - holdMs) / (float)fadeMs), (float)0.0f, (float)1.0f);
        }

        class_243 position(long nowMs, long holdMs, long fadeMs, Settings settings) {
            float evap = this.evaporation(nowMs, holdMs, fadeMs);
            if (evap <= 0.0f) {
                return this.anchor;
            }
            float chaos = Math.max(0.0f, settings.chaos());
            float eased = KimikoDissolve.easeOutCubic(evap);
            double wave = Math.sin((double)(nowMs - this.bornMs) * 0.012 + (double)this.phase) * 0.075 * (double)chaos * (double)eased;
            double sideWave = Math.cos((double)(nowMs - this.bornMs) * 0.009 + (double)this.phase * 1.37) * 0.055 * (double)chaos * (double)eased;
            return this.anchor.method_1019(this.drift.method_1021((double)eased)).method_1031(sideWave, (double)(settings.rise() * eased) * (0.75 + 0.25 * Math.sin(this.phase)), wave);
        }

        float alpha(long nowMs, long holdMs, long fadeMs) {
            float fadeIn = class_3532.method_15363((float)((float)(nowMs - this.bornMs) / 80.0f), (float)0.0f, (float)1.0f);
            float evap = this.evaporation(nowMs, holdMs, fadeMs);
            if (evap <= 0.0f) {
                return fadeIn;
            }
            return fadeIn * (1.0f - KimikoDissolve.smoothStep(evap));
        }

        float size(long nowMs, long holdMs, long fadeMs, float baseSize) {
            float evap = this.evaporation(nowMs, holdMs, fadeMs);
            float pulse = 1.0f + (float)Math.sin((float)(nowMs - this.bornMs) * 0.018f + this.phase) * 0.08f;
            float dissolveScale = 1.0f - KimikoDissolve.smoothStep(evap) * 0.45f;
            return baseSize * this.sizeMul * pulse * dissolveScale;
        }

        float angle(long nowMs) {
            return this.startAngle + (float)(nowMs - this.bornMs) / 1000.0f * this.spin;
        }
    }
}

