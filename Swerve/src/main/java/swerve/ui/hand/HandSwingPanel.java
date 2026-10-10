package swerve.ui.hand;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

import swerve.managers.ThemeManager;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.impl.render.SwingAnimation;
import swerve.modules.impl.render.ViewModel;
import swerve.ui.newgui.GuiFonts;
import swerve.ui.newgui.GuiRender;
import swerve.utils.animation.AnimationUtils;
import swerve.utils.animation.Easings;
import swerve.utils.color.ColorUtils;
import swerve.utils.render.HandProjection;
import swerve.utils.render.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;

import org.lwjgl.glfw.GLFW;

import java.util.Locale;

final class HandSwingPanel {

    private static final String TITLE = "Взмах";
    private static final String RESET = "Сброс";
    private static final String ANIMATION = "Анимация";

    private static final float WIDTH = 158.0F;
    private static final float PADDING = 8.0F;
    private static final float HEADER = 16.0F;
    private static final float ROW = 24.0F;
    private static final float ANIM_ROW = 17.0F;
    private static final float HEIGHT = PADDING + HEADER + ROW * 2.0F + ANIM_ROW + PADDING * 0.5F;
    private static final float RADIUS = 6.0F;
    private static final float TITLE_SIZE = 7.5F;
    private static final float TEXT_SIZE = 6.5F;
    private static final float TRACK_HEIGHT = 3.0F;
    private static final float TRACK_OFFSET = 15.0F;
    private static final float KNOB = 7.0F;
    private static final float HAND_GAP = 14.0F;
    private static final float SCREEN_MARGIN = 4.0F;
    private static final float FOLLOW_SPEED = 18.0F;

    private final HandAnimationMenu menu;
    private final AnimationUtils openAnim = new AnimationUtils(0.0F, 8.0F, Easings.CUBIC_OUT);
    private final Slider strengthSlider = new Slider("Сила взмаха", "");
    private final Slider speedSlider = new Slider("Скорость", "x");

    private boolean open;
    private boolean placed;
    private InteractionHand hand = InteractionHand.MAIN_HAND;
    private Slider activeSlider;

    private float anchorDX;
    private float anchorDY;
    private float fallbackX;
    private float fallbackY;
    private float targetX;
    private float targetY;
    private float x;
    private float y;
    private float resetWidth;

    HandSwingPanel(HandAnimationMenu menu) {
        this.menu = menu;
    }

    // Якорь задаётся смещением точки клика от пивота руки: панель едет вместе с рукой при перетаскивании.
    void open(InteractionHand hand, float mouseX, float mouseY) {
        this.hand = hand;
        this.activeSlider = null;
        if (HandProjection.isPivotValid(hand)) {
            this.anchorDX = mouseX - HandProjection.pivotX(hand);
            this.anchorDY = mouseY - HandProjection.pivotY(hand);
        } else {
            this.anchorDX = 0.0F;
            this.anchorDY = 0.0F;
        }
        this.fallbackX = mouseX;
        this.fallbackY = mouseY;
        this.placed = isVisible();
        this.open = true;
        computeTarget();
        if (!this.placed) {
            this.x = this.targetX;
            this.y = this.targetY;
            this.placed = true;
        }
    }

    void close() {
        this.open = false;
        this.activeSlider = null;
    }

    void reset() {
        close();
        this.placed = false;
        this.openAnim.setValue(0.0F);
    }

    boolean isOpen() {
        return this.open;
    }

    boolean isVisible() {
        return this.open || this.openAnim.getValue() > 0.01F;
    }

    boolean contains(float mouseX, float mouseY) {
        return this.open && mouseX >= this.x && mouseX <= this.x + WIDTH && mouseY >= this.y && mouseY <= this.y + HEIGHT;
    }

    void mouseClicked(float mouseX, float mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }
        if (isResetHovered(mouseX, mouseY)) {
            ViewModel module = ViewModel.get();
            if (module != null) {
                module.resetHand(this.hand);
            }
            return;
        }
        Slider slider = sliderAt(mouseY);
        if (slider != null) {
            SwingAnimation module = SwingAnimation.get();
            if (module != null && !module.isEnable()) {
                module.setEnabled(true);
            }
            this.activeSlider = slider;
            slider.dragTo(mouseX, this.x + PADDING, WIDTH - PADDING * 2.0F);
            return;
        }
        if (isAnimationRowHovered(mouseX, mouseY)) {
            this.menu.open(mouseX, mouseY);
        }
    }

    void mouseReleased() {
        this.activeSlider = null;
    }

    void mouseScrolled(float mouseY, double amount) {
        Slider slider = sliderAt(mouseY);
        if (slider != null && amount != 0.0) {
            slider.step(amount > 0.0 ? 1 : -1);
        }
    }

    void render(PoseStack stack, float mouseX, float mouseY, float delta) {
        this.openAnim.update(this.open ? 1.0F : 0.0F);
        float alpha = this.openAnim.getValue();
        if (alpha <= 0.01F) {
            this.placed = false;
            return;
        }
        SwingAnimation module = SwingAnimation.get();
        if (module == null) {
            return;
        }

        this.strengthSlider.bind(module.getStrength());
        this.speedSlider.bind(module.getSpeed());
        if (this.activeSlider != null) {
            this.activeSlider.dragTo(mouseX, this.x + PADDING, WIDTH - PADDING * 2.0F);
        }

        computeTarget();
        float follow = 1.0F - (float) Math.exp(-FOLLOW_SPEED * delta);
        this.x += (this.targetX - this.x) * follow;
        this.y += (this.targetY - this.y) * follow;

        if (this.resetWidth <= 0.0F) {
            this.resetWidth = GuiFonts.SF_MEDIUM.getWidth(RESET, TEXT_SIZE);
        }

        int accent = ThemeManager.getColor();
        float scale = 0.94F + 0.06F * alpha;
        float centerX = this.x + WIDTH * 0.5F;
        float bottom = this.y + HEIGHT;

        stack.pushPose();
        stack.translate(centerX, bottom, 0.0F);
        stack.scale(scale, scale, 1.0F);
        stack.translate(-centerX, -bottom, 0.0F);

        GuiRender.roundedWithOutline(stack, this.x, this.y, WIDTH, HEIGHT, 0.8F,
                ColorUtils.multAlpha(HandEditor.BACKGROUND, alpha), ColorUtils.multAlpha(HandEditor.OUTLINE, alpha), RADIUS);

        float headerTextY = this.y + PADDING + (HEADER - TITLE_SIZE) * 0.5F - 3.0F;
        GuiFonts.SF_MEDIUM.draw(stack, TITLE, this.x + PADDING, headerTextY, ColorUtils.multAlpha(HandEditor.WHITE, alpha), TITLE_SIZE);
        boolean resetHovered = this.open && isResetHovered(mouseX, mouseY);
        GuiFonts.SF_MEDIUM.draw(stack, RESET, this.x + WIDTH - PADDING - this.resetWidth, headerTextY + 0.5F,
                ColorUtils.multAlpha(resetHovered ? HandEditor.WHITE : HandEditor.MUTED, alpha), TEXT_SIZE);

        float rowTop = this.y + PADDING + HEADER;
        this.strengthSlider.render(stack, this.x, rowTop, accent, alpha, delta, this.open && this.activeSlider == null && mouseY >= rowTop && mouseY < rowTop + ROW && mouseX >= this.x && mouseX <= this.x + WIDTH);
        rowTop += ROW;
        this.speedSlider.render(stack, this.x, rowTop, accent, alpha, delta, this.open && this.activeSlider == null && mouseY >= rowTop && mouseY < rowTop + ROW && mouseX >= this.x && mouseX <= this.x + WIDTH);
        rowTop += ROW;

        boolean animationHovered = this.open && isAnimationRowHovered(mouseX, mouseY);
        if (animationHovered) {
            GuiRender.roundedRect(stack, this.x + 4.0F, rowTop, WIDTH - 8.0F, ANIM_ROW, 4.0F,
                    ColorUtils.multAlpha(HandEditor.HOVER, alpha));
        }
        float animationTextY = rowTop + (ANIM_ROW - TEXT_SIZE) * 0.5F - 0.5F;
        GuiFonts.SF_MEDIUM.draw(stack, ANIMATION, this.x + PADDING, animationTextY, ColorUtils.multAlpha(HandEditor.MUTED, alpha), TEXT_SIZE);
        String current = module.getType().getCurrent();
        float currentWidth = GuiFonts.SF_MEDIUM.getWidth(current, TEXT_SIZE);
        GuiFonts.SF_MEDIUM.draw(stack, current, this.x + WIDTH - PADDING - currentWidth, animationTextY,
                ColorUtils.multAlpha(animationHovered ? HandEditor.WHITE : accent, alpha), TEXT_SIZE);

        stack.popPose();
    }

    private void computeTarget() {
        float anchorX;
        float anchorY;
        if (HandProjection.isPivotValid(this.hand)) {
            anchorX = HandProjection.pivotX(this.hand) + this.anchorDX;
            anchorY = HandProjection.pivotY(this.hand) + this.anchorDY;
            this.fallbackX = anchorX;
            this.fallbackY = anchorY;
        } else {
            anchorX = this.fallbackX;
            anchorY = this.fallbackY;
        }

        Window window = Minecraft.getInstance().getWindow();
        float guiScale = (float) window.getGuiScale();
        float guiWidth = window.getWidth() / guiScale;
        float guiHeight = window.getHeight() / guiScale;

        this.targetX = Mth.clamp(anchorX - WIDTH * 0.5F, SCREEN_MARGIN, Math.max(SCREEN_MARGIN, guiWidth - WIDTH - SCREEN_MARGIN));
        float above = anchorY - HAND_GAP - HEIGHT;
        this.targetY = above >= SCREEN_MARGIN
                ? above
                : Math.min(anchorY + HAND_GAP, Math.max(SCREEN_MARGIN, guiHeight - HEIGHT - SCREEN_MARGIN));
    }

    private Slider sliderAt(float mouseY) {
        float rowTop = this.y + PADDING + HEADER;
        if (mouseY >= rowTop && mouseY < rowTop + ROW) {
            return this.strengthSlider;
        }
        if (mouseY >= rowTop + ROW && mouseY < rowTop + ROW * 2.0F) {
            return this.speedSlider;
        }
        return null;
    }

    private boolean isResetHovered(float mouseX, float mouseY) {
        float right = this.x + WIDTH - PADDING;
        return this.resetWidth > 0.0F
                && mouseX >= right - this.resetWidth - 2.0F && mouseX <= right + 2.0F
                && mouseY >= this.y + 2.0F && mouseY <= this.y + PADDING + HEADER - 2.0F;
    }

    private boolean isAnimationRowHovered(float mouseX, float mouseY) {
        float rowTop = this.y + PADDING + HEADER + ROW * 2.0F;
        return mouseX >= this.x && mouseX <= this.x + WIDTH && mouseY >= rowTop && mouseY <= rowTop + ANIM_ROW;
    }

    private static final class Slider {

        private final String label;
        private final String suffix;

        private FloatSetting setting;
        private float cachedValue = Float.NaN;
        private String valueText = "";
        private float valueWidth;
        private float displayRatio = -1.0F;

        private Slider(String label, String suffix) {
            this.label = label;
            this.suffix = suffix;
        }

        private void bind(FloatSetting setting) {
            this.setting = setting;
        }

        private void dragTo(float mouseX, float trackX, float trackWidth) {
            if (this.setting == null || trackWidth <= 0.0F) {
                return;
            }
            float ratio = Mth.clamp((mouseX - trackX) / trackWidth, 0.0F, 1.0F);
            float raw = this.setting.getMin() + (this.setting.getMax() - this.setting.getMin()) * ratio;
            float increment = this.setting.getIncrement();
            this.setting.setValue(increment > 0.0F ? Math.round(raw / increment) * increment : raw);
        }

        private void step(int direction) {
            if (this.setting != null) {
                this.setting.setValue(this.setting.get() + direction * this.setting.getIncrement());
            }
        }

        private void render(PoseStack stack, float panelX, float rowTop, int accent, float alpha, float delta, boolean hovered) {
            if (this.setting == null) {
                return;
            }
            float value = this.setting.get();
            if (value != this.cachedValue || this.valueWidth <= 0.0F) {
                this.cachedValue = value;
                this.valueText = String.format(Locale.ROOT, "%.2f", value) + this.suffix;
                this.valueWidth = GuiFonts.SF_MEDIUM.getWidth(this.valueText, TEXT_SIZE);
            }

            float range = this.setting.getMax() - this.setting.getMin();
            float ratio = range > 0.0F ? (value - this.setting.getMin()) / range : 0.0F;
            if (this.displayRatio < 0.0F) {
                this.displayRatio = ratio;
            } else {
                this.displayRatio += (ratio - this.displayRatio) * (1.0F - (float) Math.exp(-20.0F * delta));
            }

            float textY = rowTop + 2.0F;
            GuiFonts.SF_MEDIUM.draw(stack, this.label, panelX + PADDING, textY,
                    ColorUtils.multAlpha(hovered ? HandEditor.WHITE : HandEditor.MUTED, alpha), TEXT_SIZE);
            GuiFonts.SF_MEDIUM.draw(stack, this.valueText, panelX + WIDTH - PADDING - this.valueWidth, textY,
                    ColorUtils.multAlpha(HandEditor.WHITE, alpha), TEXT_SIZE);

            float trackX = panelX + PADDING;
            float trackY = rowTop + TRACK_OFFSET;
            float trackWidth = WIDTH - PADDING * 2.0F;
            GuiRender.roundedRect(stack, trackX, trackY, trackWidth, TRACK_HEIGHT, TRACK_HEIGHT * 0.5F,
                    ColorUtils.multAlpha(HandEditor.TRACK, alpha));
            float fillWidth = trackWidth * this.displayRatio;
            if (fillWidth > 0.5F) {
                GuiRender.roundedRect(stack, trackX, trackY, fillWidth, TRACK_HEIGHT, TRACK_HEIGHT * 0.5F,
                        ColorUtils.multAlpha(accent, alpha));
            }
            RenderUtils.drawRoundCircle(stack, trackX + fillWidth, trackY + TRACK_HEIGHT * 0.5F, KNOB,
                    ColorUtils.multAlpha(HandEditor.WHITE, alpha));
        }
    }
}
