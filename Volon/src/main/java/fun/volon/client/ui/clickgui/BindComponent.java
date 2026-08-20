package fun.volon.client.ui.clickgui;

import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.input.CursorManager;
import fun.volon.api.utils.input.KeyBoardUtils;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.settings.implement.BindSetting;

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
        return setting.getKey() == -1 ? "None" : KeyBoardUtils.getBindName(setting.getKey());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        float alpha = Math.max(Math.min(getAlphaAnimSetting().getValue(), 1), 0);
        int alphaInt = (int) (255 * alpha);

        if (alpha < 0.02f) return;

        Font font = Fonts.getFont("sf_regular", 13);
        String keyText = getKeyText();
        float keyTextW = font == null ? 18f : font.getStringWidth(keyText);
        float bindBoxW = Math.max(16f, keyTextW + 8f);
        float bindX = x + width - bindBoxW - PADDING;
        float bindY = y + (SETTING_HEIGHT - BIND_H) / 2f;

        if (HoveringUtils.isHovered(mouseX, mouseY, bindX, bindY, bindBoxW, BIND_H))
            CursorManager.requestHand();

        float nameMaxWidth = bindX - (x + PADDING) - 2f;
        drawTextScrolling(matrices, font, setting.displayName(),
                x + PADDING, y + 3.5f, nameMaxWidth,
                ColorUtils.setAlphaColor(ColorUtils.getColorText(), alphaInt));

        boolean hasBind = setting.getKey() != -1;
        int bgColor;
        if (binding) {
            bgColor = ColorUtils.setAlphaColor(ColorUtils.getColorButton(), (int) (35 * alpha));
        } else if (hasBind) {
            bgColor = ColorUtils.setAlphaColor(ColorUtils.getColorButton(), (int) (80 * alpha));
        } else {
            bgColor = ColorUtils.setAlphaColor(ColorUtils.getColorInactiveButton(), (int) (80 * alpha));
        }
        RenderUtils.drawRoundedRect(matrices, bindX, bindY, bindBoxW, BIND_H, RADIUS, bgColor);

        int textColor = (binding || hasBind)
                ? ColorUtils.setAlphaColor(ColorUtils.getColorText(), alphaInt)
                : ColorUtils.setAlphaColor(ColorUtils.getColorInactiveText(), alphaInt);
        float keyX = bindX + (bindBoxW - keyTextW) / 2f;
        drawText(matrices, font, keyText, keyX, bindY + 1.25f, textColor);

        height = SETTING_HEIGHT;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        Font font = Fonts.getFont("sf_regular", 13);
        String keyText = getKeyText();
        float keyTextW = font == null ? 18f : font.getStringWidth(keyText);
        float bindBoxW = Math.max(16f, keyTextW + 8f);
        float bindX = x + width - bindBoxW - PADDING;
        float bindY = y + (SETTING_HEIGHT - BIND_H) / 2f;

        if (binding) {
            if (button != 0) setting.setKey(KeyBoardUtils.createMouseBind(button));
            binding = false;
        } else if (HoveringUtils.isHovered(mouseX, mouseY, bindX, bindY, bindBoxW, BIND_H) && button == 0) {
            binding = true;
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                setting.setKey(-1);
            } else {
                setting.setKey(keyCode);
            }
            binding = false;
        }
    }

    @Override
    public boolean isVisible() {
        return setting.visible();
    }
}
