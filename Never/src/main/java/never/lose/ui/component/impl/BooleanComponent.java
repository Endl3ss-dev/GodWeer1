package never.lose.ui.component.impl;

import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.ui.component.Component;
import never.lose.utils.font.Fonts;
import never.lose.utils.input.CursorManager;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.providers.ColorProvider;

public class BooleanComponent extends Component {

    private final BooleanSetting setting;
    private boolean binding;

    public BooleanComponent(BooleanSetting setting) {
        this.setting = setting;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float alpha = Math.max(Math.min(getAlphaAnimSetting().getValue(), 1), 0) * Math.max(Math.min(getAlphaAnim().getValue(), 1), 0);
        int alphaInt = (int) (255 * alpha);

        float toggleW = 15f;
        float toggleH = 8f;
        float toggleX = x + width - toggleW - 2.5f;
        float toggleY = y + 4f;

        if (Hover.isHovered(mouseX, mouseY, toggleX, toggleY, toggleW, toggleH)) {
            CursorManager.requestHand();
        }

        Draw.text(Fonts.REGULAR, binding ? "Binding..." : setting.getName(),
                x + 4.5f, y + 5f, ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt), 7.5f, 0.4f, 1f, width - toggleW - 8f);

        setting.getAnimation().run(setting.getValue());
        float anim = setting.getAnimation().getValue();

        if (anim > 0.01f) {
            int indicator = ColorProvider.setAlpha(ColorProvider.getColorIndicator(), alphaInt);
            int inactiveIndicator = ColorProvider.setAlpha(ColorProvider.getColorInactiveIndicator(), alphaInt);
            int bgColor = ColorProvider.interpolateColor(inactiveIndicator, indicator, anim);
            Draw.round(toggleX, toggleY, toggleW, toggleH, toggleH / 2f, bgColor);
        } else {
            Draw.round(toggleX, toggleY, toggleW, toggleH, toggleH / 2f, ColorProvider.setAlpha(ColorProvider.getColorInactiveIndicator(), alphaInt));
        }

        float knobSize = toggleH - 1f;
        float knobMinX = toggleX + 0.5f;
        float knobMaxX = toggleX + toggleW - knobSize - 0.5f;
        float knobX = knobMinX + (knobMaxX - knobMinX) * anim;
        float knobY = toggleY + 0.5f;
        Draw.circle(knobX + knobSize / 2f, knobY + knobSize / 2f, knobSize / 2f, ColorProvider.setAlpha(ColorProvider.getColorSliderCircle(), alphaInt));

        setHeight(16);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        float toggleW = 15f;
        float toggleH = 8f;
        float toggleX = x + width - toggleW - 2.5f;
        float toggleY = y + 4f;

        if (Hover.isHovered(mouseX, mouseY, toggleX, toggleY, toggleW, toggleH)) {
            if (button == 0) setting.setValue(!setting.getValue());
            if (button == 2 && binding) {
                binding = false;
                return;
            }
            if (binding) {
                setting.setKey(button);
                binding = false;
            }
            if (button == 2) binding = true;
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                binding = false;
                return;
            }
            if (keyCode == GLFW.GLFW_KEY_DELETE) {
                setting.setKey(-1);
                return;
            }
            setting.setKey(keyCode);
            binding = false;
        }
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
