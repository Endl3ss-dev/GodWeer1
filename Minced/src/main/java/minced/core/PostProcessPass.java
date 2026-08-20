/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  org.joml.Matrix4f
 */
package minced.core;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import java.util.function.Supplier;
import minced.core.PostEffectPipeline;
import minced.core.ShaderProgramLoader;
import minced.core.ClientUtil;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;

public class PostProcessPass
implements Consumer {
    static boolean field8038;

    @Override
    public void accept(Object param1) {
        this.method2058();
    }

    Framebuffer field8039;
    Framebuffer field8040;
    ShaderProgramLoader field8041;
    static String field8042;
    static String field8043;
    Matrix4f field8044 = new Matrix4f();
    static String field8045;
    static String field8046;
    static String field8047;
    static float field8048;
    static float field8049;
    static float field8050;
    static float field8051;
    boolean field8052 = false;
    Supplier field8053 = null;
    String field8054;
    boolean field8055;
    int field8056;

    public static void method2057() {
        field8038 = true;
    }

    public void method2058() {
        float v1 = this.field8039.textureWidth;
        float v2 = this.field8039.textureHeight;
        float v3 = this.field8040.textureWidth;
        float v4 = this.field8040.textureHeight;
        float v5 = ClientUtil.I.getWindow().getFramebufferWidth();
        float v6 = ClientUtil.I.getWindow().getFramebufferHeight();
        this.field8039.endWrite();
        RenderSystem.viewport((int)0, (int)0, (int)((int)v3), (int)((int)v4));
        this.field8041.method2092(field8042, () -> this.field8039.getColorAttachment());
        this.field8041.method2090(field8043).set(this.field8044);
        this.field8041.method2090(field8045).set(v1, v2);
        this.field8041.method2090(field8046).set(v3, v4);
        this.field8041.method2090(field8047).set(v5, v6);
        this.field8041.method2097();
        if (field8038) {
            this.field8040.clear();
        }
        this.field8040.beginWrite(false);
        RenderSystem.depthFunc((int)519);
        BufferBuilder v7 = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        v7.vertex(0.0f, 0.0f, field8048).vertex(v3, 0.0f, field8049).vertex(v3, v4, field8050).vertex(0.0f, v4, field8051);
        BufferRenderer.draw((BuiltBuffer)v7.end());
        RenderSystem.depthFunc((int)515);
        this.field8041.method2088();
        this.field8040.endWrite();
        this.field8039.endRead();
    }

    public PostProcessPass(PostEffectPipeline param1, String param2, Framebuffer param3, Framebuffer param4, boolean param5, String param6) {
        this.field8054 = param2;
        this.field8039 = param3;
        this.field8040 = param4;
        this.field8055 = param5;
        this.field8041 = new ShaderProgramLoader(param1, param2, param6);
        this.field8056 = this.field8052 ? 9729 : 9728;
    }

    static {
        field8038 = true;
        field8042 = "DiffuseSampler";
        field8043 = "ProjMat";
        field8045 = "InSize";
        field8046 = "OutSize";
        field8047 = "ScreenSize";
        field8048 = 500.0f;
        field8049 = 500.0f;
        field8050 = 500.0f;
        field8051 = 500.0f;
        PostProcessPass.method2057();
    }
}



