package net.skycore.client.gui.workspace;

import net.skycore.client.gui.menu.MenuDraw;
import net.skycore.client.gui.menu.MenuText.Weight;
import net.skycore.client.setting.ColorSetting;
import net.skycore.client.util.SelectAndSaveHelper;

import java.awt.Color;
import java.util.Locale;

/**
 * Выбор цвета для панели Workspace.
 *
 * <p>Источник анатомии — {@code Phobia/system/screensystem/clickgui/settingcomponent/ColorComponent}
 * и {@code Arcanic/gui/ColourPicker}: квадрат насыщенность/яркость, полоса тона,
 * полоса прозрачности, поле HEX и набор готовых цветов. В отличие от Arcanic здесь нет
 * пипетки по скриншоту и RGB-режима: и то и другое требует чтения кадра
 * ({@code Screenshot}) и в 26.2 проверяется отдельно, а перенос без проверки дал бы
 * кнопку, которая молча ничего не делает. Всё остальное поведение источника сохранено.
 *
 * <p>Квадрат рисуется тремя заливками без шейдеров и текстур: чистый тон, поверх —
 * горизонтальный градиент в белый (насыщенность), затем вертикальный в чёрный
 * (яркость). Это ровно те же цвета, что даёт {@link Color#HSBtoRGB} в узлах сетки,
 * поэтому курсор всегда попадает в отображаемый цвет.
 */
final class WorkspaceColorPicker {

    static final int HIT_SV = 20;
    static final int HIT_HUE = 21;
    static final int HIT_ALPHA = 22;
    static final int HIT_HEX = 23;
    static final int HIT_CLOSE = 24;
    static final int HIT_PRESET = 25;

    /** Готовые цвета: нейтральные и типовые акценты клиента. */
    private static final int[] PRESETS = {
            0xFFFFFFFF, 0xFF111318, 0xFF8B5CF6, 0xFFC084FC, 0xFFF472B6, 0xFFF43F5E,
            0xFFFB923C, 0xFFFACC15, 0xFF4ADE80, 0xFF22D3EE, 0xFF60A5FA, 0xFF94A3B8,
    };

    private ColorSetting setting;
    private float hue;
    private float saturation;
    private float value;
    private float alpha = 1.0F;
    private boolean hexFocused;
    private String hex = "";
    private float x;
    private float y;
    private float w;
    private float h;

    boolean isOpen() {
        return this.setting != null;
    }

    ColorSetting setting() {
        return this.setting;
    }

    float x() {
        return this.x;
    }

    float y() {
        return this.y;
    }

    float width() {
        return this.w;
    }

    float height() {
        return this.h;
    }

    boolean hexFocused() {
        return this.hexFocused;
    }

    String hexText() {
        return this.hex;
    }

    /** Открывает выбор цвета рядом с элементом, из которого пришёл клик. */
    void open(WorkspaceLayout l, ColorSetting color, float anchorX, float anchorY) {
        this.setting = color;
        this.hexFocused = false;
        this.w = l.s(190.0F);
        this.h = l.s(150.0F);
        this.x = anchorX;
        this.y = anchorY;
        this.x = Math.max(l.px + l.s(6.0F), Math.min(l.px + l.pw - this.w - l.s(6.0F), this.x));
        this.y = Math.max(l.py + l.s(6.0F), Math.min(l.py + l.ph - this.h - l.s(6.0F), this.y));
        int argb = color.get() == null ? 0xFFFFFFFF : color.get();
        float[] hsb = Color.RGBtoHSB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.value = hsb[2];
        this.alpha = ((argb >>> 24) & 0xFF) / 255.0F;
        this.hex = String.format(Locale.ROOT, "%08X", argb);
    }

    void close() {
        this.setting = null;
        this.hexFocused = false;
        this.hex = "";
    }

    /** Открыт и точка внутри поповера (клик по нему не должен закрывать его). */
    boolean contains(double mouseX, double mouseY) {
        return this.setting != null
                && mouseX >= this.x && mouseX <= this.x + this.w
                && mouseY >= this.y && mouseY <= this.y + this.h;
    }

    // ---- взаимодействие

    /** Двигает насыщенность и яркость по квадрату. */
    void dragSv(WorkspaceLayout l, double mouseX, double mouseY) {
        if (this.setting == null) {
            return;
        }
        float left = this.x + l.s(10.0F);
        float top = this.y + l.s(28.0F);
        float size = l.s(70.0F);
        this.saturation = clamp01((float) ((mouseX - left) / size));
        this.value = 1.0F - clamp01((float) ((mouseY - top) / size));
        apply();
    }

    /** Двигает тон по полосе. */
    void dragHue(WorkspaceLayout l, double mouseX) {
        if (this.setting == null) {
            return;
        }
        float left = this.x + l.s(10.0F);
        float size = l.s(70.0F);
        this.hue = clamp01((float) ((mouseX - left) / size));
        apply();
    }

    /** Двигает прозрачность по полосе. */
    void dragAlpha(WorkspaceLayout l, double mouseX) {
        if (this.setting == null) {
            return;
        }
        float left = this.x + l.s(10.0F);
        float size = l.s(70.0F);
        this.alpha = clamp01((float) ((mouseX - left) / size));
        apply();
    }

    void pickPreset(int index) {
        if (this.setting == null || index < 0 || index >= PRESETS.length) {
            return;
        }
        int argb = PRESETS[index];
        this.setting.set(argb);
        float[] hsb = Color.RGBtoHSB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.value = hsb[2];
        this.alpha = ((argb >>> 24) & 0xFF) / 255.0F;
        this.hex = String.format(Locale.ROOT, "%08X", argb);
    }

    void focusHex() {
        this.hexFocused = true;
        if (this.hex.isEmpty()) {
            this.hex = String.format(Locale.ROOT, "%08X", current());
        }
    }

    void blurHex() {
        this.hexFocused = false;
    }

    void typeHex(char character) {
        if (!this.hexFocused) {
            return;
        }
        int digit = Character.digit(character, 16);
        if (digit < 0) {
            return;
        }
        if (this.hex.length() >= 8) {
            return;
        }
        this.hex = this.hex + Character.toUpperCase(character);
        commitHexIfComplete();
    }

    void backspaceHex() {
        if (!this.hexFocused || this.hex.isEmpty()) {
            return;
        }
        this.hex = this.hex.substring(0, this.hex.length() - 1);
        commitHexIfComplete();
    }

    /** Применяет введённый HEX, если он уже полный (6 или 8 цифр). */
    void commitHex() {
        if (this.setting == null) {
            return;
        }
        int length = this.hex.length();
        if (length != 6 && length != 8) {
            return;
        }
        try {
            long parsed = Long.parseLong(this.hex, 16);
            int argb = (int) (length == 6 ? (parsed | 0xFF000000L) : parsed);
            this.setting.set(argb);
            float[] hsb = Color.RGBtoHSB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, null);
            this.hue = hsb[0];
            this.saturation = hsb[1];
            this.value = hsb[2];
            this.alpha = ((argb >>> 24) & 0xFF) / 255.0F;
        } catch (NumberFormatException ignored) {
            this.hex = String.format(Locale.ROOT, "%08X", current());
        }
    }

    private void commitHexIfComplete() {
        if (this.hex.length() == 8) {
            commitHex();
        }
    }

    // ---- отрисовка

    void render(WorkspaceLayout l, WorkspaceTheme t, WorkspaceSettings.Sink sink, boolean live) {
        if (this.setting == null) {
            return;
        }
        float radius = l.s(10.0F);
        MenuDraw.shadow(this.x, this.y + l.s(6.0F), this.w, this.h, radius, l.s(20.0F), t.shadow);
        MenuDraw.round(this.x, this.y, this.w, this.h, radius, t.light ? 0xFFFFFFFF : MenuDraw.a(t.window, 252));
        MenuDraw.outline(this.x, this.y, this.w, this.h, radius, t.border);

        float font = l.s(9.5F);
        MenuDraw.textMid(SelectAndSaveHelper.text(this.setting.getName()), this.x + l.s(10.0F), this.y + l.s(6.0F),
                l.s(16.0F), Weight.SEMIBOLD, font, t.text);
        WorkspaceDraw.letterIcon(net.skycore.client.util.LegacyIconGlyphs.TRASH,
                this.x + this.w - l.s(14.0F),
                this.y + l.s(14.0F), l.s(10.0F), t.muted);
        if (live) {
            sink.hit(HIT_CLOSE, 0, 0, this.x + this.w - l.s(24.0F), this.y + l.s(4.0F), l.s(20.0F), l.s(18.0F));
        }

        // квадрат насыщенности и яркости
        float left = this.x + l.s(10.0F);
        float top = this.y + l.s(28.0F);
        float size = l.s(70.0F);
        int pure = Color.HSBtoRGB(this.hue, 1.0F, 1.0F) | 0xFF000000;
        MenuDraw.round(left, top, size, size, l.s(5.0F), pure);
        MenuDraw.hgrad(left, top, size, size, 0xFFFFFFFF, 0x00FFFFFF);
        MenuDraw.vgrad(left, top, size, size, 0x00000000, 0xFF000000);
        MenuDraw.outline(left, top, size, size, l.s(5.0F), MenuDraw.a(t.text, 50));
        float cursorX = left + size * this.saturation;
        float cursorY = top + size * (1.0F - this.value);
        MenuDraw.ring(cursorX, cursorY, l.s(4.0F), l.s(1.5F), 0xFF000000);
        MenuDraw.ring(cursorX, cursorY, l.s(3.0F), l.s(1.0F), 0xFFFFFFFF);
        if (live) {
            sink.hit(HIT_SV, 0, 0, left, top, size, size);
        }

        // полоса тона
        float barX = left + size + l.s(10.0F);
        float barW = this.x + this.w - l.s(10.0F) - barX;
        float barY = top;
        float barH = l.s(9.0F);
        int segments = 6;
        for (int i = 0; i < segments; i++) {
            float from = i / (float) segments;
            float to = (i + 1) / (float) segments;
            MenuDraw.hgrad(barX, barY + i * barH, barW, barH,
                    Color.HSBtoRGB(from, 1.0F, 1.0F) | 0xFF000000,
                    Color.HSBtoRGB(to, 1.0F, 1.0F) | 0xFF000000);
        }
        MenuDraw.outline(barX, barY, barW, barH * segments, l.s(4.0F), MenuDraw.a(t.text, 50));
        float hueY = barY + barH * segments * this.hue;
        MenuDraw.rect(barX, hueY - l.s(1.0F), barW, Math.max(2.0F, l.s(2.0F)), 0xFFFFFFFF);
        if (live) {
            sink.hit(HIT_HUE, 0, 0, barX - l.s(3.0F), barY, barW + l.s(6.0F), barH * segments);
        }

        // полоса прозрачности
        float alphaY = barY + barH * segments + l.s(8.0F);
        float alphaH = l.s(9.0F);
        int checker = 8;
        for (int i = 0; i < checker; i++) {
            MenuDraw.rect(barX + barW * i / checker, alphaY, barW / checker + 0.5F, alphaH,
                    (i % 2 == 0) ? MenuDraw.a(0xFFFFFF, 40) : MenuDraw.a(0x000000, 60));
        }
        int opaque = (current() & 0x00FFFFFF) | 0xFF000000;
        MenuDraw.hgrad(barX, alphaY, barW, alphaH, opaque & 0x00FFFFFF, opaque);
        MenuDraw.outline(barX, alphaY, barW, alphaH, l.s(4.0F), MenuDraw.a(t.text, 50));
        MenuDraw.rect(barX + barW * this.alpha - l.s(1.0F), alphaY - l.s(1.0F), Math.max(2.0F, l.s(2.0F)),
                alphaH + l.s(2.0F), 0xFFFFFFFF);
        if (live) {
            sink.hit(HIT_ALPHA, 0, 0, barX - l.s(3.0F), alphaY - l.s(3.0F), barW + l.s(6.0F), alphaH + l.s(6.0F));
        }

        // поле HEX
        float hexY = alphaY + alphaH + l.s(8.0F);
        float hexH = l.s(18.0F);
        float hexW = l.s(70.0F);
        MenuDraw.round(left, hexY, hexW, hexH, l.s(5.0F), this.hexFocused ? t.insetHover : t.inset);
        MenuDraw.outline(left, hexY, hexW, hexH, l.s(5.0F),
                this.hexFocused ? MenuDraw.a(t.accent, 170) : t.border);
        String shown = this.hex.isEmpty() ? String.format(Locale.ROOT, "%08X", current()) : this.hex;
        MenuDraw.textMid("#" + shown, left + l.s(6.0F), hexY, hexH, Weight.MEDIUM, l.s(9.0F), t.text);
        if (live) {
            sink.hit(HIT_HEX, 0, 0, left, hexY, hexW, hexH);
        }

        // готовые цвета
        float presetSize = l.s(12.0F);
        float presetY = hexY + (hexH - presetSize) * 0.5F;
        float presetX = left + hexW + l.s(8.0F);
        for (int i = 0; i < PRESETS.length; i++) {
            float px = presetX + (i % 6) * (presetSize + l.s(3.0F));
            float py = presetY + (i / 6) * (presetSize + l.s(3.0F));
            if (px + presetSize > this.x + this.w - l.s(8.0F)) {
                break;
            }
            MenuDraw.round(px, py, presetSize, presetSize, l.s(3.0F), PRESETS[i]);
            MenuDraw.outline(px, py, presetSize, presetSize, l.s(3.0F), MenuDraw.a(t.text, 45));
            if (live) {
                sink.hit(HIT_PRESET, i, 0, px, py, presetSize, presetSize);
            }
        }
    }

    /** Текущий выбранный цвет (ARGB). */
    int current() {
        int rgb = Color.HSBtoRGB(this.hue, this.saturation, this.value);
        int a = Math.round(Math.max(0.0F, Math.min(1.0F, this.alpha)) * 255.0F);
        return (a << 24) | (rgb & 0x00FFFFFF);
    }

    private void apply() {
        if (this.setting == null) {
            return;
        }
        int argb = current();
        this.setting.set(argb);
        this.hex = String.format(Locale.ROOT, "%08X", argb);
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
