package org.privates.client.screen.autobuy;

import net.mojang.blaze3d.matrix.MatrixStack;

public interface Element {

    void render(MatrixStack matrixStack, float mouseX, float mouseY);

    void click(float mouseX, float mouseY, int button, int action);

    void key(int key);

    default boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return (mouseX >= x && mouseX <= x + width) && (mouseY >= y && mouseY <= y + height);
    }

    default boolean inRange(float y, float height, float rangeY, float rangeHeight) {
        return (y + height >= rangeY) && (y <= rangeY + rangeHeight);
    }
}
