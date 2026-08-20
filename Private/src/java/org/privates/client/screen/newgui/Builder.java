package org.privates.client.screen.newgui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;

@Getter
@Setter
public abstract class Builder {

    protected float x, y, width, height;

    public boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean inRange(float y, float height, float rangeY, float rangeHeight) {
        return y <= rangeY + rangeHeight && y + height >= rangeY;
    }

    public abstract void render(MatrixStack stack, float mouseX, float mouseY);

    protected abstract void mouse(float mouseX, float mouseY, int button, int action);

    protected abstract void key(int key);

    protected abstract void charTyped(char typed);
}
