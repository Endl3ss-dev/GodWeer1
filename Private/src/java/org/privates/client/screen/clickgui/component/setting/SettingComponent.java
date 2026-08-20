package org.privates.client.screen.clickgui.component.setting;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IMouse;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.Setting;
import org.privates.client.managers.module.settings.impl.DelimiterSetting;
import org.privates.client.screen.clickgui.component.WindowComponent;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.font.StripFont;
import org.privates.client.utils.render.text.TextUtils;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public abstract class SettingComponent extends WindowComponent implements IMouse {
    public Setting<?> value;
    private final StripFont stripFont = new StripFont();
    public final float fontSize = 6.5F;
    public float margin = 3;
    private final String splitter = "-";

    public SettingComponent(final Setting<?> value) {
        this.value = value;
        size.set(90, 24);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        hoverAnimation.update();
    }

    public float drawName(MatrixStack matrix, int mouseX, int mouseY, float width) {
        boolean isHover = isHover(mouseX, mouseY, position.x, position.y + margin, width, valueHeight());

        hoverAnimation.run(isHover ? 1 : 0, 0.5, Easings.QUAD_OUT);

        int hoverColor = ColorUtil.replAlpha(Theme.getInstance().clientColor(), alpha());
        int defaultColor = ColorUtil.getColor(200, alpha());

        boolean isDelimiter = value instanceof DelimiterSetting;

        int finalColor = isDelimiter ? accentColor() : hoverAnimation().isFinished() && hoverAnimation.getValue() == 0.0 ? defaultColor : ColorUtil.overCol(defaultColor, hoverColor, hoverAnimation.get());

        return Fonts.SF_BOLD.drawSplitted(matrix, value.getName(), splitter, position.x, position.y + margin, width, finalColor, 7);
    }

    public float valueHeight() {
        return TextUtils.splitLineHeight(value.getName(), Fonts.SF_BOLD, fontSize, size.x, splitter) * fontSize;
    }


}