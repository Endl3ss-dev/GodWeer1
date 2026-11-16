/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_1309
 *  net.minecraft.class_1921
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_638
 *  net.minecraft.class_7833
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package ru.decide.utils.render.killeffect;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_638;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public final class DeathParticles {
    private static final DeathParticles INSTANCE = new DeathParticles();
    private static final class_2960 GLOW = class_2960.method_60655((String)"decide", (String)"textures/killeffect/glow.png");
    private static final float MIN_GRAVITY = 0.005f;
    private static final float GRAVITY_RANDOM_RANGE = 0.005f;
    private static final float PHYSICS_DAMPING = 0.999f;
    private static final float FREE_DAMPING = 0.995f;
    private static final float STEP_MILLIS = 16.67f;
    private static final float MAX_STEP_DELTA = 5.0f;
    private static final float ROTATION_TIME_SCALE = 0.1f;
    private static final float Y_BOUNCE_MULTIPLIER = 0.5f;
    private static final float X_BOUNCE_MULTIPLIER = 0.5f;
    private static final float Z_BOUNCE_MULTIPLIER = 0.5f;
    private static final float VERTICAL_REST_THRESHOLD = 1.0E-4f;
    private static final double FLOOR_PROBE_OFFSET = 0.05;
    private static final long MIN_LIFETIME_MILLIS = 150L;
    private final List<Burst> bursts = new ArrayList<Burst>();
    private final class_9799 allocator = new class_9799(65536);
    private class_1921 cachedLayer;
    private static final RenderPipeline PARTICLE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"kill_effect_destra_particles")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());

    private DeathParticles() {
    }

    public static DeathParticles get() {
        return INSTANCE;
    }

    public void spawn(class_1309 entity, Settings settings) {
        if (entity == null || settings == null) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null) {
            return;
        }
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        class_243 pos = entity.method_73189();
        float width = Math.max(0.35f, entity.method_17681());
        float height = Math.max(0.8f, entity.method_17682());
        float yaw = (float)Math.toRadians(-entity.method_36454() + 90.0f);
        int count = Math.max(1, settings.count());
        float size = settings.particleSize();
        long life = (long)(settings.lifetimeSeconds() * 1000.0f);
        boolean gravity = settings.gravity();
        ArrayList<Particle> particles = new ArrayList<Particle>(count);
        DeathParticles.emitSphere(particles, pos.method_1031(0.0, (double)(height - 0.2f), 0.0), width * 0.4f, Math.max(6, count / 10), size, gravity, life, rnd);
        DeathParticles.emitColumn(particles, pos, width * 0.4f, height * 0.85f, width * 0.2f, yaw, Math.max(16, count / 4), size, gravity, life, rnd);
        for (int side = -1; side <= 1; side += 2) {
            DeathParticles.emitCone(particles, pos.method_1031((double)(class_3532.method_15374((double)yaw) * width * 0.5f * (float)side), (double)(height * 0.75f), (double)(class_3532.method_15362((double)yaw) * width * 0.5f * (float)side)), width * 0.4f, width * 0.15f, Math.max(8, count / 8), size, gravity, life, rnd);
            DeathParticles.emitCone(particles, pos.method_1031((double)(class_3532.method_15374((double)yaw) * width * 0.15f * (float)side), (double)(height * 0.45f), (double)(class_3532.method_15362((double)yaw) * width * 0.15f * (float)side)), height * 0.4f, width * 0.15f, Math.max(10, count / 6), size, gravity, life, rnd);
        }
        this.bursts.add(new Burst(settings.color(), particles));
    }

    private static void emitSphere(List<Particle> out, class_243 center, float radius, int count, float size, boolean gravity, long life, ThreadLocalRandom rnd) {
        for (int i = 0; i < count; ++i) {
            float azimuth = rnd.nextFloat(0.0f, (float)Math.PI * 2);
            float polar = (float)Math.acos(2.0f * rnd.nextFloat() - 1.0f);
            float r = radius * (float)Math.cbrt(rnd.nextFloat());
            float x = r * (float)(Math.sin(polar) * Math.cos(azimuth));
            float y = r * (float)(Math.sin(polar) * Math.sin(azimuth));
            float z = r * (float)Math.cos(polar);
            DeathParticles.emit(out, center.method_1031((double)x, (double)y, (double)z), size, gravity, life, rnd);
        }
    }

    private static void emitCone(List<Particle> out, class_243 center, float radius, float spread, int count, float size, boolean gravity, long life, ThreadLocalRandom rnd) {
        for (int i = 0; i < count; ++i) {
            float vy = -rnd.nextFloat() * radius;
            float vx = (rnd.nextFloat() - 0.5f) * spread * 2.0f;
            float vz = (rnd.nextFloat() - 0.5f) * spread * 2.0f;
            DeathParticles.emit(out, center.method_1031((double)vx, (double)vy, (double)vz), size, gravity, life, rnd);
        }
    }

    private static void emitColumn(List<Particle> out, class_243 center, float radiusX, float height, float radiusZ, float yaw, int count, float size, boolean gravity, long life, ThreadLocalRandom rnd) {
        for (int i = 0; i < count; ++i) {
            float x = (rnd.nextFloat() - 0.5f) * radiusX * 2.0f;
            float y = rnd.nextFloat() * height;
            float z = (rnd.nextFloat() - 0.5f) * radiusZ * 2.0f;
            float rx = x * (float)Math.cos(yaw) - z * (float)Math.sin(yaw);
            float rz = x * (float)Math.sin(yaw) + z * (float)Math.cos(yaw);
            DeathParticles.emit(out, center.method_1031((double)rx, (double)y, (double)rz), size, gravity, life, rnd);
        }
    }

    private static void emit(List<Particle> out, class_243 pos, float size, boolean gravity, long life, ThreadLocalRandom rnd) {
        float spread = gravity ? 0.04f : 0.03f;
        float vx = (rnd.nextFloat() - 0.5f) * spread;
        float vy = gravity ? 0.025f + rnd.nextFloat() * 0.035f : (rnd.nextFloat() - 0.5f) * 0.03f;
        float vz = (rnd.nextFloat() - 0.5f) * spread;
        out.add(new Particle(pos, vx, vy, vz, size * rnd.nextFloat(0.75f, 1.35f), gravity, (long)((float)life * rnd.nextFloat(0.75f, 1.35f)), rnd));
    }

    public void clear() {
        this.bursts.clear();
    }

    public void render(class_4587 matrices, class_243 cam) {
        if (this.bursts.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        Iterator<Burst> it = this.bursts.iterator();
        while (it.hasNext()) {
            if (it.next().update(now)) continue;
            it.remove();
        }
        if (this.bursts.isEmpty()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        Quaternionf billboard = mc.field_1773.method_19418().method_23767();
        class_4597.class_4598 provider = class_4597.method_22991((class_9799)this.allocator);
        class_4588 buf = provider.method_73477(this.layer());
        for (Burst burst : this.bursts) {
            int r = burst.color >> 16 & 0xFF;
            int g = burst.color >> 8 & 0xFF;
            int b = burst.color & 0xFF;
            for (Particle p : burst.particles) {
                float lifeFrac = p.remainingLifeFraction(now);
                if (lifeFrac <= 0.01f) continue;
                float size = p.size * (0.58f + 0.42f * lifeFrac);
                int halo = DeathParticles.alpha(r, g, b, (int)(lifeFrac * 0.28f * 255.0f));
                int core = DeathParticles.alpha(r, g, b, (int)(lifeFrac * (p.alternateAlpha ? 0.94f : 0.72f) * 255.0f));
                matrices.method_22903();
                matrices.method_22904(p.x - cam.field_1352, p.y - cam.field_1351, p.z - cam.field_1350);
                matrices.method_22907((Quaternionfc)billboard);
                matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(p.rotationDegrees));
                Matrix4f mat = matrices.method_23760().method_23761();
                DeathParticles.quad(buf, mat, size * 0.85f, size * 0.85f, halo);
                DeathParticles.quad(buf, mat, size * 0.26f, size * 0.26f, core);
                matrices.method_22909();
            }
        }
        provider.method_22993();
    }

    private static void quad(class_4588 buf, Matrix4f mat, float halfW, float halfH, int color) {
        float alpha = (float)(color >>> 24) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float u0 = 0.0f;
        float v0 = 0.0f;
        float u1 = 1.0f;
        float v1 = 1.0f;
        buf.method_22918((Matrix4fc)mat, -halfW, -halfH, 0.0f).method_22913(u0, v0).method_22915(r, g, b, alpha);
        buf.method_22918((Matrix4fc)mat, halfW, -halfH, 0.0f).method_22913(u1, v0).method_22915(r, g, b, alpha);
        buf.method_22918((Matrix4fc)mat, halfW, halfH, 0.0f).method_22913(u1, v1).method_22915(r, g, b, alpha);
        buf.method_22918((Matrix4fc)mat, -halfW, halfH, 0.0f).method_22913(u0, v1).method_22915(r, g, b, alpha);
    }

    private static int alpha(int r, int g, int b, int a) {
        return Math.max(0, Math.min(255, a)) << 24 | r << 16 | g << 8 | b;
    }

    private class_1921 layer() {
        if (this.cachedLayer == null) {
            class_12247 setup = class_12247.method_75927((RenderPipeline)PARTICLE_PIPELINE).method_75934("Sampler0", GLOW).method_75937().method_75929(65536).method_75938();
            this.cachedLayer = class_1921.method_75940((String)"kill_effect_destra_particles", (class_12247)setup);
        }
        return this.cachedLayer;
    }

    @Environment(value=EnvType.CLIENT)
    public record Settings(int count, float particleSize, float lifetimeSeconds, boolean gravity, int color) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Burst {
        private final int color;
        private final List<Particle> particles;

        Burst(int color, List<Particle> particles) {
            this.color = color;
            this.particles = particles;
        }

        private boolean update(long now) {
            this.particles.removeIf(p -> p.isExpired(now));
            for (Particle p2 : this.particles) {
                p2.update(now);
            }
            return !this.particles.isEmpty();
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Particle {
        private double x;
        private double y;
        private double z;
        private float velocityX;
        private float velocityY;
        private float velocityZ;
        private final float size;
        private final boolean physicsEnabled;
        private final long lifetimeMillis;
        private final boolean alternateAlpha;
        private final float gravity;
        private final float damping;
        private float rotationDegrees;
        private final float angularVelocity;
        private final long createdAtMillis;
        private long lastUpdateMillis;

        Particle(class_243 position, float vx, float vy, float vz, float size, boolean physics, long lifetime, ThreadLocalRandom rnd) {
            this.x = position.field_1352;
            this.y = position.field_1351;
            this.z = position.field_1350;
            this.velocityX = vx;
            this.velocityY = vy;
            this.velocityZ = vz;
            this.size = size;
            this.physicsEnabled = physics;
            this.lifetimeMillis = Math.max(150L, lifetime);
            this.alternateAlpha = rnd.nextBoolean();
            this.gravity = 0.005f + rnd.nextFloat() * 0.005f;
            this.damping = physics ? 0.999f : 0.995f;
            this.rotationDegrees = rnd.nextFloat(0.0f, 360.0f);
            this.angularVelocity = rnd.nextFloat(-140.0f, 140.0f);
            this.lastUpdateMillis = this.createdAtMillis = System.currentTimeMillis();
        }

        private float remainingLifeFraction(long now) {
            float age = class_3532.method_15363((float)((float)(now - this.createdAtMillis) / (float)this.lifetimeMillis), (float)0.0f, (float)1.0f);
            return 1.0f - age;
        }

        private boolean isExpired(long now) {
            return now - this.createdAtMillis > this.lifetimeMillis;
        }

        private void update(long now) {
            class_638 world;
            if (this.isExpired(now)) {
                return;
            }
            float step = class_3532.method_15363((float)((float)(now - this.lastUpdateMillis) / 16.67f), (float)0.0f, (float)5.0f);
            this.lastUpdateMillis = now;
            if (step <= 0.0f) {
                return;
            }
            if (this.physicsEnabled) {
                this.velocityY -= this.gravity * step;
            }
            float damp = (float)Math.pow(this.damping, step);
            this.velocityX *= damp;
            this.velocityY *= damp;
            this.velocityZ *= damp;
            this.rotationDegrees += this.angularVelocity * step * 0.1f;
            double nextX = this.x + (double)(this.velocityX * step);
            double nextY = this.y + (double)(this.velocityY * step);
            double nextZ = this.z + (double)(this.velocityZ * step);
            if (this.physicsEnabled && (world = class_310.method_1551().field_1687) != null) {
                if (!world.method_8320(class_2338.method_49637((double)this.x, (double)(nextY - 0.05), (double)this.z)).method_26215()) {
                    this.velocityY = -this.velocityY * 0.5f;
                    nextY = this.y;
                }
                if (!world.method_8320(class_2338.method_49637((double)nextX, (double)this.y, (double)this.z)).method_26215()) {
                    this.velocityX = -this.velocityX * 0.5f;
                    nextX = this.x;
                }
                if (!world.method_8320(class_2338.method_49637((double)this.x, (double)this.y, (double)nextZ)).method_26215()) {
                    this.velocityZ = -this.velocityZ * 0.5f;
                    nextZ = this.z;
                }
            }
            if (Math.abs(this.velocityY) <= 1.0E-4f) {
                this.velocityX = 0.0f;
                this.velocityZ = 0.0f;
            }
            this.x = nextX;
            this.y = nextY;
            this.z = nextZ;
        }
    }
}

