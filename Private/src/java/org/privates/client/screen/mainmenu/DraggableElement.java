package org.privates.client.screen.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IScreen;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.gif.GifRender;
import org.joml.Vector2f;

public class DraggableElement {
    public String id;
    public float x, y, width, height;

    public DraggableElement(String id, float x, float y, float width, float height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}

class ToggleButton implements IScreen {
    private final Vector2f pos, size;
    private final java.util.function.Supplier<Boolean> getter;
    private final java.util.function.Consumer<Boolean> setter;
    private final Animation hover = new Animation();

    public ToggleButton(Vector2f pos, Vector2f size, java.util.function.Supplier<Boolean> getter, java.util.function.Consumer<Boolean> setter) {
        this.pos = pos;
        this.size = size;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        hover.update();
        boolean hovered = isHover(mouseX, mouseY, pos.x, pos.y, size.x, size.y);
        hover.run(hovered ? 1.0 : 0.0, 0.25, Easings.SINE_OUT, true);
        int bg = ColorUtil.getColor(20, 30, 55, 140 + (int) (40 * hover.get()));
        int border = getter.get() ? ColorUtil.getColor(100, 160, 240) : ColorUtil.getColor(60, 80, 120);
        RenderUtil.Rounded.smooth(matrix, pos.x, pos.y, size.x, size.y, bg, Round.of(6));
        RenderUtil.Rounded.roundedOutline(matrix, pos.x, pos.y, size.x, size.y, 1.2f, border, Round.of(6));
        String text = getter.get() ? "вкл" : "выкл";
        Fonts.SF_MEDIUM.drawCenter(matrix, text, pos.x + size.x / 2f, pos.y + size.y / 2f - 5f, 0xFFDDEEFF, 9);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHover(mouseX, mouseY, pos.x, pos.y, size.x, size.y)) {
            setter.accept(!getter.get());
            return true;
        }
        return false;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    @Override
    public void onClose() {
    }
}

class LoadingContainer extends Screen {
    public final long startTime;
    private final String gifName;
    private final String message;
    private final Animation loadingAnimation = new Animation();
    private GifRender gifRender;

    public LoadingContainer(String gifName, String message) {
        super(StringTextComponent.EMPTY);
        this.gifName = gifName;
        this.message = message;
        this.startTime = System.currentTimeMillis();
        try {
            // Здесь должна быть логика загрузки GIF
            // this.gifRender = new GifRender(new Namespaced("texture/" + gifName + ".gif"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void init() {
        super.init();
        loadingAnimation.set(0F);
        loadingAnimation.run(1.0, 0.5);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        loadingAnimation.update();

        // Темный фон
        int bgColor = ColorUtil.getColor(8, 12, 28, loadingAnimation.get());
        RenderUtil.drawRect(matrixStack, 0, 0, width, height, bgColor, bgColor, bgColor, bgColor, false, true);

        float centerX = width / 2f;
        float centerY = height / 2f;

        // Рендер GIF если доступен
        if (gifRender != null) {
            float gifSize = 64f;
            gifRender.draw(matrixStack, centerX - gifSize / 2f, centerY - gifSize / 2f - 30, gifSize, gifSize, 1, 15, true);
        } else {
            // Fallback спиннер
            renderSpinner(matrixStack, centerX, centerY - 30, 32f);
        }

        // Сообщение загрузки
        if (!message.isEmpty()) {
            int textColor = ColorUtil.getColor(200, 220, 255, loadingAnimation.get());
            Fonts.SF_MEDIUM.drawCenter(matrixStack, message, centerX, centerY + 50, textColor, 14);
        }

        // Прогресс бар
        renderProgressBar(matrixStack, centerX, centerY + 80);
    }

    private void renderSpinner(MatrixStack matrixStack, float centerX, float centerY, float size) {
        long currentTime = System.currentTimeMillis();
        float rotation = (currentTime % 2000) / 2000f * 360f;

        // Простой спиннер из точек
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(rotation + i * 45);
            float dotX = (float) (centerX + Math.cos(angle) * size / 2f);
            float dotY = (float) (centerY + Math.sin(angle) * size / 2f);

            float alpha = 1f - (i / 8f) * 0.7f;
            int dotColor = ColorUtil.getColor(100, 160, 240, alpha * loadingAnimation.get());

            RenderUtil.Rounded.smooth(matrixStack, dotX - 3, dotY - 3, 6, 6, dotColor, Round.of(3));
        }
    }

    private void renderProgressBar(MatrixStack matrixStack, float centerX, float centerY) {
        float barWidth = 200f;
        float barHeight = 4f;
        float barX = centerX - barWidth / 2f;

        // Фон прогресс бара
        int bgColor = ColorUtil.getColor(30, 40, 60, loadingAnimation.get());
        RenderUtil.Rounded.smooth(matrixStack, barX, centerY, barWidth, barHeight, bgColor, Round.of(2));

        // Прогресс
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(1f, elapsed / 1200f); // 1.2 секунды
        float progressWidth = barWidth * progress;

        int progressColor = ColorUtil.getColor(100, 160, 240, loadingAnimation.get());
        RenderUtil.Rounded.smooth(matrixStack, barX, centerY, progressWidth, barHeight, progressColor, Round.of(2));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

