/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Box
 *  org.joml.Matrix4f
 *  org.lwjgl.opengl.GL11
 *  sg.ec.ПO$2
 *  sg.ec.ПO$Ф
 *  sg.ec.ПO$Ч
 */
package minced.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import minced.core.ColorUtil;
import minced.core.GradientEntry;
import minced.core.RectEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import sg.ec.pO;

public final class Render2DBatch
{
    static Map field9163;
    public static MinecraftClient I = MinecraftClient.getInstance();
    static Matrix4f field9164;
    static float field9165;
    static float field9166;
    static float field9167;
    static float field9168;
    static float field9169;
    static Map field9170;
    static List field9171;
    static float field9172;
    static int field9173;
    static int field9174;
    static int field9175;
    static int field9176;
    static int field9177;
    static int field9178;
    static int field9179;
    static int field9180;
    static float field9181;
    static float field9182;
    static float field9183;
    static float field9184;
    static float field9185;
    static float field9186;
    static float field9187;
    static float field9188;
    static float field9189;
    static float field9190;
    static float field9191;
    static float field9192;
    static float field9193;
    static float field9194;
    static float field9195;
    static float field9196;
    static boolean field9197;
    static BufferBuilder field9198;
    static List l;
    static float field9199;
    static float field9200;
    static float field9201;
    static float field9202;
    static float field9203;
    static String field9204;
    static float field9205;
    static float field9206;
    static float field9207;
    static float field9208;
    static String field9209;
    static List field9210;
    static float field9211;
    static float field9212;
    static float field9213;
    static float field9214;
    static float field9215;
    static float field9216;
    static float field9217;
    static float field9218;
    static float field9219;
    static float field9220;
    static float field9221;
    static float field9222;
    static float field9223;
    static String field9224;
    static float field9225;
    static float field9226;
    static float field9227;
    static float field9228;
    static float field9229;
    static float field9230;
    static float field9231;
    static float field9232;
    static float field9233;
    static float field9234;
    static float field9235;
    static float field9236;
    static float field9237;
    static float field9238;
    static float field9239;
    static float field9240;
    static float field9241;
    static float field9242;
    static float field9243;
    static float field9244;
    static float field9245;
    static float field9246;
    static float field9247;
    static float field9248;
    static float field9249;
    static float field9250;
    static float field9251;
    static float field9252;
    static float field9253;
    static float field9254;
    static float field9255;
    static float field9256;
    static float field9257;
    static String field9258;

    private static void method2648() {
        if (field9163.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        TextureManager v0 = I.getTextureManager();
        Matrix4f v1 = field9164;
        for (Map.Entry v4 : (Iterable<java.util.Map.Entry>)(field9163.entrySet())) {
            Identifier v5 = (Identifier)v4.getKey();
            List v6 = (List)v4.getValue();
            if (v6.isEmpty()) continue;
            RenderSystem.setShaderTexture((int)0, (Identifier)v5);
            AbstractTexture v7 = v0.getTexture(v5);
            if (v7 != null) {
                RenderSystem.bindTexture((int)v7.getGlId());
                GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
                GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            }
            BufferBuilder v8 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            int v10 = v6.size();
            for (int v9 = 0; v9 < v10; ++v9) {
                pO.ch v11 = (pO.ch)v6.get(v9);
                int v12 = v11.sh;
                float v13 = (float)(v12 >> 16 & 0xFF) * field9166;
                float v14 = (float)(v12 >> 8 & 0xFF) * field9167;
                float v15 = (float)(v12 & 0xFF) * field9168;
                float v16 = (float)(v12 >> 24 & 0xFF) * field9169;
                float v17 = v11.I;
                float v18 = v11.cfr_renamed_136;
                float v19 = v17 + v11.ь;
                float v20 = v18 + v11.м;
                v8.vertex(v1, v17, v20, 0.0f).texture(0.0f, 1.0f).color(v13, v14, v15, v16);
                v8.vertex(v1, v19, v20, 0.0f).texture(1.0f, 1.0f).color(v13, v14, v15, v16);
                v8.vertex(v1, v19, v18, 0.0f).texture(1.0f, 0.0f).color(v13, v14, v15, v16);
                v8.vertex(v1, v17, v18, 0.0f).texture(0.0f, 0.0f).color(v13, v14, v15, v16);
            }
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v8.end());
        }
        RenderSystem.disableBlend();
    }

    public static void method2649(Identifier param02, float param1, float param2, float param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10, float param11, float param12, int param13, int param14, int param15, int param16, float param17, float param18, float param19, float param20, float param21, float param22, float param23, float param24) {
        ((java.util.List)field9170.computeIfAbsent(param02, param0 -> new ArrayList())).add(new GradientEntry(param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19, param20, param21, param22, param23, param24));
    }

    private static void method2650() {
        if (field9171.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder v0 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f v1 = field9164;
        int v4 = field9171.size();
        for (int v3 = 0; v3 < v4; ++v3) {
            pO.f13 v5 = (pO.f13)field9171.get(v3);
            int v6 = v5.cfr_renamed_5;
            int v7 = v5.ц;
            int v8 = v5.И;
            int v9 = v5.西;
            if ((v6 & field9173) == 0) {
                v6 |= field9174;
            }
            if ((v7 & field9175) == 0) {
                v7 |= field9176;
            }
            if ((v8 & field9177) == 0) {
                v8 |= field9178;
            }
            if ((v9 & field9179) == 0) {
                v9 |= field9180;
            }
            float v10 = (float)(v6 >> 16 & 0xFF) * field9181;
            float v11 = (float)(v6 >> 8 & 0xFF) * field9182;
            float v12 = (float)(v6 & 0xFF) * field9183;
            float v13 = (float)(v6 >> 24 & 0xFF) * field9184;
            float v14 = (float)(v7 >> 16 & 0xFF) * field9185;
            float v15 = (float)(v7 >> 8 & 0xFF) * field9186;
            float v16 = (float)(v7 & 0xFF) * field9187;
            float v17 = (float)(v7 >> 24 & 0xFF) * field9188;
            float v18 = (float)(v8 >> 16 & 0xFF) * field9189;
            float v19 = (float)(v8 >> 8 & 0xFF) * field9190;
            float v20 = (float)(v8 & 0xFF) * field9191;
            float v21 = (float)(v8 >> 24 & 0xFF) * field9192;
            float v22 = (float)(v9 >> 16 & 0xFF) * field9193;
            float v23 = (float)(v9 >> 8 & 0xFF) * field9194;
            float v24 = (float)(v9 & 0xFF) * field9195;
            float v25 = (float)(v9 >> 24 & 0xFF) * field9196;
            float v26 = v5.cfr_renamed_142;
            float v27 = v5.cfr_renamed_131;
            float v28 = v26 + v5.cfr_renamed_220;
            float v29 = v27 + v5.cfr_renamed_175;
            v0.vertex(v1, v26, v27, 0.0f).color(v10, v11, v12, v13);
            v0.vertex(v1, v26, v29, 0.0f).color(v14, v15, v16, v17);
            v0.vertex(v1, v28, v29, 0.0f).color(v18, v19, v20, v21);
            v0.vertex(v1, v28, v27, 0.0f).color(v22, v23, v24, v25);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v0.end());
        RenderSystem.disableBlend();
    }

    public static void method2651() {
        if (!field9197) {
            return;
        }
        Render2DBatch.method2678();
        field9164 = null;
        field9197 = false;
    }

    public static void method2653(float param0, float param1, float param2, float param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10, float param11, float param12, float param13, float param14, float param15, float param16, float param17, float param18, float param19, float param20, float param21, float param22, float param23) {
        field9198.vertex(field9164, param0, param1, param2).texture(param12, param13).color(param20, param21, param22, param23);
        field9198.vertex(field9164, param3, param4, param5).texture(param14, param15).color(param20, param21, param22, param23);
        field9198.vertex(field9164, param6, param7, param8).texture(param16, param17).color(param20, param21, param22, param23);
        field9198.vertex(field9164, param9, param10, param11).texture(param18, param19).color(param20, param21, param22, param23);
    }

    private static void method2654() {
        if (!Boolean.FALSE.equals(l.isEmpty())) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder v0 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f v1 = field9164;
        int v4 = l.size();
        for (int v3 = 0; v3 < v4; ++v3) {
            RectEntry v5 = (RectEntry)l.get(v3);
            int v6 = v5.field11741;
            float v7 = (float)(v6 >> 16 & 0xFF) * field9200;
            float v8 = (float)(v6 >> 8 & 0xFF) * field9201;
            float v9 = (float)(v6 & 0xFF) * field9202;
            float v10 = (float)(v6 >> 24 & 0xFF) * field9203;
            float v11 = v5.field11737;
            float v12 = v5.field11738;
            float v13 = v11 + v5.field11739;
            float v14 = v12 + v5.field11740;
            v0.vertex(v1, v11, v12, 0.0f).color(v7, v8, v9, v10);
            v0.vertex(v1, v11, v14, 0.0f).color(v7, v8, v9, v10);
            v0.vertex(v1, v13, v14, 0.0f).color(v7, v8, v9, v10);
            v0.vertex(v1, v13, v12, 0.0f).color(v7, v8, v9, v10);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v0.end());
        RenderSystem.disableBlend();
    }

    public static void method2655(float param0, float param1, float param2, float param3, int param4, int param5) {
        Render2DBatch.method2660(param0, param1, param2, param3, param4, param5, param5, param4);
    }

    private static void method2656(BufferBuilder param0, Matrix4f param1, float param2, float param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10, float param11) {
        param0.vertex(param1, param2, param3, param4).color(param8, param9, param10, param11);
        param0.vertex(param1, param5, param6, param7).color(param8, param9, param10, param11);
    }

    public static void method2657(Matrix4f param0) {
        if (field9197) {
            Render2DBatch.method2663();
        }
        field9164 = param0;
        field9198 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        field9197 = true;
    }

    private Render2DBatch() {
        throw new UnsupportedOperationException(field9204);
    }

    public static void method2658(DrawContext param0, Identifier param1, float param2, float param3, float param4, float param5, int param6) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture((int)0, (Identifier)param1);
        TextureManager v7 = I.getTextureManager();
        AbstractTexture v8 = v7.getTexture(param1);
        if (v8 != null) {
            RenderSystem.bindTexture((int)v8.getGlId());
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
        }
        float v9 = (float)(param6 >> 16 & 0xFF) / field9205;
        float v10 = (float)(param6 >> 8 & 0xFF) / field9206;
        float v11 = (float)(param6 & 0xFF) / field9207;
        float v12 = (float)(param6 >> 24 & 0xFF) / field9208;
        BufferBuilder v13 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        Matrix4f v14 = param0.getMatrices().peek().getPositionMatrix();
        v13.vertex(v14, param2, param3 + param5, 0.0f).texture(0.0f, 1.0f).color(v9, v10, v11, v12);
        v13.vertex(v14, param2 + param4, param3 + param5, 0.0f).texture(1.0f, 1.0f).color(v9, v10, v11, v12);
        v13.vertex(v14, param2 + param4, param3, 0.0f).texture(1.0f, 0.0f).color(v9, v10, v11, v12);
        v13.vertex(v14, param2, param3, 0.0f).texture(0.0f, 0.0f).color(v9, v10, v11, v12);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v13.end());
        RenderSystem.disableBlend();
    }

    public static void method2660(float param0, float param1, float param2, float param3, int param4, int param5, int param6, int param7) {
        if (!field9197) {
            throw new IllegalStateException(field9209);
        }
        field9171.add(new pO.f13(param0, param1, param2, param3, param4, param5, param6, param7));
    }

    public static void method2661(Box param0, int param1, boolean param2, boolean param3) {
        if (!param2 && !param3) {
            return;
        }
        field9210.add(new pO._2(param0, param1, param2, param3));
    }

    public static void method2662(MatrixStack param02) {
        boolean v32;
        float v18;
        float v17;
        float v16;
        float v15;
        float v14;
        float v13;
        float v12;
        float v11;
        float v10;
        int v8;
        if (field9210.isEmpty()) {
            return;
        }
        Matrix4f v1 = param02.peek().getPositionMatrix();
        boolean v2 = field9210.stream().anyMatch(param0 -> ((pO._2)param0).т);
        if (v2) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
            BufferBuilder v32_397 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            for (Object v5 : field9210) {
                if (!((pO._2)v5).т) continue;
                Box v6 = ((pO._2)v5).н;
                int v7 = ((pO._2)v5).t2;
                v8 = (int)((float)ColorUtil.cfr_renamed_0(v7) * field9211);
                int v9 = ColorUtil.method1855(v7, v8);
                v10 = (float)ColorUtil.method1854(v9) / field9212;
                v11 = (float)ColorUtil.method1846(v9) / field9213;
                v12 = (float)ColorUtil.method1847(v9) / field9214;
                v13 = (float)ColorUtil.cfr_renamed_0(v9) / field9215;
                v14 = (float)v6.minX;
                v15 = (float)v6.minY;
                v16 = (float)v6.minZ;
                v17 = (float)v6.maxX;
                v18 = (float)v6.maxY;
                float v19 = (float)v6.maxZ;
                v32_397.vertex(v1, v14, v15, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v15, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v15, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v15, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v18, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v18, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v18, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v18, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v15, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v18, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v18, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v15, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v15, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v15, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v18, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v18, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v15, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v15, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v18, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v14, v18, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v15, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v18, v16).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v18, v19).color(v10, v11, v12, v13);
                v32_397.vertex(v1, v17, v15, v19).color(v10, v11, v12, v13);
            }
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v32_397.end());
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
        if (v32 = field9210.stream().anyMatch(param0 -> ((pO._2)param0).М)) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
            BufferBuilder v4 = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            for (pO._2 v6 : (Iterable<pO._2>)(field9210)) {
                if (!v6.М) continue;
                Box v7 = v6.н;
                v8 = v6.t2;
                float v9 = (float)ColorUtil.method1854(v8) / field9216;
                v10 = (float)ColorUtil.method1846(v8) / field9217;
                v11 = (float)ColorUtil.method1847(v8) / field9218;
                v12 = (float)ColorUtil.cfr_renamed_0(v8) / field9219;
                v13 = (float)v7.minX;
                v14 = (float)v7.minY;
                v15 = (float)v7.minZ;
                v16 = (float)v7.maxX;
                v17 = (float)v7.maxY;
                v18 = (float)v7.maxZ;
                Render2DBatch.method2656(v4, v1, v13, v14, v15, v16, v14, v15, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v16, v14, v15, v16, v14, v18, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v16, v14, v18, v13, v14, v18, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v13, v14, v18, v13, v14, v15, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v13, v17, v15, v16, v17, v15, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v16, v17, v15, v16, v17, v18, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v16, v17, v18, v13, v17, v18, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v13, v17, v18, v13, v17, v15, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v13, v14, v15, v13, v17, v15, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v16, v14, v15, v16, v17, v15, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v16, v14, v18, v16, v17, v18, v9, v10, v11, v12);
                Render2DBatch.method2656(v4, v1, v13, v14, v18, v13, v17, v18, v9, v10, v11, v12);
            }
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v4.end());
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
        field9210.clear();
    }

    private static void method2663() {
        l.clear();
        field9171.clear();
        field9163.clear();
        field9210.clear();
        field9164 = null;
        field9198 = null;
        field9197 = false;
    }

    public static void method2664(DrawContext param0, float param1, float param2, float param3, float param4, int param5) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        Matrix4f v6 = param0.getMatrices().peek().getPositionMatrix();
        BufferBuilder v7 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        float v8 = (float)(param5 >> 16 & 0xFF) / field9220;
        float v9 = (float)(param5 >> 8 & 0xFF) / field9221;
        float v10 = (float)(param5 & 0xFF) / field9222;
        float v11 = (float)(param5 >> 24 & 0xFF) / field9223;
        v7.vertex(v6, param1, param2, 0.0f).color(v8, v9, v10, v11);
        v7.vertex(v6, param1, param2 + param4, 0.0f).color(v8, v9, v10, v11);
        v7.vertex(v6, param1 + param3, param2 + param4, 0.0f).color(v8, v9, v10, v11);
        v7.vertex(v6, param1 + param3, param2, 0.0f).color(v8, v9, v10, v11);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v7.end());
        RenderSystem.disableBlend();
    }

    public static void method2665(float param0, float param1, float param2, float param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10, float param11, float param12, float param13, float param14, float param15, float param16, float param17, float param18, float param19) {
        field9198.vertex(field9164, param0, param1, 0.0f).texture(param8, param9).color(param16, param17, param18, param19);
        field9198.vertex(field9164, param2, param3, 0.0f).texture(param10, param11).color(param16, param17, param18, param19);
        field9198.vertex(field9164, param4, param5, 0.0f).texture(param12, param13).color(param16, param17, param18, param19);
        field9198.vertex(field9164, param6, param7, 0.0f).texture(param14, param15).color(param16, param17, param18, param19);
    }

    public static void method2666(Identifier param02, float param1, float param2, float param3, float param4, int param5) {
        if (!field9197) {
            throw new IllegalStateException(field9224);
        }
        ((java.util.List)field9163.computeIfAbsent(param02, param0 -> new ArrayList())).add(new pO.ch(param1, param2, param3, param4, param5));
    }

    public static void method2667() {
        if (field9170.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableCull();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        TextureManager v0 = I.getTextureManager();
        for (Map.Entry v3 : (Iterable<java.util.Map.Entry>)(field9170.entrySet())) {
            Identifier v4 = (Identifier)v3.getKey();
            List v5 = (List)v3.getValue();
            if (v5.isEmpty()) continue;
            RenderSystem.setShaderTexture((int)0, (Identifier)v4);
            AbstractTexture v6 = v0.getTexture(v4);
            if (v6 != null) {
                RenderSystem.bindTexture((int)v6.getGlId());
                GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
                GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            }
            BufferBuilder v7 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            for (GradientEntry v9 : (Iterable<GradientEntry>)(v5)) {
                float v10 = (float)(v9.field11725 >> 16 & 0xFF) * field9226;
                float v11 = (float)(v9.field11725 >> 8 & 0xFF) * field9227;
                float v12 = (float)(v9.field11725 & 0xFF) * field9228;
                float v13 = (float)(v9.field11725 >> 24 & 0xFF) * field9229;
                float v14 = (float)(v9.field11726 >> 16 & 0xFF) * field9230;
                float v15 = (float)(v9.field11726 >> 8 & 0xFF) * field9231;
                float v16 = (float)(v9.field11726 & 0xFF) * field9232;
                float v17 = (float)(v9.field11726 >> 24 & 0xFF) * field9233;
                float v18 = (float)(v9.field11727 >> 16 & 0xFF) * field9234;
                float v19 = (float)(v9.field11727 >> 8 & 0xFF) * field9235;
                float v20 = (float)(v9.field11727 & 0xFF) * field9236;
                float v21 = (float)(v9.field11727 >> 24 & 0xFF) * field9237;
                float v22 = (float)(v9.field11728 >> 16 & 0xFF) * field9238;
                float v23 = (float)(v9.field11728 >> 8 & 0xFF) * field9239;
                float v24 = (float)(v9.field11728 & 0xFF) * field9240;
                float v25 = (float)(v9.field11728 >> 24 & 0xFF) * field9241;
                v7.vertex(v9.field11713, v9.field11714, v9.field11715).texture(v9.field11729, v9.field11730).color(v10, v11, v12, v13);
                v7.vertex(v9.field11716, v9.field11717, v9.field11718).texture(v9.field11731, v9.field11732).color(v14, v15, v16, v17);
                v7.vertex(v9.field11719, v9.field11720, v9.field11721).texture(v9.field11733, v9.field11734).color(v18, v19, v20, v21);
                v7.vertex(v9.field11722, v9.field11723, v9.field11724).texture(v9.field11735, v9.field11736).color(v22, v23, v24, v25);
            }
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v7.end());
        }
        field9170.clear();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)true);
    }

    public static void method2668(DrawContext param0) {
        if (field9197) {
            Render2DBatch.method2663();
        }
        field9164 = param0.getMatrices().peek().getPositionMatrix();
        field9197 = true;
    }

    public static void method2669(DrawContext param0, float param1, float param2, float param3, float param4, int param5, int param6, int param7, int param8) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        Matrix4f v9 = param0.getMatrices().peek().getPositionMatrix();
        BufferBuilder v10 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        float v11 = (float)(param5 >> 16 & 0xFF) / field9242;
        float v12 = (float)(param5 >> 8 & 0xFF) / field9243;
        float v13 = (float)(param5 & 0xFF) / field9244;
        float v14 = (float)(param5 >> 24 & 0xFF) / field9245;
        float v15 = (float)(param6 >> 16 & 0xFF) / field9246;
        float v16 = (float)(param6 >> 8 & 0xFF) / field9247;
        float v17 = (float)(param6 & 0xFF) / field9248;
        float v18 = (float)(param6 >> 24 & 0xFF) / field9249;
        float v19 = (float)(param7 >> 16 & 0xFF) / field9250;
        float v20 = (float)(param7 >> 8 & 0xFF) / field9251;
        float v21 = (float)(param7 & 0xFF) / field9252;
        float v22 = (float)(param7 >> 24 & 0xFF) / field9253;
        float v23 = (float)(param8 >> 16 & 0xFF) / field9254;
        float v24 = (float)(param8 >> 8 & 0xFF) / field9255;
        float v25 = (float)(param8 & 0xFF) / field9256;
        float v26 = (float)(param8 >> 24 & 0xFF) / field9257;
        v10.vertex(v9, param1, param2, 0.0f).color(v11, v12, v13, v14);
        v10.vertex(v9, param1, param2 + param4, 0.0f).color(v15, v16, v17, v18);
        v10.vertex(v9, param1 + param3, param2 + param4, 0.0f).color(v19, v20, v21, v22);
        v10.vertex(v9, param1 + param3, param2, 0.0f).color(v23, v24, v25, v26);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v10.end());
        RenderSystem.disableBlend();
    }

    public static void method2671() {
        l = new ArrayList();
        field9171 = new ArrayList();
        field9163 = new HashMap();
        field9210 = new ArrayList();
        field9164 = null;
        field9197 = false;
        field9198 = null;
        field9170 = new HashMap();
    }

    public static void method2672(float param0, float param1, float param2, float param3, int param4) {
        if (!field9197) {
            throw new IllegalStateException(field9258);
        }
        l.add(new RectEntry(param0, param1, param2, param3, param4));
    }

    public static void method2673(float param0, float param1, float param2, float param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10, float param11) {
        field9198.vertex(field9164, param0, param1, 0.0f).texture(0.0f, 1.0f).color(param8, param9, param10, param11);
        field9198.vertex(field9164, param2, param3, 0.0f).texture(1.0f, 1.0f).color(param8, param9, param10, param11);
        field9198.vertex(field9164, param4, param5, 0.0f).texture(1.0f, 0.0f).color(param8, param9, param10, param11);
        field9198.vertex(field9164, param6, param7, 0.0f).texture(0.0f, 0.0f).color(param8, param9, param10, param11);
    }

    public static void method2675() {
        if (!field9197 || field9198 == null) {
            return;
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)field9198.end());
        field9164 = null;
        field9198 = null;
        field9197 = false;
    }

    public static void method2676(Identifier param0, float param1, float param2, float param3, float param4, Color param5) {
        int v6 = param5.getAlpha() << 24 | param5.getRed() << 16 | param5.getGreen() << 8 | param5.getBlue();
        Render2DBatch.method2666(param0, param1, param2, param3, param4, v6);
    }

    public static void method2677(DrawContext param0, Identifier param1, float param2, float param3, float param4, float param5, Color param6) {
        int v7 = param6.getAlpha() << 24 | param6.getRed() << 16 | param6.getGreen() << 8 | param6.getBlue();
        Render2DBatch.method2658(param0, param1, param2, param3, param4, param5, v7);
    }

    private static void method2678() {
        if (field9164 == null) {
            return;
        }
        Render2DBatch.method2654();
        Render2DBatch.method2650();
        Render2DBatch.method2648();
        l.clear();
        field9171.clear();
        field9163.clear();
    }

    static {
        field9197 = false;
        field9258 = "Must call begin() before drawing!";
        field9209 = "Must call begin() before drawing!";
        field9224 = "Must call begin() before drawing!";
        field9199 = 0.003921569f;
        field9200 = 0.003921569f;
        field9201 = 0.003921569f;
        field9202 = 0.003921569f;
        field9203 = 0.003921569f;
        field9172 = 0.003921569f;
        field9173 = -16777216;
        field9174 = -16777216;
        field9175 = -16777216;
        field9176 = -16777216;
        field9177 = -16777216;
        field9178 = -16777216;
        field9179 = -16777216;
        field9180 = -16777216;
        field9181 = 0.003921569f;
        field9182 = 0.003921569f;
        field9183 = 0.003921569f;
        field9184 = 0.003921569f;
        field9185 = 0.003921569f;
        field9186 = 0.003921569f;
        field9187 = 0.003921569f;
        field9188 = 0.003921569f;
        field9189 = 0.003921569f;
        field9190 = 0.003921569f;
        field9191 = 0.003921569f;
        field9192 = 0.003921569f;
        field9193 = 0.003921569f;
        field9194 = 0.003921569f;
        field9195 = 0.003921569f;
        field9196 = 0.003921569f;
        field9165 = 0.003921569f;
        field9166 = 0.003921569f;
        field9167 = 0.003921569f;
        field9168 = 0.003921569f;
        field9169 = 0.003921569f;
        field9211 = 0.1f;
        field9212 = 255.0f;
        field9213 = 255.0f;
        field9214 = 255.0f;
        field9215 = 255.0f;
        field9216 = 255.0f;
        field9217 = 255.0f;
        field9218 = 255.0f;
        field9219 = 255.0f;
        field9220 = 255.0f;
        field9221 = 255.0f;
        field9222 = 255.0f;
        field9223 = 255.0f;
        field9242 = 255.0f;
        field9243 = 255.0f;
        field9244 = 255.0f;
        field9245 = 255.0f;
        field9246 = 255.0f;
        field9247 = 255.0f;
        field9248 = 255.0f;
        field9249 = 255.0f;
        field9250 = 255.0f;
        field9251 = 255.0f;
        field9252 = 255.0f;
        field9253 = 255.0f;
        field9254 = 255.0f;
        field9255 = 255.0f;
        field9256 = 255.0f;
        field9257 = 255.0f;
        field9205 = 255.0f;
        field9206 = 255.0f;
        field9207 = 255.0f;
        field9208 = 255.0f;
        field9225 = 0.003921569f;
        field9226 = 0.003921569f;
        field9227 = 0.003921569f;
        field9228 = 0.003921569f;
        field9229 = 0.003921569f;
        field9230 = 0.003921569f;
        field9231 = 0.003921569f;
        field9232 = 0.003921569f;
        field9233 = 0.003921569f;
        field9234 = 0.003921569f;
        field9235 = 0.003921569f;
        field9236 = 0.003921569f;
        field9237 = 0.003921569f;
        field9238 = 0.003921569f;
        field9239 = 0.003921569f;
        field9240 = 0.003921569f;
        field9241 = 0.003921569f;
        field9204 = "This is a utility class and cannot be instantiated";
        Render2DBatch.method2671();
    }
}



