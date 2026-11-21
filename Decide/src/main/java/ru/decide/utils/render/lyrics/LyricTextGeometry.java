/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 */
package ru.decide.utils.render.lyrics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import ru.decide.utils.media.MediaLog;

@Environment(value=EnvType.CLIENT)
public final class LyricTextGeometry {
    private static final Map<String, MsdfFont> FONTS = new HashMap<String, MsdfFont>();
    private static final Map<String, GpuTextureView> VIEWS = new HashMap<String, GpuTextureView>();

    private LyricTextGeometry() {
    }

    public static Layout layout(String fontName, String text, float size) {
        MsdfFont font = MsdfFont.get(fontName);
        if (font == null || text == null || text.isEmpty() || size <= 0.0f) {
            return Layout.EMPTY;
        }
        GpuTextureView atlas = LyricTextGeometry.atlasView(font);
        if (atlas == null) {
            return Layout.EMPTY;
        }
        ArrayList<Quad> quads = new ArrayList<Quad>(text.length());
        float baseline = font.ascent(size);
        float penX = 0.0f;
        int previous = -1;
        int index = 0;
        while (index < text.length()) {
            int codePoint = text.codePointAt(index);
            index += Character.charCount(codePoint);
            if (codePoint == 10) continue;
            Glyph glyph = font.glyph(codePoint);
            if (previous != -1) {
                penX += font.kerning(previous, codePoint) * size;
            }
            if (glyph != null && glyph.drawable()) {
                quads.add(new Quad(penX + glyph.planeLeft * size, baseline - glyph.planeTop * size, penX + glyph.planeRight * size, baseline - glyph.planeBottom * size, glyph.u0, glyph.v0, glyph.u1, glyph.v1, codePoint));
            }
            penX += (glyph == null ? font.missingAdvance() : glyph.advance) * size;
            previous = codePoint;
        }
        return new Layout(List.copyOf(quads), font.width(text, size), font.lineHeight(size), atlas);
    }

    public static float width(String fontName, String text, float size) {
        MsdfFont font = MsdfFont.get(fontName);
        return font == null || text == null || text.isEmpty() ? 0.0f : font.width(text, size);
    }

    public static float distanceRange(String fontName) {
        MsdfFont font = MsdfFont.get(fontName);
        return font == null ? 4.0f : font.distanceRange;
    }

    private static synchronized GpuTextureView atlasView(MsdfFont font) {
        GpuTextureView cached = VIEWS.get(font.viewId().toString());
        if (cached != null && !cached.isClosed()) {
            return cached;
        }
        VIEWS.entrySet().removeIf(e -> ((GpuTextureView)e.getValue()).isClosed());
        VIEWS.clear();
        class_310 client = class_310.method_1551();
        if (client == null) {
            return null;
        }
        if (!font.ensureAtlas(client)) {
            return null;
        }
        class_1044 texture = client.method_1531().method_4619(font.viewId());
        if (texture == null) {
            return null;
        }
        GpuTextureView view = texture.method_71659();
        if (view == null) {
            return null;
        }
        VIEWS.put(font.viewId().toString(), view);
        return view;
    }

    public static synchronized void invalidate() {
        VIEWS.clear();
        for (MsdfFont font : FONTS.values()) {
            font.markAtlasStale();
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class MsdfFont {
        private final String key;
        private final class_2960 jsonId;
        private final class_2960 textureId;
        private final Map<Integer, Glyph> glyphs = new HashMap<Integer, Glyph>();
        private final Map<Long, Float> kerning = new HashMap<Long, Float>();
        private float emSize = 1.0f;
        private float ascender = 0.8f;
        private float lineHeightRatio = 1.2f;
        private float fontSize = 32.0f;
        private float atlasWidth = 512.0f;
        private float atlasHeight = 512.0f;
        private float distanceRange = 4.0f;
        private boolean yOriginBottom = true;
        private boolean uploaded;
        private int skipped;

        private MsdfFont(String key, class_2960 jsonId, class_2960 textureId) {
            this.key = key;
            this.jsonId = jsonId;
            this.textureId = textureId;
        }

        static synchronized MsdfFont get(String name) {
            class_2960 textureId;
            if (name == null) {
                return null;
            }
            MsdfFont font = FONTS.get(name);
            if (font != null) {
                return font;
            }
            class_2960 jsonId = class_2960.method_60655((String)"decide", (String)("fonts/" + name + ".json"));
            font = new MsdfFont(name, jsonId, textureId = class_2960.method_60655((String)"decide", (String)("fonts/" + name + ".png")));
            if (!font.load()) {
                return null;
            }
            FONTS.put(name, font);
            return font;
        }

        private boolean load() {
            class_310 client = class_310.method_1551();
            if (client == null) {
                return false;
            }
            try {
                Optional resource = client.method_1478().method_14486(this.jsonId);
                if (resource.isEmpty()) {
                    MediaLog.note("font", "\u043d\u0435\u0442 \u043c\u0435\u0442\u0440\u0438\u043a: " + String.valueOf(this.jsonId));
                    return false;
                }
                try (InputStream in = ((class_3298)resource.get()).method_14482();
                     InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);){
                    this.parse(JsonParser.parseReader((Reader)reader).getAsJsonObject());
                }
                if (this.glyphs.isEmpty()) {
                    MediaLog.note("font", "\u0432 " + String.valueOf(this.jsonId) + " \u043d\u0435\u0442 \u0433\u043b\u0438\u0444\u043e\u0432");
                    return false;
                }
                MediaLog.note("font", this.key + ": " + this.glyphs.size() + " \u0433\u043b\u0438\u0444\u043e\u0432, kerning=" + this.kerning.size() + ", ascender=" + this.ascender + ", \u0430\u0442\u043b\u0430\u0441 " + this.atlasWidth + "x" + this.atlasHeight);
                return true;
            }
            catch (Exception e) {
                MediaLog.note("font", "\u043d\u0435 \u0447\u0438\u0442\u0430\u0435\u0442\u0441\u044f " + String.valueOf(this.jsonId) + ": " + String.valueOf(e));
                return false;
            }
        }

        private synchronized boolean ensureAtlas(class_310 client) {
            if (this.uploaded) {
                return true;
            }
            try {
                class_1011 image;
                Optional resource = client.method_1478().method_14486(this.textureId);
                if (resource.isEmpty()) {
                    return false;
                }
                try (InputStream in = ((class_3298)resource.get()).method_14482();){
                    image = class_1011.method_4309((InputStream)in);
                }
                if (image == null) {
                    return false;
                }
                class_1043 texture = new class_1043(() -> this.viewId().toString(), image);
                texture.method_4524();
                client.method_1531().method_4616(this.viewId(), (class_1044)texture);
                this.uploaded = true;
                MediaLog.note("font", this.key + ": \u0430\u0442\u043b\u0430\u0441 " + image.method_4307() + "x" + image.method_4323() + " \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d \u043a\u0430\u043a " + String.valueOf(this.viewId()));
                return true;
            }
            catch (Exception e) {
                MediaLog.note("font", "\u043d\u0435 \u0447\u0438\u0442\u0430\u0435\u0442\u0441\u044f \u0430\u0442\u043b\u0430\u0441 " + String.valueOf(this.textureId) + ": " + String.valueOf(e));
                return false;
            }
        }

        void markAtlasStale() {
            this.uploaded = false;
        }

        private class_2960 viewId() {
            return class_2960.method_60655((String)"decide", (String)("lyrics/font/" + this.key));
        }

        private void parse(JsonObject root) {
            if (root.has("atlas")) {
                JsonObject atlas = root.getAsJsonObject("atlas");
                this.atlasWidth = MsdfFont.floatOf(atlas, "width", 512.0f);
                this.atlasHeight = MsdfFont.floatOf(atlas, "height", 512.0f);
                this.fontSize = MsdfFont.floatOf(atlas, "size", 32.0f);
                this.distanceRange = MsdfFont.floatOf(atlas, "distanceRange", 4.0f);
                if (atlas.has("yOrigin")) {
                    this.yOriginBottom = atlas.get("yOrigin").getAsString().equalsIgnoreCase("bottom");
                }
            }
            if (root.has("metrics")) {
                JsonObject metrics = root.getAsJsonObject("metrics");
                this.emSize = MsdfFont.floatOf(metrics, "emSize", 1.0f);
                this.ascender = MsdfFont.floatOf(metrics, "ascender", 0.8f);
                this.lineHeightRatio = MsdfFont.floatOf(metrics, "lineHeight", 1.2f);
            }
            if (root.has("kerning")) {
                JsonArray kerningArray = root.getAsJsonArray("kerning");
                for (JsonElement element2 : kerningArray) {
                    JsonObject pair = element2.getAsJsonObject();
                    int left = pair.has("unicode1") ? pair.get("unicode1").getAsInt() : pair.get("left").getAsInt();
                    int right = pair.has("unicode2") ? pair.get("unicode2").getAsInt() : pair.get("right").getAsInt();
                    float amount = pair.has("advance") ? pair.get("advance").getAsFloat() : MsdfFont.floatOf(pair, "value", 0.0f);
                    this.kerning.put(MsdfFont.pairKey(left, right), Float.valueOf(amount));
                }
            }
            if (root.has("glyphs")) {
                for (JsonElement element3 : root.getAsJsonArray("glyphs")) {
                    try {
                        this.parseGlyph(element3.getAsJsonObject());
                    }
                    catch (Exception e) {
                        ++this.skipped;
                    }
                }
                if (this.skipped > 0) {
                    MediaLog.note("font", this.key + ": \u043f\u0440\u043e\u043f\u0443\u0449\u0435\u043d\u043e \u0431\u0438\u0442\u044b\u0445 \u0433\u043b\u0438\u0444\u043e\u0432 \u2014 " + this.skipped);
                }
            }
        }

        private void parseGlyph(JsonObject g) {
            int unicode;
            if (g.has("unicode")) {
                unicode = g.get("unicode").getAsInt();
            } else if (g.has("char")) {
                String s = g.get("char").getAsString();
                if (s.isEmpty()) {
                    return;
                }
                unicode = s.codePointAt(0);
            } else if (g.has("id")) {
                unicode = g.get("id").getAsInt();
            } else {
                return;
            }
            float advance = g.has("advance") ? g.get("advance").getAsFloat() : 0.0f;
            float left = 0.0f;
            float bottom = 0.0f;
            float right = 0.0f;
            float top = 0.0f;
            if (g.has("planeBounds")) {
                JsonObject plane = g.getAsJsonObject("planeBounds");
                left = MsdfFont.floatOf(plane, "left", 0.0f);
                bottom = MsdfFont.floatOf(plane, "bottom", 0.0f);
                right = MsdfFont.floatOf(plane, "right", 0.0f);
                top = MsdfFont.floatOf(plane, "top", 0.0f);
            }
            float u0 = 0.0f;
            float v0 = 0.0f;
            float u1 = 0.0f;
            float v1 = 0.0f;
            boolean drawable = false;
            if (g.has("atlasBounds")) {
                JsonObject bounds = g.getAsJsonObject("atlasBounds");
                float x0 = MsdfFont.floatOf(bounds, "left", 0.0f);
                float y0 = MsdfFont.floatOf(bounds, "bottom", 0.0f);
                float x1 = MsdfFont.floatOf(bounds, "right", 0.0f);
                float y1 = MsdfFont.floatOf(bounds, "top", 0.0f);
                if (x1 > x0 && y1 > y0) {
                    u0 = x0 / this.atlasWidth;
                    u1 = x1 / this.atlasWidth;
                    if (this.yOriginBottom) {
                        v0 = 1.0f - y1 / this.atlasHeight;
                        v1 = 1.0f - y0 / this.atlasHeight;
                    } else {
                        v0 = y0 / this.atlasHeight;
                        v1 = y1 / this.atlasHeight;
                    }
                    drawable = true;
                }
            }
            this.glyphs.put(unicode, new Glyph(left, right, top, bottom, advance, u0, v0, u1, v1, drawable));
        }

        float ascent(float size) {
            return this.ascender * size;
        }

        float lineHeight(float size) {
            return this.lineHeightRatio * size;
        }

        float missingAdvance() {
            Glyph fallback = this.glyphs.get(63);
            return fallback != null ? fallback.advance : this.emSize * 0.5f;
        }

        Glyph glyph(int codePoint) {
            Glyph glyph = this.glyphs.get(codePoint);
            if (glyph != null) {
                return glyph;
            }
            return codePoint < 1024 ? this.glyphs.get(63) : null;
        }

        float kerning(int left, int right) {
            Float value = this.kerning.get(MsdfFont.pairKey(left, right));
            return value == null ? 0.0f : value.floatValue();
        }

        float width(String text, float size) {
            float width = 0.0f;
            int previous = -1;
            int index = 0;
            while (index < text.length()) {
                Glyph glyph;
                int codePoint = text.codePointAt(index);
                index += Character.charCount(codePoint);
                if (codePoint == 10) continue;
                if (previous != -1) {
                    width += this.kerning(previous, codePoint) * size;
                }
                width += ((glyph = this.glyph(codePoint)) == null ? this.missingAdvance() : glyph.advance) * size;
                previous = codePoint;
            }
            return width;
        }

        class_2960 atlasTexture() {
            return this.textureId;
        }

        String name() {
            return this.key;
        }

        private static long pairKey(int left, int right) {
            return (long)left << 32 | (long)right & 0xFFFFFFFFL;
        }

        private static float floatOf(JsonObject object, String field, float fallback) {
            return object.has(field) ? object.get(field).getAsFloat() : fallback;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Layout(List<Quad> quads, float width, float height, GpuTextureView atlas) {
        public static final Layout EMPTY = new Layout(List.of(), 0.0f, 0.0f, null);

        public boolean empty() {
            return this.quads.isEmpty() || this.atlas == null || this.height <= 0.0f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Glyph {
        final float planeLeft;
        final float planeRight;
        final float planeTop;
        final float planeBottom;
        final float advance;
        final float u0;
        final float v0;
        final float u1;
        final float v1;
        final boolean drawable;

        Glyph(float planeLeft, float planeRight, float planeTop, float planeBottom, float advance, float u0, float v0, float u1, float v1, boolean drawable) {
            this.planeLeft = planeLeft;
            this.planeRight = planeRight;
            this.planeTop = planeTop;
            this.planeBottom = planeBottom;
            this.advance = advance;
            this.u0 = u0;
            this.v0 = v0;
            this.u1 = u1;
            this.v1 = v1;
            this.drawable = drawable;
        }

        boolean drawable() {
            return this.drawable;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Quad(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, int codePoint) {
    }
}

