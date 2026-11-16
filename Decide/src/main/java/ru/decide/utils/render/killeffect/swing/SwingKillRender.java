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
 *  net.minecraft.class_4588
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.decide.utils.render.killeffect.swing;

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
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public final class SwingKillRender {
    private static final class_1921 ADDITIVE = SwingKillRender.layer("swing_kill_additive", BlendFunction.LIGHTNING);
    private static final class_1921 SOLID = SwingKillRender.layer("swing_kill_solid", BlendFunction.TRANSLUCENT);

    private SwingKillRender() {
    }

    public static class_1921 additive() {
        return ADDITIVE;
    }

    public static class_1921 solid() {
        return SOLID;
    }

    public static int argb(int rgb, float alpha) {
        int a = Math.round(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f);
        return a << 24 | rgb & 0xFFFFFF;
    }

    public static int alpha(int color, float mul) {
        int a = Math.round((float)(color >>> 24 & 0xFF) * class_3532.method_15363((float)mul, (float)0.0f, (float)1.0f));
        return a << 24 | color & 0xFFFFFF;
    }

    public static int lerp(int from, int to, float t) {
        float f = class_3532.method_15363((float)t, (float)0.0f, (float)1.0f);
        int a = class_3532.method_48781((float)f, (int)(from >>> 24 & 0xFF), (int)(to >>> 24 & 0xFF));
        int r = class_3532.method_48781((float)f, (int)(from >> 16 & 0xFF), (int)(to >> 16 & 0xFF));
        int g = class_3532.method_48781((float)f, (int)(from >> 8 & 0xFF), (int)(to >> 8 & 0xFF));
        int b = class_3532.method_48781((float)f, (int)(from & 0xFF), (int)(to & 0xFF));
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int brighten(int color, float mul) {
        int r = class_3532.method_15340((int)((int)((float)(color >> 16 & 0xFF) * mul)), (int)0, (int)255);
        int g = class_3532.method_15340((int)((int)((float)(color >> 8 & 0xFF) * mul)), (int)0, (int)255);
        int b = class_3532.method_15340((int)((int)((float)(color & 0xFF) * mul)), (int)0, (int)255);
        return color & 0xFF000000 | r << 16 | g << 8 | b;
    }

    public static void line(class_4588 buf, Matrix4f mat, double x1, double y1, double z1, double x2, double y2, double z2, int color, float width) {
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
        double sl = Math.sqrt(sx * sx + sy * sy + sz * sz);
        if (sl < 1.0E-6) {
            sx = -dy;
            sy = dx;
            sz = 0.0;
            sl = Math.sqrt(sx * sx + sy * sy);
            if (sl < 1.0E-6) {
                sx = 1.0;
                sy = 0.0;
                sz = 0.0;
                sl = 1.0;
            }
        }
        sx = sx / sl * (double)width;
        sy = sy / sl * (double)width;
        sz = sz / sl * (double)width;
        buf.method_22918((Matrix4fc)mat, (float)(x1 - sx), (float)(y1 - sy), (float)(z1 - sz)).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)(x1 + sx), (float)(y1 + sy), (float)(z1 + sz)).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)(x2 + sx), (float)(y2 + sy), (float)(z2 + sz)).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)(x2 - sx), (float)(y2 - sy), (float)(z2 - sz)).method_39415(color);
    }

    public static void quad(class_4588 buf, Matrix4f mat, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, int color) {
        if (color >>> 24 == 0) {
            return;
        }
        buf.method_22918((Matrix4fc)mat, (float)x1, (float)y1, (float)z1).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)x2, (float)y2, (float)z2).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)x3, (float)y3, (float)z3).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)x4, (float)y4, (float)z4).method_39415(color);
    }

    public static void triangle(class_4588 buf, Matrix4f mat, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, int color) {
        if (color >>> 24 == 0) {
            return;
        }
        buf.method_22918((Matrix4fc)mat, (float)x1, (float)y1, (float)z1).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)x2, (float)y2, (float)z2).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)x3, (float)y3, (float)z3).method_39415(color);
        buf.method_22918((Matrix4fc)mat, (float)x3, (float)y3, (float)z3).method_39415(color);
    }

    public static void box(class_4588 buf, Matrix4f mat, double cx, double cy, double cz, float hx, float hy, float hz, float yaw, int topColor, int sideColor, int bottomColor) {
        double cos = Math.cos(yaw);
        double sin = Math.sin(yaw);
        double[][] local = new double[][]{{-hx, -hy, -hz}, {hx, -hy, -hz}, {hx, -hy, hz}, {-hx, -hy, hz}, {-hx, hy, -hz}, {hx, hy, -hz}, {hx, hy, hz}, {-hx, hy, hz}};
        double[] wx = new double[8];
        double[] wy = new double[8];
        double[] wz = new double[8];
        for (int i = 0; i < 8; ++i) {
            double lx = local[i][0];
            double lz = local[i][2];
            wx[i] = cx + lx * cos + lz * sin;
            wy[i] = cy + local[i][1];
            wz[i] = cz - lx * sin + lz * cos;
        }
        SwingKillRender.quad(buf, mat, wx[4], wy[4], wz[4], wx[5], wy[5], wz[5], wx[6], wy[6], wz[6], wx[7], wy[7], wz[7], topColor);
        SwingKillRender.quad(buf, mat, wx[0], wy[0], wz[0], wx[3], wy[3], wz[3], wx[2], wy[2], wz[2], wx[1], wy[1], wz[1], bottomColor);
        SwingKillRender.quad(buf, mat, wx[0], wy[0], wz[0], wx[1], wy[1], wz[1], wx[5], wy[5], wz[5], wx[4], wy[4], wz[4], sideColor);
        SwingKillRender.quad(buf, mat, wx[1], wy[1], wz[1], wx[2], wy[2], wz[2], wx[6], wy[6], wz[6], wx[5], wy[5], wz[5], sideColor);
        SwingKillRender.quad(buf, mat, wx[2], wy[2], wz[2], wx[3], wy[3], wz[3], wx[7], wy[7], wz[7], wx[6], wy[6], wz[6], sideColor);
        SwingKillRender.quad(buf, mat, wx[3], wy[3], wz[3], wx[0], wy[0], wz[0], wx[4], wy[4], wz[4], wx[7], wy[7], wz[7], sideColor);
    }

    public static void ring(class_4588 buf, Matrix4f mat, double cx, double cy, double cz, float radius, float thickness, int color, int segments) {
        if (color >>> 24 == 0 || radius <= thickness) {
            return;
        }
        float inner = radius - thickness;
        for (int i = 0; i < segments; ++i) {
            double a0 = Math.PI * 2 * (double)i / (double)segments;
            double a1 = Math.PI * 2 * (double)(i + 1) / (double)segments;
            double x0 = Math.cos(a0);
            double z0 = Math.sin(a0);
            double x1 = Math.cos(a1);
            double z1 = Math.sin(a1);
            buf.method_22918((Matrix4fc)mat, (float)(cx + x0 * (double)inner), (float)cy, (float)(cz + z0 * (double)inner)).method_39415(color);
            buf.method_22918((Matrix4fc)mat, (float)(cx + x0 * (double)radius), (float)cy, (float)(cz + z0 * (double)radius)).method_39415(color);
            buf.method_22918((Matrix4fc)mat, (float)(cx + x1 * (double)radius), (float)cy, (float)(cz + z1 * (double)radius)).method_39415(color);
            buf.method_22918((Matrix4fc)mat, (float)(cx + x1 * (double)inner), (float)cy, (float)(cz + z1 * (double)inner)).method_39415(color);
        }
    }

    public static void disc(class_4588 buf, Matrix4f mat, double cx, double cy, double cz, float radius, int center, int edge, int segments) {
        if (radius <= 0.0f) {
            return;
        }
        for (int i = 0; i < segments; ++i) {
            double a0 = Math.PI * 2 * (double)i / (double)segments;
            double a1 = Math.PI * 2 * (double)(i + 1) / (double)segments;
            buf.method_22918((Matrix4fc)mat, (float)cx, (float)cy, (float)cz).method_39415(center);
            buf.method_22918((Matrix4fc)mat, (float)(cx + Math.cos(a0) * (double)radius), (float)cy, (float)(cz + Math.sin(a0) * (double)radius)).method_39415(edge);
            buf.method_22918((Matrix4fc)mat, (float)(cx + Math.cos(a1) * (double)radius), (float)cy, (float)(cz + Math.sin(a1) * (double)radius)).method_39415(edge);
            buf.method_22918((Matrix4fc)mat, (float)cx, (float)cy, (float)cz).method_39415(center);
        }
    }

    public static void cross(class_4588 buf, Matrix4f mat, double cx, double cy, double cz, float half, int color) {
        SwingKillRender.line(buf, mat, cx - (double)half, cy, cz, cx + (double)half, cy, cz, color, 0.008f);
        SwingKillRender.line(buf, mat, cx, cy - (double)half, cz, cx, cy + (double)half, cz, color, 0.008f);
        SwingKillRender.line(buf, mat, cx, cy, cz - (double)half, cx, cy, cz + (double)half, color, 0.008f);
    }

    private static class_1921 layer(String name, BlendFunction blend) {
        RenderPipeline pipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)name)).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(blend).build());
        return class_1921.method_75940((String)name, (class_12247)class_12247.method_75927((RenderPipeline)pipeline).method_75937().method_75929(65536).method_75938());
    }
}

