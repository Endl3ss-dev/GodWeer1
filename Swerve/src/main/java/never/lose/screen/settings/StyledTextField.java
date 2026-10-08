package never.lose.screen.settings;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import never.lose.ui.object.RenderObject;
import never.lose.utils.render.Render;
import never.lose.managers.ThemeManager;
import never.lose.utils.math.MathUtil;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.function.Predicate;

public class StyledTextField extends RenderObject {

    public static final Predicate<Character> USERNAME_FILTER = c ->
            (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';

    private static final String FONT = "sfmedium";
    private static final float TEXT_SIZE = 8f;

    private static final Color BG_DEFAULT        = new Color(255, 255, 255, 8);
    private static final Color BG_FOCUSED        = new Color(255, 255, 255, 18);
    private static final Color STR_DEFAULT       = new Color(255, 255, 255, 15);
    private static final Color STR_FOCUSED       = new Color(255, 255, 255, 45);
    private static final Color ICON_DEFAULT      = new Color(255, 255, 255, 70);
    private static final Color ICON_FOCUSED      = new Color(255, 255, 255, 180);
    private static final Color TEXT_COLOR        = new Color(230, 230, 230, 255);
    private static final Color PLACEHOLDER_COLOR = new Color(255, 255, 255, 55);
    private static final Color CURSOR_COLOR      = new Color(255, 255, 255, 190);

    private final StringBuilder text = new StringBuilder();
    private final String icon;
    private final String placeholder;
    private final int maxLength;

    private int cursor = 0;
    private boolean focused = false;
    private boolean visible = true;
    private Predicate<Character> charFilter = c -> true;
    private Runnable onSubmit;
    private float focusAnim = 0f;
    private float viewOffset = 0f;

    private StyledTextField(String icon, String placeholder, int maxLength) {
        this.icon = icon;
        this.placeholder = placeholder;
        this.maxLength = maxLength;
    }

    public static StyledTextField build(float x, float y, float w, float h,
                                        String icon, String placeholder, int maxLength) {
        StyledTextField stf = new StyledTextField(icon, placeholder, maxLength);
        stf.x = x;
        stf.y = y;
        stf.width = w;
        stf.height = h;
        return stf;
    }

    public void setCharFilter(Predicate<Character> filter) {
        this.charFilter = filter != null ? filter : c -> true;
    }

    public void setOnSubmit(Runnable onSubmit) {
        this.onSubmit = onSubmit;
    }

    private float contentX() {
        return x + (icon != null && !icon.isEmpty() ? 22f : 6f);
    }

    private float contentW() {
        return x + width - 6f - contentX();
    }

    @Override
    public void render(int mouseX, int mouseY) {
        if (!visible) return;

        focusAnim = MathUtil.fastAnim(focusAnim, focused ? 1f : 0f, 10f);

        Render.blur(x, y, width, height, 6, 6, 6, 6, 1, 16,
                Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);

        Color bg = ThemeManager.interpolateColor(BG_DEFAULT, BG_FOCUSED, focusAnim);
        Color stroke = ThemeManager.interpolateColor(STR_DEFAULT, STR_FOCUSED, focusAnim);
        Render.outline(x, y, width, height, 6, 6, 6, 6, 0.6f, 0f,
                bg, bg, bg, bg,
                stroke, stroke, stroke, stroke);

        if (icon != null && !icon.isEmpty()) {
            Color iconColor = ThemeManager.interpolateColor(ICON_DEFAULT, ICON_FOCUSED, focusAnim);
            Render.textCentered(icon, x + 13, y + height / 2f - 4.5f, "hach", 9, iconColor);
        }

        drawText();
    }

    private void drawText() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Window window = mc.getWindow();
        double scale = window.getScaleFactor();

        float cx = contentX();
        float cw = contentW();
        float textY = y + height / 2f - TEXT_SIZE / 2f + 0.5f;

        int scX = (int) (cx * scale);
        int scY = (int) (window.getFramebufferHeight() - (y + height - 2f) * scale);
        int scW = (int) (cw * scale);
        int scH = (int) ((height - 4f) * scale);

        RenderSystem.enableScissor(scX, scY, scW, scH);

        if (text.length() == 0 && !focused) {
            Render.text(placeholder, cx, textY, FONT, TEXT_SIZE, PLACEHOLDER_COLOR);
        } else {
            Render.text(text.toString(), cx - viewOffset, textY, FONT, TEXT_SIZE, TEXT_COLOR);

            if (focused && (System.currentTimeMillis() / 500L) % 2L == 0L) {
                float caretX = cx + Render.textWidth(text.substring(0, cursor), FONT, TEXT_SIZE) - viewOffset;
                Render.round(caretX, y + 5f, 1f, height - 10f, 0, 0, 0, 0, 0f,
                        CURSOR_COLOR, CURSOR_COLOR, CURSOR_COLOR, CURSOR_COLOR);
            }
        }

        RenderSystem.disableScissor();
    }

    @Override
    public boolean click(int mouseX, int mouseY, int button) {
        if (!visible) return false;
        focused = MathUtil.isInPosition(x, y, width, height, mouseX, mouseY);
        return focused;
    }

    @Override
    public void charTyped(char ch, int modifiers) {
        if (!focused || !visible) return;
        if (ch < 32) return;
        if (!charFilter.test(ch)) return;
        if (text.length() >= maxLength) return;

        text.insert(cursor, ch);
        cursor++;
        ensureCaretVisible();
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused || !visible) return;

        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (cursor > 0) {
                    text.deleteCharAt(cursor - 1);
                    cursor--;
                }
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (cursor < text.length()) text.deleteCharAt(cursor);
            }
            case GLFW.GLFW_KEY_LEFT  -> { if (cursor > 0) cursor--; }
            case GLFW.GLFW_KEY_RIGHT -> { if (cursor < text.length()) cursor++; }
            case GLFW.GLFW_KEY_HOME  -> cursor = 0;
            case GLFW.GLFW_KEY_END   -> cursor = text.length();
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                if (onSubmit != null) onSubmit.run();
                else focused = false;
            }
            case GLFW.GLFW_KEY_V -> {
                if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) paste();
            }
            default -> { }
        }
        ensureCaretVisible();
    }

    private void paste() {
        String clip = MinecraftClient.getInstance().keyboard.getClipboard();
        if (clip == null || clip.isEmpty()) return;

        for (int i = 0; i < clip.length() && text.length() < maxLength; i++) {
            char ch = clip.charAt(i);
            if (ch >= 32 && charFilter.test(ch)) {
                text.insert(cursor, ch);
                cursor++;
            }
        }
    }

    private void ensureCaretVisible() {
        float cw = contentW();
        float caretX = Render.textWidth(text.substring(0, cursor), FONT, TEXT_SIZE);

        if (caretX - viewOffset > cw) {
            viewOffset = caretX - cw;
        } else if (caretX < viewOffset) {
            viewOffset = caretX;
        }
        if (viewOffset < 0f) viewOffset = 0f;
    }

    public String getText() {
        return text.toString();
    }

    public void setText(String value) {
        text.setLength(0);
        if (value != null) {
            for (int i = 0; i < value.length() && text.length() < maxLength; i++) {
                char ch = value.charAt(i);
                if (charFilter.test(ch)) text.append(ch);
            }
        }
        cursor = text.length();
        viewOffset = 0f;
        ensureCaretVisible();
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) ensureCaretVisible();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (!visible) focused = false;
    }

    public boolean isVisible() {
        return visible;
    }
}