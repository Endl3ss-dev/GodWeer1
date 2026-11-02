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
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1044
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12137
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  org.lwjgl.system.MemoryUtil
 */
package ru.decide.utils.render.font;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1044;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.lwjgl.system.MemoryUtil;
import ru.decide.utils.colors.ColorFormatting;
import ru.decide.utils.render.DrawBatcher;
import ru.decide.utils.render.font.FontAtlas;
import ru.decide.utils.render.font.Glyph;

@Environment(value=EnvType.CLIENT)
public class FontPipeline
implements DrawBatcher.Batched {
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/msdf");
    private static final class_2960 SHADER_ID = class_2960.method_60655((String)"decide", (String)"core/msdf");
    private static final float FIXED_GUI_SCALE = 2.0f;
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(PIPELINE_ID).withVertexShader(SHADER_ID).withFragmentShader(SHADER_ID).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("FontData", class_10789.field_60031).withSampler("Sampler0").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final int[] LEGACY_COLORS = new int[32];
    private static final String LEGACY_CODE_CHARS;
    private static final int MAX_CHARS = 256;
    private static final int BUFFER_SIZE = 16448;
    private static final int PREPROCESS_CACHE_SIZE = 256;
    private static final Map<String, String> preprocessCache;
    private static final int UNIFORM_RING = 32;
    private GpuBuffer[] uniformBuffers;
    private int uniformRingIndex = 0;
    private GpuBuffer dummyVertexBuffer;
    private ByteBuffer dataBuffer;
    private boolean initialized = false;
    private final Map<GpuTexture, GpuTextureView> textureViewCache = new HashMap<GpuTexture, GpuTextureView>();
    private static final int HEADER_SIZE = 64;
    private int batchedChars = 0;
    private FontAtlas currentAtlas = null;
    private float currentOutlineWidth = 0.0f;
    private int currentOutlineColor = 0;
    private final ArrayList<FontChunk> chunks = new ArrayList();
    private static final Map<Integer, Integer> SMALL_CAPS_MAP;
    private static final int LIGHTNING_CP;

    private void appendGlyph(float x, float y, float w, float h, float u0, float v0, float u1, float v1, int color, float rotation, float pivotX, float pivotY, float glyphScale) {
        if (this.batchedChars == 0) {
            this.dataBuffer.clear();
            this.dataBuffer.position(64);
        }
        this.dataBuffer.putFloat(x);
        this.dataBuffer.putFloat(y);
        this.dataBuffer.putFloat(w);
        this.dataBuffer.putFloat(h);
        this.dataBuffer.putFloat(u0);
        this.dataBuffer.putFloat(v0);
        this.dataBuffer.putFloat(u1);
        this.dataBuffer.putFloat(v1);
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        this.dataBuffer.putFloat(r);
        this.dataBuffer.putFloat(g);
        this.dataBuffer.putFloat(b);
        this.dataBuffer.putFloat(a);
        this.dataBuffer.putFloat(rotation);
        this.dataBuffer.putFloat(pivotX);
        this.dataBuffer.putFloat(pivotY);
        this.dataBuffer.putFloat(glyphScale);
        ++this.batchedChars;
    }

    private int getFixedScaledWidth() {
        class_310 client = class_310.method_1551();
        if (client == null || client.method_22683() == null) {
            return 960;
        }
        return (int)Math.ceil((double)client.method_22683().method_4489() / 2.0);
    }

    private int getFixedScaledHeight() {
        class_310 client = class_310.method_1551();
        if (client == null || client.method_22683() == null) {
            return 540;
        }
        return (int)Math.ceil((double)client.method_22683().method_4506() / 2.0);
    }

    private void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        this.dataBuffer = MemoryUtil.memAlloc((int)16448);
        ByteBuffer dummyData = MemoryUtil.memAlloc((int)4);
        dummyData.putInt(0);
        dummyData.flip();
        this.dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "client:font_dummy_vertex", 32, dummyData);
        MemoryUtil.memFree((Buffer)dummyData);
        this.uniformBuffers = new GpuBuffer[32];
        this.initialized = true;
    }

    private GpuBuffer nextUniformBuffer() {
        GpuBuffer buf = this.uniformBuffers[this.uniformRingIndex];
        if (buf == null) {
            int idx = this.uniformRingIndex;
            this.uniformBuffers[this.uniformRingIndex] = buf = RenderSystem.getDevice().createBuffer(() -> "client:font_uniform_" + idx, 136, 16448L);
        }
        this.uniformRingIndex = (this.uniformRingIndex + 1) % 32;
        return buf;
    }

    public void drawText(FontAtlas atlas, String text, float x, float y, float size, int color) {
        this.drawText(atlas, text, x, y, size, color, 0.0f, 0, 0.0f);
    }

    public void drawText(FontAtlas atlas, String text, float x, float y, float size, int color, float outlineWidth, int outlineColor, float rotation) {
        class_310 client = class_310.method_1551();
        if (client.method_1522() == null) {
            return;
        }
        if (text == null || text.isEmpty()) {
            return;
        }
        text = this.preprocessText(text);
        text = ColorFormatting.get(text);
        atlas.ensureLoaded();
        if (atlas.getGlyphCount() == 0) {
            return;
        }
        this.ensureInitialized();
        if (DrawBatcher.isEnabled()) {
            DrawBatcher.register(this);
        }
        if (this.currentAtlas != null && (this.currentAtlas != atlas || this.currentOutlineWidth != outlineWidth || this.currentOutlineColor != outlineColor)) {
            this.flush();
        }
        this.currentAtlas = atlas;
        this.currentOutlineWidth = outlineWidth;
        this.currentOutlineColor = outlineColor;
        float scale = size / atlas.getFontSize();
        float cursorX = x;
        float cursorY = y;
        float rotationRad = (float)Math.toRadians(rotation);
        float pivotX = 0.0f;
        float pivotY = 0.0f;
        if (rotation != 0.0f) {
            pivotX = x + this.getTextWidth(atlas, text, size) / 2.0f;
            pivotY = y + this.getTextHeight(atlas, text, size) / 2.0f;
        }
        int currentColor = color;
        int i = 0;
        while (i < text.length()) {
            ColorAdvance colorAdvance = this.tryParseColorCode(text, i, color, currentColor);
            if (colorAdvance.matched()) {
                currentColor = colorAdvance.color();
                i += colorAdvance.skip();
                continue;
            }
            int codePoint = text.codePointAt(i);
            int charCount = Character.charCount(codePoint);
            if (codePoint == 10) {
                cursorX = x;
                cursorY += atlas.getLineHeight() * scale;
                i += charCount;
                continue;
            }
            Glyph glyph = atlas.getGlyph(codePoint);
            if (glyph == null) {
                Glyph fallback = atlas.getGlyph(63);
                cursorX = fallback != null ? (cursorX += fallback.xAdvance * scale) : (cursorX += size * 0.5f);
                i += charCount;
                continue;
            }
            float glyphX = cursorX + glyph.xOffset * scale;
            float glyphY = cursorY + glyph.yOffset * scale;
            float glyphW = glyph.width * scale;
            float glyphH = glyph.height * scale;
            if (glyph.width > 0.0f && glyph.height > 0.0f) {
                this.appendGlyph(glyphX, glyphY, glyphW, glyphH, glyph.u0, glyph.v0, glyph.u1, glyph.v1, currentColor, rotationRad, pivotX, pivotY, scale);
            }
            cursorX += glyph.xAdvance * scale;
            if (this.batchedChars >= 256) {
                this.flush();
                this.currentAtlas = atlas;
            }
            i += charCount;
        }
        if (!DrawBatcher.isEnabled()) {
            this.flush();
        }
    }

    public void drawTextFading(FontAtlas atlas, String text, float x, float y, float size, float maxWidth, int color) {
        class_310 client = class_310.method_1551();
        if (client.method_1522() == null) {
            return;
        }
        if (text == null || text.isEmpty()) {
            return;
        }
        if (this.getTextWidth(atlas, text, size) <= maxWidth) {
            this.drawText(atlas, text, x, y, size, color, 0.0f, 0, 0.0f);
            return;
        }
        int baseAlpha = color >> 24 & 0xFF;
        if (baseAlpha == 0) {
            return;
        }
        int rgb = color & 0xFFFFFF;
        float fadeStart = maxWidth - 40.0f;
        text = this.preprocessText(text);
        text = ColorFormatting.get(text);
        atlas.ensureLoaded();
        if (atlas.getGlyphCount() == 0) {
            return;
        }
        this.ensureInitialized();
        if (DrawBatcher.isEnabled()) {
            DrawBatcher.register(this);
        }
        if (this.currentAtlas != null && (this.currentAtlas != atlas || this.currentOutlineWidth != 0.0f || this.currentOutlineColor != 0)) {
            this.flush();
        }
        this.currentAtlas = atlas;
        this.currentOutlineWidth = 0.0f;
        this.currentOutlineColor = 0;
        float scale = size / atlas.getFontSize();
        float currentX = 0.0f;
        int i = 0;
        while (i < text.length()) {
            float advance;
            ColorAdvance colorAdvance = this.trySkipColorCode(text, i);
            if (colorAdvance.matched()) {
                i += colorAdvance.skip();
                continue;
            }
            int codePoint = text.codePointAt(i);
            int charCount = Character.charCount(codePoint);
            Glyph glyph = atlas.getGlyph(codePoint);
            if (glyph != null) {
                advance = glyph.xAdvance * scale;
            } else {
                Glyph fallback = atlas.getGlyph(63);
                float f = advance = fallback != null ? fallback.xAdvance * scale : size * 0.5f;
            }
            if (currentX + advance > maxWidth) break;
            int finalAlpha = baseAlpha;
            if (currentX > fadeStart) {
                float fadeProgress = (currentX - fadeStart) / (maxWidth - fadeStart);
                finalAlpha = (int)((float)baseAlpha * (1.0f - fadeProgress));
            }
            if (finalAlpha > 0 && glyph != null && glyph.width > 0.0f && glyph.height > 0.0f) {
                this.appendGlyph(x + currentX + glyph.xOffset * scale, y + glyph.yOffset * scale, glyph.width * scale, glyph.height * scale, glyph.u0, glyph.v0, glyph.u1, glyph.v1, finalAlpha << 24 | rgb, 0.0f, 0.0f, 0.0f, scale);
                if (this.batchedChars >= 256) {
                    this.flush();
                    this.currentAtlas = atlas;
                }
            }
            currentX += advance;
            i += charCount;
        }
        if (!DrawBatcher.isEnabled()) {
            this.flush();
        }
    }

    public void drawTextFadingReverse(FontAtlas atlas, String text, float x, float y, float size, float maxWidth, int color) {
        class_310 client = class_310.method_1551();
        if (client.method_1522() == null) {
            return;
        }
        if (text == null || text.isEmpty()) {
            return;
        }
        if (this.getTextWidth(atlas, text, size) <= maxWidth) {
            this.drawText(atlas, text, x, y, size, color, 0.0f, 0, 0.0f);
            return;
        }
        int baseAlpha = color >> 24 & 0xFF;
        if (baseAlpha == 0) {
            return;
        }
        int rgb = color & 0xFFFFFF;
        float fadeWidth = 40.0f;
        text = this.preprocessText(text);
        text = ColorFormatting.get(text);
        atlas.ensureLoaded();
        if (atlas.getGlyphCount() == 0) {
            return;
        }
        this.ensureInitialized();
        if (DrawBatcher.isEnabled()) {
            DrawBatcher.register(this);
        }
        if (this.currentAtlas != null && (this.currentAtlas != atlas || this.currentOutlineWidth != 0.0f || this.currentOutlineColor != 0)) {
            this.flush();
        }
        this.currentAtlas = atlas;
        this.currentOutlineWidth = 0.0f;
        this.currentOutlineColor = 0;
        float scale = size / atlas.getFontSize();
        float totalWidth = this.getTextWidth(atlas, text, size);
        float skipWidth = totalWidth - maxWidth;
        float skipped = 0.0f;
        float drawX = 0.0f;
        int i = 0;
        while (i < text.length()) {
            float advance;
            ColorAdvance colorAdvance = this.trySkipColorCode(text, i);
            if (colorAdvance.matched()) {
                i += colorAdvance.skip();
                continue;
            }
            int codePoint = text.codePointAt(i);
            int charCount = Character.charCount(codePoint);
            Glyph glyph = atlas.getGlyph(codePoint);
            if (glyph != null) {
                advance = glyph.xAdvance * scale;
            } else {
                Glyph fallback = atlas.getGlyph(63);
                float f = advance = fallback != null ? fallback.xAdvance * scale : size * 0.5f;
            }
            if (skipped < skipWidth) {
                skipped += advance;
                i += charCount;
                continue;
            }
            if (drawX + advance > maxWidth) break;
            int finalAlpha = baseAlpha;
            if (drawX < fadeWidth) {
                finalAlpha = (int)((float)baseAlpha * (drawX / fadeWidth));
            }
            if (finalAlpha > 0 && glyph != null && glyph.width > 0.0f && glyph.height > 0.0f) {
                this.appendGlyph(x + drawX + glyph.xOffset * scale, y + glyph.yOffset * scale, glyph.width * scale, glyph.height * scale, glyph.u0, glyph.v0, glyph.u1, glyph.v1, finalAlpha << 24 | rgb, 0.0f, 0.0f, 0.0f, scale);
                if (this.batchedChars >= 256) {
                    this.flush();
                    this.currentAtlas = atlas;
                }
            }
            drawX += advance;
            i += charCount;
        }
        if (!DrawBatcher.isEnabled()) {
            this.flush();
        }
    }

    public void drawTextRightAlignFadeLeft(FontAtlas atlas, String text, float rightX, float y, float size, float fadeStartX, int color) {
        class_310 client = class_310.method_1551();
        if (client.method_1522() == null) {
            return;
        }
        if (text == null || text.isEmpty()) {
            return;
        }
        float totalWidth = this.getTextWidth(atlas, text, size);
        float startX = rightX - totalWidth;
        if (startX >= fadeStartX) {
            this.drawText(atlas, text, startX, y, size, color, 0.0f, 0, 0.0f);
            return;
        }
        int baseAlpha = color >> 24 & 0xFF;
        if (baseAlpha == 0) {
            return;
        }
        int rgb = color & 0xFFFFFF;
        float fadeWidth = Math.min(32.0f, totalWidth * 0.45f);
        if (fadeWidth < 1.0f) {
            fadeWidth = 1.0f;
        }
        text = this.preprocessText(text);
        text = ColorFormatting.get(text);
        atlas.ensureLoaded();
        if (atlas.getGlyphCount() == 0) {
            return;
        }
        this.ensureInitialized();
        if (DrawBatcher.isEnabled()) {
            DrawBatcher.register(this);
        }
        if (this.currentAtlas != null && (this.currentAtlas != atlas || this.currentOutlineWidth != 0.0f || this.currentOutlineColor != 0)) {
            this.flush();
        }
        this.currentAtlas = atlas;
        this.currentOutlineWidth = 0.0f;
        this.currentOutlineColor = 0;
        float scale = size / atlas.getFontSize();
        float cursorX = 0.0f;
        int i = 0;
        while (i < text.length()) {
            Glyph fallback;
            ColorAdvance colorAdvance = this.trySkipColorCode(text, i);
            if (colorAdvance.matched()) {
                i += colorAdvance.skip();
                continue;
            }
            int codePoint = text.codePointAt(i);
            int charCount = Character.charCount(codePoint);
            Glyph glyph = atlas.getGlyph(codePoint);
            float advance = glyph != null ? glyph.xAdvance * scale : ((fallback = atlas.getGlyph(63)) != null ? fallback.xAdvance * scale : size * 0.5f);
            float glyphX = startX + cursorX;
            float glyphRight = glyphX + advance;
            int finalAlpha = baseAlpha;
            if (glyphRight <= fadeStartX) {
                finalAlpha = 0;
            } else if (glyphX < fadeStartX + fadeWidth) {
                float t = (glyphRight - fadeStartX) / fadeWidth;
                if (t < 0.0f) {
                    t = 0.0f;
                }
                if (t > 1.0f) {
                    t = 1.0f;
                }
                finalAlpha = (int)((float)baseAlpha * t);
            }
            if (finalAlpha > 0 && glyph != null && glyph.width > 0.0f && glyph.height > 0.0f) {
                this.appendGlyph(glyphX + glyph.xOffset * scale, y + glyph.yOffset * scale, glyph.width * scale, glyph.height * scale, glyph.u0, glyph.v0, glyph.u1, glyph.v1, finalAlpha << 24 | rgb, 0.0f, 0.0f, 0.0f, scale);
                if (this.batchedChars >= 256) {
                    this.flush();
                    this.currentAtlas = atlas;
                }
            }
            cursorX += advance;
            i += charCount;
        }
        if (!DrawBatcher.isEnabled()) {
            this.flush();
        }
    }

    public void drawTextRotatedAroundPoint(FontAtlas atlas, String text, float x, float y, float size, int color, float outlineWidth, int outlineColor, float rotation, float pivotX, float pivotY) {
        class_310 client = class_310.method_1551();
        if (client.method_1522() == null) {
            return;
        }
        if (text == null || text.isEmpty()) {
            return;
        }
        text = this.preprocessText(text);
        text = ColorFormatting.get(text);
        atlas.ensureLoaded();
        if (atlas.getGlyphCount() == 0) {
            return;
        }
        this.ensureInitialized();
        if (DrawBatcher.isEnabled()) {
            DrawBatcher.register(this);
        }
        if (this.currentAtlas != null && (this.currentAtlas != atlas || this.currentOutlineWidth != outlineWidth || this.currentOutlineColor != outlineColor)) {
            this.flush();
        }
        this.currentAtlas = atlas;
        this.currentOutlineWidth = outlineWidth;
        this.currentOutlineColor = outlineColor;
        float scale = size / atlas.getFontSize();
        float cursorX = x;
        float cursorY = y;
        float rotationRad = (float)Math.toRadians(rotation);
        int currentColor = color;
        int i = 0;
        while (i < text.length()) {
            ColorAdvance colorAdvance = this.tryParseColorCode(text, i, color, currentColor);
            if (colorAdvance.matched()) {
                currentColor = colorAdvance.color();
                i += colorAdvance.skip();
                continue;
            }
            int codePoint = text.codePointAt(i);
            int charCount = Character.charCount(codePoint);
            if (codePoint == 10) {
                cursorX = x;
                cursorY += atlas.getLineHeight() * scale;
                i += charCount;
                continue;
            }
            Glyph glyph = atlas.getGlyph(codePoint);
            if (glyph == null) {
                Glyph fallback = atlas.getGlyph(63);
                cursorX = fallback != null ? (cursorX += fallback.xAdvance * scale) : (cursorX += size * 0.5f);
                i += charCount;
                continue;
            }
            float glyphX = cursorX + glyph.xOffset * scale;
            float glyphY = cursorY + glyph.yOffset * scale;
            float glyphW = glyph.width * scale;
            float glyphH = glyph.height * scale;
            if (glyph.width > 0.0f && glyph.height > 0.0f) {
                this.appendGlyph(glyphX, glyphY, glyphW, glyphH, glyph.u0, glyph.v0, glyph.u1, glyph.v1, currentColor, rotationRad, pivotX, pivotY, scale);
            }
            cursorX += glyph.xAdvance * scale;
            if (this.batchedChars >= 256) {
                this.flush();
                this.currentAtlas = atlas;
            }
            i += charCount;
        }
        if (!DrawBatcher.isEnabled()) {
            this.flush();
        }
    }

    @Override
    public int batchLayer() {
        return 3;
    }

    private void sealChunk() {
        GpuTexture gpuTexture;
        if (this.batchedChars == 0 || this.currentAtlas == null) {
            this.batchedChars = 0;
            this.currentAtlas = null;
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.method_1522() == null) {
            this.batchedChars = 0;
            this.currentAtlas = null;
            return;
        }
        class_1044 texture = client.method_1531().method_4619(this.currentAtlas.getTextureId());
        if (texture == null) {
            this.batchedChars = 0;
            this.currentAtlas = null;
            return;
        }
        try {
            gpuTexture = texture.method_68004();
        }
        catch (Exception e) {
            this.batchedChars = 0;
            this.currentAtlas = null;
            return;
        }
        if (this.chunks.size() >= 31) {
            DrawBatcher.drawImmediate(this, false);
        }
        int count = this.batchedChars;
        int endPosition = this.dataBuffer.position();
        this.dataBuffer.position(0);
        this.dataBuffer.putFloat(this.getFixedScaledWidth());
        this.dataBuffer.putFloat(this.getFixedScaledHeight());
        this.dataBuffer.putFloat(2.0f);
        this.dataBuffer.putFloat(this.currentOutlineWidth);
        this.dataBuffer.putFloat((float)(this.currentOutlineColor >> 16 & 0xFF) / 255.0f);
        this.dataBuffer.putFloat((float)(this.currentOutlineColor >> 8 & 0xFF) / 255.0f);
        this.dataBuffer.putFloat((float)(this.currentOutlineColor & 0xFF) / 255.0f);
        this.dataBuffer.putFloat((float)(this.currentOutlineColor >> 24 & 0xFF) / 255.0f);
        this.dataBuffer.putFloat(this.currentAtlas.getAtlasWidth());
        this.dataBuffer.putFloat(this.currentAtlas.getAtlasHeight());
        this.dataBuffer.putFloat(this.currentAtlas.getDistanceRange());
        this.dataBuffer.putFloat(this.currentAtlas.getFontSize());
        this.dataBuffer.putInt(count);
        this.dataBuffer.putInt(0);
        this.dataBuffer.putInt(0);
        this.dataBuffer.putInt(0);
        this.dataBuffer.position(0);
        this.dataBuffer.limit(endPosition);
        GpuBuffer uniformBuffer = this.nextUniformBuffer();
        RenderSystem.getDevice().createCommandEncoder().writeToBuffer(uniformBuffer.slice(0L, (long)this.dataBuffer.remaining()), this.dataBuffer);
        this.dataBuffer.limit(this.dataBuffer.capacity());
        this.chunks.add(new FontChunk(uniformBuffer, this.getCachedTextureView(gpuTexture), count));
        this.batchedChars = 0;
        this.currentAtlas = null;
    }

    @Override
    public void uploadBatch(CommandEncoder encoder) {
        this.sealChunk();
    }

    @Override
    public void drawBatch(RenderPass pass) {
        if (this.chunks.isEmpty()) {
            return;
        }
        class_12137 sampler = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
        pass.setPipeline(PIPELINE);
        pass.setVertexBuffer(0, this.dummyVertexBuffer);
        for (FontChunk chunk : this.chunks) {
            if (chunk.view().isClosed()) continue;
            pass.bindTexture("Sampler0", chunk.view(), sampler);
            pass.setUniform("FontData", chunk.buffer());
            pass.draw(0, chunk.count() * 6);
        }
        this.chunks.clear();
    }

    @Override
    public void discardBatch() {
        this.chunks.clear();
    }

    public void flush() {
        this.sealChunk();
        if (!DrawBatcher.isEnabled() && !this.chunks.isEmpty()) {
            DrawBatcher.drawImmediate(this, false);
        }
    }

    private GpuTextureView getCachedTextureView(GpuTexture gpuTexture) {
        GpuTextureView view = this.textureViewCache.get(gpuTexture);
        if (view == null || view.isClosed()) {
            this.textureViewCache.entrySet().removeIf(e -> {
                if (((GpuTexture)e.getKey()).isClosed() || ((GpuTextureView)e.getValue()).isClosed()) {
                    if (!((GpuTextureView)e.getValue()).isClosed()) {
                        ((GpuTextureView)e.getValue()).close();
                    }
                    return true;
                }
                return false;
            });
            view = RenderSystem.getDevice().createTextureView(gpuTexture);
            this.textureViewCache.put(gpuTexture, view);
        }
        return view;
    }

    public float getTextWidth(FontAtlas atlas, String text, float size) {
        Float base;
        atlas.ensureLoaded();
        float scale = size / atlas.getFontSize();
        text = this.preprocessText(text);
        Float cached = atlas.widthCache.get(text);
        if (cached != null) {
            return cached.floatValue() * scale;
        }
        String stripped = ColorFormatting.stripForWidth(text);
        Float f = base = stripped.equals(text) ? null : atlas.widthCache.get(stripped);
        if (base == null) {
            base = Float.valueOf(this.computeBaseWidth(atlas, stripped));
            atlas.widthCache.put(stripped, base);
        }
        atlas.widthCache.put(text, base);
        return base.floatValue() * scale;
    }

    private float computeBaseWidth(FontAtlas atlas, String text) {
        float width = 0.0f;
        float maxWidth = 0.0f;
        int i = 0;
        while (i < text.length()) {
            Glyph fallback;
            ColorAdvance colorAdvance = this.trySkipColorCode(text, i);
            if (colorAdvance.matched()) {
                i += colorAdvance.skip();
                continue;
            }
            int codePoint = text.codePointAt(i);
            int charCount = Character.charCount(codePoint);
            if (codePoint == 10) {
                maxWidth = Math.max(maxWidth, width);
                width = 0.0f;
                i += charCount;
                continue;
            }
            Glyph glyph = atlas.getGlyph(codePoint);
            width = glyph != null ? (width += glyph.xAdvance) : ((fallback = atlas.getGlyph(63)) != null ? (width += fallback.xAdvance) : (width += atlas.getFontSize() * 0.5f));
            i += charCount;
        }
        return Math.max(maxWidth, width);
    }

    public float getTextHeight(FontAtlas atlas, String text, float size) {
        atlas.ensureLoaded();
        float scale = size / atlas.getFontSize();
        int lines = 1;
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) != '\n') continue;
            ++lines;
        }
        return (float)lines * atlas.getLineHeight() * scale;
    }

    private String preprocessText(String text) {
        int cc;
        String cached = preprocessCache.get(text);
        if (cached != null) {
            return cached;
        }
        StringBuilder sb = null;
        for (int i = 0; i < text.length(); i += cc) {
            int cp = text.codePointAt(i);
            cc = Character.charCount(cp);
            if (cp == LIGHTNING_CP) {
                if (sb != null) continue;
                sb = new StringBuilder(text.substring(0, i));
                continue;
            }
            Integer rep = SMALL_CAPS_MAP.get(cp);
            if (rep != null) {
                if (sb == null) {
                    sb = new StringBuilder(text.substring(0, i));
                }
                sb.appendCodePoint(rep);
                continue;
            }
            if (sb == null) continue;
            sb.appendCodePoint(cp);
        }
        String result = sb != null ? sb.toString() : text;
        preprocessCache.put(text, result);
        return result;
    }

    private ColorAdvance tryParseColorCode(String text, int index, int defaultColor, int currentColor) {
        ColorFormatting.ColorTag tag = ColorFormatting.parseTag(text, index, defaultColor);
        if (tag != null) {
            return new ColorAdvance(tag.length(), tag.color(), true);
        }
        int codePoint = text.codePointAt(index);
        int charCount = Character.charCount(codePoint);
        if ((codePoint == 167 || codePoint == 38) && index + charCount < text.length()) {
            int code;
            int nextCodePoint = text.codePointAt(index + charCount);
            if (nextCodePoint == 35 && index + charCount + 6 < text.length()) {
                try {
                    String hex = text.substring(index + charCount + 1, index + charCount + 7);
                    int parsed = 0xFF000000 | Integer.parseInt(hex, 16);
                    return new ColorAdvance(charCount + 7, parsed, true);
                }
                catch (Exception hex) {
                    // empty catch block
                }
            }
            if ((code = LEGACY_CODE_CHARS.indexOf(Character.toLowerCase((char)nextCodePoint))) >= 0) {
                int parsed = code < 16 ? LEGACY_COLORS[code] : (code == 21 ? defaultColor : currentColor);
                return new ColorAdvance(charCount + Character.charCount(nextCodePoint), parsed, true);
            }
        }
        return new ColorAdvance(0, currentColor, false);
    }

    private ColorAdvance trySkipColorCode(String text, int index) {
        return this.tryParseColorCode(text, index, 0, 0);
    }

    public void close() {
        this.batchedChars = 0;
        this.currentAtlas = null;
        this.discardBatch();
        for (GpuTextureView view : this.textureViewCache.values()) {
            if (view.isClosed()) continue;
            view.close();
        }
        this.textureViewCache.clear();
        if (this.uniformBuffers != null) {
            for (GpuBuffer buf : this.uniformBuffers) {
                if (buf == null) continue;
                buf.close();
            }
            this.uniformBuffers = null;
        }
        this.uniformRingIndex = 0;
        if (this.dummyVertexBuffer != null) {
            this.dummyVertexBuffer.close();
            this.dummyVertexBuffer = null;
        }
        if (this.dataBuffer != null) {
            MemoryUtil.memFree((Buffer)this.dataBuffer);
            this.dataBuffer = null;
        }
        this.initialized = false;
    }

    static {
        for (int i = 0; i < 16; ++i) {
            int j = (i >> 3 & 1) * 85;
            int r = (i >> 2 & 1) * 170 + j;
            int g = (i >> 1 & 1) * 170 + j;
            int b = (i & 1) * 170 + j;
            if (i == 6) {
                r += 85;
            }
            FontPipeline.LEGACY_COLORS[i] = 0xFF000000 | r << 16 | g << 8 | b;
            FontPipeline.LEGACY_COLORS[i + 16] = (r & 0xFCFCFC) >> 2 << 24 | r << 16 | g << 8 | b;
        }
        LEGACY_CODE_CHARS = new String(new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'});
        preprocessCache = new LinkedHashMap<String, String>(257, 0.75f, true){

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return this.size() > 256;
            }
        };
        SMALL_CAPS_MAP = new HashMap<Integer, Integer>();
        SMALL_CAPS_MAP.put(7424, 97);
        SMALL_CAPS_MAP.put(665, 98);
        SMALL_CAPS_MAP.put(7428, 99);
        SMALL_CAPS_MAP.put(7429, 100);
        SMALL_CAPS_MAP.put(7431, 101);
        SMALL_CAPS_MAP.put(1171, 102);
        SMALL_CAPS_MAP.put(610, 103);
        SMALL_CAPS_MAP.put(668, 104);
        SMALL_CAPS_MAP.put(618, 105);
        SMALL_CAPS_MAP.put(7434, 106);
        SMALL_CAPS_MAP.put(7435, 107);
        SMALL_CAPS_MAP.put(671, 108);
        SMALL_CAPS_MAP.put(7437, 109);
        SMALL_CAPS_MAP.put(628, 110);
        SMALL_CAPS_MAP.put(7439, 111);
        SMALL_CAPS_MAP.put(7448, 112);
        SMALL_CAPS_MAP.put(491, 113);
        SMALL_CAPS_MAP.put(640, 114);
        SMALL_CAPS_MAP.put(7451, 116);
        SMALL_CAPS_MAP.put(7452, 117);
        SMALL_CAPS_MAP.put(7456, 118);
        SMALL_CAPS_MAP.put(7457, 119);
        SMALL_CAPS_MAP.put(655, 121);
        SMALL_CAPS_MAP.put(7458, 122);
        LIGHTNING_CP = "\u26a1".codePointAt(0);
    }

    @Environment(value=EnvType.CLIENT)
    private record ColorAdvance(int skip, int color, boolean matched) {
    }

    @Environment(value=EnvType.CLIENT)
    private record FontChunk(GpuBuffer buffer, GpuTextureView view, int count) {
    }
}

