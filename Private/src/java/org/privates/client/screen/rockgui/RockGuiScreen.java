package org.privates.client.screen.rockgui;

import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.interfaces.IMinecraft;

@Getter
public class RockGuiScreen extends Screen implements IMinecraft {

    private final RockGuiWindow window;

    public RockGuiScreen() {
        super(StringTextComponent.EMPTY);
        float width = 488;
        float height = 318;
        this.window = new RockGuiWindow(
                mw.getScaledWidth() / 2 - width / 2,
                mw.getScaledHeight() / 2 - height / 2,
                width,
                height
        );
        RockGuiRenderer.opening.getTimer().reset();
        RockGuiRenderer.opening.setForward(true);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        window.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        boolean up = GLFW.glfwGetKey(mc.getMainWindow().getHandle(), GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS;
        boolean down = GLFW.glfwGetKey(mc.getMainWindow().getHandle(), GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS;
        float offset = .1f / Math.max(60f, 5) * 500;

        if (up || down)
            this.mouseScrolled(mouseX, mouseY, up ? offset : down ? -offset : 0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        window.clicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        window.released(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        window.dragged(mouseX, mouseY, button, dragX, dragY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.window.pressed(keyCode, scanCode, modifiers);
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && RockGuiRenderer.opening.finished()) {
            this.window.close();
            mc.displayScreen(null);
            if (window.getInput() != null)
                window.getInput().selected = false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        window.scrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        window.charTyped(codePoint, modifiers);
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void tick() {
        window.tick();
        super.tick();
    }
}