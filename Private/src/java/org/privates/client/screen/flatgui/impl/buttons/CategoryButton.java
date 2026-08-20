package org.privates.client.screen.flatgui.impl.buttons;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.Category;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;

@Getter
@Setter
@Accessors(fluent = true)
public class CategoryButton extends AbstractPanel {
    private final Category category;
    private final Animation hovered = new Animation();

    public CategoryButton(Category category) {
        this.category = category;
        size().set(24F, 24F);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
        hovered.set(0F);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        hovered.update();
        hovered.run(hover(mouseX, mouseY) ? 1F : 0F, 0.25F, Easings.SINE_OUT, true);
        RenderUtil.Rounded.smooth(matrix, position().x, position().y, size().x, size().y, ColorUtil.getColor(15, 15, 20, alpha()), Round.of(4));
        //  Font font = Fonts.CLICKGUI;
        float fontSize = 10F + (hovered.get() * 3F);
        //int accentColor = ColorUtil.multAlpha(Theme.getInstance().clientColor(), 255);

        float darker = 0.5F;
        // font.drawGradientCenter(matrix, String.valueOf(category.getIcon()), position().x + size().x / 2F, position().y + 1F + size().y / 2F - fontSize / 2F, ColorUtil.fade(5, 20, accentColor, ColorUtil.multDark(accentColor, darker)), ColorUtil.fade(5, 10 * 20, accentColor, ColorUtil.multDark(accentColor, darker)), fontSize);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Luno.inst().flatGui().updateSelectedCategory(category);
        return hover(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    @Override
    public void onClose() {
    }
}