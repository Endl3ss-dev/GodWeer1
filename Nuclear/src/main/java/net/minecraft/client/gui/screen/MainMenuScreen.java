package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import nuclear.utils.IMinecraft;
import nuclear.utils.render.*;
import nuclear.utils.render.AnimationUtil;
import nuclear.utils.render.Easings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import nuclear.control.Manager;
import nuclear.utils.font.Fonts;
import org.joml.Vector4i;

import static nuclear.ui.clickgui.Panel.getColorByName;
import static nuclear.utils.IMinecraft.mc;
import static net.minecraft.util.math.MathHelper.lerp;

public class MainMenuScreen extends Screen {
    public static final RenderSkyboxCube PANORAMA_RESOURCES = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
    private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation MINECRAFT_TITLE_EDITION = new ResourceLocation("textures/gui/title/edition.png");
    private final boolean showTitleWronglySpelled;
    private Screen realmsNotification;
    private final boolean showFadeInAnimation;

    public static float animationAlpha = 0.0f;
    private final float animationSpeed = 0.095f;
    private boolean animationStarted = false;

    public MainMenuScreen() {
        this(false);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        animationAlpha = 0.0f;
        animationStarted = true;

        int buttonWidth = 138;
        int buttonHeight = 20;
        int off = (int) (buttonHeight + 5);

        this.addButton(new Button((int) (mc.getMainWindow().scaledWidth() / 2 - buttonWidth / 2f), mc.getMainWindow().scaledHeight() / 2 - 50, buttonWidth, buttonHeight, new StringTextComponent("Singleplayer"), p_onPress_1_ -> {
            mc.displayGuiScreen(new WorldSelectionScreen(this));
            }, 'z'));
        this.addButton(new Button((int) (mc.getMainWindow().scaledWidth() / 2 - buttonWidth / 2f), mc.getMainWindow().scaledHeight() / 2 - 50 + 24, buttonWidth, buttonHeight, new StringTextComponent("Multiplayer"), p_onPress_1_ -> {
            mc.displayGuiScreen(new MultiplayerScreen(this));
            }, 'E'));
        this.addButton(new Button((int) (mc.getMainWindow().scaledWidth() / 2 - buttonWidth / 2f), mc.getMainWindow().scaledHeight() / 2 - 50 + 24 + 24, buttonWidth, buttonHeight, new StringTextComponent("AltManager"), p_onPress_1_ -> {
            mc.displayGuiScreen(Manager.ALT);
        }, 'S'));
        this.addButton(new Button((int) (mc.getMainWindow().scaledWidth() / 2 - buttonWidth / 2f), mc.getMainWindow().scaledHeight() / 2 - 50 + 24 + 24 + 24, buttonWidth, buttonHeight, new StringTextComponent("Roulette"), p_onPress_1_ -> {
            mc.displayGuiScreen(new RouletteScreen(this));
        }, 'z'));

        this.addButton(new Button((int) (mc.getMainWindow().scaledWidth() / 2 - buttonWidth / 2f), mc.getMainWindow().scaledHeight() / 2 - 50 + 24 + 24 + 24 + 24, (int) (buttonWidth / 1.65f), buttonHeight, new StringTextComponent("Settings"), p_onPress_1_ -> {
            new OptionsScreen(this, this.minecraft.gameSettings);
        }, 'j'));

        this.addButton(new Button((int) (mc.getMainWindow().scaledWidth() / 2 - buttonWidth / 2f) + (int) (buttonWidth / 1.65f) + 4, mc.getMainWindow().scaledHeight() / 2 - 50 + 24 + 24 + 24 + 24, (int) (buttonWidth / 2.75f) + 1, buttonHeight, new StringTextComponent("Exit"), p_onPress_1_ -> {
            mc.shutdownMinecraftApplet();
            }, 'K'));
    }

    private boolean areRealmsNotificationsEnabled() {
        return this.minecraft.gameSettings.realmsNotifications && this.realmsNotification != null;
    }

    public MainMenuScreen(boolean fadeIn) {
        super(new TranslationTextComponent("narrator.screen.title"));
        this.showFadeInAnimation = fadeIn;
        this.showTitleWronglySpelled = (double) (new Random()).nextFloat() < 1.0E-4D;
    }

    public void tick() {
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotification.tick();
        }
        if (animationStarted && animationAlpha < 1.0f) {
            animationAlpha = lerp(animationAlpha, 1.0f, animationSpeed);
            if (animationAlpha > 0.99f) {
                animationAlpha = 1.0f;
                animationStarted = false;
            }
        }
    }

    public static CompletableFuture<Void> loadAsync(TextureManager texMngr, Executor backgroundExecutor) {
        return CompletableFuture.allOf(texMngr.loadAsync(MINECRAFT_TITLE_TEXTURES, backgroundExecutor), texMngr.loadAsync(MINECRAFT_TITLE_EDITION, backgroundExecutor), texMngr.loadAsync(PANORAMA_OVERLAY_TEXTURES, backgroundExecutor), PANORAMA_RESOURCES.loadAsync(texMngr, backgroundExecutor));
    }

    public boolean isPauseScreen() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        mc.gameRenderer.setupOverlayRendering(2);
        int iconColor = getColorByName("iconColor");
        int textColor = getColorByName("textColor");
        int windowWidth = mc.getMainWindow().getScaledWidth();
        int windowHeight = mc.getMainWindow().getScaledHeight();
        int renderWidth = (int) (windowWidth * 1.05f);
        int renderHeight = (int) (windowHeight * 1.05f);

        float normMouseX = (mouseX / (float) windowWidth) * 2 - 1;
        float normMouseY = (mouseY / (float) windowHeight) * 2 - 1;

        float maxOffsetX = (renderWidth - windowWidth) / 2.0f;
        float maxOffsetY = (renderHeight - windowHeight) / 2.0f;

        float offsetX = MathHelper.clamp(normMouseX * 4, -maxOffsetX, maxOffsetX);
        float offsetY = MathHelper.clamp(normMouseY * 4, -maxOffsetY, maxOffsetY);
        RenderUtilka.Render2D.drawImage(new ResourceLocation("nuclear/images/background.png"), offsetX - (renderWidth - windowWidth) / 2.0f, offsetY - (renderHeight - windowHeight) / 2.0f, renderWidth, renderHeight, -1);

        Fonts.gilroyBold[18].drawString(matrixStack, "COCKLEAR FREE", (mc.getMainWindow().scaledWidth() / 2f) - (Fonts.intl[14].getWidth("COCKLEAR FREE") / 2), IMinecraft.mc.getMainWindow().scaledHeight() / 2 - 70, textColor);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        mc.gameRenderer.setupOverlayRendering();
    }

    public void onClose() {
        if (this.realmsNotification != null) {
            this.realmsNotification.onClose();
        }
    }


    public static class Button extends AbstractButton {

        public static final net.minecraft.client.gui.widget.button.Button.ITooltip field_238486_s_ = (button, matrixStack, mouseX, mouseY) ->
        {
        };
        protected final net.minecraft.client.gui.widget.button.Button.IPressable onPress;
        protected final net.minecraft.client.gui.widget.button.Button.ITooltip onTooltip;
        private char iconChar;
        private AnimationUtil hoverAnimation = new AnimationUtil(0.0f, 12.0f, Easings.CUBIC_OUT);
        private float currentScale = 1.0f;
        private AnimationUtil scaleAnimation = new AnimationUtil(1.0f, 15.0f, Easings.QUART_OUT);

        public Button(int x, int y, int width, int height, ITextComponent title, net.minecraft.client.gui.widget.button.Button.IPressable pressedAction, char iconChar) {
            this(x, y, width, height, title, pressedAction, field_238486_s_);
            this.iconChar = iconChar;
        }

        public Button(int x, int y, int width, int height, ITextComponent title, net.minecraft.client.gui.widget.button.Button.IPressable pressedAction, net.minecraft.client.gui.widget.button.Button.ITooltip onTooltip) {
            super(x, y, width, height, title);
            this.onPress = pressedAction;
            this.onTooltip = onTooltip;
            this.iconChar = ' ';
        }


        float titleWidth = Fonts.intl[14].getWidth(this.getMessage().getString());

        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            int textColor = getColorByName("textColor");
            int iconColor = getColorByName("iconColor");
            int iconnoColor = getColorByName("iconnoColor");

            boolean isHovered = isMouseOverButton(mouseX, mouseY, (int) (x), y, width, height);
            
            // Обновляем анимацию наведения
            hoverAnimation.update(isHovered ? 1.0f : 0.0f);
            float hoverProgress = hoverAnimation.getValue();
            
            // Обновляем анимацию масштаба
            scaleAnimation.update(isHovered ? 1.02f : 1.0f);
            currentScale = scaleAnimation.getValue();
            
            // Вычисляем центр кнопки для масштабирования
            float centerX = x + width / 2f;
            float centerY = y + height / 2f;
            float scaledWidth = width * currentScale;
            float scaledHeight = height * currentScale;
            float scaledX = centerX - scaledWidth / 2f;
            float scaledY = centerY - scaledHeight / 2f;

            GaussianBlur.startBlur();
            RenderUtilka.Render2D.drawRoundedRect(scaledX, scaledY, scaledWidth, scaledHeight, 4f, new Color(16, 16, 16, 25).getRGB());
            GaussianBlur.endBlur(6, 3);

            if (this.iconChar == 'K') {
                int exitBgColor = ColorUtils.interpolateColor(
                    ColorUtils.setAlpha(iconnoColor, 5),
                    new Color(218, 56, 56, 15).getRGB(),
                    hoverProgress
                );
                int exitOutlineColor = ColorUtils.interpolateColor(
                    ColorUtils.setAlpha(iconnoColor, 10),
                    new Color(218, 56, 56, 15).getRGB(),
                    hoverProgress
                );
                int exitTextColor = ColorUtils.interpolateColor(
                    ColorUtils.setAlpha(textColor, 215),
                    new Color(218, 56, 56, 124).getRGB(),
                    hoverProgress
                );
                int exitIconColor = ColorUtils.interpolateColor(
                    ColorUtils.setAlpha(iconColor, 174),
                    new Color(218, 56, 56, 164).getRGB(),
                    hoverProgress
                );
                
                RenderUtilka.Render2D.drawRoundedRect(scaledX, scaledY, scaledWidth, scaledHeight, 4f, exitBgColor);
                RenderUtilka.Render2D.drawRoundOutline(scaledX, scaledY, scaledWidth, scaledHeight, 4f, -0.9f, ColorUtils.rgba(25, 26, 33, 0), new Vector4i(
                        exitOutlineColor,
                        exitOutlineColor,
                        exitOutlineColor,
                        exitOutlineColor
                ));
                Fonts.intl[13].drawCenteredString(matrixStack, this.getMessage().getString(), centerX + 6, scaledY + (scaledHeight - 8) / 2 + 3f, exitTextColor);
                Fonts.Nuclear[14].drawCenteredString(matrixStack, String.valueOf(iconChar), centerX + 1f - titleWidth / 2, scaledY + (scaledHeight - 8) / 2 + 2 + 1.5f, exitIconColor);
            } else {
                // Обычные кнопки с плавной анимацией
                int bgColor = ColorUtils.interpolateColor(
                    ColorUtils.setAlpha(iconnoColor, 5),
                    ColorUtils.setAlpha(iconnoColor, 90),
                    hoverProgress
                );
                int outlineColor = ColorUtils.interpolateColor(
                    ColorUtils.setAlpha(iconnoColor, 10),
                    ColorUtils.setAlpha(iconnoColor, 40),
                    hoverProgress
                );
                int finalTextColor = ColorUtils.interpolateColor(
                    ColorUtils.setAlpha(textColor, 215),
                    ColorUtils.setAlpha(textColor, 255),
                    hoverProgress
                );
                int finalIconColor = ColorUtils.interpolateColor(
                    ColorUtils.setAlpha(iconColor, 174),
                    ColorUtils.setAlpha(iconColor, 235),
                    hoverProgress
                );
                
                RenderUtilka.Render2D.drawRoundedRect(scaledX, scaledY, scaledWidth, scaledHeight, 4f, bgColor);
                RenderUtilka.Render2D.drawRoundOutline(scaledX, scaledY, scaledWidth, scaledHeight, 4f, -0.9f, ColorUtils.rgba(25, 26, 33, 0), new Vector4i(
                        outlineColor,
                        outlineColor,
                        outlineColor,
                        outlineColor
                ));
                Fonts.intl[13].drawCenteredString(matrixStack, this.getMessage().getString(), centerX + 6, scaledY + (scaledHeight - 8) / 2 + 3f, finalTextColor);
                if (this.getMessage().getString().equals("Roulette")) {
                    Fonts.icon[14].drawCenteredString(matrixStack, String.valueOf(iconChar), centerX + 1f - titleWidth / 2, scaledY + (scaledHeight - 8) / 2 + 2 + 1.5f, finalIconColor);
                } else {
                    Fonts.Nuclear[14].drawCenteredString(matrixStack, String.valueOf(iconChar), centerX + 1f - titleWidth / 2, scaledY + (scaledHeight - 8) / 2 + 2 + 1.5f, finalIconColor);
                }
            }
        }

        private boolean isMouseOverButton(int mouseX, int mouseY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
            return mouseX >= buttonX && mouseY >= buttonY && mouseX < buttonX + buttonWidth && mouseY < buttonY + buttonHeight;
        }

        public void onPress() {
            this.onPress.onPress(this);
        }
    }
}