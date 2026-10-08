package swerve.utils.render.msdf;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import swerve.Swerve;
import swerve.utils.render.ShaderUtils;

import net.minecraft.client.renderer.CompiledShaderProgram;

import org.joml.Matrix4f;

import com.mojang.blaze3d.shaders.Uniform;

public final class FontRenderer {

    private static boolean shaderWarned;

    private static final PoseStack IDENTITY = new PoseStack();
    private static final float BASELINE_OFFSET = 1.5f;
    private static final float DEFAULT_FADE_ZONE = 25.0f;

    private final MsdfFont font;
    private final float size;

    public FontRenderer(MsdfFont font, float size) {
        this.font = font;
        this.size = size;
    }

    public MsdfFont getFont() {
        return this.font;
    }

    public float getSize() {
        return this.size;
    }

    public float getHeight() {
        return this.size;
    }

    public float getFontHeight() {
        return this.size;
    }

    public float getStringWidth(CharSequence text) {
        return text == null ? 0.0f : this.font.getWidth(text, this.size) * 0.5f;
    }

    public float getWidth(CharSequence text) {
        return getStringWidth(text);
    }

    public void drawString(PoseStack stack, String text, double x, double y, int color) {
        draw(stack, text, (float) x, (float) y, color);
    }

    public void drawString(PoseStack stack, String text, float x, float y, int color) {
        draw(stack, text, x, y, color);
    }

    public void drawString(String text, float x, float y, int color) {
        draw(IDENTITY, text, x, y, color);
    }

    public void drawCenteredString(PoseStack stack, String text, double x, double y, int color) {
        draw(stack, text, (float) (x - getStringWidth(text) * 0.5d), (float) y, color);
    }

    public void drawCenteredString(PoseStack stack, String text, float x, float y, int color) {
        draw(stack, text, x - getStringWidth(text) * 0.5f, y, color);
    }

    public void drawRight(PoseStack stack, String text, double x, double y, int color) {
        draw(stack, text, (float) (x - getStringWidth(text)), (float) y, color);
    }

    public void drawRight(PoseStack stack, String text, float x, float y, int color) {
        draw(stack, text, x - getStringWidth(text), y, color);
    }

    public void draw(PoseStack stack, String text, double x, double y, int color) {
        draw(stack, text, (float) x, (float) y, color);
    }

    public void draw(PoseStack stack, String text, float x, float y, int color) {
        if (text == null || text.isEmpty()) {
            return;
        }

        float localSize = this.size * 0.5f;
        if (this.font.getWidth(text, localSize) <= 0.0f) {
            return;
        }

        CompiledShaderProgram shader = begin();
        if (shader == null) {
            return;
        }

        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        this.font.applyGlyphs(matrix, buffer, localSize, text, 0.0f,
                x, y - BASELINE_OFFSET + this.font.getBaselineHeight() * localSize, 0.0f, color);

        flush(buffer);
        end();
    }

    public void drawStringWithShadow(PoseStack stack, String text, float x, float y, int color) {
        draw(stack, text, x + 1.0f, y + 1.0f, 0x55000000);
        draw(stack, text, x, y, color);
    }

    public void drawStringWithOutline(PoseStack stack, String text, float x, float y, int color, int outlineColor) {
        if (text == null || text.isEmpty()) {
            return;
        }

        float localSize = this.size * 0.5f;
        if (this.font.getWidth(text, localSize) <= 0.0f) {
            return;
        }

        CompiledShaderProgram shader = begin();
        if (shader == null) {
            return;
        }

        Matrix4f matrix = stack.last().pose();
        float baseline = y - BASELINE_OFFSET + this.font.getBaselineHeight() * localSize;

        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        this.font.applyGlyphs(matrix, buffer, localSize, text, 0.0f, x - 1.0f, baseline, 0.0f, outlineColor);
        this.font.applyGlyphs(matrix, buffer, localSize, text, 0.0f, x + 1.0f, baseline, 0.0f, outlineColor);
        this.font.applyGlyphs(matrix, buffer, localSize, text, 0.0f, x, baseline - 1.0f, 0.0f, outlineColor);
        this.font.applyGlyphs(matrix, buffer, localSize, text, 0.0f, x, baseline + 1.0f, 0.0f, outlineColor);
        this.font.applyGlyphs(matrix, buffer, localSize, text, 0.0f, x, baseline, 0.0f, color);

        flush(buffer);
        end();
    }

    public void drawGradientStringHorizontal(String text, float x, float y, int leftColor, int rightColor) {
        drawGradientStringHorizontal(IDENTITY, text, x, y, leftColor, rightColor);
    }

    public void drawGradientStringHorizontal(PoseStack stack, String text, float x, float y,
                                             int leftColor, int rightColor) {
        if (text == null || text.isEmpty()) {
            return;
        }

        float localSize = this.size * 0.5f;
        if (this.font.getWidth(text, localSize) <= 0.0f) {
            return;
        }

        CompiledShaderProgram shader = begin();
        if (shader == null) {
            return;
        }

        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        this.font.applyGlyphsGradient(matrix, buffer, localSize, text, 0.0f,
                x, y - BASELINE_OFFSET + this.font.getBaselineHeight() * localSize, 0.0f, leftColor, rightColor);

        flush(buffer);
        end();
    }

    public void drawGradientStringHorizontal(PoseStack stack, String text, float x, float y,
                                             int topLeftColor, int topRightColor,
                                             int bottomLeftColor, int bottomRightColor) {
        int left = MsdfFont.lerpColor(topLeftColor, bottomLeftColor, 0.5f);
        int right = MsdfFont.lerpColor(topRightColor, bottomRightColor, 0.5f);
        drawGradientStringHorizontal(stack, text, x, y, left, right);
    }

    public void drawGradientStringVertical(PoseStack stack, String text, float x, float y,
                                           int topColor, int bottomColor) {
        draw(stack, text, x, y, MsdfFont.lerpColor(topColor, bottomColor, 0.5f));
    }

    public void drawAnimatedGradientStringHorizontal(String text, float x, float y,
                                                     int leftColor, int rightColor, float speed) {
        drawAnimatedGradientStringHorizontal(IDENTITY, text, x, y, leftColor, rightColor, speed, 1.15f);
    }

    public void drawAnimatedGradientStringHorizontal(PoseStack stack, String text, float x, float y,
                                                     int leftColor, int rightColor, float speed) {
        drawAnimatedGradientStringHorizontal(stack, text, x, y, leftColor, rightColor, speed, 1.15f);
    }

    public void drawAnimatedGradientStringHorizontal(String text, float x, float y,
                                                     int leftColor, int rightColor, float speed, float waveScale) {
        drawAnimatedGradientStringHorizontal(IDENTITY, text, x, y, leftColor, rightColor, speed, waveScale);
    }

    // Ping-pong по времени даёт «бегущую волну» без пересборки палитры: цвета концов
    // градиента интерполируются один раз на кадр, а не на каждый символ.
    public void drawAnimatedGradientStringHorizontal(PoseStack stack, String text, float x, float y,
                                                     int leftColor, int rightColor, float speed, float waveScale) {
        if (text == null || text.isEmpty()) {
            return;
        }

        double timeOffset = (System.currentTimeMillis() * 0.001d * Math.max(0.01f, speed)) % 2.0d;
        float safeScale = Math.max(0.01f, waveScale);

        int animatedLeft = MsdfFont.lerpColor(leftColor, rightColor, pingPong01((float) timeOffset));
        int animatedRight = MsdfFont.lerpColor(leftColor, rightColor, pingPong01(safeScale + (float) timeOffset));

        drawGradientStringHorizontal(stack, text, x, y, animatedLeft, animatedRight);
    }

    public void drawStringWithFade(PoseStack stack, String text, float x, float y, float maxWidth, int color) {
        if (text == null || text.isEmpty() || maxWidth <= 1.0f) {
            return;
        }

        int alpha = (color >>> 24) & 0xFF;
        if (alpha != 0 && alpha <= 4) {
            return;
        }

        float localSize = this.size * 0.5f;
        if (this.font.getWidth(text, localSize) <= 0.0f) {
            return;
        }

        CompiledShaderProgram shader = begin();
        if (shader == null) {
            return;
        }

        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        this.font.applyGlyphsFade(matrix, buffer, localSize, text, 0.0f,
                x, y - BASELINE_OFFSET + this.font.getBaselineHeight() * localSize, 0.0f,
                color, maxWidth, DEFAULT_FADE_ZONE);

        flush(buffer);
        end();
    }

    public void drawParagraph(PoseStack stack, String text, double x, double y, int defaultColor) {
        drawParagraph(stack, text, (float) x, (float) y, defaultColor);
    }

    public void drawParagraph(PoseStack stack, String text, float x, float y, int defaultColor) {
        if (text == null || text.isEmpty()) {
            return;
        }

        float localSize = this.size * 0.5f;
        if (this.font.getWidth(text, localSize) <= 0.0f) {
            return;
        }

        CompiledShaderProgram shader = begin();
        if (shader == null) {
            return;
        }

        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        this.font.applyGlyphsFormatted(matrix, buffer, localSize, text, 0.0f,
                x, y - BASELINE_OFFSET + this.font.getBaselineHeight() * localSize, 0.0f, defaultColor);

        flush(buffer);
        end();
    }

    // Цвет пишется в вершины, поэтому uniform Color держим белым: одна строка = один draw-call
    // независимо от градиента, фейда и §-кодов.
    private CompiledShaderProgram begin() {
        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.FONTS_MSDF);
        if (shader == null) {
            if (!shaderWarned) {
                shaderWarned = true;
                Swerve.LOGGER.error("MSDF font shader '{}' is not compiled, text will not render",
                        ShaderUtils.FONTS_MSDF.configId());
            }
            return null;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        setUniform(shader, "TextureSize", this.font.getAtlasWidth(), this.font.getAtlasHeight());
        setUniform(shader, "Range", this.font.getRange());
        setUniform(shader, "Thickness", 0.0f);
        setUniform(shader, "EdgeStrength", 0.5f);
        setUniform(shader, "Color", 1.0f, 1.0f, 1.0f, 1.0f);
        setUniform(shader, "OutlineThickness", 0.0f);
        setUniform(shader, "OutlineColor", 0.0f, 0.0f, 0.0f, 1.0f);

        Uniform outline = shader.getUniform("Outline");
        if (outline != null) {
            outline.set(0);
        }

        RenderSystem.setShaderTexture(0, this.font.getTextureId());
        this.font.setFiltered();

        return shader;
    }

    private void flush(BufferBuilder buffer) {
        MeshData mesh = buffer.build();
        if (mesh == null) {
            return;
        }

        RenderSystem.setShader(ShaderUtils.FONTS_MSDF);
        BufferUploader.drawWithShader(mesh);
    }

    private void end() {
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void setUniform(CompiledShaderProgram shader, String name, float value) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(value);
        }
    }

    private static void setUniform(CompiledShaderProgram shader, String name, float x, float y) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(x, y);
        }
    }

    private static void setUniform(CompiledShaderProgram shader, String name, float x, float y, float z, float w) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(x, y, z, w);
        }
    }

    private static float pingPong01(float value) {
        float wrapped = value % 2.0f;
        if (wrapped < 0.0f) {
            wrapped += 2.0f;
        }
        return wrapped > 1.0f ? 2.0f - wrapped : wrapped;
    }
}
