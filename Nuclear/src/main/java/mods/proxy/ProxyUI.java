package mods.proxy;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import nuclear.control.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.StringTextComponent;

public class ProxyUI extends Screen {
    private ProxyConnection proxyConnection;
    private TextFieldWidget hostField;
    private TextFieldWidget portField;
    private TextFieldWidget usernameField;
    private TextFieldWidget passwordField;
    private Button httpButton;
    private Button socks4Button;
    private Button socks5Button;
    private Button directButton;
    private Button applyButton;
    private Button backButton;
    private Button testButton;
    private CheckboxButton authCheckbox;
    private ProxyType selectedType;
    private String statusMessage;
    private boolean statusIsError;
    private boolean isTesting;
    private boolean useAuth;

    public ProxyUI() {
        super(new StringTextComponent("Настройки прокси"));
        this.selectedType = ProxyType.DIRECT;
        this.statusMessage = "";
        this.statusIsError = false;
        this.isTesting = false;
        this.useAuth = false;
        this.proxyConnection = Manager.PROXY_CONN;
    }

    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int buttonWidth = 80;
        int buttonSpacing = 85;
        this.directButton = (Button)this.addButton(new Button(centerX - buttonSpacing * 2 + 10, centerY - 80, buttonWidth, 20, new StringTextComponent(ProxyType.DIRECT.getDisplayName()), (button) -> this.selectProxyType(ProxyType.DIRECT)));
        this.httpButton = (Button)this.addButton(new Button(centerX - buttonSpacing + 10, centerY - 80, buttonWidth, 20, new StringTextComponent(ProxyType.HTTP.getDisplayName()), (button) -> this.selectProxyType(ProxyType.HTTP)));
        this.socks4Button = (Button)this.addButton(new Button(centerX + 10, centerY - 80, buttonWidth, 20, new StringTextComponent(ProxyType.SOCKS4.getDisplayName()), (button) -> this.selectProxyType(ProxyType.SOCKS4)));
        this.socks5Button = (Button)this.addButton(new Button(centerX + buttonSpacing + 10, centerY - 80, buttonWidth, 20, new StringTextComponent(ProxyType.SOCKS5.getDisplayName()), (button) -> this.selectProxyType(ProxyType.SOCKS5)));
        this.hostField = new TextFieldWidget(this.font, 0, 0, 120, 20, new StringTextComponent("Хост"));
        this.hostField.setMaxStringLength(255);
        this.portField = new TextFieldWidget(this.font, 0, 0, 90, 20, new StringTextComponent("Порт"));
        this.portField.setMaxStringLength(5);
        this.authCheckbox = (CheckboxButton)this.addButton(new CheckboxButton(0, 0, 240, 20, new StringTextComponent("Использовать аутентификацию"), false));
        this.usernameField = new TextFieldWidget(this.font, 0, 0, 120, 20, new StringTextComponent("Логин"));
        this.usernameField.setMaxStringLength(255);
        this.passwordField = new TextFieldWidget(this.font, 0, 0, 90, 20, new StringTextComponent("Пароль"));
        this.passwordField.setMaxStringLength(255);
        this.children.add(this.hostField);
        this.children.add(this.portField);
        this.children.add(this.usernameField);
        this.children.add(this.passwordField);
        this.applyButton = (Button)this.addButton(new Button(0, 0, 90, 20, new StringTextComponent("Применить"), (button) -> this.applySettings()));
        this.testButton = (Button)this.addButton(new Button(0, 0, 90, 20, new StringTextComponent("Тест"), (button) -> this.testConnection()));
        this.backButton = (Button)this.addButton(new Button(0, 0, 90, 20, new StringTextComponent("Назад"), (button) -> Minecraft.getInstance().displayGuiScreen(new MultiplayerScreen((Screen)null))));
        this.loadCurrentSettings();
        this.updateUI();
    }

    private void loadCurrentSettings() {
        this.selectedType = this.proxyConnection.getProxyType();
        if (!this.proxyConnection.getHost().isEmpty()) {
            this.hostField.setText(this.proxyConnection.getHost());
        }

        if (this.proxyConnection.getPort() > 0) {
            this.portField.setText(String.valueOf(this.proxyConnection.getPort()));
        }

        boolean hasAuth = !this.proxyConnection.getUsername().isEmpty() || !this.proxyConnection.getPassword().isEmpty();
        this.useAuth = hasAuth;
        this.authCheckbox.setChecked(hasAuth);
        if (!this.proxyConnection.getUsername().isEmpty()) {
            this.usernameField.setText(this.proxyConnection.getUsername());
        }

        if (!this.proxyConnection.getPassword().isEmpty()) {
            this.passwordField.setText(this.proxyConnection.getPassword());
        }

    }

    private void updateElementPositions() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        boolean isDirect = this.selectedType == ProxyType.DIRECT;
        boolean supportsAuth = this.selectedType.supportsAuth();
        boolean showAuthFields = this.useAuth && supportsAuth;
        int currentY = centerY - 50;
        if (!isDirect) {
            this.hostField.x = centerX - 140 + 20;
            this.hostField.y = currentY;
            this.portField.x = centerX + 30 + 20;
            this.portField.y = currentY;
            currentY += 30;
            if (supportsAuth) {
                this.authCheckbox.x = centerX - 140;
                this.authCheckbox.y = currentY;
                currentY += 30;
                if (showAuthFields) {
                    this.usernameField.x = centerX - 140 + 20;
                    this.usernameField.y = currentY;
                    this.passwordField.x = centerX + 30 + 20;
                    this.passwordField.y = currentY;
                    currentY += 30;
                }
            }

            currentY += 10;
        } else {
            currentY = centerY - 10;
        }

        int totalButtonsWidth = 270;
        int startX = centerX - totalButtonsWidth / 2;
        this.applyButton.x = startX;
        this.applyButton.y = currentY;
        this.testButton.x = startX + 95;
        this.testButton.y = currentY;
        this.backButton.x = startX + 190;
        this.backButton.y = currentY;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        boolean isDirect = this.selectedType == ProxyType.DIRECT;
        boolean supportsAuth = this.selectedType.supportsAuth();
        boolean showAuthFields = this.useAuth && supportsAuth;
        if (!isDirect) {
            this.hostField.render(matrixStack, mouseX, mouseY, partialTicks);
            this.portField.render(matrixStack, mouseX, mouseY, partialTicks);
            if (showAuthFields) {
                this.usernameField.render(matrixStack, mouseX, mouseY, partialTicks);
                this.passwordField.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 16777215);
        if (!isDirect) {
            drawString(matrixStack, this.font, "Хост:", this.hostField.x - 30, this.hostField.y + 5, 16777215);
            drawString(matrixStack, this.font, "Порт:", this.portField.x - 30, this.portField.y + 5, 16777215);
            if (showAuthFields) {
                drawString(matrixStack, this.font, "Логин:", this.usernameField.x - 35, this.usernameField.y + 5, 16777215);
                drawString(matrixStack, this.font, "Пароль:", this.passwordField.x - 41, this.passwordField.y + 5, 16777215);
            }
        }

        if (!this.statusMessage.isEmpty()) {
            int color = this.statusIsError ? 16733525 : 5635925;
            drawCenteredString(matrixStack, this.font, this.statusMessage, this.width / 2, this.height / 2 + 120, color);
        }
    }

    public void tick() {
        super.tick();
        boolean isDirect = this.selectedType == ProxyType.DIRECT;
        boolean showAuthFields = this.useAuth && this.selectedType.supportsAuth();
        if (!isDirect) {
            this.hostField.tick();
            this.portField.tick();
            if (showAuthFields) {
                this.usernameField.tick();
                this.passwordField.tick();
            }
        }

        this.useAuth = this.authCheckbox.isChecked();
        this.updateUI();
    }

    private void updateUI() {
        boolean isDirect = this.selectedType == ProxyType.DIRECT;
        boolean supportsAuth = this.selectedType.supportsAuth();
        boolean authEnabled = this.useAuth && supportsAuth;
        this.updateElementPositions();
        this.hostField.setEnabled(!isDirect);
        this.portField.setEnabled(!isDirect);
        this.usernameField.setEnabled(!isDirect && authEnabled);
        this.passwordField.setEnabled(!isDirect && authEnabled);
        this.authCheckbox.visible = !isDirect && supportsAuth;
        this.authCheckbox.active = !isDirect && supportsAuth;
        this.directButton.active = this.selectedType != ProxyType.DIRECT;
        this.httpButton.active = this.selectedType != ProxyType.HTTP;
        this.socks4Button.active = this.selectedType != ProxyType.SOCKS4;
        this.socks5Button.active = this.selectedType != ProxyType.SOCKS5;
        this.testButton.active = !isDirect && !this.isTesting && this.isValidInput();
        this.applyButton.active = !this.isTesting;
        if (isDirect) {
            this.hostField.setText("");
            this.portField.setText("");
            this.usernameField.setText("");
            this.passwordField.setText("");
            this.authCheckbox.setChecked(false);
            this.useAuth = false;
        }

        if (!supportsAuth) {
            this.usernameField.setText("");
            this.passwordField.setText("");
            this.authCheckbox.setChecked(false);
            this.useAuth = false;
        }

    }

    private void selectProxyType(ProxyType type) {
        this.selectedType = type;
        this.statusMessage = "";
        this.statusIsError = false;
        this.updateUI();
    }

    private void applySettings() {
        try {
            if (this.selectedType == ProxyType.DIRECT) {
                this.proxyConnection.reset();
                MinecraftProxy.updateMinecraftProxy();
                this.setStatus("Прокси успешно отключен", false);
                return;
            }

            if (!this.isValidInput()) {
                this.setStatus("Ошибка: неправильные данные прокси", true);
                return;
            }

            String host = this.hostField.getText().trim();
            int port = Integer.parseInt(this.portField.getText().trim());
            String username = this.useAuth ? this.usernameField.getText().trim() : "";
            String password = this.useAuth ? this.passwordField.getText().trim() : "";
            boolean success = this.proxyConnection.setupProxy(this.selectedType, host, port, username, password);
            if (success) {
                if (!this.proxyConnection.validateSettings()) {
                    this.setStatus("Ошибка при проверке настроек: " + this.proxyConnection.getLastError(), true);
                    return;
                }

                this.proxyConnection.setEnabled(this.selectedType != ProxyType.DIRECT);
                MinecraftProxy.updateMinecraftProxy();
                this.setStatus("Прокси успешно настроен", false);
            } else {
                this.setStatus("Ошибка: " + this.proxyConnection.getLastError(), true);
            }
        } catch (NumberFormatException var6) {
            this.setStatus("Ошибка: неправильный формат порта", true);
        } catch (Exception e) {
            this.setStatus("Ошибка: " + e.getMessage(), true);
        }

    }

    private void testConnection() {
        if (this.selectedType == ProxyType.DIRECT) {
            this.setStatus("Тестирование невозможно, так как нет прокси соединения", false);
        } else if (!this.isValidInput()) {
            this.setStatus("Ошибка: неправильные данные прокси", true);
        } else {
            this.isTesting = true;
            this.testButton.setMessage(new StringTextComponent("Тестирование..."));
            this.setStatus("Тестирование соединения...", false);
            String host = this.hostField.getText().trim();
            int port = Integer.parseInt(this.portField.getText().trim());
            String username = this.useAuth ? this.usernameField.getText().trim() : "";
            String password = this.useAuth ? this.passwordField.getText().trim() : "";
            (new Thread(() -> {
                try {
                    boolean success = false;
                    String errorMessage = null;

                    try (Socket socket = new Socket()) {
                        socket.setSoTimeout(10000);
                        socket.connect(new InetSocketAddress(host, port), 10000);
                        success = true;
                    } catch (ConnectException var12) {
                        errorMessage = "Не удалось подключиться к серверу";
                    } catch (SocketTimeoutException var13) {
                        errorMessage = "Истек тайм-аут запроса";
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                    }

                    String finalErrorMessage = errorMessage;
                    boolean finalSuccess = success;
                    Minecraft.getInstance().execute(() -> {
                        this.isTesting = false;
                        this.testButton.setMessage(new StringTextComponent("Тест"));
                        if (finalSuccess) {
                            if (!this.useAuth || !this.selectedType.supportsAuth() || username.isEmpty() && password.isEmpty()) {
                                this.setStatus("Соединение успешно", false);
                            } else {
                                this.setStatus("Соединение успешно (проверка аутентификации может потребоваться)", false);
                            }
                        } else {
                            this.setStatus("Ошибка тестирования: " + finalErrorMessage, true);
                        }

                    });
                } catch (Exception e) {
                    Minecraft.getInstance().execute(() -> {
                        this.isTesting = false;
                        this.testButton.setMessage(new StringTextComponent("Тест"));
                        this.setStatus("Ошибка тестирования: " + e.getMessage(), true);
                    });
                }

            })).start();
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 && !this.isTesting) {
            boolean isDirect = this.selectedType == ProxyType.DIRECT;
            boolean showAuthFields = this.useAuth && this.selectedType.supportsAuth();
            if (!isDirect && (this.hostField.isFocused() || this.portField.isFocused() || showAuthFields && (this.usernameField.isFocused() || this.passwordField.isFocused())) && this.isValidInput()) {
                this.testConnection();
                return true;
            }
        }

        boolean isDirect = this.selectedType == ProxyType.DIRECT;
        boolean showAuthFields = this.useAuth && this.selectedType.supportsAuth();
        if (!isDirect) {
            if (this.hostField.keyPressed(keyCode, scanCode, modifiers) || this.portField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }

            if (showAuthFields && (this.usernameField.keyPressed(keyCode, scanCode, modifiers) || this.passwordField.keyPressed(keyCode, scanCode, modifiers))) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        boolean isDirect = this.selectedType == ProxyType.DIRECT;
        boolean showAuthFields = this.useAuth && this.selectedType.supportsAuth();
        if (!isDirect) {
            if (this.hostField.charTyped(codePoint, modifiers) || this.portField.charTyped(codePoint, modifiers)) {
                return true;
            }

            if (showAuthFields && (this.usernameField.charTyped(codePoint, modifiers) || this.passwordField.charTyped(codePoint, modifiers))) {
                return true;
            }
        }

        return super.charTyped(codePoint, modifiers);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isDirect = this.selectedType == ProxyType.DIRECT;
        boolean showAuthFields = this.useAuth && this.selectedType.supportsAuth();
        if (!isDirect) {
            this.hostField.mouseClicked(mouseX, mouseY, button);
            this.portField.mouseClicked(mouseX, mouseY, button);
            if (showAuthFields) {
                this.usernameField.mouseClicked(mouseX, mouseY, button);
                this.passwordField.mouseClicked(mouseX, mouseY, button);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void setStatus(String message, boolean isError) {
        this.statusMessage = message;
        this.statusIsError = isError;
    }

    private boolean isValidInput() {
        if (this.selectedType == ProxyType.DIRECT) {
            return true;
        } else {
            String host = this.hostField.getText().trim();
            String portStr = this.portField.getText().trim();
            if (!host.isEmpty() && !portStr.isEmpty()) {
                try {
                    int port = Integer.parseInt(portStr);
                    if (port <= 0 || port > 65535) {
                        return false;
                    }
                } catch (NumberFormatException var4) {
                    return false;
                }

                return this.selectedType != ProxyType.SOCKS4 || !this.useAuth || this.usernameField.getText().trim().isEmpty() && this.passwordField.getText().trim().isEmpty();
            } else {
                return false;
            }
        }
    }

    private String getValidationErrorMessage() {
        if (this.selectedType == ProxyType.DIRECT) {
            return "";
        } else {
            String host = this.hostField.getText().trim();
            String portStr = this.portField.getText().trim();
            if (host.isEmpty()) {
                return "Не указан хост";
            } else if (portStr.isEmpty()) {
                return "Не указан порт";
            } else {
                try {
                    int port = Integer.parseInt(portStr);
                    if (port <= 0 || port > 65535) {
                        return "Порт должен быть в диапазоне от 1 до 65535";
                    }
                } catch (NumberFormatException var5) {
                    return "Неправильный формат порта";
                }

                if (this.selectedType != ProxyType.SOCKS4 || !this.useAuth || this.usernameField.getText().trim().isEmpty() && this.passwordField.getText().trim().isEmpty()) {
                    if (this.useAuth && this.selectedType.supportsAuth()) {
                        String username = this.usernameField.getText().trim();
                        String password = this.passwordField.getText().trim();
                        if (!username.isEmpty() && password.isEmpty()) {
                            return "Не заполнен пароль, хотя используется аутентификация";
                        }
                    }

                    return "";
                } else {
                    return "SOCKS4 не поддерживает аутентификацию";
                }
            }
        }
    }
}
