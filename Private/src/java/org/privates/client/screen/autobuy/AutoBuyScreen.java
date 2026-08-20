package org.privates.client.screen.autobuy;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.lwjgl.glfw.GLFW;

public class AutoBuyScreen extends Screen {

    public AutoBuyScreen() {
        super(new StringTextComponent(""));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Excellent.inst().autoBuyUI().render(matrixStack, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Excellent.inst().autoBuyUI().click((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Excellent.inst().autoBuyUI().click((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        Excellent.inst().autoBuyUI().scroll((float) mouseX, (float) mouseY, (float) delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (title.getString().contains("345345345345354"))
            Excellent.inst().autoBuyUI().key(keyCode);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (title.getString().contains("3453453453453456"))
            Excellent.inst().autoBuyUI().charTyped(codePoint);
        return super.charTyped(codePoint, modifiers);
    }
}
