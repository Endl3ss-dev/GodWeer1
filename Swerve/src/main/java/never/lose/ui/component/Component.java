package never.lose.ui.component;

import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;

public abstract class Component implements IComponent {

    public float x, y, width, height;

    private final Animation alphaAnim = new Animation(Easing.BACK_OUT, 550);
    private final Animation alphaAnimSetting = new Animation(Easing.CUBIC_OUT, 280);
    private final Animation alphaAnimBack = new Animation(Easing.CUBIC_OUT, 280);

    public boolean isVisible() {
        return true;
    }

    public Animation getAlphaAnim() {
        return alphaAnim;
    }

    public Animation getAlphaAnimSetting() {
        return alphaAnimSetting;
    }

    public Animation getAlphaAnimBack() {
        return alphaAnimBack;
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
}
