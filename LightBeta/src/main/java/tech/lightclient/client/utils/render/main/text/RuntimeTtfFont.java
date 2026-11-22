package tech.lightclient.client.utils.render.main.text;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

final class RuntimeTtfFont {
    private static final int RASTER_SIZE = 128;
    private static final int PADDING = 8;
    private static final int INITIAL_ATLAS_SIZE = 1024;
    private static final int MAX_ATLAS_SIZE = 4096;
    private static final FontRenderContext FONT_RENDER_CONTEXT =
            new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_ON, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    private final Identifier textureId;
    private final NativeImageBackedTexture texture;
    private final Map<Integer, RuntimeTtfGlyph> glyphs;
    private final float rasterSize;
    private final float lineHeight;

    private RuntimeTtfFont(Identifier textureId,
                           NativeImageBackedTexture texture,
                           Map<Integer, RuntimeTtfGlyph> glyphs,
                           float rasterSize,
                           float lineHeight) {
        this.textureId = textureId;
        this.texture = texture;
        this.glyphs = glyphs;
        this.rasterSize = rasterSize;
        this.lineHeight = lineHeight;
    }

    static RuntimeTtfFont load(String id, String resourcePath) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(resourcePath, "resourcePath");

        Font baseFont = loadFont(resourcePath);
        Font rasterFont = baseFont.deriveFont(Font.PLAIN, (float) RASTER_SIZE);
        LineMetrics lineMetrics = rasterFont.getLineMetrics("Ag", FONT_RENDER_CONTEXT);

        List<RasterGlyph> rasterGlyphs = rasterizeGlyphs(rasterFont);
        AtlasLayout layout = buildAtlas(rasterGlyphs);
        NativeImage nativeImage = toNativeImage(layout.image());

        Identifier textureId = Identifier.of("light", "runtime_fonts/" + sanitizePath(id));
        NativeImageBackedTexture texture = new NativeImageBackedTexture(textureId::toString, nativeImage);
        MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);

        Map<Integer, RuntimeTtfGlyph> glyphs = new HashMap<>(layout.glyphs().size());
        int atlasWidth = layout.image().getWidth();
        int atlasHeight = layout.image().getHeight();
        for (PlacedGlyph placed : layout.glyphs()) {
            float u0 = atlasWidth == 0 ? 0f : placed.x / (float) atlasWidth;
            float v0 = atlasHeight == 0 ? 0f : placed.y / (float) atlasHeight;
            float u1 = atlasWidth == 0 ? 0f : (placed.x + placed.width) / (float) atlasWidth;
            float v1 = atlasHeight == 0 ? 0f : (placed.y + placed.height) / (float) atlasHeight;
            glyphs.put(placed.codepoint, new RuntimeTtfGlyph(
                    placed.codepoint,
                    placed.x,
                    placed.y,
                    placed.width,
                    placed.height,
                    placed.xOffset,
                    placed.yOffset,
                    placed.xAdvance,
                    u0,
                    v0,
                    u1,
                    v1));
        }

        return new RuntimeTtfFont(textureId, texture, glyphs, RASTER_SIZE, lineMetrics.getHeight());
    }

    RuntimeTtfGlyph glyph(int codepoint) {
        return glyphs.get(codepoint);
    }

    Identifier textureId() {
        return textureId;
    }

    float rasterSize() {
        return rasterSize;
    }

    float lineHeight() {
        return lineHeight;
    }

    void destroy() {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        textureManager.destroyTexture(textureId);
        texture.close();
    }

    private static Font loadFont(String resourcePath) {
        String normalizedPath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        try (InputStream inputStream = RuntimeTtfFont.class.getClassLoader().getResourceAsStream(normalizedPath)) {
            if (inputStream == null) {
                throw new IllegalStateException("TTF resource not found: " + resourcePath);
            }
            return Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (IOException | FontFormatException exception) {
            throw new IllegalStateException("Failed to load TTF font: " + resourcePath, exception);
        }
    }

    private static List<RasterGlyph> rasterizeGlyphs(Font font) {
        List<RasterGlyph> glyphs = new ArrayList<>();
        for (int codepoint = Character.MIN_CODE_POINT; codepoint <= Character.MAX_VALUE; codepoint++) {
            if (!Character.isValidCodePoint(codepoint) || Character.isISOControl(codepoint) || Character.isSurrogate((char) codepoint)) {
                continue;
            }
            if (!font.canDisplay(codepoint)) {
                continue;
            }
            glyphs.add(rasterizeGlyph(font, codepoint));
        }
        if (glyphs.isEmpty()) {
            throw new IllegalStateException("TTF font has no displayable glyphs");
        }
        return glyphs;
    }

    private static RasterGlyph rasterizeGlyph(Font font, int codepoint) {
        String text = new String(Character.toChars(codepoint));
        GlyphVector glyphVector = font.createGlyphVector(FONT_RENDER_CONTEXT, text);
        java.awt.Rectangle bounds = glyphVector.getPixelBounds(FONT_RENDER_CONTEXT, 0f, 0f);
        float xAdvance = (float) glyphVector.getGlyphMetrics(0).getAdvanceX();

        int width = Math.max(0, bounds.width);
        int height = Math.max(0, bounds.height);
        float xOffset = bounds.x;
        float yOffset = -bounds.y;

        BufferedImage image = null;
        if (width > 0 && height > 0) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            try {
                graphics.setComposite(AlphaComposite.Src);
                graphics.setColor(new Color(255, 255, 255, 255));
                graphics.setFont(font);
                graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                graphics.drawGlyphVector(glyphVector, -bounds.x, -bounds.y);
            } finally {
                graphics.dispose();
            }
        }

        return new RasterGlyph(codepoint, width, height, xOffset, yOffset, xAdvance, image);
    }

    private static AtlasLayout buildAtlas(List<RasterGlyph> glyphs) {
        int atlasSize = INITIAL_ATLAS_SIZE;
        while (atlasSize <= MAX_ATLAS_SIZE) {
            AtlasLayout layout = tryBuildAtlas(glyphs, atlasSize);
            if (layout != null) {
                return layout;
            }
            atlasSize *= 2;
        }
        throw new IllegalStateException("Unable to fit TTF glyph atlas within " + MAX_ATLAS_SIZE + "px");
    }

    private static AtlasLayout tryBuildAtlas(List<RasterGlyph> glyphs, int atlasSize) {
        BufferedImage atlas = new BufferedImage(atlasSize, atlasSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = atlas.createGraphics();
        List<PlacedGlyph> placedGlyphs = new ArrayList<>(glyphs.size());
        try {
            graphics.setComposite(AlphaComposite.Src);
            graphics.setBackground(new Color(0, 0, 0, 0));
            graphics.clearRect(0, 0, atlasSize, atlasSize);

            int penX = PADDING;
            int penY = PADDING;
            int rowHeight = 0;

            for (RasterGlyph glyph : glyphs) {
                int boxWidth = glyph.width + PADDING;
                int boxHeight = glyph.height + PADDING;
                if (penX + boxWidth > atlasSize) {
                    penX = PADDING;
                    penY += rowHeight + PADDING;
                    rowHeight = 0;
                }
                if (penY + boxHeight > atlasSize) {
                    return null;
                }
                if (glyph.image != null) {
                    graphics.drawImage(glyph.image, penX, penY, null);
                }
                placedGlyphs.add(new PlacedGlyph(
                        glyph.codepoint,
                        penX,
                        penY,
                        glyph.width,
                        glyph.height,
                        glyph.xOffset,
                        glyph.yOffset,
                        glyph.xAdvance));
                penX += boxWidth + PADDING;
                rowHeight = Math.max(rowHeight, boxHeight);
            }
        } finally {
            graphics.dispose();
        }
        return new AtlasLayout(atlas, placedGlyphs);
    }

    private static NativeImage toNativeImage(BufferedImage image) {
        NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), false);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                nativeImage.setColorArgb(x, y, image.getRGB(x, y));
            }
        }
        return nativeImage;
    }

    private static String sanitizePath(String id) {
        return id.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/_-]", "_");
    }

    private record RasterGlyph(int codepoint, int width, int height, float xOffset, float yOffset, float xAdvance,
                               BufferedImage image) {
    }

    private record PlacedGlyph(int codepoint, int x, int y, int width, int height,
                               float xOffset, float yOffset, float xAdvance) {
    }

    private record AtlasLayout(BufferedImage image, List<PlacedGlyph> glyphs) {
    }
}
