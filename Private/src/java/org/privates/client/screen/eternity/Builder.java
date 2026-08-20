package org.privates.client.screen.eternity;

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

    abstract void render(MatrixStack stack, float mouseX, float mouseY);

    abstract void mouse(float mouseX, float mouseY, int button, int action);

    abstract void key(int key);

    abstract void charTyped(char typed);
}