/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.ProjectionType
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.GlUniform
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.gl.SimpleFramebuffer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.Window
 *  net.minecraft.item.ItemStack
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 *  sg.ec.ПК$Ч
 */
package minced.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.BooleanSupplier;
import minced.core.ColorUtil;
import minced.core.Shaders;
import minced.core.ThemeColors;
import minced.core.Minced;
import minced.enums.MAIN;
import minced.module.visual.ShaderHands;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import sg.ec.pk;

public final class HandsShader {
    static float Шн;
    static int ШШ;
    static int ШЭ;

    static MinecraftClient I = MinecraftClient.getInstance();
    int field7750 = -1;
    Deque field7751;
    static float field7752;
    static float field7753;
    static float field7754;
    float[] field7755;
    static float field7756;
    static float field7757;
    static float field7758;
    Framebuffer field7759;
    static String field7760;
    static float field7761;
    boolean field7762;
    Framebuffer field7763;
    Framebuffer field7764;
    static float field7765;
    static float field7766;
    static float field7767;
    int field7768;
    int field7769;
    Framebuffer field7770;
    Framebuffer field7771;
    Framebuffer field7772;
    List<net.minecraft.client.gl.Framebuffer> field7773 = new ArrayList<>();
    int field7774 = -1;
    long field7775;
    static long field7776;
    long field7777;
    boolean field7778;
    boolean field7779;
    static int field7780;
    ByteBuffer field7781;
    boolean field7782;
    static String field7783;
    static String field7784;
    static String field7785;
    static float field7786;
    static float field7787;
    static int field7788;
    static int field7789;
    static int field7790;
    static int field7791;
    static int field7792;
    static int field7793;
    static int field7794;
    int field7795 = -1;
    int field7796 = -1;
    static float field7797;
    static float field7798;
    static float field7799;
    static float field7800;
    static String field7801;
    static String field7802;
    static String field7803;
    static HandsShader field7804;
    static int field7805;
    static int field7806;
    static int field7807;
    static float field7808;
    static float field7809;
    static float field7810;
    static float field7811;
    static float field7812;
    static float field7813;
    static float field7814;
    static float field7815;
    static float field7816;
    static float field7817;
    static float field7818;
    float field7819 = field7818;
    static long field7820;
    static int field7821;
    static float field7822;
    static float field7823;
    static float field7824;
    static float field7825;
    static float field7826;
    static long field7827;
    static float field7828;
    static float field7829;
    static float field7830;
    static float field7831;
    static float field7832;
    static String field7833;
    static String field7834;
    static String field7835;
    static String field7836;
    static String field7837;
    static String field7838;
    static String field7839;
    static String field7840;
    static int field7841;
    static int field7842;
    static String field7843;
    static String field7844;
    static String field7845;
    static String field7846;
    static String field7847;
    static long field7848;
    static float field7849;
    static String field7850;
    static String field7851;
    static String field7852;
    static String field7853;
    static String field7854;
    static String field7855;
    static String field7856;
    static String field7857;
    static String field7858;
    static String field7859;
    static String field7860;
    static String field7861;
    static String field7862;
    static String field7863;
    static String field7864;
    static String field7865;
    static String field7866;
    static int field7867;

    private int method1908() {
        Window v1 = I.getWindow();
        if (v1 != null) {
            return Math.max(1, v1.getFramebufferHeight());
        }
        return Math.max(1, this.field7750);
    }

    private boolean method1909() {
        Window v1 = I.getWindow();
        if (v1 == null) {
            return false;
        }
        int v2 = Math.max(1, v1.getFramebufferWidth());
        int v3 = Math.max(1, v1.getFramebufferHeight());
        this.field7751.push(new pk.ch(new Matrix4f((Matrix4fc)RenderSystem.getProjectionMatrix()), RenderSystem.getProjectionType()));
        GL11.glClear((int)256);
        Matrix4f v4 = new Matrix4f();
        v4.setOrtho(0.0f, (float)v2, (float)v3, 0.0f, field7752, field7753);
        RenderSystem.setProjectionMatrix((Matrix4f)v4, (ProjectionType)ProjectionType.ORTHOGRAPHIC);
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().identity();
        RenderSystem.getModelViewStack().translate(0.0f, 0.0f, field7754);
        return true;
    }

    private int method1910() {
        int v1 = Math.round(this.field7755[0] * field7756);
        int v2 = Math.round(this.field7755[1] * field7757);
        int v3 = Math.round(this.field7755[2] * field7758);
        return ColorUtil.method1858(v1, v2, v3);
    }

    private void method1911(ShaderHands param1, int param2, int param3, int param4, float param5) {
        RenderSystem.blendFuncSeparate((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE, (GlStateManager.SrcFactor)GlStateManager.SrcFactor.ZERO, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        ShaderProgram v6 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8379);
        if (v6 == null) {
            return;
        }
        RenderSystem.setShaderTexture((int)0, (int)param2);
        RenderSystem.setShaderTexture((int)1, (int)this.field7759.getColorAttachment());
        float[] v7 = ColorUtil.method1849(param3);
        float[] v8 = ColorUtil.method1849(param4);
        this.method1946(v6, param1, v7, v8);
        this.method1912(v6, field7760, 1.0f + param5 * field7761);
        this.method1944();
    }

    private void method1912(ShaderProgram param1, String param2, float param3) {
        GlUniform v4 = param1.getUniform(param2);
        if (v4 != null) {
            v4.set(param3);
        }
    }

    private void method1913() {
        if (this.field7762 && this.field7763 != null) {
            return;
        }
        this.method1918();
        if (this.field7763 == null) {
            return;
        }
        this.field7763.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.field7763.clear();
        this.field7763.beginWrite(true);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        ShaderProgram v1 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8391);
        if (v1 == null) {
            return;
        }
        RenderSystem.setShaderTexture((int)0, (int)this.field7764.getColorAttachment());
        RenderSystem.setShaderTexture((int)1, (int)this.field7759.getColorAttachment());
        this.method1944();
        this.field7762 = true;
    }

    private boolean method1914(ShaderHands param1) {
        if (param1 == null || !param1.isEnabled()) {
            return false;
        }
        boolean v2 = param1.method1609() > field7765;
        boolean v3 = param1.method1603() > field7766 && param1.method1605() > field7767;
        return v2 || v3;
    }

    private boolean method1915(ShaderHands param1) {
        if (!param1.method1591() || HandsShader.I.player == null) {
            return false;
        }
        int v2 = this.method1938();
        if (v2 == this.field7768) {
            return false;
        }
        this.field7768 = v2;
        this.field7769 = 0;
        if (this.field7770 != null) {
            this.field7770.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            this.field7770.clear();
        }
        if (this.field7771 != null) {
            this.field7771.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            this.field7771.clear();
        }
        return true;
    }

    public void method1916() {
        this.method1930();
        this.field7751.clear();
        this.method1927(this.field7772);
        this.method1927(this.field7764);
        this.method1927(this.field7759);
        this.method1927(this.field7763);
        this.method1927(this.field7770);
        this.method1927(this.field7771);
        this.field7772 = null;
        this.field7764 = null;
        this.field7759 = null;
        this.field7763 = null;
        this.field7770 = null;
        this.field7771 = null;
        for (Framebuffer v2 : this.field7773) {
            this.method1927(v2);
        }
        this.field7773.clear();
        this.field7774 = -1;
        this.field7750 = -1;
        this.field7775 = 0L;
        this.field7777 = field7776;
        this.field7778 = false;
        this.field7779 = false;
        this.field7762 = false;
        this.field7769 = 0;
        this.field7768 = field7780;
        this.field7781 = null;
        this.field7755[0] = 1.0f;
        this.field7755[1] = 1.0f;
        this.field7755[2] = 1.0f;
    }

    private Framebuffer method1917(Framebuffer param1, int param2, int param3) {
        if (param1 == null || param1.textureWidth != param2 || param1.textureHeight != param3) {
            this.method1927(param1);
            param1 = new SimpleFramebuffer(param2, param3, false);
            this.method1924(param1);
        }
        return param1;
    }

    private void method1918() {
        if (this.field7774 <= 0 || this.field7750 <= 0) {
            return;
        }
        if (this.field7763 == null || this.field7763.textureWidth != this.field7774 || this.field7763.textureHeight != this.field7750) {
            this.method1927(this.field7763);
            this.field7763 = new SimpleFramebuffer(this.field7774, this.field7750, false);
            this.method1924(this.field7763);
        }
    }

    private void method1919() {
        if (this.field7774 <= 0 || this.field7750 <= 0) {
            return;
        }
        int v1 = Math.max(2, this.field7774 / 2);
        int v2 = Math.max(2, this.field7750 / 2);
        boolean v3 = this.field7770 == null || this.field7771 == null || this.field7770.textureWidth != v1 || this.field7770.textureHeight != v2;
        this.field7770 = this.method1917(this.field7770, v1, v2);
        this.field7771 = this.method1917(this.field7771, v1, v2);
        if (v3 || !this.field7779) {
            this.field7770.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            this.field7770.clear();
            this.field7771.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            this.field7771.clear();
            this.field7779 = true;
            this.field7775 = 0L;
        }
    }

    public void method1920() {
        ShaderHands v1 = this.method1923();
        if (!this.method1914(v1)) {
            this.method1930();
            return;
        }
        this.method1937();
        if (this.field7772 == null) {
            return;
        }
        this.method1925(this.field7772);
        this.field7782 = true;
    }

    private int cfr_renamed_125() {
        Window v1 = I.getWindow();
        if (v1 != null) {
            return Math.max(1, v1.getFramebufferWidth());
        }
        return Math.max(1, this.field7774);
    }

    private void method1921(ShaderProgram param1, int param2, int param3, float param4) {
        this.method1928(param1, field7783, Math.max(1, param2), Math.max(1, param3));
        this.method1928(param1, field7784, param4, param4);
        this.method1928(param1, field7785, field7786 / (float)Math.max(1, param2), field7787 / (float)Math.max(1, param3));
    }

    public void method1922() {
        ShaderHands v1 = this.method1923();
        if (!this.method1914(v1)) {
            this.method1930();
            return;
        }
        this.method1937();
        if (this.field7772 == null || this.field7764 == null || this.field7759 == null || !this.field7782) {
            return;
        }
        this.method1925(this.field7764);
        this.method1931(v1);
    }

    private ShaderHands method1923() {
        if (Minced.getInstance() == null || Minced.getInstance().getModuleManager() == null) {
            return null;
        }
        return (ShaderHands)Minced.getInstance().getModuleManager().method187(ShaderHands.class);
    }

    private void method1924(Framebuffer param1) {
        if (param1 == null) {
            return;
        }
        int v2 = param1.getColorAttachment();
        if (v2 == 0) {
            return;
        }
        RenderSystem.bindTexture((int)v2);
        GL11.glTexImage2D((int)3553, (int)0, (int)field7788, (int)param1.textureWidth, (int)param1.textureHeight, (int)0, (int)6408, (int)5131, (ByteBuffer)null);
        this.O(v2);
    }

    private void method1925(Framebuffer param1) {
        int v2 = GL11.glGetInteger((int)field7789);
        int v3 = GL11.glGetInteger((int)field7790);
        GL30.glBindFramebuffer((int)field7791, (int)HandsShader.I.getFramebuffer().fbo);
        GL30.glBindFramebuffer((int)field7792, (int)param1.fbo);
        GL30.glBlitFramebuffer((int)0, (int)0, (int)this.field7774, (int)this.field7750, (int)0, (int)0, (int)this.field7774, (int)this.field7750, (int)16640, (int)9728);
        GL30.glBindFramebuffer((int)field7793, (int)v2);
        GL30.glBindFramebuffer((int)field7794, (int)v3);
        I.getFramebuffer().beginWrite(true);
    }

    private void method1926() {
        if (this.field7763 == null) {
            return;
        }
        I.getFramebuffer().beginWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        if (RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR) != null) {
            RenderSystem.setShaderTexture((int)0, (int)this.field7763.getColorAttachment());
            this.method1944();
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private void method1927(Framebuffer param1) {
        if (param1 == null) {
            return;
        }
        param1.delete();
    }

    private void method1928(ShaderProgram param1, String param2, float param3, float param4) {
        GlUniform v5 = param1.getUniform(param2);
        if (v5 != null) {
            v5.set(param3, param4);
        }
    }

    private void method1929(ShaderHands param1) {
        ShaderProgram v2 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8375);
        if (v2 == null) {
            return;
        }
        this.field7759.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.field7759.clear();
        this.field7759.beginWrite(true);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture((int)0, (int)this.field7772.getColorAttachment());
        RenderSystem.setShaderTexture((int)1, (int)this.field7764.getColorAttachment());
        int v3 = this.field7772.getDepthAttachment();
        int v4 = this.field7764.getDepthAttachment();
        if (v3 != 0 && v3 != this.field7795) {
            this.method1948(v3);
            this.field7795 = v3;
        }
        if (v4 != 0 && v4 != this.field7796) {
            this.method1948(v4);
            this.field7796 = v4;
        }
        RenderSystem.setShaderTexture((int)2, (int)v3);
        RenderSystem.setShaderTexture((int)3, (int)v4);
        this.method1944();
        float v5 = param1.method1609();
        float v6 = param1.method1603();
        float v7 = param1.method1605();
        float v8 = param1.method1582();
        int v9 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        int v10 = ColorUtil.method1851(v9, field7797);
        boolean v11 = v5 > field7798;
        boolean v12 = v6 > field7799 && v7 > field7800;
        boolean v13 = param1.method1591() && v12 && !param1.method1584();
        boolean v14 = this.method1915(param1);
        if (param1.method1584()) {
            if (param1.method1591()) {
                this.method1913();
            }
            if (v11) {
                int v15 = this.method1943(param1, v8);
                I.getFramebuffer().beginWrite(true);
                RenderSystem.enableBlend();
                RenderSystem.disableDepthTest();
                if (param1.method1589()) {
                    this.method1936(param1, v15, v9, v10);
                    this.method1926();
                } else {
                    this.method1911(param1, v15, v9, v10, v5);
                }
            }
            this.method1945(param1, v9, v10, v5, v6, v7, v8);
            return;
        }
        int v15 = 0;
        if (v11) {
            v15 = this.method1943(param1, v8);
        }
        I.getFramebuffer().beginWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)false);
        RenderSystem.disableDepthTest();
        if (v11) {
            if (param1.method1589()) {
                this.method1936(param1, v15, v9, v10);
                this.method1926();
            } else {
                this.method1911(param1, v15, v9, v10, v5);
            }
        }
        if (v12) {
            ShaderProgram v16 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8377);
            if (v16 == null) {
                this.method1947();
                return;
            }
            if (v13) {
                this.method1935(param1, v14);
                v9 = this.method1910();
            }
            RenderSystem.blendFuncSeparate((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SrcFactor)GlStateManager.SrcFactor.ZERO, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
            RenderSystem.setShaderTexture((int)0, (int)this.field7759.getColorAttachment());
            float[] v17 = ColorUtil.method1849(v9);
            this.method1932(v16, field7801, v17[0], v17[1], v17[2]);
            this.method1912(v16, field7802, v6);
            this.method1912(v16, field7803, v7);
            this.method1944();
        }
        this.method1947();
    }

    public void method1930() {
        this.field7782 = false;
        this.field7795 = -1;
        this.field7796 = -1;
    }

    private void method1931(ShaderHands param1) {
        this.field7762 = false;
        RenderSystem.disableScissor();
        boolean v2 = this.method1909();
        if (v2) {
            this.method1929(param1);
        }
        if (v2) {
            this.method1934();
        }
        RenderSystem.disableScissor();
        this.method1930();
    }

    private void method1932(ShaderProgram param1, String param2, float param3, float param4, float param5) {
        GlUniform v6 = param1.getUniform(param2);
        if (v6 != null) {
            v6.set(param3, param4, param5);
        }
    }

    public static HandsShader method1933() {
        if (field7804 == null) {
            field7804 = new HandsShader();
        }
        return field7804;
    }

    private void method1934() {
        if (this.field7751.isEmpty()) {
            return;
        }
        pk.ch v1 = (pk.ch)this.field7751.pop();
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.setProjectionMatrix((Matrix4f)v1.ь, (ProjectionType)v1.н);
    }

    private void method1935(ShaderHands param1, boolean param2) {
        float v20;
        float v19;
        float v18;
        float v17;
        Framebuffer v3;
        if (!param2 && this.field7769++ % 24 != 0) {
            return;
        }
        if (param2) {
            this.field7769 = 1;
        }
        Framebuffer framebuffer = v3 = !this.field7773.isEmpty() ? (Framebuffer)this.field7773.get(this.field7773.size() - 1) : this.field7764;
        if (v3 == null) {
            return;
        }
        int v4 = Math.min(48, v3.textureWidth);
        int v5 = Math.min(48, v3.textureHeight);
        if (v4 <= 0 || v5 <= 0) {
            return;
        }
        int v6 = Math.max(0, (v3.textureWidth - v4) / 2);
        int v7 = Math.max(0, v3.textureHeight / 5);
        int v8 = v4 * v5 * 4;
        if (this.field7781 == null || this.field7781.capacity() < v8) {
            this.field7781 = BufferUtils.createByteBuffer((int)v8);
        }
        this.field7781.clear();
        int v9 = GL11.glGetInteger((int)field7805);
        GL30.glBindFramebuffer((int)field7806, (int)v3.fbo);
        GL11.glReadPixels((int)v6, (int)v7, (int)v4, (int)v5, (int)6408, (int)5121, (ByteBuffer)this.field7781);
        GL30.glBindFramebuffer((int)field7807, (int)v9);
        float v10 = 0.0f;
        float v11 = 0.0f;
        float v12 = 0.0f;
        float v13 = 0.0f;
        int v14 = v4 * v5;
        for (int v15 = 0; v15 < v14; ++v15) {
            int v16 = v15 * 4;
            v17 = (float)(this.field7781.get(v16) & 0xFF) / field7808;
            v18 = (float)(this.field7781.get(v16 + 1) & 0xFF) / field7809;
            v19 = (float)(this.field7781.get(v16 + 2) & 0xFF) / field7810;
            v20 = (float)(this.field7781.get(v16 + 3) & 0xFF) / field7811;
            if (v20 < field7812) continue;
            v10 += v17 * v20;
            v11 += v18 * v20;
            v12 += v19 * v20;
            v13 += v20;
        }
        if (v13 < field7813) {
            return;
        }
        float v15 = v10 / v13;
        float v16 = v11 / v13;
        v17 = v12 / v13;
        v18 = Math.max(v15, Math.max(v16, v17));
        if (v18 > field7814) {
            v15 /= v18;
            v16 /= v18;
            v17 /= v18;
        }
        v19 = param1.method1588();
        v20 = field7815 * v15 + field7816 * v16 + field7817 * v17;
        this.field7755[0] = HandsShader.method1939(v20 + (v15 - v20) * v19);
        this.field7755[1] = HandsShader.method1939(v20 + (v16 - v20) * v19);
        this.field7755[2] = HandsShader.method1939(v20 + (v17 - v20) * v19);
    }

    public HandsShader() {
        this.field7777 = field7820;
        this.field7755 = new float[]{1.0f, 1.0f, 1.0f};
        this.field7768 = field7821;
        this.field7751 = new ArrayDeque();
    }

    private void method1936(ShaderHands param1, int param2, int param3, int param4) {
        boolean v14;
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        this.method1919();
        if (this.field7770 == null || this.field7771 == null) {
            this.method1911(param1, param2, param3, param4, param1.method1609());
            return;
        }
        long v5 = System.currentTimeMillis();
        float v7 = this.field7775 > 0L ? Math.min(field7822, (float)(v5 - this.field7775) / field7823) : field7824;
        this.field7775 = v5;
        float v8 = this.field7819 = this.field7819 * field7825 + v7 * field7826;
        float v9 = (float)(v5 % field7827) / field7828;
        float v10 = field7829;
        float v11 = param1.method1606() * v8;
        float v12 = param1.method1598();
        float v13 = (float)(Math.sin(v9 * v10) - Math.sin((v9 - v8) * v10)) * v12;
        boolean bl = v14 = HandsShader.I.player != null && HandsShader.I.player.handSwinging;
        if (param1.method1592() && v14 && !this.field7778) {
            this.field7777 = v5;
        }
        this.field7778 = v14;
        float v15 = (float)(v5 - this.field7777) / field7830;
        float v16 = Math.max(0.0f, 1.0f - v15 / field7831);
        float v17 = v16 * param1.method1610() * field7832;
        float v18 = param1.method1601();
        float[] v19 = ColorUtil.method1849(param3);
        float[] v20 = ColorUtil.method1849(param4);
        this.field7771.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.field7771.clear();
        this.field7771.beginWrite(true);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        ShaderProgram v21 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8387);
        if (v21 != null) {
            RenderSystem.setShaderTexture((int)0, (int)this.field7770.getColorAttachment());
            this.method1928(v21, field7833, v13, v11);
            this.method1928(v21, field7834, Math.max(1, this.field7770.textureWidth), Math.max(1, this.field7770.textureHeight));
            this.method1912(v21, field7835, param1.method1599() + v17);
            this.method1912(v21, field7836, v9);
            this.method1912(v21, field7837, v8);
            this.method1912(v21, field7838, param1.method1587());
            this.method1912(v21, field7839, param1.method1604());
            this.method1944();
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.ONE, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        ShaderProgram v22 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8389);
        if (v22 != null) {
            RenderSystem.setShaderTexture((int)0, (int)param2);
            this.method1946(v22, param1, v19, v20);
            this.method1912(v22, field7840, v18);
            this.method1944();
        }
        if (param1.method1596() && this.field7763 != null) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)param1.method1586());
            if (RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR) != null) {
                RenderSystem.setShaderTexture((int)0, (int)this.field7763.getColorAttachment());
                this.method1944();
            }
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        I.getFramebuffer().beginWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)false);
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.ONE, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        if (RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR) != null) {
            RenderSystem.setShaderTexture((int)0, (int)this.field7771.getColorAttachment());
            this.method1944();
        }
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Framebuffer v23 = this.field7770;
        this.field7770 = this.field7771;
        this.field7771 = v23;
    }

    private void O(int param1) {
        RenderSystem.bindTexture((int)param1);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
        GL11.glTexParameteri((int)3553, (int)10242, (int)field7841);
        GL11.glTexParameteri((int)3553, (int)10243, (int)field7842);
        FloatBuffer v2 = BufferUtils.createFloatBuffer((int)4);
        v2.put(0.0f).put(0.0f).put(0.0f).put(0.0f).flip();
        GL11.glTexParameterfv((int)3553, (int)4100, (FloatBuffer)v2);
        RenderSystem.bindTexture((int)0);
    }

    private void method1937() {
        if (I.getWindow() == null) {
            return;
        }
        int v1 = I.getWindow().getFramebufferWidth();
        int v2 = I.getWindow().getFramebufferHeight();
        if (v1 == this.field7774 && v2 == this.field7750 && this.field7772 != null && this.field7764 != null && this.field7759 != null) {
            return;
        }
        this.method1927(this.field7772);
        this.method1927(this.field7764);
        this.method1927(this.field7759);
        this.method1927(this.field7763);
        this.method1927(this.field7770);
        this.method1927(this.field7771);
        this.field7763 = null;
        this.field7770 = null;
        this.field7771 = null;
        this.field7779 = false;
        for (Framebuffer v4 : this.field7773) {
            this.method1927(v4);
        }
        this.field7773.clear();
        this.field7772 = new SimpleFramebuffer(v1, v2, true);
        this.field7764 = new SimpleFramebuffer(v1, v2, true);
        this.field7759 = new SimpleFramebuffer(v1, v2, true);
        this.field7774 = v1;
        this.field7750 = v2;
        this.field7795 = -1;
        this.field7796 = -1;
    }

    private int method1938() {
        int v1 = HandsShader.I.player.getInventory().selectedSlot;
        v1 = 31 * v1 + this.method1942(HandsShader.I.player.getMainHandStack());
        v1 = 31 * v1 + this.method1942(HandsShader.I.player.getOffHandStack());
        return v1;
    }

    private static float method1939(float param0) {
        return param0 < 0.0f ? 0.0f : Math.min(param0, 1.0f);
    }

    private int method1940(int param1, int param2, float param3) {
        Framebuffer v8;
        int v7;
        this.method1941(param1);
        if (this.field7773.isEmpty()) {
            return param2;
        }
        int v4 = param2;
        ShaderProgram v5 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8381);
        ShaderProgram v6 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8383);
        if (v5 == null || v6 == null) {
            return v4;
        }
        for (v7 = 0; v7 < param1; ++v7) {
            v8 = (Framebuffer)this.field7773.get(v7);
            v8.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            v8.clear();
            v8.beginWrite(true);
            RenderSystem.setShader((ShaderProgramKey)Shaders.field8381);
            RenderSystem.setShaderTexture((int)0, (int)v4);
            this.method1921(v5, v8.textureWidth, v8.textureHeight, (1.0f + (float)v7) * param3);
            this.method1944();
            v4 = v8.getColorAttachment();
        }
        for (v7 = param1 - 1; v7 >= 1; --v7) {
            v8 = (Framebuffer)this.field7773.get(v7 - 1);
            v8.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            v8.clear();
            v8.beginWrite(true);
            RenderSystem.setShader((ShaderProgramKey)Shaders.field8383);
            RenderSystem.setShaderTexture((int)0, (int)v4);
            this.method1921(v6, v8.textureWidth, v8.textureHeight, (1.0f + (float)v7) * param3);
            this.method1932(v6, field7843, 1.0f, 1.0f, 1.0f);
            this.method1944();
            v4 = v8.getColorAttachment();
        }
        I.getFramebuffer().beginWrite(true);
        return v4;
    }

    private void method1941(int param1) {
        int v2;
        while (this.field7773.size() > param1) {
            v2 = this.field7773.size() - 1;
            this.method1927((Framebuffer)this.field7773.remove(v2));
        }
        for (v2 = 0; v2 < param1; ++v2) {
            Framebuffer v5;
            int v3 = Math.max(2, this.field7774 >> v2 + 1);
            int v4 = Math.max(2, this.field7750 >> v2 + 1);
            if (v2 >= this.field7773.size()) {
                v5 = new SimpleFramebuffer(v3, v4, false);
                this.method1924(v5);
                this.field7773.add(v5);
                continue;
            }
            v5 = (Framebuffer)this.field7773.get(v2);
            if (v5.textureWidth == v3 && v5.textureHeight == v4) continue;
            this.method1927(v5);
            v5 = new SimpleFramebuffer(v3, v4, false);
            this.method1924(v5);
            this.field7773.set(v2, v5);
        }
    }

    private int method1942(ItemStack param1) {
        if (param1 == null || param1.isEmpty()) {
            return 0;
        }
        int v2 = param1.getItem().hashCode();
        v2 = 31 * v2 + param1.getCount();
        v2 = 31 * v2 + param1.getComponents().hashCode();
        return v2;
    }

    private int method1943(ShaderHands param1, float param2) {
        int v3 = param1.method1608();
        float v4 = param1.method1600();
        int v5 = this.field7759.getColorAttachment();
        if (param1.method1589() || param1.method1591()) {
            this.method1913();
            if (this.field7763 != null) {
                v5 = this.field7763.getColorAttachment();
            }
        }
        return this.method1940(v3, v5, v4);
    }

    private void method1944() {
        float v1 = this.cfr_renamed_125();
        float v2 = this.method1908();
        BufferBuilder v3 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        v3.vertex(0.0f, v2, 0.0f).texture(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f);
        v3.vertex(v1, v2, 0.0f).texture(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f);
        v3.vertex(v1, 0.0f, 0.0f).texture(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f);
        v3.vertex(0.0f, 0.0f, 0.0f).texture(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v3.end());
    }

    private void method1945(ShaderHands param1, int param2, int param3, float param4, float param5, float param6, float param7) {
        boolean v9;
        ShaderProgram v8 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8385);
        if (v8 == null) {
            return;
        }
        I.getFramebuffer().beginWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderTexture((int)0, (int)this.field7759.getColorAttachment());
        boolean bl = v9 = param1.method1591() && this.field7763 != null;
        if (v9) {
            RenderSystem.setShaderTexture((int)1, (int)this.field7763.getColorAttachment());
        } else {
            RenderSystem.setShaderTexture((int)1, (int)this.field7759.getColorAttachment());
        }
        this.method1928(v8, field7844, 1.0f / (float)Math.max(1, I.getWindow().getFramebufferWidth()), 1.0f / (float)Math.max(1, I.getWindow().getFramebufferHeight()));
        float[] v10 = ColorUtil.method1849(param2);
        float[] v11 = ColorUtil.method1849(param3);
        this.method1932(v8, field7845, v10[0], v10[1], v10[2]);
        this.method1932(v8, field7846, v11[0], v11[1], v11[2]);
        this.method1912(v8, field7847, (float)(System.currentTimeMillis() % field7848) / field7849);
        this.method1912(v8, field7850, param1.method1607());
        this.method1912(v8, field7851, param1.method1583());
        this.method1912(v8, field7852, param7);
        this.method1912(v8, field7853, param4);
        this.method1912(v8, field7854, param5);
        this.method1912(v8, field7855, param6);
        this.method1912(v8, field7856, 0.0f);
        this.method1912(v8, field7857, v9 ? 1.0f : 0.0f);
        this.method1912(v8, field7858, param1.method1588());
        this.method1944();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        this.method1947();
    }

    private void method1946(ShaderProgram param1, ShaderHands param2, float[] param3, float[] param4) {
        if (param2.method1591()) {
            this.method1912(param1, field7859, 1.0f);
            this.method1912(param1, field7860, param2.method1588());
            this.method1932(param1, field7861, param3[0], param3[1], param3[2]);
            this.method1932(param1, field7862, param4[0], param4[1], param4[2]);
            return;
        }
        this.method1912(param1, field7863, 0.0f);
        this.method1912(param1, field7864, 1.0f);
        this.method1932(param1, field7865, param3[0], param3[1], param3[2]);
        this.method1932(param1, field7866, param4[0], param4[1], param4[2]);
    }

    private void method1947() {
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (int)0);
        RenderSystem.setShaderTexture((int)1, (int)0);
        RenderSystem.setShaderTexture((int)2, (int)0);
        RenderSystem.setShaderTexture((int)3, (int)0);
        I.getFramebuffer().beginWrite(true);
    }

    private void method1948(int param1) {
        RenderSystem.bindTexture((int)param1);
        GL11.glTexParameteri((int)3553, (int)field7867, (int)0);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        RenderSystem.bindTexture((int)0);
    }

    static {
        Шн = 0.0f;
        ШШ = 0;
        ШЭ = 0;
        field7818 = 0.016666668f;
        field7820 = -10000L;
        field7821 = Integer.MIN_VALUE;
        field7752 = 1000.0f;
        field7753 = 3000.0f;
        field7754 = -2000.0f;
        field7797 = 0.35f;
        field7798 = 0.001f;
        field7799 = 0.001f;
        field7800 = 0.001f;
        field7801 = "color";
        field7802 = "fill";
        field7803 = "alpha";
        field7776 = -10000L;
        field7780 = Integer.MIN_VALUE;
        field7843 = "color";
        field7760 = "exposure";
        field7761 = 1.8f;
        field7822 = 0.05f;
        field7823 = 1000.0f;
        field7824 = 0.016666668f;
        field7825 = 0.7f;
        field7826 = 0.3f;
        field7827 = 100000L;
        field7828 = 1000.0f;
        field7829 = 5.0265484f;
        field7830 = 1000.0f;
        field7831 = 0.35f;
        field7832 = 0.025f;
        field7833 = "offset";
        field7834 = "texSize";
        field7835 = "fade";
        field7836 = "t";
        field7837 = "dt";
        field7838 = "turb";
        field7839 = "flickAmp";
        field7840 = "exposure";
        field7844 = "texelSize";
        field7845 = "color";
        field7846 = "color2";
        field7847 = "time";
        field7848 = 100000L;
        field7849 = 1000.0f;
        field7850 = "speed";
        field7851 = "scale";
        field7852 = "outline";
        field7853 = "glow";
        field7854 = "fill";
        field7855 = "alpha";
        field7856 = "outlineOnly";
        field7857 = "autoColor";
        field7858 = "saturation";
        field7789 = 36010;
        field7790 = 36006;
        field7791 = 36008;
        field7792 = 36009;
        field7793 = 36008;
        field7794 = 36009;
        field7867 = 34892;
        field7788 = 34842;
        field7841 = 33069;
        field7842 = 33069;
        field7765 = 0.001f;
        field7766 = 0.001f;
        field7767 = 0.001f;
        field7859 = "autoColor";
        field7860 = "saturation";
        field7861 = "color";
        field7862 = "color2";
        field7863 = "autoColor";
        field7864 = "saturation";
        field7865 = "color";
        field7866 = "color2";
        field7805 = 36010;
        field7806 = 36008;
        field7807 = 36008;
        field7808 = 255.0f;
        field7809 = 255.0f;
        field7810 = 255.0f;
        field7811 = 255.0f;
        field7812 = 0.02f;
        field7813 = 0.001f;
        field7814 = 0.001f;
        field7815 = 0.299f;
        field7816 = 0.587f;
        field7817 = 0.114f;
        field7756 = 255.0f;
        field7757 = 255.0f;
        field7758 = 255.0f;
        field7783 = "uSize";
        field7784 = "uOffset";
        field7785 = "uHalfPixel";
        field7786 = 0.5f;
        field7787 = 0.5f;
    }
}



