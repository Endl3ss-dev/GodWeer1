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
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_7833
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render.targeteffects;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.decide.module.impl.render.targeteffects.TargetEffectFrame;
import ru.decide.module.impl.render.targeteffects.TargetEffectPalette;

@Environment(value=EnvType.CLIENT)
public final class TargetEffectRender {
    private static final class_1921 SOLID = TargetEffectRender.solid();
    private static final class_1921 GLOW = TargetEffectRender.textured();
    private static final float[] CURTAIN_HEIGHTS = new float[]{0.0f, 0.1f, 0.45f, 1.0f};
    private static final float[] CURTAIN_FADE = new float[]{1.0f, 0.7f, 0.3f, 0.0f};
    static final int[] STAR = new int[]{16, 56, 254, 124, 56, 40, 68, 130};
    static final int[] HEART = new int[]{102, 255, 255, 255, 126, 60, 24, 0};
    static final int[] ARROW = new int[]{24, 24, 24, 24, 254, 124, 56, 16};
    static final int[] CRIT = new int[]{16, 16, 56, 254, 56, 16, 16, 0};
    static final int[] SPARKLE = new int[]{16, 56, 124, 254, 124, 56, 16, 0};

    private TargetEffectRender() {
    }

    private static class_1921 solid() {
        RenderPipeline pipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/target_effects_solid")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        return class_1921.method_75940((String)"target_effects_solid", (class_12247)class_12247.method_75927((RenderPipeline)pipeline).method_75937().method_75929(65536).method_75938());
    }

    private static class_1921 textured() {
        RenderPipeline pipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/target_effects_glow")).withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withSampler("Sampler0").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).build());
        return class_1921.method_75940((String)"target_effects_glow", (class_12247)class_12247.method_75927((RenderPipeline)pipeline).method_75934("Sampler0", class_2960.method_60655((String)"decide", (String)"textures/particles/glow.png")).method_75937().method_75929(32768).method_75938());
    }

    public static class_4588 solid(TargetEffectFrame frame) {
        return frame.provider.method_73477(SOLID);
    }

    public static int argb(int rgb, float alpha) {
        int a = Math.round(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f);
        return a << 24 | rgb & 0xFFFFFF;
    }

    public static void quad(class_4588 buf, Matrix4f matrix, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, int color) {
        if (color >>> 24 == 0) {
            return;
        }
        buf.method_22918((Matrix4fc)matrix, (float)x1, (float)y1, (float)z1).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, (float)x2, (float)y2, (float)z2).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, (float)x3, (float)y3, (float)z3).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, (float)x4, (float)y4, (float)z4).method_39415(color);
    }

    public static void gradientQuad(class_4588 buf, Matrix4f matrix, double x1, double y1, double z1, int c1, double x2, double y2, double z2, int c2, double x3, double y3, double z3, int c3, double x4, double y4, double z4, int c4) {
        buf.method_22918((Matrix4fc)matrix, (float)x1, (float)y1, (float)z1).method_39415(c1);
        buf.method_22918((Matrix4fc)matrix, (float)x2, (float)y2, (float)z2).method_39415(c2);
        buf.method_22918((Matrix4fc)matrix, (float)x3, (float)y3, (float)z3).method_39415(c3);
        buf.method_22918((Matrix4fc)matrix, (float)x4, (float)y4, (float)z4).method_39415(c4);
    }

    public static void line(class_4588 buf, Matrix4f matrix, double x1, double y1, double z1, double x2, double y2, double z2, int color, float width) {
        if (color >>> 24 == 0 || width <= 0.0f) {
            return;
        }
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        if (dx * dx + dy * dy + dz * dz < 1.0E-10) {
            return;
        }
        double sx = dy * z1 - dz * y1;
        double sy = dz * x1 - dx * z1;
        double sz = dx * y1 - dy * x1;
        double length = Math.sqrt(sx * sx + sy * sy + sz * sz);
        if (length < 1.0E-6) {
            sx = -dy;
            sy = dx;
            sz = 0.0;
            length = Math.sqrt(sx * sx + sy * sy);
            if (length < 1.0E-6) {
                sx = 1.0;
                sy = 0.0;
                sz = 0.0;
                length = 1.0;
            }
        }
        sx = sx / length * (double)width;
        sy = sy / length * (double)width;
        sz = sz / length * (double)width;
        buf.method_22918((Matrix4fc)matrix, (float)(x1 - sx), (float)(y1 - sy), (float)(z1 - sz)).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, (float)(x1 + sx), (float)(y1 + sy), (float)(z1 + sz)).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, (float)(x2 + sx), (float)(y2 + sy), (float)(z2 + sz)).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, (float)(x2 - sx), (float)(y2 - sy), (float)(z2 - sz)).method_39415(color);
    }

    public static void disc(class_4588 buf, Matrix4f matrix, double cx, double cy, double cz, float radius, int center, int edge, int segments) {
        if (radius <= 0.0f) {
            return;
        }
        for (int i = 0; i < segments; ++i) {
            double a0 = Math.PI * 2 * (double)i / (double)segments;
            double a1 = Math.PI * 2 * (double)(i + 1) / (double)segments;
            TargetEffectRender.gradientQuad(buf, matrix, cx, cy, cz, center, cx + Math.cos(a0) * (double)radius, cy, cz + Math.sin(a0) * (double)radius, edge, cx + Math.cos(a1) * (double)radius, cy, cz + Math.sin(a1) * (double)radius, edge, cx, cy, cz, center);
        }
    }

    public static void ring(class_4588 buf, Matrix4f matrix, double cx, double cy, double cz, float inner, float outer, int color, int segments) {
        if (color >>> 24 == 0 || outer <= inner) {
            return;
        }
        for (int i = 0; i < segments; ++i) {
            double a0 = Math.PI * 2 * (double)i / (double)segments;
            double a1 = Math.PI * 2 * (double)(i + 1) / (double)segments;
            double x0 = Math.cos(a0);
            double z0 = Math.sin(a0);
            double x1 = Math.cos(a1);
            double z1 = Math.sin(a1);
            TargetEffectRender.quad(buf, matrix, cx + x0 * (double)inner, cy, cz + z0 * (double)inner, cx + x0 * (double)outer, cy, cz + z0 * (double)outer, cx + x1 * (double)outer, cy, cz + z1 * (double)outer, cx + x1 * (double)inner, cy, cz + z1 * (double)inner, color);
        }
    }

    public static void curtain(class_4588 buf, Matrix4f matrix, double x0, double z0, double x1, double z1, double baseY, float height0, float height1, int color0, int color1, float alpha) {
        for (int i = 0; i < CURTAIN_HEIGHTS.length - 1; ++i) {
            double y00 = baseY + (double)(height0 * CURTAIN_HEIGHTS[i]);
            double y01 = baseY + (double)(height0 * CURTAIN_HEIGHTS[i + 1]);
            double y10 = baseY + (double)(height1 * CURTAIN_HEIGHTS[i]);
            double y11 = baseY + (double)(height1 * CURTAIN_HEIGHTS[i + 1]);
            float fade0 = CURTAIN_FADE[i] * alpha;
            float fade1 = CURTAIN_FADE[i + 1] * alpha;
            TargetEffectRender.gradientQuad(buf, matrix, x0, y00, z0, TargetEffectRender.argb(color0, fade0), x1, y10, z1, TargetEffectRender.argb(color1, fade0), x1, y11, z1, TargetEffectRender.argb(color1, fade1), x0, y01, z0, TargetEffectRender.argb(color0, fade1));
        }
    }

    public static void glow(class_4587 matrices, TargetEffectFrame frame, double x, double y, double z, float size, int rgb, float alpha, float spin) {
        if (alpha <= 0.002f || size <= 0.0f) {
            return;
        }
        matrices.method_22903();
        matrices.method_46416((float)x, (float)y, (float)z);
        matrices.method_22907((Quaternionfc)frame.cameraRotation);
        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(spin));
        matrices.method_22905(size, size, 1.0f);
        Matrix4f matrix = matrices.method_23760().method_23761();
        class_4588 buf = frame.provider.method_73477(GLOW);
        int color = TargetEffectRender.argb(rgb, alpha);
        buf.method_22918((Matrix4fc)matrix, -1.0f, -1.0f, 0.0f).method_22913(0.0f, 1.0f).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, -1.0f, 1.0f, 0.0f).method_22913(0.0f, 0.0f).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, 1.0f, 1.0f, 0.0f).method_22913(1.0f, 0.0f).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, 1.0f, -1.0f, 0.0f).method_22913(1.0f, 1.0f).method_39415(color);
        matrices.method_22909();
    }

    public static void pixel(class_4587 matrices, TargetEffectFrame frame, int[] rows, double x, double y, double z, float size, float spin, int rgb, float alpha) {
        if (alpha <= 0.002f || size <= 0.0f) {
            return;
        }
        matrices.method_22903();
        matrices.method_46416((float)x, (float)y, (float)z);
        matrices.method_22907((Quaternionfc)frame.cameraRotation);
        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(spin));
        matrices.method_22905(size, size, 1.0f);
        TargetEffectRender.drawBitmap(frame.provider.method_73477(SOLID), matrices.method_23760().method_23761(), rows, rgb, alpha);
        matrices.method_22909();
    }

    public static void worldPixel(TargetEffectFrame frame, int[] rows, double x, double y, double z, float alongX, float alongZ, float size, int rgb, float alpha) {
        if (alpha <= 0.002f || size <= 0.0f) {
            return;
        }
        float length = class_3532.method_15355((float)(alongX * alongX + alongZ * alongZ));
        if (length < 1.0E-4f) {
            return;
        }
        float depth = size * 0.125f;
        float acrossX = -(alongZ /= length);
        float acrossZ = alongX /= length;
        for (int side = -1; side <= 1; side += 2) {
            TargetEffectRender.drawWorldPixel(frame, rows, x + (double)(acrossX * (float)side * depth), y, z + (double)(acrossZ * (float)side * depth), alongX, alongZ, size, rgb, alpha);
        }
    }

    private static void drawWorldPixel(TargetEffectFrame frame, int[] rows, double x, double y, double z, float alongX, float alongZ, float size, int rgb, float alpha) {
        float cell = size * 2.0f / 8.0f;
        float half = cell * 0.5f;
        class_4588 buf = frame.provider.method_73477(SOLID);
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        int color = TargetEffectRender.argb(rgb, alpha);
        int core = TargetEffectRender.argb(0xFFFFFF, alpha * 0.85f);
        for (int row = 0; row < rows.length; ++row) {
            int bits = rows[row];
            for (int col = 0; col < 8; ++col) {
                if ((bits & 1 << 7 - col) == 0) continue;
                float u = ((float)col + 0.5f - 4.0f) * cell;
                float v = (3.5f - (float)row) * cell;
                double px = x + (double)(alongX * u);
                double pz = z + (double)(alongZ * u);
                double py = y + (double)v;
                int cellColor = row >= 2 && row <= 3 && col >= 2 && col <= 3 ? core : color;
                TargetEffectRender.quad(buf, matrix, px - (double)(alongX * half), py - (double)half, pz - (double)(alongZ * half), px + (double)(alongX * half), py - (double)half, pz + (double)(alongZ * half), px + (double)(alongX * half), py + (double)half, pz + (double)(alongZ * half), px - (double)(alongX * half), py + (double)half, pz - (double)(alongZ * half), cellColor);
            }
        }
    }

    public static void pixelRing(class_4588 buf, Matrix4f matrix, double cx, double cy, double cz, float radius, float thickness, float cell, float threshold, float alpha, float phase, TargetEffectPalette palette) {
        float outer = radius + thickness * 0.5f;
        float inner = Math.max(0.0f, radius - thickness * 0.5f);
        int cells = class_3532.method_15386((float)(outer / cell)) + 1;
        for (int ix = -cells; ix <= cells; ++ix) {
            float lx = ((float)ix + 0.5f) * cell;
            for (int iz = -cells; iz <= cells; ++iz) {
                float lz = ((float)iz + 0.5f) * cell;
                float distance = class_3532.method_15355((float)(lx * lx + lz * lz));
                if (distance < inner || distance > outer || TargetEffectRender.dither(ix, iz) < threshold) continue;
                float angle = (float)(Math.atan2(lz, lx) / (Math.PI * 2));
                int color = TargetEffectRender.argb(palette.at(phase + angle), alpha * 0.8f);
                TargetEffectRender.quad(buf, matrix, cx + (double)lx - (double)(cell * 0.5f), cy, cz + (double)lz - (double)(cell * 0.5f), cx + (double)lx + (double)(cell * 0.5f), cy, cz + (double)lz - (double)(cell * 0.5f), cx + (double)lx + (double)(cell * 0.5f), cy, cz + (double)lz + (double)(cell * 0.5f), cx + (double)lx - (double)(cell * 0.5f), cy, cz + (double)lz + (double)(cell * 0.5f), color);
            }
        }
    }

    private static void drawBitmap(class_4588 buf, Matrix4f matrix, int[] rows, int rgb, float alpha) {
        float cell = 0.25f;
        int color = TargetEffectRender.argb(rgb, alpha);
        int core = TargetEffectRender.argb(0xFFFFFF, alpha * 0.85f);
        for (int row = 0; row < rows.length; ++row) {
            int bits = rows[row];
            for (int col = 0; col < 8; ++col) {
                if ((bits & 1 << 7 - col) == 0) continue;
                float x0 = -1.0f + (float)col * cell;
                float y0 = 1.0f - (float)(row + 1) * cell;
                int cellColor = row >= 2 && row <= 3 && col >= 2 && col <= 3 ? core : color;
                TargetEffectRender.quad(buf, matrix, x0, y0, 0.0, x0 + cell, y0, 0.0, x0 + cell, y0 + cell, 0.0, x0, y0 + cell, 0.0, cellColor);
            }
        }
    }

    private static float dither(int x, int z) {
        return (float)((x * 73856093 ^ z * 19349663) >>> 8 & 0xFF) / 256.0f;
    }
}

