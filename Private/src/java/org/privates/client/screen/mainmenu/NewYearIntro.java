package org.privates.client.screen.mainmenu;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Namespaced;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

public class NewYearIntro extends Screen implements IMinecraft {
    private long startTime;
    private static final long INTRO_DURATION = 3000;

    public NewYearIntro() {
        super(StringTextComponent.EMPTY);
    }

    @Override
    protected void init() {
        super.init();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed >= INTRO_DURATION) {
            mc.displayScreen(new MainMenu());
            return;
        }

        float scale = getResolutionScale();
        int themeColor = Theme.getInstance().clientColor();

        RenderUtil.drawBackground(matrixStack);

        float fadeProgress;
        if (elapsed < 1500) {
            fadeProgress = Math.min(1f, elapsed / 1000f);
        } else {
            fadeProgress = Math.max(0f, 1f - (elapsed - 1500) / 1500f);
        }

        float slideProgress = Math.min(1f, elapsed / 800f);

        float rectWidth = 450f * scale;
        float rectHeight = 250f * scale;
        float rectX = width / 2f - rectWidth / 2f;
        float rectY = height / 2f - rectHeight / 2f;

        float leftSlide = rectX + (rectWidth / 2f) * (1f - slideProgress);
        float rightSlide = rectX + (rectWidth / 2f) * slideProgress;
        float slideWidth = rectWidth / 2f * slideProgress;

        BlurRenderer2.registerRenderCall(() -> {
            int cardBg = ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, (int) (255 * 0.4f)), fadeProgress);
            RenderUtil.Rounded.roundedRect(matrixStack, leftSlide, rectY, slideWidth, rectHeight, cardBg, Round.of(8f * scale));
            RenderUtil.Rounded.roundedRect(matrixStack, rightSlide, rectY, slideWidth, rectHeight, cardBg, Round.of(8f * scale));
        });

        BlurRenderer2.draw(25);

        int cardBg = ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, (int) (255 * 0.01f)), fadeProgress);
        RenderUtil.Rounded.roundedRect(matrixStack, leftSlide, rectY, slideWidth, rectHeight, cardBg, Round.of(8f * scale));
        RenderUtil.Rounded.roundedRect(matrixStack, rightSlide, rectY, slideWidth, rectHeight, cardBg, Round.of(8f * scale));

        int borderColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, (int) (255 * 0.07f)), fadeProgress);
        RenderUtil.Rounded.roundedOutline(matrixStack, leftSlide, rectY, slideWidth, rectHeight, scale, borderColor, Round.of(8f * scale));
        RenderUtil.Rounded.roundedOutline(matrixStack, rightSlide, rectY, slideWidth, rectHeight, scale, borderColor, Round.of(8f * scale));

        float logoSize = 80f * scale;
        float logoX = width / 2f - logoSize / 2f;
        float logoY = rectY + 30f * scale;

        matrixStack.push();
        float centerX = logoX + logoSize / 2f;
        float centerY = logoY + logoSize / 2f;
        matrixStack.translate(centerX, centerY, 0);
        float logoScale = 0.8f + (fadeProgress * 0.2f);
        matrixStack.scale(logoScale, logoScale, 1);
        matrixStack.translate(-centerX, -centerY, 0);

        RenderUtil.drawImage(matrixStack, new Namespaced("texture/logo.png"), logoX, logoY, logoSize, logoSize, true, ColorUtil.multAlpha(themeColor, fadeProgress));

        matrixStack.pop();

        String text1 = "Private Client";
        String text2 = "поздравляет вас с наступающим";
        String text3 = "2026 годом";

        float textY = logoY + logoSize + 20f * scale;

        int textColor = ColorUtil.multAlpha(ColorUtil.getColor(200, 220, 255), fadeProgress);
        int accentColor = ColorUtil.multAlpha(themeColor, fadeProgress);

        Fonts.SF_BOLD.drawCenter(matrixStack, text1, width / 2f, textY, accentColor, 18f * scale);
        Fonts.SF_REGULAR.drawCenter(matrixStack, text2, width / 2f, textY + 25f * scale, textColor, 14f * scale);
        Fonts.SF_BOLD.drawCenter(matrixStack, text3, width / 2f, textY + 45f * scale, accentColor, 16f * scale);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mc.displayScreen(new MainMenu());
        return true;
    }

    private float getResolutionScale() {
        int width = mc.getMainWindow().getWidth();
        int height = mc.getMainWindow().getHeight();
        if (width >= 3840 && height >= 2160) {
            return 1.5f;
        } else if (width >= 2560 && height >= 1440) {
            return 1.0f;
        } else if (width >= 1920 || height >= 1080) {
            return 0.75f;
        } else {
            return 0.65f;
        }
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