package fun.volon.client.ui.clickgui;

import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import fun.volon.api.utils.animation.gui.GuiAnimation;
import fun.volon.api.utils.animation.gui.GuiEasing;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.input.CursorManager;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.settings.implement.BooleanSetting;

public class BooleanComponent extends Component {
    private final BooleanSetting setting;
    private final GuiAnimation animation = new GuiAnimation(GuiEasing.QUINTIC_OUT, 400);
    private boolean binding;

    public BooleanComponent(BooleanSetting setting) {
        this.setting = setting;
        this.animation.setValue(setting.isState() ? 1f : 0f);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        float alpha = Math.max(Math.min(getAlphaAnimSetting().getValue(), 1), 0);
        int alphaInt = (int) (255 * alpha);

        float toggleW = 15f;
        float toggleH = 8f;
        float toggleX = x + width - toggleW - 2.5f;
        float toggleY = y + 4f;

        if (HoveringUtils.isHovered(mouseX, mouseY, toggleX, toggleY, toggleW, toggleH)) {
            CursorManager.requestHand();
        }

        Font font = Fonts.getFont("sf_regular", 13);
        float nameMaxWidth = toggleX - (x + 4.5f) - 2f;
        drawTextScrolling(matrices, font, binding ? "Binding..." : setting.displayName(),
                x + 4.5f, y + 5f, nameMaxWidth,
                ColorUtils.setAlphaColor(ColorUtils.getColorText(), alphaInt));

        animation.run(setting.isState());
        float anim = animation.getValue();

        if (anim > 0.01f) {
            int indicator = ColorUtils.getColorIndicator();
            int darkIndicator = ColorUtils.rgba(
                    Math.max(0, (int) (((indicator >> 16) & 0xFF) * 0.45f)),
                    Math.max(0, (int) (((indicator >> 8) & 0xFF) * 0.45f)),
                    Math.max(0, (int) ((indicator & 0xFF) * 0.45f)),
                    alphaInt
            );
            int lightIndicator = ColorUtils.setAlphaColor(indicator, alphaInt);
            int inactiveIndicator = ColorUtils.setAlphaColor(ColorUtils.getColorInactiveIndicator(), alphaInt);
            int bgLeft = ColorUtils.interpolateColor(inactiveIndicator, darkIndicator, anim);
            int bgRight = ColorUtils.interpolateColor(inactiveIndicator, lightIndicator, anim);
            RenderUtils.drawGradientRect(matrices, toggleX, toggleY, toggleW, toggleH, 1.5f, bgLeft, bgRight, bgLeft, bgRight);
        } else {
            RenderUtils.drawRoundedRect(matrices, toggleX, toggleY, toggleW, toggleH, 1.5f,
                    ColorUtils.setAlphaColor(ColorUtils.getColorInactiveIndicator(), alphaInt));
        }

        float knobSize = toggleH - 1f;
        float knobMinX = toggleX + 0.5f;
        float knobMaxX = toggleX + toggleW - knobSize - 0.5f;
        float knobX = knobMinX + (knobMaxX - knobMinX) * anim;
        float knobY = toggleY + 0.5f;
        RenderUtils.drawRoundedRect(matrices, knobX, knobY, knobSize, knobSize, 1.5f,
                ColorUtils.setAlphaColor(ColorUtils.getColorSliderCircle(), alphaInt));

        height = 16f;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        float toggleW = 15f;
        float toggleH = 8f;
        float toggleX = x + width - toggleW - 2.5f;
        float toggleY = y + 4f;

        if (HoveringUtils.isHovered(mouseX, mouseY, toggleX, toggleY, toggleW, toggleH)) {
            if (button == 0) setting.setState(!setting.isState());
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
        return setting.visible();
    }
}
