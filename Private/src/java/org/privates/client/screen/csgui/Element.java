package org.privates.client.screen.csgui;

import net.mojang.blaze3d.matrix.MatrixStack;

public interface Element {
    void render(MatrixStack stack, float mouseX, float mouseY);

    void mouse(float mouseX, float mouseY, int button, int action);

    void keyPressed(int key);

    void charTyped(char typed);
}