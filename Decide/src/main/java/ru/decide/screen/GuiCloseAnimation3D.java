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
 *  net.minecraft.class_1011
 *  net.minecraft.class_1011$class_1012
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_156
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_4184
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL15
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.screen;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_156;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_4184;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.Client;
import ru.decide.module.impl.display.ClickGui;
import ru.decide.utils.annotation.IMinecraft;

@Environment(value=EnvType.CLIENT)
public class GuiCloseAnimation3D
implements IMinecraft {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:GuiCloseAnimation3D");
    private static boolean active = false;
    private static long startTime = 0L;
    private static class_243 savedCameraPos;
    private static class_243 savedForward;
    private static Quaternionf savedCameraQuat;
    private static final long DURATION_FROZEN = 900L;
    private static final long DURATION_FADE = 600L;
    private static final long DURATION_TOTAL = 1500L;
    private static final float PANEL_DISTANCE = 2.6f;
    private static final class_2960 TEXTURE_ID;
    private static final RenderPipeline PIPELINE;
    private static Function<class_2960, class_1921> renderLayerFunc;

    public static boolean start() {
        int fbH;
        int captureY;
        ClickGui clickGui;
        if (active) {
            return true;
        }
        if (GuiCloseAnimation3D.mc.field_1724 == null || GuiCloseAnimation3D.mc.field_1687 == null) {
            return false;
        }
        ClickGui clickGui2 = clickGui = GuiCloseAnimation3D.mc.field_1724 != null ? Client.get().moduleManager().get(ClickGui.class) : null;
        if (clickGui == null) {
            return false;
        }
        float guiScale = clickGui.size.getValue().floatValue();
        class_4184 camera = GuiCloseAnimation3D.mc.field_1773.method_19418();
        savedCameraPos = camera.method_71156();
        savedForward = GuiCloseAnimation3D.mc.field_1724.method_5828(1.0f).method_1029();
        savedCameraQuat = new Quaternionf((Quaternionfc)camera.method_23767());
        int guiFbW = (int)(420.0f * guiScale * 2.0f);
        int guiFbH = (int)(280.0f * guiScale * 2.0f);
        int fbW = mc.method_22683().method_4489();
        int captureX = (fbW - guiFbW) / 2;
        if (!GuiCloseAnimation3D.captureRegion(captureX, captureY = ((fbH = mc.method_22683().method_4506()) - guiFbH) / 2, guiFbW, guiFbH)) {
            return false;
        }
        active = true;
        startTime = System.currentTimeMillis();
        mc.method_1507(null);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean captureRegion(int x, int y, int w, int h) {
        try {
            int glError;
            ByteBuffer buffer;
            int fbW = mc.method_22683().method_4489();
            int fbH = mc.method_22683().method_4506();
            x = Math.max(0, Math.min(x, fbW));
            y = Math.max(0, Math.min(y, fbH));
            w = Math.min(w, fbW - x);
            h = Math.min(h, fbH - y);
            if (w <= 0 || h <= 0) {
                return false;
            }
            int prevPackBuffer = GL11.glGetInteger((int)35053);
            int prevPackAlign = GL11.glGetInteger((int)3333);
            int prevRowLength = GL11.glGetInteger((int)3330);
            int prevSkipRows = GL11.glGetInteger((int)3331);
            int prevSkipPixels = GL11.glGetInteger((int)3332);
            try {
                GL15.glBindBuffer((int)35051, (int)0);
                GL11.glPixelStorei((int)3333, (int)4);
                GL11.glPixelStorei((int)3330, (int)0);
                GL11.glPixelStorei((int)3331, (int)0);
                GL11.glPixelStorei((int)3332, (int)0);
                for (int i = 0; i < 16 && GL11.glGetError() != 0; ++i) {
                }
                buffer = BufferUtils.createByteBuffer((int)(w * h * 4 + h * 64 + 256));
                GL11.glReadPixels((int)x, (int)y, (int)w, (int)h, (int)6408, (int)5121, (ByteBuffer)buffer);
                glError = GL11.glGetError();
            }
            finally {
                GL11.glPixelStorei((int)3333, (int)prevPackAlign);
                GL11.glPixelStorei((int)3330, (int)prevRowLength);
                GL11.glPixelStorei((int)3331, (int)prevSkipRows);
                GL11.glPixelStorei((int)3332, (int)prevSkipPixels);
                GL15.glBindBuffer((int)35051, (int)prevPackBuffer);
            }
            if (glError != 0) {
                LOGGER.warn("glReadPixels failed for {}x{} region: GL error 0x{}", new Object[]{w, h, Integer.toHexString(glError)});
                return false;
            }
            class_1011 image = new class_1011(class_1011.class_1012.field_4997, w, h, false);
            for (int row = 0; row < h; ++row) {
                for (int col = 0; col < w; ++col) {
                    int i = (row * w + col) * 4;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;
                    int a = buffer.get(i + 3) & 0xFF;
                    image.method_61941(col, h - 1 - row, a << 24 | r << 16 | g << 8 | b);
                }
            }
            mc.method_1531().method_4615(TEXTURE_ID);
            class_1043 backedTexture = new class_1043(() -> "gui_close_3d_capture", image);
            mc.method_1531().method_4616(TEXTURE_ID, (class_1044)backedTexture);
            renderLayerFunc = class_156.method_34866(id -> {
                class_12247 setup = class_12247.method_75927((RenderPipeline)PIPELINE).method_75934("Sampler0", id).method_75937().method_75929(1024).method_75938();
                return class_1921.method_75940((String)"gui_close_3d_layer", (class_12247)setup);
            });
            return true;
        }
        catch (Throwable e) {
            LOGGER.error("Failed to capture GUI region for close animation", e);
            active = false;
            return false;
        }
    }

    public static void render(Matrix4f viewMatrix, float tickDelta) {
        if (!active || GuiCloseAnimation3D.mc.field_1687 == null || GuiCloseAnimation3D.mc.field_1724 == null || renderLayerFunc == null) {
            return;
        }
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= 1500L) {
            GuiCloseAnimation3D.cleanup();
            return;
        }
        float t = elapsed < 900L ? 0.0f : (float)(elapsed - 900L) / 600.0f;
        float tCubed = (t = Math.clamp(t, 0.0f, 1.0f)) * t * t;
        float shrink = 1.0f - tCubed;
        if (shrink <= 0.01f) {
            GuiCloseAnimation3D.cleanup();
            return;
        }
        ClickGui clickGui = GuiCloseAnimation3D.mc.field_1724 != null ? Client.get().moduleManager().get(ClickGui.class) : null;
        float guiScale = clickGui != null ? clickGui.size.getValue().floatValue() : 1.0f;
        class_4184 camera = GuiCloseAnimation3D.mc.field_1773.method_19418();
        class_243 camPos = camera.method_71156();
        class_243 panelWorldPos = savedCameraPos.method_1019(savedForward.method_1021((double)2.6f));
        float px = (float)(panelWorldPos.field_1352 - camPos.field_1352);
        float py = (float)(panelWorldPos.field_1351 - camPos.field_1351);
        float pz = (float)(panelWorldPos.field_1350 - camPos.field_1350);
        float guiScreenW = 420.0f * guiScale;
        float guiScreenH = 280.0f * guiScale;
        float screenHeight = (float)mc.method_22683().method_4506() / 2.0f;
        float fovRad = (float)Math.toRadians(((Integer)GuiCloseAnimation3D.mc.field_1690.method_41808().method_41753()).intValue());
        float visibleHeight = 5.2f * (float)Math.tan((double)fovRad / 2.0);
        float halfH = visibleHeight * (guiScreenH / screenHeight) * shrink / 2.0f;
        float halfW = halfH * (guiScreenW / guiScreenH);
        Vector3f c0 = new Vector3f(-halfW, -halfH, 0.0f).rotate((Quaternionfc)savedCameraQuat);
        Vector3f c1 = new Vector3f(halfW, -halfH, 0.0f).rotate((Quaternionfc)savedCameraQuat);
        Vector3f c2 = new Vector3f(halfW, halfH, 0.0f).rotate((Quaternionfc)savedCameraQuat);
        Vector3f c3 = new Vector3f(-halfW, halfH, 0.0f).rotate((Quaternionfc)savedCameraQuat);
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)new class_9799(1024));
        class_4588 consumer = immediate.method_73477(renderLayerFunc.apply(TEXTURE_ID));
        consumer.method_22918((Matrix4fc)viewMatrix, px + c0.x, py + c0.y, pz + c0.z).method_22913(0.0f, 1.0f).method_22915(1.0f, 1.0f, 1.0f, 1.0f);
        consumer.method_22918((Matrix4fc)viewMatrix, px + c1.x, py + c1.y, pz + c1.z).method_22913(1.0f, 1.0f).method_22915(1.0f, 1.0f, 1.0f, 1.0f);
        consumer.method_22918((Matrix4fc)viewMatrix, px + c2.x, py + c2.y, pz + c2.z).method_22913(1.0f, 0.0f).method_22915(1.0f, 1.0f, 1.0f, 1.0f);
        consumer.method_22918((Matrix4fc)viewMatrix, px + c3.x, py + c3.y, pz + c3.z).method_22913(0.0f, 0.0f).method_22915(1.0f, 1.0f, 1.0f, 1.0f);
        immediate.method_22993();
    }

    public static boolean isActive() {
        return active;
    }

    public static void cleanup() {
        active = false;
        if (mc != null && mc.method_1531() != null) {
            mc.method_1531().method_4615(TEXTURE_ID);
        }
        renderLayerFunc = null;
    }

    static {
        TEXTURE_ID = class_2960.method_60655((String)"decide", (String)"gui_close_3d_capture");
        PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation("pipeline/gui_close_3d").withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withSampler("Sampler0").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withCull(false).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).build());
    }
}

