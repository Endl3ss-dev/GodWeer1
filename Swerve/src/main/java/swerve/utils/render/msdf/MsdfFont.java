package swerve.utils.render.msdf;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.mojang.blaze3d.vertex.VertexConsumer;

import swerve.Swerve;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;

import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class MsdfFont {

    private static final char FORMATTING_PREFIX = '\u00A7';

    private final String name;
    private final AbstractTexture texture;
    private final float atlasWidth;
    private final float atlasHeight;
    private final float range;
    private final float lineHeight;
    private final float ascender;
    private final float descender;
    private final Int2ObjectOpenHashMap<MsdfGlyph> glyphs;

    private boolean filtered;

    private MsdfFont(String name, AbstractTexture texture, float atlasWidth, float atlasHeight,
                     float range, float lineHeight, float ascender, float descender,
                     Int2ObjectOpenHashMap<MsdfGlyph> glyphs) {
        this.name = name;
        this.texture = texture;
        this.atlasWidth = atlasWidth;
        this.atlasHeight = atlasHeight;
        this.range = range;
        this.lineHeight = lineHeight;
        this.ascender = ascender;
        this.descender = descender;
        this.glyphs = glyphs;
    }

    public String getName() {
        return this.name;
    }

    public int getTextureId() {
        return this.texture.getId();
    }

    public float getAtlasWidth() {
        return this.atlasWidth;
    }

    public float getAtlasHeight() {
        return this.atlasHeight;
    }

    public float getRange() {
        return this.range;
    }

    public float getLineHeight() {
        return this.lineHeight;
    }

    public float getAscender() {
        return this.ascender;
    }

    public float getDescender() {
        return this.descender;
    }

    public float getBaselineHeight() {
        return this.lineHeight + this.descender;
    }

    public void setFiltered() {
        if (!this.filtered) {
            this.texture.setFilter(true, false);
            this.filtered = true;
        }
    }

    public float applyGlyphs(Matrix4f matrix, VertexConsumer consumer, float size, CharSequence text,
                             float thickness, float x, float y, float z, int argb) {
        int length = text.length();
        float cursor = x;

        for (int i = 0; i < length; i++) {
            char raw = text.charAt(i);

            if (raw == FORMATTING_PREFIX && i + 1 < length) {
                i++;
                continue;
            }

            MsdfGlyph glyph = this.glyphs.get(remap(raw));
            if (glyph != null) {
                cursor += glyph.apply(matrix, consumer, size, cursor, y, z, argb) + thickness;
            }
        }

        return cursor - x;
    }

    public float applyGlyphsGradient(Matrix4f matrix, VertexConsumer consumer, float size, CharSequence text,
                                     float thickness, float x, float y, float z, int leftArgb, int rightArgb) {
        int length = text.length();
        float total = getWidth(text, size);
        float cursor = x;

        for (int i = 0; i < length; i++) {
            char raw = text.charAt(i);

            if (raw == FORMATTING_PREFIX && i + 1 < length) {
                i++;
                continue;
            }

            MsdfGlyph glyph = this.glyphs.get(remap(raw));
            if (glyph == null) {
                continue;
            }

            float progress = total > 0.0f ? (cursor - x) / total : 0.0f;
            cursor += glyph.apply(matrix, consumer, size, cursor, y, z, lerpColor(leftArgb, rightArgb, progress)) + thickness;
        }

        return cursor - x;
    }

    public float applyGlyphsFade(Matrix4f matrix, VertexConsumer consumer, float size, CharSequence text,
                                 float thickness, float x, float y, float z, int argb,
                                 float maxWidth, float fadeZone) {
        int length = text.length();
        int baseAlpha = (argb >>> 24) & 0xFF;
        if (baseAlpha == 0) {
            baseAlpha = 0xFF;
        }

        float limit = x + maxWidth;
        float fadeStart = limit - fadeZone;
        float cursor = x;

        for (int i = 0; i < length; i++) {
            char raw = text.charAt(i);

            if (raw == FORMATTING_PREFIX && i + 1 < length) {
                i++;
                continue;
            }

            MsdfGlyph glyph = this.glyphs.get(remap(raw));
            if (glyph == null) {
                continue;
            }

            if (cursor > limit && i > 0) {
                break;
            }

            int color = argb;
            if (cursor > fadeStart && fadeZone > 0.0f) {
                float progress = (cursor - fadeStart) / fadeZone;
                progress = progress < 0.0f ? 0.0f : (progress > 1.0f ? 1.0f : progress);

                int alpha = (int) (baseAlpha * (float) Math.cos(progress * Math.PI * 0.5d));
                color = (argb & 0x00FFFFFF) | (alpha << 24);
            }

            if (((color >>> 24) & 0xFF) > 4) {
                cursor += glyph.apply(matrix, consumer, size, cursor, y, z, color) + thickness;
            } else {
                cursor += glyph.getAdvance(size) + thickness;
            }
        }

        return cursor - x;
    }

    public float applyGlyphsFormatted(Matrix4f matrix, VertexConsumer consumer, float size, CharSequence text,
                                      float thickness, float x, float y, float z, int defaultArgb) {
        int length = text.length();
        int color = defaultArgb;
        float cursor = x;

        for (int i = 0; i < length; i++) {
            char raw = text.charAt(i);

            if (raw == FORMATTING_PREFIX && i + 1 < length) {
                int parsed = colorFromCode(text.charAt(i + 1), defaultArgb);
                if (parsed != 0) {
                    color = parsed;
                }
                i++;
                continue;
            }

            MsdfGlyph glyph = this.glyphs.get(remap(raw));
            if (glyph != null) {
                cursor += glyph.apply(matrix, consumer, size, cursor, y, z, color) + thickness;
            }
        }

        return cursor - x;
    }

    public float getWidth(CharSequence text, float size) {
        int length = text.length();
        float width = 0.0f;

        for (int i = 0; i < length; i++) {
            char raw = text.charAt(i);

            if (raw == FORMATTING_PREFIX && i + 1 < length) {
                i++;
                continue;
            }

            MsdfGlyph glyph = this.glyphs.get(remap(raw));
            if (glyph != null) {
                width += glyph.getAdvance(size);
            }
        }

        return width;
    }

    public boolean hasGlyphs(CharSequence text, float size) {
        return getWidth(text, size) > 0.0f;
    }

    public MsdfGlyph getGlyph(int codePoint) {
        return this.glyphs.get(remap((char) codePoint));
    }

    // Ремап small-caps юникода в ASCII без аллокаций: исходная реализация делала 26 String#replace
    // на каждый вызов отрисовки, что давало десятки мусорных строк на кадр.
    private static int remap(char c) {
        return switch (c) {
            case 'ᴀ' -> 'A';
            case 'ʙ' -> 'B';
            case 'ᴄ' -> 'C';
            case 'ᴅ' -> 'D';
            case 'ᴇ' -> 'E';
            case 'ғ', 'ꜰ' -> 'F';
            case 'ɢ' -> 'G';
            case 'ʜ' -> 'H';
            case 'ɪ' -> 'I';
            case 'ᴊ' -> 'J';
            case 'ᴋ' -> 'K';
            case 'ʟ' -> 'L';
            case 'ᴍ' -> 'M';
            case 'ɴ' -> 'N';
            case 'ᴏ' -> 'O';
            case 'ᴘ' -> 'P';
            case 'ǫ' -> 'Q';
            case 'ʀ' -> 'R';
            case 'ꜱ' -> 'S';
            case 'ᴛ' -> 'T';
            case 'ᴜ' -> 'U';
            case 'ᴠ' -> 'V';
            case 'ᴡ' -> 'W';
            case 'ʏ' -> 'Y';
            case 'ᴢ' -> 'Z';
            default -> c;
        };
    }

    private static int colorFromCode(char code, int defaultArgb) {
        int alpha = (defaultArgb >>> 24) & 0xFF;
        if (alpha == 0) {
            alpha = 0xFF;
        }

        return switch (code) {
            case '0' -> (alpha << 24) | 0x000000;
            case '1' -> (alpha << 24) | 0x0000AA;
            case '2' -> (alpha << 24) | 0x00AA00;
            case '3' -> (alpha << 24) | 0x00AAAA;
            case '4' -> (alpha << 24) | 0xAA0000;
            case '5' -> (alpha << 24) | 0xAA00AA;
            case '6' -> (alpha << 24) | 0xFFAA00;
            case '7' -> (alpha << 24) | 0xAAAAAA;
            case '8' -> (alpha << 24) | 0x555555;
            case '9' -> (alpha << 24) | 0x5555FF;
            case 'a', 'A' -> (alpha << 24) | 0x55FF55;
            case 'b', 'B' -> (alpha << 24) | 0x55FFFF;
            case 'c', 'C' -> (alpha << 24) | 0xFF5555;
            case 'd', 'D' -> (alpha << 24) | 0xFF55FF;
            case 'e', 'E' -> (alpha << 24) | 0xFFFF55;
            case 'f', 'F' -> (alpha << 24) | 0xFFFFFF;
            case 'r', 'R' -> defaultArgb;
            default -> 0;
        };
    }

    public static int lerpColor(int from, int to, float progress) {
        if (progress <= 0.0f) {
            return from;
        }
        if (progress >= 1.0f) {
            return to;
        }

        int a1 = (from >>> 24) & 0xFF;
        int r1 = (from >> 16) & 0xFF;
        int g1 = (from >> 8) & 0xFF;
        int b1 = from & 0xFF;

        int a2 = (to >>> 24) & 0xFF;
        int r2 = (to >> 16) & 0xFF;
        int g2 = (to >> 8) & 0xFF;
        int b2 = to & 0xFF;

        if (a1 == 0) {
            a1 = 0xFF;
        }
        if (a2 == 0) {
            a2 = 0xFF;
        }

        int a = (int) (a1 + (a2 - a1) * progress);
        int r = (int) (r1 + (r2 - r1) * progress);
        int g = (int) (g1 + (g2 - g1) * progress);
        int b = (int) (b1 + (b2 - b1) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static String readResource(ResourceLocation location) {
        try (InputStream stream = Minecraft.getInstance().getResourceManager().open(location);
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            StringBuilder builder = new StringBuilder(1 << 14);
            char[] chunk = new char[4096];
            int read;
            while ((read = reader.read(chunk)) != -1) {
                builder.append(chunk, 0, read);
            }
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read resource: " + location, e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name = "?";
        private ResourceLocation dataLocation;
        private ResourceLocation atlasLocation;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(String directory) {
            this.dataLocation = Swerve.id("fonts/msdf/" + directory + "/font.json");
            return this;
        }

        public Builder atlas(String directory) {
            this.atlasLocation = Swerve.id("fonts/msdf/" + directory + "/font.png");
            return this;
        }

        public MsdfFont build() {
            JsonObject root = JsonParser.parseString(readResource(this.dataLocation)).getAsJsonObject();

            JsonObject atlasObject = root.getAsJsonObject("atlas");
            float atlasWidth = atlasObject.get("width").getAsFloat();
            float atlasHeight = atlasObject.get("height").getAsFloat();
            float range = atlasObject.get("distanceRange").getAsFloat();

            JsonObject metricsObject = root.getAsJsonObject("metrics");
            float lineHeight = metricsObject.get("lineHeight").getAsFloat();
            float ascender = metricsObject.get("ascender").getAsFloat();
            float descender = metricsObject.get("descender").getAsFloat();

            JsonArray glyphsArray = root.getAsJsonArray("glyphs");
            Int2ObjectOpenHashMap<MsdfGlyph> glyphs = new Int2ObjectOpenHashMap<>(glyphsArray.size());

            for (JsonElement element : glyphsArray) {
                JsonObject glyphObject = element.getAsJsonObject();

                int unicode = glyphObject.get("unicode").getAsInt();
                float advance = glyphObject.get("advance").getAsFloat();

                float planeLeft = 0.0f;
                float planeTop = 0.0f;
                float planeRight = 0.0f;
                float planeBottom = 0.0f;
                if (glyphObject.has("planeBounds") && !glyphObject.get("planeBounds").isJsonNull()) {
                    JsonObject plane = glyphObject.getAsJsonObject("planeBounds");
                    planeLeft = plane.get("left").getAsFloat();
                    planeTop = plane.get("top").getAsFloat();
                    planeRight = plane.get("right").getAsFloat();
                    planeBottom = plane.get("bottom").getAsFloat();
                }

                float atlasLeft = 0.0f;
                float atlasTop = 0.0f;
                float atlasRight = 0.0f;
                float atlasBottom = 0.0f;
                if (glyphObject.has("atlasBounds") && !glyphObject.get("atlasBounds").isJsonNull()) {
                    JsonObject bounds = glyphObject.getAsJsonObject("atlasBounds");
                    atlasLeft = bounds.get("left").getAsFloat();
                    atlasTop = bounds.get("top").getAsFloat();
                    atlasRight = bounds.get("right").getAsFloat();
                    atlasBottom = bounds.get("bottom").getAsFloat();
                }

                glyphs.put(unicode, new MsdfGlyph(
                        unicode, advance,
                        planeLeft, planeTop, planeRight, planeBottom,
                        atlasLeft, atlasTop, atlasRight, atlasBottom,
                        atlasWidth, atlasHeight
                ));
            }

            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(this.atlasLocation);

            return new MsdfFont(this.name, texture, atlasWidth, atlasHeight, range,
                    lineHeight, ascender, descender, glyphs);
        }
    }
}
