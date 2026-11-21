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
 *  com.mojang.blaze3d.systems.RenderSystem$class_5590
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
 *  net.fabricmc.fabric.api.resource.ResourceManagerHelper
 *  net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12137
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3264
 *  net.minecraft.class_3300
 *  net.minecraft.class_6367
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package ru.decide.utils.render.lyrics;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3264;
import net.minecraft.class_3300;
import net.minecraft.class_6367;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.decide.utils.media.MediaLog;
import ru.decide.utils.render.lyrics.LyricTextGeometry;

@Environment(value=EnvType.CLIENT)
public final class LyricText3D {
    private static final LyricText3D INSTANCE = new LyricText3D();
    private static final class_2960 DEPTH_PIPELINE_ID = LyricText3D.id("pipeline/effects/lyrics_text");
    private static final class_2960 OVERLAY_PIPELINE_ID = LyricText3D.id("pipeline/effects/lyrics_text_overlay");
    private static final class_2960 SHADER = LyricText3D.id("core/lyrics_text_vertex");
    private static final class_2960 FRAGMENT_SHADER = LyricText3D.id("core/lyrics_text_fragment");
    private static final class_2960 GLOW_SHADER = LyricText3D.id("core/lyrics_glow_fragment");
    private static final class_2960 GLOW_COMPOSITE_SHADER = LyricText3D.id("core/lyrics_glow_composite_fragment");
    private static final class_2960 FULLSCREEN_VERTEX = LyricText3D.id("core/lyrics_glow_composite_vertex");
    private static final class_2960 KAWASE_VERTEX = LyricText3D.id("core/lyrics_kawase_vertex");
    private static final class_2960 KAWASE_DOWN_SHADER = LyricText3D.id("core/lyrics_kawase_down_fragment");
    private static final class_2960 KAWASE_UP_SHADER = LyricText3D.id("core/lyrics_kawase_up_fragment");
    private static final class_2960 GLOW_PIPELINE_ID = LyricText3D.id("pipeline/effects/lyrics_glow");
    private static final class_2960 GLOW_DOWN_PIPELINE_ID = LyricText3D.id("pipeline/effects/lyrics_glow_down");
    private static final class_2960 GLOW_UP_PIPELINE_ID = LyricText3D.id("pipeline/effects/lyrics_glow_up");
    private static final class_2960 GLOW_COMPOSITE_PIPELINE_ID = LyricText3D.id("pipeline/effects/lyrics_glow_composite");
    private static final String UNIFORM_BLOCK = "LyricsTextData";
    private static final String GLYPH_BLOCK = "LyricsGlyphArray";
    private static final String SAMPLER = "Atlas";
    private static final String KAWASE_PARAMS = "KawaseParams";
    private static final String GLOW_PARAMS = "LyricsGlowParams";
    private static final int UNIFORM_BYTES = 112;
    private static final int VERTEX_BYTES = 24;
    private static final int QUAD_BYTES = 96;
    private static final int PAGE_QUADS = 256;
    private static final int GLYPH_PAGE_BYTES = 8192;
    private static final int GLYPH_BUFFER_USAGE = 136;
    private static final int MAX_PAGES = 8;
    private static final int MAX_QUADS = 2048;
    private static final int GLOW_PASSES = 2;
    private static final float GLOW_RENDER_SCALE = 0.5f;
    private static final float GLOW_OFFSET_SCALE = 1.25f;
    private static final int KAWASE_UNIFORM_BYTES = 48;
    private static final int GLOW_UNIFORM_BYTES = 16;
    private static final VertexFormat FORMAT = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("LineWidth", VertexFormatElement.LINE_WIDTH).build();
    private static final Matrix4f VIEW_PROJECTION = new Matrix4f();
    private static RenderPipeline depthPipeline;
    private static RenderPipeline overlayPipeline;
    private static RenderPipeline glowSourcePipeline;
    private static RenderPipeline glowDownPipeline;
    private static RenderPipeline glowUpPipeline;
    private static RenderPipeline glowCompositePipeline;
    private static GpuBuffer uniformBuffer;
    private static GpuBuffer vertexBuffer;
    private static GpuBuffer kawaseBuffer;
    private static GpuBuffer glowParamsBuffer;
    private static final GpuBuffer[] glyphBuffers;
    private static ByteBuffer uniformData;
    private static ByteBuffer glyphData;
    private static ByteBuffer vertexData;
    private static class_6367 glowTarget;
    private static final class_6367[] glowDownTargets;
    private static final class_6367[] glowUpTargets;
    private static int glowWidth;
    private static int glowHeight;
    private static int capacity;
    private static int quads;
    private static boolean active;
    private static boolean failed;
    private static GpuTextureView atlasView;
    private static double cameraX;
    private static double cameraY;
    private static double cameraZ;
    private static double planeX;
    private static double planeY;
    private static double planeZ;
    private static float rightX;
    private static float rightY;
    private static float rightZ;
    private static float upX;
    private static float upY;
    private static float upZ;
    private static float planeScale;
    private static float planeCenterX;
    private static float planeCenterY;
    private static float grayLevel;
    private static float heatBoost;
    private static float glowStrength;
    private static float blurReference;
    private static float uvScale;
    private static float tintR;
    private static float tintG;
    private static float tintB;
    private static float tintMix;
    private static float distanceRange;
    private static float maxHeat;

    public static void distanceRange(float value) {
        if (value > 0.0f) {
            distanceRange = value;
        }
    }

    private LyricText3D() {
    }

    public static void begin(Matrix4f modelView, Matrix4f projection, class_243 camera, float gray, float boost, float glow, float blurScale, int tintRgb, boolean colored) {
        active = false;
        if (failed || modelView == null || projection == null || camera == null || !INSTANCE.ensureReady()) {
            return;
        }
        VIEW_PROJECTION.set((Matrix4fc)projection).mul((Matrix4fc)modelView);
        cameraX = camera.field_1352;
        cameraY = camera.field_1351;
        cameraZ = camera.field_1350;
        grayLevel = gray;
        heatBoost = boost;
        glowStrength = Math.max(0.0f, glow);
        blurReference = Math.max(0.001f, blurScale);
        tintR = (float)(tintRgb >> 16 & 0xFF) / 255.0f;
        tintG = (float)(tintRgb >> 8 & 0xFF) / 255.0f;
        tintB = (float)(tintRgb & 0xFF) / 255.0f;
        tintMix = colored ? 1.0f : 0.0f;
        uvScale = 0.0f;
        maxHeat = 0.0f;
        atlasView = null;
        quads = 0;
        active = true;
    }

    public static void plane(double x, double y, double z, float toRightX, float toRightY, float toRightZ, float toUpX, float toUpY, float toUpZ, float scale, float centerX, float centerY) {
        planeX = x;
        planeY = y;
        planeZ = z;
        rightX = toRightX;
        rightY = toRightY;
        rightZ = toRightZ;
        upX = toUpX;
        upY = toUpY;
        upZ = toUpZ;
        planeScale = scale;
        planeCenterX = centerX;
        planeCenterY = centerY;
    }

    public static void glyph(LyricTextGeometry.Layout layout, LyricTextGeometry.Quad quad, float shiftX, float shiftY, float blur, float heat, float alpha) {
        if (!active || layout == null || quad == null || alpha <= 0.004f) {
            return;
        }
        GpuTextureView view = layout.atlas();
        if (view == null || atlasView != null && atlasView != view || quads >= 2048) {
            return;
        }
        if (!INSTANCE.ensureCapacity(quads + 1)) {
            return;
        }
        atlasView = view;
        float localWidth = quad.x1() - quad.x0();
        float uvWidth = quad.u1() - quad.u0();
        float uvPerLocal = localWidth > 1.0E-5f ? uvWidth / localWidth : 0.0f;
        float radius = blur * uvPerLocal;
        float padLocal = blur > 0.0f ? blur * 1.15f : 0.0f;
        float padUv = padLocal * uvPerLocal;
        uvScale = uvPerLocal;
        maxHeat = Math.max(maxHeat, heat * alpha);
        float leftX = quad.x0() - padLocal - planeCenterX + shiftX;
        float rightEdge = quad.x1() + padLocal - planeCenterX + shiftX;
        float topY = planeCenterY - (quad.y0() - padLocal) + shiftY;
        float bottomY = planeCenterY - (quad.y1() + padLocal) + shiftY;
        float u0 = quad.u0() - padUv;
        float u1 = quad.u1() + padUv;
        float v0 = quad.v0() - padUv;
        float v1 = quad.v1() + padUv;
        int page = quads / 256;
        int slot = quads % 256;
        int boundsOffset = page * 8192 + slot * 16;
        int effectOffset = page * 8192 + 4096 + slot * 16;
        ByteBuffer glyphs = glyphData;
        glyphs.putFloat(boundsOffset, quad.u0());
        glyphs.putFloat(boundsOffset + 4, quad.v0());
        glyphs.putFloat(boundsOffset + 8, quad.u1());
        glyphs.putFloat(boundsOffset + 12, quad.v1());
        glyphs.putFloat(effectOffset, radius);
        glyphs.putFloat(effectOffset + 4, heat);
        glyphs.putFloat(effectOffset + 8, alpha);
        glyphs.putFloat(effectOffset + 12, 0.0f);
        int base = quads * 96;
        INSTANCE.emit(base, leftX, topY, u0, v0, slot);
        INSTANCE.emit(base + 24, leftX, bottomY, u0, v1, slot);
        INSTANCE.emit(base + 48, rightEdge, bottomY, u1, v1, slot);
        INSTANCE.emit(base + 72, rightEdge, topY, u1, v0, slot);
        ++quads;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void end(boolean throughWalls) {
        if (!active) {
            return;
        }
        active = false;
        if (quads == 0 || atlasView == null) {
            return;
        }
        class_276 target = class_310.method_1551().method_1522();
        if (target == null || target.method_71639() == null || target.method_71640() == null) {
            quads = 0;
            return;
        }
        int total = quads;
        quads = 0;
        try {
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
            ByteBuffer uniforms = uniformData;
            VIEW_PROJECTION.get(0, uniforms);
            uniforms.putFloat(64, grayLevel);
            uniforms.putFloat(68, heatBoost);
            uniforms.putFloat(72, glowStrength);
            uniforms.putFloat(76, Math.max(1.0E-6f, blurReference * uvScale));
            uniforms.putFloat(80, tintR);
            uniforms.putFloat(84, tintG);
            uniforms.putFloat(88, tintB);
            uniforms.putFloat(92, tintMix);
            uniforms.putFloat(96, distanceRange);
            uniforms.limit(112);
            encoder.writeToBuffer(uniformBuffer.slice(0L, 112L), uniforms);
            uniforms.clear();
            ByteBuffer vertices = vertexData;
            int bytes = total * 96;
            vertices.limit(bytes);
            encoder.writeToBuffer(vertexBuffer.slice(0L, (long)bytes), vertices);
            vertices.clear();
            int pages = (total + 256 - 1) / 256;
            ByteBuffer glyphs = glyphData;
            for (int page = 0; page < pages; ++page) {
                glyphs.limit((page + 1) * 8192);
                glyphs.position(page * 8192);
                encoder.writeToBuffer(INSTANCE.glyphBuffer(page).slice(0L, 8192L), glyphs);
                glyphs.clear();
            }
            class_12137 sampler = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
            RenderSystem.class_5590 indices = RenderSystem.getSequentialBuffer((VertexFormat.class_5596)VertexFormat.class_5596.field_27382);
            GpuBuffer indexBuffer = indices.method_68274(total * 6);
            try (RenderPass pass = encoder.createRenderPass(() -> "white:lyrics_text", target.method_71639(), OptionalInt.empty(), target.method_71640(), OptionalDouble.empty());){
                pass.setPipeline(throughWalls ? overlayPipeline : depthPipeline);
                pass.setVertexBuffer(0, vertexBuffer);
                pass.setIndexBuffer(indexBuffer, indices.method_31924());
                pass.setUniform(UNIFORM_BLOCK, uniformBuffer.slice());
                pass.bindTexture(SAMPLER, atlasView, sampler);
                for (int page = 0; page < pages; ++page) {
                    int count = Math.min(256, total - page * 256);
                    pass.setUniform(GLYPH_BLOCK, glyphBuffers[page].slice());
                    pass.drawIndexed(0, page * 256 * 6, count * 6, 1);
                }
            }
            if (glowStrength > 0.004f && maxHeat > 0.004f && INSTANCE.ensureGlowTargets(target.field_1482, target.field_1481)) {
                INSTANCE.glowChain(encoder, target, sampler, indices, indexBuffer, total, pages, throughWalls);
            }
        }
        catch (Throwable error) {
            MediaLog.note("gpu", "\u043a\u0430\u0434\u0440 \u0443\u043f\u0430\u043b, \u043e\u0442\u043a\u043b\u044e\u0447\u0430\u044e \u0440\u0435\u043d\u0434\u0435\u0440 \u0442\u0435\u043a\u0441\u0442\u0430: " + String.valueOf(error));
            INSTANCE.disable();
        }
        finally {
            atlasView = null;
        }
    }

    public static void registerResourceReload() {
        ResourceManagerHelper.get((class_3264)class_3264.field_14188).registerReloadListener((IdentifiableResourceReloadListener)new SimpleSynchronousResourceReloadListener(){

            public void method_14491(class_3300 manager) {
                LyricText3D.invalidate();
            }

            public class_2960 getFabricId() {
                return class_2960.method_60655((String)"decide", (String)"lyrics_text");
            }
        });
    }

    public static void invalidate() {
        active = false;
        quads = 0;
        capacity = 0;
        atlasView = null;
        failed = false;
        LyricTextGeometry.invalidate();
        INSTANCE.releaseGpu();
    }

    private void glowChain(CommandEncoder encoder, class_276 target, class_12137 sampler, RenderSystem.class_5590 indices, GpuBuffer indexBuffer, int total, int pages, boolean throughWalls) {
        int index;
        class_6367 glow = glowTarget;
        RenderPass pass = encoder.createRenderPass(() -> "white:lyrics_glow_clear", glow.method_71639(), OptionalInt.of(0), glow.method_71640(), OptionalDouble.of(1.0));
        if (pass != null) {
            pass.close();
        }
        if (!throughWalls && target.method_30278() != null && glow.method_30278() != null) {
            encoder.copyTextureToTexture(target.method_30278(), glow.method_30278(), 0, 0, 0, 0, 0, glowWidth, glowHeight);
        }
        pass = encoder.createRenderPass(() -> "white:lyrics_glow_source", glow.method_71639(), OptionalInt.empty(), glow.method_71640(), OptionalDouble.empty());
        try {
            pass.setPipeline(glowSourcePipeline);
            pass.setVertexBuffer(0, vertexBuffer);
            pass.setIndexBuffer(indexBuffer, indices.method_31924());
            pass.setUniform(UNIFORM_BLOCK, uniformBuffer.slice());
            pass.bindTexture(SAMPLER, atlasView, sampler);
            for (int page = 0; page < pages; ++page) {
                int count = Math.min(256, total - page * 256);
                pass.setUniform(GLYPH_BLOCK, glyphBuffers[page].slice());
                pass.drawIndexed(0, page * 256 * 6, count * 6, 1);
            }
        }
        finally {
            if (pass != null) {
                pass.close();
            }
        }
        GpuTextureView current = glowDownTargets[0].method_71639();
        int width = LyricText3D.glowDownTargets[0].field_1482;
        int height = LyricText3D.glowDownTargets[0].field_1481;
        for (index = 1; index < 2; ++index) {
            this.kawase(encoder, glowDownPipeline, current, width, height, glowDownTargets[index], sampler);
            current = glowDownTargets[index].method_71639();
            width = LyricText3D.glowDownTargets[index].field_1482;
            height = LyricText3D.glowDownTargets[index].field_1481;
        }
        for (index = 0; index >= 0; --index) {
            this.kawase(encoder, glowUpPipeline, current, width, height, glowUpTargets[index], sampler);
            current = glowUpTargets[index].method_71639();
            width = LyricText3D.glowUpTargets[index].field_1482;
            height = LyricText3D.glowUpTargets[index].field_1481;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer data = stack.calloc(16);
            data.putFloat(0, glowStrength * 0.5f);
            data.position(0);
            encoder.writeToBuffer(glowParamsBuffer.slice(0L, 16L), data);
        }
        try (RenderPass pass2 = encoder.createRenderPass(() -> "white:lyrics_glow_composite", target.method_71639(), OptionalInt.empty());){
            pass2.setPipeline(glowCompositePipeline);
            pass2.bindTexture("Sampler0", current, sampler);
            pass2.setUniform(GLOW_PARAMS, glowParamsBuffer);
            pass2.draw(0, 6);
        }
    }

    private void kawase(CommandEncoder encoder, RenderPipeline pipeline, GpuTextureView source, int sourceWidth, int sourceHeight, class_6367 target, class_12137 sampler) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer data = stack.calloc(48);
            data.putFloat(0, 0.0f);
            data.putFloat(4, 0.0f);
            data.putFloat(8, 1.0f);
            data.putFloat(12, 1.0f);
            data.putFloat(16, 1.25f / (float)Math.max(sourceWidth, 1));
            data.putFloat(20, 1.25f / (float)Math.max(sourceHeight, 1));
            data.putFloat(32, 0.0f);
            data.putFloat(36, 0.0f);
            data.putFloat(40, 0.0f);
            data.putFloat(44, 1.0f);
            data.position(0);
            encoder.writeToBuffer(kawaseBuffer.slice(0L, 48L), data);
        }
        try (RenderPass pass = encoder.createRenderPass(() -> "white:lyrics_glow_kawase", target.method_71639(), OptionalInt.empty());){
            pass.setPipeline(pipeline);
            pass.bindTexture("Sampler0", source, sampler);
            pass.setUniform(KAWASE_PARAMS, kawaseBuffer);
            pass.draw(0, 6);
        }
    }

    private boolean ensureGlowTargets(int width, int height) {
        if (width <= 0 || height <= 0) {
            return false;
        }
        if (glowTarget != null && glowWidth == width && glowHeight == height) {
            return true;
        }
        this.closeGlowTargets();
        glowTarget = new class_6367("decide_lyrics_glow", width, height, true);
        int currentWidth = Math.max(1, Math.round((float)width * 0.5f));
        int currentHeight = Math.max(1, Math.round((float)height * 0.5f));
        for (int index = 0; index < 2; ++index) {
            LyricText3D.glowDownTargets[index] = new class_6367("decide_lyrics_glow_down_" + index, currentWidth, currentHeight, false);
            if (index < 1) {
                LyricText3D.glowUpTargets[index] = new class_6367("decide_lyrics_glow_up_" + index, currentWidth, currentHeight, false);
            }
            currentWidth = Math.max(currentWidth / 2, 1);
            currentHeight = Math.max(currentHeight / 2, 1);
        }
        glowWidth = width;
        glowHeight = height;
        return true;
    }

    private void closeGlowTargets() {
        if (glowTarget != null) {
            glowTarget.method_1238();
        }
        glowTarget = null;
        for (int index = 0; index < 2; ++index) {
            if (glowDownTargets[index] != null) {
                glowDownTargets[index].method_1238();
            }
            LyricText3D.glowDownTargets[index] = null;
            if (glowUpTargets[index] != null) {
                glowUpTargets[index].method_1238();
            }
            LyricText3D.glowUpTargets[index] = null;
        }
        glowWidth = -1;
        glowHeight = -1;
    }

    private void emit(int cursor, float localX, float localY, float u, float v, int slot) {
        float scaledX = localX * planeScale;
        float scaledY = localY * planeScale;
        float x = (float)(planeX - cameraX) + rightX * scaledX + upX * scaledY;
        float y = (float)(planeY - cameraY) + rightY * scaledX + upY * scaledY;
        float z = (float)(planeZ - cameraZ) + rightZ * scaledX + upZ * scaledY;
        ByteBuffer vertices = vertexData;
        vertices.putFloat(cursor, x);
        vertices.putFloat(cursor + 4, y);
        vertices.putFloat(cursor + 8, z);
        vertices.putFloat(cursor + 12, u);
        vertices.putFloat(cursor + 16, v);
        vertices.putFloat(cursor + 20, slot);
    }

    private GpuBuffer glyphBuffer(int page) {
        GpuBuffer buffer = glyphBuffers[page];
        if (buffer == null || buffer.isClosed()) {
            LyricText3D.glyphBuffers[page] = buffer = RenderSystem.getDevice().createBuffer(() -> "white:lyrics_text_glyphs", 136, 8192L);
        }
        return buffer;
    }

    private boolean ensureCapacity(int required) {
        if (required <= capacity) {
            return true;
        }
        if (required > 2048) {
            return false;
        }
        int target = Math.min(2048, Math.max(256, Integer.highestOneBit(required - 1) * 2));
        try {
            ByteBuffer resized = MemoryUtil.memRealloc((ByteBuffer)vertexData, (int)(target * 96));
            if (resized == null) {
                return false;
            }
            vertexData = resized;
            if (vertexBuffer != null) {
                vertexBuffer.close();
            }
            vertexBuffer = RenderSystem.getDevice().createBuffer(() -> "white:lyrics_text_vertices", 40, (long)target * 96L);
            capacity = target;
            return true;
        }
        catch (Throwable error) {
            MediaLog.note("gpu", "\u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0440\u0430\u0441\u0448\u0438\u0440\u0438\u0442\u044c \u0432\u0435\u0440\u0448\u0438\u043d\u043d\u044b\u0439 \u0431\u0443\u0444\u0435\u0440: " + String.valueOf(error));
            this.disable();
            return false;
        }
    }

    private boolean ensureReady() {
        if (depthPipeline != null && overlayPipeline != null && uniformBuffer != null && uniformData != null && glyphData != null) {
            return true;
        }
        try {
            if (depthPipeline == null) {
                depthPipeline = this.textPipeline(DEPTH_PIPELINE_ID, DepthTestFunction.LEQUAL_DEPTH_TEST);
            }
            if (overlayPipeline == null) {
                overlayPipeline = this.textPipeline(OVERLAY_PIPELINE_ID, DepthTestFunction.NO_DEPTH_TEST);
            }
            if (uniformData == null) {
                uniformData = MemoryUtil.memAlloc((int)112);
            }
            if (glyphData == null) {
                glyphData = MemoryUtil.memAlloc((int)65536);
            }
            if (uniformBuffer == null || uniformBuffer.isClosed()) {
                uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "white:lyrics_text_uniform", 136, 112L);
            }
            if (glowSourcePipeline == null) {
                glowSourcePipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(GLOW_PIPELINE_ID).withVertexShader(SHADER).withFragmentShader(GLOW_SHADER).withVertexFormat(FORMAT, VertexFormat.class_5596.field_27382).withUniform(UNIFORM_BLOCK, class_10789.field_60031).withUniform(GLYPH_BLOCK, class_10789.field_60031).withSampler(SAMPLER).withBlend(BlendFunction.ADDITIVE).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
            }
            if (glowDownPipeline == null) {
                glowDownPipeline = this.kawasePipeline(GLOW_DOWN_PIPELINE_ID, KAWASE_DOWN_SHADER);
            }
            if (glowUpPipeline == null) {
                glowUpPipeline = this.kawasePipeline(GLOW_UP_PIPELINE_ID, KAWASE_UP_SHADER);
            }
            if (glowCompositePipeline == null) {
                glowCompositePipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(GLOW_COMPOSITE_PIPELINE_ID).withVertexShader(FULLSCREEN_VERTEX).withFragmentShader(GLOW_COMPOSITE_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform(GLOW_PARAMS, class_10789.field_60031).withSampler("Sampler0").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
            }
            if (kawaseBuffer == null || kawaseBuffer.isClosed()) {
                kawaseBuffer = RenderSystem.getDevice().createBuffer(() -> "white:lyrics_glow_kawase", 136, 48L);
            }
            if (glowParamsBuffer == null || glowParamsBuffer.isClosed()) {
                glowParamsBuffer = RenderSystem.getDevice().createBuffer(() -> "white:lyrics_glow_params", 136, 16L);
            }
            return true;
        }
        catch (Throwable error) {
            MediaLog.note("gpu", "\u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0437\u0434\u0430\u0442\u044c \u043f\u0430\u0439\u043f\u043b\u0430\u0439\u043d\u044b/\u0431\u0443\u0444\u0435\u0440\u044b: " + String.valueOf(error));
            this.disable();
            return false;
        }
    }

    private RenderPipeline textPipeline(class_2960 location, DepthTestFunction depth) {
        return class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(location).withVertexShader(SHADER).withFragmentShader(FRAGMENT_SHADER).withVertexFormat(FORMAT, VertexFormat.class_5596.field_27382).withUniform(UNIFORM_BLOCK, class_10789.field_60031).withUniform(GLYPH_BLOCK, class_10789.field_60031).withSampler(SAMPLER).withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA).withDepthTestFunction(depth).withDepthWrite(false).withCull(false).build());
    }

    private RenderPipeline kawasePipeline(class_2960 location, class_2960 fragment) {
        return class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(location).withVertexShader(KAWASE_VERTEX).withFragmentShader(fragment).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform(KAWASE_PARAMS, class_10789.field_60031).withSampler("Sampler0").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    }

    private void releaseGpu() {
        this.closeGlowTargets();
        LyricText3D.closeBuffer(kawaseBuffer);
        kawaseBuffer = null;
        LyricText3D.closeBuffer(glowParamsBuffer);
        glowParamsBuffer = null;
        LyricText3D.closeBuffer(uniformBuffer);
        uniformBuffer = null;
        LyricText3D.closeBuffer(vertexBuffer);
        vertexBuffer = null;
        for (int page = 0; page < 8; ++page) {
            LyricText3D.closeBuffer(glyphBuffers[page]);
            LyricText3D.glyphBuffers[page] = null;
        }
        LyricText3D.freeBuffer(uniformData);
        uniformData = null;
        LyricText3D.freeBuffer(glyphData);
        glyphData = null;
        LyricText3D.freeBuffer(vertexData);
        vertexData = null;
    }

    private void disable() {
        failed = true;
        this.releaseGpu();
        depthPipeline = null;
        overlayPipeline = null;
        glowSourcePipeline = null;
        glowDownPipeline = null;
        glowUpPipeline = null;
        glowCompositePipeline = null;
    }

    private static void closeBuffer(GpuBuffer buffer) {
        if (buffer != null) {
            buffer.close();
        }
    }

    private static void freeBuffer(ByteBuffer buffer) {
        if (buffer != null) {
            MemoryUtil.memFree((Buffer)buffer);
        }
    }

    private static class_2960 id(String path) {
        return class_2960.method_60655((String)"decide", (String)path);
    }

    static {
        glyphBuffers = new GpuBuffer[8];
        glowDownTargets = new class_6367[2];
        glowUpTargets = new class_6367[2];
        glowWidth = -1;
        glowHeight = -1;
        tintR = 1.0f;
        tintG = 1.0f;
        tintB = 1.0f;
        distanceRange = 4.0f;
    }
}

