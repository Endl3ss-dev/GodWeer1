package org.privates.client.screen.rockgui.windows;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.Setting;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class BindListWindow extends Window {

    private final Setting bindable;
    private final LinkedHashMap<String, Runnable> actions = new LinkedHashMap<>();
    private BindWindow child;

    public BindListWindow(Setting bindable, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.bindable = bindable;
        update();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        update();

        Theme theme = Theme.getInstance();
        int bgColor = ColorUtil.multAlpha(theme.backgroundColor(), opening.get());
        int textColor = ColorUtil.multAlpha(theme.textColor(), opening.get());

        float renderHeight = height * opening.get();
        RenderUtil.CustomStyled.drawGui(matrixStack, x, y, width, renderHeight, 4, true, 20f);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y, width, renderHeight);

        float yOff = 0;
        for (Map.Entry<String, Runnable> action : actions.entrySet()) {
            boolean hover = Mathf.isHover(mouseX, mouseY, x + 2, y + 2 + yOff, width - 4, 15);

            if (hover) {
                RenderUtil.Rounded.smooth(matrixStack, x + 2, y + 2 + yOff, width - 4, 15,
                        ColorUtil.multAlpha(theme.secondaryColor(), opening.get()), Round.of(2));
            }

            Fonts.SF_BOLD.draw(matrixStack, action.getKey(), x + 6, y + 4.5f + yOff, textColor, 14);

            if (Fonts.SF_BOLD.getWidth(action.getKey(), 14) + 12 > width) {
                width = Fonts.SF_BOLD.getWidth(action.getKey(), 14) + 12;
            }

            yOff += 17;
        }

        height = yOff + 2;

        ScissorExp.unset();
        ScissorExp.pop();
    }

    @Override
    public boolean clicked(double mouseX, double mouseY, int button) {
        float yOff = 0;
        for (Map.Entry<String, Runnable> action : actions.entrySet()) {
            if (Mathf.isHover((float) mouseX, (float) mouseY, x + 2, y + 2 + yOff, width - 4, 15)) {
                if (child != null) child.getOpening().setForward(false);
                action.getValue().run();
            }
            yOff += 17;
        }

        if (child != null && Mathf.isHover((float) mouseX, (float) mouseY, child.getX(), child.getY(), child.getWidth(), child.getHeight())) {
            return false;
        }

        if (!Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, height)) {
            opening.setForward(false);
        }

        return true;
    }

    @Override
    public boolean released(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void update() {
        actions.clear();
        actions.put("Add Bind", () -> {
        });

        actions.put("Clear", () -> {
        });
    }

    @Override
    public boolean pressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}