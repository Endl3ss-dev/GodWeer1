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
import swerve.utils.render.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;

import org.lwjgl.glfw.GLFW;

import java.util.Locale;

// Панель-«таблетка» вверху экрана: [Сила взмаха] | [Скорость] | [Анимация] | [Сброс].
final class HandSwingPanel {

    private static final String RESET = "Сброс";
    private static final String ANIMATION = "Анимация";

    private static final float HEIGHT = 26.0F;
    private static final float RADIUS = HEIGHT * 0.5F;
    private static final float PAD_X = 14.0F;
    private static final float GAP = 12.0F;
    private static final float SLIDER_WIDTH = 92.0F;
    private static final float TOP_MARGIN = 6.0F;
    private static final float SLIDE_DISTANCE = 12.0F;
    private static final float LABEL_SIZE = 5.5F;
    private static final float VALUE_SIZE = 6.5F;
    private static final float LABEL_Y = 4.5F;
    private static final float TRACK_Y = 16.5F;
    private static final float TRACK_HEIGHT = 3.0F;
    private static final float KNOB = 6.5F;
    private static final float SEPARATOR_INSET = 7.0F;
    private static final float MENU_GAP = 4.0F;

    private final HandAnimationMenu menu;
    private final AnimationUtils openAnim = new AnimationUtils(0.0F, 8.0F, Easings.CUBIC_OUT);
    private final Slider strengthSlider = new Slider("Сила взмаха", "");
    private final Slider speedSlider = new Slider("Скорость", "x");

    private boolean open;
    private boolean layoutReady;
    private InteractionHand hand = InteractionHand.MAIN_HAND;
    private Slider activeSlider;

    private float width;
    private float animationWidth;
    private float resetWidth;
    private float animationLabelWidth;
    private float x;
    private float y;
    private float strengthX;
    private float speedX;
    private float animationX;
    private float resetX;

    HandSwingPanel(HandAnimationMenu menu) {
        this.menu = menu;
    }

    void open(InteractionHand hand) {
        this.hand = hand;
        if (!this.open) {
            this.activeSlider = null;
        }
        this.open = true;
    }

    void close() {
        this.open = false;
        this.activeSlider = null;
    }

    void reset() {
        close();
        this.openAnim.setValue(0.0F);
    }

    boolean isOpen() {
        return this.open;
    }

    boolean isVisible() {
        return this.open || this.openAnim.getValue() > 0.01F;
    }

    boolean contains(float mouseX, float mouseY) {
        return this.open && this.layoutReady
                && mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + HEIGHT;
    }

    void mouseClicked(float mouseX, float mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }
        if (mouseX >= this.resetX - GAP * 0.5F) {
            ViewModel module = ViewModel.get();
            if (module != null) {
                module.resetHand(this.hand);
            }
            return;
        }
        if (mouseX >= this.animationX - GAP * 0.5F) {
            this.menu.open(this.animationX - 2.0F, this.y + HEIGHT + MENU_GAP - 2.0F);
            return;
        }
        Slider slider = sliderAt(mouseX);
        if (slider == null) {
            return;
        }
        SwingAnimation module = SwingAnimation.get();
        if (module != null && !module.isEnable()) {
            module.setEnabled(true);
        }
        this.activeSlider = slider;
        slider.dragTo(mouseX, sliderX(slider));
    }

    void mouseReleased() {
        this.activeSlider = null;
    }

    void mouseScrolled(float mouseX, double amount) {
        Slider slider = sliderAt(mouseX);
        if (slider != null && amount != 0.0) {
            slider.step(amount > 0.0 ? 1 : -1);
        }
    }

    void render(PoseStack stack, float mouseX, float mouseY, float delta) {
        this.openAnim.update(this.open ? 1.0F : 0.0F);
        float alpha = this.openAnim.getValue();
        if (alpha <= 0.01F) {
            return;
        }
        SwingAnimation module = SwingAnimation.get();
        if (module == null || !layout()) {
            return;
        }

        this.strengthSlider.bind(module.getStrength());
        this.speedSlider.bind(module.getSpeed());
        if (this.activeSlider != null) {
            this.activeSlider.dragTo(mouseX, sliderX(this.activeSlider));
        }

        Window window = Minecraft.getInstance().getWindow();
        float guiWidth = window.getWidth() / (float) window.getGuiScale();
        this.x = (guiWidth - this.width) * 0.5F;
        this.y = TOP_MARGIN - SLIDE_DISTANCE * (1.0F - alpha);
        computeColumns();

        int accent = ThemeManager.getColor();
        boolean inside = this.open && mouseY >= this.y && mouseY <= this.y + HEIGHT && mouseX >= this.x && mouseX <= this.x + this.width;
        Slider hoveredSlider = inside && this.activeSlider == null ? sliderAt(mouseX) : null;
        boolean animationHovered = inside && mouseX >= this.animationX - GAP * 0.5F && mouseX < this.resetX - GAP * 0.5F;
        boolean resetHovered = inside && mouseX >= this.resetX - GAP * 0.5F;

        GuiRender.roundedWithOutline(stack, this.x, this.y, this.width, HEIGHT, 0.8F,
                ColorUtils.multAlpha(HandEditor.BACKGROUND, alpha), ColorUtils.multAlpha(HandEditor.OUTLINE, alpha), RADIUS);

        this.strengthSlider.render(stack, this.strengthX, this.y, accent, alpha, delta, hoveredSlider == this.strengthSlider || this.activeSlider == this.strengthSlider);
        separator(stack, this.speedX - GAP * 0.5F, alpha);
        this.speedSlider.render(stack, this.speedX, this.y, accent, alpha, delta, hoveredSlider == this.speedSlider || this.activeSlider == this.speedSlider);
        separator(stack, this.animationX - GAP * 0.5F, alpha);

        GuiFonts.SF_MEDIUM.draw(stack, ANIMATION, this.animationX + (this.animationWidth - this.animationLabelWidth) * 0.5F,
                this.y + LABEL_Y, ColorUtils.multAlpha(animationHovered ? HandEditor.WHITE : HandEditor.MUTED, alpha), LABEL_SIZE);
        String current = module.getType().getCurrent();
        float currentWidth = GuiFonts.SF_MEDIUM.getWidth(current, VALUE_SIZE);
        GuiFonts.SF_MEDIUM.draw(stack, current, this.animationX + (this.animationWidth - currentWidth) * 0.5F,
                this.y + TRACK_Y - 3.5F, ColorUtils.multAlpha(animationHovered ? HandEditor.WHITE : accent, alpha), VALUE_SIZE);
        separator(stack, this.resetX - GAP * 0.5F, alpha);

        if (resetHovered) {
            GuiRender.roundedRect(stack, this.resetX - GAP * 0.5F + 2.0F, this.y + 3.0F,
                    this.x + this.width - (this.resetX - GAP * 0.5F) - 5.0F, HEIGHT - 6.0F, (HEIGHT - 6.0F) * 0.5F,
                    ColorUtils.multAlpha(HandEditor.HOVER, alpha));
        }
        GuiFonts.SF_MEDIUM.draw(stack, RESET, this.resetX, this.y + (HEIGHT - VALUE_SIZE) * 0.5F - 0.5F,
                ColorUtils.multAlpha(resetHovered ? HandEditor.WHITE : HandEditor.MUTED, alpha), VALUE_SIZE);
    }

    private void separator(PoseStack stack, float separatorX, float alpha) {
        GuiRender.roundedRect(stack, separatorX - 0.3F, this.y + SEPARATOR_INSET, 0.6F, HEIGHT - SEPARATOR_INSET * 2.0F, 0.3F,
                ColorUtils.multAlpha(HandEditor.OUTLINE, alpha));
    }

    // Ширины текста считаются один раз: шрифт может быть не готов в первые кадры, поэтому ждём ненулевую ширину.
    private boolean layout() {
        if (this.layoutReady) {
            return true;
        }
        float widest = 0.0F;
        String[] modes = SwingAnimation.MODES;
        for (int i = 0; i < modes.length; i++) {
            widest = Math.max(widest, GuiFonts.SF_MEDIUM.getWidth(modes[i], VALUE_SIZE));
        }
        this.animationLabelWidth = GuiFonts.SF_MEDIUM.getWidth(ANIMATION, LABEL_SIZE);
        this.resetWidth = GuiFonts.SF_MEDIUM.getWidth(RESET, VALUE_SIZE);
        if (widest <= 0.0F || this.animationLabelWidth <= 0.0F || this.resetWidth <= 0.0F) {
            return false;
        }
        this.animationWidth = Math.max(widest, this.animationLabelWidth);
        this.width = PAD_X * 2.0F + SLIDER_WIDTH * 2.0F + this.animationWidth + this.resetWidth + GAP * 3.0F;
        this.layoutReady = true;
        return true;
    }

    private void computeColumns() {
        this.strengthX = this.x + PAD_X;
        this.speedX = this.strengthX + SLIDER_WIDTH + GAP;
        this.animationX = this.speedX + SLIDER_WIDTH + GAP;
        this.resetX = this.animationX + this.animationWidth + GAP;
    }

    private Slider sliderAt(float mouseX) {
        if (mouseX < this.speedX - GAP * 0.5F) {
            return this.strengthSlider;
        }
        if (mouseX < this.animationX - GAP * 0.5F) {
            return this.speedSlider;
        }
        return null;
    }

    private float sliderX(Slider slider) {
        return slider == this.strengthSlider ? this.strengthX : this.speedX;
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

        private void dragTo(float mouseX, float trackX) {
            if (this.setting == null) {
                return;
            }
            float ratio = Mth.clamp((mouseX - trackX) / SLIDER_WIDTH, 0.0F, 1.0F);
            float raw = this.setting.getMin() + (this.setting.getMax() - this.setting.getMin()) * ratio;
            float increment = this.setting.getIncrement();
            this.setting.setValue(increment > 0.0F ? Math.round(raw / increment) * increment : raw);
        }

        private void step(int direction) {
            if (this.setting != null) {
                this.setting.setValue(this.setting.get() + direction * this.setting.getIncrement());
            }
        }

        private void render(PoseStack stack, float trackX, float top, int accent, float alpha, float delta, boolean hovered) {
            if (this.setting == null) {
                return;
            }
            float value = this.setting.get();
            if (value != this.cachedValue || this.valueWidth <= 0.0F) {
                this.cachedValue = value;
                this.valueText = String.format(Locale.ROOT, "%.2f", value) + this.suffix;
                this.valueWidth = GuiFonts.SF_MEDIUM.getWidth(this.valueText, LABEL_SIZE);
            }

            float range = this.setting.getMax() - this.setting.getMin();
            float ratio = range > 0.0F ? (value - this.setting.getMin()) / range : 0.0F;
            if (this.displayRatio < 0.0F) {
                this.displayRatio = ratio;
            } else {
                this.displayRatio += (ratio - this.displayRatio) * (1.0F - (float) Math.exp(-20.0F * delta));
            }

            float textY = top + LABEL_Y;
            GuiFonts.SF_MEDIUM.draw(stack, this.label, trackX, textY,
                    ColorUtils.multAlpha(hovered ? HandEditor.WHITE : HandEditor.MUTED, alpha), LABEL_SIZE);
            GuiFonts.SF_MEDIUM.draw(stack, this.valueText, trackX + SLIDER_WIDTH - this.valueWidth, textY,
                    ColorUtils.multAlpha(HandEditor.WHITE, alpha), LABEL_SIZE);

            float trackY = top + TRACK_Y;
            GuiRender.roundedRect(stack, trackX, trackY, SLIDER_WIDTH, TRACK_HEIGHT, TRACK_HEIGHT * 0.5F,
                    ColorUtils.multAlpha(HandEditor.TRACK, alpha));
            float fillWidth = SLIDER_WIDTH * this.displayRatio;
            if (fillWidth > 0.5F) {
                GuiRender.roundedRect(stack, trackX, trackY, fillWidth, TRACK_HEIGHT, TRACK_HEIGHT * 0.5F,
                        ColorUtils.multAlpha(accent, alpha));
            }
            RenderUtils.drawRoundCircle(stack, trackX + fillWidth, trackY + TRACK_HEIGHT * 0.5F, KNOB,
                    ColorUtils.multAlpha(HandEditor.WHITE, alpha));
        }
    }
}
