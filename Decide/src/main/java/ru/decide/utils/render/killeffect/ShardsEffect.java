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
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.decide.utils.render.killeffect;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.decide.utils.render.killeffect.KillEffectMath;

@Environment(value=EnvType.CLIENT)
public final class ShardsEffect {
    private static final int[] RING_COUNTS = new int[]{10, 16, 14, 8};
    private static final float[] RING_HEIGHTS = new float[]{0.2f, 0.7f, 1.3f, 1.9f};
    private static final int[] RING_HUES = new int[]{0, 20, -20, 40};
    private static final RenderPipeline SHARDS_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"kill_effect_shards")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    private static final ShardsEffect INSTANCE = new ShardsEffect();
    private final class_9799 allocator = new class_9799(65536);
    private final List<Burst> bursts = new ArrayList<Burst>();
    private final Random random = new Random();
    private class_4597.class_4598 provider;
    private class_1921 cachedLayer;

    private ShardsEffect() {
    }

    public static ShardsEffect get() {
        return INSTANCE;
    }

    public synchronized void spawn(class_243 pos, Settings settings) {
        if (pos == null || settings == null) {
            return;
        }
        float density = Math.max(0.1f, settings.density());
        ArrayList<Shard> shards = new ArrayList<Shard>();
        for (int r = 0; r < RING_COUNTS.length; ++r) {
            int count = Math.max(2, Math.round((float)RING_COUNTS[r] * density));
            for (int i = 0; i < count; ++i) {
                double angle = Math.PI * 2 / (double)count * (double)i + this.random.nextDouble() * 0.5;
                float speed = 1.2f + (float)this.random.nextDouble() * 2.0f;
                double yVel = -0.1 + this.random.nextDouble() * 0.8;
                class_243 dir = new class_243(Math.cos(angle), yVel, Math.sin(angle)).method_1029();
                float rotSpeed = (float)(this.random.nextDouble() * 8.0 - 4.0);
                float length = 0.2f + (float)this.random.nextDouble() * 0.6f;
                float grav = 0.5f + (float)this.random.nextDouble() * 1.0f;
                shards.add(new Shard(pos.method_1031(0.0, (double)RING_HEIGHTS[r], 0.0), dir, speed, rotSpeed, length, grav, RING_HUES[r]));
            }
        }
        this.bursts.add(new Burst(pos, System.currentTimeMillis(), shards));
    }

    public synchronized void clear() {
        this.bursts.clear();
    }

    public synchronized void render(class_4587 matrices, class_243 cam, Settings settings) {
        if (matrices == null || cam == null || settings == null) {
            return;
        }
        this.bursts.removeIf(b -> System.currentTimeMillis() - b.bornMs >= settings.durationMs());
        if (this.bursts.isEmpty()) {
            return;
        }
        Matrix4f mat = matrices.method_23760().method_23761();
        if (this.provider == null) {
            this.provider = class_4597.method_22991((class_9799)this.allocator);
        }
        class_4588 buf = this.provider.method_73477(this.layer());
        long now = System.currentTimeMillis();
        int theme = settings.color();
        boolean drew = false;
        for (Burst burst : this.bursts) {
            long elapsed = now - burst.bornMs;
            float t = class_3532.method_15363((float)((float)elapsed / (float)settings.durationMs()), (float)0.0f, (float)1.0f);
            if (t >= 1.0f) continue;
            drew |= this.renderBurst(buf, mat, burst, t, elapsed, theme, cam);
        }
        if (drew) {
            this.provider.method_22994(this.layer());
        }
    }

    private boolean renderBurst(class_4588 buf, Matrix4f mat, Burst burst, float t, long elapsed, int theme, class_243 cam) {
        float sphereAlpha;
        class_243 pos = burst.pos.method_1020(cam);
        float fadeIn = KillEffectMath.smoothStep(0.0f, 0.06f, t);
        boolean drew = false;
        if (t < 0.25f) {
            float bt = t / 0.25f;
            float ease = KillEffectMath.easeOutExpo(bt);
            for (int ring = 0; ring < 4; ++ring) {
                float rr = ease * (0.8f + (float)ring * 0.5f);
                float ra = (1.0f - bt) * (1.0f - (float)ring * 0.2f);
                float lw = 3.5f - (float)ring * 0.6f;
                int rc = KillEffectMath.withAlpha(KillEffectMath.shiftHue(theme, (float)ring * 22.0f), 240.0f * ra / 255.0f);
                drew |= ShardsEffect.ring(buf, mat, pos.field_1352, pos.field_1351 + 0.9, pos.field_1350, rr, 40, 0.0, rc, lw * 0.05f);
            }
            float vba = 1.0f - bt;
            drew |= ShardsEffect.line(buf, mat, pos.field_1352, pos.field_1351 - 0.5, pos.field_1350, pos.field_1352, pos.field_1351 + 3.5, pos.field_1350, KillEffectMath.withAlpha(theme, 180.0f * vba / 255.0f), 0.075f);
            drew |= ShardsEffect.line(buf, mat, pos.field_1352, pos.field_1351 - 0.5, pos.field_1350, pos.field_1352, pos.field_1351 + 3.5, pos.field_1350, KillEffectMath.withAlpha(-1, 120.0f * vba / 255.0f), 0.025f);
        }
        float gravity = t * t * 4.5f;
        float vortexPull = KillEffectMath.smoothStep(0.35f, 0.9f, t) * 2.2f;
        float ease = KillEffectMath.easeOutCubic(Math.min(t * 1.6f, 1.0f));
        float seconds = (float)elapsed / 1000.0f;
        for (int i = 0; i < burst.shards.size(); ++i) {
            Shard s = burst.shards.get(i);
            double vx = s.dir.field_1352 * (double)s.speed * (double)ease * (1.0 - (double)vortexPull * 0.45);
            double vz = s.dir.field_1350 * (double)s.speed * (double)ease * (1.0 - (double)vortexPull * 0.45);
            double vy = s.dir.field_1351 * (double)s.speed * (double)ease - (double)(gravity * s.gravMult) + (double)vortexPull * 3.0;
            double sx = s.origin.field_1352 + vx - cam.field_1352;
            double sy = s.origin.field_1351 + vy - cam.field_1351;
            double sz = s.origin.field_1350 + vz - cam.field_1350;
            double rot = Math.toRadians((double)s.rotSpeed * 6.0 * (double)seconds);
            double perpX = -s.dir.field_1350;
            double perpZ = s.dir.field_1352;
            double ex = sx + (Math.cos(rot) * s.dir.field_1352 + Math.sin(rot) * perpX) * (double)s.length;
            double ey = sy + Math.sin(rot * 0.7) * (double)s.length * 0.35;
            double ez = sz + (Math.cos(rot) * s.dir.field_1350 + Math.sin(rot) * perpZ) * (double)s.length;
            float alphaF = (1.0f - t) * (1.0f - t);
            int a = (int)(235.0f * (alphaF *= 0.65f + (float)Math.sin((double)elapsed * 0.009 + (double)i * 0.8) * 0.35f) * fadeIn);
            if (a <= 0) continue;
            float lw = 1.0f + (1.0f - t) * 2.2f;
            int col = KillEffectMath.withAlpha(KillEffectMath.shiftHue(theme, s.hueShift + i % 3 * 15), (float)a / 255.0f);
            drew |= ShardsEffect.line(buf, mat, sx, sy, sz, ex, ey, ez, col, lw * 0.016f);
            double len = Math.sqrt(vx * vx + vy * vy + vz * vz);
            if (!(len > 1.0E-4)) continue;
            double nx = vx / len;
            double ny = vy / len;
            double nz = vz / len;
            drew |= ShardsEffect.line(buf, mat, sx, sy, sz, sx - nx * 0.25 * (double)ease, sy - ny * 0.25 * (double)ease, sz - nz * 0.25 * (double)ease, KillEffectMath.withAlpha(KillEffectMath.shiftHue(theme, (float)s.hueShift + 20.0f), (float)(a / 3) / 255.0f), lw * 0.006f);
            drew |= ShardsEffect.line(buf, mat, sx, sy, sz, sx - nx * 0.12 * (double)ease, sy - ny * 0.12 * (double)ease, sz - nz * 0.12 * (double)ease, KillEffectMath.withAlpha(-1, (float)(a / 6) / 255.0f), lw * 0.003f);
        }
        float vortexT = KillEffectMath.smoothStep(0.28f, 0.88f, t);
        if (vortexT > 0.01f) {
            int spiralSegs = 48;
            float spiralH = 4.5f;
            double spiralPhase = (double)elapsed * 0.006;
            for (int ix = 0; ix < spiralSegs - 1; ++ix) {
                float fA = (float)ix / (float)spiralSegs;
                float fB = (float)(ix + 1) / (float)spiralSegs;
                for (int strand = 0; strand < 2; ++strand) {
                    double stOff = (double)strand * Math.PI;
                    double aA = (double)fA * Math.PI * 10.0 + spiralPhase + stOff;
                    double aB = (double)fB * Math.PI * 10.0 + spiralPhase + stOff;
                    float rA = (0.9f - fA * 0.82f) * vortexT;
                    float rB = (0.9f - fB * 0.82f) * vortexT;
                    int va = (int)(160.0f * vortexT * (float)Math.sin((double)fA * Math.PI) * (1.0f - fA * 0.5f));
                    if (va <= 0) continue;
                    int vc = KillEffectMath.withAlpha(KillEffectMath.shiftHue(theme, (float)strand * 30.0f + fA * 25.0f), (float)va / 255.0f);
                    drew |= ShardsEffect.line(buf, mat, pos.field_1352 + Math.cos(aA) * (double)rA, pos.field_1351 + (double)(fA * spiralH), pos.field_1350 + Math.sin(aA) * (double)rA, pos.field_1352 + Math.cos(aB) * (double)rB, pos.field_1351 + (double)(fB * spiralH), pos.field_1350 + Math.sin(aB) * (double)rB, vc, 0.011f);
                }
            }
            int ringCount = 7;
            for (int v = 0; v < ringCount; ++v) {
                float vf = (float)v / (float)ringCount;
                float vR = (0.9f - vf * 0.82f) * vortexT;
                double ph = (double)elapsed * 0.005 + (double)v * 0.6;
                int va = (int)(80.0f * vortexT * (1.0f - vf * 0.6f));
                if (va <= 0) continue;
                int vc = KillEffectMath.withAlpha(KillEffectMath.shiftHue(theme, (float)v * 20.0f), (float)va / 255.0f);
                drew |= ShardsEffect.ring(buf, mat, pos.field_1352, pos.field_1351 + (double)(vf * spiralH), pos.field_1350, vR, 24, ph, vc, 0.009f);
            }
        }
        if ((sphereAlpha = ((float)Math.sin((double)elapsed * 0.008) * 0.3f + 0.5f) * (1.0f - t) * fadeIn) > 0.02f) {
            float sr = 0.18f + (float)Math.sin((double)elapsed * 0.01) * 0.06f;
            for (int axis = 0; axis < 3; ++axis) {
                double phaseOff = (double)elapsed * 0.003 + (double)axis * Math.PI / 3.0;
                int sa = (int)(180.0f * sphereAlpha);
                int sc = KillEffectMath.withAlpha(KillEffectMath.shiftHue(theme, (float)axis * 30.0f), (float)sa / 255.0f);
                drew |= ShardsEffect.ring(buf, mat, pos.field_1352, pos.field_1351 + 0.9, pos.field_1350, sr, 24, phaseOff, sc, 0.015f);
            }
        }
        if (t > 0.85f) {
            float ft = (t - 0.85f) / 0.15f;
            float e = KillEffectMath.easeOutExpo(ft);
            for (int ringIdx = 0; ringIdx < 3; ++ringIdx) {
                float fr = e * (1.0f + (float)ringIdx * 0.6f);
                float fa = (1.0f - ft) * (1.0f - (float)ringIdx * 0.3f);
                int fc = KillEffectMath.withAlpha(KillEffectMath.shiftHue(theme, (float)ringIdx * 25.0f), 200.0f * fa / 255.0f);
                drew |= ShardsEffect.ring(buf, mat, pos.field_1352, pos.field_1351 + 0.9, pos.field_1350, fr, 40, 0.0, fc, 0.025f);
            }
        }
        return drew;
    }

    private class_1921 layer() {
        if (this.cachedLayer == null) {
            class_12247 setup = class_12247.method_75927((RenderPipeline)SHARDS_PIPELINE).method_75937().method_75929(65536).method_75938();
            this.cachedLayer = class_1921.method_75940((String)"kill_effect_shards", (class_12247)setup);
        }
        return this.cachedLayer;
    }

    private static boolean ring(class_4588 buf, Matrix4f mat, double cx, double cy, double cz, float radius, int segments, double phase, int color, float width) {
        if (radius <= width || color >>> 24 == 0) {
            return false;
        }
        float inner = radius - width;
        for (int i = 0; i < segments; ++i) {
            double a1 = Math.PI * 2 / (double)segments * (double)i + phase;
            double a2 = Math.PI * 2 / (double)segments * (double)(i + 1) + phase;
            double o1x = Math.cos(a1);
            double o1z = Math.sin(a1);
            double o2x = Math.cos(a2);
            double o2z = Math.sin(a2);
            buf.method_22918((Matrix4fc)mat, (float)(cx + o1x * (double)radius), (float)cy, (float)(cz + o1z * (double)radius)).method_39415(color);
            buf.method_22918((Matrix4fc)mat, (float)(cx + o1x * (double)inner), (float)cy, (float)(cz + o1z * (double)inner)).method_39415(color);
            buf.method_22918((Matrix4fc)mat, (float)(cx + o2x * (double)inner), (float)cy, (float)(cz + o2z * (double)inner)).method_39415(color);
            buf.method_22918((Matrix4fc)mat, (float)(cx + o2x * (double)radius), (float)cy, (float)(cz + o2z * (double)radius)).method_39415(color);
        }
        return true;
    }

    private static boolean line(class_4588 buf, Matrix4f mat, double x1, double y1, double z1, double x2, double y2, double z2, int color, float width) {
        if (color >>> 24 == 0 || width <= 0.0f) {
            return false;
        }
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        if (dx * dx + dy * dy + dz * dz < 1.0E-10) {
            return false;
        }
        double sx = dy * z1 - dz * y1;
        double sy = dz * x1 - dx * z1;
        double sz = dx * y1 - dy * x1;
        double sl = Math.sqrt(sx * sx + sy * sy + sz * sz);
        if (sl < 1.0E-6) {
            return false;
        }
        sx = sx / sl * (double)width;
        sy = sy / sl * (double)width;
        sz = sz / sl * (double)width;
        buf.method_22918((Matrix4fc)mat, (float)(x1 - sx), (float)(y1 - sy), (float)(z1 - sz)).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)(x1 + sx), (float)(y1 + sy), (float)(z1 + sz)).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)(x2 + sx), (float)(y2 + sy), (float)(z2 + sz)).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)(x2 - sx), (float)(y2 - sy), (float)(z2 - sz)).method_39415(color);
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public record Settings(int color, float density, long durationMs) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Shard {
        final class_243 origin;
        final class_243 dir;
        final float speed;
        final float rotSpeed;
        final float length;
        final float gravMult;
        final int hueShift;

        Shard(class_243 origin, class_243 dir, float speed, float rotSpeed, float length, float gravMult, int hueShift) {
            this.origin = origin;
            this.dir = dir;
            this.speed = speed;
            this.rotSpeed = rotSpeed;
            this.length = length;
            this.gravMult = gravMult;
            this.hueShift = hueShift;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Burst {
        final class_243 pos;
        final long bornMs;
        final List<Shard> shards;

        Burst(class_243 pos, long bornMs, List<Shard> shards) {
            this.pos = pos;
            this.bornMs = bornMs;
            this.shards = shards;
        }
    }
}

