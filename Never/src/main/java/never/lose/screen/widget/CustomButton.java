package never.lose.screen.widget;

import never.lose.ui.object.RenderObject;
import never.lose.utils.render.Render;
import never.lose.managers.ThemeManager;
import never.lose.utils.color.ClientColors;
import never.lose.utils.math.MathUtil;

import java.awt.Color;

public class CustomButton extends RenderObject {

    public enum ButtonType { MAIN, ALT, RED }

    private static final Color MAIN_HOVER_FILL = new Color(0xB0BEC5); // #B0BEC5
    private static final Color MAIN_HOVER_TEXT = new Color(0xECEFF1); // #ECEFF1
    private static final Color RED_HOVER_FILL  = new Color(255, 63, 63);
    private static final Color ALT_HOVER_FILL  = new Color(13, 54, 148);

    private final Runnable   onPress;
    private final String     text;
    private final String     icon;
    private final Color      iconColor;
    private final Color      textColor;
    private final ButtonType type;
    private final Color      hoverFill;
    private final Color      hoverStroke;
    private final Color      hoverText;
    private       float      anim = 0f;
    private       float      opacity = 1f;

    private CustomButton(Runnable onPress, String icon, String text,
                         Color iconColor, Color textColor, ButtonType type) {
        this.onPress   = onPress;
        this.icon      = icon;
        this.text      = text;
        this.iconColor = iconColor;
        this.textColor = textColor;
        this.type      = type;

        Color fill;
        Color textCol;
        switch (type) {
            case MAIN -> {
                fill    = MAIN_HOVER_FILL;
                textCol = MAIN_HOVER_TEXT;
            }
            case RED -> {
                fill    = RED_HOVER_FILL;
                textCol = Color.WHITE;
            }
            case ALT -> {
                fill    = ALT_HOVER_FILL;
                textCol = Color.WHITE;
            }
            default -> {
                fill    = Color.WHITE;
                textCol = Color.WHITE;
            }
        }
        this.hoverFill   = Render.injectAlpha(fill, 90);
        this.hoverStroke = Render.injectAlpha(fill, 180);
        this.hoverText   = Render.injectAlpha(textCol, 255);
    }

    public static CustomButton build(float x, float y, float w, float h,
                                     String icon, String text,
                                     Color iconColor, Color textColor,
                                     ButtonType type, Runnable onPress) {
        CustomButton btn = new CustomButton(onPress, icon, text, iconColor, textColor, type);
        btn.x      = x;
        btn.y      = y;
        btn.width  = w;
        btn.height = h;
        return btn;
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0f, Math.min(1f, opacity));
    }

    private Color faded(Color c) {
        return Render.injectAlpha(c, c.getAlpha() * opacity);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        boolean hovered = MathUtil.isInPosition(x, y, width, height, mouseX, mouseY);
        anim = MathUtil.fastAnim(anim, hovered ? 1f : 0f, 10f);

        Color fill   = faded(ThemeManager.interpolateColor(
                Render.injectAlpha(ClientColors.DISABLED, 4),
                hoverFill, anim));
        Color stroke = faded(ThemeManager.interpolateColor(
                Render.injectAlpha(ClientColors.DISABLED, 12),
                hoverStroke, anim));
        Color textCol = faded(ThemeManager.interpolateColor(
                Render.injectAlpha(ClientColors.FORE_COLOR, 120),
                hoverText, anim));

        Color blurColor = Render.injectAlpha(Color.WHITE, 255f * opacity);
        Render.blur(x, y, width, height, 8, 8, 8, 8, 1, 16,
                blurColor, blurColor, blurColor, blurColor);
        Render.outline(x, y, width, height, 8, 8, 8, 8, 0.6f, 0f,
                fill, fill, fill, fill,
                stroke, stroke, stroke, stroke);

        float iconX = x + width / 2f - 1f - Render.textWidth(text, "sfmedium", 9) / 2f - 3f;
        Render.textCentered(icon, iconX, y + height / 2f - 4.5f, "hach", 9, textCol);

        Render.textCentered(text, x + width / 2f + 4, y + height / 2f - 5.5f, "sfmedium", 9, textCol);
    }

    @Override
    public boolean click(int mouseX, int mouseY, int button) {
        // клик только на полностью видимой кнопке — при fade не ловит промахи
        if (onPress != null && opacity > 0.99f
                && MathUtil.isInPosition(x, y, width, height, mouseX, mouseY)) {
            onPress.run();
        }
        return false;
    }
}