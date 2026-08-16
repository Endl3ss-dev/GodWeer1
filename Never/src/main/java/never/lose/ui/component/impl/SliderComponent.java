package never.lose.ui.component.impl;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import never.lose.modules.api.setting.SliderSetting;
import never.lose.ui.component.Component;
import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;
import never.lose.utils.font.Fonts;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.providers.ColorProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderComponent extends Component {

    private final SliderSetting setting;
    private boolean drag;
    private final Animation sliderAnimation = new Animation(Easing.QUINTIC_OUT, 100);

    public SliderComponent(SliderSetting setting) {
        this.setting = setting;
    }

    private double round(double num, double increment) {
        double v = (double) Math.round(num / increment) * increment;
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private String formatNumber(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float alpha = Math.min(getAlphaAnimSetting().getValue(), 1) * Math.max(Math.min(getAlphaAnim().getValue(), 1), 0);
        int alphaInt = (int) (255 * alpha);

        String numberText = formatNumber(setting.getValue());
        float trackWidth = width - 9f;

        sliderAnimation.run((float) (trackWidth * (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin())));

        Draw.text(Fonts.REGULAR, setting.getName(), x + 4.5f, y + 3f,
                ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt), 7.5f, 0.6f, 1.0f, trackWidth);

        Draw.text(Fonts.REGULAR, numberText, x + width - 4.5f - Draw.width(Fonts.REGULAR, numberText, 7.5f), y + 1f,
                ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), alphaInt), 7.5f);

        float trackY = y + 14f;
        Draw.round(x + 3f, trackY - 3.5f, trackWidth + 1, 4, 1f, ColorProvider.setAlpha(ColorProvider.getColorSliderWindow(), (int) (100 * alpha)));
        Draw.round(x + 3.5f, trackY - 3, trackWidth, 3, 1f, ColorProvider.setAlpha(ColorProvider.getColorSliderWindow(), alphaInt));

        float fillWidth = MathHelper.clamp(sliderAnimation.getValue(), 0, trackWidth);
        int sliderColor = ColorProvider.setAlpha(ColorProvider.getColorSlider(), alphaInt);
        Draw.round(x + 3.5f, trackY - 3.5f, fillWidth, 4, 1f, sliderColor);

        float circleSize = drag ? 7f : 5.5f;
        float circleX = x + 3.5f + fillWidth;
        float circleY = trackY - 1.5f;
        Draw.round(circleX - circleSize / 2f, circleY - circleSize / 2f, circleSize, circleSize, circleSize / 2f,
                ColorProvider.setAlpha(ColorProvider.getColorSliderCircle(), alphaInt));

        if (drag) {
            double val = (mouseX - (x + 3.5f)) / trackWidth * (setting.getMax() - setting.getMin()) + setting.getMin();
            setting.setValue(MathHelper.clamp(round(val, setting.getStep()), setting.getMin(), setting.getMax()));
        }

        setHeight(15);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (Hover.isHovered(mouseX, mouseY, x + 3f, y + 8f, width - 6f, 8f) && button == 0) {
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
        return setting.visible.get();
    }
}
