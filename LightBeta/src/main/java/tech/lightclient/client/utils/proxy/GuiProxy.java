package tech.lightclient.client.utils.proxy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

public class GuiProxy extends Screen {
    private boolean isSocks4 = false;

    private TextFieldWidget ipPort;
    private TextFieldWidget username;
    private TextFieldWidget password;
    private CheckboxWidget enabledCheck;

    private Screen parentScreen;

    private String msg = "";

    private int[] positionY;
    private int positionX;

    public GuiProxy(Screen parentScreen) {
        super(Text.literal("Proxy"));
        this.parentScreen = parentScreen;
    }

    private static boolean isValidIpPort(String ipP) {
        if (ipP == null || ipP.isEmpty()) return false;
        String[] split = ipP.split(":");
        if (split.length > 1) {
            if (!StringUtils.isNumeric(split[1])) return false;
            try {
                int port = Integer.parseInt(split[1]);
                if (port < 0 || port > 0xFFFF) return false;
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    private boolean checkProxy() {
        if (!isValidIpPort(ipPort.getText())) {
            this.ipPort.setFocused(true);
            return false;
        }
        return true;
    }

    private void centerButtons(int amount, int buttonLength, int gap) {
        positionX = (this.width / 2) - (buttonLength / 2);
        positionY = new int[amount];
        int center = (this.height + amount * gap) / 2;
        int buttonStarts = center - (amount * gap);
        for (int i = 0; i != amount; i++) {
            positionY[i] = buttonStarts + (gap * i);
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.isEscape()) {
            MinecraftClient.getInstance().setScreen(parentScreen);
            return true;
        }
        super.keyPressed(input);
        msg = "";
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.render(context, mouseX, mouseY, partialTicks);

        if (enabledCheck.isChecked() && !isValidIpPort(ipPort.getText())) {
            enabledCheck.onPress(null);
        }

        context.drawTextWithShadow(this.textRenderer, Text.literal("Введите айпи адрес и порт. Пример ниже"), this.width / 2 - 106, positionY[3] - 15, 0xA0A0A0);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Айпи:Порт >"), this.width / 2 - 140, positionY[3] + 15, 0xA0A0A0);

        this.ipPort.render(context, mouseX, mouseY, partialTicks);

        context.drawTextWithShadow(this.textRenderer, Text.literal("Никнейм >"), this.width / 2 - 131, positionY[4] + 15, 0xA0A0A0);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Пароль >"), this.width / 2 - 126, positionY[5] + 15, 0xA0A0A0);
        this.username.render(context, mouseX, mouseY, partialTicks);
        this.password.render(context, mouseX, mouseY, partialTicks);

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(msg), this.width / 2, positionY[6] + 5, 0xA0A0A0);
    }

    @Override
    public void init() {
        int buttonLength = 160;
        centerButtons(10, buttonLength, 26);

        ProxyConfig config = ProxyConfig.getInstance();
        Proxy currentProxy = config.getDefaultProxy();

        isSocks4 = currentProxy.type == Proxy.ProxyType.SOCKS4;

        this.ipPort = new TextFieldWidget(this.textRenderer, positionX, positionY[3] + 10, buttonLength, 20, Text.literal(""));
        this.ipPort.setText(currentProxy.ipPort);
        this.ipPort.setMaxLength(1024);
        this.ipPort.setFocused(true);
        this.addSelectableChild(this.ipPort);

        this.username = new TextFieldWidget(this.textRenderer, positionX, positionY[4] + 10, buttonLength, 20, Text.literal(""));
        this.username.setMaxLength(255);
        this.username.setText(currentProxy.username);
        this.addSelectableChild(this.username);

        this.password = new TextFieldWidget(this.textRenderer, positionX, positionY[5] + 10, buttonLength, 20, Text.literal(""));
        this.password.setMaxLength(255);
        this.password.setText(currentProxy.password);
        this.addSelectableChild(this.password);

        int posXButtons = (this.width / 2) - (((buttonLength / 2) * 3) / 2);

        ButtonWidget apply = ButtonWidget.builder(Text.literal("\u041f\u0440\u0438\u043c\u0435\u043d\u0438\u0442\u044c"), button -> {
            ProxyConfig cfg = ProxyConfig.getInstance();

            if (enabledCheck.isChecked()) {
                if (checkProxy()) {
                    Proxy newProxy = new Proxy(isSocks4, ipPort.getText(), username.getText(), password.getText());
                    cfg.setDefaultProxy(newProxy);
                    cfg.setProxyEnabled(true);
                    cfg.save();
                    ProxyConfig.getInstance().setLastUsedProxy(new Proxy(newProxy));
                    MinecraftClient.getInstance().setScreen(parentScreen);
                }
            } else {
                Proxy newProxy = new Proxy(isSocks4, ipPort.getText(), username.getText(), password.getText());
                cfg.setDefaultProxy(newProxy);
                cfg.setProxyEnabled(false);
                cfg.save();
                MinecraftClient.getInstance().setScreen(parentScreen);
            }
        }).dimensions(posXButtons + (buttonLength / 2 - 62) * 2, positionY[7] - 10, buttonLength / 2 + 3, 20).build();
        this.addDrawableChild(apply);

        CheckboxWidget.Builder checkboxBuilder = CheckboxWidget.builder(Text.literal("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043f\u0440\u043e\u043a\u0441\u0438"), this.textRenderer);
        checkboxBuilder.pos((this.width / 2 - 34) - (13 + textRenderer.getWidth(Text.literal("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043f\u0440\u043e\u043a\u0441\u0438"))) / 2, positionY[7] + 15);
        if (config.isProxyEnabled()) {
            checkboxBuilder.checked(true);
        }
        this.enabledCheck = checkboxBuilder.build();
        this.addDrawableChild(this.enabledCheck);

        ButtonWidget cancel = ButtonWidget.builder(Text.literal("Отменить"), (button) -> {
            MinecraftClient.getInstance().setScreen(parentScreen);
        }).dimensions(posXButtons + (buttonLength / 2 - 16) * 2, positionY[7] - 10, buttonLength / 2 - 3, 20).build();
        this.addDrawableChild(cancel);
    }

    @Override
    public void close() {
        msg = "";
        MinecraftClient.getInstance().setScreen(parentScreen);
    }
}
