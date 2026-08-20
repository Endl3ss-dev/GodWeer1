/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.gl.ShaderProgramKey
 */
package minced.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.Shaders;
import minced.core.ClientUtil;
import minced.core.BlurRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;

public class KawaseBlur {
    BlurRenderer field12550;
    static int field12551 = 33984;
    static String field12552 = "offset";
    static String field12553 = "resolution";
    static float field12554;
    static float field12555;
    BlurRenderer field12556 = new BlurRenderer(false).setLinear();
    static String field12557 = "offset";
    static String field12558 = "resolution";
    static float field12559;
    static float field12560;
    public static KawaseBlur field12561;

    public void method3585(float param1, int param2) {
        int v7;
        Framebuffer v3 = ClientUtil.I.getFramebuffer();
        ShaderProgram v4 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8342);
        this.field12550.setup();
        RenderSystem.activeTexture((int)field12551);
        GlStateManager._bindTexture((int)v3.getColorAttachment());
        v4.getUniform(field12552).set(param1);
        v4.getUniform(field12553).set(field12554 / (float)ClientUtil.I.getWindow().getWidth(), field12555 / (float)ClientUtil.I.getWindow().getHeight());
        BlurRenderer.drawQuads();
        BlurRenderer[] v5 = new BlurRenderer[]{this.field12550, this.field12556};
        for (int v6 = 1; v6 < param2; ++v6) {
            v7 = v6 % 2;
            v5[v7].setup();
            v5[(v7 + 1) % 2].draw();
        }
        ShaderProgram v6 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8340);
        v6.getUniform(field12557).set(param1);
        v6.getUniform(field12558).set(field12559 / (float)ClientUtil.I.getWindow().getWidth(), field12560 / (float)ClientUtil.I.getWindow().getHeight());
        for (v7 = 0; v7 < param2; ++v7) {
            int v8 = v7 % 2;
            v5[(v8 + 1) % 2].setup();
            v5[v8].draw();
        }
        v3.beginWrite(false);
    }

    public KawaseBlur() {
        this.field12550 = new BlurRenderer(false).setLinear();
    }

    public static void method3586() {
        field12561 = new KawaseBlur();
    }

    static {
        field12554 = 3.0f;
        field12555 = 3.0f;
        field12559 = 3.0f;
        field12560 = 3.0f;
        KawaseBlur.method3586();
    }
}



