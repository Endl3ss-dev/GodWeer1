/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.Box
 *  org.joml.Matrix4f
 */
package minced.core;

import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.ColorUtil;
import minced.core.CancellableEvent;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

public class Render3D
implements CancellableEvent {
    static float field8570;
    static float field8571;
    static float field8572;
    static float field8573;
    static float field8574;
    static double field8575;
    static float field8576;
    static float field8577;
    static float field8578;
    static float field8579;
    static float field8580;

    private static void method2406(BufferBuilder param0, MatrixStack param1, Matrix4f param2, float param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10, float param11, float param12) {
        float v13 = param6 - param3;
        float v14 = param7 - param4;
        float v15 = param8 - param5;
        float v16 = (float)Math.sqrt(v13 * v13 + v14 * v14 + v15 * v15);
        float v17 = 0.0f;
        float v18 = 1.0f;
        float v19 = 0.0f;
        if (v16 > field8570) {
            v17 = v13 / v16;
            v18 = v14 / v16;
            v19 = v15 / v16;
        }
        param0.vertex(param2, param3, param4, param5).color(param9, param10, param11, param12).normal(param1.peek(), v17, v18, v19);
        param0.vertex(param2, param6, param7, param8).color(param9, param10, param11, param12).normal(param1.peek(), v17, v18, v19);
    }

    public static void method2407(MatrixStack param0, Box param1, int param2, float param3) {
        Render3D.method2410(param0, param1, param2, param3, true, true);
    }

    public static void method2408(MatrixStack param0, Box param1, int param2, float param3) {
        Render3D.method2409(param0, param1, param2, param3);
    }

    public static void method2409(MatrixStack param0, Box param1, int param2, float param3) {
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.RENDERTYPE_LINES);
        RenderSystem.lineWidth((float)param3);
        BufferBuilder v4 = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        Matrix4f v5 = param0.peek().getPositionMatrix();
        float v6 = (float)ColorUtil.method1854(param2) / field8571;
        float v7 = (float)ColorUtil.method1846(param2) / field8572;
        float v8 = (float)ColorUtil.method1847(param2) / field8573;
        float v9 = (float)ColorUtil.cfr_renamed_0(param2) / field8574;
        float v10 = (float)param1.minX;
        float v11 = (float)param1.minY;
        float v12 = (float)param1.minZ;
        float v13 = (float)param1.maxX;
        float v14 = (float)param1.maxY;
        float v15 = (float)param1.maxZ;
        Render3D.method2406(v4, param0, v5, v10, v11, v12, v13, v11, v12, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v13, v11, v12, v13, v11, v15, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v13, v11, v15, v10, v11, v15, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v10, v11, v15, v10, v11, v12, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v10, v14, v12, v13, v14, v12, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v13, v14, v12, v13, v14, v15, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v13, v14, v15, v10, v14, v15, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v10, v14, v15, v10, v14, v12, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v10, v11, v12, v10, v14, v12, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v13, v11, v12, v13, v14, v12, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v13, v11, v15, v13, v14, v15, v6, v7, v8, v9);
        Render3D.method2406(v4, param0, v5, v10, v11, v15, v10, v14, v15, v6, v7, v8, v9);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v4.end());
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void method2410(MatrixStack param0, Box param1, int param2, float param3, boolean param4, boolean param5) {
        param1 = param1.expand(field8575);
        if (param5) {
            int v6 = (int)((float)ColorUtil.cfr_renamed_0(param2) * field8576);
            Render3D.method2411(param0, param1, ColorUtil.method1855(param2, v6));
        }
        if (param4) {
            Render3D.method2409(param0, param1, param2, param3);
        }
    }

    public static void method2411(MatrixStack param0, Box param1, int param2) {
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder v3 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f v4 = param0.peek().getPositionMatrix();
        float v5 = (float)ColorUtil.method1854(param2) / field8577;
        float v6 = (float)ColorUtil.method1846(param2) / field8578;
        float v7 = (float)ColorUtil.method1847(param2) / field8579;
        float v8 = (float)ColorUtil.cfr_renamed_0(param2) / field8580;
        float v9 = (float)param1.minX;
        float v10 = (float)param1.minY;
        float v11 = (float)param1.minZ;
        float v12 = (float)param1.maxX;
        float v13 = (float)param1.maxY;
        float v14 = (float)param1.maxZ;
        v3.vertex(v4, v9, v10, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v10, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v10, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v10, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v13, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v13, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v13, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v13, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v10, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v13, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v13, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v10, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v10, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v10, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v13, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v13, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v10, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v10, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v13, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v9, v13, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v10, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v13, v11).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v13, v14).color(v5, v6, v7, v8);
        v3.vertex(v4, v12, v10, v14).color(v5, v6, v7, v8);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v3.end());
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void method2412(MatrixStack param0, Box param1, int param2) {
        Render3D.method2411(param0, param1, param2);
    }

    static {
        field8575 = 0.001;
        field8576 = 0.1f;
        field8571 = 255.0f;
        field8572 = 255.0f;
        field8573 = 255.0f;
        field8574 = 255.0f;
        field8577 = 255.0f;
        field8578 = 255.0f;
        field8579 = 255.0f;
        field8580 = 255.0f;
        field8570 = 1.0E-6f;
    }
}



