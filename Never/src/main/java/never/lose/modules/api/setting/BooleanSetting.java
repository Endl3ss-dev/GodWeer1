package never.lose.modules.api.setting;

import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;

import java.util.function.Supplier;

public class BooleanSetting extends Setting {

    private final Animation animation = new Animation(Easing.QUINTIC_OUT, 400);
    private final Animation clickAnimation = new Animation(Easing.QUINTIC_OUT, 400);
    private boolean clicked;
    private boolean value;
    private int key = -1;

    public BooleanSetting(String name, boolean defaultValue) {
        super(name);
        this.value = defaultValue;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        if (this.value == value) {
            return;
        }
        this.value = value;
        notifyChanged();
    }

    public void toggle() {
        setValue(!this.value);
    }

    public Animation getAnimation() {
        return animation;
    }

    public Animation getClickAnimation() {
        return clickAnimation;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        if (this.key == key) {
            return;
        }
        this.key = key;
        notifyChanged();
    }

    @Override
    public String getValueAsString() {
        return Boolean.toString(value);
    }

    @Override
    public void setValueFromString(String value) {
        setValue(Boolean.parseBoolean(value));
    }

    @Override
    public BooleanSetting setVisible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
