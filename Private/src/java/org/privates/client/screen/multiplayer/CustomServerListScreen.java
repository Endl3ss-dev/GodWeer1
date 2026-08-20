package org.privates.client.screen.multiplayer;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

public class CustomServerListScreen extends Screen implements IMinecraft {
    private final Screen previousScreen;
    private final ServerData serverData;
    private final BooleanConsumer callback;

    private float panelX, panelY, panelWidth, panelHeight;
    private float alpha = 0f;
    private float alphaV = 0f;

    private String ipAddress = "";
    private boolean ipInputActive = true;

    private float connectButtonAnim = 0f;
    private float cancelButtonAnim = 0f;

    public CustomServerListScreen(Screen previousScreen, BooleanConsumer callback, ServerData serverData) {
        super(new StringTextComponent("Direct Connect"));
        this.previousScreen = previousScreen;
        this.callback = callback;
        this.serverData = serverData;
    }

    @Override
    protected void init() {
        super.init();

        this.panelWidth = 380f;
        this.panelHeight = 160f;
        this.panelX = width / 2f - panelWidth / 2f;
        this.panelY = height / 2f - panelHeight / 2f;

        this.alpha = 1f;
        this.ipAddress = minecraft.gameSettings.lastServer;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        alphaV = Mathf.fast(alphaV, alpha, 18);

        if (alphaV <= 0.1f && alpha == 0f) {
            super.closeScreen();
            return;
        }

        Theme theme = Theme.getInstance();

        RenderUtil.drawBackground(stack);

        RenderUtil.Rounded.roundedRect(stack, 0, 0, width, height,
                ColorUtil.getColor(0, 0, 0, (int) (100 * alphaV)), Round.of(0));

        BlurRenderer2.registerRenderCall(() ->
                RenderUtil.Rounded.roundedRect(stack, panelX, panelY, panelWidth, panelHeight,
                        ColorUtil.getColor(0, 0, 0, (int) (200 * alphaV)), Round.of(6f))
        );
        BlurRenderer2.draw(18);

        RenderUtil.Rounded.roundedRect(stack, panelX, panelY, panelWidth, panelHeight,
                ColorUtil.getColor(20, 20, 20, (int) (240 * alphaV)), Round.of(6f));

        RenderUtil.Rounded.roundedOutline(stack, panelX - 0.75f, panelY - 0.75f,
                panelWidth + 1.5f, panelHeight + 1.5f, 0.75f,
                ColorUtil.multAlpha(ColorUtil.getColor(60, 60, 60), alphaV), Round.of(6f));

        renderHeader(stack, theme);
        renderIPInput(stack, mouseX, mouseY, theme);
        renderButtons(stack, mouseX, mouseY, theme);

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderHeader(MatrixStack stack, Theme theme) {
        String title = "Прямое подключение";
        float titleY = panelY + 18f;

        Fonts.SF_BOLD.drawCenter(stack, title,
                panelX + panelWidth / 2f,
                titleY - Fonts.SF_BOLD.getHeight(title, 12f) / 2f,
                ColorUtil.multAlpha(theme.textColor(), alphaV), 12f);

        String subtitle = "Введите IP адрес сервера";
        Fonts.SF_REGULAR.drawCenter(stack, subtitle,
                panelX + panelWidth / 2f,
                titleY + 14f,
                ColorUtil.multAlpha(ColorUtil.getColor(170, 170, 170), alphaV), 7.5f);
    }

    private void renderIPInput(MatrixStack stack, float mouseX, float mouseY, Theme theme) {
        float inputX = panelX + 20f;
        float inputY = panelY + 60f;
        float inputWidth = panelWidth - 40f;
        float inputHeight = 34f;

        String label = "IP адрес";
        Fonts.SF_MEDIUM.draw(stack, label, inputX, inputY - 16f,
                ColorUtil.multAlpha(theme.textColor(), alphaV * 0.85f), 7.5f);

        boolean hovered = Mathf.isHover(mouseX, mouseY, inputX, inputY, inputWidth, inputHeight);
        float hoverAnim = hovered ? 1f : 0f;

        RenderUtil.Rounded.roundedWithOutline(stack, inputX, inputY, inputWidth, inputHeight, 0.85f,
                ColorUtil.multAlpha(ColorUtil.overCol(
                        ColorUtil.getColor(30, 30, 30, 180),
                        ColorUtil.multAlpha(theme.clientColor(), 0.03f),
                        hoverAnim), alphaV),
                ColorUtil.multAlpha(ipInputActive ?
                        ColorUtil.multAlpha(theme.clientColor(), 0.5f) :
                        ColorUtil.overCol(
                                ColorUtil.getColor(70, 70, 70),
                                ColorUtil.getColor(100, 100, 100),
                                hoverAnim), alphaV),
                Round.of(4.5f));

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(inputX + 10f, inputY, inputWidth - 20f, inputHeight);

        String displayText = ipAddress.isEmpty() && !ipInputActive ? "server.example.com:25565" : ipAddress;
        int textColor = ipAddress.isEmpty() && !ipInputActive ?
                ColorUtil.getColor(130, 130, 130) : ColorUtil.getColor(240, 240, 240);

        Fonts.SF_MEDIUM.draw(stack, displayText,
                inputX + 12f,
                inputY + inputHeight / 2f - Fonts.SF_MEDIUM.getHeight(displayText, 9f) / 2f,
                ColorUtil.multAlpha(textColor, alphaV), 9f);

        if (ipInputActive && System.currentTimeMillis() % 1000 > 500) {
            float cursorX = inputX + 12f + Fonts.SF_MEDIUM.getWidth(ipAddress, 9f);
            Fonts.SF_MEDIUM.draw(stack, "|", cursorX,
                    inputY + inputHeight / 2f - Fonts.SF_MEDIUM.getHeight("|", 9f) / 2f,
                    ColorUtil.multAlpha(theme.clientColor(), alphaV), 9f);
        }

        ScissorExp.unset();
        ScissorExp.pop();

        if (!isValidIP()) {
            String error = "Некорректный формат IP адреса";
            Fonts.SF_REGULAR.draw(stack, error, inputX, inputY + inputHeight + 6f,
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 100, 100), alphaV), 6.5f);
        }
    }

    private void renderButtons(MatrixStack stack, float mouseX, float mouseY, Theme theme) {
        float buttonWidth = 150f;
        float buttonHeight = 32f;
        float buttonY = panelY + panelHeight - buttonHeight - 18f;
        float spacing = 10f;

        float connectX = panelX + panelWidth / 2f - buttonWidth - spacing / 2f;
        float cancelX = panelX + panelWidth / 2f + spacing / 2f;

        boolean connectHovered = Mathf.isHover(mouseX, mouseY, connectX, buttonY, buttonWidth, buttonHeight);
        boolean cancelHovered = Mathf.isHover(mouseX, mouseY, cancelX, buttonY, buttonWidth, buttonHeight);

        connectButtonAnim = Mathf.fast(connectButtonAnim, connectHovered ? 1f : 0f, 16);
        cancelButtonAnim = Mathf.fast(cancelButtonAnim, cancelHovered ? 1f : 0f, 16);

        boolean connectEnabled = isValidIP();

        stack.push();
        stack.translate(connectX + buttonWidth / 2f, buttonY + buttonHeight / 2f, 0);
        float scale = 1f + (connectButtonAnim * 0.02f);
        stack.scale(scale, scale, 1);
        stack.translate(-(connectX + buttonWidth / 2f), -(buttonY + buttonHeight / 2f), 0);

        int connectBg = connectEnabled ?
                ColorUtil.overCol(theme.clientColor(), ColorUtil.multBright(theme.clientColor(), 0.85f), connectButtonAnim) :
                ColorUtil.getColor(60, 60, 60);

        RenderUtil.Rounded.roundedRect(stack, connectX, buttonY, buttonWidth, buttonHeight,
                ColorUtil.multAlpha(connectBg, alphaV), Round.of(5f));

        if (connectEnabled) {
            RenderUtil.Rounded.roundedOutline(stack, connectX, buttonY, buttonWidth, buttonHeight, 0.75f,
                    ColorUtil.multAlpha(ColorUtil.multAlpha(theme.clientColor(), 0.4f + connectButtonAnim * 0.3f), alphaV),
                    Round.of(5f));
        }

        String connectText = "Подключиться";
        int connectTextColor = connectEnabled ? ColorUtil.getColor(255, 255, 255) : ColorUtil.getColor(120, 120, 120);
        Fonts.SF_BOLD.drawCenter(stack, connectText,
                connectX + buttonWidth / 2f,
                buttonY + buttonHeight / 2f - Fonts.SF_BOLD.getHeight(connectText, 9f) / 2f,
                ColorUtil.multAlpha(connectTextColor, alphaV), 9f);

        stack.pop();

        stack.push();
        stack.translate(cancelX + buttonWidth / 2f, buttonY + buttonHeight / 2f, 0);
        scale = 1f + (cancelButtonAnim * 0.02f);
        stack.scale(scale, scale, 1);
        stack.translate(-(cancelX + buttonWidth / 2f), -(buttonY + buttonHeight / 2f), 0);

        int cancelBg = ColorUtil.overCol(
                ColorUtil.getColor(50, 50, 50),
                ColorUtil.getColor(70, 70, 70),
                cancelButtonAnim
        );

        RenderUtil.Rounded.roundedRect(stack, cancelX, buttonY, buttonWidth, buttonHeight,
                ColorUtil.multAlpha(cancelBg, alphaV), Round.of(5f));

        RenderUtil.Rounded.roundedOutline(stack, cancelX, buttonY, buttonWidth, buttonHeight, 0.75f,
                ColorUtil.multAlpha(ColorUtil.overCol(
                        ColorUtil.getColor(80, 80, 80),
                        ColorUtil.getColor(120, 120, 120),
                        cancelButtonAnim), alphaV),
                Round.of(5f));

        String cancelText = "Отмена";
        Fonts.SF_BOLD.drawCenter(stack, cancelText,
                cancelX + buttonWidth / 2f,
                buttonY + buttonHeight / 2f - Fonts.SF_BOLD.getHeight(cancelText, 9f) / 2f,
                ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 220), alphaV), 9f);

        stack.pop();
    }

    private boolean isValidIP() {
        if (ipAddress.isEmpty()) return false;
        String[] parts = ipAddress.split(":");
        if (parts.length == 0 || parts.length > 2) return false;
        return !ipAddress.contains(" ");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        float inputX = panelX + 20f;
        float inputY = panelY + 60f;
        float inputWidth = panelWidth - 40f;
        float inputHeight = 34f;

        if (Mathf.isHover(mouseX, mouseY, inputX, inputY, inputWidth, inputHeight)) {
            ipInputActive = true;
            return true;
        } else {
            ipInputActive = false;
        }

        float buttonWidth = 150f;
        float buttonHeight = 32f;
        float buttonY = panelY + panelHeight - buttonHeight - 18f;
        float spacing = 10f;

        float connectX = panelX + panelWidth / 2f - buttonWidth - spacing / 2f;
        float cancelX = panelX + panelWidth / 2f + spacing / 2f;

        if (Mathf.isHover(mouseX, mouseY, connectX, buttonY, buttonWidth, buttonHeight)) {
            if (isValidIP()) {
                connectToServer();
            }
            return true;
        }

        if (Mathf.isHover(mouseX, mouseY, cancelX, buttonY, buttonWidth, buttonHeight)) {
            cancel();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ipInputActive) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !ipAddress.isEmpty()) {
                ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                if (isValidIP()) {
                    connectToServer();
                }
                return true;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            cancel();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (ipInputActive) {
            ipAddress += codePoint;
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    private void connectToServer() {
        serverData.serverIP = ipAddress;
        minecraft.gameSettings.lastServer = ipAddress;
        minecraft.gameSettings.saveOptions();
        callback.accept(true);
    }

    private void cancel() {
        alpha = 0f;
    }

    @Override
    public void closeScreen() {
        callback.accept(false);
    }

    @Override
    public void onClose() {
        minecraft.displayScreen(previousScreen);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}