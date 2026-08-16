package never.lose.ui.component.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import never.lose.modules.api.setting.StringSetting;
import never.lose.ui.component.Component;
import never.lose.utils.font.Fonts;
import never.lose.utils.input.CursorManager;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.Scissor;
import never.lose.utils.render.providers.ColorProvider;

public class StringComponent extends Component {

    private static final float NAME_H = 10f;
    private static final float FIELD_H = 12f;
    private static final float PADDING = 4.5f;
    private static final float RADIUS = 1.5f;
    private static final float FONT = 7f;

    private final StringSetting setting;
    private boolean focused;
    private int caret;
    private int selectionAnchor;
    private float scrollX;

    public StringComponent(StringSetting setting) {
        this.setting = setting;
        this.caret = setting.getValue().length();
        this.selectionAnchor = this.caret;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (!focused) {
            selectionAnchor = caret;
        }
    }

    private String value() {
        return setting.getValue();
    }

    private float fieldX() {
        return x + PADDING;
    }

    private float fieldY() {
        return y + NAME_H;
    }

    private float fieldW() {
        return width - PADDING * 2f;
    }

    private float textX() {
        return fieldX() + 3f;
    }

    private float textW() {
        return Math.max(1f, fieldW() - 6f);
    }

    private float widthOf(String text) {
        return Draw.width(Fonts.REGULAR, text, FONT);
    }

    private boolean hasSelection() {
        return caret != selectionAnchor;
    }

    private int selMin() {
        return Math.min(caret, selectionAnchor);
    }

    private int selMax() {
        return Math.max(caret, selectionAnchor);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float alpha = Math.max(Math.min(getAlphaAnimSetting().getValue(), 1), 0)
                * Math.max(Math.min(getAlphaAnim().getValue(), 1), 0);
        int alphaInt = (int) (255 * alpha);
        setHeight(NAME_H + FIELD_H + 2f);

        if (alpha < 0.02f) {
            return;
        }

        float fx = fieldX();
        float fy = fieldY();
        float fw = fieldW();
        boolean hovered = Hover.isHovered(mouseX, mouseY, fx, fy, fw, FIELD_H);
        if (hovered) {
            CursorManager.requestIBeam();
        }

        Draw.text(Fonts.REGULAR, setting.getName(), x + PADDING, y + 1.5f,
                ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt), 7.5f);

        int border = focused
                ? ColorProvider.setAlpha(ColorProvider.getColorClient(), (int) (160 * alpha))
                : ColorProvider.rgba(48, 66, 122, (int) (90 * alpha));
        Draw.round(fx - 0.5f, fy - 0.5f, fw + 1f, FIELD_H + 1f, RADIUS + 0.5f, border);
        Draw.round(fx, fy, fw, FIELD_H, RADIUS,
                ColorProvider.setAlpha(ColorProvider.getColorInactiveButton(), (int) (140 * alpha)));

        String text = value();
        float tx = textX();
        float tw = textW();
        float textY = fy + (FIELD_H - FONT) / 2f + 0.2f;
        updateScroll(tw);

        Scissor.push();
        Scissor.setFromComponentCoordinates(tx, fy, tw, FIELD_H);

        if (text.isEmpty() && !focused) {
            Draw.text(Fonts.REGULAR, "...", tx, textY,
                    ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), alphaInt), FONT);
        } else {
            if (hasSelection()) {
                float selX1 = tx - scrollX + widthOf(text.substring(0, selMin()));
                float selX2 = tx - scrollX + widthOf(text.substring(0, selMax()));
                Draw.round(selX1, fy + 2f, Math.max(1f, selX2 - selX1), FIELD_H - 4f, 1f,
                        ColorProvider.setAlpha(ColorProvider.getColorClient(), (int) (90 * alpha)));
            }

            Draw.text(Fonts.REGULAR, text, tx - scrollX, textY,
                    ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt), FONT);

            if (focused && System.currentTimeMillis() % 1000 > 500) {
                int safeCaret = Math.max(0, Math.min(caret, text.length()));
                float caretX = tx - scrollX + widthOf(text.substring(0, safeCaret));
                Draw.round(caretX, fy + 2f, 0.8f, FIELD_H - 4f, 0f,
                        ColorProvider.setAlpha(ColorProvider.getColorText(), (int) (230 * alpha)));
            }
        }

        Scissor.unset();
        Scissor.pop();
    }

    private void updateScroll(float areaW) {
        String text = value();
        int safeCaret = Math.max(0, Math.min(caret, text.length()));
        float caretPos = widthOf(text.substring(0, safeCaret));
        if (caretPos - scrollX > areaW - 2f) {
            scrollX = caretPos - areaW + 2f;
        }
        if (caretPos - scrollX < 0f) {
            scrollX = caretPos;
        }
        float total = widthOf(text);
        if (total - scrollX < areaW - 2f) {
            scrollX = Math.max(0f, total - areaW + 2f);
        }
        if (total <= areaW) {
            scrollX = 0f;
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        boolean inside = Hover.isHovered(mouseX, mouseY, fieldX(), fieldY(), fieldW(), FIELD_H);
        focused = inside && button == 0;
        if (inside && button == 0) {
            int index = caretFromMouse((float) mouseX);
            caret = index;
            selectionAnchor = index;
        }
    }

    private int caretFromMouse(float mouseX) {
        String text = value();
        float local = mouseX - textX() + scrollX;
        int best = 0;
        float bestDist = Float.MAX_VALUE;
        for (int i = 0; i <= text.length(); i++) {
            float w = widthOf(text.substring(0, i));
            float d = Math.abs(w - local);
            if (d < bestDist) {
                bestDist = d;
                best = i;
            }
        }
        return best;
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        if (!focused) {
            return;
        }
        if ((modifiers & (GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT | GLFW.GLFW_MOD_SUPER)) != 0) {
            return;
        }
        if (chr < 32 || chr == 127) {
            return;
        }
        insert(String.valueOf(chr));
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) {
            return;
        }

        boolean ctrl = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0 || (modifiers & GLFW.GLFW_MOD_SUPER) != 0;
        boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;

        if (ctrl) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_A -> {
                    selectAll();
                    return;
                }
                case GLFW.GLFW_KEY_C -> {
                    copySelection();
                    return;
                }
                case GLFW.GLFW_KEY_X -> {
                    cutSelection();
                    return;
                }
                case GLFW.GLFW_KEY_V -> {
                    paste();
                    return;
                }
                default -> {
                }
            }
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (hasSelection()) {
                    deleteSelection();
                } else if (ctrl) {
                    int start = prevWordBoundary(caret);
                    replaceRange(start, caret, "");
                    caret = start;
                    selectionAnchor = caret;
                } else if (caret > 0) {
                    replaceRange(caret - 1, caret, "");
                    caret--;
                    selectionAnchor = caret;
                }
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (hasSelection()) {
                    deleteSelection();
                } else if (caret < value().length()) {
                    replaceRange(caret, caret + 1, "");
                }
            }
            case GLFW.GLFW_KEY_LEFT -> {
                int target = ctrl ? prevWordBoundary(caret) : Math.max(0, caret - 1);
                moveCaret(target, shift);
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                int target = ctrl ? nextWordBoundary(caret) : Math.min(value().length(), caret + 1);
                moveCaret(target, shift);
            }
            case GLFW.GLFW_KEY_HOME -> moveCaret(0, shift);
            case GLFW.GLFW_KEY_END -> moveCaret(value().length(), shift);
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_ESCAPE -> focused = false;
            default -> {
            }
        }
    }

    private void insert(String raw) {
        if (hasSelection()) {
            deleteSelection();
        }
        String text = value();
        int free = setting.getMaxLength() - text.length();
        if (free <= 0 || raw.isEmpty()) {
            return;
        }
        if (raw.length() > free) {
            raw = raw.substring(0, free);
        }
        replaceRange(caret, caret, raw);
        caret += raw.length();
        selectionAnchor = caret;
    }

    private void replaceRange(int start, int end, String insert) {
        String text = value();
        int from = Math.max(0, Math.min(start, text.length()));
        int to = Math.max(from, Math.min(end, text.length()));
        setting.setValue(text.substring(0, from) + insert + text.substring(to));
    }

    private void deleteSelection() {
        int a = selMin();
        int b = selMax();
        replaceRange(a, b, "");
        caret = a;
        selectionAnchor = a;
    }

    private void moveCaret(int target, boolean keepSelection) {
        caret = Math.max(0, Math.min(value().length(), target));
        if (!keepSelection) {
            selectionAnchor = caret;
        }
    }

    private void selectAll() {
        selectionAnchor = 0;
        caret = value().length();
    }

    private void copySelection() {
        if (!hasSelection()) {
            return;
        }
        MinecraftClient.getInstance().keyboard.setClipboard(value().substring(selMin(), selMax()));
    }

    private void cutSelection() {
        if (!hasSelection()) {
            return;
        }
        copySelection();
        deleteSelection();
    }

    private void paste() {
        String clip = MinecraftClient.getInstance().keyboard.getClipboard();
        if (clip == null || clip.isEmpty()) {
            return;
        }
        insert(clip.replace('\n', ' ').replace('\r', ' ').replace('\t', ' '));
    }

    private int prevWordBoundary(int from) {
        String text = value();
        int i = from;
        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }
        while (i > 0 && text.charAt(i - 1) != ' ') {
            i--;
        }
        return i;
    }

    private int nextWordBoundary(int from) {
        String text = value();
        int i = from;
        int len = text.length();
        while (i < len && text.charAt(i) == ' ') {
            i++;
        }
        while (i < len && text.charAt(i) != ' ') {
            i++;
        }
        return i;
    }

    @Override
    public boolean isVisible() {
        boolean visible = setting.visible.get();
        if (!visible) {
            focused = false;
        }
        return visible;
    }
}
