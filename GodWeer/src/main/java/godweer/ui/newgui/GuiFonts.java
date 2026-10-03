package godweer.ui.newgui;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import godweer.GodWeer;
import godweer.utils.render.ShaderUtils;
import godweer.utils.render.msdf.Fonts;
import godweer.utils.render.msdf.MsdfFont;

import net.minecraft.client.renderer.CompiledShaderProgram;

import org.joml.Matrix4f;

// Фасад над MSDF-шрифтами с метриками исходного клиента: размер задаётся напрямую в
// GUI-пикселях (без деления на 2, как в FontRenderer), y — базовая линия глифа.
// Именно на этих соглашениях построена вся раскладка newgui, поэтому любое отклонение сломало бы координаты.
public final class GuiFonts {

    public static final Face SF_BOLD = new Face(Fonts.SfBold);
    public static final Face SF_MEDIUM = new Face(Fonts.SfMedium);
    public static final Face SF_REGULAR = new Face(Fonts.SfRegular);
    public static final Face CLICKGUI = new Face(Fonts.ClickGuiFont);
    public static final Face ICONF = new Face(Fonts.IconF);

    private static boolean shaderWarned;

    private GuiFonts() {
    }

    public static final class Face {

        private final Fonts font;

        Face(Fonts font) {
            this.font = font;
        }

        public MsdfFont atlas() {
            return this.font.atlas();
        }

        public float getWidth(String text, float size) {
            MsdfFont atlas = atlas();
            if (atlas == null || text == null || text.isEmpty()) {
                return 0.0f;
            }
            // Метрика исходного клиента: сумма advance * size по глифам (MsdfFont.getWidth из Private).
            float width = 0.0f;
            for (int i = 0; i < text.length(); i++) {
                char raw = text.charAt(i);
                if (raw == '§' && i + 1 < text.length()) {
                    i++;
                    continue;
                }
                godweer.utils.render.msdf.MsdfGlyph glyph = atlas.getGlyph(raw);
                if (glyph != null) {
                    width += glyph.getSourceAdvance() * size;
                }
            }
            return width;
        }

        public float getHeight(String text, float size) {
            MsdfFont atlas = atlas();
            return atlas == null ? 0.0f : atlas.getBaselineHeight() * size;
        }

        public void draw(PoseStack stack, String text, float x, float y, int color, float size) {
            MsdfFont atlas = atlas();
            if (atlas == null || text == null || text.isEmpty()) {
                return;
            }

            CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.FONTS_MSDF);
            if (shader == null) {
                if (!shaderWarned) {
                    shaderWarned = true;
                    GodWeer.LOGGER.error("MSDF font shader is not compiled, newgui text will not render");
                }
                return;
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);

            set(shader, "TextureSize", atlas.getAtlasWidth(), atlas.getAtlasHeight());
            set(shader, "Range", atlas.getRange());
            set(shader, "Thickness", 0.0f);
            set(shader, "EdgeStrength", 0.5f);
            set(shader, "Color", 1.0f, 1.0f, 1.0f, 1.0f);
            set(shader, "OutlineThickness", 0.0f);
            set(shader, "OutlineColor", 0.0f, 0.0f, 0.0f, 1.0f);

            Uniform outline = shader.getUniform("Outline");
            if (outline != null) {
                outline.set(0);
            }

            RenderSystem.setShaderTexture(0, atlas.getTextureId());
            atlas.setFiltered();

            // Конвенция исходного клиента: y — верх текста, базовая линия = y + baselineHeight * size,
            // затем квад глифа опускается от базовой линии на topPosition * size (MsdfFont/MsdfGlyph из Private).
            float baselineY = y + atlas.getBaselineHeight() * size;
            int alpha = (color >>> 24) & 0xFF;
            int red = (color >> 16) & 0xFF;
            int green = (color >> 8) & 0xFF;
            int blue = color & 0xFF;

            Matrix4f matrix = stack.last().pose();
            BufferBuilder buffer = Tesselator.getInstance()
                    .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            float cursor = x;
            for (int i = 0; i < text.length(); i++) {
                char raw = text.charAt(i);
                if (raw == '§' && i + 1 < text.length()) {
                    i++;
                    continue;
                }
                godweer.utils.render.msdf.MsdfGlyph glyph = atlas.getGlyph(raw);
                if (glyph == null) {
                    continue;
                }
                float glyphWidth = glyph.getPlaneWidth();
                float glyphHeight = glyph.getPlaneHeight();
                if (glyphWidth != 0.0f && glyphHeight != 0.0f) {
                    float glyphY = baselineY - glyph.getTopPosition() * size;
                    float quadWidth = glyphWidth * size;
                    float quadHeight = glyphHeight * size;

                    buffer.addVertex(matrix, cursor, glyphY, 0.0f).setUv(glyph.getMinU(), glyph.getMinV()).setColor(red, green, blue, alpha);
                    buffer.addVertex(matrix, cursor, glyphY + quadHeight, 0.0f).setUv(glyph.getMinU(), glyph.getMaxV()).setColor(red, green, blue, alpha);
                    buffer.addVertex(matrix, cursor + quadWidth, glyphY + quadHeight, 0.0f).setUv(glyph.getMaxU(), glyph.getMaxV()).setColor(red, green, blue, alpha);
                    buffer.addVertex(matrix, cursor + quadWidth, glyphY, 0.0f).setUv(glyph.getMaxU(), glyph.getMinV()).setColor(red, green, blue, alpha);
                }
                cursor += glyph.getSourceAdvance() * size;
            }

            MeshData mesh = buffer.build();
            if (mesh != null) {
                RenderSystem.setShader(ShaderUtils.FONTS_MSDF);
                BufferUploader.drawWithShader(mesh);
            }

            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }

        private static void set(CompiledShaderProgram shader, String name, float value) {
            Uniform uniform = shader.getUniform(name);
            if (uniform != null) {
                uniform.set(value);
            }
        }

        private static void set(CompiledShaderProgram shader, String name, float x, float y) {
            Uniform uniform = shader.getUniform(name);
            if (uniform != null) {
                uniform.set(x, y);
            }
        }

        private static void set(CompiledShaderProgram shader, String name, float x, float y, float z, float w) {
            Uniform uniform = shader.getUniform(name);
            if (uniform != null) {
                uniform.set(x, y, z, w);
            }
        }
    }
}
