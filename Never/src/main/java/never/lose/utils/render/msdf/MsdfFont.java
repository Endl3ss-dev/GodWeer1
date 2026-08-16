package never.lose.utils.render.msdf;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import never.lose.NeverLose;
import never.lose.utils.render.providers.ResourceProvider;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class MsdfFont {

    private final String                            name;
    private final AbstractTexture                   texture;
    private final FontData.AtlasData                atlas;
    private final FontData.MetricsData              metrics;
    private final Map<Integer, MsdfGlyph>           glyphs;
    private final Map<Integer, Map<Integer, Float>> kernings;

    private MsdfFont(String name, AbstractTexture texture,
                     FontData.AtlasData atlas, FontData.MetricsData metrics,
                     Map<Integer, MsdfGlyph> glyphs,
                     Map<Integer, Map<Integer, Float>> kernings) {
        this.name     = name;
        this.texture  = texture;
        this.atlas    = atlas;
        this.metrics  = metrics;
        this.glyphs   = glyphs;
        this.kernings = kernings;
    }

    public int getTextureId() {
        return this.texture.getGlId();
    }

    public void applyGlyphs(Matrix4f matrix, VertexConsumer consumer,
                            String text, float size, float thickness,
                            float spacing, float x, float y, float z, int color) {
        int prevChar = -1;
        for (int i = 0; i < text.length(); i++) {
            int _char = text.charAt(i);
            MsdfGlyph glyph = this.glyphs.get(_char);
            if (glyph == null) continue;

            Map<Integer, Float> kerning = this.kernings.get(prevChar);
            if (kerning != null) x += kerning.getOrDefault(_char, 0f) * size;

            x += glyph.apply(matrix, consumer, size, x, y, z, color) + thickness + spacing;
            prevChar = _char;
        }
    }

    public void applyGlyphsGradient(Matrix4f matrix, VertexConsumer consumer,
                                    String text, float size, float thickness,
                                    float spacing, float x, float y, float z) {
        int prevChar = -1;
        for (int i = 0; i < text.length(); i++) {
            int _char = text.charAt(i);
            MsdfGlyph glyph = this.glyphs.get(_char);
            if (glyph == null) continue;

            Map<Integer, Float> kerning = this.kernings.get(prevChar);
            if (kerning != null) x += kerning.getOrDefault(_char, 0f) * size;

            int color = NeverLose.getInstance().getThemeManager().getColor(i * 20).getRGB();
            x += glyph.apply(matrix, consumer, size, x, y, z, color) + thickness + spacing;
            prevChar = _char;
        }
    }

    public void applyShadow(Matrix4f matrix, VertexConsumer consumer,
                            String text, float size, float thickness,
                            float spacing, float x, float y, float z) {
        int prevChar = -1;
        for (int i = 0; i < text.length(); i++) {
            int _char = text.charAt(i);
            MsdfGlyph glyph = this.glyphs.get(_char);
            if (glyph == null) continue;

            Map<Integer, Float> kerning = this.kernings.get(prevChar);
            if (kerning != null) x += kerning.getOrDefault(_char, 0f) * size;

            int color = NeverLose.getInstance().getThemeManager().getColor(i * 20).getRGB();
            x += glyph.apply(matrix, consumer, size, x, y, z, color) + thickness + spacing;
            prevChar = _char;
        }
    }

    public float getWidth(String text, float size) {
        int   prevChar = -1;
        float width    = 0f;
        for (int i = 0; i < text.length(); i++) {
            int _char = text.charAt(i);
            MsdfGlyph glyph = this.glyphs.get(_char);
            if (glyph == null) continue;

            Map<Integer, Float> kerning = this.kernings.get(prevChar);
            if (kerning != null) width += kerning.getOrDefault(_char, 0f) * size;

            width += glyph.getWidth(size) + 0.3f;
            prevChar = _char;
        }
        return width;
    }

    public String              getName()    { return name;    }
    public FontData.AtlasData  getAtlas()   { return atlas;   }
    public FontData.MetricsData getMetrics() { return metrics; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {

        private String     name           = "?";
        private Identifier dataIdentifier;
        private Identifier atlasIdentifier;

        private Builder() {}

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder data(String dataFileName) {
            this.dataIdentifier = Identifier.of("n2ver", "fonts/" + dataFileName + ".json");
            return this;
        }

        public Builder atlas(String atlasFileName) {
            this.atlasIdentifier = Identifier.of("n2ver", "fonts/" + atlasFileName + ".png");
            return this;
        }

        public MsdfFont build() {
            FontData data = ResourceProvider.fromJsonToInstance(this.dataIdentifier, FontData.class);
            AbstractTexture texture = MinecraftClient.getInstance()
                    .getTextureManager().getTexture(this.atlasIdentifier);

            if (data == null) {
                throw new RuntimeException("Failed to read font data: " + this.dataIdentifier);
            }

            RenderSystem.recordRenderCall(() -> texture.setFilter(true, false));

            float aW = data.atlas().width();
            float aH = data.atlas().height();

            Map<Integer, MsdfGlyph> glyphs = data.glyphs().stream()
                    .collect(Collectors.toMap(
                            FontData.GlyphData::unicode,
                            g -> new MsdfGlyph(g, aW, aH)
                    ));

            Map<Integer, Map<Integer, Float>> kernings = new HashMap<>();
            data.kernings().forEach(k -> {
                Map<Integer, Float> map = kernings.computeIfAbsent(
                        k.leftChar(), id -> new HashMap<>());
                map.put(k.rightChar(), k.advance());
            });

            return new MsdfFont(name, texture, data.atlas(), data.metrics(), glyphs, kernings);
        }
    }
}