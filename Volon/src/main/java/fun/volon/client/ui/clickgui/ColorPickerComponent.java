package fun.volon.client.ui.clickgui;

import net.minecraft.client.util.math.MatrixStack;
import fun.volon.api.utils.animation.gui.GuiAnimation;
import fun.volon.api.utils.animation.gui.GuiEasing;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.input.CursorManager;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.api.utils.scissor.ScissorUtils;
import fun.volon.client.modules.settings.implement.ColorSetting;

import java.awt.Color;

public class ColorPickerComponent extends Component {
    private final ColorSetting setting;
    private boolean opened;
    private final GuiAnimation openAnim = new GuiAnimation(GuiEasing.QUINTIC_OUT, 300);

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
        float alpha = Math.min(getAlphaAnimSetting().getValue(), 1);
        int alphaInt = (int) (255 * alpha);

        openAnim.run(opened);
        float expandedHeight = 55f;

        Font font = Fonts.getFont("sf_regular", 13);
        float previewSize = 8f;
        float previewX = x + width - previewSize - 5;
        float previewY = y + 2.5f;

        float nameMaxWidth = previewX - (x + 4.5f) - 2f;
        drawTextScrolling(matrices, font, setting.displayName(),
                x + 4.5f, y + 3, nameMaxWidth,
                ColorUtils.rgba(255, 255, 255, alphaInt));

        if (HoveringUtils.isHovered(mouseX, mouseY, previewX, previewY, previewSize, previewSize))
            CursorManager.requestHand();

        RenderUtils.drawRoundedRect(matrices, previewX - 0.5f, previewY - 0.5f, previewSize + 1, previewSize + 1, 1.5f,
                ColorUtils.rgba(60, 60, 65, (int) (150 * alpha)));
        RenderUtils.drawRoundedRect(matrices, previewX, previewY, previewSize, previewSize, 1.5f, setting.getValue());

        if (openAnim.getValue() > 0.01f) {
            float pickerY = y + 13;
            float animH = openAnim.getValue() * expandedHeight;
            float animAlpha = alpha * openAnim.getValue();
            int animAlphaInt = (int) (255 * animAlpha);

            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(x, pickerY, width, animH);

            RenderUtils.drawRoundedRect(matrices, x + 4.5f, pickerY - 0.5f, width - 9, expandedHeight + 1, 2.5f,
                    ColorUtils.rgba(60, 60, 65, (int) (150 * animAlpha)));
            RenderUtils.drawRoundedRect(matrices, x + 5, pickerY, width - 10, expandedHeight, 2.5f,
                    ColorUtils.rgba(20, 20, 25, animAlphaInt));

            float svX = x + 9;
            float svY = pickerY + 4;
            float svSize = 40;

            int cHue = ColorUtils.setAlphaColor(Color.HSBtoRGB(hue, 1F, 1F), animAlphaInt);
            int cWhite = ColorUtils.rgba(255, 255, 255, animAlphaInt);
            int cClearWhite = ColorUtils.rgba(255, 255, 255, 0);
            int cBlack = ColorUtils.rgba(0, 0, 0, animAlphaInt);
            int cClearBlack = ColorUtils.rgba(0, 0, 0, 0);

            RenderUtils.drawRoundedRect(matrices, svX, svY, svSize, svSize, 2f, cHue);
            RenderUtils.drawGradientRect(matrices, svX, svY, svSize, svSize, 2f, cWhite, cClearWhite, cWhite, cClearWhite);
            RenderUtils.drawGradientRect(matrices, svX, svY, svSize, svSize, 2f, cClearBlack, cClearBlack, cBlack, cBlack);

            float svCursorX = svX + saturation * svSize;
            float svCursorY = svY + (1 - value) * svSize;
            RenderUtils.drawRoundedRect(matrices, svCursorX - 2.5f, svCursorY - 2.5f, 5, 5, 2.5f,
                    ColorUtils.rgba(0, 0, 0, (int) (180 * animAlpha)));
            RenderUtils.drawRoundedRect(matrices, svCursorX - 1.5f, svCursorY - 1.5f, 3, 3, 1.5f,
                    ColorUtils.rgba(255, 255, 255, animAlphaInt));

            float hueX = svX + svSize + 6;
            float hueY = svY;
            float hueW = 6;

            for (float i = 0; i <= svSize; i += 0.5f) {
                int color = ColorUtils.setAlphaColor(Color.HSBtoRGB(i / svSize, 1F, 1F), animAlphaInt);
                RenderUtils.drawRoundedRect(matrices, hueX, hueY + i, hueW, 1f, 0f, color);
            }

            float hueCursorY = hueY + hue * svSize;
            RenderUtils.drawRoundedRect(matrices, hueX - 1.5f, hueCursorY - 2.5f, hueW + 3f, 5f, 2f,
                    ColorUtils.rgba(0, 0, 0, (int) (180 * animAlpha)));
            RenderUtils.drawRoundedRect(matrices, hueX - 0.5f, hueCursorY - 1.5f, hueW + 1f, 3f, 1f,
                    ColorUtils.rgba(255, 255, 255, animAlphaInt));

            if (draggingSV) {
                saturation = Math.max(0, Math.min(1, ((float) mouseX - svX) / svSize));
                value = 1F - Math.max(0, Math.min(1, ((float) mouseY - svY) / svSize));
                syncColor();
            } else if (draggingHue) {
                hue = Math.max(0, Math.min(1, ((float) mouseY - hueY) / svSize));
                syncColor();
            }

            ScissorUtils.pop();
        }

        height = 13 + (openAnim.getValue() * expandedHeight);
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

        if (HoveringUtils.isHovered(mouseX, mouseY, previewX, previewY, previewSize, previewSize) && button == 1) {
            opened = !opened;
            return;
        }

        if (opened && button == 0) {
            float svX = x + 9;
            float svY = y + 13 + 4;
            float svSize = 40;
            float hueX = svX + svSize + 6;

            if (HoveringUtils.isHovered(mouseX, mouseY, svX, svY, svSize, svSize)) draggingSV = true;
            else if (HoveringUtils.isHovered(mouseX, mouseY, hueX - 2, svY, 10, svSize)) draggingHue = true;
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        draggingSV = false;
        draggingHue = false;
    }

    @Override
    public boolean isVisible() {
        return setting.visible();
    }
}
