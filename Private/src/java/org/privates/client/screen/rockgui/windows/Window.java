package org.privates.client.screen.rockgui.windows;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;

@Getter
@Setter
public abstract class Window implements IMinecraft {

    protected float x, y, width, height;
    protected final Animation opening = new Animation().setSpeed(300).setEasing(Easings.SINE_IN_OUT);
    
    public Window(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.opening.setForward(true);
    }

    public abstract void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);

    public abstract boolean clicked(double mouseX, double mouseY, int button);

    public abstract boolean released(double mouseX, double mouseY, int button);

    public boolean dragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    public boolean scrolled(double mouseX, double mouseY, double delta) {
        return false;
    }

    public boolean pressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    public void tick() {
    }

    public void update() {
    }

    public boolean canClick(double mouseX, double mouseY) {
        return !Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, height);
    }
}