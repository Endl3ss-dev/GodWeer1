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

public class CustomAddServerScreen extends Screen implements IMinecraft {
    private final Screen previousScreen;
    private final ServerData serverData;
    private final BooleanConsumer callback;
    private final boolean isEditing;

    private float panelX, panelY, panelWidth, panelHeight;
    private float alpha = 0f;
    private float alphaV = 0f;

    private String serverName = "";
    private String serverIP = "";
    private boolean nameInputActive = true;
    private boolean ipInputActive = false;

    private float saveButtonAnim = 0f;
    private float cancelButtonAnim = 0f;

    public CustomAddServerScreen(Screen previousScreen, BooleanConsumer callback, ServerData serverData) {
        super(new StringTextComponent(serverData.serverName.isEmpty() ? "Add Server" : "Edit Server"));
        this.previousScreen = previousScreen;
        this.callback = callback;
        this.serverData = serverData;
        this.isEditing = !serverData.serverName.isEmpty() || !serverData.serverIP.isEmpty();

        this.serverName = serverData.serverName;
        this.serverIP = serverData.serverIP;
    }

    @Override
    protected void init() {
        super.init();

        this.panelWidth = 400f;
        this.panelHeight = 220f;
        this.panelX = width / 2f - panelWidth / 2f;
        this.panelY = height / 2f - panelHeight / 2f;

        this.alpha = 1f;
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
        renderInputFields(stack, mouseX, mouseY, theme);
        renderButtons(stack, mouseX, mouseY, theme);

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderHeader(MatrixStack stack, Theme theme) {
        String title = isEditing ? "Редактировать сервер" : "Добавить сервер";
        float titleY = panelY + 20f;

        Fonts.SF_BOLD.drawCenter(stack, title,
                panelX + panelWidth / 2f,
                titleY - Fonts.SF_BOLD.getHeight(title, 12f) / 2f,
                ColorUtil.multAlpha(theme.textColor(), alphaV), 12f);

        String subtitle = isEditing ? "Измените данные сервера" : "Заполните информацию о сервере";
        Fonts.SF_REGULAR.drawCenter(stack, subtitle,
                panelX + panelWidth / 2f,
                titleY + 14f,
                ColorUtil.multAlpha(ColorUtil.getColor(170, 170, 170), alphaV), 7.5f);
    }

    private void renderInputFields(MatrixStack stack, float mouseX, float mouseY, Theme theme) {
        float inputX = panelX + 25f;
        float inputWidth = panelWidth - 50f;
        float inputHeight = 34f;

        float nameY = panelY + 65f;
        renderInputField(stack, mouseX, mouseY, theme, inputX, nameY, inputWidth, inputHeight,
                "Название сервера", serverName, nameInputActive, "Мой сервер");

        float ipY = nameY + inputHeight + 28f;
        renderInputField(stack, mouseX, mouseY, theme, inputX, ipY, inputWidth, inputHeight,
                "IP адрес", serverIP, ipInputActive, "server.example.com:25565");
    }

    private void renderInputField(MatrixStack stack, float mouseX, float mouseY, Theme theme,
                                  float x, float y, float width, float height,
                                  String label, String value, boolean active, String placeholder) {

        Fonts.SF_MEDIUM.draw(stack, label, x, y - 16f,
                ColorUtil.multAlpha(theme.textColor(), alphaV * 0.85f), 7.5f);

        boolean hovered = Mathf.isHover(mouseX, mouseY, x, y, width, height);
        float hoverAnim = hovered ? 1f : 0f;

        RenderUtil.Rounded.roundedWithOutline(stack, x, y, width, height, 0.85f,
                ColorUtil.multAlpha(ColorUtil.overCol(
                        ColorUtil.getColor(30, 30, 30, 180),
                        ColorUtil.multAlpha(theme.clientColor(), 0.03f),
                        hoverAnim), alphaV),
                ColorUtil.multAlpha(active ?
                        ColorUtil.multAlpha(theme.clientColor(), 0.5f) :
                        ColorUtil.overCol(
                                ColorUtil.getColor(70, 70, 70),
                                ColorUtil.getColor(100, 100, 100),
                                hoverAnim), alphaV),
                Round.of(4.5f));

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x + 10f, y, width - 20f, height);

        String displayText;
        int textColor;

        if (value.isEmpty() && !active) {
            displayText = placeholder;
            textColor = ColorUtil.getColor(130, 130, 130);
        } else {
            displayText = value;
            textColor = ColorUtil.getColor(240, 240, 240);
        }

        Fonts.SF_MEDIUM.draw(stack, displayText,
                x + 12f,
                y + height / 2f - Fonts.SF_MEDIUM.getHeight(displayText, 9f) / 2f,
                ColorUtil.multAlpha(textColor, alphaV), 9f);

        if (active && System.currentTimeMillis() % 1000 > 500) {
            float cursorX = x + 12f + Fonts.SF_MEDIUM.getWidth(value, 9f);
            Fonts.SF_MEDIUM.draw(stack, "|", cursorX,
                    y + height / 2f - Fonts.SF_MEDIUM.getHeight("|", 9f) / 2f,
                    ColorUtil.multAlpha(theme.clientColor(), alphaV), 9f);
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderButtons(MatrixStack stack, float mouseX, float mouseY, Theme theme) {
        float buttonWidth = 160f;
        float buttonHeight = 34f;
        float buttonY = panelY + panelHeight - buttonHeight - 20f;
        float spacing = 12f;

        float saveX = panelX + panelWidth / 2f - buttonWidth - spacing / 2f;
        float cancelX = panelX + panelWidth / 2f + spacing / 2f;

        boolean saveHovered = Mathf.isHover(mouseX, mouseY, saveX, buttonY, buttonWidth, buttonHeight);
        boolean cancelHovered = Mathf.isHover(mouseX, mouseY, cancelX, buttonY, buttonWidth, buttonHeight);

        saveButtonAnim = Mathf.fast(saveButtonAnim, saveHovered ? 1f : 0f, 16);
        cancelButtonAnim = Mathf.fast(cancelButtonAnim, cancelHovered ? 1f : 0f, 16);

        boolean saveEnabled = !serverName.isEmpty() && !serverIP.isEmpty() && isValidIP(serverIP);

        stack.push();
        stack.translate(saveX + buttonWidth / 2f, buttonY + buttonHeight / 2f, 0);
        float scale = 1f + (saveButtonAnim * 0.02f);
        stack.scale(scale, scale, 1);
        stack.translate(-(saveX + buttonWidth / 2f), -(buttonY + buttonHeight / 2f), 0);

        int saveBg = saveEnabled ?
                ColorUtil.overCol(theme.clientColor(), ColorUtil.multBright(theme.clientColor(), 0.85f), saveButtonAnim) :
                ColorUtil.getColor(60, 60, 60);

        RenderUtil.Rounded.roundedRect(stack, saveX, buttonY, buttonWidth, buttonHeight,
                ColorUtil.multAlpha(saveBg, alphaV), Round.of(5.5f));

        if (saveEnabled) {
            RenderUtil.Rounded.roundedOutline(stack, saveX, buttonY, buttonWidth, buttonHeight, 0.85f,
                    ColorUtil.multAlpha(ColorUtil.multAlpha(theme.clientColor(), 0.4f + saveButtonAnim * 0.3f), alphaV),
                    Round.of(5.5f));
        }

        String saveText = isEditing ? "Сохранить" : "Добавить";
        int saveTextColor = saveEnabled ? ColorUtil.getColor(255, 255, 255) : ColorUtil.getColor(120, 120, 120);
        Fonts.SF_BOLD.drawCenter(stack, saveText,
                saveX + buttonWidth / 2f,
                buttonY + buttonHeight / 2f - Fonts.SF_BOLD.getHeight(saveText, 9.5f) / 2f,
                ColorUtil.multAlpha(saveTextColor, alphaV), 9.5f);

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
                ColorUtil.multAlpha(cancelBg, alphaV), Round.of(5.5f));

        RenderUtil.Rounded.roundedOutline(stack, cancelX, buttonY, buttonWidth, buttonHeight, 0.85f,
                ColorUtil.multAlpha(ColorUtil.overCol(
                        ColorUtil.getColor(80, 80, 80),
                        ColorUtil.getColor(120, 120, 120),
                        cancelButtonAnim), alphaV),
                Round.of(5.5f));

        String cancelText = "Отмена";
        Fonts.SF_BOLD.drawCenter(stack, cancelText,
                cancelX + buttonWidth / 2f,
                buttonY + buttonHeight / 2f - Fonts.SF_BOLD.getHeight(cancelText, 9.5f) / 2f,
                ColorUtil.multAlpha(ColorUtil.getColor(220, 220, 220), alphaV), 9.5f);

        stack.pop();
    }

    private boolean isValidIP(String ip) {
        if (ip.isEmpty()) return false;
        String[] parts = ip.split(":");
        if (parts.length == 0 || parts.length > 2) return false;
        return !ip.contains(" ");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        float inputX = panelX + 25f;
        float inputWidth = panelWidth - 50f;
        float inputHeight = 34f;

        float nameY = panelY + 65f;
        if (Mathf.isHover(mouseX, mouseY, inputX, nameY, inputWidth, inputHeight)) {
            nameInputActive = true;
            ipInputActive = false;
            return true;
        }

        float ipY = nameY + inputHeight + 28f;
        if (Mathf.isHover(mouseX, mouseY, inputX, ipY, inputWidth, inputHeight)) {
            nameInputActive = false;
            ipInputActive = true;
            return true;
        }

        float buttonWidth = 160f;
        float buttonHeight = 34f;
        float buttonY = panelY + panelHeight - buttonHeight - 20f;
        float spacing = 12f;

        float saveX = panelX + panelWidth / 2f - buttonWidth - spacing / 2f;
        float cancelX = panelX + panelWidth / 2f + spacing / 2f;

        if (Mathf.isHover(mouseX, mouseY, saveX, buttonY, buttonWidth, buttonHeight)) {
            if (!serverName.isEmpty() && !serverIP.isEmpty() && isValidIP(serverIP)) {
                saveServer();
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
        if (nameInputActive) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !serverName.isEmpty()) {
                serverName = serverName.substring(0, serverName.length() - 1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                nameInputActive = false;
                ipInputActive = true;
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER && !serverName.isEmpty() && !serverIP.isEmpty() && isValidIP(serverIP)) {
                saveServer();
                return true;
            }
        }

        if (ipInputActive) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !serverIP.isEmpty()) {
                serverIP = serverIP.substring(0, serverIP.length() - 1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                ipInputActive = false;
                nameInputActive = true;
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER && !serverName.isEmpty() && !serverIP.isEmpty() && isValidIP(serverIP)) {
                saveServer();
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
        if (nameInputActive) {
            serverName += codePoint;
            return true;
        }
        if (ipInputActive) {
            serverIP += codePoint;
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    private void saveServer() {
        serverData.serverName = serverName;
        serverData.serverIP = serverIP;
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