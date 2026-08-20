package org.privates.client.screen.flatgui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector2f;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.api.interfaces.IMouse;
import org.privates.client.api.interfaces.IWindow;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.screen.flatgui.impl.CategoryWidget;
import org.privates.client.screen.flatgui.impl.ModuleListWidget;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.ScaleMath;
import org.privates.client.utils.render.color.ColorFormatting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatGuiScreen extends Screen implements IMinecraft, IWindow, IMouse {
    private boolean exit = false;
    private final Animation alpha = new Animation();
    private final Animation scale = new Animation();
    private final Vector2f position = new Vector2f(0, 0);
    private final Vector2f size = new Vector2f(540, 320);

    private final CategoryWidget categoryWidget = new CategoryWidget();
    private final ModuleListWidget moduleListWidget = new ModuleListWidget(this);
    @Getter
    public static Category selectedCategory = Category.COMBAT;
    private Module selectedModule = null;
    float margin = 5F;

    public FlatGuiScreen() {
        super(StringTextComponent.EMPTY);
    }


    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        categoryWidget.resize(minecraft, width, height);
        moduleListWidget.resize(minecraft, width, height);
    }

    @Override
    protected void init() {
        super.init();
        alpha.set(0.0);
        scale.set(0.0);
        alpha.run(1.0, 0.2, Easings.EXPO_OUT);
        scale.set(0.75F);
        scale.run(1.0, 0.2, Easings.EXPO_OUT);
        exit = false;
        categoryWidget.init();
        moduleListWidget.init(selectedCategory);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Vector2f mouse = ScaleMath.getMouse(mouseX, mouseY);
        int finalMouseX = (int) mouse.x;
        int finalMouseY = (int) mouse.y;
        alpha.update();
        scale.update();
        this.mouseCheck();
        this.closeCheck();

        float panelX = width / 2F - size.x / 2F;
        float panelY = height / 2F - size.y / 2F;
        float scale = scale().get();
        float margin = 5F;
        float iconSize = 24F;
        float fontSize = 16F;

        ScaleMath.scalePre();
        matrixStack.push();
        matrixStack.translate(width / 2F, height / 2F, 0);
        matrixStack.scale(scale, scale, 0);
        matrixStack.translate(-(width / 2F), -(height / 2F), 0);

        RenderUtil.Rounded.smooth(matrixStack, panelX, panelY, size.x, size.y, ColorUtil.getColor(10, 10, 15, alpha.get()), Round.of(8));
        int accentColor = ColorUtil.multAlpha(-1, 255);

        float darker = 0.5F;
        //Fonts.SEMI.drawLetterGradient(matrixStack, 'A', panelX + margin + iconSize / 2F - 7, panelY + margin + iconSize / 2F - fontSize / 2F, ColorUtil.fade(5, 20, accentColor, ColorUtil.multDark(accentColor, darker)), ColorUtil.fade(5, 10 * 20, accentColor, ColorUtil.multDark(accentColor, darker)), fontSize);
        Fonts.SF_SEMIBOLD.draw(matrixStack, ColorFormatting.getColor(32, 32, 32) + "| " + ColorFormatting.reset() + selectedCategory.getName(), panelX + margin + Fonts.SF_SEMIBOLD.getWidth("A", iconSize) + margin * 2F, panelY + margin * 2F, -1, 8F);

        categoryWidget.position().set(panelX, panelY);
        categoryWidget.size().set(margin + 24F + margin, size.y);
        categoryWidget.render(matrixStack, finalMouseX, finalMouseY, partialTicks);

        float moduleListX = panelX + margin + 24F + margin + margin;
        float moduleListY = panelY + margin * 3 + 8F;
        float moduleListWidth = 150F;
        float moduleListHeight = size.y - (margin * 4 + 8F);
        moduleListWidget.position().set(moduleListX, moduleListY);
        moduleListWidget.size().set(moduleListWidth, moduleListHeight);
        moduleListWidget.render(matrixStack, finalMouseX, finalMouseY, partialTicks);


        if (selectedModule != null) {
            float settingsX = moduleListX + moduleListWidth + margin + 2F;
            float settingsY = moduleListY;
            Fonts.SF_SEMIBOLD.draw(matrixStack, "Настройки " + selectedModule.getName(), settingsX, settingsY - margin - 8F, ColorUtil.getColor(255, 255, 255, alpha.get()), 8F);
        }

        matrixStack.pop();
        ScaleMath.scalePost();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector2f mouse = ScaleMath.getMouse(mouseX, mouseY);
        int finalMouseX = (int) mouse.x;
        int finalMouseY = (int) mouse.y;
        if (!exit) {
            categoryWidget.mouseClicked(finalMouseX, finalMouseY, button);
            moduleListWidget.mouseClicked(finalMouseX, finalMouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Vector2f mouse = ScaleMath.getMouse(mouseX, mouseY);
        int finalMouseX = (int) mouse.x;
        int finalMouseY = (int) mouse.y;
        if (!exit) {
            categoryWidget.mouseReleased(finalMouseX, finalMouseY, button);
            moduleListWidget.mouseReleased(finalMouseX, finalMouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!exit) {
            if (keyCode == Keyboard.KEY_GRAVE.getKey()) {
                return shouldCloseOnEsc();
            }
            categoryWidget.keyPressed(keyCode, scanCode, modifiers);
            moduleListWidget.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!exit) {
            categoryWidget.keyReleased(keyCode, scanCode, modifiers);
            moduleListWidget.keyReleased(keyCode, scanCode, modifiers);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!exit) {
            categoryWidget.charTyped(codePoint, modifiers);
            moduleListWidget.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (!exit && scale.getValue() > 0.0F && alpha.getValue() > 0.0F) {
            alpha.run(0.0, 0.2, Easings.EXPO_IN);
            scale.run(0.0, 0.2, Easings.EXPO_IN);
            exit = true;
            mc.mouseHelper.forceGrabMouse(false);
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        categoryWidget.onClose();
        moduleListWidget.onClose();
    }

    private void mouseCheck() {
        if (!Minecraft.IS_RUNNING_ON_MAC) {
            KeyBinding.updateKeyBindState();
        }
        boolean alphaCheck = alpha.isFinished() && alpha.getValue() == 1.0D;
        boolean scaleCheck = scale.isFinished() && scale.getValue() == 1.0D;
        if (alphaCheck && scaleCheck && mc.mouseHelper.isMouseGrabbed()) {
            mc.mouseHelper.ungrabMouse();
        }
    }

    private void closeCheck() {
        if (exit && scale.isFinished() && alpha.isFinished()) {
            closeScreen();
            exit = false;
        }
    }

    public void updateSelectedCategory(Category category) {
        selectedCategory = category;
        this.selectedModule = null;
        moduleListWidget.init(selectedCategory);
    }

    public void setSelectedModule(Module module) {
        this.selectedModule = module;
    }

    public static Category getSelectedCategory() {
        return selectedCategory;
    }
}