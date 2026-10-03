package godweer.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

// Базовый компонент newgui: позиция, размеры и хит-тесты.
public abstract class Builder {

    protected float x, y, width, height;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean inRange(float y, float height, float rangeY, float rangeHeight) {
        return y <= rangeY + rangeHeight && y + height >= rangeY;
    }

    public abstract void render(PoseStack stack, float mouseX, float mouseY);

    protected abstract void mouse(float mouseX, float mouseY, int button, int action);

    protected abstract void key(int key);

    public abstract void charTyped(char typed);
}
