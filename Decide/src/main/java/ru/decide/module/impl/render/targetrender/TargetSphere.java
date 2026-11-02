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
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_7833
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render.targetrender;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public final class TargetSphere {
    private static final TargetSphere INSTANCE = new TargetSphere();
    private static final float RADIUS = 0.95f;
    private static final float Y_OFFSET = 0.15f;
    private static final float GOLDEN_ANGLE = 2.0f * (3.0f - (float)Math.sqrt(5.0));
    private static final class_2960[] TEXTURES = new class_2960[]{class_2960.method_60655((String)"decide", (String)"textures/targetrender/particles/glow.png"), class_2960.method_60655((String)"decide", (String)"textures/targetrender/particles/heart.png"), class_2960.method_60655((String)"decide", (String)"textures/targetrender/particles/star.png"), class_2960.method_60655((String)"decide", (String)"textures/targetrender/particles/snowflake.png"), class_2960.method_60655((String)"decide", (String)"textures/targetrender/particles/particle1.png"), class_2960.method_60655((String)"decide", (String)"textures/targetrender/particles/particle2.png"), class_2960.method_60655((String)"decide", (String)"textures/targetrender/particles/sparkle.png")};
    private final class_9799 allocator = new class_9799(131072);
    private final Map<class_2960, class_1921> layers = new HashMap<class_2960, class_1921>();
    private long startNanos = System.nanoTime();
    private int textureIndex;
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/world/targetrender_sphere")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());

    private TargetSphere() {
    }

    public static TargetSphere get() {
        return INSTANCE;
    }

    public void resetClock() {
        this.startNanos = System.nanoTime();
    }

    private static int countFor(float ringsSetting) {
        int rings = Math.max(6, Math.min(10, Math.round(ringsSetting)));
        return Math.max(96, Math.min(520, rings * rings * 2));
    }

    private static float[] pointAt(int index, int count, float t) {
        float y2;
        float sinZ;
        float u = ((float)index + 0.5f) / Math.max(1.0f, (float)count);
        float y = 1.0f - 2.0f * u;
        float ringRadius = (float)Math.sqrt(Math.max(0.0f, 1.0f - y * y));
        float angle = (float)index * GOLDEN_ANGLE + t * 0.85f;
        float px = (float)Math.cos(angle) * ringRadius;
        float py = y;
        float pz = (float)Math.sin(angle) * ringRadius;
        float ax = (float)Math.sin(t * 0.22f + u * ((float)Math.PI * 2)) * 0.22f;
        float cosX = (float)Math.cos(ax);
        float sinX = (float)Math.sin(ax);
        float y1 = py * cosX - pz * sinX;
        float z1 = py * sinX + pz * cosX;
        py = y1;
        pz = z1;
        float az = (float)Math.cos((double)(t * 0.28f) + (double)u * Math.PI) * 0.22f;
        float cosZ = (float)Math.cos(az);
        float x2 = px * cosZ - py * (sinZ = (float)Math.sin(az));
        float len = (float)Math.sqrt((px = x2) * px + (py = (y2 = px * sinZ + py * cosZ)) * py + pz * pz);
        if (len < 1.0E-6f) {
            return new float[]{0.0f, 1.0f, 0.0f};
        }
        return new float[]{px / len, py / len, pz / len};
    }

    public void render(class_4587 matrices, class_1309 target, float tickDelta, float fade, float ringsSetting, float alphaSetting, float sizeSetting, float speedSetting, int textureIndexArg, int baseColor) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1773 == null || mc.field_1773.method_19418() == null) {
            return;
        }
        this.textureIndex = Math.max(0, Math.min(TEXTURES.length - 1, textureIndexArg));
        int count = TargetSphere.countFor(ringsSetting);
        float alpha01 = class_3532.method_15363((float)(alphaSetting * fade), (float)0.0f, (float)1.0f);
        float baseSize = sizeSetting * Math.max(0.35f, fade);
        if (alpha01 <= 0.001f || baseSize <= 0.0f) {
            return;
        }
        float t = (float)(System.nanoTime() - this.startNanos) / 1.0E9f * Math.max(0.1f, speedSetting);
        class_243 pos = target.method_30950(tickDelta);
        float centerY = (float)(pos.field_1351 + (double)target.method_17682() / 2.0 + (double)0.15f);
        class_243 cam = mc.field_1773.method_19418().method_71156();
        Quaternionf billboard = mc.field_1773.method_19418().method_23767();
        int color = baseColor;
        class_4597.class_4598 provider = class_4597.method_22991((class_9799)this.allocator);
        this.pass(provider, matrices, cam, billboard, target, pos, centerY, t, count, baseSize, alpha01, color, true);
        this.pass(provider, matrices, cam, billboard, target, pos, centerY, t, count, baseSize, alpha01, color, false);
    }

    private void pass(class_4597.class_4598 provider, class_4587 matrices, class_243 cam, Quaternionf billboard, class_1309 target, class_243 pos, float centerY, float t, int count, float baseSize, float alpha01, int color, boolean glowPass) {
        class_2960 texture = glowPass ? class_2960.method_60655((String)"decide", (String)"textures/targetrender/particles/glow.png") : TEXTURES[this.textureIndex];
        class_4588 buf = provider.method_73477(this.layer(texture));
        for (int i = 0; i < count; ++i) {
            float pulse;
            float[] p = TargetSphere.pointAt(i, count, t);
            float scale = 0.95f;
            float wx = (float)(pos.field_1352 + (double)(p[0] * scale));
            float wy = centerY + p[1] * scale;
            float wz = (float)(pos.field_1350 + (double)(p[2] * scale));
            float heightNorm = p[1] / Math.max(0.001f, 0.95f) * 0.5f + 0.5f;
            float size = baseSize * (0.78f + 0.22f * heightNorm) * (pulse = 0.75f + 0.25f * (float)Math.sin(t * 1.2f + (float)i * 0.17f));
            if (size <= 0.001f) continue;
            float particleAlpha = alpha01 * (0.58f + 0.22f * pulse);
            int c = glowPass ? TargetSphere.withAlpha(color, particleAlpha * (0.16f + 0.1f * pulse)) : TargetSphere.withAlpha(color, particleAlpha);
            matrices.method_22903();
            matrices.method_22904((double)wx - cam.field_1352, (double)wy - cam.field_1351, (double)wz - cam.field_1350);
            matrices.method_22907((Quaternionfc)billboard);
            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(180.0f));
            Matrix4f mat = matrices.method_23760().method_23761();
            TargetSphere.quad(buf, mat, glowPass ? size * 1.45f * 0.5f : size * 0.5f, c);
            matrices.method_22909();
        }
        provider.method_22993();
    }

    private static int withAlpha(int color, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha * 255.0f)));
        return a << 24 | color & 0xFFFFFF;
    }

    private static void quad(class_4588 buf, Matrix4f mat, float half, int color) {
        float a = (float)(color >>> 24) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        buf.method_22918((Matrix4fc)mat, -half, -half, 0.0f).method_22913(0.0f, 0.0f).method_22915(r, g, b, a);
        buf.method_22918((Matrix4fc)mat, half, -half, 0.0f).method_22913(1.0f, 0.0f).method_22915(r, g, b, a);
        buf.method_22918((Matrix4fc)mat, half, half, 0.0f).method_22913(1.0f, 1.0f).method_22915(r, g, b, a);
        buf.method_22918((Matrix4fc)mat, -half, half, 0.0f).method_22913(0.0f, 1.0f).method_22915(r, g, b, a);
    }

    private class_1921 layer(class_2960 texture) {
        return this.layers.computeIfAbsent(texture, id -> class_1921.method_75940((String)"targetrender_sphere", (class_12247)class_12247.method_75927((RenderPipeline)PIPELINE).method_75934("Sampler0", id).method_75937().method_75929(131072).method_75938()));
    }

    public void close() {
        this.layers.clear();
    }
}

