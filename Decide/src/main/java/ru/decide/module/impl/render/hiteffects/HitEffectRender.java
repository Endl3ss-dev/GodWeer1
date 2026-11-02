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
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_7833
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package ru.decide.module.impl.render.hiteffects;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
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
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public final class HitEffectRender {
    private static final int FROM = -45715;
    private static final int TO = -11930;
    private static final class_1921 SOLID = HitEffectRender.solid();
    private static final class_1921 GLOW = HitEffectRender.glow();
    private static final class_1921 TRAIL = HitEffectRender.trail();
    static final int[] CRIT = new int[]{16, 16, 56, 254, 56, 16, 16, 0};
    static final int[] HEART = new int[]{102, 255, 255, 255, 126, 60, 24, 0};
    static final int[] STAR = new int[]{16, 56, 254, 124, 56, 40, 68, 130};
    private static final String[] BURST = new String[]{"                                ", "              ..                ", "              ..                ", "              .#.               ", "             .##.               ", "             .##.   ...         ", "        ..   .###. .##.         ", "        .#.. .####.##.          ", "         .##.########.          ", "         .###########.  .....   ", "    ...  .############..###.    ", "   .###..#################.     ", "    .####################.      ", "     .##################.       ", "      .#################.       ", "       .#################....   ", "     ..######################.. ", "    .#######################... ", "   .####################....    ", "    ....###############.        ", "        .###############.       ", "       .#################.      ", "       .#############..##.      ", "      .###..####.###.  ...      ", "      .##.  .##. .##.           ", "     .#..   .##. .###.          ", "     ..      ..   .##.          ", "             .    .##.          ", "                   .#.          ", "                    .           ", "                                ", "                                "};
    private static final String[] WORDS = new String[]{"POW", "BAM", "ZAP", "OOF"};
    private static final String[][] COMIC_WORDS = new String[][]{{"    ..........    ........  ....    ........    ", "    .########.    .######.  .##.    .##..##.    ", "    .########......######....##.    .##..##.    ", "    .##......##..##......##..##.    .##..##.    ", "    .##.    .##..##.    .##..##.    .##..##.    ", "    .##.    .##..##.    .##..##.    .##..##.    ", "    .##......##..##.    .##..##......##..##.    ", "    .########....##.    .##..##..##..##..##.    ", "    .########.  .##.    .##..##..##..##..##.    ", "    .YY.......  .YY.    .YY..YY..YY..YY..YY.    ", "    .YY.        .YY.    .YY..YY..YY..YY..YY.    ", "    .YY.        .YY.    .YY..YYYY..YYYY.....    ", "    .YY.        .YY......YY..YYYY..YYYY.....    ", "    .YY.        ...YYYYYY....YY......YY..YY.    ", "    .YY.          .YYYYYY.  .YY.    .YY..YY.    ", "    ....          ........  ....    ........    "}, {"    ..........    ........  ....    ........    ", "    .########.    .######.  .##.    .##..##.    ", "    .########......######....##......##..##.    ", "    .##......##..##......##..####..####..##.    ", "    .##.    .##..##.    .##..####..####..##.    ", "    .##.    .##..##.    .##..##..##..##..##.    ", "    .##......##..##......##..##..##..##..##.    ", "    .########....##########..##..##..##..##.    ", "    .########....##########..##..##..##..##.    ", "    .YY......YY..YY......YY..YY......YY..YY.    ", "    .YY.    .YY..YY.    .YY..YY.    .YY..YY.    ", "    .YY.    .YY..YY.    .YY..YY.    .YY.....    ", "    .YY......YY..YY.    .YY..YY.    .YY.....    ", "    .YYYYYYYY....YY.    .YY..YY.    .YY..YY.    ", "    .YYYYYYYY.  .YY.    .YY..YY.    .YY..YY.    ", "    ..........  ....    ........    ........    "}, {"    ............  ........  ..........  ....    ", "    .##########.  .######.  .########.  .##.    ", "    .##########....######....########....##.    ", "    .........##..##......##..##......##..##.    ", "          ...##..##.    .##..##.    .##..##.    ", "          .##....##.    .##..##.    .##..##.    ", "        ...##.  .##......##..##......##..##.    ", "        .##...  .##########..########....##.    ", "      ...##.    .##########..########.  .##.    ", "      .YY...    .YY......YY..YY.......  .YY.    ", "    ...YY.      .YY.    .YY..YY.        .YY.    ", "    .YY...      .YY.    .YY..YY.        ....    ", "    .YY..........YY.    .YY..YY.        ....    ", "    .YYYYYYYYYY..YY.    .YY..YY.        .YY.    ", "    .YYYYYYYYYY..YY.    .YY..YY.        .YY.    ", "    ................    ........        ....    "}, {"      ........    ........  ................    ", "      .######.    .######.  .##########..##.    ", "    ...######......######....##########..##.    ", "    .##......##..##......##..##..........##.    ", "    .##.    .##..##.    .##..##.        .##.    ", "    .##.    .##..##.    .##..##.        .##.    ", "    .##.    .##..##.    .##..##.......  .##.    ", "    .##.    .##..##.    .##..########.  .##.    ", "    .##.    .##..##.    .##..########.  .##.    ", "    .YY.    .YY..YY.    .YY..YY.......  .YY.    ", "    .YY.    .YY..YY.    .YY..YY.        .YY.    ", "    .YY.    .YY..YY.    .YY..YY.        ....    ", "    .YY......YY..YY......YY..YY.        ....    ", "    ...YYYYYY......YYYYYY....YY.        .YY.    ", "      .YYYYYY.    .YYYYYY.  .YY.        .YY.    ", "      ........    ........  ....        ....    "}};
    private static final float[] TRAIL_RINGS = new float[]{0.0f, 0.3f, 1.0f};
    private static final float[] TRAIL_FALLOFF = new float[]{1.0f, 0.5f, 0.0f};
    private static final float[] SIDES = new float[]{-1.0f, 1.0f};

    private HitEffectRender() {
    }

    static Frame frame(class_4587 matrices, class_4597.class_4598 provider, class_243 camera, Quaternionf rotation, float partialTick) {
        return new Frame(matrices, provider, camera, rotation, partialTick);
    }

    static int color(float t) {
        return ColorUtil.overCol(-45715, -11930, 0.5f - 0.5f * class_3532.method_15362((double)(t * ((float)Math.PI * 2))));
    }

    static String word(float seed) {
        return WORDS[Math.min(WORDS.length - 1, (int)(seed * (float)WORDS.length))];
    }

    private static int wordIndex(String word) {
        for (int index = 0; index < WORDS.length; ++index) {
            if (!WORDS[index].equals(word)) continue;
            return index;
        }
        return 0;
    }

    private static int shade(int rgb, float factor) {
        int r = (int)((float)(rgb >> 16 & 0xFF) * factor);
        int g = (int)((float)(rgb >> 8 & 0xFF) * factor);
        int b = (int)((float)(rgb & 0xFF) * factor);
        return r << 16 | g << 8 | b;
    }

    static void glow(Frame frame, double x, double y, double z, float size, int rgb, float alpha, float spin) {
        if (alpha <= 0.002f || size <= 0.0f) {
            return;
        }
        frame.matrices.method_22903();
        frame.matrices.method_46416((float)x, (float)y, (float)z);
        frame.matrices.method_22907((Quaternionfc)frame.rotation);
        frame.matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(spin));
        frame.matrices.method_22905(size, size, 1.0f);
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        class_4588 buffer = frame.provider.method_73477(GLOW);
        int color = HitEffectRender.trailColor(rgb, alpha);
        buffer.method_22918((Matrix4fc)matrix, -1.0f, -1.0f, 0.0f).method_22913(0.0f, 1.0f).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, -1.0f, 1.0f, 0.0f).method_22913(0.0f, 0.0f).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, 1.0f, 1.0f, 0.0f).method_22913(1.0f, 0.0f).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, 1.0f, -1.0f, 0.0f).method_22913(1.0f, 1.0f).method_39415(color);
        frame.matrices.method_22909();
    }

    static void pixel(Frame frame, int[] rows, double x, double y, double z, float size, float spin, int rgb, float alpha) {
        HitEffectRender.sprite(frame, rows, 8, rows.length, x, y, z, size, spin, rgb, alpha);
    }

    static void sprite(Frame frame, int[] rows, int width, int height, double x, double y, double z, float size, float spin, int rgb, float alpha) {
        if (alpha <= 0.002f || size <= 0.0f) {
            return;
        }
        frame.matrices.method_22903();
        frame.matrices.method_46416((float)x, (float)y, (float)z);
        frame.matrices.method_22907((Quaternionfc)frame.rotation);
        frame.matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(spin));
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        class_4588 buffer = frame.provider.method_73477(SOLID);
        float cell = size * 2.0f / (float)Math.max(width, height);
        float left = (float)(-width) * cell * 0.5f;
        float top = (float)height * cell * 0.5f;
        int color = HitEffectRender.argb(rgb, alpha);
        for (int row = 0; row < rows.length; ++row) {
            int bits = rows[row];
            for (int col = 0; col < width; ++col) {
                if ((bits & 1 << width - 1 - col) == 0) continue;
                float x0 = left + (float)col * cell;
                float y0 = top - (float)(row + 1) * cell;
                HitEffectRender.quad(buffer, matrix, x0, y0, 0.0f, x0 + cell, y0, 0.0f, x0 + cell, y0 + cell, 0.0f, x0, y0 + cell, 0.0f, color);
            }
        }
        frame.matrices.method_22909();
    }

    static void comicBurst(Frame frame, double x, double y, double z, float size, float spin, int rgb, float alpha) {
        if (alpha <= 0.002f || size <= 0.0f) {
            return;
        }
        frame.matrices.method_22903();
        frame.matrices.method_46416((float)x, (float)y, (float)z);
        frame.matrices.method_22907((Quaternionfc)frame.rotation);
        frame.matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(spin));
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        class_4588 buffer = frame.provider.method_73477(SOLID);
        float cell = size * 2.0f / 32.0f;
        float left = -16.0f * cell;
        float top = 16.0f * cell;
        int fill = HitEffectRender.argb(rgb, alpha);
        int edge = HitEffectRender.argb(HitEffectRender.shade(rgb, 0.12f), alpha);
        for (int row = 0; row < BURST.length; ++row) {
            String line = BURST[row];
            for (int col = 0; col < line.length(); ++col) {
                int color;
                char pixel = line.charAt(col);
                int n = pixel == '#' ? fill : (color = pixel == '.' ? edge : 0);
                if (color == 0) continue;
                float x0 = left + (float)col * cell;
                float y0 = top - (float)(row + 1) * cell;
                HitEffectRender.quad(buffer, matrix, x0, y0, 0.0f, x0 + cell, y0, 0.0f, x0 + cell, y0 + cell, 0.0f, x0, y0 + cell, 0.0f, color);
            }
        }
        frame.matrices.method_22909();
    }

    static void comicWord(Frame frame, String word, double x, double y, double z, float size, float spin, int rgb, float alpha) {
        if (alpha <= 0.002f || size <= 0.0f) {
            return;
        }
        String[] pattern = COMIC_WORDS[HitEffectRender.wordIndex(word)];
        frame.matrices.method_22903();
        frame.matrices.method_46416((float)x, (float)y, (float)z);
        frame.matrices.method_22907((Quaternionfc)frame.rotation);
        frame.matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(spin));
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        class_4588 buffer = frame.provider.method_73477(SOLID);
        float cell = size * 2.0f / 48.0f;
        float left = -24.0f * cell;
        float top = 8.0f * cell;
        int outline = HitEffectRender.argb(0x15151A, alpha);
        int fill = HitEffectRender.argb(0xFFFFFF, alpha);
        int yellow = HitEffectRender.argb(16769354, alpha);
        for (int row = 0; row < pattern.length; ++row) {
            String line = pattern[row];
            for (int col = 0; col < line.length(); ++col) {
                int color;
                char pixel = line.charAt(col);
                int n = pixel == '#' ? fill : (pixel == 'Y' ? yellow : (color = pixel == '.' ? outline : 0));
                if (color == 0) continue;
                float x0 = left + (float)col * cell;
                float y0 = top - (float)(row + 1) * cell;
                HitEffectRender.quad(buffer, matrix, x0, y0, 0.0f, x0 + cell, y0, 0.0f, x0 + cell, y0 + cell, 0.0f, x0, y0 + cell, 0.0f, color);
            }
        }
        frame.matrices.method_22909();
    }

    static Vector3f trailSegment(Frame frame, Vector3f previousSide, double x1, double y1, double z1, float width1, int color1, float alpha1, double x2, double y2, double z2, float width2, int color2, float alpha2) {
        if (alpha1 <= 0.002f && alpha2 <= 0.002f || width1 <= 0.0f && width2 <= 0.0f) {
            return previousSide;
        }
        Vector3f end = new Vector3f((float)x2, (float)y2, (float)z2);
        Vector3f start = new Vector3f((float)x1, (float)y1, (float)z1);
        Vector3f direction = new Vector3f((Vector3fc)end).sub((Vector3fc)start);
        if (direction.lengthSquared() < 1.0E-8f) {
            return previousSide;
        }
        Vector3f side = new Vector3f((Vector3fc)direction).cross((Vector3fc)end).normalize();
        if (side.lengthSquared() < 1.0E-6f) {
            side.set(0.0f, 0.0f, -1.0f).rotate((Quaternionfc)frame.rotation).cross((Vector3fc)direction).normalize();
        }
        if (side.lengthSquared() < 1.0E-6f) {
            return previousSide;
        }
        Vector3f startSide = previousSide == null ? side : previousSide;
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        class_4588 buffer = frame.provider.method_73477(TRAIL);
        for (float sign : SIDES) {
            for (int ring = 0; ring < TRAIL_RINGS.length - 1; ++ring) {
                float inner = TRAIL_RINGS[ring];
                float outer = TRAIL_RINGS[ring + 1];
                HitEffectRender.quad(buffer, matrix, start.x + startSide.x * sign * width1 * inner, start.y + startSide.y * sign * width1 * inner, start.z + startSide.z * sign * width1 * inner, HitEffectRender.trailColor(color1, alpha1 * TRAIL_FALLOFF[ring]), start.x + startSide.x * sign * width1 * outer, start.y + startSide.y * sign * width1 * outer, start.z + startSide.z * sign * width1 * outer, HitEffectRender.trailColor(color1, alpha1 * TRAIL_FALLOFF[ring + 1]), end.x + side.x * sign * width2 * outer, end.y + side.y * sign * width2 * outer, end.z + side.z * sign * width2 * outer, HitEffectRender.trailColor(color2, alpha2 * TRAIL_FALLOFF[ring + 1]), end.x + side.x * sign * width2 * inner, end.y + side.y * sign * width2 * inner, end.z + side.z * sign * width2 * inner, HitEffectRender.trailColor(color2, alpha2 * TRAIL_FALLOFF[ring]));
            }
        }
        return side;
    }

    static void trailCap(Frame frame, double x, double y, double z, Vector3f side, float width, int color, float alpha) {
        if (side == null || width <= 0.0f || alpha <= 0.002f) {
            return;
        }
        Vector3f point = new Vector3f((float)x, (float)y, (float)z);
        Vector3f forward = new Vector3f((Vector3fc)point).cross((Vector3fc)side).normalize();
        if (forward.lengthSquared() < 1.0E-6f) {
            forward.set(0.0f, 1.0f, 0.0f);
        }
        Matrix4f matrix = frame.matrices.method_23760().method_23761();
        class_4588 buffer = frame.provider.method_73477(TRAIL);
        float step = 0.3926991f;
        for (int wedge = 0; wedge < 8; ++wedge) {
            float angle1 = (float)wedge * step;
            float angle2 = angle1 + step;
            for (int ring = 0; ring < TRAIL_RINGS.length - 1; ++ring) {
                float inner = TRAIL_RINGS[ring];
                float outer = TRAIL_RINGS[ring + 1];
                float c1 = class_3532.method_15362((double)angle1);
                float s1 = class_3532.method_15374((double)angle1);
                float c2 = class_3532.method_15362((double)angle2);
                float s2 = class_3532.method_15374((double)angle2);
                HitEffectRender.quad(buffer, matrix, point.x + side.x * c1 * width * inner + forward.x * s1 * width * inner, point.y + side.y * c1 * width * inner + forward.y * s1 * width * inner, point.z + side.z * c1 * width * inner + forward.z * s1 * width * inner, HitEffectRender.trailColor(color, alpha * TRAIL_FALLOFF[ring]), point.x + side.x * c1 * width * outer + forward.x * s1 * width * outer, point.y + side.y * c1 * width * outer + forward.y * s1 * width * outer, point.z + side.z * c1 * width * outer + forward.z * s1 * width * outer, HitEffectRender.trailColor(color, alpha * TRAIL_FALLOFF[ring + 1]), point.x + side.x * c2 * width * outer + forward.x * s2 * width * outer, point.y + side.y * c2 * width * outer + forward.y * s2 * width * outer, point.z + side.z * c2 * width * outer + forward.z * s2 * width * outer, HitEffectRender.trailColor(color, alpha * TRAIL_FALLOFF[ring + 1]), point.x + side.x * c2 * width * inner + forward.x * s2 * width * inner, point.y + side.y * c2 * width * inner + forward.y * s2 * width * inner, point.z + side.z * c2 * width * inner + forward.z * s2 * width * inner, HitEffectRender.trailColor(color, alpha * TRAIL_FALLOFF[ring]));
            }
        }
    }

    static void trail(Frame frame, double x, double y, double z, double motionX, double motionY, double motionZ, float size, int rgb, float alpha) {
        if (alpha <= 0.002f || size <= 0.0f) {
            return;
        }
        double tailX = x - motionX * 1.8;
        double tailY = y - motionY * 1.8;
        double tailZ = z - motionZ * 1.8;
        int headColor = ColorUtil.overCol(rgb, 0xFFFFFF, 0.5f);
        Vector3f side = HitEffectRender.trailSegment(frame, null, tailX, tailY, tailZ, size * 0.2f, rgb, 0.0f, x, y, z, size, headColor, alpha);
        HitEffectRender.trailCap(frame, x, y, z, side, size, headColor, alpha);
    }

    static int argb(int rgb, float alpha) {
        int a = Math.round(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f);
        return a << 24 | rgb & 0xFFFFFF;
    }

    private static int trailColor(int rgb, float alpha) {
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        float saturation = max == 0 ? 0.0f : (float)(max - min) / (float)max;
        float factor = class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f);
        int a = Math.max(1, Math.round(saturation * factor * 255.0f));
        return a << 24 | (int)((float)r * factor) << 16 | (int)((float)g * factor) << 8 | (int)((float)b * factor);
    }

    private static void quad(class_4588 buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int color) {
        buffer.method_22918((Matrix4fc)matrix, x1, y1, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x2, y2, z2).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x3, y3, z3).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x4, y4, z4).method_39415(color);
    }

    private static void quad(class_4588 buffer, Matrix4f matrix, float x1, float y1, float z1, int c1, float x2, float y2, float z2, int c2, float x3, float y3, float z3, int c3, float x4, float y4, float z4, int c4) {
        buffer.method_22918((Matrix4fc)matrix, x1, y1, z1).method_39415(c1);
        buffer.method_22918((Matrix4fc)matrix, x2, y2, z2).method_39415(c2);
        buffer.method_22918((Matrix4fc)matrix, x3, y3, z3).method_39415(c3);
        buffer.method_22918((Matrix4fc)matrix, x4, y4, z4).method_39415(c4);
    }

    private static class_1921 solid() {
        RenderPipeline pipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/hit_effects_solid")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).build());
        return class_1921.method_75940((String)"hit_effects_solid", (class_12247)class_12247.method_75927((RenderPipeline)pipeline).method_75937().method_75929(65536).method_75938());
    }

    private static class_1921 trail() {
        RenderPipeline pipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/hit_effects_trail")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA).build());
        return class_1921.method_75940((String)"hit_effects_trail", (class_12247)class_12247.method_75927((RenderPipeline)pipeline).method_75937().method_75929(65536).method_75938());
    }

    private static class_1921 glow() {
        RenderPipeline pipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/hit_effects_glow")).withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withSampler("Sampler0").withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).build());
        return class_1921.method_75940((String)"hit_effects_glow", (class_12247)class_12247.method_75927((RenderPipeline)pipeline).method_75934("Sampler0", class_2960.method_60655((String)"decide", (String)"textures/particles/glow_premultiplied.png")).method_75937().method_75929(32768).method_75938());
    }

    @Environment(value=EnvType.CLIENT)
    static final class Frame {
        final class_4587 matrices;
        final class_4597.class_4598 provider;
        final class_243 camera;
        final Quaternionf rotation;
        final float partialTick;

        Frame(class_4587 matrices, class_4597.class_4598 provider, class_243 camera, Quaternionf rotation, float partialTick) {
            this.matrices = matrices;
            this.provider = provider;
            this.camera = camera;
            this.rotation = rotation;
            this.partialTick = partialTick;
        }
    }
}

