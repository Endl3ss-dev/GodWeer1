/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.utils.render.font;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.utils.render.font.FontAtlas;
import ru.decide.utils.render.font.FontPipeline;

@Environment(value=EnvType.CLIENT)
public class FontRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"client/FontRenderer");
    private final FontPipeline pipeline = new FontPipeline();
    private final Map<String, FontAtlas> fonts = new HashMap<String, FontAtlas>();
    private boolean initialized = false;

    public void loadFont(String name, String path) {
        class_2960 jsonId = class_2960.method_60655((String)"decide", (String)("fonts/" + path + ".json"));
        class_2960 textureId = class_2960.method_60655((String)"decide", (String)("fonts/" + path + ".png"));
        FontAtlas atlas = new FontAtlas(jsonId, textureId);
        this.fonts.put(name, atlas);
        LOGGER.info("Registered font: {} -> {}", (Object)name, (Object)path);
    }

    public void loadAllFonts(Map<String, String> registry) {
        for (Map.Entry<String, String> entry : registry.entrySet()) {
            this.loadFont(entry.getKey(), entry.getValue());
        }
    }

    public void initialize() {
        if (this.initialized) {
            return;
        }
        LOGGER.info("Initializing {} fonts...", (Object)this.fonts.size());
        long startTime = System.currentTimeMillis();
        for (Map.Entry<String, FontAtlas> entry : this.fonts.entrySet()) {
            entry.getValue().forceLoad();
        }
        this.initialized = true;
        LOGGER.info("All fonts initialized in {}ms", (Object)(System.currentTimeMillis() - startTime));
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public FontAtlas getFont(String name) {
        return this.fonts.get(name);
    }

    public void drawText(String fontName, String text, float x, float y, float size, int color) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return;
        }
        this.pipeline.drawText(atlas, text, x, y, size, color, 0.0f, 0, 0.0f);
    }

    public void drawText(String fontName, String text, float x, float y, float size, int color, float rotation) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return;
        }
        this.pipeline.drawText(atlas, text, x, y, size, color, 0.0f, 0, rotation);
    }

    public void drawTextWithOutline(String fontName, String text, float x, float y, float size, int color, float outlineWidth, int outlineColor) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return;
        }
        this.pipeline.drawText(atlas, text, x, y, size, color, outlineWidth, outlineColor, 0.0f);
    }

    public void drawTextWithOutline(String fontName, String text, float x, float y, float size, int color, float outlineWidth, int outlineColor, float rotation) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return;
        }
        this.pipeline.drawText(atlas, text, x, y, size, color, outlineWidth, outlineColor, rotation);
    }

    public void drawTextFading(String fontName, String text, float x, float y, float size, float maxWidth, int color) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return;
        }
        this.pipeline.drawTextFading(atlas, text, x, y, size, maxWidth, color);
    }

    public void drawTextFadingReverse(String fontName, String text, float x, float y, float size, float maxWidth, int color) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return;
        }
        this.pipeline.drawTextFadingReverse(atlas, text, x, y, size, maxWidth, color);
    }

    public void drawTextRightAlignFadeLeft(String fontName, String text, float rightX, float y, float size, float fadeStartX, int color) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return;
        }
        this.pipeline.drawTextRightAlignFadeLeft(atlas, text, rightX, y, size, fadeStartX, color);
    }

    public void drawCenteredText(String fontName, String text, float x, float y, float size, int color) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return;
        }
        float width = this.pipeline.getTextWidth(atlas, text, size);
        this.pipeline.drawText(atlas, text, x - width / 2.0f, y, size, color, 0.0f, 0, 0.0f);
    }

    public void drawCenteredText(String fontName, String text, float x, float y, float size, int color, float rotation) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return;
        }
        float width = this.pipeline.getTextWidth(atlas, text, size);
        float height = this.pipeline.getTextHeight(atlas, text, size);
        float centerX = x;
        float centerY = y + height / 2.0f;
        this.pipeline.drawTextRotatedAroundPoint(atlas, text, x - width / 2.0f, y, size, color, 0.0f, 0, rotation, centerX, centerY);
    }

    public float getTextWidth(String fontName, String text, float size) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return 0.0f;
        }
        return this.pipeline.getTextWidth(atlas, text, size);
    }

    public float getLineHeight(String fontName, float size) {
        FontAtlas atlas = this.fonts.get(fontName);
        if (atlas == null) {
            return size;
        }
        return atlas.getLineHeight() / atlas.getFontSize() * size;
    }

    public void flush() {
        this.pipeline.flush();
    }

    public void close() {
        this.pipeline.close();
        this.fonts.clear();
        this.initialized = false;
    }
}

