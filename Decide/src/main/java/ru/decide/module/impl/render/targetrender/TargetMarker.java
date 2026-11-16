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
 *  net.minecraft.class_332
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_7833
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render.targetrender;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
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
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.decide.utils.other.Projection;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public final class TargetMarker {
    private static final TargetMarker INSTANCE = new TargetMarker();
    private static final String[] MARKER_MODES = new String[]{"\u041a\u0432\u0430\u0434\u0440\u0430\u0442", "\u0422\u0440\u0435\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a", "\u041a\u0440\u0443\u0433"};
    private static final class_2960[] MARKER_TEXTURES = new class_2960[]{class_2960.method_60655((String)"decide", (String)"textures/targetrender/markers/square.png"), class_2960.method_60655((String)"decide", (String)"textures/targetrender/markers/triangle.png"), class_2960.method_60655((String)"decide", (String)"textures/targetrender/markers/circle.png")};
    private final class_9799 allocator = new class_9799(4096);
    private class_4597.class_4598 provider = class_4597.method_22991((class_9799)this.allocator);
    private class_1921 cachedLayer;
    private class_2960 cachedTexture;
    private class_4588 buf;
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/world/targetrender_marker")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());

    private TargetMarker() {
    }

    public static TargetMarker get() {
        return INSTANCE;
    }

    public static int textureIndexFor(String mode) {
        for (int i = 0; i < MARKER_MODES.length; ++i) {
            if (!MARKER_MODES[i].equals(mode)) continue;
            return i;
        }
        return 0;
    }

    private static float sizeFor(class_332 context, class_1309 target, float baseSize, float tickDelta) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1773 == null || mc.field_1773.method_19418() == null) {
            return 0.0f;
        }
        double dist = mc.field_1773.method_19418().method_71156().method_1022(target.method_30950(tickDelta));
        if (dist < 0.01) {
            return 0.0f;
        }
        float fov = TargetMarker.currentFovDegrees(mc);
        float scale = fov == 70.0f ? 1.0f : fov / 70.0f;
        float sizeBase = 12.0f + (baseSize * 2.0f - 3.0f);
        return (float)(Math.max(10.0, 500.0 / dist) * (double)(sizeBase / 50.0f) / (double)scale);
    }

    private static float currentFovDegrees(class_310 mc) {
        try {
            Matrix4f proj = mc.field_1773.method_22973(((Integer)mc.field_1690.method_41808().method_41753()).floatValue());
            float p11 = proj.m11();
            if (p11 <= 1.0E-6f) {
                return 70.0f;
            }
            return (float)Math.toDegrees(2.0 * Math.atan(1.0 / (double)p11));
        }
        catch (Throwable ignored) {
            return 70.0f;
        }
    }

    private static float rotationFor(boolean spin, float speed, float amplitudeDeg) {
        if (!spin) {
            return 0.0f;
        }
        double safeSpeed = Math.max(0.1, (double)speed);
        double period = 450.0 / safeSpeed;
        return (float)(Math.sin((double)System.currentTimeMillis() / period) * (double)amplitudeDeg);
    }

    public void render(class_332 context, class_1309 target, float tickDelta, float fade, String markerMode, float sizeSetting, boolean spin, float spinSpeed, float spinAmplitude, int baseColor) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1773 == null || mc.field_1773.method_19418() == null) {
            return;
        }
        if (fade <= 0.01f || context == null) {
            return;
        }
        float size = TargetMarker.sizeFor(context, target, sizeSetting, tickDelta) * fade;
        if (size <= 0.01f) {
            return;
        }
        class_243 pos = target.method_30950(tickDelta);
        class_243 above = new class_243(pos.field_1352, pos.field_1351 + (double)target.method_17682() / 2.0, pos.field_1350);
        class_243 screen = Projection.worldSpaceToScreenSpace(above);
        if (screen.field_1350 < 0.0) {
            return;
        }
        float x = (float)screen.field_1352;
        float y = (float)screen.field_1351;
        float offsetY = "\u0422\u0440\u0435\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a".equals(markerMode) ? size * -0.1f : 0.0f;
        float rotation = TargetMarker.rotationFor(spin, spinSpeed, spinAmplitude);
        float half = size / 2.0f;
        int color = TargetMarker.withAlpha(baseColor, fade);
        Draw.textureRotated(TargetMarker.identifierFor(markerMode), x - half, y - half + offsetY, size, size, rotation, color);
    }

    public void renderWorld(class_4587 matrices, class_1309 target, float tickDelta, float fade, String markerMode, float sizeSetting, boolean spin, float spinSpeed, float spinAmplitude, int baseColor) {
        int screenHeight;
        float fov;
        class_310 mc = class_310.method_1551();
        if (mc.field_1773 == null || mc.field_1773.method_19418() == null) {
            return;
        }
        if (fade <= 0.01f) {
            return;
        }
        class_243 pos = target.method_30950(tickDelta);
        class_243 cam = mc.field_1773.method_19418().method_71156();
        double distance = cam.method_1022(pos);
        if (distance < 0.01) {
            return;
        }
        float pixels = TargetMarker.sizeFor(null, target, sizeSetting, tickDelta) * fade;
        float worldSize = Draw.worldSizeForPixels(pixels, distance, fov = TargetMarker.currentFovDegrees(mc), screenHeight = mc.method_22683().method_4502());
        if (worldSize <= 0.001f) {
            return;
        }
        float offsetY = "\u0422\u0440\u0435\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a".equals(markerMode) ? worldSize * -0.1f : 0.0f;
        float rotation = TargetMarker.rotationFor(spin, spinSpeed, spinAmplitude);
        class_2960 texture = TargetMarker.identifierFor(markerMode);
        if (this.cachedLayer == null || !texture.equals((Object)this.cachedTexture)) {
            this.cachedTexture = texture;
            this.cachedLayer = class_1921.method_75940((String)"targetrender_marker", (class_12247)class_12247.method_75927((RenderPipeline)PIPELINE).method_75934("Sampler0", texture).method_75937().method_75929(4096).method_75938());
        }
        this.buf = this.provider.method_73477(this.cachedLayer);
        float half = worldSize / 2.0f;
        int color = TargetMarker.withAlpha(baseColor, fade);
        float a = (float)(color >>> 24) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        matrices.method_22903();
        matrices.method_22904(pos.field_1352 - cam.field_1352, pos.field_1351 + (double)target.method_17682() * 0.5 - cam.field_1351, pos.field_1350 - cam.field_1350);
        matrices.method_22907((Quaternionfc)mc.field_1773.method_19418().method_23767());
        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(180.0f));
        if (rotation != 0.0f) {
            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(rotation));
        }
        matrices.method_46416(0.0f, offsetY, 0.0f);
        Matrix4f mat = matrices.method_23760().method_23761();
        this.buf.method_22918((Matrix4fc)mat, -half, -half, 0.0f).method_22913(0.0f, 0.0f).method_22915(r, g, b, a);
        this.buf.method_22918((Matrix4fc)mat, half, -half, 0.0f).method_22913(1.0f, 0.0f).method_22915(r, g, b, a);
        this.buf.method_22918((Matrix4fc)mat, half, half, 0.0f).method_22913(1.0f, 1.0f).method_22915(r, g, b, a);
        this.buf.method_22918((Matrix4fc)mat, -half, half, 0.0f).method_22913(0.0f, 1.0f).method_22915(r, g, b, a);
        matrices.method_22909();
        this.provider.method_22993();
    }

    private static class_2960 identifierFor(String mode) {
        return MARKER_TEXTURES[TargetMarker.textureIndexFor(mode)];
    }

    private static int withAlpha(int color, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f)));
        return a << 24 | color & 0xFFFFFF;
    }
}

