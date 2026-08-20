package org.privates.client.screen.rockgui.windows;

import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.Setting;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

public class BindWindow extends Window {

    private final BindListWindow parent;
    private final Setting setting;
    private boolean binding;

    public BindWindow(BindListWindow parent, Setting setting, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.parent = parent;
        this.setting = setting;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Theme theme = Theme.getInstance();
        int bgColor = ColorUtil.multAlpha(theme.backgroundColor(), opening.get());
        int textColor = ColorUtil.multAlpha(theme.textColor(), opening.get());
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), opening.get());

        float renderWidth = width * opening.get();
        RenderUtil.CustomStyled.drawGui(matrixStack, x, y, renderWidth, height, 4, true, 20f);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y, renderWidth, height);

        float yOff = -1;
        float inc = 12;

        Fonts.SF_BOLD.draw(matrixStack, "Key", x + 5, y + 5 + yOff, textColor, 14);

        String bind = binding ? "..." : "NONE";
        float bindWidth = Fonts.SF_BOLD.getWidth(bind, 14) + 4;

        RenderUtil.Rounded.smooth(matrixStack, x + width - 5 - bindWidth, y + 5 + yOff, bindWidth, 10, separatorColor, Round.of(2));
        Fonts.SF_BOLD.draw(matrixStack, bind, x + width - 5 - bindWidth + 1.7f, y + 5.5f + yOff, textColor, 14);

        yOff += inc;

        Fonts.SF_BOLD.draw(matrixStack, "Type", x + 5, y + 5 + yOff, textColor, 14);
        yOff += inc;

        Fonts.SF_BOLD.draw(matrixStack, "Control", x + 5, y + 5 + yOff, textColor, 14);
        String del = "Delete";
        Fonts.SF_BOLD.draw(matrixStack, del, x + width - 6 - Fonts.SF_BOLD.getWidth(del, 14), y + 5 + yOff, ColorUtil.getColor(255, 0, 0), 14);
        yOff += inc;

        this.height = yOff + 6;

        ScissorExp.unset();
        ScissorExp.pop();
    }

    @Override
    public boolean clicked(double mouseX, double mouseY, int button) {
        if (binding) {
            binding = false;
            return false;
        }

        float yOff = 0;
        float inc = 11;

        if (Mathf.isHover((float) mouseX, (float) mouseY, x, y + 5 + yOff, width, inc)) {
            binding = true;
        }
        yOff += inc;

        yOff += inc;

        if (Mathf.isHover((float) mouseX, (float) mouseY, x + 5, y + 5 + yOff, width - 10, inc)) {
            opening.setForward(false);
        }

        if (!Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, height)) {
            opening.setForward(false);
            return true;
        }

        return false;
    }

    @Override
    public boolean released(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean pressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            binding = false;
        }
        return false;
    }
}