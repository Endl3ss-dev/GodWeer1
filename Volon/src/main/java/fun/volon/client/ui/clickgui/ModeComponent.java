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
import fun.volon.client.modules.settings.implement.ModeSetting;

import java.util.ArrayList;
import java.util.List;

public class ModeComponent extends Component {
    private final ModeSetting setting;
    private static final float NAME_HEIGHT = 10f;
    private static final float OPTION_H = 11f;
    private static final float GAP = 2f;
    private static final float PADDING = 4.5f;
    private static final float RADIUS = 1f;

    private final List<GuiAnimation> anims = new ArrayList<>();

    public ModeComponent(ModeSetting setting) {
        this.setting = setting;
        for (int i = 0; i < setting.getMods().size(); i++) {
            anims.add(new GuiAnimation(GuiEasing.QUINTIC_OUT, 250));
        }
    }

    private float optionWidth(Font font, String mode) {
        String label = setting.displayMode(mode);
        return (font == null ? label.length() * 4f : font.getStringWidth(label)) + 8f;
    }

    private float calcHeight(Font font) {
        float maxRowWidth = width - PADDING * 2;
        float rowWidth = 0;
        float totalHeight = NAME_HEIGHT + GAP;
        boolean firstInRow = true;

        for (String mode : setting.getMods()) {
            float ow = optionWidth(font, mode);
            if (!firstInRow && rowWidth + ow > maxRowWidth) {
                totalHeight += OPTION_H + GAP;
                rowWidth = ow + GAP;
            } else {
                rowWidth += ow + GAP;
                firstInRow = false;
            }
        }
        totalHeight += OPTION_H;
        return totalHeight;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        float animValue = getAlphaAnimSetting().getValue();
        float alpha = Math.max(Math.min(animValue * getAlphaAnim().getValue(), 1), 0);
        int alphaInt = (int) (255 * alpha);

        if (alpha < 0.02f) return;

        Font font = Fonts.getFont("sf_regular", 13);
        float totalHeight = calcHeight(font);
        height = (totalHeight + 2f) * animValue;

        drawText(matrices, font, setting.displayName(), x + PADDING, y + 1.5f,
                ColorUtils.setAlphaColor(ColorUtils.getColorText(), alphaInt));

        float curX = x + PADDING;
        float curY = y + NAME_HEIGHT + GAP;
        boolean firstInRow = true;
        int i = 0;

        for (String mode : setting.getMods()) {
            float ow = optionWidth(font, mode);
            if (!firstInRow && curX + ow > x + width - PADDING) {
                curX = x + PADDING;
                curY += OPTION_H + GAP;
            }
            firstInRow = false;

            boolean selected = setting.is(mode);
            GuiAnimation anim = anims.get(i);
            anim.run(selected);
            float av = anim.getValue();

            if (HoveringUtils.isHovered(mouseX, mouseY, curX, curY, ow, OPTION_H))
                CursorManager.requestHand();

            int bgColor = ColorUtils.interpolateColor(
                    ColorUtils.setAlphaColor(ColorUtils.getColorInactiveButton(), alphaInt),
                    ColorUtils.setAlphaColor(ColorUtils.getColorButton(), alphaInt),
                    av
            );
            RenderUtils.drawRoundedRect(matrices, curX, curY, ow, OPTION_H, RADIUS, bgColor);

            int textColor = ColorUtils.interpolateColor(
                    ColorUtils.setAlphaColor(ColorUtils.getColorInactiveText(), alphaInt),
                    ColorUtils.setAlphaColor(ColorUtils.getColorText(), alphaInt),
                    av
            );
            if (font != null) {
                float tw = font.getStringWidth(setting.displayMode(mode));
                drawText(matrices, font, setting.displayMode(mode), curX + (ow - tw) / 2f, curY + 1.75f, textColor);
            }

            curX += ow + GAP;
            i++;
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return;

        Font font = Fonts.getFont("sf_regular", 13);
        float curX = x + PADDING;
        float curY = y + NAME_HEIGHT + GAP;
        boolean firstInRow = true;

        for (String mode : setting.getMods()) {
            float ow = optionWidth(font, mode);
            if (!firstInRow && curX + ow > x + width - PADDING) {
                curX = x + PADDING;
                curY += OPTION_H + GAP;
            }
            firstInRow = false;

            if (HoveringUtils.isHovered(mouseX, mouseY, curX, curY, ow, OPTION_H)) {
                setting.set(mode);
                return;
            }
            curX += ow + GAP;
        }
    }

    @Override
    public boolean isVisible() {
        return setting.visible();
    }
}
