package swerve.ui.hud;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.managers.ThemeManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.ui.newgui.GuiRender;
import swerve.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import org.lwjgl.glfw.GLFW;

// Базовый HUD-элемент: позиция (можно привязать к правому/нижнему краю), сглаживание,
// перетаскивание мышью, масштаб и анимация появления.
//
// Контракт наследника:
//   measure()        — задать размер через setSize(w, h) ДО отрисовки (кэш из тика, без вычислений строк);
//   renderContent()  — рисовать в локальных координатах, (0,0) = левый верхний угол элемента.
public abstract class HudElement extends Module {

    private static final float MARGIN = 2.0f;
    private static final float POSITION_SMOOTHING = 18.0f;
    private static final float APPEAR_SPEED = 12.0f;
    private static final float APPEAR_EPSILON = 0.01f;
    private static final float DRAG_OUTLINE_OFFSET = 2.0f;
    private static final float DRAG_OUTLINE_RADIUS = 4.0f;
    private static final float MAX_FRAME_DELTA = 0.05f;

    private final FloatSetting scale = new FloatSetting("Масштаб", 1.0f, 0.5f, 2.0f, 0.05f);
    // По умолчанию перетаскивание только с ALT: иначе ЛКМ в бою случайно сдвигал бы HUD.
    private final BooleanSetting altDrag = new BooleanSetting("Драг по ALT", true);
    private final BooleanSetting resetPosition = new BooleanSetting("Сбросить позицию", false);

    private final float defaultXFraction;
    private final float defaultYFraction;
    private final boolean draggable;

    // anchorRight — targetX хранится как правый край, anchorBottom — targetY как нижний.
    protected boolean anchorRight = true;
    protected boolean anchorBottom;

    private float targetX;
    private float targetY;
    private float renderX;
    private float renderY;
    private boolean positioned;
    private boolean snapPosition;

    private float contentWidth;
    private float contentHeight;
    private float frameDelta = 1.0f / 60.0f;
    private long lastFrameNanos;
    private float appear;
    private boolean hasRenderPosition;

    private boolean dragging;
    private float grabOffsetX;
    private float grabOffsetY;

    protected HudElement(String name, String description, float defaultXFraction, float defaultYFraction) {
        super(name, description, ModuleCategory.INTERFACE);
        this.defaultXFraction = defaultXFraction;
        this.defaultYFraction = defaultYFraction;
        this.draggable = true;
        addSettings(scale, altDrag, resetPosition);
        HudManager.register(this);
    }

    // Для элементов, которые позиционируются по углу экрана сами: драг и сохранение позиции не нужны.
    protected HudElement(String name, String description) {
        super(name, description, ModuleCategory.INTERFACE);
        this.defaultXFraction = 0.0f;
        this.defaultYFraction = 0.0f;
        this.draggable = false;
        addSettings(scale);
        HudManager.register(this);
    }

    protected abstract void measure();

    protected abstract void renderContent(PoseStack stack, float alpha, float delta);

    protected final void setSize(float width, float height) {
        this.contentWidth = width;
        this.contentHeight = height;
    }

    protected final float getFrameDelta() {
        return this.frameDelta;
    }

    protected final void setTargetPosition(float x, float y) {
        this.targetX = x;
        this.targetY = y;
    }

    // Смена якоря смещает отсчёт на ширину/высоту элемента — чтобы не было рывка,
    // в первом кадре после смены сглаживание пропускается.
    protected final void setAnchor(boolean right, boolean bottom) {
        if (this.anchorRight == right && this.anchorBottom == bottom) {
            return;
        }
        this.anchorRight = right;
        this.anchorBottom = bottom;
        this.snapPosition = true;
    }

    public final void renderElement(PoseStack stack) {
        updateFrameDelta();

        float targetAppear = isEnable() ? 1.0f : 0.0f;
        this.appear = approach(this.appear, targetAppear, this.frameDelta, APPEAR_SPEED);
        if (targetAppear == 0.0f && this.appear <= APPEAR_EPSILON) {
            this.appear = 0.0f;
            this.dragging = false;
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ensurePositioned(minecraft);
        measure();
        applyPositionReset(minecraft);

        float scaleValue = getScaleValue();
        float drawnWidth = this.contentWidth * scaleValue;
        float drawnHeight = this.contentHeight * scaleValue;
        clampTarget(minecraft, drawnWidth, drawnHeight);
        smoothPosition();

        float drawX = this.anchorRight ? this.renderX - drawnWidth : this.renderX;
        float drawY = this.anchorBottom ? this.renderY - drawnHeight : this.renderY;
        float alpha = smoothStep(this.appear);

        stack.pushPose();
        stack.translate(drawX, drawY, 0.0f);
        stack.scale(scaleValue, scaleValue, 1.0f);
        renderContent(stack, alpha, this.frameDelta);
        stack.popPose();

        if (this.dragging) {
            GuiRender.roundedOutline(stack,
                    drawX - DRAG_OUTLINE_OFFSET, drawY - DRAG_OUTLINE_OFFSET,
                    drawnWidth + DRAG_OUTLINE_OFFSET * 2.0f, drawnHeight + DRAG_OUTLINE_OFFSET * 2.0f,
                    1.0f, ColorUtils.multAlpha(ThemeManager.getColor(), 0.85f * this.appear), DRAG_OUTLINE_RADIUS);
        }
    }

    public final boolean hitTest(double mouseX, double mouseY) {
        if (!this.draggable || !isEnable() || this.appear < 0.5f) {
            return false;
        }

        float scaleValue = getScaleValue();
        float drawnWidth = this.contentWidth * scaleValue;
        float drawnHeight = this.contentHeight * scaleValue;
        if (drawnWidth <= 0.0f || drawnHeight <= 0.0f) {
            return false;
        }

        float drawX = this.anchorRight ? this.renderX - drawnWidth : this.renderX;
        float drawY = this.anchorBottom ? this.renderY - drawnHeight : this.renderY;
        return mouseX >= drawX - 1.0f && mouseX <= drawX + drawnWidth + 1.0f
                && mouseY >= drawY - 1.0f && mouseY <= drawY + drawnHeight + 1.0f;
    }

    public final boolean acceptsDrag(Minecraft minecraft) {
        if (!this.draggable || !isEnable()) {
            return false;
        }
        if (!this.altDrag.get()) {
            return true;
        }

        long window = minecraft.getWindow().getWindow();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
    }

    public final void beginDrag(double mouseX, double mouseY) {
        this.dragging = true;
        this.grabOffsetX = (float) mouseX - this.targetX;
        this.grabOffsetY = (float) mouseY - this.targetY;
    }

    // Ведём элемент ровно под курсором: сглаживание в этом кадре не применяется.
    public final void drag(Minecraft minecraft, double mouseX, double mouseY) {
        if (!this.dragging) {
            return;
        }

        this.targetX = (float) mouseX - this.grabOffsetX;
        this.targetY = (float) mouseY - this.grabOffsetY;

        float scaleValue = getScaleValue();
        clampTarget(minecraft, this.contentWidth * scaleValue, this.contentHeight * scaleValue);
        this.renderX = this.targetX;
        this.renderY = this.targetY;
    }

    public final void endDrag() {
        if (!this.dragging) {
            return;
        }
        this.dragging = false;
        HudPositionStore.put(getName(), this.targetX, this.targetY);
        HudPositionStore.save();
    }

    public final boolean isDragging() {
        return this.dragging;
    }

    public final float getScaleValue() {
        return this.scale.get() * (0.97f + 0.03f * this.appear);
    }

    // Текущая позиция отрисовки (при правом/нижнем якоре — правый/нижний край).
    // Нужна элементам с физикой драга: они считают скорость по смещению между кадрами.
    protected final float getRenderX() {
        return this.renderX;
    }

    protected final float getRenderY() {
        return this.renderY;
    }

    @Override
    public void onDisable() {
        this.dragging = false;
        super.onDisable();
    }

    private void ensurePositioned(Minecraft minecraft) {
        if (this.positioned) {
            return;
        }
        this.positioned = true;

        // У элементов по углу экрана цель выставляет measure(), здесь позиция не нужна.
        if (!this.draggable) {
            return;
        }

        float[] stored = HudPositionStore.get(getName());
        if (stored != null) {
            this.targetX = stored[0];
            this.targetY = stored[1];
        } else {
            this.targetX = minecraft.getWindow().getGuiScaledWidth() * this.defaultXFraction;
            this.targetY = minecraft.getWindow().getGuiScaledHeight() * this.defaultYFraction;
        }
        this.renderX = this.targetX;
        this.renderY = this.targetY;
    }

    private void applyPositionReset(Minecraft minecraft) {
        if (!this.draggable || !this.resetPosition.get()) {
            return;
        }

        this.resetPosition.setState(false);
        HudPositionStore.remove(getName());
        this.targetX = minecraft.getWindow().getGuiScaledWidth() * this.defaultXFraction;
        this.targetY = minecraft.getWindow().getGuiScaledHeight() * this.defaultYFraction;
        this.snapPosition = true;
    }

    private void clampTarget(Minecraft minecraft, float drawnWidth, float drawnHeight) {
        float screenWidth = minecraft.getWindow().getGuiScaledWidth();
        float screenHeight = minecraft.getWindow().getGuiScaledHeight();

        if (this.anchorRight) {
            float minX = Math.min(drawnWidth + MARGIN, screenWidth - MARGIN);
            this.targetX = Mth.clamp(this.targetX, minX, screenWidth - MARGIN);
        } else {
            this.targetX = Mth.clamp(this.targetX, MARGIN, Math.max(MARGIN, screenWidth - drawnWidth - MARGIN));
        }

        if (this.anchorBottom) {
            float minY = Math.min(drawnHeight + MARGIN, screenHeight - MARGIN);
            this.targetY = Mth.clamp(this.targetY, minY, screenHeight - MARGIN);
        } else {
            this.targetY = Mth.clamp(this.targetY, MARGIN, Math.max(MARGIN, screenHeight - drawnHeight - MARGIN));
        }
    }

    private void smoothPosition() {
        if (this.snapPosition || !this.hasRenderPosition) {
            this.snapPosition = false;
            this.hasRenderPosition = true;
            this.renderX = this.targetX;
            this.renderY = this.targetY;
            return;
        }

        this.renderX = approach(this.renderX, this.targetX, this.frameDelta, POSITION_SMOOTHING);
        this.renderY = approach(this.renderY, this.targetY, this.frameDelta, POSITION_SMOOTHING);
    }

    private void updateFrameDelta() {
        long now = System.nanoTime();
        if (this.lastFrameNanos == 0L) {
            this.lastFrameNanos = now;
            this.frameDelta = 1.0f / 60.0f;
            return;
        }

        this.frameDelta = Mth.clamp((now - this.lastFrameNanos) / 1_000_000_000.0f, 0.0f, MAX_FRAME_DELTA);
        this.lastFrameNanos = now;
    }

    private static float approach(float current, float target, float delta, float speed) {
        return current + (target - current) * (1.0f - (float) Math.exp(-delta * speed));
    }

    private static float smoothStep(float value) {
        float clamped = Mth.clamp(value, 0.0f, 1.0f);
        return clamped * clamped * (3.0f - 2.0f * clamped);
    }
}
