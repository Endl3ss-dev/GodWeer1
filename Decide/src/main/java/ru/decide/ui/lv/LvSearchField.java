/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  org.lwjgl.glfw.GLFW
 */
package ru.decide.ui.lv;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_310;
import net.minecraft.class_332;
import org.lwjgl.glfw.GLFW;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.utils.other.GuiSounds;
import ru.decide.utils.render.Draw;
import ru.decide.utils.render.Scissor;

@Environment(value=EnvType.CLIENT)
public final class LvSearchField {
    private static final float SIZE = 7.0f;
    private String text = "";
    private String placeholder = "\u041f\u043e\u0438\u0441\u043a...";
    private String iconGlyph = "q";
    private boolean typing;
    private boolean dragging;
    private int cursorPosition;
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private long lastClickTime;
    private float xOffset;
    private float bx;
    private float by;
    private float bw;
    private float bh;
    private float textStartX;
    private float textVisibleW;
    private float hoverT;
    private float focusT;
    private float animCursorX;
    private boolean cursorSnap = true;
    private final List<Float> charAnim = new ArrayList<Float>();

    public String getText() {
        return this.text;
    }

    public void setText(String s) {
        this.text = s == null ? "" : s;
        this.cursorPosition = Math.min(this.cursorPosition, this.text.length());
        this.clearSelection();
        this.dragging = false;
        this.cursorSnap = true;
        this.charAnim.clear();
        for (int i = 0; i < this.text.length(); ++i) {
            this.charAnim.add(Float.valueOf(1.0f));
        }
    }

    public void render(class_332 ctx, float x, float y, float w, float h, float alpha, float mx, float my, float delta) {
        float cx;
        this.bx = x;
        this.by = y;
        this.bw = w;
        this.bh = h;
        this.cursorPosition = LvSearchField.clamp(this.cursorPosition, 0, this.text.length());
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
        float f13 = 1.0f - (float)Math.exp(-delta * 16.0f);
        float f14 = 1.0f - (float)Math.exp(-delta * 11.0f);
        this.hoverT += ((hover ? 1.0f : 0.0f) - this.hoverT) * f13;
        this.focusT += ((this.typing ? 1.0f : 0.0f) - this.focusT) * f14;
        float ht = Math.max(this.hoverT, this.focusT);
        int accentRGB = ThemeManager.accent(255.0f) & 0xFFFFFF;
        Draw.rect(x, y, w, h, LvSearchField.col(0, 0, 0, (40.0f + 12.0f * ht) * alpha), 4.0f);
        int n4 = LvSearchField.withAlpha(new Color(9, 9, 9, 0), alpha);
        int n5 = LvSearchField.withAlpha(new Color(9, 12, 14, 0), alpha);
        Draw.outline(x, y, w, h, 0.8f, n4);
        float iconSize = 7.5f;
        float iconX = x + 5.0f;
        if (this.iconGlyph.isEmpty()) {
            this.textStartX = x + 5.0f + 1.0f;
        } else {
            float iconY = y + (h - iconSize) * 0.5f + 0.3f;
            int mixC = LvSearchField.mixRgb(0xB4B4B4, accentRGB, ht);
            int iconAlpha = LvSearchField.clampA((125.0f + 95.0f * ht) * alpha);
            FontsLV.DECIDE.msdf(this.iconGlyph, iconX, iconY, iconSize, iconAlpha << 24 | mixC);
            this.textStartX = iconX + iconSize + 4.0f;
        }
        float rightEdge = x + w - 5.0f;
        this.textVisibleW = Math.max(4.0f, rightEdge - this.textStartX);
        this.updateXOffset();
        float textX = this.textStartX - this.xOffset;
        float textY = y + (h - 7.0f) * 0.5f - 0.5f;
        Scissor.enable(this.textStartX - 2.0f, y, this.textVisibleW + 2.0f, h, 2.0f);
        if (this.typing && this.hasSelection()) {
            int selStart = this.getStartOfSelection();
            int selEnd = this.getEndOfSelection();
            float sx = textX + FontsLV.MONTSERRAT_MEDIUM.width(this.text.substring(0, selStart), 7.0f);
            float ex = textX + FontsLV.MONTSERRAT_MEDIUM.width(this.text.substring(0, selEnd), 7.0f);
            Draw.rect(sx, y + 2.5f, ex - sx, h - 5.0f, LvSearchField.col(60, 128, 240, 165.0f * alpha), 1.5f);
        }
        float revealSpeed = delta * 3.6f;
        boolean anyRevealing = false;
        for (int i = 0; i < this.charAnim.size(); ++i) {
            float val = this.charAnim.get(i).floatValue();
            if (!(val < 1.0f)) continue;
            this.charAnim.set(i, Float.valueOf(Math.min(1.0f, val + revealSpeed)));
            anyRevealing = true;
        }
        if (!this.text.isEmpty()) {
            int textColor = LvSearchField.col(255, 255, 255, 235.0f * alpha);
            if (!anyRevealing) {
                FontsLV.MONTSERRAT_MEDIUM.draw(this.text, textX, textY, 7.0f, textColor);
            } else {
                float charCenter = textY + 3.5f;
                for (int i = 0; i < this.text.length(); ++i) {
                    float cp = LvSearchField.clamp01(this.charProgress(i));
                    if (cp <= 0.0f) continue;
                    cx = textX + FontsLV.MONTSERRAT_MEDIUM.width(this.text.substring(0, i), 7.0f);
                    String ch = String.valueOf(this.text.charAt(i));
                    int charA = LvSearchField.clampA(235.0f * alpha * cp);
                    if (charA <= 0) continue;
                    int charCol = charA << 24 | 0xFFFFFF;
                    if (!(cp >= 1.0f)) continue;
                    FontsLV.MONTSERRAT_MEDIUM.draw(ch, cx, textY, 7.0f, charCol);
                }
            }
        } else {
            float placeholderAlpha = 1.0f - this.focusT;
            if (placeholderAlpha > 0.01f && !this.placeholder.isEmpty()) {
                FontsLV.MONTSERRAT_MEDIUM.draw(this.placeholder, textX, textY, 7.0f, LvSearchField.col(255, 255, 255, 95.0f * alpha * placeholderAlpha));
            }
        }
        Scissor.disable();
        float cursorOffsetX = FontsLV.MONTSERRAT_MEDIUM.width(this.text.substring(0, this.cursorPosition), 7.0f);
        if (!this.cursorSnap && this.typing) {
            this.animCursorX += (cursorOffsetX - this.animCursorX) * (1.0f - (float)Math.exp(-delta * 20.0f));
        } else {
            this.animCursorX = cursorOffsetX;
            this.cursorSnap = false;
        }
        long now = System.currentTimeMillis();
        if (this.focusT > 0.01f && !this.hasSelection()) {
            float blink = (float)(Math.sin((double)now / 200.0) * 0.5 + 0.5);
            cx = textX + this.animCursorX;
            Draw.rect(cx, y + 2.5f, 0.6f, h - 5.0f, LvSearchField.col(255, 255, 255, (70.0f + 185.0f * blink) * this.focusT * alpha), 0.0f);
        }
        if (this.dragging) {
            int idx = this.cursorIndexAt(mx);
            if (this.selectionStart == -1) {
                this.selectionStart = this.cursorPosition;
            }
            this.selectionEnd = this.cursorPosition = idx;
            if (this.selectionStart == this.selectionEnd) {
                this.clearSelection();
            }
        }
    }

    public void blur() {
        this.typing = false;
        this.dragging = false;
        this.clearSelection();
    }

    public boolean hasText() {
        return !this.text.isEmpty();
    }

    public boolean isTyping() {
        return this.typing;
    }

    public boolean charTyped(class_11905 input) {
        if (!this.typing) {
            return false;
        }
        int cp = input.comp_4793();
        if (Character.isISOControl(cp)) {
            return false;
        }
        this.deleteSelectedText();
        this.replaceText(this.cursorPosition, this.cursorPosition, Character.toString((char)cp));
        GuiSounds.type();
        return true;
    }

    public boolean keyPressed(class_11908 input) {
        if (!this.typing) {
            return false;
        }
        int key = input.comp_4795();
        boolean ctrl = LvSearchField.ctrlDown();
        boolean shift = LvSearchField.shiftDown();
        if (ctrl) {
            switch (key) {
                case 65: {
                    this.selectAllText();
                    return true;
                }
                case 67: {
                    this.copyToClipboard();
                    return true;
                }
                case 86: {
                    this.pasteFromClipboard();
                    return true;
                }
                case 88: {
                    this.cutToClipboard();
                    return true;
                }
                case 259: {
                    this.deletePrevious(true);
                    return true;
                }
                case 261: {
                    this.deleteNext(true);
                    return true;
                }
                case 262: {
                    this.moveCursorTo(this.findNextWordBoundary(this.cursorPosition), shift);
                    return true;
                }
                case 263: {
                    this.moveCursorTo(this.findPreviousWordBoundary(this.cursorPosition), shift);
                    return true;
                }
            }
            return false;
        }
        switch (key) {
            case 256: {
                if (!this.text.isEmpty()) {
                    this.setText("");
                } else {
                    this.blur();
                }
                return true;
            }
            case 257: {
                this.blur();
                return true;
            }
            case 259: {
                this.deletePrevious(false);
                return true;
            }
            case 261: {
                this.deleteNext(false);
                return true;
            }
            case 262: {
                if (!shift && this.hasSelection()) {
                    this.moveCursorTo(this.getEndOfSelection(), false);
                } else {
                    this.moveCursorTo(this.cursorPosition + 1, shift);
                }
                return true;
            }
            case 263: {
                if (!shift && this.hasSelection()) {
                    this.moveCursorTo(this.getStartOfSelection(), false);
                } else {
                    this.moveCursorTo(this.cursorPosition - 1, shift);
                }
                return true;
            }
            case 268: {
                this.moveCursorTo(0, shift);
                return true;
            }
            case 269: {
                this.moveCursorTo(this.text.length(), shift);
                return true;
            }
        }
        return false;
    }

    public boolean mouseClicked(float mx, float my, int button) {
        boolean inside;
        boolean bl = inside = mx >= this.bx && mx <= this.bx + this.bw && my >= this.by && my <= this.by + this.bh;
        if (inside && button == 0) {
            this.cursorSnap = true;
            long now = System.currentTimeMillis();
            int idx = this.cursorIndexAt(mx);
            if (now - this.lastClickTime < 250L) {
                this.typing = true;
                this.dragging = false;
                this.selectAllText();
            } else {
                this.typing = true;
                this.dragging = true;
                this.selectionStart = this.cursorPosition = idx;
                this.selectionEnd = this.cursorPosition;
            }
            this.lastClickTime = now;
            return true;
        }
        if (!inside && button == 0) {
            this.blur();
        }
        return false;
    }

    public void mouseReleased(int button) {
        if (button == 0) {
            this.dragging = false;
        }
    }

    public void focus() {
        this.typing = true;
        this.dragging = false;
        this.cursorPosition = this.text.length();
        this.clearSelection();
        this.cursorSnap = true;
    }

    private void replaceText(int start, int end, String insertion) {
        int i;
        int e;
        int s = LvSearchField.clamp(start, 0, this.text.length());
        if (s > (e = LvSearchField.clamp(end, 0, this.text.length()))) {
            int tmp = s;
            s = e;
            e = tmp;
        }
        this.text = this.text.substring(0, s) + insertion + this.text.substring(e);
        this.cursorPosition = s + insertion.length();
        for (i = Math.min(e, this.charAnim.size()) - 1; i >= s; --i) {
            this.charAnim.remove(i);
        }
        for (i = 0; i < insertion.length(); ++i) {
            this.charAnim.add(s + i, Float.valueOf(-((float)i * 0.14f)));
        }
        this.clearSelection();
    }

    private boolean isWordCharacter(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private void selectAllText() {
        if (this.text.isEmpty()) {
            this.clearSelection();
            this.cursorPosition = 0;
        } else {
            this.selectionStart = 0;
            this.selectionEnd = this.text.length();
            this.cursorPosition = this.text.length();
        }
    }

    private void copyToClipboard() {
        if (this.hasSelection()) {
            class_310.method_1551().field_1774.method_1455(this.getSelectedText());
        }
    }

    private void cutToClipboard() {
        if (this.hasSelection()) {
            class_310.method_1551().field_1774.method_1455(this.getSelectedText());
            this.deleteSelectedText();
        }
    }

    private void pasteFromClipboard() {
        String clip = class_310.method_1551().field_1774.method_1460();
        if (clip != null && !clip.isEmpty()) {
            String sanitized = clip.replace("\r", "").replace("\n", " ");
            this.deleteSelectedText();
            this.replaceText(this.cursorPosition, this.cursorPosition, sanitized);
        }
    }

    private void deleteSelectedText() {
        if (this.hasSelection()) {
            this.replaceText(this.getStartOfSelection(), this.getEndOfSelection(), "");
        }
    }

    private void deletePrevious(boolean word) {
        if (this.hasSelection()) {
            this.deleteSelectedText();
        } else if (this.cursorPosition > 0) {
            int n = word ? this.findPreviousWordBoundary(this.cursorPosition) : this.cursorPosition - 1;
            this.replaceText(n, this.cursorPosition, "");
        }
    }

    private void deleteNext(boolean word) {
        if (this.hasSelection()) {
            this.deleteSelectedText();
        } else if (this.cursorPosition < this.text.length()) {
            int n = word ? this.findNextWordBoundary(this.cursorPosition) : this.cursorPosition + 1;
            this.replaceText(this.cursorPosition, n, "");
        }
    }

    private void moveCursorTo(int pos, boolean select) {
        int clamped = LvSearchField.clamp(pos, 0, this.text.length());
        if (select) {
            if (this.selectionStart == -1) {
                this.selectionStart = this.cursorPosition;
            }
            this.selectionEnd = this.cursorPosition = clamped;
            if (this.selectionStart == this.selectionEnd) {
                this.clearSelection();
            }
        } else {
            this.cursorPosition = clamped;
            this.clearSelection();
        }
    }

    private int findNextWordBoundary(int pos) {
        int n;
        for (n = LvSearchField.clamp(pos, 0, this.text.length()); n < this.text.length() && !this.isWordCharacter(this.text.charAt(n)); ++n) {
        }
        while (n < this.text.length() && this.isWordCharacter(this.text.charAt(n))) {
            ++n;
        }
        return n;
    }

    private int findPreviousWordBoundary(int pos) {
        int n;
        for (n = LvSearchField.clamp(pos, 0, this.text.length()); n > 0 && !this.isWordCharacter(this.text.charAt(n - 1)); --n) {
        }
        while (n > 0 && this.isWordCharacter(this.text.charAt(n - 1))) {
            --n;
        }
        return n;
    }

    private int cursorIndexAt(float mx) {
        int n;
        float f = mx - this.textStartX + this.xOffset;
        for (n = 0; n < this.text.length(); ++n) {
            float cw = FontsLV.MONTSERRAT_MEDIUM.width(this.text.substring(n, n + 1), 7.0f);
            float offset = FontsLV.MONTSERRAT_MEDIUM.width(this.text.substring(0, n), 7.0f);
            if (offset + cw / 2.0f > f) break;
        }
        return LvSearchField.clamp(n, 0, this.text.length());
    }

    private void updateXOffset() {
        float maxOff;
        float curX = FontsLV.MONTSERRAT_MEDIUM.width(this.text.substring(0, Math.min(this.cursorPosition, this.text.length())), 7.0f);
        if (curX < this.xOffset) {
            this.xOffset = Math.max(0.0f, curX - 6.0f);
        } else if (curX - this.xOffset > this.textVisibleW - 4.0f) {
            this.xOffset = curX - (this.textVisibleW - 4.0f) + 6.0f;
        }
        if (this.xOffset < 0.0f) {
            this.xOffset = 0.0f;
        }
        if (this.xOffset > (maxOff = Math.max(0.0f, FontsLV.MONTSERRAT_MEDIUM.width(this.text, 7.0f) - this.textVisibleW + 4.0f))) {
            this.xOffset = maxOff;
        }
    }

    private void clearSelection() {
        this.selectionStart = -1;
        this.selectionEnd = -1;
    }

    private boolean hasSelection() {
        return this.selectionStart != -1 && this.selectionEnd != -1 && this.selectionStart != this.selectionEnd;
    }

    private int getStartOfSelection() {
        return Math.min(this.selectionStart, this.selectionEnd);
    }

    private int getEndOfSelection() {
        return Math.max(this.selectionStart, this.selectionEnd);
    }

    private String getSelectedText() {
        return this.text.substring(this.getStartOfSelection(), this.getEndOfSelection());
    }

    private float charProgress(int i) {
        return i >= 0 && i < this.charAnim.size() ? this.charAnim.get(i).floatValue() : 1.0f;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static float clamp01(float f) {
        return f < 0.0f ? 0.0f : (f > 1.0f ? 1.0f : f);
    }

    private static int col(int r, int g, int b, float a) {
        int ai = Math.max(0, Math.min(255, Math.round(a)));
        return ai <= 0 ? 0 : new Color(r, g, b, ai).getRGB();
    }

    private static int withAlpha(Color c, float f) {
        int a = LvSearchField.clampA((float)c.getAlpha() * f);
        return a << 24 | c.getRGB() & 0xFFFFFF;
    }

    private static int clampA(float f) {
        return Math.max(0, Math.min(255, Math.round(f)));
    }

    private static int mixRgb(int c1, int c2, float t) {
        t = LvSearchField.clamp01(t);
        int r1 = c1 >> 16 & 0xFF;
        int g1 = c1 >> 8 & 0xFF;
        int b1 = c1 & 0xFF;
        int r2 = c2 >> 16 & 0xFF;
        int g2 = c2 >> 8 & 0xFF;
        int b2 = c2 & 0xFF;
        return Math.round((float)r1 + (float)(r2 - r1) * t) << 16 | Math.round((float)g1 + (float)(g2 - g1) * t) << 8 | Math.round((float)b1 + (float)(b2 - b1) * t);
    }

    private static boolean shiftDown() {
        long handle = class_310.method_1551().method_22683().method_4490();
        return GLFW.glfwGetKey((long)handle, (int)340) == 1 || GLFW.glfwGetKey((long)handle, (int)344) == 1;
    }

    private static boolean ctrlDown() {
        long handle = class_310.method_1551().method_22683().method_4490();
        return GLFW.glfwGetKey((long)handle, (int)341) == 1 || GLFW.glfwGetKey((long)handle, (int)345) == 1;
    }
}

