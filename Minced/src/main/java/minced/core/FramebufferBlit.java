/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 */
package minced.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public class FramebufferBlit
{
    public static boolean field763;
    static String field764;
    static String field765;

    public static void method204() {
        field763 = true;
    }

    public static void method205(Framebuffer param0, int param1, int param2) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask((boolean)false);
        GlStateManager._viewport((int)0, (int)0, (int)param1, (int)param2);
        ShaderProgram v3 = Objects.requireNonNull(RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.BLIT_SCREEN), field764);
        v3.addSamplerTexture(field765, param0.getColorAttachment());
        BufferBuilder v4 = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.BLIT_SCREEN);
        v4.vertex(0.0f, 0.0f, 0.0f);
        v4.vertex(1.0f, 0.0f, 0.0f);
        v4.vertex(1.0f, 1.0f, 0.0f);
        v4.vertex(0.0f, 1.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v4.end());
        GlStateManager._depthMask((boolean)true);
        GlStateManager._colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
    }

    static {
        field763 = true;
        field764 = "Blit shader not loaded";
        field765 = "InSampler";
        FramebufferBlit.method204();
    }
}



