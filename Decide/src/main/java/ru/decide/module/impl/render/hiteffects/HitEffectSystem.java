/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_9799
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package ru.decide.module.impl.render.hiteffects;

import java.util.Arrays;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.module.impl.render.hiteffects.HitEffectParticles;
import ru.decide.module.impl.render.hiteffects.HitEffectRender;

@Environment(value=EnvType.CLIENT)
public final class HitEffectSystem {
    private static final int SLASH = 0;
    private static final int COMIC = 1;
    private static final int SLASH_LIFE = 9;
    private static final int COMIC_LIFE = 14;
    private final HitEffectParticles sparks = new HitEffectParticles(256, 0.9f, -0.03f);
    private final HitEffectParticles floaters = new HitEffectParticles(128, 0.9f, 0.003f);
    private final HitEffectParticles stars = new HitEffectParticles(192, 0.86f, 0.0f);
    private final HitEffectParticles[] pools = new HitEffectParticles[]{this.sparks, this.floaters, this.stars};
    private final Burst[] bursts = new Burst[16];
    private final Random random = new Random();
    private int cursor;
    private float size = 1.0f;

    public HitEffectSystem() {
        Arrays.setAll(this.bursts, index -> new Burst());
    }

    public void attack(String mode, class_243 hit, class_243 gaze, class_238 box, float yaw, float size, float amount) {
        switch (mode) {
            case "\u0418\u0441\u043a\u0440\u044b": {
                this.spray(hit, gaze, Math.round(16.0f * amount), 1.0f, size);
                break;
            }
            case "\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0439 \u043a\u0440\u0438\u0442": {
                this.scatter(hit, this.sparks, 2, Math.round(10.0f * amount), 0.1f, 0.09f, 9, size);
                break;
            }
            case "\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0435 \u0441\u0435\u0440\u0434\u0435\u0447\u043a\u0438": {
                this.hearts(hit, amount, size);
                break;
            }
            case "\u041a\u043e\u043c\u0438\u043a\u0441": {
                class_243 beside = this.beside(box, yaw, size);
                this.burst(beside, gaze, yaw, 1);
                this.scatter(beside, this.stars, 4, 6, 0.06f, 0.05f, 12, size);
                break;
            }
            default: {
                this.burst(hit, gaze, yaw, 0);
                this.spray(hit, gaze, 6, 0.6f, size);
            }
        }
    }

    public void tick() {
        for (HitEffectParticles pool : this.pools) {
            pool.tick();
        }
        for (Burst burst : this.bursts) {
            if (burst.age >= HitEffectSystem.life(burst.kind)) continue;
            ++burst.age;
        }
    }

    public void render(EventRender3D event, class_9799 allocator) {
        if (!this.hasLive()) {
            return;
        }
        class_4597.class_4598 provider = class_4597.method_22991((class_9799)allocator);
        class_243 camera = class_310.method_1551().field_1773.method_19418().method_71156();
        Quaternionf rotation = new Quaternionf((Quaternionfc)class_310.method_1551().field_1773.method_19418().method_23767());
        HitEffectRender.Frame frame = HitEffectRender.frame(event.getMatrixStack(), provider, camera, rotation, event.getTickDelta());
        for (HitEffectParticles pool : this.pools) {
            pool.render(frame);
        }
        for (Burst burst : this.bursts) {
            if (burst.age >= HitEffectSystem.life(burst.kind)) continue;
            float time = (float)burst.age + event.getTickDelta();
            if (burst.kind == 0) {
                this.slash(frame, burst, time);
                continue;
            }
            this.comic(frame, burst, time);
        }
        provider.method_22993();
    }

    public void clear() {
        for (HitEffectParticles pool : this.pools) {
            pool.clear();
        }
        for (Burst burst : this.bursts) {
            burst.age = Integer.MAX_VALUE;
        }
        this.cursor = 0;
    }

    private boolean hasLive() {
        for (HitEffectParticles pool : this.pools) {
            if (!pool.hasLive()) continue;
            return true;
        }
        for (Burst burst : this.bursts) {
            if (burst.age >= HitEffectSystem.life(burst.kind)) continue;
            return true;
        }
        return false;
    }

    private void burst(class_243 point, class_243 gaze, float yaw, int kind) {
        Vector3f up;
        Burst burst = this.bursts[this.cursor];
        this.cursor = (this.cursor + 1) % this.bursts.length;
        float radians = (float)Math.toRadians(yaw);
        float roll = (this.random.nextFloat() * 2.0f - 1.0f) * 0.7f;
        Vector3f forward = new Vector3f(-class_3532.method_15374((double)radians), 0.0f, -class_3532.method_15362((double)radians));
        Vector3f second = new Vector3f((Vector3fc)forward).cross((Vector3fc)(up = new Vector3f((float)gaze.field_1352, (float)gaze.field_1351, (float)gaze.field_1350).normalize()));
        if (second.lengthSquared() < 1.0E-6f) {
            second.set(1.0f, 0.0f, 0.0f).cross((Vector3fc)up);
        }
        if (second.lengthSquared() < 1.0E-6f) {
            second.set(0.0f, 0.0f, 1.0f);
        }
        second.normalize();
        burst.u.set((Vector3fc)forward).mul(class_3532.method_15362((double)roll)).fma(class_3532.method_15374((double)roll), (Vector3fc)second);
        burst.v.set((Vector3fc)second).mul(class_3532.method_15362((double)roll)).fma(-class_3532.method_15374((double)roll), (Vector3fc)forward);
        burst.x = point.field_1352;
        burst.y = point.field_1351;
        burst.z = point.field_1350;
        burst.seed = this.random.nextFloat();
        burst.kind = kind;
        burst.age = 0;
    }

    private void slash(HitEffectRender.Frame frame, Burst burst, float time) {
        float vanish;
        float appear = HitEffectSystem.smooth(Math.min(1.0f, time / 3.0f));
        if (appear - (vanish = HitEffectSystem.smooth(class_3532.method_15363((float)((time - 3.0f) / 6.0f), (float)0.0f, (float)1.0f))) < 0.02f) {
            return;
        }
        float wide = 0.85f * this.size;
        float thin = 0.17f * this.size;
        float side = burst.seed < 0.5f ? 1.0f : -1.0f;
        float fade = 1.0f - vanish * 0.5f;
        double x = burst.x - frame.camera.field_1352;
        double y = burst.y - frame.camera.field_1351;
        double z = burst.z - frame.camera.field_1350;
        for (int layer = 0; layer < 2; ++layer) {
            boolean core = layer == 1;
            double previousX = 0.0;
            double previousY = 0.0;
            double previousZ = 0.0;
            float previousWidth = 0.0f;
            int previousColor = 0;
            float previousAlpha = 0.0f;
            boolean hasPrevious = false;
            Vector3f previousSide = null;
            for (int index = 0; index <= 28; ++index) {
                float progress = (float)index / 28.0f;
                float angle = 1.5707964f + side * (class_3532.method_16439((float)progress, (float)vanish, (float)appear) - 0.5f) * 2.5f;
                float localX = class_3532.method_15362((double)angle) * wide;
                float localY = (class_3532.method_15374((double)angle) - 0.8f) * wide;
                float profile = (float)Math.pow(class_3532.method_15374((double)((float)Math.PI * progress)), 0.7) * (0.35f + 0.65f * progress);
                int color = core ? 0xFFFFFF : HitEffectRender.color(progress * 0.6f + burst.seed);
                float alpha = (core ? 1.0f : 0.7f) * fade;
                float width = thin * profile * (core ? 0.3f : 1.0f);
                double pointX = x + (double)(burst.u.x * localX) + (double)(burst.v.x * localY);
                double pointY = y + (double)(burst.u.y * localX) + (double)(burst.v.y * localY);
                double pointZ = z + (double)(burst.u.z * localX) + (double)(burst.v.z * localY);
                if (hasPrevious) {
                    previousSide = HitEffectRender.trailSegment(frame, previousSide, previousX, previousY, previousZ, previousWidth, previousColor, previousAlpha, pointX, pointY, pointZ, width, color, alpha);
                }
                previousX = pointX;
                previousY = pointY;
                previousZ = pointZ;
                previousWidth = width;
                previousColor = color;
                previousAlpha = alpha;
                hasPrevious = true;
            }
            HitEffectRender.trailCap(frame, previousX, previousY, previousZ, previousSide, previousWidth, previousColor, previousAlpha);
        }
    }

    private void comic(HitEffectRender.Frame frame, Burst burst, float time) {
        float progress = time / 14.0f;
        float pop = 1.25f * HitEffectSystem.smooth(class_3532.method_15363((float)(progress * 5.0f), (float)0.0f, (float)1.0f)) - 0.25f * HitEffectSystem.smooth(class_3532.method_15363((float)(progress * 5.0f - 1.0f), (float)0.0f, (float)1.0f));
        float size = 0.3f * this.size * pop * class_3532.method_15363((float)((1.0f - progress) * 5.0f), (float)0.0f, (float)1.0f);
        float tilt = (float)Math.toDegrees((burst.seed - 0.5f) * 0.6f);
        double dx = frame.camera.field_1352 - burst.x;
        double dy = frame.camera.field_1351 - burst.y;
        double dz = frame.camera.field_1350 - burst.z;
        float lift = (float)(0.05 / Math.max(0.001, Math.sqrt(dx * dx + dy * dy + dz * dz)));
        double x = burst.x - frame.camera.field_1352 + dx * (double)lift;
        double y = burst.y - frame.camera.field_1351 + dy * (double)lift;
        double z = burst.z - frame.camera.field_1350 + dz * (double)lift;
        HitEffectRender.comicBurst(frame, x, y, z, size, tilt, HitEffectRender.color(burst.seed), 1.0f);
        HitEffectRender.comicWord(frame, HitEffectRender.word(burst.seed), x, y, z, size * 1.1f, tilt, 0xFFFFFF, 1.0f);
    }

    private void spray(class_243 point, class_243 gaze, int count, float spread, float size) {
        for (int index = 0; index < count; ++index) {
            float speed = (0.2f + 0.25f * this.random.nextFloat()) * size;
            double vx = gaze.field_1352 * 0.6 + this.random.nextGaussian() * (double)spread;
            double vy = gaze.field_1351 * 0.6 + this.random.nextGaussian() * (double)spread + 0.3;
            double vz = gaze.field_1350 * 0.6 + this.random.nextGaussian() * (double)spread;
            double length = Math.sqrt(vx * vx + vy * vy + vz * vz);
            double scale = (double)speed / Math.max(0.001, length);
            this.sparks.spawn(1, 10 + this.random.nextInt(9), 0.045f * size, HitEffectRender.color(this.random.nextFloat())).at(point.field_1352, point.field_1351, point.field_1350).move(vx * scale, vy * scale, vz * scale);
        }
        this.stars.spawn(0, 5, 0.35f * size, 0xFFFFFF).at(point.field_1352, point.field_1351, point.field_1350).move(0.0, 0.0, 0.0);
    }

    private void hearts(class_243 point, float amount, float size) {
        int count = Math.round(5.0f * amount);
        for (int index = 0; index < count; ++index) {
            float particleSize = (0.12f + 0.06f * this.random.nextFloat()) * size;
            this.floaters.spawn(3, 24 + this.random.nextInt(10), particleSize, HitEffectRender.color(this.random.nextFloat())).at(point.field_1352 + (double)(this.random.nextFloat() - 0.5f) * 0.5, point.field_1351 + (double)(this.random.nextFloat() - 0.5f) * 0.4, point.field_1350 + (double)(this.random.nextFloat() - 0.5f) * 0.5).move(this.random.nextGaussian() * 0.03, 0.06 + (double)this.random.nextFloat() * 0.06, this.random.nextGaussian() * 0.03);
        }
    }

    private void scatter(class_243 point, HitEffectParticles pool, int shape, int count, float speed, float size, int life, float scale) {
        for (int index = 0; index < count; ++index) {
            double vx = this.random.nextGaussian();
            double vy = this.random.nextGaussian();
            double vz = this.random.nextGaussian();
            double length = Math.sqrt(vx * vx + vy * vy + vz * vz);
            double factor = (double)speed * (1.0 + (double)this.random.nextFloat()) * (double)scale / Math.max(0.001, length);
            pool.spawn(shape, life + this.random.nextInt(9), size * (1.0f + 0.67f * this.random.nextFloat()) * scale, HitEffectRender.color(this.random.nextFloat())).at(point.field_1352, point.field_1351, point.field_1350).move(vx * factor, vy * factor, vz * factor);
        }
    }

    private class_243 beside(class_238 box, float yaw, float size) {
        class_243 center = box.method_1005();
        float radians = (float)Math.toRadians(yaw);
        double offset = (this.random.nextBoolean() ? 1.0 : -1.0) * ((box.field_1320 - box.field_1323) * 0.5 + 0.45 * (double)size);
        return new class_243(center.field_1352 - (double)class_3532.method_15374((double)radians) * offset, box.field_1322 + (box.field_1325 - box.field_1322) * 0.8, center.field_1350 - (double)class_3532.method_15362((double)radians) * offset);
    }

    public void configure(float size) {
        this.size = size;
    }

    private static float smooth(float value) {
        return value * value * (3.0f - 2.0f * value);
    }

    private static int life(int kind) {
        return kind == 0 ? 9 : 14;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Burst {
        private final Vector3f u = new Vector3f();
        private final Vector3f v = new Vector3f();
        private double x;
        private double y;
        private double z;
        private float seed;
        private int kind = 0;
        private int age = Integer.MAX_VALUE;

        private Burst() {
        }
    }
}

