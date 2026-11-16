/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1306
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package ru.decide.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import ru.decide.Client;
import ru.decide.lang.Lang;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.GlassHands;
import ru.decide.module.impl.render.Hands;
import ru.decide.module.impl.render.SwingAnimation;
import ru.decide.screen.editor.EditorButton;
import ru.decide.screen.editor.EditorWidgets;
import ru.decide.screen.editor.OverlayEditor;
import ru.decide.screen.editor.Rect;
import ru.decide.screen.editor.SettingsPanel;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.MathUtil;
import ru.decide.utils.render.GlassHandsRenderer;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class HandsEditor
implements OverlayEditor {
    private static final HandsEditor INSTANCE = new HandsEditor();
    private static final long BOUNDS_TTL_MS = 250L;
    private static final long AUTO_SWING_INTERVAL_MS = 500L;
    private static final class_1799 RIGHT_PREVIEW_STACK = new class_1799((class_1935)class_1802.field_22022);
    private static final class_1799 LEFT_PREVIEW_STACK = new class_1799((class_1935)class_1802.field_8288);
    private static final float[][] HAND_SAMPLE_POINTS = new float[][]{{-0.55f, -0.55f, -0.35f}, {-0.55f, -0.55f, 0.35f}, {-0.55f, 0.55f, -0.35f}, {-0.55f, 0.55f, 0.35f}, {0.55f, -0.55f, -0.35f}, {0.55f, -0.55f, 0.35f}, {0.55f, 0.55f, -0.35f}, {0.55f, 0.55f, 0.35f}};
    private final HandBounds leftBounds = new HandBounds();
    private final HandBounds rightBounds = new HandBounds();
    private final Animation visibility = new Animation();
    private final Animation leftHover = new Animation();
    private final Animation rightHover = new Animation();
    private final EditorButton saveButton = new EditorButton("\u0412\u044b\u0439\u0442\u0438 \u0438 \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c", true);
    private final EditorButton resetAllButton = new EditorButton("\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c \u043e\u0431\u0435 \u0440\u0443\u043a\u0438", false);
    private final EditorButton resetLeftButton = new EditorButton("\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c \u043b\u0435\u0432\u0443\u044e", false);
    private final EditorButton resetRightButton = new EditorButton("\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c \u043f\u0440\u0430\u0432\u0443\u044e", false);
    private boolean active;
    private class_1306 hoveredArm;
    private class_1306 draggingArm;
    private int draggingButton = -1;
    private boolean autoSwingPreview;
    private long nextAutoSwingAt;
    private float dragStartMouseX;
    private float dragStartMouseY;
    private float dragStartValueX;
    private float dragStartValueY;
    private float screenWidth;
    private float screenHeight;
    private Rect autoSwingBounds = Rect.EMPTY;
    private final EditorWidgets widgets = new EditorWidgets();
    private final SettingsPanel swingPanel = new SettingsPanel();
    private final SettingsPanel glassPanel = new SettingsPanel();

    private HandsEditor() {
    }

    public static HandsEditor getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public class_1799 previewStackFor(class_1306 arm) {
        return arm == class_1306.field_6183 ? RIGHT_PREVIEW_STACK : LEFT_PREVIEW_STACK;
    }

    public void open() {
        if (this.active) {
            return;
        }
        this.active = true;
        this.hoveredArm = null;
        this.draggingArm = null;
        this.draggingButton = -1;
        this.autoSwingPreview = false;
        this.nextAutoSwingAt = 0L;
        this.swingPanel.reset();
        this.glassPanel.reset();
        this.closePickers();
        this.leftBounds.invalidate();
        this.rightBounds.invalidate();
        this.visibility.set(0.0);
        this.visibility.run(1.0, (double)0.24f, Easings.EXPO_OUT);
        Hands hands = Hands.get();
        if (hands != null) {
            hands.setEditorActive(true);
        }
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
        if (!this.active) {
            return;
        }
        if (Client.get() != null && Client.get().configManager() != null) {
            Client.get().configManager().autoSave();
        }
        this.deactivate();
    }

    private void deactivate() {
        this.active = false;
        this.hoveredArm = null;
        this.draggingArm = null;
        this.draggingButton = -1;
        this.swingPanel.releaseDrag();
        this.glassPanel.releaseDrag();
        this.autoSwingPreview = false;
        this.nextAutoSwingAt = 0L;
        this.closePickers();
        Hands hands = Hands.get();
        if (hands != null) {
            hands.setEditorActive(false);
        }
        this.restoreGlassHandsRenderer();
    }

    private void closePickers() {
        GlassHands glass = GlassHands.getInstance();
        if (glass != null) {
            glass.tintColor.pickerOpen = false;
        }
        this.swingPanel.closePickers();
        this.glassPanel.closePickers();
    }

    @Override
    public void render(float width, float height, float mouseX, float mouseY, float parentAlpha) {
        if (!this.active) {
            return;
        }
        this.screenWidth = width;
        this.screenHeight = height;
        this.widgets.setMouse(mouseX, mouseY);
        this.layoutButtons(width, height);
        this.swingPanel.layout(SwingAnimation.get(), "Swing Animation", width - 168.0f - 16.0f, 14.0f, width, height, this.widgets);
        this.glassPanel.layout(GlassHands.getInstance(), "GlassHands", 16.0f, 14.0f, width, height, this.widgets);
        this.updateAutoSwingPreview();
        this.visibility.update();
        float alpha = this.visibility.get() * parentAlpha;
        this.hoveredArm = this.draggingArm != null ? this.draggingArm : this.findHoveredArm(mouseX, mouseY);
        this.updateHover(this.leftHover, this.hoveredArm == class_1306.field_6182);
        this.updateHover(this.rightHover, this.hoveredArm == class_1306.field_6183);
        this.saveButton.update(mouseX, mouseY);
        this.resetAllButton.update(mouseX, mouseY);
        this.resetLeftButton.update(mouseX, mouseY);
        this.resetRightButton.update(mouseX, mouseY);
        RenderUtil.Render2D.rect(0.0f, 0.0f, width, height, ColorUtil.getColor(0, 0.3f * alpha));
        Client.get().render2D().flushAll();
        GlassHandsRenderer.getInstance().restoreHandsAfterOverlay();
        this.drawHeader(width, alpha);
        this.widgets.drawButton(this.resetLeftButton, alpha);
        this.widgets.drawButton(this.resetRightButton, alpha);
        this.widgets.drawButton(this.resetAllButton, alpha);
        this.widgets.drawButton(this.saveButton, alpha);
        this.widgets.drawToggleRow(this.autoSwingBounds, Lang.tr("\u0410\u0432\u0442\u043e-\u0432\u0437\u043c\u0430\u0445 \u043f\u0440\u0430\u0432\u043e\u0439 \u0440\u0443\u043a\u0438"), this.autoSwingPreview, "editor:autoswing", alpha);
        this.swingPanel.draw(this.widgets, alpha);
        this.glassPanel.draw(this.widgets, alpha);
    }

    private void drawHeader(float width, float alpha) {
        Client.get().render2D().flushAll();
        Fonts.sf_regular.drawCentered(Lang.tr("\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u0440\u0443\u043a"), width / 2.0f, 16.0f, 8.0f, ColorUtil.getColor(240, alpha));
        Fonts.sf_regular.drawCentered(Lang.tr("\u041d\u0430\u0432\u0435\u0434\u0438\u0442\u0435 \u043a\u0443\u0440\u0441\u043e\u0440 \u043d\u0430 \u0440\u0443\u043a\u0443"), width / 2.0f, 29.0f, 6.0f, ColorUtil.getColor(255, alpha * 0.45f));
        Fonts.sf_regular.drawCentered(Lang.tr("\u041b\u041a\u041c / \u041f\u041a\u041c + \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u0435 \u2014 \u043f\u0435\u0440\u0435\u043c\u0435\u0441\u0442\u0438\u0442\u044c") + "   \u2022   " + Lang.tr("\u041a\u043e\u043b\u0435\u0441\u043e \u2014 \u0438\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u0440\u0430\u0437\u043c\u0435\u0440"), width / 2.0f, 39.0f, 5.5f, ColorUtil.getColor(255, alpha * 0.3f));
    }

    private void layoutButtons(float width, float height) {
        float buttonH = 18.0f;
        float sideW = 100.0f;
        float saveW = 120.0f;
        float resetAllW = 112.0f;
        float bottomY = height - 30.0f;
        this.saveButton.set(width / 2.0f - saveW / 2.0f, bottomY, saveW, buttonH);
        this.resetAllButton.set(width / 2.0f - resetAllW / 2.0f, bottomY - 25.0f, resetAllW, buttonH);
        this.resetLeftButton.set(20.0f, bottomY, sideW, buttonH);
        this.resetRightButton.set(width - 20.0f - sideW, bottomY, sideW, buttonH);
        this.autoSwingBounds = new Rect(width / 2.0f - 80.0f, bottomY - 52.0f, 160.0f, 13.0f);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (!this.active) {
            return false;
        }
        if ((button == 0 || button == 1) && (this.glassPanel.handleClick(mouseX, mouseY, button) || this.swingPanel.handleClick(mouseX, mouseY, button))) {
            return true;
        }
        if (button == 0) {
            if (this.autoSwingBounds.contains(mouseX, mouseY)) {
                this.autoSwingPreview = !this.autoSwingPreview;
                this.nextAutoSwingAt = 0L;
                return true;
            }
            Hands hands = Hands.get();
            if (this.saveButton.contains(mouseX, mouseY)) {
                this.saveButton.press();
                this.saveAndExit();
            } else if (hands != null && this.resetAllButton.contains(mouseX, mouseY)) {
                this.resetAllButton.press();
                hands.resetAll();
            } else if (hands != null && this.resetLeftButton.contains(mouseX, mouseY)) {
                this.resetLeftButton.press();
                hands.resetLeft();
            } else if (hands != null && this.resetRightButton.contains(mouseX, mouseY)) {
                this.resetRightButton.press();
                hands.resetRight();
            } else {
                class_1306 arm = this.findDragArm(mouseX, mouseY);
                if (arm != null) {
                    this.beginDrag(arm, mouseX, mouseY, button);
                }
            }
            return true;
        }
        if (button == 1) {
            if (this.isOverControls(mouseX, mouseY)) {
                return true;
            }
            class_1306 arm = this.findDragArm(mouseX, mouseY);
            if (arm != null) {
                this.beginDrag(arm, mouseX, mouseY, button);
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(float deltaX, float deltaY) {
        return this.active;
    }

    @Override
    public boolean mouseReleased(int button) {
        if (!this.active) {
            return false;
        }
        if (button == 0) {
            this.swingPanel.releaseDrag();
            this.glassPanel.releaseDrag();
        }
        if (button == this.draggingButton) {
            this.draggingArm = null;
            this.draggingButton = -1;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(float mouseX, float mouseY, double verticalAmount) {
        if (!this.active) {
            return false;
        }
        if (this.glassPanel.handleScroll(mouseX, mouseY, verticalAmount) || this.swingPanel.handleScroll(mouseX, mouseY, verticalAmount)) {
            return true;
        }
        if (this.autoSwingBounds.contains(mouseX, mouseY)) {
            return true;
        }
        class_1306 arm = this.findHoveredArm(mouseX, mouseY);
        Hands hands = Hands.get();
        if (arm == null || hands == null || verticalAmount == 0.0) {
            return true;
        }
        SliderSetting scale = arm == class_1306.field_6183 ? hands.rScale : hands.lScale;
        float direction = verticalAmount > 0.0 ? 1.0f : -1.0f;
        float oldScale = scale.getValue().floatValue();
        float newScale = this.clampAndRound(oldScale + direction * scale.increment, scale);
        if (Math.abs(newScale - oldScale) < 1.0E-4f) {
            return true;
        }
        Rect rendered = this.boundsFor(arm).freshRect();
        Rect anchor = rendered != null ? rendered : this.controlRect(arm);
        float ratio = newScale / oldScale;
        float shiftX = (1.0f - ratio) * (mouseX - anchor.centerX());
        float shiftY = (1.0f - ratio) * (mouseY - anchor.centerY());
        SliderSetting xSetting = arm == class_1306.field_6183 ? hands.rx : hands.lx;
        SliderSetting ySetting = arm == class_1306.field_6183 ? hands.ry : hands.ly;
        float pixelsPerUnit = this.dragPixelsPerUnit();
        xSetting.set(Float.valueOf(class_3532.method_15363((float)(xSetting.getValue().floatValue() + shiftX / pixelsPerUnit), (float)xSetting.min, (float)xSetting.max)));
        ySetting.set(Float.valueOf(class_3532.method_15363((float)(ySetting.getValue().floatValue() - shiftY / pixelsPerUnit), (float)ySetting.min, (float)ySetting.max)));
        scale.set(Float.valueOf(newScale));
        return true;
    }

    @Override
    public void rawMouseButton(float mouseX, float mouseY, int button, boolean pressed) {
        if (!this.active) {
            return;
        }
        if (button != 0 && button != 1) {
            return;
        }
        if (!pressed) {
            if (button == 0) {
                this.swingPanel.releaseDrag();
                this.glassPanel.releaseDrag();
            }
            if (button == this.draggingButton) {
                this.draggingArm = null;
                this.draggingButton = -1;
            }
            return;
        }
        if (this.isOverControls(mouseX, mouseY)) {
            return;
        }
        class_1306 arm = this.findDragArm(mouseX, mouseY);
        if (arm != null) {
            this.beginDrag(arm, mouseX, mouseY, button);
        }
    }

    @Override
    public void rawMouseMoved(float mouseX, float mouseY) {
        if (this.glassPanel.rawMouseMoved(mouseX, mouseY)) {
            return;
        }
        if (this.swingPanel.rawMouseMoved(mouseX, mouseY)) {
            return;
        }
        this.applyDrag(mouseX, mouseY);
    }

    private void beginDrag(class_1306 arm, float mouseX, float mouseY, int button) {
        Hands hands = Hands.get();
        if (hands == null) {
            return;
        }
        this.draggingArm = arm;
        this.draggingButton = button;
        this.dragStartMouseX = mouseX;
        this.dragStartMouseY = mouseY;
        this.dragStartValueX = (arm == class_1306.field_6183 ? hands.rx.getValue() : hands.lx.getValue()).floatValue();
        this.dragStartValueY = (arm == class_1306.field_6183 ? hands.ry.getValue() : hands.ly.getValue()).floatValue();
    }

    private void applyDrag(float mouseX, float mouseY) {
        if (!this.active || this.draggingArm == null) {
            return;
        }
        Hands hands = Hands.get();
        if (hands == null) {
            return;
        }
        float sensitivity = 1.0f / this.dragPixelsPerUnit();
        float valueX = this.dragStartValueX + (mouseX - this.dragStartMouseX) * sensitivity;
        float valueY = this.dragStartValueY - (mouseY - this.dragStartMouseY) * sensitivity;
        SliderSetting xSetting = this.draggingArm == class_1306.field_6183 ? hands.rx : hands.lx;
        SliderSetting ySetting = this.draggingArm == class_1306.field_6183 ? hands.ry : hands.ly;
        xSetting.set(Float.valueOf(class_3532.method_15363((float)valueX, (float)xSetting.min, (float)xSetting.max)));
        ySetting.set(Float.valueOf(class_3532.method_15363((float)valueY, (float)ySetting.min, (float)ySetting.max)));
    }

    private class_1306 findDragArm(float mouseX, float mouseY) {
        class_1306 arm = this.findHoveredArm(mouseX, mouseY);
        if (arm == null && mouseY >= this.screenHeight * 0.32f) {
            arm = mouseX < this.screenWidth / 2.0f ? class_1306.field_6182 : class_1306.field_6183;
        }
        return arm;
    }

    private float clampAndRound(float value, SliderSetting setting) {
        return class_3532.method_15363((float)MathUtil.round(value, setting.increment), (float)setting.min, (float)setting.max);
    }

    public boolean isAutoSwingPreview() {
        return this.active && this.autoSwingPreview;
    }

    private void updateAutoSwingPreview() {
        if (!this.autoSwingPreview) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1724 == null || client.field_1724.field_6252) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now < this.nextAutoSwingAt) {
            return;
        }
        class_1268 rightHand = client.field_1724.method_6068() == class_1306.field_6183 ? class_1268.field_5808 : class_1268.field_5810;
        client.field_1724.method_23667(rightHand, false);
        this.nextAutoSwingAt = now + 500L;
    }

    private class_1306 findHoveredArm(float mouseX, float mouseY) {
        if (this.isOverControls(mouseX, mouseY) || this.screenWidth <= 0.0f || this.screenHeight <= 0.0f) {
            return null;
        }
        Rect left = this.controlRect(class_1306.field_6182);
        Rect right = this.controlRect(class_1306.field_6183);
        boolean overLeft = left.contains(mouseX, mouseY);
        boolean overRight = right.contains(mouseX, mouseY);
        if (overLeft && overRight) {
            float rightDistance;
            float leftDistance = left.distanceSquaredToCenter(mouseX, mouseY);
            return leftDistance <= (rightDistance = right.distanceSquaredToCenter(mouseX, mouseY)) ? class_1306.field_6182 : class_1306.field_6183;
        }
        if (overLeft) {
            return class_1306.field_6182;
        }
        if (overRight) {
            return class_1306.field_6183;
        }
        if (mouseY >= this.screenHeight * 0.32f) {
            return mouseX < this.screenWidth / 2.0f ? class_1306.field_6182 : class_1306.field_6183;
        }
        return null;
    }

    private Rect controlRect(class_1306 arm) {
        Hands hands = Hands.get();
        float valueX = 0.0f;
        float valueY = 0.0f;
        float scale = 1.0f;
        if (hands != null) {
            valueX = (arm == class_1306.field_6183 ? hands.rx.getValue() : hands.lx.getValue()).floatValue();
            valueY = (arm == class_1306.field_6183 ? hands.ry.getValue() : hands.ly.getValue()).floatValue();
            scale = (arm == class_1306.field_6183 ? hands.rScale.getValue() : hands.lScale.getValue()).floatValue();
        }
        float pixelsPerUnit = this.dragPixelsPerUnit();
        float baseX = this.screenWidth * (arm == class_1306.field_6182 ? 0.16f : 0.84f);
        float baseY = this.screenHeight * 0.78f;
        float baseSize = class_3532.method_15363((float)(Math.min(this.screenWidth, this.screenHeight) * 0.38f), (float)150.0f, (float)240.0f);
        float size = class_3532.method_15363((float)(baseSize * (0.88f + scale * 0.12f)), (float)145.0f, (float)275.0f);
        float centerX = baseX + valueX * pixelsPerUnit;
        float centerY = baseY - valueY * pixelsPerUnit;
        centerX = class_3532.method_15363((float)centerX, (float)(size / 2.0f + 8.0f), (float)(this.screenWidth - size / 2.0f - 8.0f));
        centerY = class_3532.method_15363((float)centerY, (float)(size / 2.0f + 52.0f), (float)(this.screenHeight - size / 2.0f - 54.0f));
        return new Rect(centerX - size / 2.0f, centerY - size / 2.0f, size, size);
    }

    private float dragPixelsPerUnit() {
        return Math.max(1.0f, this.screenHeight / 1.05f);
    }

    private boolean isOverControls(float mouseX, float mouseY) {
        return this.saveButton.contains(mouseX, mouseY) || this.resetAllButton.contains(mouseX, mouseY) || this.resetLeftButton.contains(mouseX, mouseY) || this.resetRightButton.contains(mouseX, mouseY) || this.autoSwingBounds.contains(mouseX, mouseY) || this.swingPanel.bounds().contains(mouseX, mouseY) || this.glassPanel.bounds().contains(mouseX, mouseY);
    }

    private void updateHover(Animation animation, boolean hovered) {
        animation.update();
        animation.run(hovered ? 1.0 : 0.0, 0.18f, Easings.QUAD_OUT, true);
    }

    public void updateHandBounds(class_1306 arm, class_4587 matrices) {
        if (!this.active || matrices == null) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1773 == null || client.method_22683() == null) {
            return;
        }
        Matrix4f position = new Matrix4f((Matrix4fc)matrices.method_23760().method_23761());
        Matrix4f modelView = new Matrix4f((Matrix4fc)RenderSystem.getModelViewMatrix());
        Matrix4f projection = client.field_1773.method_22973(70.0f);
        float width = (float)client.method_22683().method_4489() / 2.0f;
        float height = (float)client.method_22683().method_4506() / 2.0f;
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        int projected = 0;
        Vector4f point = new Vector4f();
        for (float[] sample : HAND_SAMPLE_POINTS) {
            point.set(sample[0], sample[1], sample[2], 1.0f);
            point.mul((Matrix4fc)position);
            point.mul((Matrix4fc)modelView);
            point.mul((Matrix4fc)projection);
            if (point.w() <= 0.001f || !Float.isFinite(point.w())) continue;
            float ndcX = point.x() / point.w();
            float ndcY = point.y() / point.w();
            if (!Float.isFinite(ndcX) || !Float.isFinite(ndcY)) continue;
            float sx = (ndcX * 0.5f + 0.5f) * width;
            float sy = (0.5f - ndcY * 0.5f) * height;
            minX = Math.min(minX, sx);
            minY = Math.min(minY, sy);
            maxX = Math.max(maxX, sx);
            maxY = Math.max(maxY, sy);
            ++projected;
        }
        if (projected < 2) {
            return;
        }
        float centerX = (minX + maxX) / 2.0f;
        float centerY = (minY + maxY) / 2.0f;
        float boundsWidth = class_3532.method_15363((float)(maxX - minX + 34.0f), (float)76.0f, (float)260.0f);
        float boundsHeight = class_3532.method_15363((float)(maxY - minY + 42.0f), (float)90.0f, (float)280.0f);
        if (!Float.isFinite(centerX) || !Float.isFinite(centerY)) {
            return;
        }
        this.boundsFor(arm).set(centerX, centerY, boundsWidth, boundsHeight);
    }

    public void captureBeforeHands() {
        if (!this.active) {
            return;
        }
        GlassHandsRenderer renderer = GlassHandsRenderer.getInstance();
        renderer.setKeepHandsResult(true);
        GlassHands glassHands = GlassHands.getInstance();
        if (glassHands != null && glassHands.isEnabled()) {
            renderer.setEnabled(true);
            glassHands.applyRendererSettings();
        } else {
            this.configureEditorOutline(renderer);
        }
        renderer.captureSceneBeforeHands();
    }

    public void captureAfterHands() {
        if (!this.active) {
            return;
        }
        GlassHandsRenderer renderer = GlassHandsRenderer.getInstance();
        renderer.captureSceneAfterHands();
        renderer.renderGlassEffect();
    }

    private void configureEditorOutline(GlassHandsRenderer renderer) {
        renderer.setEnabled(true);
        renderer.setBlurEnabled(false);
        renderer.setSaturation(1.0f);
        renderer.setReflect(false);
        renderer.setTintColor(0);
        renderer.setTintIntensity(0.0f);
        renderer.setIceIntensity(0.0f);
        renderer.setSmokeAmount(0.0f);
        renderer.setOutlineEnabled(true);
        renderer.setOutlineGlowStrength(this.hoveredArm == null ? 12.0f : 20.0f);
        renderer.setOutlineWidth(1);
        renderer.setOutlineGlowMode(2);
        renderer.setOutlineFillColor(0);
        renderer.setOutlineColor(ColorUtil.getClientColor1(1));
        renderer.setShimmerEnabled(true);
        renderer.setShimmerWidth(0.045f);
        renderer.setShimmerPeriodSec(2.8f);
    }

    private void restoreGlassHandsRenderer() {
        GlassHandsRenderer renderer = GlassHandsRenderer.getInstance();
        renderer.setKeepHandsResult(false);
        GlassHands glassHands = GlassHands.getInstance();
        if (glassHands != null && glassHands.isEnabled()) {
            renderer.setEnabled(true);
            glassHands.applyRendererSettings();
        } else {
            renderer.setEnabled(false);
        }
    }

    private HandBounds boundsFor(class_1306 arm) {
        return arm == class_1306.field_6183 ? this.rightBounds : this.leftBounds;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class HandBounds {
        private float centerX;
        private float centerY;
        private float width;
        private float height;
        private long updatedAt;

        private HandBounds() {
        }

        void set(float centerX, float centerY, float width, float height) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
            this.updatedAt = System.currentTimeMillis();
        }

        void invalidate() {
            this.updatedAt = 0L;
        }

        Rect freshRect() {
            boolean fresh = this.updatedAt > 0L && System.currentTimeMillis() - this.updatedAt <= 250L;
            return fresh ? new Rect(this.centerX - this.width / 2.0f, this.centerY - this.height / 2.0f, this.width, this.height) : null;
        }
    }
}

