package never.lose.ui.component.impl;

import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import never.lose.modules.api.setting.BindSetting;
import never.lose.ui.component.Component;
import never.lose.utils.font.Fonts;
import never.lose.utils.input.CursorManager;
import never.lose.utils.input.KeyStorage;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.providers.ColorProvider;

public class BindComponent extends Component {

    private final BindSetting setting;
    private boolean binding;

    private static final float SETTING_HEIGHT = 14f;
    private static final float BIND_H = 10f;
    private static final float PADDING = 4.5f;
    private static final float RADIUS = 1f;

    public BindComponent(BindSetting setting) {
        this.setting = setting;
    }

    private String getKeyText() {
        if (binding) return "...";
        return (setting.getValue() == -1) ? "None" : KeyStorage.getKey(setting.getValue());
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float alpha = Math.max(Math.min(getAlphaAnimSetting().getValue(), 1), 0) * Math.max(Math.min(getAlphaAnim().getValue(), 1), 0);
        int alphaInt = (int) (255 * alpha);

        if (alpha < 0.02f) return;

        String keyText = getKeyText();
        float keyTextW = Draw.width(Fonts.REGULAR, keyText, 6.5f);
        float bindBoxW = Math.max(16f, keyTextW + 8f);
        float bindX = x + width - bindBoxW - PADDING;
        float bindY = y + (SETTING_HEIGHT - BIND_H) / 2f;

        if (Hover.isHovered(mouseX, mouseY, bindX, bindY, bindBoxW, BIND_H)) {
            CursorManager.requestHand();
        }

        Draw.text(Fonts.REGULAR, setting.getName(), x + PADDING, y + 3.5f,
                ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt), 7.5f);

        boolean hasBind = setting.getValue() != -1;
        int bgColor;
        if (binding) {
            bgColor = ColorProvider.setAlpha(ColorProvider.getColorButton(), (int) (35 * alpha));
        } else if (hasBind) {
            bgColor = ColorProvider.setAlpha(ColorProvider.getColorButton(), (int) (80 * alpha));
        } else {
            bgColor = ColorProvider.setAlpha(ColorProvider.getColorInactiveButton(), (int) (80 * alpha));
        }
        Draw.round(bindX, bindY, bindBoxW, BIND_H, RADIUS, bgColor);

        int textColor = (binding || hasBind)
                ? ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt)
                : ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), alphaInt);
        float keyX = bindX + (bindBoxW - keyTextW) / 2f;
        Draw.text(Fonts.REGULAR, keyText, keyX, bindY + 1.25f, textColor, 7.5f);

        setHeight(SETTING_HEIGHT);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        String keyText = getKeyText();
        float keyTextW = Draw.width(Fonts.REGULAR, keyText, 7.5f);
        float bindBoxW = Math.max(16f, keyTextW + 8f);
        float bindX = x + width - bindBoxW - PADDING;
        float bindY = y + (SETTING_HEIGHT - BIND_H) / 2f;

        if (binding) {
            if (button != 0) setting.setValue(button);
            binding = false;
        } else if (Hover.isHovered(mouseX, mouseY, bindX, bindY, bindBoxW, BIND_H) && button == 0) {
            binding = true;
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                setting.setValue(-1);
            } else {
                setting.setValue(keyCode);
            }
            binding = false;
        }
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
