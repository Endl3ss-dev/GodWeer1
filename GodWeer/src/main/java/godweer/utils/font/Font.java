package godweer.utils.font;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.utils.color.ColorUtils;
import godweer.utils.render.msdf.FontRenderer;
import godweer.utils.render.msdf.MsdfFont;
import godweer.utils.render.msdf.MsdfGlyph;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Порт {@code org.privates.client.utils.render.font.Font} из клиента Private.
 * <p>
 * Слой совместимости: наружу сохранён исходный API ({@code draw}, {@code getWidth}, {@code getHeight}
 * и т.д. с размером в каждом вызове), внутри — существующий рендер GodWeer
 * ({@link FontRenderer} + {@link MsdfFont} с кэшем на размер).
 * <p>
 * {@link FontRenderer} внутри работает с половинным размером, поэтому наружный размер
 * всегда передаётся как {@code size * 2}, а метрики считаются по исходному размеру —
 * так ширина строки совпадает с реально нарисованным продвижением глифов.
 */
public final class Font {

    private static final char FORMATTING_PREFIX = '§';

    private static final float SIZE_SCALE = 2.0F;
    private static final float MIN_SIZE = 4.0F;
    private static final float MAX_SIZE = 48.0F;

    private static final float SHADOW_OFFSET = 0.25F;
    private static final float SHADOW_BRIGHTNESS = 0.25F;
    private static final float OUTLINE_BRIGHTNESS = 0.25F;
    private static final float OUTLINE_ALPHA = 0.5F;
    private static final int DEFAULT_OUTLINE_COLOR = 0xFF000000;

    private static final float DEFAULT_GRADIENT_SPEED = 2.0F;
    private static final float DEFAULT_GRADIENT_WAVE_SCALE = 1.15F;

    private static final PoseStack IDENTITY = new PoseStack();

    private final godweer.utils.render.msdf.Fonts atlas;

    private static float gradientSpeed = DEFAULT_GRADIENT_SPEED;
    private static float gradientWaveScale = DEFAULT_GRADIENT_WAVE_SCALE;

    public Font(godweer.utils.render.msdf.Fonts atlas) {
        this.atlas = atlas;
    }

    public godweer.utils.render.msdf.Fonts getAtlas() {
        return this.atlas;
    }

    public static void setGradientSpeed(float speed) {
        gradientSpeed = speed <= 0.0F ? DEFAULT_GRADIENT_SPEED : speed;
    }

    public static float getGradientSpeed() {
        return gradientSpeed;
    }

    public static void setGradientWaveScale(float waveScale) {
        gradientWaveScale = waveScale <= 0.0F ? DEFAULT_GRADIENT_WAVE_SCALE : waveScale;
    }

    public float getBaselineHeight() {
        MsdfFont msdf = msdf(MIN_SIZE);
        return msdf == null ? 0.0F : msdf.getBaselineHeight();
    }

    public float getHeight(String text, float size) {
        MsdfFont msdf = msdf(size);
        return msdf == null ? 0.0F : msdf.getBaselineHeight() * size;
    }

    public float getWidth(String text, float size) {
        if (text == null || text.isEmpty()) {
            return 0.0F;
        }
        return width(text, size);
    }

    public float getWidth(Component component, float size) {
        if (component == null) {
            return 0.0F;
        }

        List<Component> siblings = component.getSiblings();
        if (siblings.isEmpty()) {
            return width(component.getString(), size);
        }

        float total = 0.0F;
        for (int i = 0, count = siblings.size(); i < count; i++) {
            total += width(siblings.get(i).getString(), size);
        }
        return total;
    }

    public float getCharWidth(char character, float size) {
        MsdfFont msdf = msdf(size);
        if (msdf == null) {
            return 0.0F;
        }

        MsdfGlyph glyph = msdf.getGlyph(character);
        return glyph == null ? 0.0F : glyph.getAdvance(size);
    }

    public void draw(PoseStack stack, String text, float x, float y, int color, float size) {
        if (text == null || text.isEmpty()) {
            return;
        }

        FontRenderer renderer = renderer(size);
        if (renderer == null) {
            return;
        }

        if (ColorFormatting.hasFormatting(text)) {
            drawFormatted(stack, renderer, text, x, y, color, size);
            return;
        }

        // §-коды парсятся самим рендером (один draw-call на строку), маркеры ${...} — нет.
        if (text.indexOf(FORMATTING_PREFIX) >= 0) {
            renderer.drawParagraph(stack, text, x, y, color);
            return;
        }

        renderer.draw(stack, text, x, y, color);
    }

    public void draw(PoseStack stack, String text, float x, float y, int color, float size,
                     boolean outline, float outlineThickness, int outlineColor) {
        if (!outline) {
            draw(stack, text, x, y, color, size);
            return;
        }

        FontRenderer renderer = renderer(size);
        if (renderer == null) {
            return;
        }

        renderer.drawStringWithOutline(stack, text, x, y, color,
                outlineColor == 0 ? DEFAULT_OUTLINE_COLOR : outlineColor);
    }

    public void drawShadow(PoseStack stack, String text, float x, float y, int color, float size) {
        draw(stack, text, x + SHADOW_OFFSET, y + SHADOW_OFFSET, ColorUtils.multDark(color, SHADOW_BRIGHTNESS), size);
        draw(stack, text, x, y, color, size);
    }

    public void drawOutline(PoseStack stack, String text, float x, float y, int color, float size) {
        int outlineColor = ColorUtils.multDark(ColorUtils.multAlpha(color, OUTLINE_ALPHA), OUTLINE_BRIGHTNESS);
        draw(stack, text, x, y, color, size, true, 0.0F, outlineColor);
    }

    public void drawCenter(PoseStack stack, String text, float x, float y, int color, float size) {
        draw(stack, text, x - (getWidth(text, size) * 0.5F), y, color, size);
    }

    public void drawCenterShadow(PoseStack stack, String text, float x, float y, int color, float size) {
        drawShadow(stack, text, x - (getWidth(text, size) * 0.5F), y, color, size);
    }

    public void drawCenterOutline(PoseStack stack, String text, float x, float y, int color, float size) {
        drawOutline(stack, text, x - (getWidth(text, size) * 0.5F), y, color, size);
    }

    public void drawRight(PoseStack stack, String text, float x, float y, int color, float size) {
        draw(stack, text, x - getWidth(text, size), y, color, size);
    }

    public void drawRightShadow(PoseStack stack, String text, float x, float y, int color, float size) {
        drawShadow(stack, text, x - getWidth(text, size), y, color, size);
    }

    public void drawRightOutline(PoseStack stack, String text, float x, float y, int color, float size) {
        drawOutline(stack, text, x - getWidth(text, size), y, color, size);
    }

    public void drawCenteredY(String text, float x, float y, Color color, float size) {
        drawCenteredY(IDENTITY, text, x, y, color, size);
    }

    public void drawCenteredY(PoseStack stack, String text, float x, float y, Color color, float size) {
        drawCenteredY(stack, text, x, y, color == null ? 0xFFFFFFFF : color.getRGB(), size);
    }

    public void drawCenteredY(PoseStack stack, String text, float x, float y, int color, float size) {
        draw(stack, text, x, y - (getHeight(text, size) * 0.5F), color, size);
    }

    public void drawCenteredXY(String text, float x, float y, Color color, float size) {
        drawCenteredXY(IDENTITY, text, x, y, color, size);
    }

    public void drawCenteredXY(PoseStack stack, String text, float x, float y, Color color, float size) {
        drawCenteredXY(stack, text, x, y, color == null ? 0xFFFFFFFF : color.getRGB(), size);
    }

    public void drawCenteredXY(PoseStack stack, String text, float x, float y, int color, float size) {
        draw(stack, text, x - (getWidth(text, size) * 0.5F), y - (getHeight(text, size) * 0.5F), color, size);
    }

    public void drawGradient(PoseStack stack, String text, float x, float y, int color1, int color2, float size) {
        FontRenderer renderer = renderer(size);
        if (renderer == null || text == null || text.isEmpty()) {
            return;
        }

        renderer.drawAnimatedGradientStringHorizontal(stack, text, x, y, color1, color2, gradientSpeed, gradientWaveScale);
    }

    public void drawGradientCenter(PoseStack stack, String text, float x, float y, int color1, int color2, float size) {
        drawGradient(stack, text, x - (getWidth(text, size) * 0.5F), y, color1, color2, size);
    }

    public void drawGradientRight(PoseStack stack, String text, float x, float y, int color1, int color2, float size) {
        drawGradient(stack, text, x - getWidth(text, size), y, color1, color2, size);
    }

    public float drawSplitted(PoseStack stack, String text, String splitter, float x, float y, float width,
                              int color, float size) {
        return splitted(stack, text, splitter, width, size,
                (target, line, offset) -> draw(target, line, x, y + offset, color, size), true);
    }

    public float drawSplittedCenter(PoseStack stack, String text, String splitter, float x, float y, float width,
                                    int color, float size) {
        return splitted(stack, text, splitter, width, size,
                (target, line, offset) -> drawCenter(target, line, x + (width * 0.5F), y + offset, color, size), true);
    }

    public float drawSplittedRight(PoseStack stack, String text, String splitter, float x, float y, float width,
                                   int color, float size) {
        return splitted(stack, text, splitter, width, size,
                (target, line, offset) -> drawRight(target, line, x + width, y + offset, color, size), true);
    }

    public float splitted(PoseStack stack, String text, String splitter, float width, float size,
                          TextDrawer drawer, boolean draw) {
        List<String> lines = TextUtils.splitLine(text, this, size, width, splitter);
        float offset = 0.0F;

        for (int i = 0, count = lines.size(); i < count; i++) {
            if (draw) {
                drawer.draw(stack, lines.get(i), offset);
            }
            offset += size;
        }

        return offset;
    }

    public void drawTextComponent(PoseStack stack, Component component, float x, float y, int color,
                                  boolean shadow, float size) {
        if (component == null) {
            return;
        }

        StringBuilder builder = new StringBuilder(component.getString().length() + 16);
        List<Component> siblings = component.getSiblings();

        if (siblings.isEmpty()) {
            builder.append(component.getString());
        } else {
            int alpha = ColorUtils.alpha(color);
            if (alpha == 0) {
                alpha = 255;
            }
            for (int i = 0, count = siblings.size(); i < count; i++) {
                appendSibling(builder, siblings.get(i), color, alpha);
            }
        }

        String text = builder.toString();
        if (shadow) {
            drawShadow(stack, text, x, y, color, size);
        } else {
            draw(stack, text, x, y, color, size);
        }
    }

    private void appendSibling(StringBuilder builder, Component sibling, int defaultColor, int defaultAlpha) {
        Style style = sibling.getStyle();
        TextColor textColor = style.getColor();

        builder.append(textColor == null
                ? ColorFormatting.getColor(defaultColor)
                : ColorFormatting.getColor((textColor.getValue() & 0xFFFFFF) | (defaultAlpha << 24)));
        builder.append(sibling.getString());
    }

    private void drawFormatted(PoseStack stack, FontRenderer renderer, String text, float x, float y,
                               int color, float size) {
        Matcher matcher = ColorFormatting.PATTERN.matcher(text);
        int lastIndex = 0;
        int currentColor = color;
        float cursor = x;

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String part = text.substring(lastIndex, matcher.start());
                renderer.draw(stack, part, cursor, y, currentColor);
                cursor += width(part, size);
            }

            currentColor = matcher.group().equalsIgnoreCase(ColorFormatting.reset())
                    ? color
                    : ColorFormatting.parseColor(matcher, color);
            lastIndex = matcher.end();
        }

        if (lastIndex < text.length()) {
            renderer.draw(stack, text.substring(lastIndex), cursor, y, currentColor);
        }
    }

    private float width(String text, float size) {
        MsdfFont msdf = msdf(size);
        if (msdf == null) {
            return 0.0F;
        }

        String clean = ColorFormatting.removeFormatting(text);
        return clean == null || clean.isEmpty() ? 0.0F : msdf.getWidth(clean, size);
    }

    private MsdfFont msdf(float size) {
        FontRenderer renderer = renderer(size);
        return renderer == null ? null : renderer.getFont();
    }

    private FontRenderer renderer(float size) {
        if (this.atlas == null) {
            return null;
        }

        float clamped = size < MIN_SIZE ? MIN_SIZE : (size > MAX_SIZE ? MAX_SIZE : size);
        return this.atlas.size(Math.round(clamped * SIZE_SCALE));
    }

    @FunctionalInterface
    public interface TextDrawer {
        void draw(PoseStack stack, String line, float yOffset);
    }
}
