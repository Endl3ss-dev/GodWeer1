/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_1011$class_1012
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_10799
 *  net.minecraft.class_2960
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL15
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.screen;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_10799;
import net.minecraft.class_2960;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.Client;
import ru.decide.module.impl.display.ClickGui;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.render.others.RenderSampler;
import ru.decide.utils.render.voronoi.VoronoiOfQuad;

@Environment(value=EnvType.CLIENT)
public class GuiCloseAnimationShatter
implements IMinecraft {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:GuiCloseAnimationShatter");
    private static final String TEXTURE_NAME = "gui_close_shatter_capture";
    private static final class_2960 TEXTURE_ID = class_2960.method_60655((String)"decide", (String)"gui_close_shatter_capture");
    private static final class_2960 SHADER_ID = class_2960.method_60655((String)"decide", (String)"post/guishatter/shatter");
    private static final VertexFormat VERTEX_FORMAT = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("Color", VertexFormatElement.COLOR).build();
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/guishatter")).withVertexShader(SHADER_ID).withFragmentShader(SHADER_ID).withVertexFormat(VERTEX_FORMAT, VertexFormat.class_5596.field_27379).withSampler("uGui").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final int MIN_SHARDS = 22;
    private static final int SHARD_SPREAD = 7;
    private static final float RELAX_STRENGTH = 0.45f;
    private static final float SEED_JITTER = 0.32f;
    private static final float MIN_DISTANCE = 0.68f;
    private static final float MARGIN = 0.004f;
    private static final float EXPAND = 0.34f;
    private static final float SPREAD = 0.028f;
    private static final float CURL_DEGREES = 26.0f;
    private static final float CURL_DEPTH = 0.1f;
    private static final long DURATION_MS = 700L;
    private static final float PAD_DESIGN = 14.0f;
    private static final int VERTEX_BYTES = 24;
    private static final int MAX_VERTICES = 2048;
    private static boolean active = false;
    private static long startTime = 0L;
    private static final List<VoronoiOfQuad.Polygon> shards = new ArrayList<VoronoiOfQuad.Polygon>();
    private static float[] baseAngle = new float[0];
    private static float[] baseRadius = new float[0];
    private static float[] fade = new float[0];
    private static float[] radial = new float[0];
    private static float centerX;
    private static float centerY;
    private static float halfExtentX;
    private static float halfExtentY;
    private static float aspect;
    private static float nx1;
    private static float ny1;
    private static float nx2;
    private static float ny2;
    private static GpuBuffer vertexBuffer;
    private static ByteBuffer vertexData;
    private static boolean ready;

    private GuiCloseAnimationShatter() {
    }

    public static boolean start() {
        if (active) {
            return true;
        }
        if (mc == null || GuiCloseAnimationShatter.mc.field_1724 == null || GuiCloseAnimationShatter.mc.field_1687 == null) {
            return false;
        }
        ClickGui clickGui = Client.get().moduleManager().get(ClickGui.class);
        if (clickGui == null) {
            return false;
        }
        float guiScale = clickGui.size.getValue().floatValue();
        if (guiScale <= 0.0f) {
            return false;
        }
        int fbW = mc.method_22683().method_4489();
        int fbH = mc.method_22683().method_4506();
        if (fbW <= 0 || fbH <= 0) {
            return false;
        }
        float pad = 14.0f * guiScale * 2.0f;
        int panelW = (int)(420.0f * guiScale * 2.0f);
        int panelH = (int)(280.0f * guiScale * 2.0f);
        int capX = (fbW - panelW) / 2;
        int capY = (fbH - panelH) / 2;
        int capW = panelW;
        int capH = panelH;
        int rx = Math.max(0, capX - (int)pad);
        int ry = Math.max(0, capY - (int)pad);
        int rw = Math.min(fbW - rx, capW + (int)(pad * 2.0f));
        int rh = Math.min(fbH - ry, capH + (int)(pad * 2.0f));
        if (rw <= 4 || rh <= 4) {
            return false;
        }
        if (!GuiCloseAnimationShatter.captureRegion(rx, ry, rw, rh)) {
            return false;
        }
        if (!GuiCloseAnimationShatter.buildShards(rx, ry, rw, rh, fbW, fbH)) {
            GuiCloseAnimationShatter.releaseTexture();
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
            GuiCloseAnimationShatter.releaseTexture();
            mc.method_1531().method_4616(TEXTURE_ID, (class_1044)new class_1043(() -> TEXTURE_NAME, image));
            return true;
        }
        catch (Throwable e) {
            LOGGER.error("Failed to capture GUI region for shatter animation", e);
            return false;
        }
    }

    private static boolean buildShards(int rx, int ry, int rw, int rh, int fbW, int fbH) {
        nx1 = GuiCloseAnimationShatter.clamp01((float)rx / (float)fbW);
        ny1 = GuiCloseAnimationShatter.clamp01((float)ry / (float)fbH);
        nx2 = GuiCloseAnimationShatter.clamp01((float)(rx + rw) / (float)fbW);
        ny2 = GuiCloseAnimationShatter.clamp01((float)(ry + rh) / (float)fbH);
        centerX = (nx1 + nx2) * 0.5f;
        centerY = (ny1 + ny2) * 0.5f;
        halfExtentX = Math.max(0.001f, (nx2 - nx1) * 0.5f);
        halfExtentY = Math.max(0.001f, (ny2 - ny1) * 0.5f);
        aspect = Math.max(0.001f, (float)fbW / (float)fbH);
        Random random = new Random();
        int count = 22 + random.nextInt(7);
        VoronoiOfQuad voronoi = VoronoiOfQuad.spread(nx1, ny1, nx2, ny2, nx1 - 0.004f, ny1 - 0.004f, nx2 + 0.004f, ny2 + 0.004f, count, aspect, 0.45f, 0.32f, 0.68f, random);
        shards.clear();
        shards.addAll(voronoi.getPolygons());
        if (shards.isEmpty()) {
            return false;
        }
        int n = shards.size();
        baseAngle = new float[n];
        baseRadius = new float[n];
        fade = new float[n];
        radial = new float[n];
        for (int i = 0; i < n; ++i) {
            VoronoiOfQuad.Vec2f c = GuiCloseAnimationShatter.shards.get((int)i).center;
            float dx = (c.x - centerX) * aspect;
            float dy = c.y - centerY;
            GuiCloseAnimationShatter.baseAngle[i] = (float)Math.atan2(dy, dx);
            GuiCloseAnimationShatter.baseRadius[i] = (float)Math.sqrt(dx * dx + dy * dy);
            GuiCloseAnimationShatter.fade[i] = 0.45f * random.nextFloat();
            GuiCloseAnimationShatter.radial[i] = 0.65f + 0.7f * random.nextFloat();
        }
        return true;
    }

    public static void render() {
        if (!active) {
            return;
        }
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= 700L) {
            GuiCloseAnimationShatter.cleanup();
            return;
        }
        float p = GuiCloseAnimationShatter.clamp01((float)elapsed / 700.0f);
        float globalAlpha = p <= 0.75f ? 1.0f : GuiCloseAnimationShatter.clamp01(1.0f - (p - 0.75f) / 0.25f);
        try {
            if (!GuiCloseAnimationShatter.ensureBuffer()) {
                return;
            }
            int vertices = GuiCloseAnimationShatter.buildGeometry(p, globalAlpha);
            if (vertices <= 0) {
                GuiCloseAnimationShatter.cleanup();
                return;
            }
            GuiCloseAnimationShatter.draw(vertices);
        }
        catch (Throwable t) {
            GuiCloseAnimationShatter.cleanup();
        }
    }

    private static boolean ensureBuffer() {
        if (ready && vertexBuffer != null && !vertexBuffer.isClosed() && vertexData != null) {
            return true;
        }
        try {
            if (vertexBuffer != null && !vertexBuffer.isClosed()) {
                vertexBuffer.close();
            }
            vertexData = ByteBuffer.allocateDirect(49152).order(ByteOrder.nativeOrder());
            vertexBuffer = RenderSystem.getDevice().createBuffer(() -> "decide:guishatter_vertices", 40, 49152L);
            ready = vertexBuffer != null && vertexData != null;
        }
        catch (Throwable t) {
            ready = false;
        }
        return ready;
    }

    private static int buildGeometry(float progress, float globalAlpha) {
        ByteBuffer out = vertexData;
        out.clear();
        float p = GuiCloseAnimationShatter.clamp01(progress);
        float push = GuiCloseAnimationShatter.easeOutCubic(p);
        float invW = 1.0f / Math.max(1.0E-4f, nx2 - nx1);
        float invH = 1.0f / Math.max(1.0E-4f, ny2 - ny1);
        int vertices = 0;
        int n = shards.size();
        for (int i = 0; i < n; ++i) {
            VoronoiOfQuad.Polygon poly = shards.get(i);
            float shardAlpha = GuiCloseAnimationShatter.clamp01(1.0f - p * (0.9f + fade[i])) * globalAlpha;
            if (shardAlpha <= 0.002f) continue;
            float radius = baseRadius[i] * (1.0f + 0.34f * radial[i] * push) + 0.028f * radial[i] * push;
            float pivotX = centerX + (float)Math.cos(baseAngle[i]) * radius / aspect;
            float pivotY = centerY + (float)Math.sin(baseAngle[i]) * radius;
            float offsetX = pivotX - poly.center.x;
            float offsetY = pivotY - poly.center.y;
            float edgeX = GuiCloseAnimationShatter.clampSigned((poly.center.x - centerX) * aspect / halfExtentX);
            float edgeY = GuiCloseAnimationShatter.clampSigned((poly.center.y - centerY) / halfExtentY);
            float yawAngle = (float)Math.toRadians(26.0f * edgeX * push);
            float pitchAngle = (float)Math.toRadians(-26.0f * edgeY * push);
            float bowlZ = 0.1f * (1.0f - Math.min(1.0f, edgeX * edgeX + edgeY * edgeY)) * push;
            float yawSin = (float)Math.sin(yawAngle);
            float yawCos = (float)Math.cos(yawAngle);
            float pitchSin = (float)Math.sin(pitchAngle);
            float pitchCos = (float)Math.cos(pitchAngle);
            List<VoronoiOfQuad.Vec2f> pts = poly.getAllVertices();
            int corners = Math.min(pts.size(), 682);
            if (corners < 3) continue;
            int alpha = Math.round(GuiCloseAnimationShatter.clamp01(shardAlpha) * 255.0f);
            int t = 1;
            while (t + 1 < corners && vertices + 3 <= 2048) {
                GuiCloseAnimationShatter.putVertex(out, pts.get(0), offsetX, offsetY, pivotX, pivotY, yawSin, yawCos, pitchSin, pitchCos, bowlZ, alpha, invW, invH);
                GuiCloseAnimationShatter.putVertex(out, pts.get(t), offsetX, offsetY, pivotX, pivotY, yawSin, yawCos, pitchSin, pitchCos, bowlZ, alpha, invW, invH);
                GuiCloseAnimationShatter.putVertex(out, pts.get(t + 1), offsetX, offsetY, pivotX, pivotY, yawSin, yawCos, pitchSin, pitchCos, bowlZ, alpha, invW, invH);
                vertices += 3;
                ++t;
            }
        }
        out.flip();
        return vertices;
    }

    private static void putVertex(ByteBuffer out, VoronoiOfQuad.Vec2f src, float offsetX, float offsetY, float pivotX, float pivotY, float yawSin, float yawCos, float pitchSin, float pitchCos, float bowlZ, int alpha, float invW, float invH) {
        float localX = (src.x + offsetX - pivotX) * aspect;
        float localY = src.y + offsetY - pivotY;
        float rotatedX = localX * yawCos;
        float rotatedZ = -localX * yawSin;
        float finalY = localY * pitchCos - rotatedZ * pitchSin;
        float finalZ = localY * pitchSin + rotatedZ * pitchCos + bowlZ;
        float posX = pivotX + rotatedX / aspect;
        float posY = pivotY + finalY;
        float perspective = 1.0f / Math.max(0.2f, 1.0f + finalZ * 0.9f);
        posX = 0.5f + (posX - 0.5f) * perspective;
        posY = 0.5f + (posY - 0.5f) * perspective;
        float uvX = (src.x - nx1) * invW;
        float uvY = (src.y - ny1) * invH;
        out.putFloat(posX);
        out.putFloat(posY);
        out.putFloat(0.0f);
        out.putFloat(uvX);
        out.putFloat(uvY);
        out.put((byte)-1);
        out.put((byte)-1);
        out.put((byte)-1);
        out.put((byte)alpha);
    }

    private static void draw(int vertices) {
        if (mc.method_1522() == null) {
            return;
        }
        GpuTextureView view = GuiCloseAnimationShatter.captureView();
        if (view == null) {
            return;
        }
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.writeToBuffer(vertexBuffer.slice(0L, (long)vertexData.remaining()), vertexData);
        RenderPass pass = encoder.createRenderPass(() -> "decide:gui_close_shatter", mc.method_1522().method_71639(), OptionalInt.empty());
        try {
            pass.setPipeline(PIPELINE);
            pass.setVertexBuffer(0, vertexBuffer);
            pass.bindTexture("uGui", view, RenderSampler.linear());
            pass.draw(0, vertices);
        }
        catch (Throwable t) {
            try {
                pass.close();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            throw t;
        }
        pass.close();
    }

    private static GpuTextureView captureView() {
        try {
            class_1044 texture = mc.method_1531().method_4619(TEXTURE_ID);
            if (texture == null) {
                return null;
            }
            GpuTexture gpu = texture.method_68004();
            if (gpu == null || gpu.isClosed()) {
                return null;
            }
            return texture.method_71659();
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static void cancel() {
        if (!active && shards.isEmpty()) {
            return;
        }
        GuiCloseAnimationShatter.cleanup();
    }

    private static void cleanup() {
        active = false;
        shards.clear();
        baseAngle = new float[0];
        baseRadius = new float[0];
        fade = new float[0];
        radial = new float[0];
        GuiCloseAnimationShatter.releaseTexture();
    }

    private static void releaseTexture() {
        try {
            if (mc != null && mc.method_1531() != null) {
                mc.method_1531().method_4615(TEXTURE_ID);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static float clamp01(float f) {
        return Math.max(0.0f, Math.min(1.0f, f));
    }

    private static float clampSigned(float f) {
        return Math.max(-1.0f, Math.min(1.0f, f));
    }

    private static float easeOutCubic(float f) {
        float g = 1.0f - f;
        return 1.0f - g * g * g;
    }

    static {
        halfExtentX = 1.0f;
        halfExtentY = 1.0f;
        aspect = 1.0f;
        ready = false;
    }
}

