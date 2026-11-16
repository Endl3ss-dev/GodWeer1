/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 *  org.joml.Matrix3x2fStack
 */
package ru.decide.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.joml.Matrix3x2fStack;
import ru.decide.Client;
import ru.decide.inventorypreset.InventoryPreset;
import ru.decide.inventorypreset.InventoryPresetManager;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.animation.satoshi.Direction;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.MathUtil;
import ru.decide.utils.other.ClipboardPaste;
import ru.decide.utils.render.Render2D;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.Scissor;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class InventoryPresetScreen
extends class_437
implements IMinecraft {
    private static final float PANEL_W = 400.0f;
    private static final float PANEL_H = 250.0f;
    private static final float LEFT_W = 160.0f;
    private static final float ROW_H = 18.0f;
    private static final float ROW_GAP = 3.0f;
    private static final float CELL = 19.0f;
    private static final float TEXT = 6.5f;
    private static final float SMALL = 6.0f;
    private final class_437 parent;
    private final List<Row> rows = new ArrayList<Row>();
    private final Map<String, ru.decide.utils.animation.satoshi.Animation> anims = new HashMap<String, ru.decide.utils.animation.satoshi.Animation>();
    private final Animation openAnim = new Animation();
    private String name = "";
    private boolean focused;
    private float scroll;
    private float scrollTarget;
    private float maxScroll;
    private float scaleFix = 1.0f;
    private float panelX;
    private float panelY;
    private float mouseX;
    private float mouseY;
    private InventoryPreset preview;

    public InventoryPresetScreen(class_437 parent) {
        super((class_2561)class_2561.method_43470((String)"\u041c\u0435\u043d\u0435\u0434\u0436\u0435\u0440 \u043f\u0440\u0435\u0441\u0435\u0442\u043e\u0432"));
        this.parent = parent;
    }

    private InventoryPresetManager manager() {
        return Client.get().inventoryPresetManager();
    }

    protected void method_25426() {
        this.openAnim.set(0.0);
        this.openAnim.run(1.0, 0.25, Easings.SINE_OUT);
    }

    public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
    }

    public void method_25394(class_332 context, int rawMouseX, int rawMouseY, float delta) {
        InventoryPreset selected;
        float targetScale = 2.0f;
        float currentScale = mc.method_22683().method_4495();
        this.scaleFix = targetScale / currentScale;
        float screenW = (float)mc.method_22683().method_4486() / this.scaleFix;
        float screenH = (float)mc.method_22683().method_4502() / this.scaleFix;
        this.mouseX = (float)rawMouseX / this.scaleFix;
        this.mouseY = (float)rawMouseY / this.scaleFix;
        this.openAnim.update();
        float alpha = this.openAnim.get();
        this.scroll += (this.scrollTarget - this.scroll) * 0.2f;
        this.panelX = screenW / 2.0f - 200.0f;
        this.panelY = screenH / 2.0f - 125.0f + (1.0f - alpha) * 30.0f;
        Render2D.beginOverlay();
        RenderUtil.Render2D.rect(0.0f, 0.0f, screenW, screenH, ColorUtil.getColor(0, 0.6f * alpha));
        RenderUtil.Render2D.glow(this.panelX, this.panelY, 400.0f, 249.5f, ColorUtil.getColor(0, 0.15f * alpha), 8.0f, 15.0f, 1.0f);
        RenderUtil.Blur.blur(this.panelX, this.panelY, 400.0f, 250.0f, alpha, 8.0f, ColorUtil.multAlpha(ColorUtil.multDark(ColorUtil.background(), 0.6f), alpha));
        RenderUtil.Render2D.outline(this.panelX, this.panelY, 400.0f, 250.0f, 0.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * 0.35f), 8.0f);
        Font font = Fonts.sf_regular;
        RenderUtil.Render2D.rect(this.panelX + 160.0f, this.panelY + 8.0f, 0.5f, 234.0f, ColorUtil.getColor(255, 0.05f * alpha), 1.0f);
        Fonts.icon.draw("P", this.panelX + 10.0f, this.panelY + 12.5f, 7.0f, ColorUtil.multAlpha(ColorUtil.client(), alpha));
        font.draw("\u041f\u0440\u0435\u0441\u0435\u0442\u044b \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044f", this.panelX + 22.0f, this.panelY + 11.5f, 7.0f, ColorUtil.getColor(200, alpha));
        font.draw("\u041f\u0440\u0435\u0434\u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440", this.panelX + 160.0f + 12.0f, this.panelY + 11.5f, 7.0f, ColorUtil.getColor(200, alpha));
        float inputX = this.panelX + 10.0f;
        float inputW = 140.0f;
        float inputY = this.panelY + 28.0f;
        this.drawInput(font, inputX, inputY, inputW, alpha);
        float saveY = inputY + 16.0f + 3.0f;
        boolean saveHover = this.drawButton(font, "\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0442\u0435\u043a\u0443\u0449\u0438\u0439", inputX, saveY, inputW, alpha, true);
        this.rows.clear();
        List<InventoryPreset> presets = this.manager().presets();
        float listY = saveY + 16.0f + 6.0f;
        float listH = this.panelY + 250.0f - 32.0f - listY;
        this.maxScroll = Math.max(0.0f, (float)presets.size() * 21.0f - listH);
        this.scrollTarget = MathUtil.clamp(this.scrollTarget, 0.0f, this.maxScroll);
        Scissor.enable(inputX, listY, inputW, listH, 2.0f);
        InventoryPreset hoveredPreset = null;
        float rowY = listY - this.scroll;
        for (InventoryPreset preset : presets) {
            boolean active = preset.name().equalsIgnoreCase(this.manager().activeName());
            boolean hovered = InventoryPresetScreen.inside(this.mouseX, this.mouseY, inputX, rowY, inputW, 18.0f);
            if (hovered) {
                hoveredPreset = preset;
            }
            float hover = this.hoverAnim("row:" + preset.name(), hovered);
            ru.decide.utils.animation.satoshi.Animation activeAnim = this.anim("active:" + preset.name());
            activeAnim.setDirection(active ? Direction.FORWARDS : Direction.BACKWARDS);
            float sel = activeAnim.getOutput();
            RenderUtil.Render2D.rect(inputX, rowY, inputW, 18.0f, ColorUtil.overCol(ColorUtil.getColor(40, 0.15f * alpha), ColorUtil.replAlpha(ColorUtil.client(), 0.25f * alpha), sel), 5.0f);
            RenderUtil.Render2D.outline(inputX, rowY, inputW, 18.0f, 0.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * Math.max(hover, sel)), 5.0f);
            font.drawFadingText(preset.name(), inputX + 6.0f, rowY + 5.5f, inputW - 26.0f, ColorUtil.getColor(255, alpha * (0.5f + 0.5f * Math.max(hover, sel))), 6.5f);
            boolean deleteHover = InventoryPresetScreen.inside(this.mouseX, this.mouseY, inputX + inputW - 18.0f, rowY, 18.0f, 18.0f);
            Fonts.icon.drawCentered("Q", inputX + inputW - 9.0f, rowY + 7.0f, 4.0f, deleteHover ? ColorUtil.getColor(235, 70, 70, alpha) : ColorUtil.getColor(255, alpha * 0.35f));
            this.rows.add(new Row(preset, inputX, rowY, inputW, 18.0f));
            rowY += 21.0f;
        }
        Scissor.disable();
        boolean hasActive = this.manager().activePreset().isPresent();
        this.drawButton(font, hasActive ? "\u041e\u0442\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043f\u0440\u0435\u0441\u0435\u0442" : "\u041f\u0440\u0435\u0441\u0435\u0442 \u043d\u0435 \u0432\u044b\u0431\u0440\u0430\u043d", inputX, this.panelY + 250.0f - 26.0f, inputW, alpha, false);
        InventoryPreset inventoryPreset = selected = hoveredPreset != null ? hoveredPreset : this.preview;
        if (selected == null) {
            selected = this.manager().activePreset().orElse(presets.isEmpty() ? null : presets.get(0));
        }
        this.renderPreview(context, font, selected, alpha, rawMouseX, rawMouseY);
        Render2D.endOverlay();
    }

    private void drawInput(Font font, float x, float y, float w, float alpha) {
        boolean placeholder;
        float hover = this.hoverAnim("input", InventoryPresetScreen.inside(this.mouseX, this.mouseY, x, y, w, 16.0f) || this.focused);
        ru.decide.utils.animation.satoshi.Animation focusAnim = this.anim("input:focus");
        focusAnim.setDirection(this.focused ? Direction.FORWARDS : Direction.BACKWARDS);
        float focus = focusAnim.getOutput();
        RenderUtil.Render2D.rect(x, y, w, 16.0f, ColorUtil.overCol(ColorUtil.getColor(40, 0.15f * alpha), ColorUtil.getColor(25, 0.3f * alpha), Math.max(hover, focus)), 5.0f);
        RenderUtil.Render2D.outline(x, y, w, 16.0f, 0.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * focus), 5.0f);
        boolean bl = placeholder = this.name.isEmpty() && focus < 0.99f;
        if (placeholder) {
            font.draw("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043f\u0440\u0435\u0441\u0435\u0442\u0430", x + 6.0f, y + 4.5f + 4.0f * focus, 6.5f, ColorUtil.getColor(255, alpha * (1.0f - focus) * 0.35f));
        }
        if (!this.name.isEmpty()) {
            font.drawFadingTextReverse(this.name, x + 6.0f, y + 4.5f, w - 14.0f, ColorUtil.getColor(255, alpha * 0.85f), 6.5f);
        }
        if (focus > 0.01f && System.currentTimeMillis() / 450L % 2L == 0L) {
            RenderUtil.Render2D.rect(x + 6.5f + font.getWidth(this.name, 6.5f), y + 4.5f, 0.6f, 8.0f, ColorUtil.replAlpha(ColorUtil.client(), alpha * focus), 0.3f);
        }
    }

    private boolean drawButton(Font font, String label, float x, float y, float w, float alpha, boolean accent) {
        boolean hovered = InventoryPresetScreen.inside(this.mouseX, this.mouseY, x, y, w, 16.0f);
        float hover = this.hoverAnim("btn:" + label, hovered);
        RenderUtil.Render2D.glow(x, y, w, 16.0f, ColorUtil.getColor(0, 0.06f * alpha), 5.0f, 7.0f, 1.0f);
        RenderUtil.Render2D.rect(x, y, w, 16.0f, ColorUtil.overCol(ColorUtil.getColor(40, 0.15f * alpha), ColorUtil.replAlpha(ColorUtil.client(), (accent ? 0.3f : 0.2f) * alpha), hover), 5.0f);
        RenderUtil.Render2D.outline(x, y, w, 16.0f, 0.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * (accent ? 0.4f + 0.6f * hover : hover)), 5.0f);
        font.drawCentered(label, x + w / 2.0f, y + 4.5f, 6.5f, ColorUtil.getColor(255, alpha * (0.55f + 0.45f * hover)));
        return hovered;
    }

    private void renderPreview(class_332 context, Font font, InventoryPreset preset, float alpha, int rawMouseX, int rawMouseY) {
        float baseX = this.panelX + 160.0f + 12.0f;
        float baseY = this.panelY + 34.0f;
        if (preset == null) {
            font.draw("\u0421\u043e\u0445\u0440\u0430\u043d\u0451\u043d\u043d\u044b\u0445 \u043f\u0440\u0435\u0441\u0435\u0442\u043e\u0432 \u043f\u043e\u043a\u0430 \u043d\u0435\u0442", baseX, baseY, 6.5f, ColorUtil.getColor(255, alpha * 0.35f));
            return;
        }
        font.draw(preset.name(), baseX, baseY, 6.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha));
        float gridX = baseX + 26.0f;
        float gridY = baseY + 24.0f;
        class_1799 hovered = class_1799.field_8037;
        font.draw("\u0411\u0440\u043e\u043d\u044f", baseX, baseY + 14.0f, 6.0f, ColorUtil.getColor(255, alpha * 0.35f));
        for (int i = 0; i < 4; ++i) {
            class_1799 stack = preset.slot(36 + i).toStack();
            if (!this.drawCell(context, stack, baseX, gridY + (float)i * 19.0f, alpha)) continue;
            hovered = stack;
        }
        font.draw("\u041e\u0444\u0444\u0445\u0435\u043d\u0434", baseX, gridY + 76.0f + 4.0f, 6.0f, ColorUtil.getColor(255, alpha * 0.35f));
        class_1799 offhand = preset.slot(40).toStack();
        if (this.drawCell(context, offhand, baseX, gridY + 76.0f + 15.0f, alpha)) {
            hovered = offhand;
        }
        font.draw("\u0418\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044c", gridX, baseY + 14.0f, 6.0f, ColorUtil.getColor(255, alpha * 0.35f));
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                class_1799 stack = preset.slot(9 + row * 9 + col).toStack();
                if (!this.drawCell(context, stack, gridX + (float)col * 19.0f, gridY + (float)row * 19.0f, alpha)) continue;
                hovered = stack;
            }
        }
        font.draw("\u0425\u043e\u0442\u0431\u0430\u0440", gridX, gridY + 57.0f + 4.0f, 6.0f, ColorUtil.getColor(255, alpha * 0.35f));
        for (int col = 0; col < 9; ++col) {
            class_1799 stack = preset.slot(col).toStack();
            if (!this.drawCell(context, stack, gridX + (float)col * 19.0f, gridY + 57.0f + 15.0f, alpha)) continue;
            hovered = stack;
        }
        if (!hovered.method_7960()) {
            Render2D.endOverlay();
            context.method_51446(this.field_22793, hovered, rawMouseX, rawMouseY);
            Render2D.beginOverlay();
        }
    }

    private boolean drawCell(class_332 context, class_1799 stack, float x, float y, float alpha) {
        boolean hovered = InventoryPresetScreen.inside(this.mouseX, this.mouseY, x, y, 17.0f, 17.0f);
        RenderUtil.Render2D.rect(x, y, 17.0f, 17.0f, ColorUtil.getColor(40, 0.15f * alpha), 4.0f);
        if (hovered) {
            RenderUtil.Render2D.outline(x, y, 17.0f, 17.0f, 0.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha), 4.0f);
        }
        if (!stack.method_7960()) {
            Matrix3x2fStack matrices = context.method_51448();
            matrices.pushMatrix();
            matrices.translate((x + 8.5f) * this.scaleFix, (y + 8.5f) * this.scaleFix);
            matrices.scale(this.scaleFix, this.scaleFix);
            context.method_51427(stack, -8, -8);
            context.method_51431(this.field_22793, stack, -8, -8);
            matrices.popMatrix();
        }
        return hovered && !stack.method_7960();
    }

    private ru.decide.utils.animation.satoshi.Animation anim(String key) {
        return this.anims.computeIfAbsent(key, k -> new EaseInOutQuad(300, 1.0));
    }

    private float hoverAnim(String key, boolean hovered) {
        ru.decide.utils.animation.satoshi.Animation animation = this.anim(key + ":hover");
        animation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        return animation.getOutput();
    }

    public boolean method_25402(class_11909 click, boolean doubled) {
        if (click.method_74245() != 0) {
            return super.method_25402(click, doubled);
        }
        float mx = (float)(click.comp_4798() / (double)this.scaleFix);
        float my = (float)(click.comp_4799() / (double)this.scaleFix);
        float inputX = this.panelX + 10.0f;
        float inputW = 140.0f;
        float inputY = this.panelY + 28.0f;
        this.focused = InventoryPresetScreen.inside(mx, my, inputX, inputY, inputW, 16.0f);
        if (InventoryPresetScreen.inside(mx, my, inputX, inputY + 16.0f + 3.0f, inputW, 16.0f)) {
            this.saveCurrent();
            return true;
        }
        for (Row row : this.rows) {
            if (InventoryPresetScreen.inside(mx, my, row.x + row.w - 18.0f, row.y, 18.0f, row.h)) {
                this.manager().delete(row.preset);
                if (this.preview == row.preset) {
                    this.preview = null;
                }
                return true;
            }
            if (!InventoryPresetScreen.inside(mx, my, row.x, row.y, row.w - 18.0f, row.h)) continue;
            this.manager().activate(row.preset);
            this.field_22787.method_1507(this.parent);
            return true;
        }
        if (InventoryPresetScreen.inside(mx, my, inputX, this.panelY + 250.0f - 26.0f, inputW, 16.0f) && this.manager().activePreset().isPresent()) {
            this.manager().deactivate();
            return true;
        }
        return true;
    }

    private void saveCurrent() {
        InventoryPreset saved = this.manager().saveCurrent(this.name);
        if (saved != null) {
            this.preview = saved;
            this.name = "";
            this.focused = false;
        }
    }

    public boolean method_25404(class_11908 input) {
        if (this.focused) {
            if (input.comp_4795() == 257) {
                this.saveCurrent();
            } else if (ClipboardPaste.pasteCombo(input.comp_4795())) {
                this.name = ClipboardPaste.append(this.name, 32);
            } else if (input.comp_4795() == 259 && !this.name.isEmpty()) {
                this.name = this.name.substring(0, this.name.length() - 1);
            } else if (input.comp_4795() == 256) {
                this.focused = false;
            }
            return true;
        }
        if (input.comp_4795() == 256) {
            this.field_22787.method_1507(this.parent);
            return true;
        }
        return super.method_25404(input);
    }

    public boolean method_25400(class_11905 input) {
        if (this.focused && input.method_74227() && this.name.length() < 32) {
            this.name = this.name + input.method_74226();
            return true;
        }
        return super.method_25400(input);
    }

    public boolean method_25401(double mouseX, double mouseY, double horizontal, double vertical) {
        this.scrollTarget = MathUtil.clamp((float)((double)this.scrollTarget - vertical * 24.0), 0.0f, this.maxScroll);
        return true;
    }

    public void method_25419() {
        this.field_22787.method_1507(this.parent);
    }

    public boolean method_25421() {
        return false;
    }

    private static boolean inside(float mouseX, float mouseY, float x, float y, float w, float h) {
        return MathUtil.isHovered(mouseX, mouseY, x, y, w, h);
    }

    @Environment(value=EnvType.CLIENT)
    private record Row(InventoryPreset preset, float x, float y, float w, float h) {
    }
}

