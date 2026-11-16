package tech.lightclient.ui.screen;

import net.minecraft.util.Identifier;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.render.GpuCompatibility;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;

public class ButtonComponent {
    private static final float BUTTON_BACKGROUND_ASPECT_RATIO = 16f / 9f;

    public String text;
    public float x;
    public float y;
    public float w;
    public float h;
    public Runnable onClick;
    private final Identifier backgroundTexture;

    private final SimpleLinearAnimation hoverAnim = new SimpleLinearAnimation(180);

    public ButtonComponent(String text, Runnable onClick) {
        this(text, null, onClick);
    }

    public ButtonComponent(String text, Identifier backgroundTexture, Runnable onClick) {
        this.text = text;
        this.backgroundTexture = backgroundTexture;
        this.onClick = onClick;
    }

    public void setBounds(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void render(Renderer2D renderer, FontObject font, float mouseX, float mouseY) {
        boolean hovered = isHovered(mouseX, mouseY);
        if (hovered) {
            hoverAnim.show();
        } else {
            hoverAnim.hide();
        }

        float progress = hoverAnim.getProgress();
        int accent = ClientColors.ICON.getRGB();
        int backgroundColor = interpolateColor(rgba(255, 255, 255, 8), withAlpha(accent, 18), progress);
        int outlineColor = interpolateColor(rgba(255, 255, 255, 14), withAlpha(accent, 34), progress);
        int textColor = interpolateColor(rgba(235, 235, 240, 230), withAlpha(accent, 255), progress);

        if (!GpuCompatibility.isAmd()) {
            renderer.blur(x, y, w, h, 8f, 0.75f);
        }
        if (backgroundTexture != null) {
            // The source images are 16:9 while menu buttons are very wide.
            // Crop the centre rather than squeezing a full landscape into a short button.
            float buttonAspectRatio = w / h;
            float u0 = 0f;
            float v0 = 0f;
            float u1 = 1f;
            float v1 = 1f;
            if (buttonAspectRatio > BUTTON_BACKGROUND_ASPECT_RATIO) {
                float visibleHeight = BUTTON_BACKGROUND_ASPECT_RATIO / buttonAspectRatio;
                v0 = (1f - visibleHeight) * 0.5f;
                v1 = v0 + visibleHeight;
            } else {
                float visibleWidth = buttonAspectRatio / BUTTON_BACKGROUND_ASPECT_RATIO;
                u0 = (1f - visibleWidth) * 0.5f;
                u1 = u0 + visibleWidth;
            }
            renderer.drawTextureRegionRounded(backgroundTexture, x, y, w, h, u0, v0, u1, v1, 0xFFFFFFFF, 8f, false);
            renderer.rect(x, y, w, h, 8f, rgba(0, 0, 0, 88));
        }
        renderer.rect(x, y, w, h, 8f, backgroundColor);
        renderer.rectOutline(x, y, w, h, 8f, outlineColor, 1f);

        float fontSize = h * 0.34f;
        float baseline = y + h * 0.5f + FontRegistry.centeredBaselineOffset(font, 'H', fontSize);
        int textAlpha = (textColor >>> 24) & 0xFF;
        int shadowColor = withAlpha(0x000000, Math.round(textAlpha * 0.6f));
        renderer.centredText(font, x + w * 0.5f + 1.2f, baseline + 1.4f, fontSize, text, shadowColor);
        renderer.centredText(font, x + w * 0.5f, baseline, fontSize, text, textColor);
    }

    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    private int interpolateColor(int from, int to, float factor) {
        int a1 = (from >>> 24) & 0xFF;
        int r1 = (from >>> 16) & 0xFF;
        int g1 = (from >>> 8) & 0xFF;
        int b1 = from & 0xFF;

        int a2 = (to >>> 24) & 0xFF;
        int r2 = (to >>> 16) & 0xFF;
        int g2 = (to >>> 8) & 0xFF;
        int b2 = to & 0xFF;

        return rgba(
                lerp(r1, r2, factor),
                lerp(g1, g2, factor),
                lerp(b1, b2, factor),
                lerp(a1, a2, factor)
        );
    }

    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int withAlpha(int rgb, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (rgb & 0x00FFFFFF);
    }

    private static int lerp(int from, int to, float t) {
        return (int) (from + (to - from) * t);
    }
}
