/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.utils.render.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.utils.render.font.Glyph;

@Environment(value=EnvType.CLIENT)
public class FontAtlas {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"client/Font");
    private final class_2960 jsonId;
    private final class_2960 textureId;
    private final Map<Integer, Glyph> glyphs;
    private float atlasWidth = 512.0f;
    private float atlasHeight = 512.0f;
    private float fontSize = 32.0f;
    private float lineHeight = 40.0f;
    private float distanceRange = 4.0f;
    private boolean yOriginBottom = false;
    private final AtomicBoolean loaded = new AtomicBoolean(false);
    final Map<String, Float> widthCache = new LinkedHashMap<String, Float>(512, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Float> eldest) {
            return this.size() > 2048;
        }
    };

    public FontAtlas(class_2960 jsonId, class_2960 textureId) {
        this.jsonId = jsonId;
        this.textureId = textureId;
        this.glyphs = new HashMap<Integer, Glyph>();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forceLoad() {
        if (this.loaded.get()) {
            return;
        }
        FontAtlas fontAtlas = this;
        synchronized (fontAtlas) {
            if (this.loaded.get()) {
                return;
            }
            this.doLoad();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void ensureLoaded() {
        if (this.loaded.get()) {
            return;
        }
        FontAtlas fontAtlas = this;
        synchronized (fontAtlas) {
            if (this.loaded.get()) {
                return;
            }
            this.doLoad();
        }
    }

    private void doLoad() {
        try {
            Optional resourceOpt = class_310.method_1551().method_1478().method_14486(this.jsonId);
            if (resourceOpt.isEmpty()) {
                LOGGER.warn("Font JSON not found: {}", (Object)this.jsonId);
                this.loaded.set(true);
                return;
            }
            try (InputStream is = ((class_3298)resourceOpt.get()).method_14482();
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);){
                JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
                this.parseJson(root);
                this.loaded.set(true);
                LOGGER.info("Loaded font: {} with {} glyphs", (Object)this.jsonId, (Object)this.glyphs.size());
            }
        }
        catch (Exception e) {
            LOGGER.error("Failed to load font: {}", (Object)this.jsonId, (Object)e);
            this.loaded.set(true);
        }
    }

    private void parseJson(JsonObject root) {
        float emSize = 1.0f;
        if (root.has("atlas")) {
            JsonObject atlas = root.getAsJsonObject("atlas");
            this.atlasWidth = this.getFloat(atlas, "width", 512.0f);
            this.atlasHeight = this.getFloat(atlas, "height", 512.0f);
            this.fontSize = this.getFloat(atlas, "size", 32.0f);
            this.distanceRange = this.getFloat(atlas, "distanceRange", 4.0f);
            if (atlas.has("yOrigin")) {
                String origin = atlas.get("yOrigin").getAsString();
                this.yOriginBottom = origin.equalsIgnoreCase("bottom");
            }
            LOGGER.info("Atlas: {}x{}, size={}, distanceRange={}, yOrigin={}", new Object[]{Float.valueOf(this.atlasWidth), Float.valueOf(this.atlasHeight), Float.valueOf(this.fontSize), Float.valueOf(this.distanceRange), this.yOriginBottom ? "bottom" : "top"});
        }
        if (root.has("metrics")) {
            JsonObject metrics = root.getAsJsonObject("metrics");
            emSize = this.getFloat(metrics, "emSize", 1.0f);
            float normalizedLineHeight = this.getFloat(metrics, "lineHeight", 1.2f);
            this.lineHeight = normalizedLineHeight * this.fontSize;
        }
        if (root.has("glyphs")) {
            JsonArray glyphsArray = root.getAsJsonArray("glyphs");
            for (JsonElement elem : glyphsArray) {
                JsonObject g = elem.getAsJsonObject();
                this.parseMsdfGlyph(g, emSize);
            }
        }
    }

    private void parseMsdfGlyph(JsonObject g, float emSize) {
        int unicode = -1;
        if (g.has("unicode")) {
            unicode = g.get("unicode").getAsInt();
        } else if (g.has("char")) {
            String charStr = g.get("char").getAsString();
            if (!charStr.isEmpty()) {
                unicode = charStr.codePointAt(0);
            }
        } else if (g.has("id")) {
            unicode = g.get("id").getAsInt();
        }
        if (unicode < 0) {
            return;
        }
        float advance = this.getFloat(g, "advance", 0.0f) * this.fontSize;
        if (advance == 0.0f) {
            advance = this.getFloat(g, "xadvance", 0.0f);
        }
        float x = 0.0f;
        float y = 0.0f;
        float w = 0.0f;
        float h = 0.0f;
        float xOffset = 0.0f;
        float yOffset = 0.0f;
        if (g.has("atlasBounds")) {
            JsonObject bounds = g.getAsJsonObject("atlasBounds");
            float left = this.getFloat(bounds, "left", 0.0f);
            float bottom = this.getFloat(bounds, "bottom", 0.0f);
            float right = this.getFloat(bounds, "right", 0.0f);
            float top = this.getFloat(bounds, "top", 0.0f);
            x = left;
            w = right - left;
            h = top - bottom;
            y = this.yOriginBottom ? this.atlasHeight - top : bottom;
        } else if (g.has("x") && g.has("y") && g.has("width") && g.has("height")) {
            x = this.getFloat(g, "x", 0.0f);
            y = this.getFloat(g, "y", 0.0f);
            w = this.getFloat(g, "width", 0.0f);
            h = this.getFloat(g, "height", 0.0f);
        }
        if (g.has("planeBounds")) {
            JsonObject plane = g.getAsJsonObject("planeBounds");
            float pLeft = this.getFloat(plane, "left", 0.0f);
            float pBottom = this.getFloat(plane, "bottom", 0.0f);
            float pRight = this.getFloat(plane, "right", 0.0f);
            float pTop = this.getFloat(plane, "top", 0.0f);
            xOffset = pLeft * this.fontSize;
            float ascender = 0.95f;
            yOffset = (ascender - pTop) * this.fontSize;
        } else if (g.has("xoffset") && g.has("yoffset")) {
            xOffset = this.getFloat(g, "xoffset", 0.0f);
            yOffset = this.getFloat(g, "yoffset", 0.0f);
        }
        this.glyphs.put(unicode, new Glyph(unicode, x, y, w, h, xOffset, yOffset, advance, this.atlasWidth, this.atlasHeight));
    }

    private float getFloat(JsonObject obj, String key, float def) {
        return obj.has(key) ? obj.get(key).getAsFloat() : def;
    }

    public Glyph getGlyph(int codePoint) {
        return this.glyphs.get(codePoint);
    }

    public boolean hasGlyph(int codePoint) {
        return this.glyphs.containsKey(codePoint);
    }

    public class_2960 getTextureId() {
        return this.textureId;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public float getLineHeight() {
        return this.lineHeight;
    }

    public float getAtlasWidth() {
        return this.atlasWidth;
    }

    public float getAtlasHeight() {
        return this.atlasHeight;
    }

    public float getDistanceRange() {
        return this.distanceRange;
    }

    public boolean isLoaded() {
        return this.loaded.get();
    }

    public int getGlyphCount() {
        return this.glyphs.size();
    }
}

