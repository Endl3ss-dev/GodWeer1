package godweer.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

public abstract class Builder {

    protected float x, y, width, height;

    public boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean inRange(float y, float height, float rangeY, float rangeHeight) {
        return y <= rangeY + rangeHeight && y + height >= rangeY;
    }

    public abstract void render(PoseStack stack, float mouseX, float mouseY);

    protected abstract void mouse(float mouseX, float mouseY, int button, int action);

    protected abstract void key(int key);

    protected abstract void charTyped(char typed);

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
