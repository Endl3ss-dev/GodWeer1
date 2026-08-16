package never.lose.ui.component.impl;

import net.minecraft.client.util.math.MatrixStack;
import never.lose.modules.api.setting.ColorSetting;
import never.lose.ui.component.Component;
import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;
import never.lose.utils.font.Fonts;
import never.lose.utils.input.CursorManager;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.Scissor;
import never.lose.utils.render.providers.ColorProvider;

import java.awt.Color;

public class ColorPickerComponent extends Component {

    private final ColorSetting setting;
    private boolean opened;
    private final Animation openAnim = new Animation(Easing.QUINTIC_OUT, 300);

    private boolean draggingSV, draggingHue;
    private float hue, saturation, value;

    public ColorPickerComponent(ColorSetting setting) {
        this.setting = setting;
        updateHSV();
    }

    private void updateHSV() {
        Color c = new Color(setting.getValue(), true);
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        float alpha = Math.min(getAlphaAnimSetting().getValue(), 1) * Math.max(Math.min(getAlphaAnim().getValue(), 1), 0);
        int alphaInt = (int) (255 * alpha);

        openAnim.run(opened);
        float expandedHeight = 55f;

        Draw.text(Fonts.REGULAR, setting.getName(), x + 4.5f, y + 3, ColorProvider.rgba(255, 255, 255, alphaInt), 7.5f);

        float previewSize = 8f;
        float previewX = x + width - previewSize - 5;
        float previewY = y + 2.5f;

        if (Hover.isHovered(mouseX, mouseY, previewX, previewY, previewSize, previewSize)) {
            CursorManager.requestHand();
        }

        Draw.round(previewX - 0.5f, previewY - 0.5f, previewSize + 1, previewSize + 1, 1.5f, ColorProvider.rgba(48, 66, 122, (int) (140 * alpha)));
        Draw.round(previewX, previewY, previewSize, previewSize, 1.5f, setting.getValue());

        if (openAnim.getValue() > 0.01f) {
            float pickerY = y + 13;
            float animH = openAnim.getValue() * expandedHeight;
            float animAlpha = alpha * openAnim.getValue();
            int animAlphaInt = (int) (255 * animAlpha);

            Scissor.push();
            Scissor.setFromComponentCoordinates(x, pickerY, width, animH);

            Draw.round(x + 4.5f, pickerY - 0.5f, width - 9, expandedHeight + 1, 2.5f, ColorProvider.rgba(48, 66, 122, (int) (140 * animAlpha)));
            Draw.round(x + 5, pickerY, width - 10, expandedHeight, 2.5f, ColorProvider.rgba(16, 22, 42, animAlphaInt));

            float svX = x + 9;
            float svY = pickerY + 4;
            float svSize = 40;

            int cHue = ColorProvider.setAlpha(Color.HSBtoRGB(hue, 1F, 1F), animAlphaInt);
            int cWhite = ColorProvider.rgba(255, 255, 255, animAlphaInt);
            int cClearWhite = ColorProvider.rgba(255, 255, 255, 0);
            int cBlack = ColorProvider.rgba(0, 0, 0, animAlphaInt);
            int cClearBlack = ColorProvider.rgba(0, 0, 0, 0);

            Draw.round(svX, svY, svSize, svSize, 2f, cHue);
            Draw.round(svX, svY, svSize, svSize, 2f, cWhite, cWhite, cClearWhite, cClearWhite);
            Draw.round(svX, svY, svSize, svSize, 2f, cClearBlack, cBlack, cBlack, cClearBlack);

            float svCursorX = svX + saturation * svSize;
            float svCursorY = svY + (1 - value) * svSize;
            Draw.round(svCursorX - 2.5f, svCursorY - 2.5f, 5, 5, 2.5f, ColorProvider.rgba(0, 0, 0, (int) (180 * animAlpha)));
            Draw.round(svCursorX - 1.5f, svCursorY - 1.5f, 3, 3, 1.5f, ColorProvider.rgba(255, 255, 255, animAlphaInt));

            float hueX = svX + svSize + 6;
            float hueY = svY;
            float hueW = 6;

            for (float i = 0; i <= svSize; i += 0.5f) {
                int color = ColorProvider.setAlpha(Color.HSBtoRGB(i / svSize, 1F, 1F), animAlphaInt);
                Draw.round(hueX, hueY + i, hueW, 1f, 0f, color);
            }

            float hueCursorY = hueY + hue * svSize;
            Draw.round(hueX - 1.5f, hueCursorY - 2.5f, hueW + 3f, 5f, 2f, ColorProvider.rgba(0, 0, 0, (int) (180 * animAlpha)));
            Draw.round(hueX - 0.5f, hueCursorY - 1.5f, hueW + 1f, 3f, 1f, ColorProvider.rgba(255, 255, 255, animAlphaInt));

            if (draggingSV) {
                saturation = Math.max(0, Math.min(1, (mouseX - svX) / svSize));
                value = 1F - Math.max(0, Math.min(1, (mouseY - svY) / svSize));
                syncColor();
            } else if (draggingHue) {
                hue = Math.max(0, Math.min(1, (mouseY - hueY) / svSize));
                syncColor();
            }

            Scissor.unset();
            Scissor.pop();
        }

        setHeight(13 + (openAnim.getValue() * expandedHeight));
    }

    private void syncColor() {
        int rgb = Color.HSBtoRGB(hue, saturation, value);
        setting.setValue(rgb | 0xFF000000);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        float previewSize = 8f;
        float previewX = x + width - previewSize - 5;
        float previewY = y + 2.5f;

        if (Hover.isHovered(mouseX, mouseY, previewX, previewY, previewSize, previewSize) && button == 1) {
            opened = !opened;
            return;
        }

        if (opened && button == 0) {
            float svX = x + 9;
            float svY = y + 13 + 4;
            float svSize = 40;
            float hueX = svX + svSize + 6;

            if (Hover.isHovered(mouseX, mouseY, svX, svY, svSize, svSize)) draggingSV = true;
            else if (Hover.isHovered(mouseX, mouseY, hueX - 2, svY, 10, svSize)) draggingHue = true;
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        draggingSV = false;
        draggingHue = false;
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
