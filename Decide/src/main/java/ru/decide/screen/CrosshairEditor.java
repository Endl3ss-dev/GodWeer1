/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;
import ru.decide.lang.Lang;
import ru.decide.module.api.settings.impl.PixelGridSetting;
import ru.decide.module.impl.render.CrossHair;
import ru.decide.screen.editor.EditorButton;
import ru.decide.screen.editor.EditorWidgets;
import ru.decide.screen.editor.OverlayEditor;
import ru.decide.screen.editor.Rect;
import ru.decide.screen.editor.SettingsPanel;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.MathUtil;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class CrosshairEditor
implements OverlayEditor,
IMinecraft {
    private static final CrosshairEditor INSTANCE = new CrosshairEditor();
    private final EditorWidgets widgets = new EditorWidgets();
    private final SettingsPanel panel = new SettingsPanel();
    private final EditorButton saveButton = new EditorButton("\u0412\u044b\u0439\u0442\u0438 \u0438 \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c", true);
    private final EditorButton clearButton = new EditorButton("\u041e\u0447\u0438\u0441\u0442\u0438\u0442\u044c", false);
    private final EditorButton resetButton = new EditorButton("\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c", false);
    private final Animation visibility = new Animation();
    private boolean active;
    private boolean mirrorX = true;
    private boolean mirrorY = true;
    private int paintMode = -1;
    private float canvasX;
    private float canvasY;
    private float canvasSize;
    private float cell;
    private Rect mirrorXBounds = Rect.EMPTY;
    private Rect mirrorYBounds = Rect.EMPTY;

    private CrosshairEditor() {
    }

    public static CrosshairEditor getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void open() {
        if (this.active) {
            return;
        }
        this.active = true;
        this.paintMode = -1;
        this.panel.reset();
        this.panel.unplace();
        this.visibility.set(0.0);
        this.visibility.run(1.0, (double)0.24f, Easings.EXPO_OUT);
    }

    @Override
    public void saveAndExit() {
        if (!this.active) {
            return;
        }
        if (Client.get() != null && Client.get().configManager() != null) {
            Client.get().configManager().autoSave();
        }
        this.deactivate();
    }

    @Override
    public void closeFromMenuRemoval() {
        this.saveAndExit();
    }

    private void deactivate() {
        this.active = false;
        this.paintMode = -1;
        this.panel.releaseDrag();
        this.panel.closePickers();
    }

    @Override
    public void render(float width, float height, float mouseX, float mouseY, float parentAlpha) {
        if (!this.active) {
            return;
        }
        CrossHair module = CrossHair.getInstance();
        if (module == null || !module.isDrawing()) {
            this.saveAndExit();
            return;
        }
        this.widgets.setMouse(mouseX, mouseY);
        this.layout(width, height);
        this.saveButton.update(mouseX, mouseY);
        this.clearButton.update(mouseX, mouseY);
        this.resetButton.update(mouseX, mouseY);
        this.panel.layout(module, "Cross Hair", width - 168.0f - 16.0f, 14.0f, width, height, this.widgets);
        this.visibility.update();
        float alpha = this.visibility.get() * parentAlpha;
        RenderUtil.Render2D.rect(0.0f, 0.0f, width, height, ColorUtil.getColor(0, 0.55f * alpha));
        Client.get().render2D().flushAll();
        Fonts.sf_regular.drawCentered(Lang.tr("\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043f\u0440\u0438\u0446\u0435\u043b\u0430"), width / 2.0f, 16.0f, 8.0f, ColorUtil.getColor(240, alpha));
        Fonts.sf_regular.drawCentered(Lang.tr("\u041b\u041a\u041c \u2014 \u0440\u0438\u0441\u043e\u0432\u0430\u0442\u044c") + "   \u2022   " + Lang.tr("\u041f\u041a\u041c \u2014 \u0441\u0442\u0438\u0440\u0430\u0442\u044c") + "   \u2022   " + Lang.tr("Esc \u2014 \u0432\u044b\u0439\u0442\u0438"), width / 2.0f, 29.0f, 6.0f, ColorUtil.getColor(255, alpha * 0.45f));
        this.drawCanvas(module.grid, alpha);
        this.widgets.drawToggleRow(this.mirrorXBounds, Lang.tr("\u0417\u0435\u0440\u043a\u0430\u043b\u043e \u043f\u043e X"), this.mirrorX, "crosshair:mirrorx", alpha);
        this.widgets.drawToggleRow(this.mirrorYBounds, Lang.tr("\u0417\u0435\u0440\u043a\u0430\u043b\u043e \u043f\u043e Y"), this.mirrorY, "crosshair:mirrory", alpha);
        this.widgets.drawButton(this.clearButton, alpha);
        this.widgets.drawButton(this.resetButton, alpha);
        this.widgets.drawButton(this.saveButton, alpha);
        this.panel.draw(this.widgets, alpha);
    }

    private void layout(float width, float height) {
        float areaW = Math.max(200.0f, width - 168.0f - 40.0f);
        this.canvasSize = MathUtil.clamp(Math.min(areaW * 0.75f, height - 150.0f), 110.0f, 340.0f);
        this.canvasX = areaW / 2.0f - this.canvasSize / 2.0f;
        this.canvasY = 50.0f;
        this.cell = this.canvasSize / 21.0f;
        float toggleY = this.canvasY + this.canvasSize + 8.0f;
        float half = this.canvasSize / 2.0f - 3.0f;
        this.mirrorXBounds = new Rect(this.canvasX, toggleY, half, 13.0f);
        this.mirrorYBounds = new Rect(this.canvasX + this.canvasSize / 2.0f + 3.0f, toggleY, half, 13.0f);
        float buttonH = 18.0f;
        float saveW = 120.0f;
        float sideW = 88.0f;
        float bottomY = height - 30.0f;
        this.saveButton.set(this.canvasX + this.canvasSize / 2.0f - saveW / 2.0f, bottomY, saveW, buttonH);
        this.clearButton.set(this.canvasX + this.canvasSize / 2.0f - sideW - 4.0f, bottomY - 24.0f, sideW, buttonH);
        this.resetButton.set(this.canvasX + this.canvasSize / 2.0f + 4.0f, bottomY - 24.0f, sideW, buttonH);
    }

    private void drawCanvas(PixelGridSetting grid, float alpha) {
        int hy;
        RenderUtil.Render2D.glow(this.canvasX, this.canvasY, this.canvasSize, this.canvasSize, ColorUtil.getColor(0, 0.2f * alpha), 4.0f, 12.0f, 1.0f);
        RenderUtil.Render2D.rect(this.canvasX, this.canvasY, this.canvasSize, this.canvasSize, ColorUtil.getColor(18, 0.85f * alpha), 4.0f);
        int c = grid.center();
        int fill = ColorUtil.replAlpha(ColorUtil.client(), alpha);
        int gridLine = ColorUtil.getColor(255, 0.07f * alpha);
        int axisLine = ColorUtil.getColor(255, 0.18f * alpha);
        for (int i = 1; i < 21; ++i) {
            float offset = (float)i * this.cell;
            boolean axis = i == c || i == c + 1;
            int line = axis ? axisLine : gridLine;
            RenderUtil.Render2D.rect(this.canvasX + offset, this.canvasY, 0.5f, this.canvasSize, line);
            RenderUtil.Render2D.rect(this.canvasX, this.canvasY + offset, this.canvasSize, 0.5f, line);
        }
        for (int gy = 0; gy < 21; ++gy) {
            for (int gx = 0; gx < 21; ++gx) {
                if (!grid.get(gx, gy)) continue;
                int pixelFill = CrossHair.getInstance() != null ? CrossHair.getInstance().resolvePixelColor(gx, gy, alpha) : ColorUtil.replAlpha(ColorUtil.client(), alpha);
                RenderUtil.Render2D.rect(this.canvasX + (float)gx * this.cell + 0.5f, this.canvasY + (float)gy * this.cell + 0.5f, this.cell - 1.0f, this.cell - 1.0f, pixelFill, 1.0f);
            }
        }
        int hx = this.cellAt(this.widgets.mouseX(), this.canvasX);
        if (grid.inBounds(hx, hy = this.cellAt(this.widgets.mouseY(), this.canvasY))) {
            int hover = CrossHair.getInstance() != null ? CrossHair.getInstance().resolvePixelColor(hx, hy, alpha * 0.4f) : ColorUtil.replAlpha(ColorUtil.client(), alpha * 0.35f);
            for (int[] point : this.mirrored(hx, hy)) {
                RenderUtil.Render2D.rect(this.canvasX + (float)point[0] * this.cell + 0.5f, this.canvasY + (float)point[1] * this.cell + 0.5f, this.cell - 1.0f, this.cell - 1.0f, hover, 1.0f);
            }
        }
        RenderUtil.Render2D.outline(this.canvasX, this.canvasY, this.canvasSize, this.canvasSize, 0.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * 0.35f), 4.0f);
    }

    private int cellAt(float mouse, float origin) {
        if (this.cell <= 0.0f) {
            return -1;
        }
        return (int)Math.floor((mouse - origin) / this.cell);
    }

    private boolean overCanvas(float mouseX, float mouseY) {
        return mouseX >= this.canvasX && mouseY >= this.canvasY && mouseX < this.canvasX + this.canvasSize && mouseY < this.canvasY + this.canvasSize;
    }

    private int[][] mirrored(int gx, int gy) {
        int last = 20;
        if (this.mirrorX && this.mirrorY) {
            return new int[][]{{gx, gy}, {last - gx, gy}, {gx, last - gy}, {last - gx, last - gy}};
        }
        if (this.mirrorX) {
            return new int[][]{{gx, gy}, {last - gx, gy}};
        }
        if (this.mirrorY) {
            return new int[][]{{gx, gy}, {gx, last - gy}};
        }
        return new int[][]{{gx, gy}};
    }

    private void paint(float mouseX, float mouseY) {
        int gy;
        if (this.paintMode < 0 || !this.overCanvas(mouseX, mouseY)) {
            return;
        }
        CrossHair module = CrossHair.getInstance();
        if (module == null) {
            return;
        }
        int gx = this.cellAt(mouseX, this.canvasX);
        if (!module.grid.inBounds(gx, gy = this.cellAt(mouseY, this.canvasY))) {
            return;
        }
        for (int[] point : this.mirrored(gx, gy)) {
            module.grid.put(point[0], point[1], this.paintMode == 1);
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (!this.active) {
            return false;
        }
        if ((button == 0 || button == 1) && this.panel.handleClick(mouseX, mouseY, button)) {
            return true;
        }
        if (this.overCanvas(mouseX, mouseY)) {
            this.paintMode = button == 1 ? 0 : 1;
            this.paint(mouseX, mouseY);
            return true;
        }
        if (button != 0) {
            return true;
        }
        CrossHair module = CrossHair.getInstance();
        if (this.mirrorXBounds.contains(mouseX, mouseY)) {
            this.mirrorX = !this.mirrorX;
        } else if (this.mirrorYBounds.contains(mouseX, mouseY)) {
            this.mirrorY = !this.mirrorY;
        } else if (this.saveButton.contains(mouseX, mouseY)) {
            this.saveButton.press();
            this.saveAndExit();
        } else if (module != null && this.clearButton.contains(mouseX, mouseY)) {
            this.clearButton.press();
            module.grid.clear();
        } else if (module != null && this.resetButton.contains(mouseX, mouseY)) {
            this.resetButton.press();
            module.grid.reset();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(int button) {
        if (!this.active) {
            return false;
        }
        this.paintMode = -1;
        if (button == 0) {
            this.panel.releaseDrag();
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(float mouseX, float mouseY, double verticalAmount) {
        if (!this.active) {
            return false;
        }
        this.panel.handleScroll(mouseX, mouseY, verticalAmount);
        return true;
    }

    @Override
    public void rawMouseButton(float mouseX, float mouseY, int button, boolean pressed) {
        if (!this.active) {
            return;
        }
        if (!pressed) {
            this.paintMode = -1;
            if (button == 0) {
                this.panel.releaseDrag();
            }
            return;
        }
        if (this.overCanvas(mouseX, mouseY)) {
            this.paintMode = button == 1 ? 0 : 1;
            this.paint(mouseX, mouseY);
        }
    }

    @Override
    public void rawMouseMoved(float mouseX, float mouseY) {
        if (!this.active) {
            return;
        }
        if (this.panel.rawMouseMoved(mouseX, mouseY)) {
            return;
        }
        this.paint(mouseX, mouseY);
    }
}

