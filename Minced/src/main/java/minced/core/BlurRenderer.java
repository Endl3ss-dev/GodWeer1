/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.util.math.Vec2f
 *  org.joml.Matrix4f
 */
package minced.core;

import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.ScaleUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec2f;
import org.joml.Matrix4f;

public class BlurRenderer
extends Framebuffer {
    static MinecraftClient I = MinecraftClient.getInstance();
    boolean field12187;
    // field_1480/field_1477 used to be re-declared here, shadowing Framebuffer's own size fields.
    // They stayed 0, so every size check reported "changed" and the FBO was deleted and recreated
    // on every frame. Compare against the inherited textureWidth/textureHeight instead.

    public void stop() {
        this.endRead();
        I.getFramebuffer().beginWrite(true);
    }

    public static void drawQuads() {
        Vec2f v0 = ScaleUtil.method2887(I.getWindow().getScaledWidth(), I.getWindow().getScaledHeight());
        double v1 = v0.x;
        double v3 = v0.y;
        BlurRenderer.drawQuads(0.0, 0.0, v1, v3);
    }

    public void draw() {
        this.beginRead();
        BlurRenderer.drawQuads();
    }

    public static void drawQuads(double param0, double param2, double param4, double param6, int param8) {
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder v9 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        v9.vertex((float)param0, (float)param2, 0.0f).color(param8);
        v9.vertex((float)param0, (float)(param2 + param6), 0.0f).color(param8);
        v9.vertex((float)(param0 + param4), (float)(param2 + param6), 0.0f).color(param8);
        v9.vertex((float)(param0 + param4), (float)param2, 0.0f).color(param8);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v9.end());
    }

    public BlurRenderer(int param1, int param2, boolean param3) {
        super(param3);
        this.resize(param1, param2);
    }

    public static void drawQuads(DrawContext param0, double param1, double param3, int param5) {
        BlurRenderer.drawQuads(param0, 0.0, 0.0, param1, param3, param5);
    }

    public BlurRenderer setLinear() {
        this.field12187 = true;
        return this;
    }

    public static void drawQuads(DrawContext param0) {
        Vec2f v1 = ScaleUtil.method2887(I.getWindow().getScaledWidth(), I.getWindow().getScaledHeight());
        double v2 = v1.x;
        double v4 = v1.y;
        BlurRenderer.drawQuads(param0, 0.0, 0.0, v2, v4);
    }

    public static BlurRenderer createFramebuffer(BlurRenderer param0, boolean param1) {
        if (BlurRenderer.needsNewFramebuffer(param0)) {
            if (param0 != null) {
                param0.delete();
            }
            int v2 = I != null && I.getWindow() != null ? I.getWindow().getFramebufferWidth() : 1;
            int v3 = I != null && I.getWindow() != null ? I.getWindow().getFramebufferHeight() : 1;
            return new BlurRenderer(v2, v3, param1);
        }
        return param0;
    }

    public static BlurRenderer createFramebuffer(BlurRenderer param0) {
        return BlurRenderer.createFramebuffer(param0, false);
    }

    public void draw(int param1) {
        this.beginRead();
        BlurRenderer.drawQuads(param1);
    }

    public static void drawQuads(int param0) {
        Vec2f v1 = ScaleUtil.method2887(I.getWindow().getScaledWidth(), I.getWindow().getScaledHeight());
        double v2 = v1.x;
        double v4 = v1.y;
        BlurRenderer.drawQuads(0.0, 0.0, v2, v4, param0);
    }

    private static void resizeFramebuffer(BlurRenderer param0) {
        if (param0 == null || I == null || I.getWindow() == null) {
            return;
        }
        int v1 = Math.max(I.getWindow().getFramebufferWidth(), 1);
        int v2 = Math.max(I.getWindow().getFramebufferHeight(), 1);
        if (param0.textureWidth != v1 || param0.textureHeight != v2) {
            param0.resize(v1, v2);
        }
    }

    public static void drawQuads(double param0, double param2, int param4) {
        BlurRenderer.drawQuads(0.0, 0.0, param0, param2, param4);
    }

    public void setTexFilter(int param1) {
        super.setTexFilter(this.field12187 ? 9729 : param1);
    }

    public void setup(boolean param1) {
        BlurRenderer.resizeFramebuffer(this);
        if (param1) {
            this.clear();
        }
        this.beginWrite(false);
    }

    public void draw(Framebuffer param1) {
        param1.beginRead();
        BlurRenderer.drawQuads();
    }

    public static void drawQuads(DrawContext param0, double param1, double param3) {
        BlurRenderer.drawQuads(param0, 0.0, 0.0, param1, param3);
    }

    public void setup() {
        this.setup(true);
    }

    public static void drawQuads(DrawContext param0, double param1, double param3, double param5, double param7) {
        Matrix4f v9 = param0.getMatrices().peek().getPositionMatrix();
        BufferBuilder v10 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        v10.vertex(v9, (float)param1, (float)param3, 0.0f).texture(0.0f, 0.0f);
        v10.vertex(v9, (float)param1, (float)(param3 + param7), 0.0f).texture(0.0f, 1.0f);
        v10.vertex(v9, (float)(param1 + param5), (float)(param3 + param7), 0.0f).texture(1.0f, 1.0f);
        v10.vertex(v9, (float)(param1 + param5), (float)param3, 0.0f).texture(1.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v10.end());
    }

    public static void drawQuads(DrawContext param0, double param1, double param3, double param5, double param7, int param9) {
        Matrix4f v10 = param0.getMatrices().peek().getPositionMatrix();
        BufferBuilder v11 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        v11.vertex(v10, (float)param1, (float)param3, 0.0f).color(param9).texture(0.0f, 0.0f);
        v11.vertex(v10, (float)param1, (float)(param3 + param7), 0.0f).color(param9).texture(0.0f, 1.0f);
        v11.vertex(v10, (float)(param1 + param5), (float)(param3 + param7), 0.0f).color(param9).texture(1.0f, 1.0f);
        v11.vertex(v10, (float)(param1 + param5), (float)param3, 0.0f).color(param9).texture(1.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v11.end());
    }

    public static void drawQuads(double param0, double param2, double param4, double param6) {
        BufferBuilder v8 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        v8.vertex((float)param0, (float)param2, 0.0f).texture(0.0f, 0.0f);
        v8.vertex((float)param0, (float)(param2 + param6), 0.0f).texture(0.0f, 1.0f);
        v8.vertex((float)(param0 + param4), (float)(param2 + param6), 0.0f).texture(1.0f, 1.0f);
        v8.vertex((float)(param0 + param4), (float)param2, 0.0f).texture(1.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v8.end());
    }

    public static void drawQuads(double param0, double param2) {
        BlurRenderer.drawQuads(0.0, 0.0, param0, param2);
    }

    public static boolean needsNewFramebuffer(BlurRenderer param0) {
        if (I == null || I.getWindow() == null) {
            return param0 == null;
        }
        int v1 = Math.max(I.getWindow().getFramebufferWidth(), 1);
        int v2 = Math.max(I.getWindow().getFramebufferHeight(), 1);
        return param0 == null || param0.textureWidth != v1 || param0.textureHeight != v2;
    }

    public static void drawQuads(DrawContext param0, int param1) {
        Vec2f v2 = ScaleUtil.method2887(I.getWindow().getScaledWidth(), I.getWindow().getScaledHeight());
        double v3 = v2.x;
        double v5 = v2.y;
        BlurRenderer.drawQuads(param0, 0.0, 0.0, v3, v5, param1);
    }

    public BlurRenderer(boolean param1) {
        super(param1);
        this.resize(1, 1);
    }
}



