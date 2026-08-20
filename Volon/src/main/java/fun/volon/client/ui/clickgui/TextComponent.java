package fun.volon.client.ui.clickgui;

import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.input.CursorManager;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.api.utils.scissor.ScissorUtils;
import fun.volon.client.modules.settings.implement.TextSetting;

public class TextComponent extends Component {
    private final TextSetting setting;
    private boolean editing;

    public TextComponent(TextSetting setting) {
        this.setting = setting;
        this.height = 16f;
    }

    @Override
    public boolean isVisible() {
        return setting.visible();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        float alpha = Math.max(Math.min(getAlphaAnimSetting().getValue(), 1), 0);
        int alphaInt = (int) (255 * alpha);

        Font font = Fonts.getFont("sf_regular", 13);
        float boxW = 54f;
        float boxX = x + width - boxW - 4.5f;

        if (HoveringUtils.isHovered(mouseX, mouseY, boxX, y + 3f, boxW, 10f)) {
            CursorManager.requestIBeam();
        }

        drawText(matrices, font, setting.displayName(), x + 4.5f, y + 4.2f,
                ColorUtils.setAlphaColor(ColorUtils.getColorText(), alphaInt));
        int boxColor = editing
                ? ColorUtils.setAlphaColor(ColorUtils.getColorField(), alphaInt)
                : ColorUtils.setAlphaColor(ColorUtils.getColorInactiveField(), alphaInt);
        RenderUtils.drawRoundedRect(matrices, boxX, y + 3f, boxW, 10f, 2f, boxColor);

        if (font != null) {
            String value = setting.get();
            String draw = value.isEmpty() ? "..." : value;
            if (editing && (System.currentTimeMillis() / 500L) % 2L == 0L) draw += "|";
            int textColor = value.isEmpty()
                    ? ColorUtils.setAlphaColor(ColorUtils.getColorInactiveText(), alphaInt)
                    : ColorUtils.setAlphaColor(ColorUtils.getColorText(), alphaInt);
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(boxX + 3f, y + 3f, boxW - 6f, 10f);
            drawText(matrices, font, draw, boxX + 3f, y + 4.2f, textColor);
            ScissorUtils.pop();
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) editing = HoveringUtils.isHovered(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!editing) return;
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            editing = false;
        } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !setting.get().isEmpty()) {
            setting.setText(setting.get().substring(0, setting.get().length() - 1));
        }
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        if (editing && !Character.isISOControl(chr)) {
            setting.setText(setting.get() + chr);
        }
    }
}
