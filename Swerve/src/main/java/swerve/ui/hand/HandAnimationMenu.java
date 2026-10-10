package swerve.ui.hand;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

import swerve.managers.ThemeManager;
import swerve.modules.impl.render.SwingAnimation;
import swerve.ui.newgui.GuiFonts;
import swerve.ui.newgui.GuiRender;
import swerve.utils.animation.AnimationUtils;
import swerve.utils.animation.Easings;
import swerve.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import org.lwjgl.glfw.GLFW;

final class HandAnimationMenu {

    private static final String TITLE = "Анимация";
    private static final float ITEM_HEIGHT = 13.0F;
    private static final float TITLE_HEIGHT = 14.0F;
    private static final float PADDING = 4.0F;
    private static final float TEXT_SIZE = 7.0F;
    private static final float TITLE_SIZE = 6.5F;
    private static final float TEXT_INSET = 11.0F;
    private static final float RADIUS = 5.0F;
    private static final float MIN_WIDTH = 92.0F;
    private static final float SCREEN_MARGIN = 4.0F;
    private static final float HEIGHT = PADDING + TITLE_HEIGHT + SwingAnimation.MODES.length * ITEM_HEIGHT + PADDING;

    private final AnimationUtils openAnim = new AnimationUtils(0.0F, 9.0F, Easings.CUBIC_OUT);

    private boolean open;
    private boolean widthReady;
    private float width = MIN_WIDTH;
    private float anchorX;
    private float anchorY;
    private float x;
    private float y;

    void open(float mouseX, float mouseY) {
        this.open = true;
        this.anchorX = mouseX;
        this.anchorY = mouseY;
        layout();
    }

    void close() {
        this.open = false;
    }

    void reset() {
        this.open = false;
        this.openAnim.setValue(0.0F);
    }

    boolean isOpen() {
        return this.open;
    }

    boolean isVisible() {
        return this.open || this.openAnim.getValue() > 0.01F;
    }

    boolean contains(float mouseX, float mouseY) {
        return this.open && mouseX >= this.x && mouseX <= this.x + this.width
                && mouseY >= this.y && mouseY <= this.y + HEIGHT;
    }

    void mouseClicked(float mouseX, float mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }
        int index = indexAt(mouseX, mouseY);
        if (index < 0) {
            return;
        }
        SwingAnimation module = SwingAnimation.get();
        if (module != null) {
            module.select(index);
        }
        close();
    }

    void render(PoseStack stack, float mouseX, float mouseY) {
        this.openAnim.update(this.open ? 1.0F : 0.0F);
        float alpha = this.openAnim.getValue();
        if (alpha <= 0.01F) {
            return;
        }
        SwingAnimation module = SwingAnimation.get();
        if (module == null) {
            return;
        }
        layout();

        int accent = ThemeManager.getColor();
        int selected = module.getType().getIndex();
        int hovered = this.open ? indexAt(mouseX, mouseY) : -1;
        float scale = 0.94F + 0.06F * alpha;

        stack.pushPose();
        stack.translate(this.x, this.y, 0.0F);
        stack.scale(scale, scale, 1.0F);
        stack.translate(-this.x, -this.y, 0.0F);

        GuiRender.roundedWithOutline(stack, this.x, this.y, this.width, HEIGHT, 0.8F,
                ColorUtils.multAlpha(HandEditor.BACKGROUND, alpha), ColorUtils.multAlpha(HandEditor.OUTLINE, alpha), RADIUS);
        GuiFonts.SF_MEDIUM.draw(stack, TITLE, this.x + 8.0F, this.y + PADDING + (TITLE_HEIGHT - TITLE_SIZE) * 0.5F - 1.0F,
                ColorUtils.multAlpha(HandEditor.MUTED, alpha), TITLE_SIZE);
        GuiRender.roundedRect(stack, this.x + 6.0F, this.y + PADDING + TITLE_HEIGHT - 1.5F, this.width - 12.0F, 0.6F, 0.3F,
                ColorUtils.multAlpha(HandEditor.OUTLINE, alpha));

        float top = this.y + PADDING + TITLE_HEIGHT;
        String[] modes = SwingAnimation.MODES;
        for (int i = 0; i < modes.length; i++) {
            float itemY = top + i * ITEM_HEIGHT;
            if (i == hovered) {
                GuiRender.roundedRect(stack, this.x + 3.0F, itemY + 1.0F, this.width - 6.0F, ITEM_HEIGHT - 2.0F, 3.0F,
                        ColorUtils.multAlpha(HandEditor.HOVER, alpha));
            }
            if (i == selected) {
                GuiRender.roundedRect(stack, this.x + 5.0F, itemY + 3.5F, 2.0F, ITEM_HEIGHT - 7.0F, 1.0F,
                        ColorUtils.multAlpha(accent, alpha));
            }
            int color = i == selected || i == hovered ? HandEditor.WHITE : HandEditor.MUTED;
            GuiFonts.SF_MEDIUM.draw(stack, modes[i], this.x + TEXT_INSET, itemY + (ITEM_HEIGHT - TEXT_SIZE) * 0.5F - 0.5F,
                    ColorUtils.multAlpha(color, alpha), TEXT_SIZE);
        }

        stack.popPose();
    }

    private int indexAt(float mouseX, float mouseY) {
        float top = this.y + PADDING + TITLE_HEIGHT;
        if (mouseX < this.x || mouseX > this.x + this.width || mouseY < top) {
            return -1;
        }
        int index = (int) ((mouseY - top) / ITEM_HEIGHT);
        return index < SwingAnimation.MODES.length ? index : -1;
    }

    private void layout() {
        if (!this.widthReady) {
            float widest = 0.0F;
            String[] modes = SwingAnimation.MODES;
            for (int i = 0; i < modes.length; i++) {
                widest = Math.max(widest, GuiFonts.SF_MEDIUM.getWidth(modes[i], TEXT_SIZE));
            }
            if (widest > 0.0F) {
                this.width = Math.max(MIN_WIDTH, widest + TEXT_INSET * 2.0F);
                this.widthReady = true;
            }
        }

        Window window = Minecraft.getInstance().getWindow();
        float guiScale = (float) window.getGuiScale();
        float guiWidth = window.getWidth() / guiScale;
        float guiHeight = window.getHeight() / guiScale;
        this.x = Mth.clamp(this.anchorX + 2.0F, SCREEN_MARGIN, Math.max(SCREEN_MARGIN, guiWidth - this.width - SCREEN_MARGIN));
        this.y = Mth.clamp(this.anchorY + 2.0F, SCREEN_MARGIN, Math.max(SCREEN_MARGIN, guiHeight - HEIGHT - SCREEN_MARGIN));
    }
}
