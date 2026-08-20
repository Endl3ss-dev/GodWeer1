package org.privates.client.utils.render.font;

import lombok.Getter;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.api.interfaces.IRender;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.render.color.ColorFormatting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.shader.ShaderManager;
import org.privates.client.utils.render.text.TextUtils;
import org.privates.client.utils.tuples.Triplet;

import java.util.List;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_COLOR;

@Getter
public class Font implements IMinecraft, IRender {
    private final MsdfFont font;
    private final FontData.AtlasData atlas;
    private final float baselineHeight;

    public Font(String name) {
        font = MsdfFont.builder().withAtlas(name + ".png").withData(name + ".json").build();
        atlas = font.getAtlas();
        baselineHeight = font.getMetrics().baselineHeight();
    }

    public void drawTextComponent(MatrixStack matrix, ITextComponent textComponent, float x, float y, int color, boolean shadow, float size) {
        StringBuilder sb = new StringBuilder(textComponent.getString().length() << 1);
        float colorAlpha = ColorUtil.alphaf(color);

        for (ITextComponent component : textComponent.getSiblings()) {
            List<ITextComponent> siblings = component.getSiblings();
            if (!siblings.isEmpty()) {
                for (ITextComponent charComponent : siblings) {
                    appendComponent(sb, charComponent, color, colorAlpha);
                }
            } else {
                appendComponent(sb, component, color, colorAlpha);
            }
        }
        draw(matrix, sb.toString(), x, y, color, size);
    }

    private void appendComponent(StringBuilder sb, ITextComponent component, int defaultColor, float colorAlpha) {
        Integer componentColor = component.getStyle().getColor() != null ? component.getStyle().getColor().getColor() : null;
        sb.append(ColorFormatting.getColor(componentColor != null ? ColorUtil.replAlpha(componentColor, colorAlpha) : defaultColor));
        sb.append(replaceSymbols(component.getString()));
    }

    public float getWidth(ITextComponent textComponent, float size) {
        StringBuilder sb = new StringBuilder(textComponent.getString().length());

        for (ITextComponent component : textComponent.getSiblings()) {
            List<ITextComponent> siblings = component.getSiblings();
            if (!siblings.isEmpty()) {
                for (ITextComponent charComponent : siblings) {
                    sb.append(replaceSymbols(charComponent.getString()));
                }
            } else {
                sb.append(replaceSymbols(component.getString()));
            }
        }
        return getWidth(sb.toString(), size);
    }

    private String replaceSymbols(String string) {
        return string
                .replace("⚡", "")
                .replace("ᴀ", "a").replace("ʙ", "b").replace("ᴄ", "c").replace("ᴅ", "d")
                .replace("ᴇ", "e").replace("ғ", "f").replace("ɢ", "g").replace("ʜ", "h")
                .replace("ɪ", "i").replace("ᴊ", "j").replace("ᴋ", "k").replace("ʟ", "l")
                .replace("ᴍ", "m").replace("ɴ", "n").replace("ᴏ", "o").replace("ᴘ", "p")
                .replace("ǫ", "q").replace("ʀ", "r").replace("ᴛ", "t").replace("ᴜ", "u")
                .replace("ᴠ", "v").replace("ᴡ", "w").replace("ʏ", "y").replace("ᴢ", "z");
    }

    public void drawWithSpace(MatrixStack matrix, String text, float x, float y, int color, float size, float space) {
        draw(matrix, text, x, y, color, size, false, 0F, -1, space);
    }

    public void drawGradient(MatrixStack matrix, String text, float x, float y, int color1, int color2, float size) {
        int speed = Theme.getInstance().getSpeed();
        int len = text.length();
        StringBuilder sb = new StringBuilder(len * 10);

        for (int i = 0; i < len; i++) {
            sb.append(ColorFormatting.getColor(ColorUtil.fade(speed, i * speed, color1, color2))).append(text.charAt(i));
        }
        draw(matrix, sb.toString(), x, y, -1, size);
    }

    public void drawGradientCenter(MatrixStack matrix, String text, float x, float y, int color1, int color2, float size) {
        drawGradient(matrix, text, x - (getWidth(text, size) * 0.5F), y, color1, color2, size);
    }

    public void drawGradientRight(MatrixStack matrix, String text, float x, float y, int color1, int color2, float size) {
        drawGradient(matrix, text, x - getWidth(text, size), y, color1, color2, size);
    }

    public float drawSplitted(MatrixStack matrix, String text, String splitter, float x, float y, float width, int color, float size) {
        return splitted(matrix, text, splitter, width, size, (m, s, o) -> draw(m, s, x, y + o, color, size), true);
    }

    public float drawSplittedCenter(MatrixStack matrix, String text, String splitter, float x, float y, float width, int color, float size) {
        return splitted(matrix, text, splitter, width, size, (m, s, o) -> drawCenter(m, s, x + (width * 0.5F), y + o, color, size), true);
    }

    public float drawSplittedRight(MatrixStack matrix, String text, String splitter, float x, float y, float width, int color, float size) {
        return splitted(matrix, text, splitter, width, size, (m, s, o) -> drawRight(m, s, x + width, y + o, color, size), true);
    }

    public float splitted(MatrixStack matrix, String text, String splitter, float width, float size, Triplet.TriConsumer<MatrixStack, String, Float> drawFunction, boolean draw) {
        List<String> strings = TextUtils.splitLine(text, this, size, width, splitter);
        float offset = 0.0F;
        for (String str : strings) {
            if (draw) drawFunction.accept(matrix, str, offset);
            offset += size;
        }
        return offset;
    }

    public void draw(MatrixStack matrix, String text, float x, float y, int color, float size) {
        draw(matrix, text, x, y, color, size, false, 0F, -1, 0);
    }

    public void drawShadow(MatrixStack matrix, String text, float x, float y, int color, float size) {
        int shadowColor = ColorUtil.multDark(color, 0.25F);
        draw(matrix, text, x + 0.25F, y + 0.25F, shadowColor, size, false, 0F, -1, 0);
        draw(matrix, text, x, y, color, size, false, 0F, -1, 0);
    }

    public void drawOutline(MatrixStack matrix, String text, float x, float y, int color, float size) {
        draw(matrix, text, x, y, color, size, true, 0.25F, ColorUtil.multDark(ColorUtil.multAlpha(color, 0.5F), 0.25F), 0);
    }

    public void drawRight(MatrixStack matrix, String text, float x, float y, int color, float size) {
        draw(matrix, text, x - getWidth(text, size), y, color, size);
    }

    public void drawRightShadow(MatrixStack matrix, String text, float x, float y, int color, float size) {
        drawShadow(matrix, text, x - getWidth(text, size), y, color, size);
    }

    public void drawRightOutline(MatrixStack matrix, String text, float x, float y, int color, float size) {
        drawOutline(matrix, text, x - getWidth(text, size), y, color, size);
    }

    public void drawCenteredY(String text, float x, float y, java.awt.Color color, float size) {
        draw(new MatrixStack(), text, x, y - (baselineHeight * size * 0.5F), color.getRGB(), size);
    }

    public void drawCenteredXY(String text, float x, float y, java.awt.Color color, float size) {
        float halfHeight = baselineHeight * size * 0.5F;
        draw(new MatrixStack(), text, x - (getWidth(text, size) * 0.5F), y - halfHeight, color.getRGB(), size);
    }

    public float getHeight(String text, float size) {
        return baselineHeight * size;
    }

    public void drawCenteredY(MatrixStack matrix, String text, float x, float y, java.awt.Color color, float size) {
        draw(matrix, text, x, y - (baselineHeight * size * 0.5F), color.getRGB(), size);
    }

    public void drawCenteredXY(MatrixStack matrix, String text, float x, float y, java.awt.Color color, float size) {
        float halfHeight = baselineHeight * size * 0.5F;
        draw(matrix, text, x - (getWidth(text, size) * 0.5F), y - halfHeight, color.getRGB(), size);
    }

    public void drawCenteredY(MatrixStack matrix, String text, float x, float y, int color, float size) {
        draw(matrix, text, x, y - (baselineHeight * size * 0.5F), color, size);
    }

    public void drawCenteredXY(MatrixStack matrix, String text, float x, float y, int color, float size) {
        float halfHeight = baselineHeight * size * 0.5F;
        draw(matrix, text, x - (getWidth(text, size) * 0.5F), y - halfHeight, color, size);
    }

    public void drawCenter(MatrixStack matrix, String text, float x, float y, int color, float size) {
        draw(matrix, text, x - (getWidth(text, size) * 0.5F), y, color, size);
    }

    public void drawCenterShadow(MatrixStack matrix, String text, float x, float y, int color, float size) {
        drawShadow(matrix, text, x - (getWidth(text, size) * 0.5F), y, color, size);
    }

    public void drawCenterOutline(MatrixStack matrix, String text, float x, float y, int color, float size) {
        drawOutline(matrix, text, x - (getWidth(text, size) * 0.5F), y, color, size);
    }

    public void draw(MatrixStack matrix, String text, float x, float y, int color, float size, boolean outline, float outlineThickness, int outlineColor) {
        draw(matrix, text, x, y, color, size, outline, outlineThickness, outlineColor, 0);
    }

    public void draw(MatrixStack matrix, String text, float x, float y, int color, float size, boolean outline, float outlineThickness, int outlineColor, float space) {
        if (text == null || text.isEmpty()) return;

        matrix.push();

        ShaderManager shader = ShaderManager.fontShader;

        shader.load();
        shader.setUniformi("image", 0)
                .setUniformf("textureSize", atlas.width(), atlas.height())
                .setUniformf("range", atlas.range())
                .setUniformf("edgeStrength", -0.5F, 0.5F)
                .setUniformf("thickness", 0.0F)
                .setUniformi("outline", outline ? 1 : 0)
                .setUniformf("outlineThickness", outlineThickness)
                .setUniformf("outlineColor", ColorUtil.getRGBAf(outlineColor));

        font.bind();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        IRender.BUFFER.begin(GL11.GL_QUADS, POSITION_TEX_COLOR);

        String strippedText = TextFormatting.removeFormatting(text);
        if (strippedText == null) strippedText = text;

        font.applyGlyphs(matrix, BUFFER, size, strippedText, 0, x, y, 0, space, color);

        IRender.TESSELLATOR.draw();

        RenderSystem.disableBlend();
        font.unbind();
        shader.unload();

        matrix.pop();
    }

    public float getWidth(String text, float size) {
        return font.getWidth(text, size);
    }

    public float getWidth(String text, float size, float thickness) {
        return font.getWidth(text, size, thickness);
    }
}