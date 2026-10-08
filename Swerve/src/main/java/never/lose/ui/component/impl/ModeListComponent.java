package never.lose.ui.component.impl;

import net.minecraft.client.util.math.MatrixStack;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.api.setting.ModeListSetting;
import never.lose.ui.component.Component;
import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;
import never.lose.utils.font.Fonts;
import never.lose.utils.input.CursorManager;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.providers.ColorProvider;

import java.util.ArrayList;
import java.util.List;

public class ModeListComponent extends Component {

    private final ModeListSetting setting;
    private static final float NAME_HEIGHT = 10f;
    private static final float OPTION_H = 11f;
    private static final float GAP = 2f;
    private static final float PADDING = 4.5f;
    private static final float RADIUS = 1f;

    private final List<Animation> anims = new ArrayList<>();

    public ModeListComponent(ModeListSetting setting) {
        this.setting = setting;
        for (int i = 0; i < setting.getSettings().size(); i++) {
            anims.add(new Animation(Easing.QUINTIC_OUT, 250));
        }
    }

    private float optionWidth(String name) {
        return Draw.width(Fonts.REGULAR, name, 7.5f) + 8f;
    }

    private float calcHeight() {
        float rowWidth = 0;
        float totalHeight = NAME_HEIGHT + GAP;
        boolean firstInRow = true;

        for (BooleanSetting s : setting.getSettings()) {
            float ow = optionWidth(s.getName());
            if (!firstInRow && rowWidth + ow > width - PADDING * 2) {
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float animValue = getAlphaAnimSetting().getValue();
        float alpha = Math.max(Math.min(animValue * getAlphaAnim().getValue(), 1), 0);
        int alphaInt = (int) (255 * alpha);

        if (alpha < 0.02f) return;

        float totalHeight = calcHeight();
        setHeight((totalHeight + 2f) * animValue);

        Draw.text(Fonts.REGULAR, setting.getName(), x + PADDING, y + 1.5f,
                ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt), 7.5f);

        float curX = x + PADDING;
        float curY = y + NAME_HEIGHT + GAP;
        boolean firstInRow = true;
        int i = 0;

        for (BooleanSetting s : setting.getSettings()) {
            float ow = optionWidth(s.getName());
            if (!firstInRow && curX + ow > x + width - PADDING) {
                curX = x + PADDING;
                curY += OPTION_H + GAP;
            }
            firstInRow = false;

            boolean selected = s.getValue();
            Animation anim = anims.get(i);
            anim.run(selected);
            float av = anim.getValue();

            if (Hover.isHovered(mouseX, mouseY, curX, curY, ow, OPTION_H)) {
                CursorManager.requestHand();
            }

            int bgColor = ColorProvider.interpolateColor(
                    ColorProvider.setAlpha(ColorProvider.getColorInactiveButton(), alphaInt),
                    ColorProvider.setAlpha(ColorProvider.getColorButton(), alphaInt),
                    av
            );
            Draw.round(curX, curY, ow, OPTION_H, RADIUS, bgColor);

            int textColor = ColorProvider.interpolateColor(
                    ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), alphaInt),
                    ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt),
                    av
            );
            float tw = Draw.width(Fonts.REGULAR, s.getName(), 7.5f);
            Draw.text(Fonts.REGULAR, s.getName(), curX + (ow - tw) / 2f, curY + 1.75f, textColor, 7.5f);

            curX += ow + GAP;
            i++;
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return;

        float curX = x + PADDING;
        float curY = y + NAME_HEIGHT + GAP;
        boolean firstInRow = true;

        for (BooleanSetting s : setting.getSettings()) {
            float ow = optionWidth(s.getName());
            if (!firstInRow && curX + ow > x + width - PADDING) {
                curX = x + PADDING;
                curY += OPTION_H + GAP;
            }
            firstInRow = false;

            if (Hover.isHovered(mouseX, mouseY, curX, curY, ow, OPTION_H)) {
                s.setValue(!s.getValue());
                s.setClicked(true);
                return;
            }
            curX += ow + GAP;
        }
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
