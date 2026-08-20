package fun.volon.client.ui.clickgui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import fun.volon.api.utils.animation.gui.GuiAnimation;
import fun.volon.api.utils.animation.gui.GuiEasing;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.input.CursorManager;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.settings.implement.FloatSetting;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderComponent extends Component {
    private final FloatSetting setting;
    private boolean drag;
    private final GuiAnimation sliderAnimation = new GuiAnimation(GuiEasing.QUINTIC_OUT, 100);

    public SliderComponent(FloatSetting setting) {
        this.setting = setting;
    }

    private double round(double num, double increment) {
        double v = Math.round(num / increment) * increment;
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private String formatNumber(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        float alpha = Math.min(getAlphaAnimSetting().getValue(), 1);
        int alphaInt = (int) (255 * alpha);

        String numberText = formatNumber(setting.get());
        float trackWidth = width - 9f;

        sliderAnimation.run((float) (trackWidth * (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin())));

        Font font = Fonts.getFont("sf_regular", 13);
        if (font != null) {
            float numberW = font.getStringWidth(numberText);
            float nameMaxWidth = width - 9f - numberW - 2f;
            drawTextScrolling(matrices, font, setting.displayName(),
                    x + 4.5f, y + 3f, nameMaxWidth,
                    ColorUtils.setAlphaColor(ColorUtils.getColorText(), alphaInt));
            drawText(matrices, font, numberText,
                    x + width - 4.5f - numberW, y + 1f,
                    ColorUtils.setAlphaColor(ColorUtils.getColorInactiveText(), alphaInt));
        }

        float trackY = y + 14f;
        RenderUtils.drawRoundedRect(matrices, x + 3f, trackY - 3.5f, trackWidth + 1, 4, 1f,
                ColorUtils.setAlphaColor(ColorUtils.getColorSliderWindow(), (int) (100 * alpha)));
        RenderUtils.drawRoundedRect(matrices, x + 3.5f, trackY - 3, trackWidth, 3, 1f,
                ColorUtils.setAlphaColor(ColorUtils.getColorSliderWindow(), alphaInt));

        float fillWidth = MathHelper.clamp(sliderAnimation.getValue(), 0, trackWidth);
        int sliderColor = ColorUtils.setAlphaColor(ColorUtils.getColorSlider(), alphaInt);
        int sliderBase = ColorUtils.getColorSlider();
        int sliderDark = ColorUtils.rgba(
                (int) (((sliderBase >> 16) & 0xFF) * 0.25f),
                (int) (((sliderBase >> 8) & 0xFF) * 0.25f),
                (int) ((sliderBase & 0xFF) * 0.25f),
                alphaInt
        );
        RenderUtils.drawGradientRect(matrices, x + 3.5f, trackY - 3.5f, fillWidth, 4, 1f,
                sliderDark, sliderColor, sliderDark, sliderColor);

        float circleX = x + 3.5f + fillWidth;
        RenderUtils.drawRoundedRect(matrices, circleX - 2.5f, trackY - 4f, 5, 5, 1.75f,
                ColorUtils.setAlphaColor(ColorUtils.getColorSliderCircle(), alphaInt));

        if (HoveringUtils.isHovered(mouseX, mouseY, x + 3f, y + 8f, width - 6f, 8f)) {
            CursorManager.requestHand();
        }

        if (drag) {
            double val = (mouseX - (x + 3.5f)) / trackWidth * (setting.getMax() - setting.getMin()) + setting.getMin();
            setting.setValue((float) MathHelper.clamp(round(val, setting.getIncrement()), setting.getMin(), setting.getMax()));
        }

        height = 15f;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (HoveringUtils.isHovered(mouseX, mouseY, x + 3f, y + 8f, width - 6f, 8f) && button == 0) {
            drag = true;
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        drag = false;
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) drag = false;
    }

    @Override
    public boolean isVisible() {
        return setting.visible();
    }
}
